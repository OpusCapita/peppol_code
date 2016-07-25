package com.opuscapita.peppol.commons.container.document.impl.ubl;

import com.opuscapita.peppol.commons.container.document.impl.FieldsReader;

/**
 * @author Sergejs.Roze
 */
public enum UblDocumentType {
    UNDEFINED("", null),
    INVOICE("Invoice", UblInvoice.class),
    CREDIT_NOTE("CreditNote", UblCreditNode.class),
    DESADV("DespatchAdvice", UblDespatchAdvice.class),
    REMINDER("Reminder", UblInvoice.class),
    ORDER("Order", UblOrder.class),
    ORDER_RESPONSE("OrderResponse", UblOrderResponse.class),
    CATALOGUE("Catalogue", UblCatalogue.class),
    CATALOGUE_RESPONSE("CatalogueResponse", UblCatalogueResponse.class);

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
