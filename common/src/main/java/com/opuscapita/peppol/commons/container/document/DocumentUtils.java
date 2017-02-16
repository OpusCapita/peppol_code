package com.opuscapita.peppol.commons.container.document;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Common XPath utils.
 *
 * @author Sergejs.Roze
 */
public class DocumentUtils {
    public static final String OBJECT_ENVELOPE = "ObjectEnvelope";
    private static final String SBD = "StandardBusinessDocument";
    private static final String SBDH = "StandardBusinessDocumentHeader";

    /**
     * Returns root node of the document or null if document has no root node.
     * Ignores StandardBusinessDocument header if met.
     *
     * @param document the document
     * @return the root node of the document or null if document has no root node
     */
    @Nullable
    public static Node getRootNode(@NotNull Document document) {
        NodeList nodes = document.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                if (SBD.equals(node.getLocalName())) {
                    nodes = node.getChildNodes();
                    for (i = 0; i < nodes.getLength(); i++) {
                        node = nodes.item(i);
                        if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                            if (!SBDH.equals(node.getLocalName()) && !OBJECT_ENVELOPE.equals(node.getLocalName())) {
                                return node;
                            }
                        }
                    }
                } else {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * Search for given XPath starting from given node.
     *
     * @param from the node to start from
     * @param xpath the xpath as an array of strings
     * @return the first child node with required XPath or null if no such node found
     */
    @Nullable
    public static Node searchForXPath(@NotNull Node from, @NotNull String... xpath) {
        for (String name : xpath) {
            from = searchForChildNode(from, name);
            if (from == null) {
                return null;
            }
        }
        return from;
    }

    /**
     * Search for child node of a given node using the node name.
     *
     * @param from the given node
     * @param name the name of the child to search for
     * @return the first child node with this name or null if no such child node exists
     */
    @Nullable
    public static Node searchForChildNode(@NotNull Node from, @NotNull String name) {
        NodeList nodes = from.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (name.equals(node.getLocalName())) {
                return node;
            }
        }
        return null;
    }

    /**
     * Creates document out of the given byte array.
     *
     * @param bytes the byte array to parse
     * @return the document object from the byte array
     */
    @NotNull
    public static Document getDocument(@NotNull byte[] bytes)
            throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(new ByteArrayInputStream(bytes));
    }

    /**
     * Returns StandardBusinessDocument block of the document if any.
     *
     * @param document the document to process
     * @return the SBDH block if any or null if no SBDH found
     */
    @Nullable
    public static Node getSBDH(@NotNull Document document) {
        NodeList nodes = document.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                if (SBD.equals(node.getLocalName())) {
                    return searchForChildNode(node, SBDH);
                }
            }
        }
        return null;
    }

    /**
     * Reads document identification field from SBDH.
     *
     * @param document the document
     * @return the document identification value or null if not available
     */
    @Nullable
    public static String readSbdhStandard(Document document) {
        Node sbdh = DocumentUtils.getSBDH(document);
        if (sbdh != null) {
            Node standard = DocumentUtils.searchForXPath(sbdh, "DocumentIdentification", "Standard");
            if (standard != null) {
                return standard.getTextContent();
            }
        }
        return null;
    }

    /**
     * Reads profile id from the document.
     *
     * @param document the document
     * @return the profile id of the document or null if not available
     */
    @Nullable
    public static String readDocumentProfileId(Document document) {
        Node root = DocumentUtils.getRootNode(document);
        if (root != null) {
            Node profileId = DocumentUtils.searchForXPath(root, "ProfileID");
            if (profileId != null) {
                return profileId.getTextContent();
            }
        }
        return null;
    }

    /**
     * Reads customization ID from the document.
     *
     * @param document the document
     * @return the customization id or null if cannot be found
     */
    @Nullable
    public static String readCustomizationId(@NotNull Document document) {
        Node root = DocumentUtils.getRootNode(document);
        if (root != null) {
            Node customizationId = DocumentUtils.searchForXPath(root, "CustomizationID");
            if (customizationId != null) {
                return customizationId.getTextContent();
            }
        }
        return null;
    }

    /**
     * Reads value from the given node and xpath in case current value is null, otherwise returns current value.
     *
     * @param current the current value of the field
     * @param node the node to start from
     * @param path the XPath as an array
     * @return the current value if not empty, the value read from the given node and xpath, or null if cannot read value
     */
    @Nullable
    public static String selectValueFrom(@Nullable String current, @Nullable Node node, @NotNull String... path) {
        if (current != null) {
            return current;
        }
        if (node == null) {
            return null;
        }
        Node resultNode = DocumentUtils.searchForXPath(node, path);
        if (resultNode == null) {
            return null;
        }
        return StringUtils.trimWhitespace(resultNode.getTextContent());
    }

}
