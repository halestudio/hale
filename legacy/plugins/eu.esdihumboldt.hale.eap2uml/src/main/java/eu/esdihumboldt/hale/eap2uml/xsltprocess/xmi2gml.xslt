<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    exclude-result-prefixes="f xmi xsd uml data"
    xmlns:xmi="http://schema.omg.org/spec/XMI/2.1" 
		xmlns:xsd="http://www.w3.org/2001/XMLSchema"
		xmlns:uml="http://schema.omg.org/spec/UML/2.1.2"
		xmlns:gml="http://www.opengis.net/gml/3.2"
		xmlns:f="xmi2gml-as:functions"
		xmlns:data="xmi2gml-as:data"
>
	<!--
		LGPL 
		.....
	-->
	<xsl:output method="xml" indent="yes"/>
	<xsl:strip-space  elements="*"/>
	
	<xsl:variable name="root" select="/"/>
	<xsl:key name="elements-by-id" match="//*[@xmi:id]" use="@xmi:id"/>
	
	<xsl:template match="/">
		<!--xsl:apply-templates select="//*:Application_Schema"/-->
            <!-- mdv TUD -->
		<xsl:apply-templates select="//*:Application_Schema|//*:applicationSchema"/>
	</xsl:template>

	<!--xsl:template name="application-schema" match="//*:Application_Schema"-->
      <!-- mdv TUD -->
	<xsl:template name="application-schema" match="//*:Application_Schema|//*:applicationSchema">
		<xsl:variable name="filename" select="concat('output/', f:application-schema.xsd-document(.))"/>
		<xsl:message>
			<xsl:value-of	select="$filename" />
		</xsl:message>
		<xsl:result-document href="{$filename}">
			<xsl:variable name="this" select="."/>
			<xsl:comment>
				LICENSE
				version
				.....
			</xsl:comment>
			<xsd:schema targetNamespace="{f:application-schema.target-namespace(.)}" elementFormDefault="qualified"
					version="{f:application-schema.version(.)}"
					xmlns:gml="http://www.opengis.net/gml/3.2">
			<xsl:namespace name="{f:application-schema.xmlns(.)}" select="f:application-schema.target-namespace(.)"/>
			<xsl:for-each select="f:application-schema.get-package(.)/f:package.get-supplier-packages(.)[f:package.has-application-schema(.)]/f:package.get-application-schema(.)">
				<xsl:namespace name="{f:application-schema.xmlns(.)}" select="f:application-schema.target-namespace(.)"/>
			</xsl:for-each>
			<xsd:import namespace="http://www.opengis.net/gml/3.2" schemaLocation="{concat($this/f:application-schema.path-to-output-root(.), '../../../schemas.opengis.net/gml/3.2.1/gml.xsd')}"/>
			<xsd:import namespace="http://www.w3.org/1999/xlink"/>
			<xsl:for-each select="f:application-schema.get-package(.)/f:package.get-supplier-packages(.)[f:package.has-application-schema(.)]/f:package.get-application-schema(.)">
				<xsd:import>
					<xsl:message>
						<xsl:text>  include: </xsl:text>
						<xsl:value-of select="$this/f:application-schema.path-to-output-root(.)"/>
						<xsl:value-of select="f:application-schema.xsd-document(.)"/>
					</xsl:message>
					<xsl:attribute name="namespace">
						<xsl:value-of select="f:application-schema.target-namespace(.)"/>
					</xsl:attribute>
					<xsl:attribute name="schemaLocation">
						<xsl:value-of select="$this/f:application-schema.path-to-output-root(.)"/>
						<xsl:value-of select="f:application-schema.xsd-document(.)"/>
					</xsl:attribute>
				</xsd:import>
			</xsl:for-each>
			<xsl:apply-templates select="f:application-schema.get-package(.)//*[@xmi:type='uml:Class' or @xmi:type='uml:Enumeration' or @xmi:type='DataType']"/>
			</xsd:schema>
		</xsl:result-document>
	</xsl:template>

	<xsl:template name="data-type" match="//*[@xmi:type='uml:Class' and (f:class.is-data-type(.) or f:class.is-union(.))]">
		
		<xsl:call-template name="check-data-type"/>
		<xsl:call-template name="section-header">
			<xsl:with-param name="text" select="concat(f:class.name(.), ' data type')"/>
		</xsl:call-template>

		<xsl:call-template name="element-header"/>
		<xsd:element name="{f:class.name(.)}"
								 type="{concat(f:class.get-application-schema(.)/f:application-schema.xmlns(.),':',f:class.type-name(.))}"
								 substitutionGroup="gml:AbstractObject"/>
		
		<xsl:call-template name="element-header"/>
		<xsd:complexType name="{f:class.type-name(.)}">
			<xsl:if test="f:class.is-abstract(.)">
				<xsl:attribute name="abstract" select="'true'"/>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="f:class.is-union(.)">
					<xsd:choice>
						<xsl:apply-templates select="ownedAttribute[@xmi:type='uml:Property']"/>
					</xsd:choice>
				</xsl:when>
				<xsl:otherwise>
					<xsd:sequence>
						<xsl:apply-templates select="ownedAttribute[@xmi:type='uml:Property']"/>
					</xsd:sequence>
				</xsl:otherwise>
			</xsl:choose>
		</xsd:complexType>
		
		<xsl:if test="f:class.has-property-definition(.)">
			<xsl:call-template name="element-header"/>
			<xsd:complexType name="{f:class.property-type-name(.)}">
				<xsd:sequence>
					<xsd:element ref="{concat(f:class.get-application-schema(.)/f:application-schema.xmlns(.),':',f:class.name(.))}"/>
				</xsd:sequence>
				<xsd:attributeGroup ref="gml:OwnershipAttributeGroup" />
			</xsd:complexType>
		</xsl:if>
	</xsl:template>

	<xsl:template name="feature-type" match="//*[@xmi:type='uml:Class' and f:class.is-feature-type(.)]">
		
		<xsl:call-template name="check-feature-type"/>
		<xsl:call-template name="section-header">
			<xsl:with-param name="text" select="concat(f:class.name(.), ' feature type')"/>
		</xsl:call-template>

		<xsl:call-template name="element-header"/>
		<xsd:element name="{f:class.name(.)}"
								 type="{concat(f:class.get-application-schema(.)/f:application-schema.xmlns(.),':',f:class.type-name(.))}"
								 substitutionGroup="{f:feature-type.substitution-group(.)}"/>
		
		<xsl:call-template name="element-header"/>
		<xsd:complexType name="{f:class.type-name(.)}">
			<xsl:if test="f:class.is-abstract(.)">
				<xsl:attribute name="abstract" select="'true'"/>
			</xsl:if>
			<xsd:complexContent>
				<xsd:extension base="{f:feature-type.base(.)}">
					<xsd:sequence>
						<xsl:apply-templates select="ownedAttribute[@xmi:type='uml:Property']"/>
					</xsd:sequence>
				</xsd:extension>
			</xsd:complexContent>
		</xsd:complexType>
		
		<xsl:if test="f:class.has-property-definition(.)">
			<xsl:call-template name="element-header"/>
			<xsd:complexType name="{f:class.property-type-name(.)}">
				<xsd:sequence>
					<xsd:element ref="{concat(f:class.get-application-schema(.)/f:application-schema.xmlns(.),':',f:class.name(.))}"/>
				</xsd:sequence>
				<xsd:attributeGroup ref="gml:AssociationAttributeGroup"/>
				<xsd:attributeGroup ref="gml:OwnershipAttributeGroup" />
			</xsd:complexType>
		</xsl:if>
		
		<xsl:if test="f:class.has-value-property-definition(.)">
			<xsl:call-template name="element-header"/>
			<xsd:complexType name="{f:class.property-by-value-type-name(.)}">
				<xsd:sequence>
					<xsd:element ref="{concat(f:class.get-application-schema(.)/f:application-schema.xmlns(.),':',f:class.name(.))}"/>
				</xsd:sequence>
				<xsd:attributeGroup ref="gml:OwnershipAttributeGroup" />
			</xsd:complexType>
		</xsl:if>
	</xsl:template>

	<xsl:template name="object-type" match="//*[@xmi:type='uml:Class' and f:class.is-object-type(.)]">
		
		<xsl:call-template name="check-object-type"/>
		<xsl:call-template name="section-header">
			<xsl:with-param name="text" select="concat(f:class.name(.), ' object type')"/>
		</xsl:call-template>

		<xsl:call-template name="element-header"/>
		<xsd:element name="{f:class.name(.)}"
								 type="{concat(f:class.get-application-schema(.)/f:application-schema.xmlns(.),':',f:class.type-name(.))}"
								 substitutionGroup="gml:AbstractCurveSegment"/>

		<xsl:call-template name="element-header"/>
		<xsd:complexType name="{f:class.type-name(.)}">
			<xsl:if test="f:class.is-abstract(.)">
				<xsl:attribute name="abstract" select="'true'"/>
			</xsl:if>
			<xsd:complexContent>
				<xsd:extension base="gml:AbstractCurveSegmentType">
					<xsd:sequence>
						<xsl:apply-templates select="ownedAttribute[@xmi:type='uml:Property']"/>
					</xsd:sequence>
				</xsd:extension>
			</xsd:complexContent>
		</xsd:complexType>
	</xsl:template>

	<xsl:template name="enumeration" match="//*[@xmi:type='uml:Enumeration' and f:class.is-enumeration(.)]">
		
		<xsl:call-template name="section-header">
			<xsl:with-param name="text" select="concat(f:class.name(.), ' enumeration')"/>
		</xsl:call-template>

		<xsl:call-template name="element-header"/>
		<xsd:simpleType name="{f:class.type-name(.)}">
			<xsd:restriction base="xsd:string">
				<xsl:apply-templates select="ownedLiteral[@xmi:type='uml:EnumerationLiteral']"/>
			</xsd:restriction>
		</xsd:simpleType>
	</xsl:template>
	
	<xsl:template name="code-list" match="//*[@xmi:type='uml:Enumeration' and f:class.is-code-list(.)]">
		
		<xsl:call-template name="section-header">
			<xsl:with-param name="text" select="concat(f:class.name(.), ' code list')"/>
		</xsl:call-template>

		<xsl:call-template name="element-header"/>
		<xsd:simpleType name="{f:class.type-name(.)}">
			<xsd:union memberTypes="{concat(f:class.get-application-schema(.)/f:application-schema.xmlns(.),':',f:enumeration.type-name(.),' ',f:class.get-application-schema(.)/f:application-schema.xmlns(.),':',f:enumeration.other-type-name(.))}"/>
		</xsd:simpleType>
		
		<xsl:call-template name="element-header"/>
		<xsd:simpleType name="{f:enumeration.type-name(.)}">
			<xsd:restriction base="xsd:string">
				<xsl:apply-templates select="ownedLiteral[@xmi:type='uml:EnumerationLiteral']"/>
			</xsd:restriction>
		</xsd:simpleType>
		
		<xsl:call-template name="element-header"/>
		<xsd:simpleType name="{f:enumeration.other-type-name(.)}">
			<xsd:restriction base="xsd:string">
				<xsd:pattern value="other: \w{{2,}}"/>
			</xsd:restriction>
		</xsd:simpleType>
	</xsl:template>

	<xsl:template name="simple-property" match="//*[@xmi:type='uml:Property']">

            <xsl:variable name="refto" >
              <xsl:call-template name="getUMLdatatypeId" >
                 <xsl:with-param name="this" select="." />
              </xsl:call-template>
	      </xsl:variable>

            <xsl:variable name="type">
              <xsl:choose>
                <xsl:when test="string-length($refto)>0">
                   <xsl:value-of select="f:property.type($refto)/f:class.property-type-name-ref(.)"/>
                </xsl:when>
                <xsl:otherwise>
                   <xsl:value-of select="'debug'"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

		<xsd:element name="{f:property.name(.)}" type="{$type}">
			<xsl:if test="f:property.lower(.)!=1">
				<xsl:attribute name="minOccurs" select="f:property.lower(.)"/>
			</xsl:if>
			<xsl:if test="not(f:property.upper(.)) or f:property.upper(.)!=1">
				<xsl:attribute name="maxOccurs" select="if (not(f:property.upper(.))) then 'unbounded' else f:property.upper(.)"/>
			</xsl:if>
		</xsd:element>

	</xsl:template>

	<xsl:template name="enumeration-literal" match="//*[@xmi:type='uml:EnumerationLiteral']">
		<xsd:enumeration value="{f:enumeration.literal-name(.)}"/>
	</xsl:template>

	<xsl:template name="enumeration-literal-annotated" mode="annotated" match="//*[@xmi:type='uml:EnumerationLiteral']">
		<xsd:enumeration value="5">
			<xsd:annotation>
				<xsd:appinfo>
					<gml:description>
						<xsl:value-of select="f:enumeration.literal-name(.)"/>
					</gml:description>
				</xsd:appinfo>
			</xsd:annotation>
		</xsd:enumeration>
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
		<xsl:copy-of select="key('elements-by-id',$this/@base_Package, $root)"/>
	</xsl:function>	

	<xsl:function name="f:application-schema.target-namespace" as="xsd:string">
		<xsl:param name="this" as="element()"/>
		<xsl:variable name="package" select="key('elements-by-id',$this/@base_Package, $root)"/>
		<xsl:choose>
			<xsl:when test="$this/@targetNamespace">
				<xsl:value-of	select="$this/@targetNamespace" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of	select="lower-case(concat('http://organization.org/', translate($package/@name, ' ', '_')))" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
	<xsl:function name="f:application-schema.xmlns" as="xsd:string">
		<xsl:param name="this" as="element()"/>
		<xsl:variable name="package" select="key('elements-by-id',$this/@base_Package, $root)"/>
		<xsl:choose>
			<xsl:when test="$this/@xmlns">
				<xsl:value-of	select="$this/@xmlns" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of	select="lower-case(substring($package/@name, 0, 3))" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<xsl:function name="f:application-schema.version" as="xsd:string">
		<xsl:param name="this" as="element()"/>
		<xsl:variable name="package" select="key('elements-by-id',$this/@base_Package, $root)"/>
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
		<xsl:variable name="package" select="key('elements-by-id',$this/@base_Package, $root)"/>
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
		<xsl:variable name="package" select="key('elements-by-id',$this/@base_Package, $root)"/>
		<xsl:choose>
			<xsl:when test="$this/@xsdDocument">
				<xsl:value-of	select="$this/@xsdDocument" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of	select="concat($package/@name,'.xsd')" />
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
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:value-of select="if ($root//*:Application_Schema[@base_Package=$this/@xmi:id]) then true() else false()" />
	</xsl:function>

	<xsl:function name="f:package.get-application-schema" as="element()">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:copy-of select="$root//*:Application_Schema[@base_Package=$this/@xmi:id]"/>		
	</xsl:function>

	<xsl:function name="f:package.get-supplier-packages"  as="element(packagedElement)*">
		<xsl:param name="this" as="element(packagedElement)"/>
		<xsl:copy-of select="key('elements-by-id',$root//*[@xmi:type='uml:Dependency' and client/@xmi:idref=$this/@xmi:id]/supplier/@xmi:idref, $root)"/>
	</xsl:function>

	<!-- = class type functions ======================================================== -->

	<xsl:function name="f:class.is-data-type" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:value-of select="count($root//*:DataType[@base_Class=$this/@xmi:id])>0" />
	</xsl:function>
	
	<xsl:function name="f:class.is-feature-type" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement)" />
		<!--xsl:value-of select="count($root//*:FeatureType[@base_Class=$this/@xmi:id])>0" /-->
            <!-- mdv TUD -->
		<xsl:value-of select="boolean($root//*:FeatureType[@base_Class=$this/@xmi:id]|$root//*:featureType[@base_Class=$this/@xmi:id] )" />
	</xsl:function>

	<xsl:function name="f:class.is-object-type" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:value-of select="count($root//*:Type[@base_Class=$this/@xmi:id])>0 or count($root//*[@base_Class=$this/@xmi:id])=0" />
	</xsl:function>

	<xsl:function name="f:class.is-enumeration" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement)" />
            <!-- mdv 2010 treat codelist in INSPIRE-based EA as enumeration temporarily -->
		<!--xsl:value-of select="count($root//*:Enumeration[@base_Enumeration=$this/@xmi:id])>0" /-->
		<xsl:value-of select="boolean($root//*:Enumeration[@base_Enumeration=$this/@xmi:id]|$root//*:codelist[@base_Class=$this/@xmi:id])" />
	</xsl:function>

	<xsl:function name="f:class.is-code-list" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement)" />
            <!-- mdv 2010 codelist/@base_Class in INSPIRE EA -->
		<xsl:value-of select="count($root//*:CodeList[@base_Enumeration=$this/@xmi:id])>0" />
	</xsl:function>
	
	<xsl:function name="f:class.is-union" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:value-of select="count($root//*:Union[@base_Class=$this/@xmi:id])>0" />
	</xsl:function>	
	
	<!-- = class functions ======================================================== -->
	
	<xsl:function name="f:class.name" as="xsd:string">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:value-of select="$this/@name"/>
	</xsl:function>

	<xsl:function name="f:class.type-name" as="xsd:string">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:value-of select="concat($this/@name, 'Type')"/>
	</xsl:function>
	
	<xsl:function name="f:class.property-type-name" as="xsd:string">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:value-of select="concat($this/@name, 'PropertyType')"/>
	</xsl:function>

	<xsl:function name="f:class.property-type-name-ref" as="xsd:string">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:choose>
			<xsl:when test="$this/@xmi:type='uml:Enumeration'">
				<xsl:value-of select="concat($this/f:class.get-application-schema(.)/f:application-schema.xmlns(.),':',$this/f:class.type-name(.))"/>
			</xsl:when>
			<xsl:when test="f:predefined-type.is-predefinied-type($this/@name)">
				<xsl:value-of select="f:predefined-type.get-property-type-name($this/@name)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat($this/f:class.get-application-schema(.)/f:application-schema.xmlns(.),':',$this/f:class.property-type-name(.))"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<xsl:function name="f:class.property-by-value-type-name" as="xsd:string">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:value-of select="concat($this/@name, 'PropertyByValueType')"/>
	</xsl:function>

	<xsl:function name="f:class.get-application-schema" as="element()?">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:copy-of select="key('elements-by-id',$this/@xmi:id, $root)/ancestor::*[@xmi:type='uml:Package' and f:package.has-application-schema(.)]/f:package.get-application-schema(.)"/>
	</xsl:function>

	<xsl:function name="f:class.is-simple-type" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:value-of select="$this/@xmi:type='uml:PrimitiveType'"/>
	</xsl:function>

	<xsl:function name="f:class.has-base-class" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:value-of select="count($this/generalization)>0"/>
	</xsl:function>

	<xsl:function name="f:class.has-multiple-base-class" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:value-of select="count($this/generalization)>1"/>
	</xsl:function>

	<xsl:function name="f:class.base-class"  as="element(packagedElement)?">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:copy-of select="key('elements-by-id',$this/generalization/@general, $root)"/>
	</xsl:function>
	
	<xsl:function name="f:class.is-abstract" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:value-of select="$this/@isAbstract='true'"/>
	</xsl:function>

	<!-- = class metadata functions ======================================================== -->

	<xsl:function name="f:class.has-property-definition" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:value-of select="if (not($root//*[@base_Class=$this/@xmi:id]/@noPropertyType) or $root//*[@base_Class=$this/@xmi:id]/@noPropertyType='false') then true() else false()" />
	</xsl:function>
	
	<xsl:function name="f:class.has-value-property-definition" as="xsd:boolean">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:value-of select="$root//*[@base_Class=$this/@xmi:id]/@byValuePropertyType='true'" />
	</xsl:function>
	
	<!-- = feature type functions ======================================================== -->
	
	<xsl:function name="f:feature-type.substitution-group" as="xsd:string">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:choose>
			<xsl:when test="$this/f:class.has-base-class(.)">
				<xsl:value-of select="concat($this/f:class.base-class(.)/f:class.get-application-schema(.)/f:application-schema.xmlns(.),':',$this/f:class.base-class(.)/f:class.name(.))"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'gml:AbstractFeature'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<xsl:function name="f:feature-type.base" as="xsd:string">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:choose>
			<xsl:when test="$this/f:class.has-base-class(.)">
				<xsl:value-of select="concat($this/f:class.base-class(.)/f:class.get-application-schema(.)/f:application-schema.xmlns(.),':',$this/f:class.base-class(.)/f:class.type-name(.))"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'gml:AbstractFeatureType'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<!-- = enumeration name functions ======================================================== -->

	<xsl:function name="f:enumeration.literal-name" as="xsd:string">
		<xsl:param name="this" as="element(ownedLiteral)" />
		<xsl:value-of select="$this/@name"/>
	</xsl:function>
	
	<xsl:function name="f:enumeration.type-name" as="xsd:string">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:value-of select="concat($this/@name, 'EnumerationType')"/>
	</xsl:function>

	<xsl:function name="f:enumeration.other-type-name" as="xsd:string">
		<xsl:param name="this" as="element(packagedElement)" />
		<xsl:value-of select="concat($this/@name, 'OtherType')"/>
	</xsl:function>


	<!-- = property functions ======================================================== -->

	<xsl:function name="f:property.name" as="xsd:string">
		<xsl:param name="this" as="element(ownedAttribute)"/>
		<xsl:value-of select="$this/@name"/>
	</xsl:function>

	<!--xsl:function name="f:property.type" as="element()">
		<xsl:param name="this" as="element(ownedAttribute)"/>
		<xsl:copy-of select="key('elements-by-id',$this/@type, $root)"/>
	</xsl:function-->

	<xsl:function name="f:property.type" as="element()">
		<xsl:param name="refto" as="xsd:string"/>
		<xsl:copy-of select="key('elements-by-id',$refto, $root)"/>
	</xsl:function>


  <!-- temp debug mdv 2010 -->
  <xsl:template name="getUMLdatatypeId" >
	<xsl:param name="this" />

      <xsl:choose>

         <!-- magicdraw -->
         <xsl:when test="boolean($this/@type)" >
           <xsl:value-of select="$this/@type" />
         </xsl:when>

         <xsl:when test="$this/type[@xmi:type='uml:PrimitiveType']" >
             <!-- typename is inline, no ref needed, but TODO try here: ref to itself -->
         </xsl:when>

         <xsl:when test="$this/type[@xmi:type='uml:DataType']" >
           <xsl:value-of select="substring-after($this/type/@href, '#')" />
         </xsl:when>

         <!-- ea -->
         <xsl:when test="boolean($this/type/@xmi:idref)" >
           <xsl:value-of select="$this/type/@xmi:idref" />
         </xsl:when>

         <!-- both ? -->
         <xsl:otherwise>
           <!-- refers to //attribute/properties/@type TODO check -->
           <!--xsl:value-of select="$this/@xmi:id" /-->
         </xsl:otherwise>

      </xsl:choose>

  </xsl:template> 


	<xsl:function name="f:property.lower" as="xsd:integer">
		<xsl:param name="this" as="element(ownedAttribute)"/>
		<xsl:choose>
			<xsl:when test="$this/@lowerValue">
				<xsl:value-of select="$this/@lowerValue"/>
			</xsl:when>
			<xsl:when test="$this/lowerValue">
				<xsl:value-of select="$this/lowerValue/@value"/>
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



	<!-- = predefined type functions ======================================================== -->
	
	<xsl:key name="type-by-uml-type" match="//data:type" use="data:uml-type" />

	<xsl:function name="f:predefined-type.is-predefinied-type" as="xsd:boolean">
		<xsl:param name="type-name" as="xsd:string" />
		<xsl:value-of select="count(key('type-by-uml-type', $type-name, document('')))!=0"/>		
	</xsl:function>
	
	<xsl:function name="f:predefined-type.get-element-name" as="xsd:string">
		<xsl:param name="type-name" as="xsd:string" />
		<xsl:value-of select="key('type-by-uml-type', $type-name, document(''))/data:gml-element"/>
	</xsl:function>
	
	<xsl:function name="f:predefined-type.get-type-name" as="xsd:string">
		<xsl:param name="type-name" as="xsd:string" />
		<xsl:value-of select="key('type-by-uml-type', $type-name, document(''))/data:gml-type"/>
	</xsl:function>

	<xsl:function name="f:predefined-type.get-property-type-name" as="xsd:string">
		<xsl:param name="type-name" as="xsd:string" />		
		<xsl:value-of select="key('type-by-uml-type', $type-name, document(''))/data:gml-property-type"/>
	</xsl:function>

	<data:type-map>
		<data:type>
			<data:uml-type>GM_Object</data:uml-type>
			<data:gml-element>gml:AbstractGeometry</data:gml-element>
			<data:gml-type>gml:AbstractGeometryType</data:gml-type>
			<data:gml-property-type>gml:GeometryPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Primitive</data:uml-type>
			<data:gml-element>gml:AbstractGeometricPrimtive</data:gml-element>
			<data:gml-type>gml:AbstractGeometricPrimtiveType</data:gml-type>
			<data:gml-property-type>gml:GeometricPrimtivePropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>DirectPosition</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>gml:DirectPositionType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Position</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type isGroup="true">gml:geometricPositionGroup</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_PointArray</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type isGroup="true">gml:geometricPositionListGroup</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Point</data:uml-type>
			<data:gml-element>gml:Point</data:gml-element>
			<data:gml-type>gml:PointType</data:gml-type>
			<data:gml-property-type>gml:PointPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Curve</data:uml-type>
			<data:gml-element>gml:Curve</data:gml-element>
			<data:gml-type>gml:CurveType</data:gml-type>
			<data:gml-property-type>gml:CurvePropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Surface</data:uml-type>
			<data:gml-element>gml:Surface</data:gml-element>
			<data:gml-type>gml:SurfaceType</data:gml-type>
			<data:gml-property-type>gml:SurfacePropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_PolyhedralSurface</data:uml-type>
			<data:gml-element>gml:PolyhedralSurface</data:gml-element>
			<data:gml-type>gml:PolyhedralSurfaceType</data:gml-type>
			<data:gml-property-type isAnonymous="true"></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_TriangulatedSurface</data:uml-type>
			<data:gml-element>gml:TriangulatedSurface</data:gml-element>
			<data:gml-type>gml:TriangulatedSurfaceType</data:gml-type>
			<data:gml-property-type isAnonymous="true"></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Tin</data:uml-type>
			<data:gml-element>gml:Tin</data:gml-element>
			<data:gml-type>gml:TinType</data:gml-type>
			<data:gml-property-type isAnonymous="true"></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Solid</data:uml-type>
			<data:gml-element>gml:Solid</data:gml-element>
			<data:gml-type>gml:SolidType</data:gml-type>
			<data:gml-property-type>gml:SolidPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_OrientableCurve</data:uml-type>
			<data:gml-element>gml:OrientableCurve</data:gml-element>
			<data:gml-type>gml:OrientableCurveType</data:gml-type>
			<data:gml-property-type>gml:CurvePropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_OrientableSurface</data:uml-type>
			<data:gml-element>gml:OrientableSurface</data:gml-element>
			<data:gml-type>gml:OrientableSurfaceType</data:gml-type>
			<data:gml-property-type>gml:SurfacePropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Ring</data:uml-type>
			<data:gml-element>gml:Ring</data:gml-element>
			<data:gml-type>gml:RingType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Shell</data:uml-type>
			<data:gml-element>gml:Shell</data:gml-element>
			<data:gml-type>gml:ShellType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type></data:uml-type>
			<data:gml-element>gml:LineString</data:gml-element>
			<data:gml-type>gml:LineStringType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type></data:uml-type>
			<data:gml-element>gml:Polygon</data:gml-element>
			<data:gml-type>gml:PolygonType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type></data:uml-type>
			<data:gml-element>gml:LinearRing</data:gml-element>
			<data:gml-type>gml:LinearRingType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_CompositePoint</data:uml-type>
			<data:gml-element>gml:Point</data:gml-element>
			<data:gml-type>gml:PointType</data:gml-type>
			<data:gml-property-type>gml:PointPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_CompositeCurve</data:uml-type>
			<data:gml-element>gml:CompositeCurve</data:gml-element>
			<data:gml-type>gml:CompositeCurveType</data:gml-type>
			<data:gml-property-type isAnonymous="true"></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_CompositeSurface</data:uml-type>
			<data:gml-element>gml:CompositeSurface</data:gml-element>
			<data:gml-type>gml:CompositeSurfaceType</data:gml-type>
			<data:gml-property-type isAnonymous="true"></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_CompositeSolid</data:uml-type>
			<data:gml-element>gml:CompositeSolid</data:gml-element>
			<data:gml-type>gml:CompositeSolidType</data:gml-type>
			<data:gml-property-type isAnonymous="true"></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Complex</data:uml-type>
			<data:gml-element>gml:GeometricComplex</data:gml-element>
			<data:gml-type>gml:GeometricComplexType</data:gml-type>
			<data:gml-property-type>gml:GeometricComplexPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Aggregate</data:uml-type>
			<data:gml-element>gml:MultiGeometry</data:gml-element>
			<data:gml-type>gml:MultiGeometryType</data:gml-type>
			<data:gml-property-type>gml:MultiGeometryPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_MultiPoint</data:uml-type>
			<data:gml-element>gml:MultiPoint</data:gml-element>
			<data:gml-type>gml:MultiPointType</data:gml-type>
			<data:gml-property-type>gml:MultiPointPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_MultiCurve</data:uml-type>
			<data:gml-element>gml:MultiCurve</data:gml-element>
			<data:gml-type>gml:MultiCurveType</data:gml-type>
			<data:gml-property-type>gml:MultiCurvePropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_MultiSurface</data:uml-type>
			<data:gml-element>gml:MultiSurface</data:gml-element>
			<data:gml-type>gml:MultiSurfaceType</data:gml-type>
			<data:gml-property-type>gml:MultiSurfacePropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_MultiSolid</data:uml-type>
			<data:gml-element>gml:MultiSolid</data:gml-element>
			<data:gml-type>gml:MultiSolidType</data:gml-type>
			<data:gml-property-type>gml:MultiSolidPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_MultiPrimitive</data:uml-type>
			<data:gml-element>gml:MultiGeometry</data:gml-element>
			<data:gml-type>gml:MultiGeometryType</data:gml-type>
			<data:gml-property-type>gml:MultiGeometryPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_CurveSegment</data:uml-type>
			<data:gml-element>gml:AbstractCurveSegment</data:gml-element>
			<data:gml-type>gml:AbstractCurveSegmentType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Arc</data:uml-type>
			<data:gml-element>gml:Arc</data:gml-element>
			<data:gml-type>gml:ArcType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_ArcByBulge</data:uml-type>
			<data:gml-element>gml:ArcByBulge</data:gml-element>
			<data:gml-type>gml:ArcByBulgeType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type></data:uml-type>
			<data:gml-element>gml:ArcByCenterPoint</data:gml-element>
			<data:gml-type>gml:ArcByCenterPointType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_ArcString</data:uml-type>
			<data:gml-element>gml:ArcString</data:gml-element>
			<data:gml-type>gml:ArcStringType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_ArcStringByBulge</data:uml-type>
			<data:gml-element>gml:ArcStringByBulge</data:gml-element>
			<data:gml-type>gml:ArcStringByBulgeType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Bezier</data:uml-type>
			<data:gml-element>gml:Bezier</data:gml-element>
			<data:gml-type>gml:BezierType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_BsplineCurve</data:uml-type>
			<data:gml-element>gml:BSpline</data:gml-element>
			<data:gml-type>gml:BSplineType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Circle</data:uml-type>
			<data:gml-element>gml:Circle</data:gml-element>
			<data:gml-type>gml:CircleType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type></data:uml-type>
			<data:gml-element>gml:CircleByCenterPoint</data:gml-element>
			<data:gml-type>gml:CircleByCenterPointType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Clothoid</data:uml-type>
			<data:gml-element>gml:Clothoid</data:gml-element>
			<data:gml-type>gml:ClothoidType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_CubicSpline</data:uml-type>
			<data:gml-element>gml:CubicSpline</data:gml-element>
			<data:gml-type>gml:CubicSplineType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_GeodesicString</data:uml-type>
			<data:gml-element>gml:GeodesicString</data:gml-element>
			<data:gml-type>gml:GeodesicStringType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_LineString</data:uml-type>
			<data:gml-element>gml:LineStringSegment</data:gml-element>
			<data:gml-type>gml:LineStringSegmentType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_OffsetCurve</data:uml-type>
			<data:gml-element>gml:OffsetCurve</data:gml-element>
			<data:gml-type>gml:OffsetCurveType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_SurfacePatch</data:uml-type>
			<data:gml-element>gml:AbstractSurfacePatch</data:gml-element>
			<data:gml-type>gml:AbstractSurfacePatchType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_GriddedSurface</data:uml-type>
			<data:gml-element>gml:AbstractGriddedSurface</data:gml-element>
			<data:gml-type>gml:AbstractGriddedSurfaceType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_ParametricCurveSurface</data:uml-type>
			<data:gml-element>gml:AbstractParametricCurveSurface</data:gml-element>
			<data:gml-type>gml:AbstractParametricCurveSurfaceType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Cone</data:uml-type>
			<data:gml-element>gml:Cone</data:gml-element>
			<data:gml-type>gml:ConeType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Cylinder</data:uml-type>
			<data:gml-element>gml:Cylinder</data:gml-element>
			<data:gml-type>gml:CylinderType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Geodesic</data:uml-type>
			<data:gml-element>gml:Geodesic</data:gml-element>
			<data:gml-type>gml:GeodesicType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Polygon</data:uml-type>
			<data:gml-element>gml:PolygonPatch</data:gml-element>
			<data:gml-type>gml:PolygonPatchType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type></data:uml-type>
			<data:gml-element>gml:Rectangle</data:gml-element>
			<data:gml-type>gml:RectangleType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Sphere</data:uml-type>
			<data:gml-element>gml:Sphere</data:gml-element>
			<data:gml-type>gml:SphereType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GM_Triangle</data:uml-type>
			<data:gml-element>gml:Triangle</data:gml-element>
			<data:gml-type>gml:TriangleType</data:gml-type>
			<data:gml-property-type></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TP_Object</data:uml-type>
			<data:gml-element>gml:AbstractTopology</data:gml-element>
			<data:gml-type>gml:AbstractTopologyType</data:gml-type>
			<data:gml-property-type isAnonymous="true"></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TP_Node</data:uml-type>
			<data:gml-element>gml:Node</data:gml-element>
			<data:gml-type>gml:NodeType</data:gml-type>
			<data:gml-property-type>gml:DirectedNodePropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TP_Edge</data:uml-type>
			<data:gml-element>gml:Edge</data:gml-element>
			<data:gml-type>gml:EdgeType</data:gml-type>
			<data:gml-property-type>gml:DirectedEdgePropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TP_Face</data:uml-type>
			<data:gml-element>gml:Face</data:gml-element>
			<data:gml-type>gml:FaceType</data:gml-type>
			<data:gml-property-type>gml:DirectedFacePropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TP_Solid</data:uml-type>
			<data:gml-element>gml:TopoSolid</data:gml-element>
			<data:gml-type>gml:TopoSolidType</data:gml-type>
			<data:gml-property-type>gml:DirectedTopoSolidPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TP_DirectedNode</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>gml:DirectedNodePropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TP_DirectedEdge</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>gml:DirectedEdgePropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TP_DirectedFace</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>gml:DirectedFacePropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TP_DirectedSolid</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>gml:DirectedTopoSolidPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TP_Complex</data:uml-type>
			<data:gml-element>gml:TopoComplex</data:gml-element>
			<data:gml-type>gml:TopoComplexType</data:gml-type>
			<data:gml-property-type>gml:TopoComplexPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type></data:uml-type>
			<data:gml-element>gml:TopoPoint</data:gml-element>
			<data:gml-type>gml:TopoPointType</data:gml-type>
			<data:gml-property-type>gml:TopoPointPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type></data:uml-type>
			<data:gml-element>gml:TopoCurve</data:gml-element>
			<data:gml-type>gml:TopoCurveType</data:gml-type>
			<data:gml-property-type>gml:TopoCurvePropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type></data:uml-type>
			<data:gml-element>gml:TopoSurface</data:gml-element>
			<data:gml-type>gml:TopoSurfaceType</data:gml-type>
			<data:gml-property-type>gml:TopoSurfacePropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type></data:uml-type>
			<data:gml-element>gml:TopoVolume</data:gml-element>
			<data:gml-type>gml:TopoVolumeType</data:gml-type>
			<data:gml-property-type>gml:TopoVolumePropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_Object</data:uml-type>
			<data:gml-element>gml:AbstractTimeObject</data:gml-element>
			<data:gml-type>gml:AbstractTimeObjectType</data:gml-type>
			<data:gml-property-type isAnonymous="true"></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_Complex</data:uml-type>
			<data:gml-element>gml:AbstractTimeComplex</data:gml-element>
			<data:gml-type>gml:AbstractTimeComplexType</data:gml-type>
			<data:gml-property-type isAnonymous="true"></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_GeometricPrimitive</data:uml-type>
			<data:gml-element>gml:AbstractTimeGeometricPrimtive</data:gml-element>
			<data:gml-type>gml:AbstractTimeGeometricPrimtiveType</data:gml-type>
			<data:gml-property-type>gml:TimeGeometricPrimtivePropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_Instant</data:uml-type>
			<data:gml-element>gml:TimeInstant</data:gml-element>
			<data:gml-type>gml:TimeInstantType</data:gml-type>
			<data:gml-property-type>gml:TimeInstantPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_Period</data:uml-type>
			<data:gml-element>gml:TimePeriod</data:gml-element>
			<data:gml-type>gml:TimePeriodType</data:gml-type>
			<data:gml-property-type>gml:TimePeriodPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_TopologicalComplex</data:uml-type>
			<data:gml-element>gml:TimeTopologyComplex</data:gml-element>
			<data:gml-type>gml:TimeTopologyComplexType</data:gml-type>
			<data:gml-property-type>gml:TimeTopologyComplexPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_TopologicalPrimitive</data:uml-type>
			<data:gml-element>gml:AbstractTimeTopologyPrimtive</data:gml-element>
			<data:gml-type>gml:AbstractTimeTopologyPrimtiveType</data:gml-type>
			<data:gml-property-type>gml:TimeTopologyPrimtivePropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_Node</data:uml-type>
			<data:gml-element>gml:TimeNode</data:gml-element>
			<data:gml-type>gml:TimeNodeType</data:gml-type>
			<data:gml-property-type>gml:TimeNodePropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_Edge</data:uml-type>
			<data:gml-element>gml:TimeEdge</data:gml-element>
			<data:gml-type>gml:TimeEdgeType</data:gml-type>
			<data:gml-property-type>gml:TimeEdgePropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_PeriodDuration</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>gml:duration (property element), xsd:duration</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_IntervalLength</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type isGroup="true">gml:timeInterval</data:gml-property-type>
			<data:gml-property-type>gml:TimeIntervalLengthType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_Duration</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type isGroup="true">gml:timeLength</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_Position</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>gml:TimePositionType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_IndeterminateValue</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>@TimeIndeterminateValue</data:gml-property-type> <!--(attribute on TimePositionType)-->
		</data:type>
		<data:type>
			<data:uml-type>TM_Coordinate</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>xsd:decimal</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_CalDate</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>gml:CalDate</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_ClockTime</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>xsd:time</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_DateAndTime</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>xsd:dateTime</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_Calendar</data:uml-type>
			<data:gml-element>gml:TimeCalendar</data:gml-element>
			<data:gml-type>gml:TimeCalendarType</data:gml-type>
			<data:gml-property-type>gml:TimeCalendarPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_CalendarEra</data:uml-type>
			<data:gml-element>gml:TimeCalendarEra</data:gml-element>
			<data:gml-type>gml:TimeCalendarEraType</data:gml-type>
			<data:gml-property-type>gml:TimeCalendarEraPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_Clock</data:uml-type>
			<data:gml-element>gml:TimeClock</data:gml-element>
			<data:gml-type>gml:TimeClockType</data:gml-type>
			<data:gml-property-type>gml:TimeClockPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_CoordinateSystem</data:uml-type>
			<data:gml-element>gml:TimeCoordinateSystem</data:gml-element>
			<data:gml-type>gml:TimeCoordinateSystemType</data:gml-type>
			<data:gml-property-type isAnonymous="true"></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_OrdinalReferenceSystem</data:uml-type>
			<data:gml-element>gml:TimeOrdinalReferenceSystem</data:gml-element>
			<data:gml-type>gml:TimeOrdinalReferenceSystemType</data:gml-type>
			<data:gml-property-type isAnonymous="true"></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>TM_OrdinalEra</data:uml-type>
			<data:gml-element>gml:TimeOrdinalEra</data:gml-element>
			<data:gml-type>gml:TimeOrdinalEraType</data:gml-type>
			<data:gml-property-type>gml:TimeOrdinalEraPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>SC_CRS</data:uml-type>
			<data:gml-element>gml:AbstractCRS</data:gml-element>
			<data:gml-type>gml:AbstractCRSType</data:gml-type>
			<data:gml-property-type>gml:CRSPropertyType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>SI_LocationInstance</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>gml:LocationName</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>CV_Coverage</data:uml-type>
			<data:gml-element>gml:AbstractCoverage</data:gml-element>
			<data:gml-type>gml:AbstractCoverageType</data:gml-type>
			<data:gml-property-type isAnonymous="true"></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>CV_ContinuousCoverage</data:uml-type>
			<data:gml-element>gml:AbstractContinuousCoverage</data:gml-element>
			<data:gml-type>gml:AbstractContinuousCoverageType</data:gml-type>
			<data:gml-property-type isAnonymous="true"></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>CV_DiscreteCoverage</data:uml-type>
			<data:gml-element>gml:AbstractDiscreteCoverage</data:gml-element>
			<data:gml-type>gml:DiscreteCoverageType</data:gml-type>
			<data:gml-property-type isAnonymous="true"></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>CV_DiscretePointCoverage</data:uml-type>
			<data:gml-element>gml:MultiPointCoverage</data:gml-element>
			<data:gml-type>gml:MultiPointCoverageType</data:gml-type>
			<data:gml-property-type isAnonymous="true"></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>CV_DiscreteCurveCoverage</data:uml-type>
			<data:gml-element>gml:MultiCurveCoverage</data:gml-element>
			<data:gml-type>gml:MultiCurveCoverageType</data:gml-type>
			<data:gml-property-type isAnonymous="true"></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>CV_DiscreteSurfaceCoverage</data:uml-type>
			<data:gml-element>gml:MultiSurfaceCoverage</data:gml-element>
			<data:gml-type>gml:MultiSurfaceCoverageType</data:gml-type>
			<data:gml-property-type isAnonymous="true"></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>CV_DiscreteSolidCoverage</data:uml-type>
			<data:gml-element>gml:MultiSolidCoverage</data:gml-element>
			<data:gml-type>gml:MultiSolidCoverageType</data:gml-type>
			<data:gml-property-type isAnonymous="true"></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>CV_DiscreteGridPointCoverage</data:uml-type>
			<data:gml-element>gml:GridCoverage</data:gml-element>
			<data:gml-type>gml:GridCoverageType</data:gml-type>
			<data:gml-property-type isAnonymous="true"></data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>CharacterString</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>xsd:string</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>Boolean</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>xsd:boolean</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>Real, Number</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>xsd:double</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>Decimal</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>xsd:decimal</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>Date</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>xsd:date</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>Time</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>xsd:time</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>DateTime</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>xsd:dateTime</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>Integer</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>xsd:integer</data:gml-property-type>
			<data:gml-property-type>xsd:nonPositiveInteger</data:gml-property-type>
			<data:gml-property-type>xsd:negativeInteger</data:gml-property-type>
			<data:gml-property-type>xsd:nonNegativeInteger</data:gml-property-type>
			<data:gml-property-type>xsd:positiveInteger</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>Vector</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>gml:VectorType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>GenericName</data:uml-type>
			<data:uml-type>LocalName</data:uml-type>
			<data:uml-type>ScopeName</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>gml:CodeType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>Length</data:uml-type>
			<data:uml-type>Distance</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>gml:LengthType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>Angle</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>gml:AngleType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>Speed</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>gml:SpeedType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>Scale</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>gml:ScaleType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>Area</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>gml:AreaType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>Volume</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>gml:VolumeType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>Measure</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>gml:MeasureType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>Sign</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>gml:SignType</data:gml-property-type>
		</data:type>
		<data:type>
			<data:uml-type>UnitOfMeasure</data:uml-type>
			<data:gml-element></data:gml-element>
			<data:gml-type></data:gml-type>
			<data:gml-property-type>gml:UnitOfMeasureType</data:gml-property-type>
		</data:type>
	</data:type-map>

</xsl:stylesheet>