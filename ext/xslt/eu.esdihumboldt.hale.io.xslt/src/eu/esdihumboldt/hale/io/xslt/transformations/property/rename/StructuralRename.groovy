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
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.io.xslt.XsltGenerationContext
import eu.esdihumboldt.hale.io.xslt.functions.XslFunction
import eu.esdihumboldt.hale.io.xslt.functions.XslVariable
import groovy.xml.MarkupBuilder


/**
 * Structural rename function. Each instance should only be used once.
 * 
 * @author Simon Templer
 */
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
	 * The XSLT generation context needed for adding XSL templates.
	 */
	XsltGenerationContext xsltContext

	/**
	 * Associates source and target with the corresponding template name.
	 */
	final templates = [:]

	@Override
	public String getSequence(Cell cell, ListMultimap<String, XslVariable> variables,
	XsltGenerationContext xsltContext) {
		this.xsltContext = xsltContext;

		use (CellUtil, DefinitionUtil) {
			ignoreNamespaces = Boolean.parseBoolean(cell.getOptionalRawParameter(
					PARAMETER_IGNORE_NAMESPACES, 'false'));

			XslVariable sourceVar = variables.get(null)[0]

			def source = sourceVar.entity.definition.getDefinitionGroup()
			def target = cell.target.get(null)[0].definition.definition.getDefinitionGroup()

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

		use(DefinitionUtil) {
			xsltContext.addInclude().output.withWriter('UTF-8'){ w ->
				def xsl = new MarkupBuilder(w)
				xsl.'xsl:template'(name: templateName) {
					'xsl:param'(name: T_PARAM_SOURCE)
					//TODO go through target children
					for (ChildDefinition child in target.getAllChildren()) {
						ChildDefinition sourceMatch = findMatch(child, source)
						if (sourceMatch) {
							//TODO determine source XPath
							String select = 'TBD';
							// copy using template
							'xsl:for-each'(select: select) {
								String subTemplate = generateTemplate(
									sourceMatch.getDefinitionGroup(), child.getDefinitionGroup())
								
								//TODO wrap by xsl:attribute/element (though for attribute a value-of should suffice)
								'xsl:call-template'(name: subTemplate) {
									'xsl:with-param'(name: T_PARAM_SOURCE, select: '.')
								}
								//TODO wrap by xsl:attribute/element
							}
						}
					}
					
					//TODO handle eventual value
				}
			}
		}

		return templateName
	}
	
	/**
	 * Find a match to the target property in the source group, based on the
	 * name. Namespaces may be ignored if the cell was configured accordingly.
	 * 
	 * @param target the target property
	 * @param sourceParent the source group
	 * @return the source property that was found having a matching name
	 */
	private ChildDefinition findMatch(ChildDefinition target, DefinitionGroup sourceParent) {
		// best match is always the one with the exact same name
		def match = sourceParent.getChild(target.name)
		
		if (!match && ignoreNamespaces) {
			// also accept 'weak' matches
			//XXX for now, just take the first weak match
			for (candidate in sourceParent.getAllChildren()) {
				if (candidate.name.localPart == target.name.localPart) {
					match = candidate
					break
				}
			}
		}
		
		match
	}
	
}
