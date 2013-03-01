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

package eu.esdihumboldt.hale.io.xslt.transformations.property.classification;

import com.google.common.collect.ListMultimap

import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.CellUtil
import eu.esdihumboldt.hale.common.align.model.functions.ClassificationMappingFunction
import eu.esdihumboldt.hale.common.align.model.functions.ClassificationMappingUtil
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition
import eu.esdihumboldt.hale.common.core.io.HaleIO
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag
import eu.esdihumboldt.hale.io.xslt.XsltGenerationContext
import eu.esdihumboldt.hale.io.xslt.functions.XslVariable
import eu.esdihumboldt.hale.io.xslt.transformations.base.AbstractFunctionTransformation
import groovy.xml.XmlUtil


/**
 * XSLT representation of the Mathematical Expression function.
 * 
 * @author Andrea Antonello
 */
class XslClassification extends AbstractFunctionTransformation implements ClassificationMappingFunction {

	@Override
	public String getSequence(Cell cell, ListMultimap<String, XslVariable> variables,
	XsltGenerationContext context) {

		def target = cell.getTarget().get(null)[0];
		PropertyEntityDefinition d = target.getDefinition();

		// get the property definition
		PropertyDefinition propertyDefinition = d.getDefinition()

		def var = variables.get(null)[0].XPath
		def setVar = """"""

		/*
		 * handle special cases of not maching class
		 */
		String notClassifiedAction = CellUtil.getOptionalParameter(cell,
				PARAMETER_NOT_CLASSIFIED_ACTION, Value.of(USE_NULL_ACTION)).as(String.class);

		// use source
		if (USE_SOURCE_ACTION.equals(notClassifiedAction))
			setVar = """
				<xsl:variable name="endVar" >
					<xsl:value-of select="$var"/>
				</xsl:variable>
			"""
		else
		// fixed value
		if (notClassifiedAction.startsWith(USE_FIXED_VALUE_ACTION_PREFIX)){

			def fixedValue = notClassifiedAction.substring(notClassifiedAction.indexOf(':') + 1);
			setVar = """
				<xsl:variable name="endVar" >
					<xsl:text>$fixedValue</xsl:text>
				</xsl:variable>
				"""
		} else
		// return null
		{
			if (propertyDefinition.asProperty().getConstraint(XmlAttributeFlag).enabled) {
				// TODO handling of nullability of attributes might be discussed in future
				setVar = """
						<xsl:variable name="endVar" >
						<xsl:text/>
						</xsl:variable>
						"""
			}
			else {
				// a mandatory element
				if (propertyDefinition.asProperty().getConstraint(NillableFlag).enabled) {
					// a nillable mandatory element
					setVar = """
						<xsl:variable name="endVar" >
							<xsl:attribute name="xsi:nil">true</xsl:attribute>
							<xsl:text/>
						</xsl:variable>
						"""
				}else{
					// not nillable element, then leave it empty
					setVar = """
						<xsl:variable name="endVar" >
						<xsl:text/>
						</xsl:variable>
						"""
				}
			}
		}


		def transformationParameters = cell.getTransformationParameters()
		/*
		 * FIXME currently the context is passed as null. 
		 * This means that the lookup table is embedded in each piece of xsl 
		 * per property. This will have to be improved for better 
		 * lookup table maintenance, maybe from a dedicated file. 
		 */
		def lookup = ClassificationMappingUtil.getClassificationLookup(transformationParameters,null);
		def complexElement = HaleIO.getComplexElement(lookup)

		def serializedElement = XmlUtil.serialize(complexElement);
		// remove the xml header... TODO could be done better?
		serializedElement = serializedElement.split("\\?\\>")[1]
		def lookupVar = """
			<xsl:variable name="lookup" >
				$serializedElement
			</xsl:variable>
			"""

		def check = """
				$setVar
				<xsl:variable name="checkVar" >
					<xsl:value-of select="."/>
				</xsl:variable>
                <xsl:variable name="testVar" select="\$lookup/lookup-table/entry[key/@value = \$checkVar]/value/@value"/>
                
                <xsl:if test="\$testVar">
                    <xsl:value-of select="\$testVar"/>
                </xsl:if>
                <xsl:if test="not(\$testVar)">
                    <xsl:value-of select="\$endVar"/>
                </xsl:if>
			"""

		"""
			$lookupVar

			$check
		"""
	}

	private String checkXsltOwn(String item){
		if (item.trim().equals("/")) {
			return " div "
		}
		if (item.trim().equals("%")) {
			return " mod "
		}

		return item
	}

}
