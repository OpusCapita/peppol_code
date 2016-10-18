<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:cbc="urn:oasis:names:tc:ubl:CommonBasicComponents:1:0"
        xmlns:cac="urn:sfti:CommonAggregateComponents:1:0"
        xmlns:sfti="urn:sfti:documents:BasicInvoice:1:0"
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
    <xsl:output xmlns:svrl="http://purl.oclc.org/dsdl/svrl" method="xml"
                omit-xml-declaration="no"
                standalone="yes"
                indent="yes"/>

    <!--XSD TYPES FOR XSLT2-->


    <!--KEYS AND FUNCTIONS-->


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
                <xsl:variable name="p_1" select="1+    count(preceding-sibling::*[name()=name(current())])"/>
                <xsl:if test="$p_1&gt;1 or following-sibling::*[name()=name(current())]">[<xsl:value-of select="$p_1"/>]
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>*[local-name()='</xsl:text>
                <xsl:value-of select="local-name()"/>
                <xsl:text>']</xsl:text>
                <xsl:variable name="p_2"
                              select="1+   count(preceding-sibling::*[local-name()=local-name(current())])"/>
                <xsl:if test="$p_2&gt;1 or following-sibling::*[local-name()=local-name(current())]">[<xsl:value-of
                        select="$p_2"/>]
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="@*" mode="schematron-get-full-path">
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
        <xsl:value-of
                select="concat('.processing-instruction-', 1+count(preceding-sibling::processing-instruction()), '-')"/>
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
                                title="Schema for Svefaktura Business Rules; ; Ecru"
                                schemaVersion="iso">
            <xsl:comment>
                <xsl:value-of select="$archiveDirParameter"/>  
                <xsl:value-of select="$archiveNameParameter"/>  
                <xsl:value-of select="$fileNameParameter"/>  
                <xsl:value-of select="$fileDirParameter"/>
            </xsl:comment>
            <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:tc:ubl:CommonAggregateComponents:1:0"
                                                prefix="cac2"/>
            <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:tc:ubl:CommonBasicComponents:1:0" prefix="cbc"/>
            <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:tc:ubl:CoreComponentParameters:1:0" prefix="ccts"/>
            <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:tc:ubl:CoreComponentTypes:1:0" prefix="cct"/>
            <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:tc:ubl:SpecializedDatatypes:1:0" prefix="sdt"/>
            <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:tc:ubl:UnspecializedDatatypes:1:0" prefix="udt"/>
            <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:tc:ubl:codelist:AllowanceChargeReasonCode:1:0"
                                                prefix="rsn"/>
            <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:tc:ubl:codelist:ChannelCode:1:0" prefix="chn"/>
            <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:tc:ubl:codelist:ChipCode:1:0" prefix="chc"/>
            <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:tc:ubl:codelist:CountryIdentificationCode:1:0"
                                                prefix="cnt"/>
            <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:tc:ubl:codelist:CurrencyCode:1:0" prefix="cur"/>
            <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:tc:ubl:codelist:DocumentStatusCode:1:0"
                                                prefix="stat"/>
            <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:tc:ubl:codelist:LatitudeDirectionCode:1:0"
                                                prefix="lat"/>
            <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:tc:ubl:codelist:LineStatusCode:1:0"
                                                prefix="lstat"/>
            <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:tc:ubl:codelist:LongitudeDirectionCode:1:0"
                                                prefix="lon"/>
            <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:tc:ubl:codelist:PaymentMeansCode:1:0"
                                                prefix="pty"/>
            <svrl:ns-prefix-in-attribute-values uri="urn:oasis:names:tc:ubl:codelist:SubstitutionStatusCode:1:0"
                                                prefix="sst"/>
            <svrl:ns-prefix-in-attribute-values uri="urn:sfti:CommonAggregateComponents:1:0" prefix="cac"/>
            <svrl:ns-prefix-in-attribute-values uri="urn:sfti:documents:BasicInvoice:1:0" prefix="sfti"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M20"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M21"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M22"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M23"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M24"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M25"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M26"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M27"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M28"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M29"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M30"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M31"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M32"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M33"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M34"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M35"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M36"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M37"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M38"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M39"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M40"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M41"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M42"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M43"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M44"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M45"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M46"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M47"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M48"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M49"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M50"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M51"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M52"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M53"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M54"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M55"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M56"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M57"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M58"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M59"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M60"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M61"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M62"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M63"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M64"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M65"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M66"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M67"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M68"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M69"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M70"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M71"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M72"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M73"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M74"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M75"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M76"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M77"/>
            <svrl:active-pattern>
                <xsl:attribute name="document">
                    <xsl:value-of select="document-uri(/)"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </svrl:active-pattern>
            <xsl:apply-templates select="/" mode="M78"/>
        </svrl:schematron-output>
    </xsl:template>

    <!--SCHEMATRON PATTERNS-->
    <svrl:text xmlns:svrl="http://purl.oclc.org/dsdl/svrl">Schema for Svefaktura Business Rules; ; Ecru</svrl:text>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="//cac:TaxCategory/cac:ID" priority="1000" mode="M20">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="//cac:TaxCategory/cac:ID"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="normalize-space(.) = ."/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="normalize-space(.) = .">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        cac:TaxCategory/cac:ID ska anges i normaliserad form (utan mellanslag eller radbrytningar)
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M20"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M20"/>
    <xsl:template match="@*|node()" priority="-2" mode="M20">
        <xsl:apply-templates select="*" mode="M20"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="//cac:TaxScheme/cac:ID" priority="1000" mode="M21">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="//cac:TaxScheme/cac:ID"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="normalize-space(.) = ."/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="normalize-space(.) = .">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        cac:TaxScheme/cac:ID ska anges i normaliserad form (utan mellanslag eller radbrytningar)
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M21"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M21"/>
    <xsl:template match="@*|node()" priority="-2" mode="M21">
        <xsl:apply-templates select="*" mode="M21"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="//cbc:PostalZone" priority="1000" mode="M22">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="//cbc:PostalZone"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="matches(.,'^\w*$')"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="matches(.,'^\w*$')">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Postnummer skall skickas som en teckensträng utan redigeringstecken (som blanka bindesstreck
                        eller annat
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M22"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M22"/>
    <xsl:template match="@*|node()" priority="-2" mode="M22">
        <xsl:apply-templates select="*" mode="M22"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="//cbc:Telefax|//cbc:Telephone" priority="1000" mode="M23">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="//cbc:Telefax|//cbc:Telephone"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="matches(.,'^\+?\d*$')"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="matches(.,'^\+?\d*$')">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Telefon och faxnummer skall skickas som en teckensträng utan redigeringstecken (som blanka
                        bindesstreck eller annat)
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M23"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M23"/>
    <xsl:template match="@*|node()" priority="-2" mode="M23">
        <xsl:apply-templates select="*" mode="M23"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice" priority="1000" mode="M24">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl" context="/sfti:Invoice"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="sfti:LineItemCountNumeric"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="sfti:LineItemCountNumeric">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        LineItemCountNumeric måste finnas
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="count(cac:TaxTotal) &lt; 2"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="count(cac:TaxTotal) &lt; 2">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Det får bara förekomma en repetition av TaxTotal
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="not(//attribute::xsi:schemaLocation)"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="not(//attribute::xsi:schemaLocation)">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        xsi:schemaLocation ska inte anges i xml-filer som utväxlas.
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M24"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M24"/>
    <xsl:template match="@*|node()" priority="-2" mode="M24">
        <xsl:apply-templates select="*" mode="M24"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:BuyerParty/cac:Party/cac:Address" priority="1000"
                  mode="M25">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:BuyerParty/cac:Party/cac:Address"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="not(cac:AddressLine/cbc:Line and cbc:*)"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="not(cac:AddressLine/cbc:Line and cbc:*)">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Adress är angiven som i både strukturerad form och ostrukturerad (AddressLine)
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="not(exists(cac:AddressLine)) or exists(cac:AddressLine[count(cbc:Line) &gt; 1])"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="not(exists(cac:AddressLine)) or exists(cac:AddressLine[count(cbc:Line) &gt; 1])">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Ingen komplett adress är angiven
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M25"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M25"/>
    <xsl:template match="@*|node()" priority="-2" mode="M25">
        <xsl:apply-templates select="*" mode="M25"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:BuyerParty/cac:Party/cac:PartyTaxScheme/cac:CompanyID"
                  priority="1000"
                  mode="M26">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:BuyerParty/cac:Party/cac:PartyTaxScheme/cac:CompanyID"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="matches(.,'^\w*$')"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="matches(.,'^\w*$')">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Organisations- och momsregistreringsnummer skall skickas som en teckensträng utan
                        redigeringstecken (som blanka bindesstreck eller annat)
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M26"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M26"/>
    <xsl:template match="@*|node()" priority="-2" mode="M26">
        <xsl:apply-templates select="*" mode="M26"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:BuyerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:ID"
                  priority="1000"
                  mode="M27">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:BuyerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:ID"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="normalize-space(.) = 'VAT' or normalize-space(.) = 'SWT'"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="normalize-space(.) = 'VAT' or normalize-space(.) = 'SWT'">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Ogiltig TaxScheme/ID, skall vara VAT | SWT
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M27"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M27"/>
    <xsl:template match="@*|node()" priority="-2" mode="M27">
        <xsl:apply-templates select="*" mode="M27"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:BuyerParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName"
                  priority="1000"
                  mode="M28">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:BuyerParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test=". != ../../cac:PartyName/cbc:Name"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test=". != ../../cac:PartyName/cbc:Name">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        PartyTaxScheme/ RegistrationName anges bara om det avviker från PartyName/Name
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M28"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M28"/>
    <xsl:template match="@*|node()" priority="-2" mode="M28">
        <xsl:apply-templates select="*" mode="M28"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:Delivery/cbc:ActualDeliveryDateTime" priority="1000"
                  mode="M29">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:Delivery/cbc:ActualDeliveryDateTime"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="xs:date(../../cbc:IssueDate) &gt;= xs:date(substring-before(.,'T'))"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="xs:date(../../cbc:IssueDate) &gt;= xs:date(substring-before(.,'T'))">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        /sfti:Invoice/cac:Delivery/cbc:ActualDeliveryDateTime får inte infalla senare än IssueDate
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M29"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M29"/>
    <xsl:template match="@*|node()" priority="-2" mode="M29">
        <xsl:apply-templates select="*" mode="M29"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:ExchangeRate/cac:SourceCurrencyCode" priority="1000"
                  mode="M30">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:ExchangeRate/cac:SourceCurrencyCode"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="/sfti:Invoice/sfti:InvoiceCurrencyCode"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="/sfti:Invoice/sfti:InvoiceCurrencyCode">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        InvoiceCurrencyCode måste vara angiven om ExchangeRate används
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="/sfti:Invoice/sfti:TaxCurrencyCode"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="/sfti:Invoice/sfti:TaxCurrencyCode">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        TaxCurrencyCode måste vara angiven om ExchangeRate används
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="/sfti:Invoice/cac:ExchangeRate/cbc:CalculationRate"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="/sfti:Invoice/cac:ExchangeRate/cbc:CalculationRate">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        CalculationRate måste vara angiven om ExchangeRate finns
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="number(/sfti:Invoice/cac:ExchangeRate/cbc:CalculationRate) &gt; 0"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="number(/sfti:Invoice/cac:ExchangeRate/cbc:CalculationRate) &gt; 0">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        CalculationRate felaktig. Måste vara &amp;gt; 0
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M30"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M30"/>
    <xsl:template match="@*|node()" priority="-2" mode="M30">
        <xsl:apply-templates select="*" mode="M30"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:InvoiceLine/cac:AllowanceCharge" priority="1000"
                  mode="M31">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:InvoiceLine/cac:AllowanceCharge"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="cbc:Amount"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="cbc:Amount">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        cbc:Amount måste anges på fakturaraden då rabatt eller avgift används
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M31"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M31"/>
    <xsl:template match="@*|node()" priority="-2" mode="M31">
        <xsl:apply-templates select="*" mode="M31"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:InvoiceLine/cac:AllowanceCharge/cbc:Amount"
                  priority="1000"
                  mode="M32">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:InvoiceLine/cac:AllowanceCharge/cbc:Amount"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="number(.) &gt;= 0"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="number(.) &gt;= 0">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        InvoiceLine/AllowanceCharge/Amount: Belopp kan inte vara negativt, växla ChargeIndicator
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M32"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M32"/>
    <xsl:template match="@*|node()" priority="-2" mode="M32">
        <xsl:apply-templates select="*" mode="M32"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:InvoiceLine/cac:AllowanceCharge/cbc:ChargeIndicator"
                  priority="1000"
                  mode="M33">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:InvoiceLine/cac:AllowanceCharge/cbc:ChargeIndicator"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test=". ='true' or .='false'"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test=". ='true' or .='false'">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        ChargeIndicator: använd värdena true | false
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M33"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M33"/>
    <xsl:template match="@*|node()" priority="-2" mode="M33">
        <xsl:apply-templates select="*" mode="M33"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:InvoiceLine/cac:Item" priority="1000" mode="M34">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:InvoiceLine/cac:Item"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="not(cac:TaxCategory/cac:TaxScheme/cac:ID) or  normalize-space(cac:TaxCategory/cac:TaxScheme/cac:ID) = 'VAT'"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="not(cac:TaxCategory/cac:TaxScheme/cac:ID) or normalize-space(cac:TaxCategory/cac:TaxScheme/cac:ID) = 'VAT'">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Item/TaxCategory/TaxScheme/ID felaktig, kan bara vara "VAT"
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M34"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M34"/>
    <xsl:template match="@*|node()" priority="-2" mode="M34">
        <xsl:apply-templates select="*" mode="M34"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:InvoiceLine/cac:Item/cac:BasePrice/cbc:BaseQuantity"
                  priority="1000"
                  mode="M35">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:InvoiceLine/cac:Item/cac:BasePrice/cbc:BaseQuantity"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="(@quantityUnitCode = ../../../cbc:InvoicedQuantity/@quantityUnitCode) or not(../../../cbc:InvoicedQuantity/@quantityUnitCode)"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="(@quantityUnitCode = ../../../cbc:InvoicedQuantity/@quantityUnitCode) or not(../../../cbc:InvoicedQuantity/@quantityUnitCode)">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        InvoiceLine/InvoicedQuantity uttrycks i annan måttenhet än Item/BasePrice/BaseQuantity för
                        InvoiceLine/ID
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M35"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M35"/>
    <xsl:template match="@*|node()" priority="-2" mode="M35">
        <xsl:apply-templates select="*" mode="M35"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:InvoiceLine/cac:Item/cac:TaxCategory" priority="1000"
                  mode="M36">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:InvoiceLine/cac:Item/cac:TaxCategory"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="(upper-case(normalize-space(cac:ID)) = 'E') or (upper-case(normalize-space(cac:ID)) = 'S')"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="(upper-case(normalize-space(cac:ID)) = 'E') or (upper-case(normalize-space(cac:ID)) = 'S')">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        TaxTotal/TaxSubtotal/TaxCategory/ID felaktig, skall vara S, eller vid skatteundantag E
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M36"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M36"/>
    <xsl:template match="@*|node()" priority="-2" mode="M36">
        <xsl:apply-templates select="*" mode="M36"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:Percent"
                  priority="1000"
                  mode="M37">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:Percent"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="(normalize-space(../cac:ID) = 'E' and xs:decimal(.) = 0) or (normalize-space(../cac:ID)='S' and xs:decimal(.) != 0) "/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="(normalize-space(../cac:ID) = 'E' and xs:decimal(.) = 0) or (normalize-space(../cac:ID)='S' and xs:decimal(.) != 0)">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Percent får endast vara = 0 vid skatteundantag
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M37"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M37"/>
    <xsl:template match="@*|node()" priority="-2" mode="M37">
        <xsl:apply-templates select="*" mode="M37"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:InvoiceLine/cac:Item/cac:TaxCategory[cac:ID = 'S']"
                  priority="1000"
                  mode="M38">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:InvoiceLine/cac:Item/cac:TaxCategory[cac:ID = 'S']"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="(number(cbc:Percent) = 6) or (number(cbc:Percent) = 12) or (number(cbc:Percent) = 25)"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="(number(cbc:Percent) = 6) or (number(cbc:Percent) = 12) or (number(cbc:Percent) = 25)">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        InvoiceLine/Item/TaxCategory/Percent: kontrollera skattesatsen, för Sverige gäller 6, 12 och
                        25%, resp vid ID=E skattesatsen 0%. Andra skattesatser kan gälla i utländska fakturor.
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M38"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M38"/>
    <xsl:template match="@*|node()" priority="-2" mode="M38">
        <xsl:apply-templates select="*" mode="M38"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template
            match="/sfti:Invoice/cac:InvoiceLine[cac:Item/cac:BasePrice][cac:AllowanceCharge/cbc:ChargeIndicator='false'][cac:AllowanceCharge/cbc:Amount][cac:Item/cac:BasePrice/cbc:BaseQuantity]"
            priority="1000"
            mode="M39">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:InvoiceLine[cac:Item/cac:BasePrice][cac:AllowanceCharge/cbc:ChargeIndicator='false'][cac:AllowanceCharge/cbc:Amount][cac:Item/cac:BasePrice/cbc:BaseQuantity]"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="abs((number(cac:Item/cac:BasePrice/cbc:PriceAmount) * number(cbc:InvoicedQuantity) div number(cac:Item/cac:BasePrice/cbc:BaseQuantity) -  number(cac:AllowanceCharge/cbc:Amount) - number(cbc:LineExtensionAmount))) &lt; 0.5"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="abs((number(cac:Item/cac:BasePrice/cbc:PriceAmount) * number(cbc:InvoicedQuantity) div number(cac:Item/cac:BasePrice/cbc:BaseQuantity) - number(cac:AllowanceCharge/cbc:Amount) - number(cbc:LineExtensionAmount))) &lt; 0.5">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        InvoiceLine/LineExtensionAmount Beräkning av radbelopp är felaktigt
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M39"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M39"/>
    <xsl:template match="@*|node()" priority="-2" mode="M39">
        <xsl:apply-templates select="*" mode="M39"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template
            match="/sfti:Invoice/cac:InvoiceLine[cac:Item/cac:BasePrice][cac:AllowanceCharge/cbc:ChargeIndicator='false'][cac:AllowanceCharge/cbc:Amount][not(cac:Item/cac:BasePrice/cbc:BaseQuantity)]"
            priority="1000"
            mode="M40">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:InvoiceLine[cac:Item/cac:BasePrice][cac:AllowanceCharge/cbc:ChargeIndicator='false'][cac:AllowanceCharge/cbc:Amount][not(cac:Item/cac:BasePrice/cbc:BaseQuantity)]"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="abs((number(cac:Item/cac:BasePrice/cbc:PriceAmount) * number(cbc:InvoicedQuantity)  -  number(cac:AllowanceCharge/cbc:Amount) - number(cbc:LineExtensionAmount))) &lt; 0.5"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="abs((number(cac:Item/cac:BasePrice/cbc:PriceAmount) * number(cbc:InvoicedQuantity) - number(cac:AllowanceCharge/cbc:Amount) - number(cbc:LineExtensionAmount))) &lt; 0.5">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        InvoiceLine/LineExtensionAmount Beräkning av radbelopp är felaktigt
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M40"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M40"/>
    <xsl:template match="@*|node()" priority="-2" mode="M40">
        <xsl:apply-templates select="*" mode="M40"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template
            match="/sfti:Invoice/cac:InvoiceLine[cac:Item/cac:BasePrice][cac:AllowanceCharge/cbc:ChargeIndicator='true'][cac:AllowanceCharge/cbc:Amount][cac:Item/cac:BasePrice/cbc:BaseQuantity]"
            priority="1000"
            mode="M41">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:InvoiceLine[cac:Item/cac:BasePrice][cac:AllowanceCharge/cbc:ChargeIndicator='true'][cac:AllowanceCharge/cbc:Amount][cac:Item/cac:BasePrice/cbc:BaseQuantity]"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="abs((number(cac:Item/cac:BasePrice/cbc:PriceAmount) * number(cbc:InvoicedQuantity) div number(cac:Item/cac:BasePrice/cbc:BaseQuantity) +  number(cac:AllowanceCharge/cbc:Amount) - number(cbc:LineExtensionAmount))) &lt; 0.5"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="abs((number(cac:Item/cac:BasePrice/cbc:PriceAmount) * number(cbc:InvoicedQuantity) div number(cac:Item/cac:BasePrice/cbc:BaseQuantity) + number(cac:AllowanceCharge/cbc:Amount) - number(cbc:LineExtensionAmount))) &lt; 0.5">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        InvoiceLine/LineExtensionAmount Beräkning av radbelopp är felaktigt
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M41"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M41"/>
    <xsl:template match="@*|node()" priority="-2" mode="M41">
        <xsl:apply-templates select="*" mode="M41"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template
            match="/sfti:Invoice/cac:InvoiceLine[cac:Item/cac:BasePrice][cac:AllowanceCharge/cbc:ChargeIndicator='true'][cac:AllowanceCharge/cbc:Amount][not(cac:Item/cac:BasePrice/cbc:BaseQuantity)]"
            priority="1000"
            mode="M42">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:InvoiceLine[cac:Item/cac:BasePrice][cac:AllowanceCharge/cbc:ChargeIndicator='true'][cac:AllowanceCharge/cbc:Amount][not(cac:Item/cac:BasePrice/cbc:BaseQuantity)]"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="abs((number(cac:Item/cac:BasePrice/cbc:PriceAmount) * number(cbc:InvoicedQuantity)  +  number(cac:AllowanceCharge/cbc:Amount) - number(cbc:LineExtensionAmount))) &lt; 0.5"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="abs((number(cac:Item/cac:BasePrice/cbc:PriceAmount) * number(cbc:InvoicedQuantity) + number(cac:AllowanceCharge/cbc:Amount) - number(cbc:LineExtensionAmount))) &lt; 0.5">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        InvoiceLine/LineExtensionAmount Beräkning av radbelopp är felaktigt
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M42"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M42"/>
    <xsl:template match="@*|node()" priority="-2" mode="M42">
        <xsl:apply-templates select="*" mode="M42"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template
            match="/sfti:Invoice/cac:InvoiceLine[cac:Item/cac:BasePrice][cac:Item/cac:BasePrice/cbc:BaseQuantity][not(cac:AllowanceCharge/cbc:Amount)]"
            priority="1000"
            mode="M43">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:InvoiceLine[cac:Item/cac:BasePrice][cac:Item/cac:BasePrice/cbc:BaseQuantity][not(cac:AllowanceCharge/cbc:Amount)]"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="abs((number(cac:Item/cac:BasePrice/cbc:PriceAmount) * number(cbc:InvoicedQuantity) div number(cac:Item/cac:BasePrice/cbc:BaseQuantity) - number(cbc:LineExtensionAmount))) &lt; 0.5"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="abs((number(cac:Item/cac:BasePrice/cbc:PriceAmount) * number(cbc:InvoicedQuantity) div number(cac:Item/cac:BasePrice/cbc:BaseQuantity) - number(cbc:LineExtensionAmount))) &lt; 0.5">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        InvoiceLine/LineExtensionAmount Beräkning av radbelopp är felaktigt
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M43"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M43"/>
    <xsl:template match="@*|node()" priority="-2" mode="M43">
        <xsl:apply-templates select="*" mode="M43"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template
            match="/sfti:Invoice/cac:InvoiceLine[cac:Item/cac:BasePrice][not(cac:Item/cac:BasePrice/cbc:BaseQuantity)][not(cac:AllowanceCharge/cbc:Amount)]"
            priority="1000"
            mode="M44">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:InvoiceLine[cac:Item/cac:BasePrice][not(cac:Item/cac:BasePrice/cbc:BaseQuantity)][not(cac:AllowanceCharge/cbc:Amount)]"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="abs((number(cac:Item/cac:BasePrice/cbc:PriceAmount) * number(cbc:InvoicedQuantity) - number(cbc:LineExtensionAmount))) &lt; 0.5"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="abs((number(cac:Item/cac:BasePrice/cbc:PriceAmount) * number(cbc:InvoicedQuantity) - number(cbc:LineExtensionAmount))) &lt; 0.5">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        InvoiceLine/LineExtensionAmount Beräkning av radbelopp är felaktigt
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M44"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M44"/>
    <xsl:template match="@*|node()" priority="-2" mode="M44">
        <xsl:apply-templates select="*" mode="M44"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:LegalTotal/cbc:LineExtensionTotalAmount"
                  priority="1000"
                  mode="M45">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:LegalTotal/cbc:LineExtensionTotalAmount"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="abs(xs:decimal(.) - sum(../../cac:InvoiceLine/cbc:LineExtensionAmount)) &lt;= 0.5 "/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="abs(xs:decimal(.) - sum(../../cac:InvoiceLine/cbc:LineExtensionAmount)) &lt;= 0.5">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        cbc:LineExtensionTotalAmount ska vara lika med summan av fakturaradernas cbc:LineExtensionAmount
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M45"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M45"/>
    <xsl:template match="@*|node()" priority="-2" mode="M45">
        <xsl:apply-templates select="*" mode="M45"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:LegalTotal/cbc:TaxExclusiveTotalAmount"
                  priority="1000"
                  mode="M46">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:LegalTotal/cbc:TaxExclusiveTotalAmount"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="abs(xs:decimal(.) - sum(../../cac:InvoiceLine/cbc:LineExtensionAmount) - sum(../../sfti:AllowanceCharge[cbc:ChargeIndicator='true']/cbc:Amount) + sum(../../sfti:AllowanceCharge[cbc:ChargeIndicator='false']/cbc:Amount))  &lt;= 0.5"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="abs(xs:decimal(.) - sum(../../cac:InvoiceLine/cbc:LineExtensionAmount) - sum(../../sfti:AllowanceCharge[cbc:ChargeIndicator='true']/cbc:Amount) + sum(../../sfti:AllowanceCharge[cbc:ChargeIndicator='false']/cbc:Amount)) &lt;= 0.5">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        cbc:TaxExclusiveTotalAmount ska vara lika med summan av fakturaradernas cbc:LineExtensionAmount
                        +/- AllowanceCharge/cbc:Amount
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M46"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M46"/>
    <xsl:template match="@*|node()" priority="-2" mode="M46">
        <xsl:apply-templates select="*" mode="M46"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:LegalTotal/cbc:TaxInclusiveTotalAmount"
                  priority="1000"
                  mode="M47">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:LegalTotal/cbc:TaxInclusiveTotalAmount"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="abs(xs:decimal(.) - sum(../../cac:InvoiceLine/cbc:LineExtensionAmount) - sum(../../sfti:AllowanceCharge[cbc:ChargeIndicator='true']/cbc:Amount) + sum(../../sfti:AllowanceCharge[cbc:ChargeIndicator='false']/cbc:Amount) - xs:decimal(../../cac:TaxTotal/cbc:TotalTaxAmount) - sum(cac:RoundOffAmount)) &lt;= 0.5"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="abs(xs:decimal(.) - sum(../../cac:InvoiceLine/cbc:LineExtensionAmount) - sum(../../sfti:AllowanceCharge[cbc:ChargeIndicator='true']/cbc:Amount) + sum(../../sfti:AllowanceCharge[cbc:ChargeIndicator='false']/cbc:Amount) - xs:decimal(../../cac:TaxTotal/cbc:TotalTaxAmount) - sum(cac:RoundOffAmount)) &lt;= 0.5">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        cbc:TaxInclusiveTotalAmount ska vara lika med summan av fakturaradernas cbc:LineExtensionAmount
                        +/- AllowanceCharge/cbc:Amount + cac:TaxTotal/cbc:TotalTaxAmount + cac:RoundOffAmount
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M47"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M47"/>
    <xsl:template match="@*|node()" priority="-2" mode="M47">
        <xsl:apply-templates select="*" mode="M47"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template
            match="/sfti:Invoice/cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cac:ID"
            priority="1000"
            mode="M48">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cac:ID"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="../../../cac:ID or .='SNDOSESS'  "/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="../../../cac:ID or .='SNDOSESS'">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Kontonummer måste anges då BIC anges
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M48"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M48"/>
    <xsl:template match="@*|node()" priority="-2" mode="M48">
        <xsl:apply-templates select="*" mode="M48"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:PaymentMeans/cac:PayeeFinancialAccount/cac:ID"
                  priority="1000"
                  mode="M49">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:PaymentMeans/cac:PayeeFinancialAccount/cac:ID"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="matches(.,'^\w*$')"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="matches(.,'^\w*$')">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Pg, bg och bankkontonummer skall skickas som en teckensträng utan redigeringstecken (som blanka
                        bindesstreck eller annat)
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M49"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M49"/>
    <xsl:template match="@*|node()" priority="-2" mode="M49">
        <xsl:apply-templates select="*" mode="M49"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:PaymentMeans/cac:PayeePartyName/cbc:Name"
                  priority="1000"
                  mode="M50">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:PaymentMeans/cac:PayeePartyName/cbc:Name"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test=". != ../../../cac:SellerParty/cac:Party/cac:PartyName/cbc:Name"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test=". != ../../../cac:SellerParty/cac:Party/cac:PartyName/cbc:Name">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        PaymentMeans/PayeePartyName/Name: Avsett för överlåtelse av faktura, men här är namnet samma som
                        säljarens
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M50"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M50"/>
    <xsl:template match="@*|node()" priority="-2" mode="M50">
        <xsl:apply-templates select="*" mode="M50"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:PaymentMeans/cac:PaymentMeansTypeCode" priority="1000"
                  mode="M51">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:PaymentMeans/cac:PaymentMeansTypeCode"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test=". = '1'"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test=". = '1'">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        PaymentMeansTypeCode ogiltig
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M51"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M51"/>
    <xsl:template match="@*|node()" priority="-2" mode="M51">
        <xsl:apply-templates select="*" mode="M51"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:PaymentMeans/cbc:DuePaymentDate" priority="1000"
                  mode="M52">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:PaymentMeans/cbc:DuePaymentDate"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="xs:date(.) &gt;= xs:date(/sfti:Invoice/cbc:IssueDate)"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="xs:date(.) &gt;= xs:date(/sfti:Invoice/cbc:IssueDate)">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        /sfti:Invoice/cac:PaymentMeans/cbc:DuePaymentDate infaller tidigare än IssueDate
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M52"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M52"/>
    <xsl:template match="@*|node()" priority="-2" mode="M52">
        <xsl:apply-templates select="*" mode="M52"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:PaymentTerms/cbc:PenaltySurchargePercent"
                  priority="1000"
                  mode="M53">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:PaymentTerms/cbc:PenaltySurchargePercent"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="number(/sfti:Invoice/cac:PaymentTerms/cbc:PenaltySurchargePercent) &gt;= 0"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="number(/sfti:Invoice/cac:PaymentTerms/cbc:PenaltySurchargePercent) &gt;= 0">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        /sfti:Invoice/cac:PaymentTerms/cbc:PenaltySurchargePercent måste vara större eller lika med 0
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M53"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M53"/>
    <xsl:template match="@*|node()" priority="-2" mode="M53">
        <xsl:apply-templates select="*" mode="M53"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:SellerParty/cac:Party" priority="1000" mode="M54">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:SellerParty/cac:Party"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="cac:PartyTaxScheme"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="cac:PartyTaxScheme">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        /Invoice/SellerParty/Party/PartyTaxScheme: Både organisationsnr och momsregistreringsnr saknas,
                        detta kräver att en alternativ identifiering avtalats
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="cac:PartyTaxScheme/cac:TaxScheme/cac:ID[normalize-space(.) = 'SWT']"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="cac:PartyTaxScheme/cac:TaxScheme/cac:ID[normalize-space(.) = 'SWT']">
                    <xsl:attribute name="flag">information</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Säljarens organisationsnummmer (motsv) enligt officiell registrering skall normalt alltid finnas
                        i en PartyTaxScheme/CompanyID med TaxScheme/ID=SWT
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M54"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M54"/>
    <xsl:template match="@*|node()" priority="-2" mode="M54">
        <xsl:apply-templates select="*" mode="M54"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:SellerParty/cac:Party/cac:Address" priority="1000"
                  mode="M55">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:SellerParty/cac:Party/cac:Address"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="not(cac:AddressLine/cbc:Line and cbc:*)"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="not(cac:AddressLine/cbc:Line and cbc:*)">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Adress är angiven som i både strukturerad form och ostrukturerad (AddressLine)
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="not(exists(cac:AddressLine)) or exists(cac:AddressLine[count(cbc:Line) &gt; 1])"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="not(exists(cac:AddressLine)) or exists(cac:AddressLine[count(cbc:Line) &gt; 1])">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Ingen komplett adress är angiven
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M55"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M55"/>
    <xsl:template match="@*|node()" priority="-2" mode="M55">
        <xsl:apply-templates select="*" mode="M55"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:SellerParty/cac:Party/cac:PartyTaxScheme/cac:CompanyID"
                  priority="1000"
                  mode="M56">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:SellerParty/cac:Party/cac:PartyTaxScheme/cac:CompanyID"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="matches(.,'^\w*$')"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="matches(.,'^\w*$')">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Organisations- och momsregistreringsnummer skall skickas som en teckensträng utan
                        redigeringstecken (som blanka bindesstreck eller annat)
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M56"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M56"/>
    <xsl:template match="@*|node()" priority="-2" mode="M56">
        <xsl:apply-templates select="*" mode="M56"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:SellerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:ID"
                  priority="1000"
                  mode="M57">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:SellerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:ID"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="normalize-space(.) = 'VAT' or normalize-space(.) = 'SWT'"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="normalize-space(.) = 'VAT' or normalize-space(.) = 'SWT'">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Ogiltig TaxScheme/ID, skall vara VAT | SWT
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M57"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M57"/>
    <xsl:template match="@*|node()" priority="-2" mode="M57">
        <xsl:apply-templates select="*" mode="M57"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:SellerParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName"
                  priority="1000"
                  mode="M58">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:SellerParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test=". != ../../cac:PartyName/cbc:Name"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test=". != ../../cac:PartyName/cbc:Name">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        PartyTaxScheme/ RegistrationName anges bara om det avviker från PartyName/Name
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M58"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M58"/>
    <xsl:template match="@*|node()" priority="-2" mode="M58">
        <xsl:apply-templates select="*" mode="M58"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:TaxTotal/cac:TaxSubTotal" priority="1000" mode="M59">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:TaxTotal/cac:TaxSubTotal"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="(cac:TaxCurrencyTaxAmount and ../../sfti:TaxCurrencyCode)  or (not(cac:TaxCurrencyTaxAmount) and not(../../sfti:TaxCurrencyCode))"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="(cac:TaxCurrencyTaxAmount and ../../sfti:TaxCurrencyCode) or (not(cac:TaxCurrencyTaxAmount) and not(../../sfti:TaxCurrencyCode))">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        TaxCurrencyCode kräver att omräkning av moms redovisas i TaxSubTotal/TaxCurrencyTaxAmount
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M59"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M59"/>
    <xsl:template match="@*|node()" priority="-2" mode="M59">
        <xsl:apply-templates select="*" mode="M59"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:TaxTotal/cac:TaxSubTotal/cac:TaxCategory[cac:ID = 'S']"
                  priority="1000"
                  mode="M60">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:TaxTotal/cac:TaxSubTotal/cac:TaxCategory[cac:ID = 'S']"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="(number(cbc:Percent) = 6) or (number(cbc:Percent) = 12) or (number(cbc:Percent) = 25)"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="(number(cbc:Percent) = 6) or (number(cbc:Percent) = 12) or (number(cbc:Percent) = 25)">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        TaxTotal/TaxSubtotal/TaxCategory/Percent: kontrollera skattesatsen, för Sverige gäller 6, 12 och
                        25%, resp vid ID=E skattesatsen 0%. Andra skattesatser kan gälla i utländska fakturor.
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M60"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M60"/>
    <xsl:template match="@*|node()" priority="-2" mode="M60">
        <xsl:apply-templates select="*" mode="M60"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/cac:TaxTotal/cbc:TotalTaxAmount" priority="1000"
                  mode="M61">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/cac:TaxTotal/cbc:TotalTaxAmount"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="abs(xs:decimal(.) - sum(../cac:TaxSubTotal/cbc:TaxAmount)) &lt;= 0.5"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="abs(xs:decimal(.) - sum(../cac:TaxSubTotal/cbc:TaxAmount)) &lt;= 0.5">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        cbc:TotalTaxAmount ska vara summan av cac:TaxSubTotal/cbc:TaxAmount
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M61"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M61"/>
    <xsl:template match="@*|node()" priority="-2" mode="M61">
        <xsl:apply-templates select="*" mode="M61"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/sfti:AdditionalDocumentReference/cac:ID" priority="1000"
                  mode="M62">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/sfti:AdditionalDocumentReference/cac:ID"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="@identificationSchemeAgencyName='SFTI' or not(@identificationSchemeAgencyName)"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="@identificationSchemeAgencyName='SFTI' or not(@identificationSchemeAgencyName)">
                    <xsl:attribute name="flag">information</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        AdditionalDocumentReference: Referensens attribut/kvalificerare (identificationSchemeAgencyName)
                        måste avtalas bilateralt om inte värdet SFTI används
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="contains('[CT][ACD][DQ][ATS]',concat('[',@identificationSchemeID,']'))"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="contains('[CT][ACD][DQ][ATS]',concat('[',@identificationSchemeID,']'))">
                    <xsl:attribute name="flag">information</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        AdditionalDocumentReference: Referensens attribut/kvalificerare (identificationSchemeID) måste
                        avtalas bilateralt om andra värden än CT, ACD, DQ eller ATS används
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="(contains('[CT][ACD][DQ][ATS]',concat('[',@identificationSchemeID,']'))  and @identificationSchemeAgencyName = 'SFTI') or not(contains('[CT][ACD][DQ][ATS]',concat('[',@identificationSchemeID,']')))"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="(contains('[CT][ACD][DQ][ATS]',concat('[',@identificationSchemeID,']')) and @identificationSchemeAgencyName = 'SFTI') or not(contains('[CT][ACD][DQ][ATS]',concat('[',@identificationSchemeID,']')))">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        AdditionalDocumentReference: När värdena CT, ACD, DQ eller ATS används ska
                        identificationSchemeAgencyName = 'SFTI'
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="(contains('[CT][ACD][DQ][ATS]',concat('[',@identificationSchemeID,']')) and  @identificationSchemeAgencyName = 'SFTI') or not(@identificationSchemeAgencyName= 'SFTI')"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="(contains('[CT][ACD][DQ][ATS]',concat('[',@identificationSchemeID,']')) and @identificationSchemeAgencyName = 'SFTI') or not(@identificationSchemeAgencyName= 'SFTI')">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        AdditionalDocumentReference: @identificationSchemeAgencyName='SFTI' ska inte användas då andra
                        värden än CT, ACD, DQ eller ATS anges.
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M62"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M62"/>
    <xsl:template match="@*|node()" priority="-2" mode="M62">
        <xsl:apply-templates select="*" mode="M62"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/sfti:AllowanceCharge" priority="1000" mode="M63">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/sfti:AllowanceCharge"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="/sfti:Invoice/cac:LegalTotal/cbc:TaxExclusiveTotalAmount"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="/sfti:Invoice/cac:LegalTotal/cbc:TaxExclusiveTotalAmount">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        I denna faktura bör fakturabelopp exkl skatt redovisas i LegalTotal/TaxExclusiveTotalAmount
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="normalize-space(cac:TaxCategory/cac:TaxScheme/cac:ID) ='VAT'"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="normalize-space(cac:TaxCategory/cac:TaxScheme/cac:ID) ='VAT'">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        AllowanceCharge/TaxCategory/TaxScheme/ID felaktig, kan bara vara "VAT" i AllowanceCharge
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M63"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M63"/>
    <xsl:template match="@*|node()" priority="-2" mode="M63">
        <xsl:apply-templates select="*" mode="M63"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/sfti:AllowanceCharge/cac:ReasonCode" priority="1000"
                  mode="M64">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/sfti:AllowanceCharge/cac:ReasonCode"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test=". = 'ZZZ'"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test=". = 'ZZZ'">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Oidentifierad orsakstext till rabatt/tillägg på fakturanivå
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M64"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M64"/>
    <xsl:template match="@*|node()" priority="-2" mode="M64">
        <xsl:apply-templates select="*" mode="M64"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/sfti:AllowanceCharge/cac:TaxCategory" priority="1000"
                  mode="M65">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/sfti:AllowanceCharge/cac:TaxCategory"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="cbc:Percent"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="cbc:Percent">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Percent måste anges
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M65"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M65"/>
    <xsl:template match="@*|node()" priority="-2" mode="M65">
        <xsl:apply-templates select="*" mode="M65"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/sfti:AllowanceCharge/cac:TaxCategory/cac:ID"
                  priority="1000"
                  mode="M66">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/sfti:AllowanceCharge/cac:TaxCategory/cac:ID"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="(normalize-space(.) = 'E') or (normalize-space(.) = 'S')"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="(normalize-space(.) = 'E') or (normalize-space(.) = 'S')">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        TaxTotal/TaxSubtotal/TaxCategory/ID felaktig, skall vara S, eller vid skatteundantag E
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="(normalize-space(.)='E' and ../../../cac:TaxTotal/cac:TaxSubTotal/cac:TaxCategory/cbc:ExemptionReason) or normalize-space(.) != 'E'"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="(normalize-space(.)='E' and ../../../cac:TaxTotal/cac:TaxSubTotal/cac:TaxCategory/cbc:ExemptionReason) or normalize-space(.) != 'E'">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        TaxTotal/TaxSubtotal/TaxCategory/ID=E: Skatteundantag angivet, kontrollera att fakturan
                        innehåller en motivering av undantaget.
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M66"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M66"/>
    <xsl:template match="@*|node()" priority="-2" mode="M66">
        <xsl:apply-templates select="*" mode="M66"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/sfti:AllowanceCharge/cac:TaxCategory/cbc:Percent"
                  priority="1000"
                  mode="M67">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/sfti:AllowanceCharge/cac:TaxCategory/cbc:Percent"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="(normalize-space(../cac:ID) = 'E' and xs:decimal(.) = 0) or (normalize-space(../cac:ID) !='E')"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="(normalize-space(../cac:ID) = 'E' and xs:decimal(.) = 0) or (normalize-space(../cac:ID) !='E')">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Percent skall vara = 0 vid skatteundantag
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="(normalize-space(../cac:ID) = 'S' and xs:decimal(.) != 0) or (normalize-space(../cac:ID) !='S')"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="(normalize-space(../cac:ID) = 'S' and xs:decimal(.) != 0) or (normalize-space(../cac:ID) !='S')">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Percent får endast vara = 0 vid skatteundantag
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M67"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M67"/>
    <xsl:template match="@*|node()" priority="-2" mode="M67">
        <xsl:apply-templates select="*" mode="M67"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/sfti:AllowanceCharge/cac:TaxCategory[cac:ID = 'S']"
                  priority="1000"
                  mode="M68">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/sfti:AllowanceCharge/cac:TaxCategory[cac:ID = 'S']"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="(number(cbc:Percent) = 6) or (number(cbc:Percent) = 12) or (number(cbc:Percent) = 25)"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="(number(cbc:Percent) = 6) or (number(cbc:Percent) = 12) or (number(cbc:Percent) = 25)">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        TaxTotal/TaxSubtotal/TaxCategory/Percent: kontrollera skattesatsen, för Sverige gäller 6, 12 och
                        25%, resp vid ID=E skattesatsen 0%. Andra skattesatser kan gälla i utländska fakturor.
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M68"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M68"/>
    <xsl:template match="@*|node()" priority="-2" mode="M68">
        <xsl:apply-templates select="*" mode="M68"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/sfti:InvoiceTypeCode" priority="1000" mode="M69">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/sfti:InvoiceTypeCode"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test=". = 380 or . = 381"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test=". = 380 or . = 381">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        /sfti:Invoice/sfti:InvoiceTypeCode felaktig, tillåtna värden är 380 och 381
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M69"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M69"/>
    <xsl:template match="@*|node()" priority="-2" mode="M69">
        <xsl:apply-templates select="*" mode="M69"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/sfti:InvoiceTypeCode[. = '381']" priority="1000"
                  mode="M70">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/sfti:InvoiceTypeCode[. = '381']"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="/sfti:Invoice/sfti:InitialInvoiceDocumentReference/cac:ID or (/sfti:Invoice/sfti:InvoicingPeriod[cbc:StartDateTime][cbc:EndDateTime])"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="/sfti:Invoice/sfti:InitialInvoiceDocumentReference/cac:ID or (/sfti:Invoice/sfti:InvoicingPeriod[cbc:StartDateTime][cbc:EndDateTime])">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Kreditnota skall referera till tidigare fakturering. InitialDocumentReference som normalt
                        används för detta saknas
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M70"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M70"/>
    <xsl:template match="@*|node()" priority="-2" mode="M70">
        <xsl:apply-templates select="*" mode="M70"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/sfti:InvoicingPeriod" priority="1000" mode="M71">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/sfti:InvoicingPeriod"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="cbc:StartDateTime and cbc:EndDateTime"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="cbc:StartDateTime and cbc:EndDateTime">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Både EndDateTime och StartDateTime måste anges i InvoicingPeriod
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="xs:dateTime(cbc:EndDateTime) &gt;= xs:dateTime(cbc:StartDateTime)"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="xs:dateTime(cbc:EndDateTime) &gt;= xs:dateTime(cbc:StartDateTime)">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        InvoicingPeriod: kontrollera periodens början och slut
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="xs:date(substring-before(cbc:EndDateTime,'T')) &lt;= xs:date(/sfti:Invoice/cbc:IssueDate)"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="xs:date(substring-before(cbc:EndDateTime,'T')) &lt;= xs:date(/sfti:Invoice/cbc:IssueDate)">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        IssueDate: Fakturan utställd före faktura-periodens slut
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M71"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M71"/>
    <xsl:template match="@*|node()" priority="-2" mode="M71">
        <xsl:apply-templates select="*" mode="M71"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/sfti:LineItemCountNumeric" priority="1000" mode="M72">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/sfti:LineItemCountNumeric"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="count(/sfti:Invoice/cac:InvoiceLine) = ."/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="count(/sfti:Invoice/cac:InvoiceLine) = .">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        LineItemCountNumeric stämmer inte med antalet fakturarader
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="number(.) &gt; 0"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="number(.) &gt; 0">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        LineItemCountNumeric skall vara ett positivt heltal
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M72"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M72"/>
    <xsl:template match="@*|node()" priority="-2" mode="M72">
        <xsl:apply-templates select="*" mode="M72"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/sfti:RequisitionistDocumentReference/cac:ID"
                  priority="1000"
                  mode="M73">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/sfti:RequisitionistDocumentReference/cac:ID"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="translate(.,' ','') != ''"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="translate(.,' ','') != ''">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Invoice/RequisitionistDocumentReference/ID får inte vara tom
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M73"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M73"/>
    <xsl:template match="@*|node()" priority="-2" mode="M73">
        <xsl:apply-templates select="*" mode="M73"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice/sfti:TaxCurrencyCode" priority="1000" mode="M74">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice/sfti:TaxCurrencyCode"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="/sfti:Invoice/cac:ExchangeRate"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl" test="/sfti:Invoice/cac:ExchangeRate">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        TaxCurrencyCode förutsätter uppgifter om omräkningskurs i ExchangeRate
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="/sfti:Invoice/cac:TaxTotal/cac:TaxSubTotal"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="/sfti:Invoice/cac:TaxTotal/cac:TaxSubTotal">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        TaxCurrencyCode kräver att omräkning av moms redovisas i TaxSubTotal/TaxCurrencyTaxAmount
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="/sfti:Invoice/cac:TaxTotal/cac:TaxSubTotal/cac:TaxCurrencyTaxAmount"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="/sfti:Invoice/cac:TaxTotal/cac:TaxSubTotal/cac:TaxCurrencyTaxAmount">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        TaxCurrencyCode kräver att omräkning av moms redovisas i TaxSubTotal/TaxCurrencyTaxAmount
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="/sfti:Invoice/sfti:InvoiceCurrencyCode"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="/sfti:Invoice/sfti:InvoiceCurrencyCode">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        InvoiceCurrencyCode saknas
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M74"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M74"/>
    <xsl:template match="@*|node()" priority="-2" mode="M74">
        <xsl:apply-templates select="*" mode="M74"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template
            match="/sfti:Invoice[(sfti:TaxCurrencyCode = sfti:InvoiceCurrencyCode) or not(sfti:TaxCurrencyCode)]/cac:TaxTotal/cac:TaxSubTotal"
            priority="1000"
            mode="M75">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice[(sfti:TaxCurrencyCode = sfti:InvoiceCurrencyCode) or not(sfti:TaxCurrencyCode)]/cac:TaxTotal/cac:TaxSubTotal"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="abs((number(cbc:TaxableAmount) * number(cac:TaxCategory/cbc:Percent) div 100) - number(cbc:TaxAmount)) &lt; 0.5"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="abs((number(cbc:TaxableAmount) * number(cac:TaxCategory/cbc:Percent) div 100) - number(cbc:TaxAmount)) &lt; 0.5">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        TaxTotal/TaxSubTotal/TaxAmount: Momsbelopp felaktigt beräknat för skattesats
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M75"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M75"/>
    <xsl:template match="@*|node()" priority="-2" mode="M75">
        <xsl:apply-templates select="*" mode="M75"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template
            match="/sfti:Invoice[cac:TaxTotal/cac:TaxSubTotal/cbc:TaxableAmount]/cac:LegalTotal/cbc:TaxExclusiveTotalAmount"
            priority="1000"
            mode="M76">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice[cac:TaxTotal/cac:TaxSubTotal/cbc:TaxableAmount]/cac:LegalTotal/cbc:TaxExclusiveTotalAmount"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="abs(number(.) - sum(/sfti:Invoice/cac:TaxTotal/cac:TaxSubTotal/cbc:TaxableAmount))&lt;0.5"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="abs(number(.) - sum(/sfti:Invoice/cac:TaxTotal/cac:TaxSubTotal/cbc:TaxableAmount))&lt;0.5">
                    <xsl:attribute name="flag">warning</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        Fakturerat belopp exkl moms (LegalTotal/TaxExclusiveTotalAmount) överensstämmer inte med
                        skattepliktiga belopp i TaxTotal/TaxSubTotal/TaxableAmount
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M76"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M76"/>
    <xsl:template match="@*|node()" priority="-2" mode="M76">
        <xsl:apply-templates select="*" mode="M76"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template match="/sfti:Invoice[sfti:InvoiceCurrencyCode][sfti:TaxCurrencyCode]"
                  priority="1000"
                  mode="M77">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice[sfti:InvoiceCurrencyCode][sfti:TaxCurrencyCode]"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="sfti:InvoiceCurrencyCode != sfti:TaxCurrencyCode"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="sfti:InvoiceCurrencyCode != sfti:TaxCurrencyCode">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        /sfti:Invoice/sfti:TaxCurrencyCode får inte vara samma som
                        /sfti:Invoice/sfti:InvoiceCurrencyCode
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M77"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M77"/>
    <xsl:template match="@*|node()" priority="-2" mode="M77">
        <xsl:apply-templates select="*" mode="M77"/>
    </xsl:template>

    <!--PATTERN -->


    <!--RULE -->
    <xsl:template
            match="/sfti:Invoice[sfti:TaxCurrencyCode][sfti:TaxCurrencyCode != sfti:InvoiceCurrencyCode]/cac:TaxTotal"
            priority="1000"
            mode="M78">
        <svrl:fired-rule xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                         context="/sfti:Invoice[sfti:TaxCurrencyCode][sfti:TaxCurrencyCode != sfti:InvoiceCurrencyCode]/cac:TaxTotal"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when
                    test="abs(sum(cac:TaxSubTotal/cac:TaxCurrencyTaxAmount) - sum(cac:TaxSubTotal/cbc:TaxAmount) * number(/sfti:Invoice/cac:ExchangeRate/cbc:CalculationRate)) &lt; 0.5"/>
            <xsl:otherwise>
                <svrl:failed-assert xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                                    test="abs(sum(cac:TaxSubTotal/cac:TaxCurrencyTaxAmount) - sum(cac:TaxSubTotal/cbc:TaxAmount) * number(/sfti:Invoice/cac:ExchangeRate/cbc:CalculationRate)) &lt; 0.5">
                    <xsl:attribute name="flag">fatal</xsl:attribute>
                    <xsl:attribute name="location">
                        <xsl:apply-templates select="." mode="schematron-select-full-path"/>
                    </xsl:attribute>
                    <svrl:text>
                        TaxTotal/TaxSubTotal/TaxCurrencyTaxAmount: felaktig omräkning av moms till momsvaluta för
                        skattesats
                    </svrl:text>
                </svrl:failed-assert>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*" mode="M78"/>
    </xsl:template>
    <xsl:template match="text()" priority="-1" mode="M78"/>
    <xsl:template match="@*|node()" priority="-2" mode="M78">
        <xsl:apply-templates select="*" mode="M78"/>
    </xsl:template>
</xsl:stylesheet>