package com.opuscapita.peppol.commons.container.xml;

import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.document.DocumentWarning;
import com.opuscapita.peppol.commons.container.document.ParticipantId;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sergejs.Roze
 */
public class DocumentParserHandler extends DefaultHandler {
    private final static String SBD = "/StandardBusinessDocument";
    private final static String SBDH = SBD + "/StandardBusinessDocumentHeader";

    private final static Logger logger = LoggerFactory.getLogger(DocumentParserHandler.class);

    private final Endpoint endpoint;
    private final String fileName;
    private final List<Template> templates = new ArrayList<>();
    private final LinkedList<String> paths = new LinkedList<>();

    private final List<DocumentError> errors = new ArrayList<>();
    private final List<DocumentWarning> warnings = new ArrayList<>();

    private String value;

    DocumentParserHandler(@Nullable String fileName, @NotNull DocumentTemplates templates, @NotNull Endpoint endpoint) {
        this.fileName = fileName;
        this.endpoint = endpoint;
        for (DocumentTemplate dt : templates.getTemplates()) {
            Template template = new Template(dt.getName(), dt.getRoot());
            for (FieldInfo fi : dt.getFields()) {
                Field field = new Field(fi);
                template.fields.add(field);
            }
            this.templates.add(template);
        }

        paths.addLast("");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String path = paths.getLast() + "/" + localName;
        if (path.startsWith(SBDH)) {
            path = path.replaceFirst(SBDH, "\\$SBDH");
        }
        paths.addLast(path);

        for (Template template : templates) {
            if (path.equals(template.root)) {
                template.rootNameSpace = uri;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (StringUtils.isNotBlank(value)) {
            String path = paths.getLast();

            Iterator<Template> iterator = templates.iterator();
            while (iterator.hasNext()) {
                Template template = iterator.next();
                for (Field field : template.fields) {
                    if (field.getPaths() == null) {
                        continue;
                    }
                    for (String fp : field.getPaths()) {
                        if (fp.startsWith("$ROOT")) {
                            fp = fp.replaceFirst("\\$ROOT", template.root);
                        }
                        if (fp.equals(path)) {
                            if (field.value != null && !value.equals(field.value)) {
                                if (field.getId().equals("sender_id") || field.getId().equals("recipient_id")) {
                                    if (!new ParticipantId(field.value).equals(new ParticipantId(value))) {
                                        template.addError("Value of participant ID " + field.getId() + " differs in the document: " +
                                                field.value + " and " + value);
                                    }
                                } else {
                                    template.addError("There are different values for the field " + field.getId() + " in the document: "
                                            + field.value + " and " + value);
                                }
                            } else {
                                if (field.getMask() != null && !value.matches(field.getMask())) {
                                    logger.debug("Removing unmatched template " + template.name);
                                    iterator.remove();
                                } else {
                                    logger.debug(
                                            field.getId() + " matched by " + template.name + ", value = " + value + ", path = " + path);
                                    if (!value.equals(field.value)) {
                                        template.matchedCount++;
                                        field.value = value;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
            value = null;
        }
        paths.removeLast();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        value = new String(ch, start, length).trim();
    }

    @NotNull
    public DocumentInfo getResult() {
        if (templates.size() == 0) {
            return noResult(null);
        }
        // remove all templates without matched root
        Template best = templates.stream().max(Comparator.comparingInt(t -> t.matchedCount)).orElse(null);
        templates.removeIf(template -> ":".equals(template.rootNameSpace));
        if (templates.size() == 0) {
            return noResult(best);
        }

        templates.forEach(this::checkMandatoryFields);
        if (templates.size() != 1) {
            if (templates.stream().filter(t -> t.errors == null).count() == 1) {
                return oneResult(templates.stream().filter(t -> t.errors == null).findFirst().orElse(null));
            }
            return manyResults(templates);
        }

        return oneResult(templates.get(0));
    }

    private void checkMandatoryFields(Template template) {
        for (Field field : template.fields) {
            if (field.value == null && field.isMandatory()) {
                template.addError("Missing mandatory field " + field.getId());
            }
        }
    }

    private void setFields(Template template, DocumentInfo result) {
        for (Field field : template.fields) {
            if (field.value != null) {
                result.with(field.getId(), field.value);
            }
        }
    }

    private DocumentInfo oneResult(Template template) {
        DocumentInfo result = new DocumentInfo();
        result.setRootNodeName(template.originalRoot);
        result.setRootNameSpace(template.rootNameSpace);
        String[] parts = template.name.split("\\.", 2);
        result.setArchetype(Archetype.valueOf(parts[0]));
        if (parts.length > 1) {
            result.setDocumentType(parts[1]);
        } else {
            logger.error("Cannot get document type name from template name: '" + template.name + "'");
        }

        setFields(template, result);
        addErrorsAndWarnings(result, template);

        if (!result.getErrors().isEmpty()) {
            result.setArchetype(Archetype.INVALID);
        }
        return result;
    }

    private DocumentInfo manyResults(List<Template> templates) {
        logger.warn("Document " + fileName + " matches following templates: " +
                templates.stream().map(t -> t.name).collect(Collectors.joining(", ")));
        errors.add(new DocumentError(endpoint, "Document " + fileName + " matches following templates: " +
                templates.stream().map(t -> t.name).collect(Collectors.joining(", "))));

        DocumentInfo result = new DocumentInfo();
        result.setArchetype(Archetype.INVALID);

        for (Template template : templates) {
            setFields(template, result);
        }

        Template[] array = new Template[templates.size()];
        int count = 0;
        for (Template template : templates) {
            array[count++] = template;
        }
        addErrorsAndWarnings(result, array);
        return result;
    }

    @NotNull
    private DocumentInfo noResult(@Nullable Template best) {
        logger.warn("No matching document templates found for " + fileName);
        errors.add(new DocumentError(endpoint, "No matching document templates found for " + fileName));

        DocumentInfo result = new DocumentInfo();
        result.setArchetype(Archetype.INVALID);

        if (best != null) {
            for (Field field : best.fields) {
                if (field.value != null) {
                    result.with(field.getId(), field.value);
                }
            }
            addErrorsAndWarnings(result, best);
        }

        addErrorsAndWarnings(result);
        return result;
    }

    private void addErrorsAndWarnings(@NotNull DocumentInfo info, @NotNull Template... templates) {
        for (Template template : templates) {
            if (template.errors != null) {
                for (String error : template.errors) {
                    info.getErrors().add(new DocumentError(endpoint, error));
                }
            }
        }
        info.getErrors().addAll(errors);
        info.getWarnings().addAll(warnings);
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        warnings.add(new DocumentWarning(endpoint,
                "Parser warning in position " + e.getLineNumber() + ":" + e.getColumnNumber() + " - " + e.getMessage()));
        logger.warn("Parser warning in position " + e.getLineNumber() + ":" + e.getColumnNumber() + " - " + e.getMessage() +
                " in " + fileName);
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        errors.add(new DocumentError(endpoint,
                "Parser error in position " + e.getLineNumber() + ":" + e.getColumnNumber() + " - " + e.getMessage()));
        logger.warn("Parser error in position " + e.getLineNumber() + ":" + e.getColumnNumber() + " - " + e.getMessage() +
                " in " + fileName);
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        errors.add(new DocumentError(endpoint,
                "Fatal parser error in position " + e.getLineNumber() + ":" + e.getColumnNumber() + " - " + e.getMessage()));
        logger.warn("Fatal parser error in position " + e.getLineNumber() + ":" + e.getColumnNumber() + " - " + e.getMessage() +
                " in " + fileName);
    }

    // single file format
    private class Template {
        private final String name;
        private final String root;
        private final String originalRoot;
        private List<Field> fields = new ArrayList<>();
        private List<String> errors = null;
        private String rootNameSpace = ":";
        private int matchedCount;

        Template(@NotNull String name, @Nullable String root) {
            this.name = name;
            if (root != null) {
                if (root.startsWith("$SBD/")) {
                    this.root = root.replaceFirst("\\$SBD", SBD);
                    this.originalRoot = root.replaceFirst("\\$SBD/", "");
                } else {
                    this.root = "/" + root;
                    this.originalRoot = root;
                }
            } else {
                this.root = null;
                this.originalRoot = null;
            }
        }

        void addError(String error) {
            if (errors == null) {
                errors = new ArrayList<>();
            }
            errors.add(error);
        }
    }

    // single field in single known file format
    private class Field extends FieldInfo {
        private String value;

        Field(@NotNull FieldInfo other) {
            super(other.getId(), other.isMandatory(), other.getMask(), other.getConstant(), other.getPaths());
            value = getConstant();
        }
    }

}
