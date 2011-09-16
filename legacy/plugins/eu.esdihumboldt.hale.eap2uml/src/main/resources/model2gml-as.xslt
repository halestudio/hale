<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    exclude-result-prefixes="m f xsd data"
		xmlns:xsd="http://www.w3.org/2001/XMLSchema"
		xmlns:gml="http://www.opengis.net/gml/3.2"
    xmlns:m="urn:gml-application-schema-model"                
		xmlns:f="gmlas-toolkit:functions"
		xmlns:data="gmlas-toolkit:data"
>
  <xsl:include href="predefined-types.xslt"/>

  <xsl:param name="gml-version" select="'3.2'"/>
  <xsl:param name="output-root" select="'output'"/>
  
  <xsl:output method="xml" indent="yes"/>
  <xsl:strip-space  elements="*"/>

  <xsl:variable name="root" select="/"/>

  <xsl:template match="/">
    <xsl:message>Processing <xsl:value-of select="tokenize(document-uri(/), '(\\|/)')[last()]"/></xsl:message>
    <xsl:apply-templates select="//m:application-schema"/>
  </xsl:template>

  <xsl:template name="application-schema" match="m:application-schema">
    <xsl:variable name="this" select="."/>
    <xsl:result-document href="{concat($output-root, '/', @xsdDocument)}">
     <xsl:message>-<xsl:value-of select="@xsdDocument"/></xsl:message>
     <xsl:comment>
        LICENSE
        version
        .....
     
        <xsl:text>&#10;</xsl:text></xsl:comment>      
      <xsl:text>&#10;</xsl:text><xsd:schema targetNamespace="{@targetNamespace}" elementFormDefault="qualified"
					version="{@version}"
					xmlns:gml="http://www.opengis.net/gml/3.2">
        <xsl:namespace name="{@nsPrefix}" select="@targetNamespace"/>
        <xsl:for-each select="m:import">
          <xsl:variable name="imported" select="f:application-schema.get-by-document(@xsdDocument)"/>
          <xsl:namespace name="{$imported/@nsPrefix}" select="$imported/@targetNamespace"/>
        </xsl:for-each>
        <xsd:import namespace="http://www.opengis.net/gml/3.2" schemaLocation="{concat($this/f:application-schema.path-to-output-root(.), '../../../schemas.opengis.net/gml/3.2.1/gml.xsd')}"/>
        <xsd:import namespace="http://www.w3.org/1999/xlink"/>
        <xsl:for-each select="m:import">
          <xsl:variable name="imported" select="f:application-schema.get-by-document(@xsdDocument)"/>
          <xsd:import namespace="{$imported/@targetNamespace}" schemaLocation="{@xsdDocument}"/>
        </xsl:for-each>
        <xsl:apply-templates/>
        <xsl:text>&#10;</xsl:text>
        <xsl:text>&#10;</xsl:text>
      </xsd:schema>
    </xsl:result-document>
  </xsl:template>


  <xsl:template name="complex-type" match="m:feature-type|m:object-type|m:data-type|m:union">

    <xsl:message>--<xsl:value-of select="concat(@name, ' ', local-name(.))"/></xsl:message>

    <xsl:call-template name="section-header">
      <xsl:with-param name="text" select="concat(@name, ' ', local-name(.))"/>
    </xsl:call-template>

    <xsl:call-template name="element-header"/>
    <xsd:element name="{@name}"
								 type="{concat(../@nsPrefix,':',concat(@name, 'Type'))}"
								 substitutionGroup="{f:class.substitution-group(.)}"/>

    <xsl:call-template name="element-header"/>
    <xsd:complexType name="{concat(@name, 'Type')}">
      <xsl:if test="@abstract='true'">
        <xsl:attribute name="abstract" select="'true'"/>
      </xsl:if>

      <xsl:variable name="attributes">
        <xsl:element name="{if (local-name(.)='union') then 'xsd:choice' else 'xsd:sequence'}">
          <xsl:apply-templates select="m:attribute"/>
        </xsl:element>
      </xsl:variable>
      
      <xsl:choose>
        <xsl:when test="f:class.base(.)">
          <xsd:complexContent>
            <xsd:extension base="{f:class.base(.)}">
              <xsl:copy-of select="$attributes"/>
            </xsd:extension>
          </xsd:complexContent>
        </xsl:when>
        <xsl:otherwise>
          <xsl:copy-of select="$attributes"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsd:complexType>

    <xsl:if test="not(@hasPropertyDefinition='false')">
      <xsl:call-template name="element-header"/>
      <xsd:complexType name="{concat(@name, 'PropertyType')}">
        <xsl:choose>
          <xsl:when test="local-name(.)='feature-type' or local-name(.)='object-type'">
            <xsl:call-template name="reference-type"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="inline-type"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsd:complexType>
    </xsl:if>

    <xsl:if test="not(@hasValuePropertyDefinition='false')">
      <xsl:call-template name="element-header"/>
      <xsd:complexType name="{concat(@name, 'PropertyByValueType')}">
        <xsl:call-template name="inline-type"/>
      </xsd:complexType>
    </xsl:if>
  </xsl:template>


  <xsl:template name="reference-type">
    <xsd:sequence minOccurs="0">
      <xsd:element ref="{concat(../@nsPrefix,':',@name)}"/>
    </xsd:sequence>
    <xsd:attributeGroup ref="gml:AssociationAttributeGroup"/>
    <xsd:attributeGroup ref="gml:OwnershipAttributeGroup" />
  </xsl:template>
  
  <xsl:template name="inline-type">
    <xsd:sequence>
      <xsd:element ref="{concat(../@nsPrefix,':',@name)}"/>
    </xsd:sequence>
    <xsd:attributeGroup ref="gml:OwnershipAttributeGroup" />
  </xsl:template>
  

  <xsl:template name="simple-property" match="m:attribute[f:uml-type.is-predefinied-type(@type) and not(@isMetadata='true') and (not(@aggregation) or @aggregation='none')]">
    <xsd:element name="{@name}">
      <xsl:attribute name="type" select="f:uml-type.get-property-type-name(@type)"/>
      <xsl:call-template name="property-multiplicity-attribute"/>
    </xsd:element>
  </xsl:template>


  <xsl:template name="complex-property" match="m:attribute[not(f:uml-type.is-predefinied-type(@type)) and not(@isMetadata='true') and (not(@aggregation) or @aggregation='none')]">
    <xsd:element name="{@name}">
      <xsl:call-template name="property-multiplicity-attribute"/>
      <xsl:choose>
        <xsl:when test="not(f:class.get-by-prefix-name(@type)/@hasPropertyDefinition='false')">
          <xsl:attribute name="type" select="concat(@type,'PropertyType')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsd:complexType>
            <xsd:sequence minOccurs="0">
              <xsd:element ref="{@type}"/>
            </xsd:sequence>
            <xsd:attributeGroup ref="gml:AssociationAttributeGroup"/>
            <xsd:attributeGroup ref="gml:OwnershipAttributeGroup" />
          </xsd:complexType>
        </xsl:otherwise>
      </xsl:choose>      
    </xsd:element>
  </xsl:template>

  <xsl:template name="metadata-property" match="m:attribute[@isMetadata='true']">
    <xsd:element name="{@name}">
      <xsl:call-template name="property-multiplicity-attribute"/>
      <xsd:complexType>
        <xsd:complexContent>
          <xsd:extension base="gml:AbstractMetadataPropertyType">
            <xsd:sequence>
              <xsd:element ref="{@type}"/>
            </xsd:sequence>
          </xsd:extension>
        </xsd:complexContent>
      </xsd:complexType>
    </xsd:element>
  </xsl:template>

  <xsl:template name="association-property" match="m:attribute[not(@isMetadata='true') and (@aggregation='shared' or @aggregation='composite')]">
    <xsd:element name="{@name}">
      <xsl:call-template name="property-multiplicity-attribute"/>
      <xsd:complexType>
        <xsd:complexContent>
          <xsd:extension base="gml:AbstractMemberType">
            <xsd:sequence minOccurs="0">
              <xsd:element ref="{@type}"/>
            </xsd:sequence>
            <xsd:attributeGroup ref="gml:AssociationAttributeGroup"/>
          </xsd:extension>
        </xsd:complexContent>
      </xsd:complexType>
    </xsd:element>
  </xsl:template>


  <xsl:template name="property-type-attribute">
    <xsl:choose>
      <xsl:when test="f:uml-type.is-predefinied-type(@type)">
      </xsl:when>
      <xsl:otherwise>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="property-multiplicity-attribute">
    <xsl:if test="@min!=1">
      <xsl:attribute name="minOccurs" select="@min"/>
    </xsl:if>
    <xsl:if test="@max and @max!='1'">
      <xsl:attribute name="maxOccurs" select="if (@max='*') then 'unbounded' else @max"/>
    </xsl:if>
  </xsl:template>



  <xsl:template name="enumeration" match="m:enumeration">

    <xsl:message>--<xsl:value-of select="concat(@name, ' ', local-name(.))"/></xsl:message>
    
    <xsl:call-template name="section-header">
      <xsl:with-param name="text" select="concat(@name, ' enumeration')"/>
    </xsl:call-template>

    <xsl:call-template name="element-header"/>
    <xsd:simpleType name="{@name}">
      <xsd:restriction base="xsd:string">
        <xsl:apply-templates select="m:item"/>
      </xsd:restriction>
    </xsd:simpleType>
  </xsl:template>

  <xsl:template name="code-list" match="m:code-list[not(@asDictionary) or @asDictionary='false']">

    <xsl:message>--<xsl:value-of select="concat(@name, ' ', local-name(.))"/></xsl:message>
    
    <xsl:call-template name="section-header">
      <xsl:with-param name="text" select="concat(@name, ' code list')"/>
    </xsl:call-template>

    <xsl:call-template name="element-header"/>
    <xsd:simpleType name="{concat(@name, 'Type')}">
      <xsd:union memberTypes="{concat(../@nsPrefix,':',@name),' ',concat(../@nsPrefix,':',@name,'OtherType')}"/>
    </xsd:simpleType>

    <xsl:call-template name="element-header"/>
    <xsd:simpleType name="{@name}">
      <xsd:restriction base="xsd:string">
        <xsl:apply-templates select="m:item"/>
      </xsd:restriction>
    </xsd:simpleType>

    <xsl:call-template name="element-header"/>
    <xsd:simpleType name="{concat(@name, 'OtherType')}">
      <xsd:restriction base="xsd:string">
        <xsd:pattern value="other: \w{{2,}}"/>
      </xsd:restriction>
    </xsd:simpleType>
  </xsl:template>

  <xsl:template name="enumeration-literal" match="m:item">
    <xsd:enumeration value="{@value}">
      <xsl:if test="@description">
        <xsd:annotation>
          <xsd:appinfo>
            <gml:description>
              <xsl:value-of select="@description"/>
            </gml:description>
          </xsd:appinfo>
        </xsd:annotation>
      </xsl:if>
    </xsd:enumeration>
  </xsl:template>

  <xsl:template name="code-list-as-dictionary" match="m:code-list[@asDictionary='true']">
    <gml:Dictionary gml:id="CodeList" >
      <gml:identifier codeSpace="{concat(../@targetNamespace,'/',@name)}">My code lists</gml:identifier>
      <gml:dictionaryEntry>
        <gml:Dictionary gml:id="{@name}">
          <gml:identifier codeSpace="{concat(../@targetNamespace,'/',../@xsdDocument)}">
            <xsl:value-of select="@name"/>
          </gml:identifier>
          <xsl:apply-templates mode="dictionary-entry" select="m:item"/>
        </gml:Dictionary>
      </gml:dictionaryEntry>
    </gml:Dictionary>
  </xsl:template>

  <xsl:template name="enumeration-literal-dictionary-entry" mode="dictionary-entry" match="m:item">
    <gml:dictionaryEntry>
      <gml:Definition gml:id="{concat(../@name,'_',@value)}">
        <gml:description>
          <xsl:value-of select="@description"/>
        </gml:description>
        <gml:identifier codeSpace="{concat(../../@targetNamespace,'/',../../@xsdDocument,'#',../@name)}">
          <xsl:value-of select="@value"/>
        </gml:identifier>
      </gml:Definition>
    </gml:dictionaryEntry>
  </xsl:template>

  <!-- ============================================================= -->

  <xsl:template name="section-header">
    <xsl:param name="text"/>
    <xsl:text>&#10;   </xsl:text>
    <xsl:text>&#10;   </xsl:text>
    <xsl:text>&#10;   </xsl:text>
    <xsl:comment>
      <xsl:text> </xsl:text>
      <xsl:value-of select="$text"/>
      <xsl:text> ------------------------- </xsl:text>
    </xsl:comment>
  </xsl:template>

  <xsl:template name="element-header">
    <xsl:param name="text"/>
    <xsl:text>&#10;   </xsl:text>
    <xsl:text>&#10;   </xsl:text>
    <xsl:if test="string-length($text)>0">
      <xsl:comment>
        <xsl:text> </xsl:text>
        <xsl:value-of select="$text"/>
        <xsl:text> ------------------------- </xsl:text>
      </xsl:comment>
    </xsl:if>
  </xsl:template>

  <!-- ============================================================= -->


  <!-- = application-schema functions ======================================================== -->
  
  <xsl:function name="f:application-schema.get-by-document" as="element()">
    <xsl:param name="document" as="xsd:string"/>
    <xsl:sequence select="$root//m:application-schema[@xsdDocument=$document]"/>
  </xsl:function>

  <xsl:function name="f:application-schema.path-to-output-root">
    <xsl:param name="this" as="element()"/>
    <xsl:for-each select="tokenize($this/@xsdDocument,'/')[position()!=last()]">
      <xsl:text>../</xsl:text>
    </xsl:for-each>
  </xsl:function>

  <!-- = feature-type functions ======================================================== -->

  <xsl:function name="f:class.substitution-group" as="xsd:string">
    <xsl:param name="this" />
    <xsl:choose>
      <xsl:when test="$this/@base">
        <xsl:value-of select="$this/@base"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$this/local-name()='feature-type'">
            <xsl:value-of select="'gml:AbstractFeature'"/>
          </xsl:when>
          <xsl:when test="$this/local-name()='object-type'">
            <xsl:value-of select="'gml:AbstractGML'"/>
          </xsl:when>
          <xsl:otherwise>  <!-- data-type | union -->
            <xsl:value-of select="'gml:AbstractObject'"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="f:class.base" as="xsd:string?">
    <xsl:param name="this" />
    <xsl:choose>      
      <xsl:when test="$this/@base">
        <xsl:value-of select="concat($this/@base,'Type')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$this/local-name()='feature-type'">
            <xsl:value-of select="'gml:AbstractFeatureType'"/>
          </xsl:when>
          <xsl:when test="$this/local-name()='object-type'">
            <xsl:value-of select="'gml:AbstractGMLType'"/>
          </xsl:when>
          <!-- data-type | union -->
          <!-- return empty string -->
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="f:class.get-by-prefix-name" as="element()?">
    <xsl:param name="prefix-name" as="xsd:string"/>
    <xsl:sequence select="$root//m:application-schema[@nsPrefix=substring-before($prefix-name, ':')]/*[@name=substring-after($prefix-name,':')]"/>
  </xsl:function>
  
</xsl:stylesheet>