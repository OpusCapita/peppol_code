<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"
                xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
                xmlns:ubl="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"
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
   <function xmlns="http://www.w3.org/1999/XSL/Transform" name="u:mod11">
     <param name="val"/>
     <variable name="length" select="string-length($val) - 1"/>
     <variable name="digits"
                select="reverse(for $i in string-to-codepoints(substring($val, 0, $length + 1)) return $i - 48)"/>
     <variable name="weightedSum"
                select="sum(for $i in (0 to $length - 1) return $digits[$i + 1] * (($i mod 6) + 2))"/>
     <value-of select="number($val) &gt; 0 and (11 - ($weightedSum mod 11)) mod 11 = number(substring($val, $length + 1, 1))"/>
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
                              title="Sjekk mot norske nasjonale regler"
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
         <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"
                                             prefix="ubl"/>
         <svrl:ns-prefix-in-attribute-values uri="utils" prefix="u"/>
         <svrl:active-pattern>
            <xsl:attribute name="document">
               <xsl:value-of select="document-uri(/)"/>
            </xsl:attribute>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M6"/>
      </svrl:schematron-output>
   </xsl:template>

   <!--SCHEMATRON PATTERNS-->
   <svrl:text xmlns:svrl="http://purl.oclc.org/dsdl/svrl">Sjekk mot norske nasjonale regler</svrl:text>

   <!--PATTERN -->
   <xsl:variable name="isZ01" select="//cbc:InvoiceTypeCode = 'Z01'"/>
   <xsl:variable name="isZ02" select="//cbc:InvoiceTypeCode = 'Z02'"/>
   <xsl:variable name="isB2C"
                 select="$isZ01 or //cac:AdditionalDocumentReference/cbc:DocumentType = 'elektroniskB2Cfaktura'"/>

	  <!--RULE -->
   <xsl:template match="//cbc:ProfileID" priority="1026" mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="//cbc:ProfileID"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test=". = 'urn:www.cenbii.eu:profile:bii04:ver2.0' or . = 'urn:www.cenbii.eu:profile:bii05:ver2.0' or . = 'urn:www.cenbii.eu:profile:biixy:ver2.0'"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test=". = 'urn:www.cenbii.eu:profile:bii04:ver2.0' or . = 'urn:www.cenbii.eu:profile:bii05:ver2.0' or . = 'urn:www.cenbii.eu:profile:biixy:ver2.0'">
               <xsl:attribute name="id">EHFPROFILE-T10-R001</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[EHFPROFILE-T10-R001]-An invoice transaction T10 must only be used in Profiles 4, 5 or xy.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="cbc:InvoiceTypeCode" priority="1025" mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="cbc:InvoiceTypeCode"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="index-of(tokenize('380 393 384 Z01 Z02', '\s'), .)"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="index-of(tokenize('380 393 384 Z01 Z02', '\s'), .)">
               <xsl:attribute name="id">NOGOV-T10-R042</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R042]-An Invoice MUST be coded with the InvoiceTypeCode code list UNCL D1001 BII2 subset</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="/ubl:Invoice/cac:AccountingSupplierParty/cac:Party"
                 priority="1024"
                 mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="/ubl:Invoice/cac:AccountingSupplierParty/cac:Party"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="$isZ02 or (cac:Contact/cbc:ID != '')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="$isZ02 or (cac:Contact/cbc:ID != '')">
               <xsl:attribute name="id">NOGOV-T10-R001</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R001]-A contact reference identifier SHOULD be provided for AccountingSupplierParty according to EHF.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="//cac:PaymentMeans" priority="1023" mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="//cac:PaymentMeans"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(cac:PayeeFinancialAccount/cbc:ID != '')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="(cac:PayeeFinancialAccount/cbc:ID != '')">
               <xsl:attribute name="id">NOGOV-T10-R011</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R011]-PayeeFinancialAccount MUST be provided according EHF.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(cbc:PaymentID != '')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="(cbc:PaymentID != '')">
               <xsl:attribute name="id">NOGOV-T10-R012</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R012]-Payment Identifier (KID number) SHOULD be used according to EHF.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="//cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:ID[attribute::schemeID = 'BBAN']"
                 priority="1022"
                 mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="//cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:ID[attribute::schemeID = 'BBAN']"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(string(.) castable as xs:integer)"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="(string(.) castable as xs:integer)">
               <xsl:attribute name="id">NOGOV-T10-R032</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R032]-Only numbers are allowed as bank account number if scheme is BBAN.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="//cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:ID[attribute::schemeID = 'IBAN']"
                 priority="1021"
                 mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="//cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:ID[attribute::schemeID = 'IBAN']"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(matches(., 'NO') = true()) and (substring(., 3) castable as xs:integer)"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="(matches(., 'NO') = true()) and (substring(., 3) castable as xs:integer)">
               <xsl:attribute name="id">NOGOV-T10-R033</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R033]-IBAN number is not for a norwegain bank account</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="//cac:OrderReference" priority="1020" mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="//cac:OrderReference"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(child::cbc:ID != '')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="(child::cbc:ID != '')">
               <xsl:attribute name="id">NOGOV-T10-R013</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R013]-An association to Order Reference SHOULD be provided according to EHF.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="//cac:Item" priority="1019" mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="//cac:Item"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="$isZ02 or (cac:SellersItemIdentification/cbc:ID != '')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="$isZ02 or (cac:SellersItemIdentification/cbc:ID != '')">
               <xsl:attribute name="id">NOGOV-T10-R002</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R002]-The sellers ID for the item SHOULD be provided according to EHF.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="//cac:InvoiceLine" priority="1018" mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="//cac:InvoiceLine"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(cbc:AccountingCost)"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="(cbc:AccountingCost)">
               <xsl:attribute name="id">NOGOV-T10-R003</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R003]-The buyer's accounting code applied to the Invoice Line SHOULD be provided according to EHF.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="$isZ02 or (cac:OrderLineReference/cbc:LineID != '')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="$isZ02 or (cac:OrderLineReference/cbc:LineID != '')">
               <xsl:attribute name="id">NOGOV-T10-R004</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R004]-An association to Order Line Reference SHOULD be provided according to EHF.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="//cac:InvoiceLine/cac:Item/cac:OriginCountry"
                 priority="1017"
                 mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="//cac:InvoiceLine/cac:Item/cac:OriginCountry"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(cbc:IdentificationCode != '')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="(cbc:IdentificationCode != '')">
               <xsl:attribute name="id">NOGOV-T10-R022</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R022]-Identification code MUST be specified when describing origin country.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="//cac:InvoiceLine/cac:Item/cac:ManufacturerParty"
                 priority="1016"
                 mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="//cac:InvoiceLine/cac:Item/cac:ManufacturerParty"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(cac:PartyName/cbc:Name != '')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="(cac:PartyName/cbc:Name != '')">
               <xsl:attribute name="id">NOGOV-T10-R024</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R024]-Name MUST be specified when describing a manufacturer party.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="//cac:InvoiceLine/cac:Item/cac:CommodityClassification"
                 priority="1015"
                 mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="//cac:InvoiceLine/cac:Item/cac:CommodityClassification"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(cbc:ItemClassificationCode != '')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="(cbc:ItemClassificationCode != '')">
               <xsl:attribute name="id">NOGOV-T10-R023</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R023]-Item classification code MUST be specified when describing commodity classification.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="/ubl:Invoice" priority="1014" mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="/ubl:Invoice"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="((cac:TaxTotal[cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:ID = 'VAT']/cbc:TaxAmount != 0) and (cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID) or (cac:TaxTotal[cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:ID = 'VAT']/cbc:TaxAmount = 0) or not((cac:TaxTotal[cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:ID = 'VAT'])))"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="((cac:TaxTotal[cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:ID = 'VAT']/cbc:TaxAmount != 0) and (cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID) or (cac:TaxTotal[cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:ID = 'VAT']/cbc:TaxAmount = 0) or not((cac:TaxTotal[cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:ID = 'VAT'])))">
               <xsl:attribute name="id">NOGOV-T10-R014</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R014]-If the VAT total amount in an invoice exists it MUST contain the suppliers VAT number.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="((cac:AllowanceCharge[cbc:ChargeIndicator = 'true']) and (cac:LegalMonetaryTotal/cbc:ChargeTotalAmount != '') or not(cac:AllowanceCharge[cbc:ChargeIndicator = 'true']))"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="((cac:AllowanceCharge[cbc:ChargeIndicator = 'true']) and (cac:LegalMonetaryTotal/cbc:ChargeTotalAmount != '') or not(cac:AllowanceCharge[cbc:ChargeIndicator = 'true']))">
               <xsl:attribute name="id">NOGOV-T10-R034</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R034]-If charge is present on document level, total charge must be stated.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="((cac:AllowanceCharge[cbc:ChargeIndicator = 'false']) and (cac:LegalMonetaryTotal/cbc:AllowanceTotalAmount != '') or not(cac:AllowanceCharge[cbc:ChargeIndicator = 'false']))"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="((cac:AllowanceCharge[cbc:ChargeIndicator = 'false']) and (cac:LegalMonetaryTotal/cbc:AllowanceTotalAmount != '') or not(cac:AllowanceCharge[cbc:ChargeIndicator = 'false']))">
               <xsl:attribute name="id">NOGOV-T10-R035</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R035]-If allowance is present on document level, total allowance must be stated.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="$isZ02 or (cac:ContractDocumentReference/cbc:ID != '')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="$isZ02 or (cac:ContractDocumentReference/cbc:ID != '')">
               <xsl:attribute name="id">NOGOV-T10-R005</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R005]-ContractDocumentReference SHOULD be provided according to EHF.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(cbc:InvoiceTypeCode != '')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="(cbc:InvoiceTypeCode != '')">
               <xsl:attribute name="id">NOGOV-T10-R016</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R016]-An EHF invoice MUST have an invoice type code.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(cac:PaymentMeans)"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="(cac:PaymentMeans)">
               <xsl:attribute name="id">NOGOV-T10-R019</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R019]-An invoice MUST have payment means information.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="//cac:PartyTaxScheme/cbc:CompanyID"
                 priority="1013"
                 mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="//cac:PartyTaxScheme/cbc:CompanyID"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(string-length(.) = 12) and (substring(., 1, 9) castable as xs:integer) and (substring(., 10, 12) = 'MVA') and xs:boolean(u:mod11(substring(., 1, 9)))"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="(string-length(.) = 12) and (substring(., 1, 9) castable as xs:integer) and (substring(., 10, 12) = 'MVA') and xs:boolean(u:mod11(substring(., 1, 9)))">
               <xsl:attribute name="id">NOGOV-T10-R030</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R030]-A VAT number MUST be valid Norwegian organization number (nine numbers) followed by the letters MVA.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="//cac:PartyLegalEntity/cbc:CompanyID"
                 priority="1012"
                 mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="//cac:PartyLegalEntity/cbc:CompanyID"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(string-length(.) = 9) and (string(.) castable as xs:integer) and xs:boolean(u:mod11(.))"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="(string-length(.) = 9) and (string(.) castable as xs:integer) and xs:boolean(u:mod11(.))">
               <xsl:attribute name="id">NOGOV-T10-R031</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R031]-A valid Norwegian organization number for seller, buyer and payee MUST be nine numbers..</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="/ubl:Invoice/cac:PaymentTerms" priority="1011" mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="/ubl:Invoice/cac:PaymentTerms"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(cbc:Note != '')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="(cbc:Note != '')">
               <xsl:attribute name="id">NOGOV-T10-R020</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R020]-Note MUST be specified when describing Payment terms.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="/ubl:Invoice/cac:TaxRepresentativeParty"
                 priority="1010"
                 mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="/ubl:Invoice/cac:TaxRepresentativeParty"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(cac:PartyName/cbc:Name != '')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="(cac:PartyName/cbc:Name != '')">
               <xsl:attribute name="id">NOGOV-T10-R017</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R017]-Name MUST be specified when describing a Tax Representative</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(cac:PartyTaxScheme/cbc:CompanyID != '')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="(cac:PartyTaxScheme/cbc:CompanyID != '')">
               <xsl:attribute name="id">NOGOV-T10-R018</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R018]-Company identifier MUST be specified when describing a Tax Representative</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="/ubl:Invoice/cac:TaxTotal" priority="1009" mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="/ubl:Invoice/cac:TaxTotal"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(cac:TaxSubtotal)"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="(cac:TaxSubtotal)">
               <xsl:attribute name="id">NOGOV-T10-R021</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R021]-An invoice MUST have Tax Subtotal specifications.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="string-length(substring-after(cbc:TaxAmount, '.')) &lt;= 2"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="string-length(substring-after(cbc:TaxAmount, '.')) &lt;= 2">
               <xsl:attribute name="id">NOGOV-T10-R038</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R038]-Total tax amount cannot have more than 2 decimals</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="count(distinct-values(cac:TaxSubtotal/cac:TaxCategory/cbc:ID/normalize-space(text()))) = count(cac:TaxSubtotal)"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="count(distinct-values(cac:TaxSubtotal/cac:TaxCategory/cbc:ID/normalize-space(text()))) = count(cac:TaxSubtotal)">
               <xsl:attribute name="id">NOGOV-T10-R041</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R041]-Multiple tax subtotals per tax category is not allowed.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="//*[contains(name(), 'Amount') and not(contains(name(), 'Transaction'))]"
                 priority="1008"
                 mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="//*[contains(name(), 'Amount') and not(contains(name(), 'Transaction'))]"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="not(attribute::currencyID) or (attribute::currencyID and attribute::currencyID = /ubl:Invoice/cbc:DocumentCurrencyCode)"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="not(attribute::currencyID) or (attribute::currencyID and attribute::currencyID = /ubl:Invoice/cbc:DocumentCurrencyCode)">
               <xsl:attribute name="id">NOGOV-T10-R025</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R025]-The attribute currencyID must have the same value as DocumentCurrencyCode, except the attribute for TransactionCurrencyTaxAmount.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="not(name(parent::node()) = 'cac:LegalMonetaryTotal') or string-length(substring-after(., '.')) &lt;= 2"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="not(name(parent::node()) = 'cac:LegalMonetaryTotal') or string-length(substring-after(., '.')) &lt;= 2">
               <xsl:attribute name="id">NOGOV-T10-R037</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R037]-Document level amounts cannot have more than 2 decimals</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="//*[contains(name(), 'Date')]" priority="1007" mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="//*[contains(name(), 'Date')]"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(string(.) castable as xs:date) and (string-length(.) = 10)"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="(string(.) castable as xs:date) and (string-length(.) = 10)">
               <xsl:attribute name="id">NOGOV-T10-R028</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R028]-A date must be formatted YYYY-MM-DD.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="*/@mimeCode" priority="1006" mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="*/@mimeCode"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="((. = 'application/pdf' or . = 'image/gif' or . = 'image/tiff' or . = 'image/jpeg' or . = 'image/png' or . = 'text/plain'))"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="((. = 'application/pdf' or . = 'image/gif' or . = 'image/tiff' or . = 'image/jpeg' or . = 'image/png' or . = 'text/plain'))">
               <xsl:attribute name="id">NOGOV-T10-R010</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R010]-Attachment is not a recommended MIMEType.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="//cac:Party/cbc:EndpointID" priority="1005" mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="//cac:Party/cbc:EndpointID"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="@schemeID = 'NO:ORGNR'"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="@schemeID = 'NO:ORGNR'">
               <xsl:attribute name="id">NOGOV-T10-R027</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R027]-An endpoint identifier scheme MUST have the value 'NO:ORGNR'.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(string(.) castable as xs:integer) and (string-length(.) = 9) and xs:boolean(u:mod11(.))"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="(string(.) castable as xs:integer) and (string-length(.) = 9) and xs:boolean(u:mod11(.))">
               <xsl:attribute name="id">NOGOV-T10-R026</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R026]-MUST be a valid Norwegian organization number. Only numerical value allowed</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="/ubl:Invoice/cac:AccountingCustomerParty/cac:Party"
                 priority="1004"
                 mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="/ubl:Invoice/cac:AccountingCustomerParty/cac:Party"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(cac:PartyIdentification/cbc:ID != '')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="(cac:PartyIdentification/cbc:ID != '')">
               <xsl:attribute name="id">NOGOV-T10-R006</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R006]-A customer number for AccountingCustomerParty SHOULD be provided according to EHF.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(cac:Contact/cbc:ID != '')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="(cac:Contact/cbc:ID != '')">
               <xsl:attribute name="id">NOGOV-T10-R007</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R007]-A contact reference identifier MUST be provided for AccountingCustomerParty according to EHF.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="$isB2C or (cac:PartyLegalEntity/cbc:CompanyID != '')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="$isB2C or (cac:PartyLegalEntity/cbc:CompanyID != '')">
               <xsl:attribute name="id">NOGOV-T10-R009</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R009]-PartyLegalEntity for AccountingCustomerParty MUST be provided according to EHF.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="$isB2C or (cac:PartyLegalEntity/cbc:RegistrationName != '')"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="$isB2C or (cac:PartyLegalEntity/cbc:RegistrationName != '')">
               <xsl:attribute name="id">NOGOV-T10-R015</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R015]-Registration name for AccountingCustomerParty MUST be provided according to EHF.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="//cac:PartyIdentification/cbc:ID[@schemeID = 'NO:ORGNR']"
                 priority="1003"
                 mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="//cac:PartyIdentification/cbc:ID[@schemeID = 'NO:ORGNR']"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="(string(.) castable as xs:integer) and (string-length(.) = 9) and xs:boolean(u:mod11(.))"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="(string(.) castable as xs:integer) and (string-length(.) = 9) and xs:boolean(u:mod11(.))">
               <xsl:attribute name="id">NOGOV-T10-R036</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R036]-When scheme is NO:ORGNR, a valid Norwegian organization number must be used. Only numerical value allowed</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="/ubl:Invoice/cac:TaxTotal/cac:TaxSubtotal"
                 priority="1002"
                 mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="/ubl:Invoice/cac:TaxTotal/cac:TaxSubtotal"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="string-length(substring-after(cbc:TaxableAmount, '.')) &lt;= 2"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="string-length(substring-after(cbc:TaxableAmount, '.')) &lt;= 2">
               <xsl:attribute name="id">NOGOV-T10-R039</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R039]-Tax subtotal amounts cannot have more than 2 decimals</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="string-length(substring-after(cbc:TaxAmount, '.')) &lt;= 2"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="string-length(substring-after(cbc:TaxAmount, '.')) &lt;= 2">
               <xsl:attribute name="id">NOGOV-T10-R039</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R039]-Tax subtotal amounts cannot have more than 2 decimals</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="/ubl:Invoice/cac:AllowanceCharge" priority="1001" mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="/ubl:Invoice/cac:AllowanceCharge"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="string-length(substring-after(cbc:Amount, '.')) &lt;= 2"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="string-length(substring-after(cbc:Amount, '.')) &lt;= 2">
               <xsl:attribute name="id">NOGOV-T10-R040</xsl:attribute>
               <xsl:attribute name="flag">fatal</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R040]-Allowance or charge amounts on document level cannot have more than 2 decimals</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>

	  <!--RULE -->
   <xsl:template match="cac:LegalMonetaryTotal" priority="1000" mode="M6">
      <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                       context="cac:LegalMonetaryTotal"/>

		    <!--ASSERT -->
      <xsl:choose>
         <xsl:when test="not(cbc:PayableRoundingAmount) or abs(xs:decimal(cbc:PayableRoundingAmount)) &lt;= max((xs:decimal(abs(cbc:PayableAmount) div 10), xs:decimal(1)))"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                test="not(cbc:PayableRoundingAmount) or abs(xs:decimal(cbc:PayableRoundingAmount)) &lt;= max((xs:decimal(abs(cbc:PayableAmount) div 10), xs:decimal(1)))">
               <xsl:attribute name="id">NOGOV-T10-R043</xsl:attribute>
               <xsl:attribute name="flag">warning</xsl:attribute>
               <xsl:attribute name="location">
                  <xsl:apply-templates select="." mode="schematron-select-full-path"/>
               </xsl:attribute>
               <svrl:text>[NOGOV-T10-R043]-Payable rounding amount should be no more than 10% of payable amount.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M6"/>
   <xsl:template match="@*|node()" priority="-2" mode="M6">
      <xsl:apply-templates select="@*|*" mode="M6"/>
   </xsl:template>
</xsl:stylesheet>
