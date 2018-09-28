<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:iso="http://purl.oclc.org/dsdl/schematron" xmlns:saxon="http://saxon.sf.net/" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:rsm="urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100" xmlns:udt="urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100" xmlns:qdt="urn:un:unece:uncefact:data:standard:QualifiedDataType:100" xmlns:ram="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100" xmlns:u="utils" version="2.0">
<!--Implementers: please note that overriding process-prolog or process-root is 
    the preferred method for meta-stylesheets to use where possible. -->
<xsl:param name="archiveDirParameter"/>
<xsl:param name="archiveNameParameter"/>
<xsl:param name="fileNameParameter"/>
<xsl:param name="fileDirParameter"/>
<xsl:variable name="document-uri">
<xsl:value-of select="document-uri(/)"/>
</xsl:variable>

<!--PHASES-->


<!--PROLOG-->
<xsl:output xmlns:svrl="http://purl.oclc.org/dsdl/svrl" method="xml" omit-xml-declaration="no" standalone="yes" indent="yes"/>

<!--XSD TYPES FOR XSLT2-->


<!--KEYS AND FUNCTIONS-->
<function xmlns="http://www.w3.org/1999/XSL/Transform" name="u:slack" as="xs:boolean">
        <param name="exp" as="xs:decimal"/>
        <param name="val" as="xs:decimal"/>
        <param name="slack" as="xs:decimal"/>
        <value-of select="xs:decimal($exp + $slack) &gt;= $val and xs:decimal($exp - $slack) &lt;= $val"/>
    </function>

<!--DEFAULT RULES-->


<!--MODE: SCHEMATRON-SELECT-FULL-PATH-->
<!--This mode can be used to generate an ugly though full XPath for locators-->
<xsl:template match="*" mode="schematron-select-full-path">
<xsl:apply-templates select="." mode="schematron-get-full-path"/>
</xsl:template>

