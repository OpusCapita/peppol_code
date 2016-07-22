package com.opuscapita.peppol.commons.container.document.impl.ubl;

import com.opuscapita.peppol.commons.container.document.impl.FieldsReader;

/**
 * @author Sergejs.Roze
 */
public enum UblDocumentType {
    UNDEFINED("", null),
    INVOICE("Invoice", UblInvoiceFieldsReader.class),
    CREDIT_NOTE("CreditNote", UblCreditNodeFieldsReader.class),
    DESADV("DespatchAdvice", UblDespatchAdviceFieldsReader.class);

    private final String tag;
    private final Class<? extends FieldsReader> reader;

    UblDocumentType(String tag, Class<? extends FieldsReader> reader) {
        this.tag = tag;
        this.reader = reader;
    }

    public String getTag() {
        return tag;
    }

    public Class<? extends FieldsReader> getReader() {
        return reader;
    }
}
