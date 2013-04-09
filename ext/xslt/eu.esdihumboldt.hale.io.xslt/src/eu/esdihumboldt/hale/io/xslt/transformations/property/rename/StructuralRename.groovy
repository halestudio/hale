/*
 * Copyright (c) 2013 Fraunhofer IGD
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.transformations.property.rename

import com.google.common.collect.ListMultimap

import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.CellUtil
import eu.esdihumboldt.hale.common.align.model.functions.RenameFunction
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.io.gml.writer.internal.GmlWriterUtil
import eu.esdihumboldt.hale.io.xslt.GroovyXslHelpers
import eu.esdihumboldt.hale.io.xslt.XsltGenerationContext
import eu.esdihumboldt.hale.io.xslt.functions.XslFunction
import eu.esdihumboldt.hale.io.xslt.functions.XslVariable
import groovy.xml.MarkupBuilder


/**
 * Structural rename function. Each instance should only be used once.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
class StructuralRename implements XslFunction, RenameFunction {

	/**
	 * The parameter source for a structural rename template.
	 */
	private static final String T_PARAM_SOURCE = 'source'

	/**
	 * If namespaces should be ignored when checking for similarity of source
	 * and target.
	 */
	boolean ignoreNamespaces
	
	/**
	 * States if using xsl:copy-of is allowed.
	 */
	final boolean allowCopyOf = true;

	/**
	 * The XSLT generation context needed for adding XSL templates.
	 */
	XsltGenerationContext xsltContext

	/**
	 * Associates source and target with the corresponding template name.
	 */
	final templates = [:]

	@Override
	public String getSequence(Cell cell, ListMultimap<String, XslVariable> variables,
			XsltGenerationContext xsltContext, Cell typeCell) {
		this.xsltContext = xsltContext;

		use (CellUtil, DefinitionUtil) {
			ignoreNamespaces = cell.getOptionalParameter(
					PARAMETER_IGNORE_NAMESPACES, Value.of(false)).as(Boolean);

			XslVariable sourceVar = variables.get(null)[0]

			def source = sourceVar.entity.definition.getDefinitionGroup()
			def target = cell.target.get(null)[0].definition.definition.getDefinitionGroup()

			if (useCopyOf(source, target)) {
				// copy attributes, elements and text (order matters)
				"""<xsl:copy-of select="${sourceVar.XPath}/@*, ${sourceVar.XPath}/child::node()"/>"""
			}
			else {
				// generate copy templates recursively
				String template = generateTemplate(source, target)
	
				// call the base template
				"""
				<xsl:call-template name="$template">
					<xsl:with-param name="$T_PARAM_SOURCE" select="${sourceVar.XPath}" />
				</xsl:call-template>
				"""
			}
		}
	}

	/**
	 * Generate a XSL template copying properties with the same name from
	 * source to target, additionally to an eventual direct value.
	 * 
	 * @param source the source group
	 * @param target the target group
	 * @return the name of the template
	 */
	private String generateTemplate(DefinitionGroup source, DefinitionGroup target) {
		def key = [source, target]
		// check if a corresponding template has already been created
		String template = templates[key]
		if (template) {
			// return the name of the existing template
			return template
		}

		String templateName = xsltContext.reserveTemplateName(
				"structuralRename_${source.name.localPart}_${target.name.localPart}")
		// store the template name for this pair
		templates[key] = templateName

		use(GroovyXslHelpers, DefinitionUtil) {
			xsltContext.addInclude().output.withWriter('UTF-8'){ w ->
				def xsl = new MarkupBuilder(w)
				xsl.'xsl:template'(name: templateName) {
					'xsl:param'(name: T_PARAM_SOURCE)
					// go through target children
					// first all attributes
					for (PropertyDefinition child in target.getAllProperties().findAll{it.isAttribute()}) {
						// generate attribute if possible
						generateAttribute(xsl, child, source)
					}
					// then through all elements
					for (PropertyDefinition child in target.getAllProperties().findAll{!it.isAttribute()}) {
						/*
						 * FIXME Going through the properties like this has a problem:
						 * The target structure eventually may not be build correctly!
						 * Choices, repeated groups!
						 */
						PropertyDefinition sourceMatch = findMatch(child, source, ignoreNamespaces)
						if (sourceMatch) {
							// determine source XPath
							String selectSource = '$' + T_PARAM_SOURCE + '/' + sourceMatch.asXPath(xsltContext);
							
							if (sourceMatch == child && useCopyOf(sourceMatch.propertyType, child.propertyType)) {
								// do a deep copy
								'xsl:copy-of'(select: selectSource)
							}
							else {
								//TODO restrict selection cardinality?
								// copy using template
								'xsl:for-each'(select: selectSource) {
									// target is an element
									'xsl:element'(child.name.asMap(xsltContext)) {
										if (child.hasChildren()) {
											// only use the template if the element has children of its own
											String subTemplate = generateTemplate(
													sourceMatch.getDefinitionGroup(), child.getDefinitionGroup())
	
											'xsl:call-template'(name: subTemplate) {
												'xsl:with-param'(name: T_PARAM_SOURCE, select: '.')
											}
										}
										else if (child.hasValue() && sourceMatch.hasValue()) {
											// value copy is possible
											'xsl:value-of'(select: '.')
										}
										else {
											//TODO warn?
										}
									}
								}
							}
						}
					}

					// handle eventual value
					if (source.hasValue() && target.hasValue()) {
						//XXX a condition needed if the source actually has a value?
						'xsl:value-of'(select: '$' + T_PARAM_SOURCE)
					}
				}
			}
		}

		return templateName
	}
	
	private boolean useCopyOf(DefinitionGroup source, DefinitionGroup target) {
		//XXX use copy-of if allowed and types match
		return allowCopyOf && source == target
	}

	/**
	 * Generate a target attribute.
	 * 	
	 * @param xsl the XSL builder
	 * @param target the target attribute definition
	 * @param sourceParent the source parent definition
	 */
	private void generateAttribute(def xsl, PropertyDefinition target, DefinitionGroup sourceParent) {
		// find match for target in source
		PropertyDefinition sourceMatch = findMatch(target, sourceParent, ignoreNamespaces)
		if (sourceMatch) {
			// a match exists
			
			// determine source XPath
			String selectSource = '$' + T_PARAM_SOURCE + '/' + sourceMatch.asXPath(xsltContext);
			
			//TODO restrict selection cardinality?
			
			// determine if this is a required attribute
			def required = target.getCardinality().minOccurs > 0
			
			if (target.hasValue() && sourceMatch.hasValue()) {
				// value copy is possible
				
				// XPath that determines if the source has a value
				def testSource = sourceMatch.isAttribute() ? selectSource : "${selectSource}/text()"
				
				// if the source is there, copy the value
				xsl.'xsl:if'(test: testSource) {
					'xsl:attribute'(target.name.asMap(xsltContext)) { 'xsl:value-of'(select: selectSource) }
				}
				
				if (required) {
					// if the source is not there, warn or use default
					xsl.'xsl:if'(test: "not($testSource)") {
						// try to determine default value
						def defaultValue = generateDefaultValue(target)
						if (defaultValue) {
							if (defaultValue instanceof Closure) {
								'xsl:attribute'(target.name.asMap(xsltContext)) {
									defaultValue(xsl)
								}
							}
							else {
								'xsl:attribute'(target.name.asMap(xsltContext), defaultValue)
							}
						}
						else {
							xsl.'xsl:message'("Could not provide a value for required attribute $target.name.localPart")
						}
					}
				}
				
			}
			else {
				//TODO warn through reporter?
			}
		}
	}
	
	/**
	 * Generate a default value for the given mandatory target property.
	 * 
	 * @param target the property definition
	 * @return a value, a closure taking the XSL builder as argument, or
	 *   <code>null</code>
	 */
	private def generateDefaultValue(PropertyDefinition target) {
		// special case: mandatory id
		if (target.isAttribute() && GmlWriterUtil.isID(target.getPropertyType())) {
			return {
				xsl ->
				xsl.'xsl:value-of'(select: 'generate-id()')
			}
		}
		
		null
	}

	/**
	 * Find a match to the target property in the source group, based on the
	 * name.
	 * 
	 * @param target the target property
	 * @param sourceParent the source group
	 * @param ignoreNamespace if the namespace may be ignored 
	 * @return the source property that was found having a matching name
	 */
	private PropertyDefinition findMatch(PropertyDefinition target, DefinitionGroup sourceParent,
		boolean ignoreNamespaces) {
		// best match is always the one with the exact same name
		def match = sourceParent.getChild(target.name)

		if (!match || !match.asProperty() && !ignoreNamespaces) {
			// look for strict matches in the contained groups
			for (group in sourceParent.getAllChildren().findAll{it.asGroup()}) {
				match = findMatch(target, group, false)
				if (match) break
			}
		}
		
		if (!match && ignoreNamespaces) {
			// also accept 'weak' matches
			//XXX for now, just take the first weak match
			for (candidate in sourceParent.getAllProperties()) {
				if (candidate.name.localPart == target.name.localPart) {
					match = candidate
					break
				}
			}
		}

		match
	}

}
