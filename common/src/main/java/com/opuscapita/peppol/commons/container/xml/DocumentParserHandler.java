package com.opuscapita.peppol.commons.container.xml;

import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.document.DocumentWarning;
import com.opuscapita.peppol.commons.container.document.ParticipantId;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("UnusedAssignment")
public class DocumentParserHandler extends DefaultHandler {
    private final static String SBD = "/StandardBusinessDocument";
    private final static String SBDH = SBD + "/StandardBusinessDocumentHeader";
    private final static Logger logger = LoggerFactory.getLogger(DocumentParserHandler.class);
    private final Map<String, Boolean> sbdhMandatoryFields;
    private final Endpoint endpoint;
    private final String fileName;
    private final List<Template> templates = new ArrayList<>();
    private final LinkedList<String> paths = new LinkedList<>();

    private final List<DocumentError> errors = new ArrayList<>();
    private final List<DocumentWarning> warnings = new ArrayList<>();
    private final boolean shouldFailOnInconsistency;
    private String value;
    private boolean checkSBDH = true;
    private boolean sbdhPresent = false;
    private String documentIdFromSbdh = "";
    private String processIdFromSbdh = "";
    private String previousValue;

    DocumentParserHandler(@Nullable String fileName, @NotNull DocumentTemplates templates, @NotNull Endpoint endpoint, boolean shouldFailOnInconsistency) {
        this.fileName = fileName;
        this.endpoint = endpoint;
        this.shouldFailOnInconsistency = shouldFailOnInconsistency;

        //skipping sbdh for validator module and some tests
        checkSBDH = shouldCheckSBDH(endpoint);

        for (DocumentTemplate dt : templates.getTemplates()) {
            Template template = new Template(dt.getName(), dt.getRoot());
            for (FieldInfo fi : dt.getFields()) {
                Field field = new Field(fi);
                template.fields.add(field);
            }
            this.templates.add(template);
        }

        paths.addLast("");

        sbdhMandatoryFields = new HashMap<String, Boolean>() {{
            put("sender_id", false);
            put("recipient_id", false);
        }};

    }

