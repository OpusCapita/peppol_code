<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"
                xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
                xmlns:ubl="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2"
                xmlns:u="utils"
                version="2.0"><!--Implementers: please note that overriding process-prolog or process-root is 
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
   <xsl:output xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
               method="xml"
               omit-xml-declaration="no"
               standalone="yes"
               indent="yes"/>

   <!--XSD TYPES FOR XSLT2-->


   <!--KEYS AND FUNCTIONS-->
   <function xmlns="http://www.w3.org/1999/XSL/Transform"
             name="u:mod11"
             as="xs:boolean">
      <param name="val"/>
      <variable name="length" select="string-length($val) - 1"/>
      <variable name="digits"
                select="reverse(for $i in string-to-codepoints(substring($val, 0, $length + 1)) return $i - 48)"/>
      <variable name="weightedSum"
                select="sum(for $i in (0 to $length - 1) return $digits[$i + 1] * (($i mod 6) + 2))"/>
      <value-of select="number($val) &gt; 0 and (11 - ($weightedSum mod 11)) mod 11 = number(substring($val, $length + 1, 1))"/>
  </function>
   <function xmlns="http://www.w3.org/1999/XSL/Transform"
             name="u:gln"
             as="xs:boolean">
      <param name="val"/>
      <variable name="length" select="string-length($val) - 1"/>
      <variable name="digits"
                select="reverse(for $i in string-to-codepoints(substring($val, 0, $length + 1)) return $i - 48)"/>
      <variable name="weightedSum"
                select="sum(for $i in (0 to $length - 1) return $digits[$i + 1] * (1 + ((($i + 1) mod 2) * 2)))"/>
      <value-of select="10 - ($weightedSum mod 10) = number(substring($val, $length + 1, 1))"/>
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
      <xsl:variable name="preceding"
                    select="count(preceding-sibling::*[local-name()=local-name(current())                                   and namespace-uri() = namespace-uri(current())])"/>
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
      <svrl:schematron-output xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                              title="Sjekk mot norske regler "
                              schemaVersion="iso">
         <xsl:comment>
            <xsl:value-of select="$archiveDirParameter"/>   
		 <xsl:value-of select="$archiveNameParameter"/>  
		 <xsl:value-of select="$fileNameParameter"/>  
		 <xsl:value-of select="$fileDirParameter"/>
         </xsl:comment>
         <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"
                                             prefix="cbc"/>
         <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
                                             prefix="cac"/>
         <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2"
                                             prefix="ubl"/>
         <svrl:ns-prefix-in-attribute-values uri="utils" prefix="u"/>
         <svrl:active-pattern>
            <xsl:attribute name="document">
               <xsl:value-of select="document-uri(/)"/>
            </xsl:attribute>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M7"/>
      </svrl:schematron-output>
   </xsl:template>

   <!--SCHEMATRON PATTERNS-->
   <svrl:text xmlns:svrl="http://purl.oclc.org/dsdl/svrl">Sjekk mot norske regler </svrl:text>

   <!--PATTERN -->
   <xsl:variable name="isB2C"
                 select="//cac:AdditionalDocumentReference/cbc:DocumentType = 'elektroniskB2Cfaktura'"/>
   <xsl:variable name="documentCurrencyCode"
                 select="/ubl:CreditNote/cbc:DocumentCurrencyCode"/>

	  <!--RULE -->
   <xsl:template match="cbc:ProfileID" priority="1019" mode="M7">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="cbc:ProfileID"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="some $p in tokenize('urn:www.cenbii.eu:profile:bii05:ver2.0 urn:www.cenbii.eu:profile:biixx:ver2.0 urn:www.cenbii.eu:profile:biixy:ver2.0', '\s') satisfies $p = normalize-space(.)"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="some $p in tokenize('urn:www.cenbii.eu:profile:bii05:ver2.0 urn:www.cenbii.eu:profile:biixx:ver2.0 urn:www.cenbii.eu:profile:biixy:ver2.0', '\s') satisfies $p = normalize-space(.)">
               <xsl:attribute name="id">EHFPROFILE-T14-R001</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[EHFPROFILE-T14-R001]-A credit note transaction T14 must only be used with profiles 5, xx or xy.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="ubl:CreditNote" priority="1018" mode="M7">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="ubl:CreditNote"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="((cac:TaxTotal[cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:ID = 'VAT']/cbc:TaxAmount != 0) and (cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID) or (cac:TaxTotal[cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:ID = 'VAT']/cbc:TaxAmount = 0) or not((cac:TaxTotal[cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:ID = 'VAT'])))"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="((cac:TaxTotal[cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:ID = 'VAT']/cbc:TaxAmount != 0) and (cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID) or (cac:TaxTotal[cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:ID = 'VAT']/cbc:TaxAmount = 0) or not((cac:TaxTotal[cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:ID = 'VAT'])))">
               <xsl:attribute name="id">NOGOV-T14-R003</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R003]-If the VAT total amount in a credit note exists it MUST contain the suppliers VAT number.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="cac:LegalMonetaryTotal/cbc:ChargeTotalAmount or not(cac:AllowanceCharge[cbc:ChargeIndicator = 'true'])"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="cac:LegalMonetaryTotal/cbc:ChargeTotalAmount or not(cac:AllowanceCharge[cbc:ChargeIndicator = 'true'])">
               <xsl:attribute name="id">NOGOV-T14-R021</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R021]-If charge is present on document level, total charge must be stated.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="cac:LegalMonetaryTotal/cbc:AllowanceTotalAmount or not(cac:AllowanceCharge[cbc:ChargeIndicator = 'false'])"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="cac:LegalMonetaryTotal/cbc:AllowanceTotalAmount or not(cac:AllowanceCharge[cbc:ChargeIndicator = 'false'])">
               <xsl:attribute name="id">NOGOV-T14-R022</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R022]-If allowance is present on document level, total allowance must be stated.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="local-name(/*) = 'CreditNote' and (((//cac:BillingReference/cac:InvoiceDocumentReference/cbc:ID) or (//cac:BillingReference/cac:CreditNoteDocumentReference/cbc:ID)) or (//cbc:ProfileID = 'urn:www.cenbii.eu:profile:biixx:ver2.0'))"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="local-name(/*) = 'CreditNote' and (((//cac:BillingReference/cac:InvoiceDocumentReference/cbc:ID) or (//cac:BillingReference/cac:CreditNoteDocumentReference/cbc:ID)) or (//cbc:ProfileID = 'urn:www.cenbii.eu:profile:biixx:ver2.0'))">
               <xsl:attribute name="id">EHFPROFILE-T14-R002</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[EHFPROFILE-T14-R002]-A creditnote transaction T14 in Profile other than xx MUST have an invoice or creditnote reference identifier.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="cac:AccountingSupplierParty/cac:Party"
                 priority="1017"
                 mode="M7">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="cac:AccountingSupplierParty/cac:Party"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="cac:Contact/cbc:ID"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="cac:Contact/cbc:ID">
               <xsl:attribute name="id">NOGOV-T14-R001</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R001]-A contact reference identifier SHOULD be provided for AccountingSupplierParty according to EHF.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="cbc:Amount | cbc:TaxableAmount | cbc:TaxAmount | cbc:LineExtensionAmount | cbc:PriceAmount | cbc:BaseAmount | cac:LegalMonetaryTotal/cbc:*"
                 priority="1016"
                 mode="M7">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="cbc:Amount | cbc:TaxableAmount | cbc:TaxAmount | cbc:LineExtensionAmount | cbc:PriceAmount | cbc:BaseAmount | cac:LegalMonetaryTotal/cbc:*"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="not(@currencyID) or @currencyID = $documentCurrencyCode"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="not(@currencyID) or @currencyID = $documentCurrencyCode">
               <xsl:attribute name="id">NOGOV-T14-R005</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R005]-The attribute currencyID must have the same value as DocumentCurrencyCode, except the attribute for TransactionCurrencyTaxAmount.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="not(parent::node()/local-name() = 'LegalMonetaryTotal') or string-length(substring-after(., '.')) &lt;= 2"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="not(parent::node()/local-name() = 'LegalMonetaryTotal') or string-length(substring-after(., '.')) &lt;= 2">
               <xsl:attribute name="id">NOGOV-T14-R024</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R024]-Document level amounts cannot have more than 2 decimals</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="cac:Item" priority="1015" mode="M7">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="cac:Item"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="cac:SellersItemIdentification/cbc:ID"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="cac:SellersItemIdentification/cbc:ID">
               <xsl:attribute name="id">NOGOV-T14-R002</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R002]-The sellers ID for the item SHOULD be provided according to EHF.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="cac:AccountingCustomerParty/cac:Party"
                 priority="1014"
                 mode="M7">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="cac:AccountingCustomerParty/cac:Party"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="$isB2C or cac:PartyLegalEntity/cbc:CompanyID"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="$isB2C or cac:PartyLegalEntity/cbc:CompanyID">
               <xsl:attribute name="id">NOGOV-T14-R004</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R004]-PartyLegalEntity for AccountingCustomerParty MUST be provided according to EHF.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="$isB2C or cac:PartyLegalEntity/cbc:RegistrationName"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="$isB2C or cac:PartyLegalEntity/cbc:RegistrationName">
               <xsl:attribute name="id">NOGOV-T14-R008</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R008]-Registration name for AccountingCustomerParty MUST be provided according to EHF.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="cac:PartyIdentification/cbc:ID"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="cac:PartyIdentification/cbc:ID">
               <xsl:attribute name="id">NOGOV-T14-R006</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R006]-A customer number for AccountingCustomerParty SHOULD be provided according to EHF.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="cac:Contact/cbc:ID"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="cac:Contact/cbc:ID">
               <xsl:attribute name="id">NOGOV-T14-R007</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R007]-A contact reference identifier MUST be provided for AccountingCustomerParty according to EHF.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="ubl:CreditNote/cac:TaxTotal" priority="1013" mode="M7">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="ubl:CreditNote/cac:TaxTotal"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="cac:TaxSubtotal"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="cac:TaxSubtotal">
               <xsl:attribute name="id">NOGOV-T14-R018</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R018]-A credit note MUST have Tax Subtotal specifications.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="string-length(substring-after(cbc:TaxAmount, '.')) &lt;= 2"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="string-length(substring-after(cbc:TaxAmount, '.')) &lt;= 2">
               <xsl:attribute name="id">NOGOV-T14-R025</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R025]-Total tax amount cannot have more than 2 decimals</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="count(distinct-values(cac:TaxSubtotal/cac:TaxCategory/cbc:ID/normalize-space(text()))) = count(cac:TaxSubtotal)"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="count(distinct-values(cac:TaxSubtotal/cac:TaxCategory/cbc:ID/normalize-space(text()))) = count(cac:TaxSubtotal)">
               <xsl:attribute name="id">NOGOV-T14-R041</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R041]-Multiple tax subtotals per tax category is not allowed.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="cac:TaxRepresentativeParty" priority="1012" mode="M7">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="cac:TaxRepresentativeParty"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="cac:PartyTaxScheme/cbc:CompanyID"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="cac:PartyTaxScheme/cbc:CompanyID">
               <xsl:attribute name="id">NOGOV-T14-R017</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R017]-Company identifier MUST be specified when describing a Tax Representative</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="cac:Party/cbc:EndpointID" priority="1011" mode="M7">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="cac:Party/cbc:EndpointID"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="@schemeID = 'NO:ORGNR'"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="@schemeID = 'NO:ORGNR'">
               <xsl:attribute name="id">NOGOV-T14-R010</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R010]-An endpoint identifier scheme MUST have the value 'NO:ORGNR'.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="matches(., '^[0-9]{9}$') and u:mod11(.)"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="matches(., '^[0-9]{9}$') and u:mod11(.)">
               <xsl:attribute name="id">NOGOV-T14-R009</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R009]-Endpoint ID MUST be a valid Norwegian organization number. Only numerical value allowed</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="cac:PartyLegalEntity/cbc:CompanyID"
                 priority="1010"
                 mode="M7">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="cac:PartyLegalEntity/cbc:CompanyID"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="matches(., '^[0-9]{9}$') and u:mod11(.)"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="matches(., '^[0-9]{9}$') and u:mod11(.)">
               <xsl:attribute name="id">NOGOV-T14-R014</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R014]-A valid Norwegian organization number for seller, buyer and payee MUST be nine numbers..</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="cac:PartyTaxScheme/cbc:CompanyID" priority="1009" mode="M7">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="cac:PartyTaxScheme/cbc:CompanyID"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="matches(., '^[0-9]{9}MVA$') and u:mod11(substring(., 1, 9))"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="matches(., '^[0-9]{9}MVA$') and u:mod11(substring(., 1, 9))">
               <xsl:attribute name="id">NOGOV-T14-R013</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R013]-A VAT number MUST be valid Norwegian organization number (nine numbers) followed by the letters MVA.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="cbc:IssueDate | cbc:TaxPointDate | cbc:StartDate | cbc:EndDate | cbc:ActualDeliveryDate | cbc:PaymentDueDate | cbc:Date"
                 priority="1008"
                 mode="M7">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="cbc:IssueDate | cbc:TaxPointDate | cbc:StartDate | cbc:EndDate | cbc:ActualDeliveryDate | cbc:PaymentDueDate | cbc:Date"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(text() castable as xs:date) and (string-length(.) = 10)"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="(text() castable as xs:date) and (string-length(.) = 10)">
               <xsl:attribute name="id">NOGOV-T14-R011</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R011]-A date must be formatted YYYY-MM-DD.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="cbc:EmbeddedDocumentBinaryObject[@mimeCode]"
                 priority="1007"
                 mode="M7">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="cbc:EmbeddedDocumentBinaryObject[@mimeCode]"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="some $c in tokenize('application/pdf image/gif image/tiff image/jpeg image/png text/plain', '\s') satisfies $c = @mimeCode"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="some $c in tokenize('application/pdf image/gif image/tiff image/jpeg image/png text/plain', '\s') satisfies $c = @mimeCode">
               <xsl:attribute name="id">NOGOV-T14-R020</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R020]-Attachment is not a recommended MIMEType.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:ID[@schemeID = 'BBAN']"
                 priority="1006"
                 mode="M7">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:ID[@schemeID = 'BBAN']"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="matches(., '^[0-9]+$')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="matches(., '^[0-9]+$')">
               <xsl:attribute name="id">NOGOV-T14-R015</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R015]-Only numbers are allowed as bank account number if scheme is BBAN.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:ID[@schemeID = 'IBAN']"
                 priority="1005"
                 mode="M7">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:ID[@schemeID = 'IBAN']"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="matches(., '^[A-Z]{2}[0-9]+$')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="matches(., '^[A-Z]{2}[0-9]+$')">
               <xsl:attribute name="id">NOGOV-T14-R016</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R016]-IBAN number is not for a Norwegain bank account</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="cac:CommodityClassification" priority="1004" mode="M7">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="cac:CommodityClassification"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="cbc:ItemClassificationCode"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="cbc:ItemClassificationCode">
               <xsl:attribute name="id">NOGOV-T14-R019</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R019]-Item classification code MUST be specified when describing commodity classification.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="cac:PartyIdentification/cbc:ID[@schemeID = 'NO:ORGNR']"
                 priority="1003"
                 mode="M7">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="cac:PartyIdentification/cbc:ID[@schemeID = 'NO:ORGNR']"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="matches(., '^[0-9]{9}$')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="matches(., '^[0-9]{9}$')">
               <xsl:attribute name="id">NOGOV-T14-R023</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R023]-When scheme is NO:ORGNR, a Norwegian organizational number must be used. Only numerical value allowed</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="cac:TaxSubtotal" priority="1002" mode="M7">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="cac:TaxSubtotal"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="string-length(substring-after(cbc:TaxableAmount, '.')) &lt;= 2"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="string-length(substring-after(cbc:TaxableAmount, '.')) &lt;= 2">
               <xsl:attribute name="id">NOGOV-T14-R026</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R026]-Tax subtotal amounts cannot have more than 2 decimals</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="string-length(substring-after(cbc:TaxAmount, '.')) &lt;= 2"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="string-length(substring-after(cbc:TaxAmount, '.')) &lt;= 2">
               <xsl:attribute name="id">NOGOV-T14-R026</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R026]-Tax subtotal amounts cannot have more than 2 decimals</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="ubl:CreditNote/cac:AllowanceCharge"
                 priority="1001"
                 mode="M7">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="ubl:CreditNote/cac:AllowanceCharge"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="string-length(substring-after(cbc:Amount, '.')) &lt;= 2"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="string-length(substring-after(cbc:Amount, '.')) &lt;= 2">
               <xsl:attribute name="id">NOGOV-T14-R027</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R027]-Allowance or charge amounts on document level cannot have more than 2 decimals</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="cbc:ID[@schemeID = 'GLN']" priority="1000" mode="M7">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="cbc:ID[@schemeID = 'GLN']"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="u:gln(.)"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="u:gln(.)">
               <xsl:attribute name="id">NOGOV-T14-R044</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T14-R044]-Invalid GLN number provided.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M7"/>
   <xsl:template match="@*|node()" priority="-2" mode="M7">
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>
</xsl:stylesheet>