<!--MODE: SCHEMATRON-FULL-PATH-->
<!--This mode can be used to generate an ugly though full XPath for locators-->
<xsl:template match="*" mode="schematron-get-full-path">
<xsl:apply-templates select="parent::*" mode="schematron-get-full-path"/>
<xsl:text>/</xsl:text>
<xsl:choose>
<xsl:when test="namespace-uri()=''">
<xsl:value-of select="name()"/>
</xsl:when>
<xsl:otherwise>
<xsl:text>*:</xsl:text>
<xsl:value-of select="local-name()"/>
<xsl:text>[namespace-uri()='</xsl:text>
<xsl:value-of select="namespace-uri()"/>
<xsl:text>']</xsl:text>
</xsl:otherwise>
</xsl:choose>
<xsl:variable name="preceding" select="count(preceding-sibling::*[local-name()=local-name(current())                                   and namespace-uri() = namespace-uri(current())])"/>
<xsl:text>[</xsl:text>
<xsl:value-of select="1+ $preceding"/>
<xsl:text>]</xsl:text>
</xsl:template>
<xsl:template match="@*" mode="schematron-get-full-path">
<xsl:apply-templates select="parent::*" mode="schematron-get-full-path"/>
<xsl:text>/</xsl:text>
<xsl:choose>
<xsl:when test="namespace-uri()=''">@<xsl:value-of select="name()"/>
</xsl:when>
<xsl:otherwise>
<xsl:text>@*[local-name()='</xsl:text>
<xsl:value-of select="local-name()"/>
<xsl:text>' and namespace-uri()='</xsl:text>
<xsl:value-of select="namespace-uri()"/>
<xsl:text>']</xsl:text>
</xsl:otherwise>
</xsl:choose>
</xsl:template>

<!--MODE: SCHEMATRON-FULL-PATH-2-->
<!--This mode can be used to generate prefixed XPath for humans-->
<xsl:template match="node() | @*" mode="schematron-get-full-path-2">
<xsl:for-each select="ancestor-or-self::*">
<xsl:text>/</xsl:text>
<xsl:value-of select="name(.)"/>
<xsl:if test="preceding-sibling::*[name(.)=name(current())]">
<xsl:text>[</xsl:text>
<xsl:value-of select="count(preceding-sibling::*[name(.)=name(current())])+1"/>
<xsl:text>]</xsl:text>
</xsl:if>
</xsl:for-each>
<xsl:if test="not(self::*)">
<xsl:text/>/@<xsl:value-of select="name(.)"/>
</xsl:if>
</xsl:template>
<!--MODE: SCHEMATRON-FULL-PATH-3-->
<!--This mode can be used to generate prefixed XPath for humans 
	(Top-level element has index)-->
<xsl:template match="node() | @*" mode="schematron-get-full-path-3">
<xsl:for-each select="ancestor-or-self::*">
<xsl:text>/</xsl:text>
<xsl:value-of select="name(.)"/>
<xsl:if test="parent::*">
<xsl:text>[</xsl:text>
<xsl:value-of select="count(preceding-sibling::*[name(.)=name(current())])+1"/>
<xsl:text>]</xsl:text>
</xsl:if>
</xsl:for-each>
<xsl:if test="not(self::*)">
<xsl:text/>/@<xsl:value-of select="name(.)"/>
</xsl:if>
</xsl:template>

<!--MODE: GENERATE-ID-FROM-PATH -->
<xsl:template match="/" mode="generate-id-from-path"/>
<xsl:template match="text()" mode="generate-id-from-path">
<xsl:apply-templates select="parent::*" mode="generate-id-from-path"/>
<xsl:value-of select="concat('.text-', 1+count(preceding-sibling::text()), '-')"/>
</xsl:template>
<xsl:template match="comment()" mode="generate-id-from-path">
<xsl:apply-templates select="parent::*" mode="generate-id-from-path"/>
<xsl:value-of select="concat('.comment-', 1+count(preceding-sibling::comment()), '-')"/>
</xsl:template>
<xsl:template match="processing-instruction()" mode="generate-id-from-path">
<xsl:apply-templates select="parent::*" mode="generate-id-from-path"/>
<xsl:value-of select="concat('.processing-instruction-', 1+count(preceding-sibling::processing-instruction()), '-')"/>
</xsl:template>
<xsl:template match="@*" mode="generate-id-from-path">
<xsl:apply-templates select="parent::*" mode="generate-id-from-path"/>
<xsl:value-of select="concat('.@', name())"/>
</xsl:template>
<xsl:template match="*" mode="generate-id-from-path" priority="-0.5">
<xsl:apply-templates select="parent::*" mode="generate-id-from-path"/>
<xsl:text>.</xsl:text>
<xsl:value-of select="concat('.',name(),'-',1+count(preceding-sibling::*[name()=name(current())]),'-')"/>
</xsl:template>

<!--MODE: GENERATE-ID-2 -->
<xsl:template match="/" mode="generate-id-2">U</xsl:template>
<xsl:template match="*" mode="generate-id-2" priority="2">
<xsl:text>U</xsl:text>
<xsl:number level="multiple" count="*"/>
</xsl:template>
<xsl:template match="node()" mode="generate-id-2">
<xsl:text>U.</xsl:text>
<xsl:number level="multiple" count="*"/>
<xsl:text>n</xsl:text>
<xsl:number count="node()"/>
</xsl:template>
<xsl:template match="@*" mode="generate-id-2">
<xsl:text>U.</xsl:text>
<xsl:number level="multiple" count="*"/>
<xsl:text>_</xsl:text>
<xsl:value-of select="string-length(local-name(.))"/>
<xsl:text>_</xsl:text>
<xsl:value-of select="translate(name(),':','.')"/>
</xsl:template>
<!--Strip characters-->
<xsl:template match="text()" priority="-1"/>

<!--SCHEMA SETUP-->
<xsl:template match="/">
<svrl:schematron-output title="Rules for PEPPOL BIS 3.0 Billing" schemaVersion="iso" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">
<xsl:comment>
<xsl:value-of select="$archiveDirParameter"/>   
		 <xsl:value-of select="$archiveNameParameter"/>  
		 <xsl:value-of select="$fileNameParameter"/>  
		 <xsl:value-of select="$fileDirParameter"/>
</xsl:comment>
<svrl:ns-prefix-in-attribute-values uri="urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100" prefix="rsm"/>
<svrl:ns-prefix-in-attribute-values uri="urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100" prefix="udt"/>
<svrl:ns-prefix-in-attribute-values uri="urn:un:unece:uncefact:data:standard:QualifiedDataType:100" prefix="qdt"/>
<svrl:ns-prefix-in-attribute-values uri="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100" prefix="ram"/>
<svrl:ns-prefix-in-attribute-values uri="http://www.w3.org/2001/XMLSchema" prefix="xs"/>
<svrl:ns-prefix-in-attribute-values uri="utils" prefix="u"/>
<svrl:active-pattern>
<xsl:attribute name="document">
<value-of select="document-uri(/)"/>
</xsl:attribute>
<xsl:apply-templates/>
</svrl:active-pattern>
<xsl:apply-templates select="/" mode="M11"/>
<svrl:active-pattern>
<xsl:attribute name="document">
<value-of select="document-uri(/)"/>
</xsl:attribute>
<xsl:apply-templates/>
</svrl:active-pattern>
<xsl:apply-templates select="/" mode="M12"/>
<svrl:active-pattern>
<xsl:attribute name="document">
<value-of select="document-uri(/)"/>
</xsl:attribute>
<xsl:apply-templates/>
</svrl:active-pattern>
<xsl:apply-templates select="/" mode="M13"/>
<svrl:active-pattern>
<xsl:attribute name="document">
<value-of select="document-uri(/)"/>
</xsl:attribute>
<xsl:apply-templates/>
</svrl:active-pattern>
<xsl:apply-templates select="/" mode="M14"/>
<svrl:active-pattern>
<xsl:attribute name="document">
<value-of select="document-uri(/)"/>
</xsl:attribute>
<xsl:apply-templates/>
</svrl:active-pattern>
<xsl:apply-templates select="/" mode="M15"/>
<svrl:active-pattern>
<xsl:attribute name="document">
<value-of select="document-uri(/)"/>
</xsl:attribute>
<xsl:apply-templates/>
</svrl:active-pattern>
<xsl:apply-templates select="/" mode="M16"/>
</svrl:schematron-output>
</xsl:template>

<!--SCHEMATRON PATTERNS-->
<svrl:text xmlns:svrl="http://purl.oclc.org/dsdl/svrl">Rules for PEPPOL BIS 3.0 Billing</svrl:text>
<xsl:param name="profile" select="             if (/rsm:CrossIndustryInvoice/rsm:ExchangedDocumentContext/ram:BusinessProcessSpecifiedDocumentContextParameter and matches(normalize-space(/rsm:CrossIndustryInvoice/rsm:ExchangedDocumentContext/ram:BusinessProcessSpecifiedDocumentContextParameter/ram:ID), 'urn:fdc:peppol.eu:2017:poacc:billing:([0-9]{2}):1.0')) then                 tokenize(normalize-space(/rsm:CrossIndustryInvoice/rsm:ExchangedDocumentContext/ram:BusinessProcessSpecifiedDocumentContextParameter/ram:ID), ':')[7]             else                 'Unknown'"/>
<xsl:param name="supplierCountry" select="             if (/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction[1]/ram:ApplicableHeaderTradeAgreement[1]/ram:SellerTradeParty[1]/ram:SpecifiedTaxRegistration[ram:ID/@schemeID = 'VAT']/substring(ram:ID, 1, 2)) then                 upper-case(normalize-space(/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction[1]/ram:ApplicableHeaderTradeAgreement[1]/ram:SellerTradeParty[1]/ram:SpecifiedTaxRegistration[ram:ID/@schemeID = 'VAT']/substring(ram:ID, 1, 2)))             else                 if (/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTaxRepresentativeTradeParty/ram:SpecifiedTaxRegistration[ram:ID/@schemeID = 'VAT']/substring(ram:ID, 1, 2)) then                     upper-case(normalize-space(/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTaxRepresentativeTradeParty/ram:SpecifiedTaxRegistration[ram:ID/@schemeID = 'VAT']/substring(ram:ID, 1, 2)))                 else                     if (/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress/ram:CountryID) then                         upper-case(normalize-space(/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:PostalTradeAddress/ram:CountryID))                     else                         'XX'"/>
<xsl:param name="documentCurrencyCode" select="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:InvoiceCurrencyCode"/>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="rsm:ExchangedDocumentContext" priority="1014" mode="M11">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="rsm:ExchangedDocumentContext"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="ram:BusinessProcessSpecifiedDocumentContextParameter/ram:ID"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="ram:BusinessProcessSpecifiedDocumentContextParameter/ram:ID">
<xsl:attribute name="id">PEPPOL-EN16931-R001</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Business process MUST be provided.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="starts-with(normalize-space(ram:GuidelineSpecifiedDocumentContextParameter/ram:ID/text()), 'urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0')"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="starts-with(normalize-space(ram:GuidelineSpecifiedDocumentContextParameter/ram:ID/text()), 'urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0')">
<xsl:attribute name="id">PEPPOL-EN16931-R004</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Specification identifier MUST have the value
                'urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0'.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M11"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="ram:ApplicableHeaderTradeAgreement" priority="1013" mode="M11">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="ram:ApplicableHeaderTradeAgreement"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="ram:BuyerReference or ram:BuyerOrderReferencedDocument/ram:IssuerAssignedID"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="ram:BuyerReference or ram:BuyerOrderReferencedDocument/ram:IssuerAssignedID">
<xsl:attribute name="id">PEPPOL-EN16931-R003</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>A buyer reference or purchase order reference MUST be
                provided.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M11"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="ram:ApplicableHeaderTradeSettlement" priority="1012" mode="M11">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="ram:ApplicableHeaderTradeSettlement"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="not(ram:TaxCurrencyCode) or normalize-space(ram:TaxCurrencyCode/text()) != normalize-space(ram:InvoiceCurrencyCode/text())"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="not(ram:TaxCurrencyCode) or normalize-space(ram:TaxCurrencyCode/text()) != normalize-space(ram:InvoiceCurrencyCode/text())">
<xsl:attribute name="id">PEPPOL-EN16931-R005</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>VAT accounting currency code MUST be different from invoice currency
                code when provided.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="not(ram:TaxCurrencyCode) or count(ram:SpecifiedTradeSettlementHeaderMonetarySummation/ram:TaxTotalAmount[@currencyID = normalize-space(../../ram:TaxCurrencyCode/text())])"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="not(ram:TaxCurrencyCode) or count(ram:SpecifiedTradeSettlementHeaderMonetarySummation/ram:TaxTotalAmount[@currencyID = normalize-space(../../ram:TaxCurrencyCode/text())])">
<xsl:attribute name="id">PEPPOL-EN16931-R052</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>The currencyID for invoice total VAT in accounting currency, must be
                the same as VAT accounting currency code (BT-6)</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="count(ram:SpecifiedTradeSettlementHeaderMonetarySummation/ram:TaxTotalAmount[@currencyID = $documentCurrencyCode]) = 1"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="count(ram:SpecifiedTradeSettlementHeaderMonetarySummation/ram:TaxTotalAmount[@currencyID = $documentCurrencyCode]) = 1">
<xsl:attribute name="id">PEPPOL-EN16931-R053</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Only one tax total with tax subtotals MUST be provided.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     count(ram:SpecifiedTradeSettlementHeaderMonetarySummation/ram:TaxTotalAmount[@currencyID != $documentCurrencyCode]) = (if (ram:TaxCurrencyCode) then                         1                     else                         0)"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="count(ram:SpecifiedTradeSettlementHeaderMonetarySummation/ram:TaxTotalAmount[@currencyID != $documentCurrencyCode]) = (if (ram:TaxCurrencyCode) then 1 else 0)">
<xsl:attribute name="id">PEPPOL-EN16931-R054</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Only one tax total without tax subtotals MUST be provided when tax
                currency code is provided.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M11"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="rsm:ExchangedDocument" priority="1011" mode="M11">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="rsm:ExchangedDocument"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="count(ram:IncludedNote) &lt;= 1 and not(ram:IncludedNote/ram:SubjectCode)"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="count(ram:IncludedNote) &lt;= 1 and not(ram:IncludedNote/ram:SubjectCode)">
<xsl:attribute name="id">PEPPOL-EN16931-R002</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>No more than one note is allowed on document level.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M11"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="ram:BuyerTradeParty" priority="1010" mode="M11">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="ram:BuyerTradeParty"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="ram:URIUniversalCommunication/ram:URIID"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="ram:URIUniversalCommunication/ram:URIID">
<xsl:attribute name="id">PEPPOL-EN16931-R010</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Buyer electronic address MUST be provided</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M11"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="ram:SellerTradeParty" priority="1009" mode="M11">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="ram:SellerTradeParty"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="ram:URIUniversalCommunication/ram:URIID"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="ram:URIUniversalCommunication/ram:URIID">
<xsl:attribute name="id">PEPPOL-EN16931-R020</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Seller electronic address MUST be provided</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M11"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="ram:SpecifiedTradeAllowanceCharge[ram:CalculationPercent and not(ram:BasisAmount)]" priority="1008" mode="M11">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="ram:SpecifiedTradeAllowanceCharge[ram:CalculationPercent and not(ram:BasisAmount)]"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="false()"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="false()">
<xsl:attribute name="id">PEPPOL-EN16931-R041</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Allowance/charge base
                amount MUST be provided when allowance/charge percentage is provided.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M11"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="ram:SpecifiedTradeAllowanceCharge[not(ram:CalculationPercent) and ram:BasisAmount]" priority="1007" mode="M11">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="ram:SpecifiedTradeAllowanceCharge[not(ram:CalculationPercent) and ram:BasisAmount]"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="false()"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="false()">
<xsl:attribute name="id">PEPPOL-EN16931-R042</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Allowance/charge percentage
                MUST be provided when allowance/charge base amount is provided.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M11"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="ram:SpecifiedTradeAllowanceCharge" priority="1006" mode="M11">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="ram:SpecifiedTradeAllowanceCharge"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     not(ram:CalculationPercent and ram:BasisAmount) or u:slack(if (ram:ActualAmount) then                         ram:ActualAmount                     else                         0, (xs:decimal(ram:BasisAmount) * xs:decimal(ram:CalculationPercent)) div 100, 0.02)"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="not(ram:CalculationPercent and ram:BasisAmount) or u:slack(if (ram:ActualAmount) then ram:ActualAmount else 0, (xs:decimal(ram:BasisAmount) * xs:decimal(ram:CalculationPercent)) div 100, 0.02)">
<xsl:attribute name="id">PEPPOL-EN16931-R040</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Allowance/charge amount must equal base amount * percentage/100 if base
                amount and percentage exists</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M11"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="                 ram:SpecifiedTradeSettlementPaymentMeans[some $code in tokenize('49 59', '\s')                     satisfies normalize-space(ram:TypeCode) = $code]" priority="1005" mode="M11">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="                 ram:SpecifiedTradeSettlementPaymentMeans[some $code in tokenize('49 59', '\s')                     satisfies normalize-space(ram:TypeCode) = $code]"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="../ram:SpecifiedTradePaymentTerms/ram:DirectDebitMandateID"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="../ram:SpecifiedTradePaymentTerms/ram:DirectDebitMandateID">
<xsl:attribute name="id">PEPPOL-EN16931-R061</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Mandate reference MUST be provided for direct debit.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M11"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="rsm:SupplyChainTradeTransaction[ram:ApplicableHeaderTradeSettlement/ram:BillingSpecifiedPeriod/ram:StartDateTime]/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:BillingSpecifiedPeriod/ram:StartDateTime" priority="1004" mode="M11">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="rsm:SupplyChainTradeTransaction[ram:ApplicableHeaderTradeSettlement/ram:BillingSpecifiedPeriod/ram:StartDateTime]/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:BillingSpecifiedPeriod/ram:StartDateTime"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="udt:DateTimeString &gt;= ../../../../ram:ApplicableHeaderTradeSettlement/ram:BillingSpecifiedPeriod/ram:StartDateTime/udt:DateTimeString"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="udt:DateTimeString &gt;= ../../../../ram:ApplicableHeaderTradeSettlement/ram:BillingSpecifiedPeriod/ram:StartDateTime/udt:DateTimeString">
<xsl:attribute name="id">PEPPOL-EN16931-R110</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Start date of line period MUST be within invoice period.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M11"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="rsm:SupplyChainTradeTransaction[ram:ApplicableHeaderTradeSettlement/ram:BillingSpecifiedPeriod/ram:EndDateTime]/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:BillingSpecifiedPeriod/ram:EndDateTime" priority="1003" mode="M11">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="rsm:SupplyChainTradeTransaction[ram:ApplicableHeaderTradeSettlement/ram:BillingSpecifiedPeriod/ram:EndDateTime]/ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:BillingSpecifiedPeriod/ram:EndDateTime"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="udt:DateTimeString &lt;= ../../../../ram:ApplicableHeaderTradeSettlement/ram:BillingSpecifiedPeriod/ram:EndDateTime/udt:DateTimeString"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="udt:DateTimeString &lt;= ../../../../ram:ApplicableHeaderTradeSettlement/ram:BillingSpecifiedPeriod/ram:EndDateTime/udt:DateTimeString">
<xsl:attribute name="id">PEPPOL-EN16931-R111</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>End date of line period MUST be within invoice period.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M11"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="ram:IncludedSupplyChainTradeLineItem" priority="1002" mode="M11">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="ram:IncludedSupplyChainTradeLineItem"/>
<xsl:variable name="lineExtensionAmount" select="                     if (ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeSettlementLineMonetarySummation/ram:LineTotalAmount) then                         xs:decimal(ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeSettlementLineMonetarySummation/ram:LineTotalAmount)                     else                         0"/>
<xsl:variable name="quantity" select="                     if (ram:SpecifiedLineTradeDelivery/ram:BilledQuantity) then                         xs:decimal(ram:SpecifiedLineTradeDelivery/ram:BilledQuantity)                     else                         1"/>
<xsl:variable name="priceAmount" select="                     if (ram:SpecifiedLineTradeAgreement/ram:NetPriceProductTradePrice/ram:ChargeAmount) then                         xs:decimal(ram:SpecifiedLineTradeAgreement/ram:NetPriceProductTradePrice/ram:ChargeAmount)                     else                         0"/>
<xsl:variable name="baseQuantity" select="                     if (ram:SpecifiedLineTradeAgreement/ram:NetPriceProductTradePrice/ram:BasisQuantity and xs:decimal(ram:SpecifiedLineTradeAgreement/ram:NetPriceProductTradePrice/ram:BasisQuantity) != 0) then                         xs:decimal(ram:SpecifiedLineTradeAgreement/ram:NetPriceProductTradePrice/ram:BasisQuantity)                     else                         1"/>
<xsl:variable name="allowancesTotal" select="                     if (ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeAllowanceCharge[normalize-space(ram:ChargeIndicator/udt:Indicator) = 'false']) then                         xs:decimal(sum(ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeAllowanceCharge[normalize-space(ram:ChargeIndicator/udt:Indicator) = 'false']/ram:ActualAmount))                     else                         0"/>
<xsl:variable name="chargesTotal" select="                     if (ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeAllowanceCharge[normalize-space(ram:ChargeIndicator/udt:Indicator) = 'true']) then                         xs:decimal(sum(ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeAllowanceCharge[normalize-space(ram:ChargeIndicator/udt:Indicator) = 'true']/ram:ActualAmount))                     else                         0"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="u:slack($lineExtensionAmount, ($quantity * ($priceAmount div $baseQuantity)) + $chargesTotal - $allowancesTotal, 0.02)"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="u:slack($lineExtensionAmount, ($quantity * ($priceAmount div $baseQuantity)) + $chargesTotal - $allowancesTotal, 0.02)">
<xsl:attribute name="id">PEPPOL-EN16931-R120</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Invoice line net amount MUST equal (Invoiced quantity * (Item net
                price/item price base quantity) + Invoice line charge amount - Invoice line
                allowance amount</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M11"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="ram:NetPriceProductTradePrice | ram:GrossPriceProductTradePrice" priority="1001" mode="M11">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="ram:NetPriceProductTradePrice | ram:GrossPriceProductTradePrice"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="not(ram:BasisQuantity) or xs:decimal(ram:BasisQuantity) &gt; 0"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="not(ram:BasisQuantity) or xs:decimal(ram:BasisQuantity) &gt; 0">
<xsl:attribute name="id">PEPPOL-EN16931-R121</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Base quantity MUST be a positive number above zero.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M11"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="ram:NetPriceProductTradePrice/ram:BasisQuantity[@unitCode] | ram:GrossPriceProductTradePrice/ram:BasisQuantity[@unitCode]" priority="1000" mode="M11">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="ram:NetPriceProductTradePrice/ram:BasisQuantity[@unitCode] | ram:GrossPriceProductTradePrice/ram:BasisQuantity[@unitCode]"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="@unitCode = ../../../ram:SpecifiedLineTradeDelivery/ram:BilledQuantity/@unitCode"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="@unitCode = ../../../ram:SpecifiedLineTradeDelivery/ram:BilledQuantity/@unitCode">
<xsl:attribute name="id">PEPPOL-EN16931-R130</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Unit code of price base quantity MUST be same as invoiced
                quantity.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M11"/>
</xsl:template>
<xsl:template match="text()" priority="-1" mode="M11"/>
<xsl:template match="@*|node()" priority="-2" mode="M11">
<xsl:apply-templates select="*" mode="M11"/>
</xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="ram:SellerTradeParty[$supplierCountry = 'NO']" priority="1000" mode="M12">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="ram:SellerTradeParty[$supplierCountry = 'NO']"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="ram:SpecifiedLegalOrganization"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="ram:SpecifiedLegalOrganization">
<xsl:attribute name="id">NO-R-001</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Norwegian
                suppliers MUST provide legal entity.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="ram:SpecifiedTaxRegistration/ram:ID[@schemeID = 'FC'][normalize-space(text()) = 'Foretaksregisteret']"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="ram:SpecifiedTaxRegistration/ram:ID[@schemeID = 'FC'][normalize-space(text()) = 'Foretaksregisteret']">
<xsl:attribute name="id">NO-R-002</xsl:attribute>
<xsl:attribute name="flag">warning</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Most invoice issuers are required to append "Foretaksregisteret" to
                their invoice. "Dersom selger er aksjeselskap, allmennaksjeselskap eller filial av
                utenlandsk selskap skal også ordet «Foretaksregisteret» fremgå av salgsdokumentet,
                jf. foretaksregisterloven § 10-2."</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M12"/>
</xsl:template>
<xsl:template match="text()" priority="-1" mode="M12"/>
<xsl:template match="@*|node()" priority="-2" mode="M12">
<xsl:apply-templates select="*" mode="M12"/>
</xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="rsm:CrossIndustryInvoice" priority="1002" mode="M13">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="rsm:CrossIndustryInvoice"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     not(($supplierCountry = 'DK')                     and (normalize-space(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ReceivableSpecifiedTradeAccountingAccount/ram:ID/text()) = '')                     )"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="not(($supplierCountry = 'DK') and (normalize-space(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ReceivableSpecifiedTradeAccountingAccount/ram:ID/text()) = '') )">
<xsl:attribute name="id">DK-R-001</xsl:attribute>
<xsl:attribute name="flag">warning</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>For Danish suppliers when the Accounting is known it should be
                referred on the Invoice</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     not($supplierCountry = 'DK'                     and not(normalize-space(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedLegalOrganization/ram:ID/text()) != '')                     )"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="not($supplierCountry = 'DK' and not(normalize-space(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedLegalOrganization/ram:ID/text()) != '') )">
<xsl:attribute name="id">DK-R-002</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Danish suppliers MUST provide legal entity.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     not(($supplierCountry = 'DK')                     and (rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeAllowanceCharge/ram:ReasonCode = 'ZZZ')                     and not((string-length(normalize-space(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeAllowanceCharge/ram:Reason/text())) = 4                     and number(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeAllowanceCharge/ram:Reason) &gt;= 0)                     and number(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeAllowanceCharge/ram:Reason &lt;= 9999))                     )"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="not(($supplierCountry = 'DK') and (rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeAllowanceCharge/ram:ReasonCode = 'ZZZ') and not((string-length(normalize-space(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeAllowanceCharge/ram:Reason/text())) = 4 and number(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeAllowanceCharge/ram:Reason) &gt;= 0) and number(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeAllowanceCharge/ram:Reason &lt;= 9999)) )">
<xsl:attribute name="id">DK-R-004</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>When specifying non-VAT Taxes, Danish suppliers MUST use the
                SpecifiedTradeAllowanceCharge/ReasonCode="ZZZ" and the 4-digit Tax category MUST be
                specified as Reason</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     not(($supplierCountry = 'DK')                     and (((boolean(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:GlobalID))                     and (normalize-space(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:GlobalID/@schemeID) = ''))                     or                     ((boolean(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:GlobalID))                     and (normalize-space(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:GlobalID/@schemeID) = ''))                     )                     )"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="not(($supplierCountry = 'DK') and (((boolean(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:GlobalID)) and (normalize-space(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:GlobalID/@schemeID) = '')) or ((boolean(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:GlobalID)) and (normalize-space(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:GlobalID/@schemeID) = '')) ) )">
<xsl:attribute name="id">DK-R-013</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>For Danish Suppliers it is mandatory to use schemeID when GlobalID is
                used for SellerTradeParty or BuyerTradeParty</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     not(($supplierCountry = 'DK')                     and (((boolean(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedLegalOrganization/ram:ID))                     and (normalize-space(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedLegalOrganization/ram:ID/@schemeID) = ''))                     or                     ((boolean(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:SpecifiedLegalOrganization/ram:ID))                     and (normalize-space(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:SpecifiedLegalOrganization/ram:ID/@schemeID) = ''))                     )                     )"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="not(($supplierCountry = 'DK') and (((boolean(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedLegalOrganization/ram:ID)) and (normalize-space(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedLegalOrganization/ram:ID/@schemeID) = '')) or ((boolean(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:SpecifiedLegalOrganization/ram:ID)) and (normalize-space(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:SpecifiedLegalOrganization/ram:ID/@schemeID) = '')) ) )">
<xsl:attribute name="id">DK-R-014</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>For Danish Suppliers it is mandatory to use schemeID when
                SpecifiedLegalOrganization is used for SellerTradeParty or BuyerTradeParty</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M13"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem" priority="1001" mode="M13">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:IncludedSupplyChainTradeLineItem"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     not(($supplierCountry = 'DK')                     and (ram:SpecifiedTradeProduct/ram:DesignatedProductClassification/ram:ClassCode/@listID = 'MP')                     and not((ram:SpecifiedTradeProduct/ram:DesignatedProductClassification/ram:ClassCode/@listVersionID = '19.05.01')                     or (ram:SpecifiedTradeProduct/ram:DesignatedProductClassification/ram:ClassCode/@listVersionID = '19.0501')                     )                     )"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="not(($supplierCountry = 'DK') and (ram:SpecifiedTradeProduct/ram:DesignatedProductClassification/ram:ClassCode/@listID = 'MP') and not((ram:SpecifiedTradeProduct/ram:DesignatedProductClassification/ram:ClassCode/@listVersionID = '19.05.01') or (ram:SpecifiedTradeProduct/ram:DesignatedProductClassification/ram:ClassCode/@listVersionID = '19.0501') ) )">
<xsl:attribute name="id">DK-R-003</xsl:attribute>
<xsl:attribute name="flag">warning</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>If ItemClassification is provided from Danish suppliers, UNSPSC
                version 19.0501 should be used</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M13"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans" priority="1000" mode="M13">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     not(($supplierCountry = 'DK')                     and not(contains(' 1 10 31 42 48 49 50 58 59 93 97 ', concat(' ', ram:TypeCode, ' ')))                     )"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="not(($supplierCountry = 'DK') and not(contains(' 1 10 31 42 48 49 50 58 59 93 97 ', concat(' ', ram:TypeCode, ' '))) )">
<xsl:attribute name="id">DK-R-005</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>For Danish suppliers the following Payment means type codes are
                allowed: 1, 10, 31, 42, 48, 49, 50, 58, 59, 93 and 97</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     not(($supplierCountry = 'DK')                     and ((ram:TypeCode = '31') or (ram:TypeCode = '42'))                     and not((normalize-space(ram:PayeePartyCreditorFinancialAccount/ram:IBANID/text()) != '') and (normalize-space(ram:PayerSpecifiedDebtorFinancialInstitution/ram:BICID/text()) != ''))                     )"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="not(($supplierCountry = 'DK') and ((ram:TypeCode = '31') or (ram:TypeCode = '42')) and not((normalize-space(ram:PayeePartyCreditorFinancialAccount/ram:IBANID/text()) != '') and (normalize-space(ram:PayerSpecifiedDebtorFinancialInstitution/ram:BICID/text()) != '')) )">
<xsl:attribute name="id">DK-R-006</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>For Danish suppliers, bank account and registration account are
                mandatory if payment means is 31 or 42</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     not(($supplierCountry = 'DK')                     and (ram:TypeCode = '49')                     and not((normalize-space(../ram:CreditorReferenceID/text()) != '')                     and (normalize-space(ram:SpecifiedTradePaymentTerms/ram:DirectDebitMandateID/text()) != ''))                     )"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="not(($supplierCountry = 'DK') and (ram:TypeCode = '49') and not((normalize-space(../ram:CreditorReferenceID/text()) != '') and (normalize-space(ram:SpecifiedTradePaymentTerms/ram:DirectDebitMandateID/text()) != '')) )">
<xsl:attribute name="id">DK-R-007</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>For Danish suppliers DirectDebitMandateID and CreditorReferenceID are
                mandatory when payment means is 49</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     not(($supplierCountry = 'DK')                     and (ram:TypeCode = '50')                     and not(((substring(../ram:PaymentReference, 0, 4) = '01#')                     or (substring(../ram:PaymentReference, 0, 4) = '04#')                     or (substring(../ram:PaymentReference, 0, 4) = '15#'))                     and (string-length(ram:PayeePartyCreditorFinancialAccount/ram:IBANID/text()) = 7)                     )                     )"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="not(($supplierCountry = 'DK') and (ram:TypeCode = '50') and not(((substring(../ram:PaymentReference, 0, 4) = '01#') or (substring(../ram:PaymentReference, 0, 4) = '04#') or (substring(../ram:PaymentReference, 0, 4) = '15#')) and (string-length(ram:PayeePartyCreditorFinancialAccount/ram:IBANID/text()) = 7) ) )">
<xsl:attribute name="id">DK-R-008</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>For Danish Suppliers PaymentReference is mandatory and MUST start with
                01#, 04# or 15# (kortartkode), and PayeePartyCreditorFinancialAccount/IBANID (Giro
                kontonummer) is mandatory and must be 7 characters long, when payment means equals
                50 (Giro)</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     not(($supplierCountry = 'DK')                     and (ram:TypeCode = '50')                     and ((substring(../ram:PaymentReference, 0, 4) = '04#')                     or (substring(../ram:PaymentReference, 0, 4) = '15#'))                     and not(string-length(../ram:PaymentReference) = 19)                     )"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="not(($supplierCountry = 'DK') and (ram:TypeCode = '50') and ((substring(../ram:PaymentReference, 0, 4) = '04#') or (substring(../ram:PaymentReference, 0, 4) = '15#')) and not(string-length(../ram:PaymentReference) = 19) )">
<xsl:attribute name="id">DK-R-009</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>For Danish Suppliers if the PaymentReference is prefixed with 04# or
                015# the 16 digits instruction Id must be added to the PaymentReference eg.
                "04#1234567890123456" when Payment means equals 50 (Giro)</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     not(($supplierCountry = 'DK')                     and (ram:TypeCode = '93')                     and not(((substring(../ram:PaymentReference, 0, 4) = '71#')                     or (substring(../ram:PaymentReference, 0, 4) = '73#')                     or (substring(../ram:PaymentReference, 0, 4) = '75#'))                     and (string-length(ram:PayeePartyCreditorFinancialAccount/ram:IBANID/text()) = 8)                     )                     )"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="not(($supplierCountry = 'DK') and (ram:TypeCode = '93') and not(((substring(../ram:PaymentReference, 0, 4) = '71#') or (substring(../ram:PaymentReference, 0, 4) = '73#') or (substring(../ram:PaymentReference, 0, 4) = '75#')) and (string-length(ram:PayeePartyCreditorFinancialAccount/ram:IBANID/text()) = 8) ) )">
<xsl:attribute name="id">DK-R-010</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>For Danish Suppliers the PaymentReference is mandatory and MUST start
                with 71#, 73# or 75# (kortartkode) and and PayeePartyCreditorFinancialAccount/IBANID
                (Kreditornummer) is mandatory and must be exactly 8 characters long, when Payment
                means equals 93 (FIK)</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     not(($supplierCountry = 'DK')                     and (ram:TypeCode = '93')                     and ((substring(../ram:PaymentReference, 0, 4) = '71#')                     or (substring(../ram:PaymentReference, 0, 4) = '75#'))                     and not((string-length(../ram:PaymentReference) = 18)                     or (string-length(../ram:PaymentReference) = 19))                     )"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="not(($supplierCountry = 'DK') and (ram:TypeCode = '93') and ((substring(../ram:PaymentReference, 0, 4) = '71#') or (substring(../ram:PaymentReference, 0, 4) = '75#')) and not((string-length(../ram:PaymentReference) = 18) or (string-length(../ram:PaymentReference) = 19)) )">
<xsl:attribute name="id">DK-R-011</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>For Danish Suppliers if the PaymentReference is prefixed with 71# or
                75# the 15-16 digits instruction Id must be added to the PaymentReference eg.
                "71#1234567890123456" when payment Method equals 93 (FIK)</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     not(($supplierCountry = 'DK')                     and (ram:TypeCode = '97')                     )"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="not(($supplierCountry = 'DK') and (ram:TypeCode = '97') )">
<xsl:attribute name="id">DK-R-012</xsl:attribute>
<xsl:attribute name="flag">warning</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>For Danish suppliers when Payment means equals 97, the payment is
                made to "NemKonto"</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M13"/>
</xsl:template>
<xsl:template match="text()" priority="-1" mode="M13"/>
<xsl:template match="@*|node()" priority="-2" mode="M13">
<xsl:apply-templates select="*" mode="M13"/>
</xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="ram:SellerTradeParty[$supplierCountry = 'IT']" priority="1000" mode="M14">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="ram:SellerTradeParty[$supplierCountry = 'IT']"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="(exists(ram:SpecifiedTaxRegistration/ram:ID) and ram:SpecifiedTaxRegistration/ram:ID[@schemeID = 'FC'])"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="(exists(ram:SpecifiedTaxRegistration/ram:ID) and ram:SpecifiedTaxRegistration/ram:ID[@schemeID = 'FC'])">
<xsl:attribute name="id">IT-R-001</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text> [IT-R-001] BT-32 (Seller tax registration identifier) - Italian
                suppliers MUST provide Tax Regime Identifier. I fornitori italiani devono indicare
                il Regime Fiscale </svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="exists(ram:PostalTradeAddress/ram:LineOne)"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="exists(ram:PostalTradeAddress/ram:LineOne)">
<xsl:attribute name="id">IT-R-002</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>
                [IT-R-002] BT-35 (Seller address line 1) - Italian suppliers MUST provide the postal
                address line 1 - I fornitori italiani devono indicare l'indirizzo postale. </svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="exists(ram:PostalTradeAddress/ram:CityName)"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="exists(ram:PostalTradeAddress/ram:CityName)">
<xsl:attribute name="id">IT-R-003</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>
                [IT-R-003] BT-37 (Seller city) - Italian suppliers MUST provide the postal address
                city - I fornitori italiani devono indicare la città di residenza. </svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="exists(ram:PostalTradeAddress/ram:PostcodeCode)"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="exists(ram:PostalTradeAddress/ram:PostcodeCode)">
<xsl:attribute name="id">IT-R-004</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text> [IT-R-004] BT-38 (Seller post code) - Italian suppliers MUST provide
                the postal address post code - I fornitori italiani devono indicare il CAP di
                residenza.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M14"/>
</xsl:template>
<xsl:template match="text()" priority="-1" mode="M14"/>
<xsl:template match="@*|node()" priority="-2" mode="M14">
<xsl:apply-templates select="*" mode="M14"/>
</xsl:template>

<!--PATTERN -->


	<!--RULE -->
<xsl:template match="rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty[ram:PostalTradeAddress/ram:CountryID = 'SE' and ram:SpecifiedTaxRegistration/substring(ram:ID[@schemeID = 'VAT'], 1, 2) = 'SE']" priority="1006" mode="M15">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty[ram:PostalTradeAddress/ram:CountryID = 'SE' and ram:SpecifiedTaxRegistration/substring(ram:ID[@schemeID = 'VAT'], 1, 2) = 'SE']"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="string-length(normalize-space(ram:SpecifiedTaxRegistration/ram:ID[@schemeID = 'VAT'])) = 14"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="string-length(normalize-space(ram:SpecifiedTaxRegistration/ram:ID[@schemeID = 'VAT'])) = 14">
<xsl:attribute name="id">SE-R-1</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>For Swedish suppliers, Swedish VAT-numbers must consist of 14
                characters.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="string(number(substring(ram:SpecifiedTaxRegistration/ram:ID[@schemeID = 'VAT'], 3, 12))) != 'NaN'"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="string(number(substring(ram:SpecifiedTaxRegistration/ram:ID[@schemeID = 'VAT'], 3, 12))) != 'NaN'">
<xsl:attribute name="id">SE-R-2</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>For Swedish suppliers, the Swedish VAT-numbers must have the trailing
                12 characters in numeric form</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M15"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedLegalOrganization[../ram:CountryID = 'SE' and ram:SpecifiedLegalOrganization/ram:ID]" priority="1005" mode="M15">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty/ram:SpecifiedLegalOrganization[../ram:CountryID = 'SE' and ram:SpecifiedLegalOrganization/ram:ID]"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="string(number(ram:ID)) != 'NaN'"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="string(number(ram:ID)) != 'NaN'">
<xsl:attribute name="id">SE-R-3</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Swedish organisation numbers should be numeric.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="string-length(normalize-space(ram:ID)) = 10"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="string-length(normalize-space(ram:ID)) = 10">
<xsl:attribute name="id">SE-R-4</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Swedish organisation numbers consist of 10 characters.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M15"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty[ram:PostalTradeAddress/ram:CountryID = 'SE' and ram:SpecifiedLegalOrganization/ram:ID]/ram:SpecifiedTaxRegistration[ram:ID/@schemeID = 'FC']/ram:ID" priority="1004" mode="M15">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty[ram:PostalTradeAddress/ram:CountryID = 'SE' and ram:SpecifiedLegalOrganization/ram:ID]/ram:SpecifiedTaxRegistration[ram:ID/@schemeID = 'FC']/ram:ID"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="normalize-space(upper-case(.)) = 'GODKÄND FÖR F-SKATT'"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="normalize-space(upper-case(.)) = 'GODKÄND FÖR F-SKATT'">
<xsl:attribute name="id">SE-R-5</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>For Swedish suppliers, when using Seller tax registration identifier, 'Godkänd för
                F-skatt' must be stated</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M15"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="//ram:ApplicableTradeTax[/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty[ram:PostalTradeAddress/ram:CountryID = 'SE']/ram:SpecifiedTaxRegistration[substring(ram:ID[@schemeID = 'VAT'], 1, 2) = 'SE']] | //ram:CategoryTradeTax[/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty[ram:PostalTradeAddress/ram:CountryID = 'SE']/ram:SpecifiedTaxRegistration[substring(ram:ID[@schemeID = 'VAT'], 1, 2) = 'SE']]" priority="1003" mode="M15">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="//ram:ApplicableTradeTax[/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty[ram:PostalTradeAddress/ram:CountryID = 'SE']/ram:SpecifiedTaxRegistration[substring(ram:ID[@schemeID = 'VAT'], 1, 2) = 'SE']] | //ram:CategoryTradeTax[/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty[ram:PostalTradeAddress/ram:CountryID = 'SE']/ram:SpecifiedTaxRegistration[substring(ram:ID[@schemeID = 'VAT'], 1, 2) = 'SE']]"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="number(ram:RateApplicablePercent) = 25 or number(ram:RateApplicablePercent) = 12 or number(ram:RateApplicablePercent) = 6"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="number(ram:RateApplicablePercent) = 25 or number(ram:RateApplicablePercent) = 12 or number(ram:RateApplicablePercent) = 6">
<xsl:attribute name="id">SE-R-6</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>For Swedish suppliers, only standard VAT rate of 6, 12 or 25 are
                used</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M15"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction[ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty[ram:PostalTradeAddress/ram:CountryID = 'SE']]/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans[normalize-space(ram:TypeCode) = '30' and normalize-space(ram:PayeeSpecifiedCreditorFinancialInstitution/ram:BICID) = 'SE:PLUSGIRO']/ram:PayeePartyCreditorFinancialAccount/ram:ProprietaryID" priority="1002" mode="M15">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction[ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty[ram:PostalTradeAddress/ram:CountryID = 'SE']]/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans[normalize-space(ram:TypeCode) = '30' and normalize-space(ram:PayeeSpecifiedCreditorFinancialInstitution/ram:BICID) = 'SE:PLUSGIRO']/ram:PayeePartyCreditorFinancialAccount/ram:ProprietaryID"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="string(number(normalize-space(.))) != 'NaN'"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="string(number(normalize-space(.))) != 'NaN'">
<xsl:attribute name="id">SE-R-7</xsl:attribute>
<xsl:attribute name="flag">warning</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>For Swedish suppliers using Plusgiro, the Account ID must be numeric </svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="string-length(normalize-space(.)) &gt;= 2 and string-length(normalize-space(.)) &lt;= 8"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="string-length(normalize-space(.)) &gt;= 2 and string-length(normalize-space(.)) &lt;= 8">
<xsl:attribute name="id">SE-R-10</xsl:attribute>
<xsl:attribute name="flag">warning</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>For Swedish suppliers using Plusgiro, the Account ID must have 2-8
                characteres</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M15"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction[ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty[ram:PostalTradeAddress/ram:CountryID = 'SE']]/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans[normalize-space(ram:TypeCode) = '30' and normalize-space(ram:PayeeSpecifiedCreditorFinancialInstitution/ram:BICID) = 'SE:BANKGIRO']/ram:PayeePartyCreditorFinancialAccount/ram:ProprietaryID" priority="1001" mode="M15">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction[ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty[ram:PostalTradeAddress/ram:CountryID = 'SE']]/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans[normalize-space(ram:TypeCode) = '30' and normalize-space(ram:PayeeSpecifiedCreditorFinancialInstitution/ram:BICID) = 'SE:BANKGIRO']/ram:PayeePartyCreditorFinancialAccount/ram:ProprietaryID"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="string(number(normalize-space(.))) != 'NaN'"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="string(number(normalize-space(.))) != 'NaN'">
<xsl:attribute name="id">SE-R-8</xsl:attribute>
<xsl:attribute name="flag">warning</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>For Swedish suppliers using Bankgiro, the Account ID must be numeric </svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="string-length(normalize-space(.)) = 7 or string-length(normalize-space(.)) = 8"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="string-length(normalize-space(.)) = 7 or string-length(normalize-space(.)) = 8">
<xsl:attribute name="id">SE-R-9</xsl:attribute>
<xsl:attribute name="flag">warning</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>For Swedish suppliers using Bankgiro, the Account ID must have 7-8
                characters</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M15"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction[ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty[ram:PostalTradeAddress/ram:CountryID = 'SE']]/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans[normalize-space(ram:TypeCode) = '50' or normalize-space(ram:TypeCode) = '56']" priority="1000" mode="M15">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction[ram:ApplicableHeaderTradeAgreement/ram:SellerTradeParty[ram:PostalTradeAddress/ram:CountryID = 'SE']]/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans[normalize-space(ram:TypeCode) = '50' or normalize-space(ram:TypeCode) = '56']"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="false()"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="false()">
<xsl:attribute name="id">SE-R-11</xsl:attribute>
<xsl:attribute name="flag">warning</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>For Swedish suppliers using Swedish
                Bankgiro or Plusgiro, the proper way to indicate this is to use Code 30 for
                PaymentMeans and FinancialInstitutionBranch ID with code SE:BANKGIRO or
                SE:PLUSGIRO</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M15"/>
</xsl:template>
<xsl:template match="text()" priority="-1" mode="M15"/>
<xsl:template match="@*|node()" priority="-2" mode="M15">
<xsl:apply-templates select="*" mode="M15"/>
</xsl:template>

<!--PATTERN -->
<xsl:variable name="ISO3166" select="tokenize('AD AE AF AG AI AL AM AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BJ BL BM BN BO BQ BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CW CX CY CZ DE DJ DK DM DO DZ EC EE EG EH ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IM IN IO IQ IR IS IT JE JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MF MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RE RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR SS ST SV SX SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW', '\s')"/>
<xsl:variable name="ISO4217" select="tokenize('AFN EUR ALL DZD USD AOA XCD XCD ARS AMD AWG AUD AZN BSD BHD BDT BBD BYN BZD XOF BMD INR BTN BOB BOV USD BAM BWP NOK BRL USD BND BGN XOF BIF CVE KHR XAF CAD KYD XAF XAF CLP CLF CNY AUD AUD COP COU KMF CDF XAF NZD CRC XOF HRK CUP CUC ANG CZK DKK DJF XCD DOP USD EGP SVC USD XAF ERN ETB FKP DKK FJD XPF XAF GMD GEL GHS GIP DKK XCD USD GTQ GBP GNF XOF GYD HTG USD AUD HNL HKD HUF ISK INR IDR XDR IRR IQD GBP ILS JMD JPY GBP JOD KZT KES AUD KPW KRW KWD KGS LAK LBP LSL ZAR LRD LYD CHF MOP MKD MGA MWK MYR MVR XOF USD MRO MUR XUA MXN MXV USD MDL MNT XCD MAD MZN MMK NAD ZAR AUD NPR XPF NZD NIO XOF NGN NZD AUD USD NOK OMR PKR USD PAB USD PGK PYG PEN PHP NZD PLN USD QAR RON RUB RWF SHP XCD XCD XCD WST STD SAR XOF RSD SCR SLL SGD ANG XSU SBD SOS ZAR SSP LKR SDG SRD NOK SZL SEK CHF CHE CHW SYP TWD TJS TZS THB USD XOF NZD TOP TTD TND TRY TMT USD AUD UGX UAH AED GBP USD USD USN UYU UYI UZS VUV VEF VND USD USD XPF MAD YER ZMW ZWL XBA XBB XBC XBD XTS XXX XAU XPD XPT XAG', '\s')"/>
<xsl:variable name="MIMECODE" select="tokenize('application/pdf image/png image/jpeg text/csv application/vnd.openxmlformats-officedocument.spreadsheetml.sheet application/vnd.oasis.opendocument.spreadsheet', '\s')"/>
<xsl:variable name="UNCL2005" select="tokenize('3 35 432', '\s')"/>
<xsl:variable name="UNCL5189" select="tokenize('41 42 60 62 63 64 65 66 67 68 70 71 88 95 100 102 103 104 105', '\s')"/>
<xsl:variable name="UNCL7161" select="tokenize('AA AAA AAC AAD AAE AAF AAH AAI AAS AAT AAV AAY AAZ ABA ABB ABC ABD ABF ABK ABL ABN ABR ABS ABT ABU ACF ACG ACH ACI ACJ ACK ACL ACM ACS ADC ADE ADJ ADK ADL ADM ADN ADO ADP ADQ ADR ADT ADW ADY ADZ AEA AEB AEC AED AEF AEH AEI AEJ AEK AEL AEM AEN AEO AEP AES AET AEU AEV AEW AEX AEY AEZ AJ AU CA CAB CAD CAE CAF CAI CAJ CAK CAL CAM CAN CAO CAP CAQ CAR CAS CAT CAU CAV CAW CD CG CS CT DAB DAD DL EG EP ER FAA FAB FAC FC FH FI GAA HAA HD HH IAA IAB ID IF IR IS KO L1 LA LAA LAB LF MAE MI ML NAA OA PA PAA PC PL RAB RAC RAD RAF RE RF RH RV SA SAA SAD SAE SAI SG SH SM SU TAB TAC TT TV V1 V2 WH XAA YY ZZZ', '\s')"/>
<xsl:variable name="UNCL5305" select="tokenize('AE E S Z G O K L M', '\s')"/>

	<!--RULE -->
<xsl:template match="ram:AttachmentBinaryObject[@mimeCode]" priority="1007" mode="M16">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="ram:AttachmentBinaryObject[@mimeCode]"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     some $code in $MIMECODE                         satisfies @mimeCode = $code"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="some $code in $MIMECODE satisfies @mimeCode = $code">
<xsl:attribute name="id">PEPPOL-EN16931-CL001</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Invalid mime code.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M16"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="ram:SpecifiedTradeAllowanceCharge[normalize-space(ram:ChargeIndicator/udt:Indicator) = 'false']/ram:ReasonCode" priority="1006" mode="M16">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="ram:SpecifiedTradeAllowanceCharge[normalize-space(ram:ChargeIndicator/udt:Indicator) = 'false']/ram:ReasonCode"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     some $code in $UNCL5189                         satisfies normalize-space(text()) = $code"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="some $code in $UNCL5189 satisfies normalize-space(text()) = $code">
<xsl:attribute name="id">PEPPOL-EN16931-CL002</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Reason code MUST be according to subset of UNCL 5189 D.16B.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M16"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="ram:SpecifiedTradeAllowanceCharge[normalize-space(ram:ChargeIndicator/udt:Indicator) = 'true']/ram:ReasonCode" priority="1005" mode="M16">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="ram:SpecifiedTradeAllowanceCharge[normalize-space(ram:ChargeIndicator/udt:Indicator) = 'true']/ram:ReasonCode"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     some $code in $UNCL7161                         satisfies normalize-space(text()) = $code"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="some $code in $UNCL7161 satisfies normalize-space(text()) = $code">
<xsl:attribute name="id">PEPPOL-EN16931-CL003</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Reason code MUST be according to UNCL 7161 D.16B.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M16"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="ram:ApplicableTradeTax/ram:CategoryCode" priority="1004" mode="M16">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="ram:ApplicableTradeTax/ram:CategoryCode"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     some $code in $UNCL5305                         satisfies normalize-space(text()) = $code"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="some $code in $UNCL5305 satisfies normalize-space(text()) = $code">
<xsl:attribute name="id">PEPPOL-EN16931-CL004</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Tax category code MUST be according to defined subset of UNCL 5305
                D.16B.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M16"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="ram:CountryID" priority="1003" mode="M16">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="ram:CountryID"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     some $code in $ISO3166                         satisfies normalize-space(text()) = $code"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="some $code in $ISO3166 satisfies normalize-space(text()) = $code">
<xsl:attribute name="id">PEPPOL-EN16931-CL005</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Country code MUST be according to ISO 3166 Alpha-2.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M16"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="ram:TaxTotalAmount[@currencyID]" priority="1002" mode="M16">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="ram:TaxTotalAmount[@currencyID]"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     some $code in $ISO4217                         satisfies @currencyID = $code"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="some $code in $ISO4217 satisfies @currencyID = $code">
<xsl:attribute name="id">PEPPOL-EN16931-CL007</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Currency code must be according to ISO 4217:2005</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M16"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="ram:ExchangedDocument/ram:TypeCode" priority="1001" mode="M16">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="ram:ExchangedDocument/ram:TypeCode"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="                     $profile != '01' or (some $code in tokenize('380 383 386 393 82 80 84 395 575 623 780 381 396 81 83 532', '\s')                         satisfies normalize-space(text()) = $code)"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="$profile != '01' or (some $code in tokenize('380 383 386 393 82 80 84 395 575 623 780 381 396 81 83 532', '\s') satisfies normalize-space(text()) = $code)">
<xsl:attribute name="id">PEPPOL-EN16931-P0100</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>Invoice type code MUST be set according to the profile.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M16"/>
</xsl:template>

	<!--RULE -->
<xsl:template match="udt:DateTimeString" priority="1000" mode="M16">
<svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="udt:DateTimeString"/>

		<!--ASSERT -->
<xsl:choose>
<xsl:when test="normalize-space(@format) = '102' and string-length(text()) = 8 and matches(normalize-space(text()), '20[0-9]{6}')"/>
<xsl:otherwise>
<svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="normalize-space(@format) = '102' and string-length(text()) = 8 and matches(normalize-space(text()), '20[0-9]{6}')">
<xsl:attribute name="id">PEPPOL-EN16931-F001</xsl:attribute>
<xsl:attribute name="flag">fatal</xsl:attribute>
<xsl:attribute name="location">
<xsl:apply-templates select="." mode="schematron-select-full-path"/>
</xsl:attribute>
<svrl:text>A date MUST be formatted YYYYMMDD.</svrl:text>
</svrl:failed-assert>
</xsl:otherwise>
</xsl:choose>
<xsl:apply-templates select="*" mode="M16"/>
</xsl:template>
<xsl:template match="text()" priority="-1" mode="M16"/>
<xsl:template match="@*|node()" priority="-2" mode="M16">
<xsl:apply-templates select="*" mode="M16"/>
</xsl:template>
</xsl:stylesheet>