    private boolean shouldCheckSBDH(@NotNull Endpoint endpoint) {
        boolean shouldCheck = !(endpoint.getType().equals(ProcessType.TEST) || endpoint.getType().equals(ProcessType.REST) || endpoint.getType().equals(ProcessType.WEB));
        if (!shouldCheck) {
            logger.debug("Endpoint type recognized as " + endpoint.getType() + ", SBDH check will be skipped");
        }
        return shouldCheck;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        String path = paths.getLast() + "/" + localName;
        if (path.startsWith(SBDH)) {
            sbdhPresent = true;
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
    public void endElement(String uri, String localName, String qName) {
        if (StringUtils.isNotBlank(value)) {
            String path = paths.getLast();
            //System.out.println(path+"==>"+value);
            if (path.contains("Scope/InstanceIdentifier")) {
                switch (previousValue) {
                    case "DOCUMENTID":
                        documentIdFromSbdh = value;
                        break;
                    case "PROCESSID":
                        processIdFromSbdh = value;
                        break;
                }
            }
            // for each known template
            Iterator<Template> iterator = templates.iterator();
            while (iterator.hasNext()) {
                Template template = iterator.next();
                for (Field field : template.fields) {
                    if (field.getPaths() == null) {
                        continue;
                    }
                    // for each known path in the single template
                    for (String fp : field.getPaths()) {
                        if (fp.startsWith("$ROOT")) {
                            fp = fp.replaceFirst("\\$ROOT", template.root);
                        }
                        // when path is known by template
                        if (fp.equals(path)) {
                            if (!field.matches(value)) {
                                // path is known but value not corresponds to the mask
                                //System.out.println(field + " <> " +value);
                                logger.debug("Removing unmatched template " + template.name);
                                iterator.remove();
                            } else {
                                // add another value here
                                logger.debug(field.getId() + " matched by " + template.name +
                                        ", value = " + value + ", path = " + path);
                                if (sbdhMandatoryFields.containsKey(field.getId()) && path.contains("SBDH/")) {
                                    sbdhMandatoryFields.replace(field.getId(), true);
                                }
                                field.addValue(value);
                            }
                            break;
                        }
                    }
                }
            }
            previousValue = value;
            value = null;
        }
        paths.removeLast();
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        value = new String(ch, start, length).trim();
    }

    /**
     * Process parsing results, returns filled document information object that can be either accepted or
     * rejected document.
     *
     * @return the complete document information object
     */
    @NotNull
    public DocumentInfo getResult() {
        DocumentInfo result;

        //SBDH missing and it's not WEB or REST endpoint
        if (checkSBDH && !sbdhPresent) {
            errors.add(new DocumentError(endpoint, "No SBDH present in file: " + fileName));
            logger.warn("No SBDH present in file: " + fileName);
        }

        // check if there are some results left after all
        if (templates.size() == 0) {
            return noResult(null);
        }

        // remove all templates without matched root
        templates.removeIf(template -> ":".equals(template.rootNameSpace));

        // select the best template - the one that has most values filled
        Template best = templates.stream().max(Comparator.comparingInt(Template::matchedCount)).orElse(null);
        if (templates.size() == 0) {
            return noResult(best);
        }

        // process constants, check mandatory fields, check possible overwritten fields in remaining templates
        templates.forEach(this::checkFields);


        // select what to return if there are still more than one template left
        if (templates.size() != 1) {
            //Template bestTemplate = templates.stream().min(Comparator.comparingInt(Template::errorsCount)).orElse(null);
            //trying to find best template according to which one has less errors
            templates.sort(Comparator.comparingInt(Template::errorsCount));
            if (templates.get(0).errorsCount() < templates.get(1).errorsCount()) {
                result = oneResult(templates.get(0));
            } else {
                // sorry, no luck, return all matching templates
                return manyResults(templates);
            }
        } else {
            // OK, only one template left, use it as result
            result = oneResult(templates.get(0));
        }

        //Perform checks on SBDH field values
        if (checkSBDH && sbdhPresent) {
            checkMandatorySbdhFields(result);
        }

        if (!result.getErrors().isEmpty()) {
            result.setArchetype(Archetype.INVALID);
        }

        return result;
    }

    private void checkMandatorySbdhFields(DocumentInfo result) {
        if (result.getCustomizationId().trim().length() == 0) {
            result.getErrors().add(new DocumentError(endpoint, "Customization id is missing in file: " + fileName));
            logger.warn("Customization id is missing in file: " + fileName);
        }

        if (result.getProfileId().trim().length() == 0) {
            result.getErrors().add(new DocumentError(endpoint, "Profile id is missing in file: " + fileName));
            logger.warn("Profile id is missing in file: " + fileName);
        }

        if (sbdhMandatoryFields.get("sender_id") && !no.difi.oxalis.sniffer.identifier.ParticipantId.isValidParticipantIdentifierPattern(result.getSenderId())) {
            result.getErrors().add(new DocumentError(endpoint, "Invalid  sender id[" + result.getSenderId() + "] in file: " + fileName));
            logger.warn("Invalid sender id[" + result.getSenderId() + "] in file: " + fileName);
        }

        if (sbdhMandatoryFields.get("recipient_id") && !no.difi.oxalis.sniffer.identifier.ParticipantId.isValidParticipantIdentifierPattern(result.getRecipientId())) {
            result.getErrors().add(new DocumentError(endpoint, "Invalid recipient id[" + result.getRecipientId() + "] in file: " + fileName));
            logger.warn("Invalid recipient id[" + result.getRecipientId() + "] in file: " + fileName);
        }

        sbdhMandatoryFields
                .entrySet()
                .stream()
                .filter(entry -> !entry.getValue())
                .forEach(entry -> {
                    result.getErrors().add(new DocumentError(endpoint, "SBDH is missing " + entry.getKey() + " field for file: " + fileName));
                    logger.warn("SBDH is missing " + entry.getKey() + " field for file: " + fileName);
                });

        if (processIdFromSbdh == null || processIdFromSbdh.isEmpty()) {
            result.getErrors().add(new DocumentError(endpoint, "PROCESSID is missing or empty in SBDH for file: " + fileName));
            logger.warn("PROCESSID is missing or empty in SBDH for file: " + fileName);
        }

        if (documentIdFromSbdh == null || documentIdFromSbdh.isEmpty()) {
            result.getErrors().add(new DocumentError(endpoint, "DOCUMENTID is missing or empty in SBDH for file: " + fileName));
            logger.warn("DOCUMENTID is missing or empty in SBDH for file: " + fileName);
        }

    }

    @SuppressWarnings("ConstantConditions")
    private void checkFields(Template template) {
        for (Field field : template.fields) {
            // add errors for overwritten fields
            if (field.values != null && field.values.size() > 1) {
                // the first value should have been read from the header, count how many others have this value
                String first = field.values.get(0);
                //if we need to check for SBDH or the SBDH is anyway present lets check
                //== 1 because we are also comparing all values with the first one we have already, so if the first one will match only with itself, this is an error
                if (checkSBDH || field.getPaths().get(0).contains("$SBDH")) {
                    if (field.values.stream().filter(v -> areEqual(field.getId(), first, v)).count() == 1) {
                        StringBuilder errorText = new StringBuilder("There are different conflicting values in the document for the field '"
                                + field.getId() + ": " + String.join(", ", field.values + System.lineSeparator()));
                        errorText.append("There should be at least one match in the document body for the ").append(field.getId()).append(" specified in SBDH").append(System.lineSeparator());
                        errorText.append("Paths: ").append(System.lineSeparator());
                        for (String path : field.getPaths()) {
                            errorText.append("  ").append(path).append(System.lineSeparator());
                        }
                        if (shouldFailOnInconsistency) {
                            template.addError(errorText.toString());
                        } else {
                            logger.warn(errorText.toString());
                        }
                    }
                }
            }

            // process constants
            if (field.values == null && field.getConstant() != null) {
                field.addValue(field.getConstant());
            }

            // check mandatory fields presence
            if (field.values == null && field.isMandatory()) {
                StringBuilder errorText = new StringBuilder("Missing mandatory field: " + field.getId() + System.lineSeparator());
                errorText.append("Paths: ").append(System.lineSeparator());
                for (String path : field.getPaths()) {
                    errorText.append("  ").append(path).append(System.lineSeparator());
                }
                template.addError(errorText.toString());
            }
        }
    }

    private boolean areEqual(@NotNull String id, @NotNull String valueOne, @NotNull String valueTwo) {
        return !(id.equals("sender_id") || id.equals("recipient_id"))
                || new ParticipantId(valueOne).equals(new ParticipantId(valueTwo));
    }

    private void setFields(Template template, DocumentInfo result) {
        for (Field field : template.fields) {
            if (field.values != null) {
                result.with(field.getId(), field.values.get(0));
            }
        }
    }

    private DocumentInfo oneResult(Template template) {
        DocumentInfo result = new DocumentInfo();
        //TODO: provide more information on recognized document
        logger.info("Document " + fileName + ", recognized as " + template.originalRoot);
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

       /* //this causes mess because the main error is: document matches several templates, the rest doesn't matter that much
        Template[] templatesArray = new Template[templates.size()];
        int count = 0;
        for (Template template : templates) {
            templatesArray[count++] = template;
        }
        addErrorsAndWarnings(result, templatesArray);
        */
        result.getErrors().addAll(errors);
        result.getWarnings().addAll(warnings);
        return result;
    }

    @NotNull
    private DocumentInfo noResult(@Nullable Template best) {
        logger.warn("No matching document templates found for " + fileName);
        errors.add(new DocumentError(endpoint, "No matching document templates found for " + fileName));

        DocumentInfo result = new DocumentInfo();
        result.setArchetype(Archetype.UNRECOGNIZED);

        if (best != null) {
            for (Field field : best.fields) {
                if (field.values != null) {
                    result.with(field.getId(), field.values.get(0));
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
    public void warning(SAXParseException e) {
        warnings.add(new DocumentWarning(endpoint,
                "Parser warning in position " + e.getLineNumber() + ":" + e.getColumnNumber() + " - " + e.getMessage()));
        logger.warn("Parser warning in position " + e.getLineNumber() + ":" + e.getColumnNumber() + " - " + e.getMessage() +
                " in " + fileName);
    }

    @Override
    public void error(SAXParseException e) {
        errors.add(new DocumentError(endpoint,
                "Parser error in position " + e.getLineNumber() + ":" + e.getColumnNumber() + " - " + e.getMessage()));
        logger.warn("Parser error in position " + e.getLineNumber() + ":" + e.getColumnNumber() + " - " + e.getMessage() +
                " in " + fileName);
    }

    @Override
    public void fatalError(SAXParseException e) {
        errors.add(new DocumentError(endpoint,
                "Fatal parser error in position " + e.getLineNumber() + ":" + e.getColumnNumber() + " - " + e.getMessage()));
        logger.warn("Fatal parser error in position " + e.getLineNumber() + ":" + e.getColumnNumber() + " - " + e.getMessage() +
                " in " + fileName);
    }

    // single supported file format
    private class Template {
        private final String name;
        private final String root;
        private final String originalRoot;
        private List<Field> fields = new ArrayList<>();
        private List<String> errors = null;
        private String rootNameSpace = ":";

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

        // how many fields have values
        int matchedCount() {
            return (int) fields.stream().filter(f -> f.values != null).count();
        }

        //safe check for errors count
        int errorsCount() {
            return (errors == null) ? 0 : errors.size();
        }
    }

    // single field in single known file format
    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    private class Field extends FieldInfo {
        private List<String> values;

        Field(@NotNull FieldInfo other) {
            super(other.getId(), other.isMandatory(), other.getMask(), other.getConstant(), other.getPaths());
        }

        void addValue(@NotNull String value) {
            if (values == null) {
                values = new ArrayList<>();
            }
            values.add(value);
        }

        boolean matches(@NotNull String value) {
            return getMask() == null || value.matches(getMask());
        }

        @Override
        public String toString() {
            return "Field{" +
                    "values=" + values +
                    ", id=" +getId() +
                    ", constant=" +getConstant()+
                    ", mask=" +getMask()+
                    ", paths=" + (getPaths() != null ? getPaths().stream().collect(Collectors.joining(", ")) : "[]") +
                    '}';
        }
    }

}
