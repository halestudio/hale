<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    exclude-result-prefixes="f xmi xsd data"
    xmlns:xmi="http://schema.omg.org/spec/XMI/2.1" 
    xmlns:m="urn:gml-application-schema-model" 
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
		xmlns:f="gmlas-toolkit:functions"
		xmlns:data="gmlas-toolkit:data"
>
	<!--xmlns:uml="http://schema.omg.org/spec/UML/2.1.2"-->
	<!--
      MagicDraw 16.0 exports UML 2.1.2
			MagicDraw 16.8 exports UML 2.3
	-->
	<!--
		LGPL
		......ggg
	-->

  <xsl:include href="predefined-types.xslt"/>

  <xsl:output method="xml" indent="yes"/>
  <xsl:strip-space  elements="*"/>


  <xsl:param name="uml-version" select="'2.1.2'"/>
  <xsl:param name="uml-ns" select="concat('http://schema.omg.org/spec/UML/', $uml-version)"/>
  <xsl:param name="xmi-version" select="'2.1'"/>
  <xsl:param name="gml-version" select="'3.2'"/>
 
	
	<xsl:variable name="root" select="/"/>
	<xsl:key name="elements-by-id" match="//*[@xmi:id]" use="@xmi:id"/>
  
	
	<xsl:template match="/">
    
    <xsl:comment>
      LICENSE
      versionjjjj
      .....
      <xsl:text>&#10;</xsl:text>
    </xsl:comment>
    
    <xsl:message>Processing <xsl:value-of select="tokenize(document-uri(/), '(\\|/)')[last()]"/></xsl:message>
    
    <xsl:text>&#10;</xsl:text><m:application-schema-model>
      <xsl:for-each select="//*:Application_Schema">
        <xsl:namespace name="{f:application-schema.xmlns(.)}" select="f:application-schema.target-namespace(.)"/>
      </xsl:for-each>
      <xsl:apply-templates select="//*:Application_Schema"/>
    </m:application-schema-model>
    
	</xsl:template>
  

	<xsl:template name="application-schema" match="//*:Application_Schema">
    
    <xsl:message>-<xsl:value-of select="concat(f:application-schema.xsd-document(.), ' ', local-name(.))"/></xsl:message>
    
    <m:application-schema xsdDocument="{f:application-schema.xsd-document(.)}" nsPrefix="{f:application-schema.xmlns(.)}" targetNamespace="{f:application-schema.target-namespace(.)}" version="{f:application-schema.version(.)}">
      <xsl:for-each select="f:application-schema.get-package(.)/f:package.get-supplier-packages(.)[f:package.has-application-schema(.)]/f:package.get-application-schema(.)">
        <m:import xsdDocument="{f:application-schema.xsd-document(.)}"/>
      </xsl:for-each>
      <xsl:apply-templates select="f:application-schema.get-package(.)//*[(@xmi:type='uml:Class' or @xmi:type='uml:Enumeration' or @xmi:type='uml:DataType') and f:class.get-application-schema(.)/@xmi:id=current()/@xmi:id]"/>
    </m:application-schema>
    
	</xsl:template>
  
  

  <xsl:template name="complex-type" match="*[@xmi:type='uml:Class']">
    
    <xsl:message>--<xsl:value-of select="concat(@name, ' ', @xmi:type)"/></xsl:message>
    
    <!--xsl:call-template name="check-complex-type"/-->
    
    <xsl:element name="{concat('m:', f:class.element-name(.))}">
      <xsl:attribute name="name" select="@name"/>
      <xsl:if test="f:class.is-abstract(.)">
        <xsl:attribute name="abstract" select="'true'"/>
      </xsl:if>
      <xsl:if test="not(f:class.has-property-definition(.))">
        <xsl:attribute name="hasPropertyDefinition" select="'false'"/>
      </xsl:if>
      <xsl:if test="not(f:class.has-value-property-definition(.))">
        <xsl:attribute name="hasValuePropertyDefinition" select="'false'"/>
      </xsl:if>
      <xsl:if test="f:class.has-base-class(.)">
        <xsl:attribute name="base" select="f:class.base-class(.)/f:class.prefixed-name(.)"/>
      </xsl:if>
      <xsl:apply-templates select="ownedAttribute[@xmi:type='uml:Property']">
        <xsl:sort select="f:property.sequence-number(.)"/>
      </xsl:apply-templates>
    </xsl:element>
    
	</xsl:template>
  

	<xsl:template name="enumeration" match="*[@xmi:type='uml:Enumeration']">
    
    <xsl:message>--<xsl:value-of select="concat(@name, ' ', @xmi:type)"/></xsl:message>
    
    <xsl:element name="{concat('m:', f:class.element-name(.))}">
      <xsl:attribute name="name" select="@name"/>
      <xsl:if test="f:enumeration.as-dictionary(.)">
        <xsl:attribute name="asDictionary" select="'true'"/>
      </xsl:if>
      <xsl:apply-templates select="*[@xmi:type='uml:EnumerationLiteral' or @xmi:type='uml:Property']"/>
    </xsl:element>
    
	</xsl:template>	
  
  

	<xsl:template name="simple-property" match="*[@xmi:type='uml:Property' and ../@xmi:type='uml:Class']">

    <xsl:variable name="attribute">
      <xsl:if test="f:property.get-stereotype(.)/@inlineOrByReference and not(f:property.type(.)/f:class.is-feature-type(.) or f:property.type(.)/f:class.is-object-type(.))">
        <xsl:comment>
          WARNING inlineOrByReference tag is ignored for data types and unions
        </xsl:comment>
      </xsl:if>
      <m:attribute name="{f:property.name(.)}">
        <xsl:choose>
          <xsl:when test="not(f:property.type(.))">
            <xsl:attribute name="type" select="f:property.type(.)/@name"/>
          </xsl:when>
          <xsl:when test="f:uml-type.is-predefinied-type(f:property.type(.)/@name)">
            <xsl:attribute name="type" select="f:property.type(.)/@name"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:attribute name="type" select="f:property.type(.)/f:class.prefixed-name(.)"/>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="f:property.lower(.)!=1">
          <xsl:attribute name="min" select="f:property.lower(.)"/>
        </xsl:if>
        <xsl:if test="not(f:property.upper(.)) or f:property.upper(.)!=1">
          <xsl:attribute name="max" select="if (not(f:property.upper(.))) then '*' else f:property.upper(.)"/>
        </xsl:if>
        <xsl:if test="not(f:property.by-reference(.)) or not(f:property.type(.)/f:class.is-feature-type(.) or f:property.type(.)/f:class.is-object-type(.))">
          <xsl:attribute name="reference" select="'false'"/>
        </xsl:if>
        <xsl:if test="not(f:property.inline(.)) and (f:property.type(.)/f:class.is-feature-type(.) or f:property.type(.)/f:class.is-object-type(.))">
          <xsl:attribute name="inline" select="'false'"/>
        </xsl:if>
        <xsl:if test="f:property.is-metadata(.)">
          <xsl:attribute name="isMetadata" select="'true'"/>
        </xsl:if>
        <xsl:if test="@aggregation='composite' or @aggregation='shared'">
          <xsl:attribute name="aggregation" select="@aggregation"/>
        </xsl:if>
      </m:attribute>      
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="f:property.type(.) and @name">
        <xsl:copy-of select="$attribute"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:comment>
          <xsl:if test="not(@name)">
            <xsl:text>ERROR: UML property has no name! </xsl:text>
          </xsl:if>
          <xsl:if test="not(f:property.type(.))">
            <xsl:text>ERROR: UML property has type!</xsl:text>
          </xsl:if>
          "<xsl:value-of select="@name"/>"  (<xsl:value-of select="@xmi:id"/>)
        </xsl:comment>
      </xsl:otherwise>
    </xsl:choose>
    
	</xsl:template>



  <xsl:template name="enumeration-literal" match="*[@xmi:type='uml:EnumerationLiteral']">
		<m:item value="{f:enumeration.literal-name(.)}"/>
	</xsl:template>
  
  

	<xsl:template name="enumeration-literal-annotated" match="*[@xmi:type='uml:Property' and ../@xmi:type='uml:Enumeration']">
    <m:item value="{defaultValue/@value}" description="{@name}"/>
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

	<xsl:template name="check-data-type">
		<xsl:if test="f:class.has-multiple-base-class(.)">
			<xsl:message>
				<xsl:text>  warning: </xsl:text>
				<xsl:value-of select="f:class.name(.)"/>
				<xsl:text> has multiple base classes!</xsl:text>
			</xsl:message>
		</xsl:if>
		<xsl:if test="f:class.has-base-class(.) and (not(f:class.base-class(.)/f:class.is-data-type(.)) or f:class.base-class(.)/f:class.is-union(.))">
			<xsl:message>
				<xsl:text>  warning: </xsl:text>
				<xsl:value-of select="f:class.name(.)"/>
				<xsl:text> is data type, but base class is not!</xsl:text>
			</xsl:message>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="check-feature-type">
		<xsl:if test="f:class.has-multiple-base-class(.)">
			<xsl:message>
				<xsl:text>  warning: </xsl:text>
				<xsl:value-of select="f:class.name(.)"/>
				<xsl:text> has multiple base classes!</xsl:text>
			</xsl:message>
		</xsl:if>
		<xsl:if test="f:class.has-base-class(.) and not(f:class.base-class(.)/f:class.is-feature-type(.))">
			<xsl:message>
				<xsl:text>  warning: </xsl:text>
				<xsl:value-of select="f:class.name(.)"/>
				<xsl:text> is feture type, but base class is not!</xsl:text>
			</xsl:message>
		</xsl:if>
	</xsl:template>

	<xsl:template name="check-object-type">
		<xsl:if test="f:class.has-multiple-base-class(.)">
			<xsl:message>
				<xsl:text>  warning: </xsl:text>
				<xsl:value-of select="f:class.name(.)"/>
				<xsl:text> has multiple base classes!</xsl:text>
			</xsl:message>
		</xsl:if>
		<xsl:if test="f:class.has-base-class(.) and not(f:class.base-class(.)/f:class.is-object-type(.))">
			<xsl:message>
				<xsl:text>  warning: </xsl:text>
				<xsl:value-of select="f:class.name(.)"/>
				<xsl:text> is object type, but base class is not!</xsl:text>
			</xsl:message>
		</xsl:if>
	</xsl:template>
	
	<!-- = application-schema functions ================================================== -->
	
	<xsl:function name="f:application-schema.get-package" as="node()">
		<xsl:param name="this" as="node()" />
		<xsl:sequence select="$root/key('elements-by-id',$this/@base_Package)"/>
	</xsl:function>	

	<xsl:function name="f:application-schema.target-namespace" as="xsd:string">
		<xsl:param name="this" as="element()"/>
		<xsl:variable name="package" select="$root/key('elements-by-id',$this/@base_Package)"/>
		<xsl:choose>
			<xsl:when test="$this/@targetNamespace">
				<xsl:value-of	select="$this/@targetNamespace" />
			</xsl:when>
			<xsl:otherwise>
        <xsl:variable name="parent-schema" select="$this/ancestor::*[@xmi:type='uml:Package' and f:package.has-application-schema(.)][1]"/>
        <xsl:variable name="parent-schema-parents" select="$parent-schema/ancestor::*[@xmi:type='uml:Package']"/>
        <xsl:variable name="qualified-name">
          <xsl:value-of select="for $p in $package/ancestor-or-self::*[@xmi:type='uml:Package'] return translate($p/@name, ' ', '_')" separator="/"/>
        </xsl:variable>
        <xsl:value-of	select="concat('urn:', $qualified-name)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
	<xsl:function name="f:application-schema.xmlns" as="xsd:string">
		<xsl:param name="this" as="element()"/>
		<xsl:variable name="package" select="$root/key('elements-by-id',$this/@base_Package)"/>
		<xsl:choose>
			<xsl:when test="$this/@xmlns">
				<xsl:value-of	select="$this/@xmlns" />
			</xsl:when>
			<xsl:otherwise>
        <xsl:variable name="same-prefix" select="$root//*:Application_Schema[lower-case(substring(f:application-schema.get-package(.)/@name, 1, 3))=lower-case(substring($package/@name, 1, 3))]"/>
        <xsl:choose>
          <xsl:when test="count($same-prefix) > 1">
            <xsl:value-of	select="concat(lower-case(substring($package/@name, 1, 3)),f:index-of-node($same-prefix, $this))" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of	select="lower-case(substring($package/@name, 1, 3))" />
          </xsl:otherwise>
        </xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

  <xsl:function name="f:index-of-node" as="xsd:integer*">
    <xsl:param name="node-set" as="node()*"/>
    <xsl:param name="node" as="node()"/>
    <xsl:sequence select="
    for $i in 1 to count($node-set) return
       if ($node-set[$i] is $node) then $i else ()"/>
  </xsl:function>

  <xsl:function name="f:application-schema.version" as="xsd:string">
		<xsl:param name="this" as="element()"/>
		<xsl:variable name="package" select="$root/key('elements-by-id',$this/@base_Package)"/>
		<xsl:choose>
			<xsl:when test="$this/@version">
				<xsl:value-of	select="$this/@version" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>1.0</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<xsl:function name="f:application-schema.gml-schema-profile" as="xsd:string">
		<xsl:param name="this" as="element()"/>
		<xsl:variable name="package" select="$root/key('elements-by-id',$this/@base_Package)"/>
		<xsl:choose>
			<xsl:when test="$this/@gmlSchemaProfile">
				<xsl:value-of	select="$this/@gmlProfileSchema" />
			</xsl:when>
			<xsl:otherwise>
				<!--TODO: default gml schema-->
				<xsl:text></xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<xsl:function name="f:application-schema.xsd-document" as="xsd:string">
		<xsl:param name="this" as="element()"/>
		<xsl:variable name="package" select="$root/key('elements-by-id',$this/@base_Package)"/>
		<xsl:choose>
			<xsl:when test="$this/@xsdDocument">
				<xsl:value-of	select="$this/@xsdDocument" />
			</xsl:when>
			<xsl:otherwise>
        <xsl:variable name="parent-schema" select="$this/ancestor::*[@xmi:type='uml:Package' and f:package.has-application-schema(.)][1]"/>
        <xsl:variable name="parent-schema-parents" select="$parent-schema/ancestor::*[@xmi:type='uml:Package']"/>
        <xsl:variable name="qualified-name">
          <xsl:value-of select="for $p in $package/ancestor-or-self::*[@xmi:type='uml:Package'] return $p/@name" separator="/"/>
        </xsl:variable>
        <xsl:value-of	select="concat($qualified-name,'.xsd')" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<xsl:function name="f:application-schema.path-to-output-root">
		<xsl:param name="this" as="element()"/>
		<xsl:for-each select="tokenize(f:application-schema.xsd-document($this),'/')[position()!=last()]">
			<xsl:text>../</xsl:text>
		</xsl:for-each>
	</xsl:function>

	<!-- = package functions ======================================================== -->
	
	<xsl:function name="f:package.has-application-schema" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement, xsd:anyType)" />
		<xsl:value-of select="if ($root//*:Application_Schema[@base_Package=$this/@xmi:id]) then true() else false()" />
	</xsl:function>

	<xsl:function name="f:package.get-application-schema" as="element()">
		<xsl:param name="this" as="element(packagedElement, xsd:anyType)" />
		<xsl:sequence select="$root//*:Application_Schema[@base_Package=$this/@xmi:id]"/>		
	</xsl:function>

	<xsl:function name="f:package.get-supplier-packages"  as="element(packagedElement, xsd:anyType)*">
		<xsl:param name="this" as="element(packagedElement)"/>
		<xsl:sequence select="
                  $root/key('elements-by-id',$root//*[@xmi:type='uml:Dependency' and client/@xmi:idref=$this/@xmi:id]/supplier/@xmi:idref) |
                  $root/key('elements-by-id',$this//*[@xmi:type='uml:PackageImport']/@importedPackage)"/>
	</xsl:function>

	<!-- = class type functions ======================================================== -->

	<xsl:function name="f:class.is-data-type" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement, xsd:anyType)" />
		<xsl:value-of select="$this/@xmi:type='uml:Class' and count($root//*:DataType[@base_Class=$this/@xmi:id])>0" />
	</xsl:function>
	
	<xsl:function name="f:class.is-feature-type" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement, xsd:anyType)" />
		<xsl:value-of select="$this/@xmi:type='uml:Class' and count($root//*:FeatureType[@base_Class=$this/@xmi:id])>0" />
	</xsl:function>

	<xsl:function name="f:class.is-object-type" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement, xsd:anyType)" />
		<xsl:value-of select="$this/@xmi:type='uml:Class' and (count($root//*:Type[@base_Class=$this/@xmi:id])>0 or count($root//*[@base_Class=$this/@xmi:id])=0)" />
	</xsl:function>

	<xsl:function name="f:class.is-enumeration" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement, xsd:anyType)" />
    <!--xsl:value-of select="$this/@xmi:type='uml:Enumeration' and count($root//*:Enumeration[@base_Enumeration=$this/@xmi:id])>0" /-->
    <xsl:value-of select="$this/@xmi:type='uml:Enumeration' and count($root//*:CodeList[@base_Enumeration=$this/@xmi:id])=0 or
                  $this/@xmi:type='uml:Class' and count($root//*:Enumeration[@base_Enumeration=$this/@xmi:id])>0" />
  </xsl:function>

	<xsl:function name="f:class.is-code-list" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement, xsd:anyType)" />
		<xsl:value-of select="$this/@xmi:type='uml:Enumeration' and count($root//*:CodeList[@base_Enumeration=$this/@xmi:id])>0" />
	</xsl:function>
	
	<xsl:function name="f:class.is-union" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement, xsd:anyType)" />
		<xsl:value-of select="$this/@xmi:type='uml:Class' and count($root//*:Union[@base_Class=$this/@xmi:id])>0" />
	</xsl:function>

  <xsl:function name="f:class.element-name" as="xsd:string?">
    <xsl:param name="this" as="element()" />
    <xsl:choose>
      <xsl:when test="$this/f:class.is-data-type(.)">
        <xsl:value-of select="'data-type'"/>
      </xsl:when>
      <xsl:when test="$this/f:class.is-feature-type(.)">
        <xsl:value-of select="'feature-type'"/>
      </xsl:when>
      <xsl:when test="$this/f:class.is-object-type(.)">
        <xsl:value-of select="'object-type'"/>
      </xsl:when>
      <xsl:when test="$this/f:class.is-union(.)">
        <xsl:value-of select="'union'"/>
      </xsl:when>
      <xsl:when test="$this/f:class.is-enumeration(.)">
        <xsl:value-of select="'enumeration'"/>
      </xsl:when>
      <xsl:when test="$this/f:class.is-code-list(.)">
        <xsl:value-of select="'code-list'"/>
      </xsl:when>      
    </xsl:choose>
  </xsl:function>
	
	<!-- = class functions ======================================================== -->
	
	<xsl:function name="f:class.name" as="xsd:string">
		<xsl:param name="this" as="element(packagedElement, xsd:anyType)" />
    <xsl:variable name="parent-schema" select="$this/ancestor::*[@xmi:type='uml:Package' and f:package.has-application-schema(.)][1]"/>
    <xsl:variable name="parent-schema-parents" select="$parent-schema/ancestor::*[@xmi:type='uml:Package']"/>
		<xsl:value-of select="((for $p in $this/ancestor::*[@xmi:type='uml:Package' and not(f:package.has-application-schema(.)) and not(. intersect $parent-schema-parents)] return $p/@name),$this/@name)" separator="-"/>
	</xsl:function>

  <xsl:function name="f:class.prefixed-name" as="xsd:string">
    <xsl:param name="this" as="element(packagedElement, xsd:anyType)" />
    <xsl:choose>
      <xsl:when test="$this/f:class.get-application-schema(.)">
        <xsl:value-of select="concat($this/f:class.get-application-schema(.)/f:application-schema.xmlns(.),':',$this/f:class.name(.))"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$this/f:class.name(.)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

	<xsl:function name="f:class.get-application-schema" as="element()?">
		<xsl:param name="this" as="element(packagedElement, xsd:anyType)" />
    <xsl:sequence select="$root/key('elements-by-id',$this/@xmi:id)/ancestor::*[@xmi:type='uml:Package' and f:package.has-application-schema(.)][1]/f:package.get-application-schema(.)"/>
  </xsl:function>

	<xsl:function name="f:class.is-simple-type" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement, xsd:anyType)" />
		<xsl:value-of select="$this/@xmi:type='uml:PrimitiveType'"/>
	</xsl:function>

	<xsl:function name="f:class.has-base-class" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement, xsd:anyType)" />
		<xsl:value-of select="count($this/generalization[f:class.element-name(key('elements-by-id',@general,$root))=$this/f:class.element-name(.)])>0"/>
	</xsl:function>

	<xsl:function name="f:class.has-multiple-base-class" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement, xsd:anyType)" />
		<xsl:value-of select="count($this/generalization)>1"/>
	</xsl:function>

	<xsl:function name="f:class.base-class"  as="element(packagedElement, xsd:anyType)?">
		<xsl:param name="this" as="element(packagedElement, xsd:anyType)" />
		<xsl:sequence select="$root/key('elements-by-id',$this/generalization[f:class.element-name(key('elements-by-id',@general,$root))=$this/f:class.element-name(.)][1]/@general)"/>
	</xsl:function>
	
	<xsl:function name="f:class.is-abstract" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement, xsd:anyType)" />
		<xsl:value-of select="$this/@isAbstract='true'"/>
	</xsl:function>

	<!-- = class metadata functions ======================================================== -->

	<xsl:function name="f:class.has-property-definition" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement, xsd:anyType)" />
		<xsl:value-of select="if (not($root//*[@base_Class=$this/@xmi:id]/@noPropertyType) or $root//*[@base_Class=$this/@xmi:id]/@noPropertyType='false') then true() else false()" />
	</xsl:function>
	
	<xsl:function name="f:class.has-value-property-definition" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement, xsd:anyType)" />
		<xsl:value-of select="count($root//*[@base_Class=$this/@xmi:id]/@byValuePropertyType)=0 or $root//*[@base_Class=$this/@xmi:id]/@byValuePropertyType='true'" />
	</xsl:function>
	
	<!-- = enumeration name functions ======================================================== -->

	<xsl:function name="f:enumeration.literal-name" as="xsd:string">
		<xsl:param name="this" as="element(ownedLiteral)" />
		<xsl:value-of select="$this/@name"/>
	</xsl:function>
  
  <xsl:function name="f:enumeration.as-dictionary" as="xsd:string">
    <xsl:param name="this" as="element()" />
    <xsl:value-of select="$root//*[@base_Enumeration=$this/@xmi:id]/@asDictionary='true'" />
  </xsl:function>

  <!-- = property functions ======================================================== -->

	<xsl:function name="f:property.name" as="xsd:string">
		<xsl:param name="this" as="element(ownedAttribute)"/>
		<xsl:value-of select="$this/@name"/>
	</xsl:function>

	<xsl:function name="f:property.type" as="element()?">
		<xsl:param name="this" as="element(ownedAttribute)"/>
		<xsl:sequence select="$root/key('elements-by-id',$this/@type)"/>
	</xsl:function>

	<xsl:function name="f:property.lower" as="xsd:integer">
		<xsl:param name="this" as="element(ownedAttribute)"/>
		<xsl:choose>
			<xsl:when test="$this/@lowerValue">
				<xsl:value-of select="$this/@lowerValue"/>
			</xsl:when>
			<xsl:when test="$this/lowerValue/@value">
				<xsl:value-of select="$this/lowerValue/@value"/>
			</xsl:when>
      <xsl:when test="$this/lowerValue">
        <xsl:value-of select="0"/>
      </xsl:when>
      <xsl:otherwise>
				<xsl:value-of select="1"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<xsl:function name="f:property.upper" as="xsd:integer?">
		<xsl:param name="this" as="element(ownedAttribute)"/>
		<xsl:choose>
			<xsl:when test="$this/@upperValue">
				<xsl:value-of select="$this/@upperValue and $this/@upperValue!='*'"/>
			</xsl:when>
			<xsl:when test="$this/@upperValue and $this/@upperValue='*'"/>
			<xsl:when test="$this/upperValue and $this/upperValue/@value!='*'">
				<xsl:value-of select="$this/upperValue/@value"/>
			</xsl:when>
			<xsl:when test="$this/upperValue and $this/upperValue/@value='*'"/>
			<xsl:otherwise>
				<xsl:value-of select="1"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

  <xsl:function name="f:property.get-stereotype" as="element()?">
    <xsl:param name="this" as="element(ownedAttribute, xsd:anyType)" />
    <xsl:sequence select="$root//*:Attribute[@base_Property=$this/@xmi:id]"/>
  </xsl:function>

  <xsl:function name="f:property.sequence-number" as="xsd:integer?">
    <xsl:param name="this" as="element(ownedAttribute)"/>
    <xsl:if test="$this/f:property.get-stereotype(.)/@sequenceNumber">
      <xsl:value-of select="$this/f:property.get-stereotype(.)/@sequenceNumber"/>
    </xsl:if>
  </xsl:function>

  <xsl:function name="f:property.is-metadata" as="xsd:boolean">
    <xsl:param name="this" as="element(ownedAttribute)"/>
    <xsl:value-of select="$this/f:property.get-stereotype(.)/@isMetadata='true'"/>
  </xsl:function>
  
  <xsl:function name="f:property.inline-or-by-reference" as="xsd:string">
    <xsl:param name="this" as="element(ownedAttribute)"/>
    <xsl:choose>
      <xsl:when test="$this/f:property.get-stereotype(.)/@inlineOrByReference='inline'">
        <xsl:value-of select="'inline'"/>
      </xsl:when>
      <xsl:when test="$this/f:property.get-stereotype(.)/@inlineOrByReference='byReference'">
        <xsl:value-of select="'byReference'"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="'inlineOrByReference'"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="f:property.inline" as="xsd:boolean">
    <xsl:param name="this" as="element(ownedAttribute)"/>
    <xsl:value-of select="not($this/f:property.get-stereotype(.)/@inlineOrByReference='byReference')"/>
  </xsl:function>
  
  <xsl:function name="f:property.by-reference" as="xsd:boolean">
    <xsl:param name="this" as="element(ownedAttribute)"/>
    <xsl:value-of select="not($this/f:property.get-stereotype(.)/@inlineOrByReference='inline')"/>
  </xsl:function>

</xsl:stylesheet>