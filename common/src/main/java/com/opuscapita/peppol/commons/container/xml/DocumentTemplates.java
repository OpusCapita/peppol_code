package com.opuscapita.peppol.commons.container.xml;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergejs.Roze
 */
@Component
@ConfigurationProperties(prefix = "peppol.common.document_templates")
public class DocumentTemplates {
    private List<DocumentTemplate> templates = new ArrayList<>();

    public List<DocumentTemplate> getTemplates() {
        return templates;
    }

    public void setTemplates(@NotNull List<DocumentTemplate> templates) {
        this.templates = templates;
    }

    public int getCount(){
        return templates.size();
    }
}
