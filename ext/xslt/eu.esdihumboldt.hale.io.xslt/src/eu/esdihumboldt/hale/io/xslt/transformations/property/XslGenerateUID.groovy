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

package eu.esdihumboldt.hale.io.xslt.transformations.property

import com.google.common.collect.ListMultimap

import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.functions.GenerateUIDFunction
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition
import eu.esdihumboldt.hale.io.xslt.XsltGenerationContext
import eu.esdihumboldt.hale.io.xslt.functions.XslVariable
import eu.esdihumboldt.hale.io.xslt.transformations.base.AbstractFunctionTransformation


/**
 * XSLT implementation of the Generate Unique Id function.
 * 
 * @author Andrea Antonello 
 */
class XslGenerateUID extends AbstractFunctionTransformation implements GenerateUIDFunction {

	@Override
	public String getSequence(Cell cell, ListMultimap<String, XslVariable> variables,
			XsltGenerationContext context) {
		def target = cell.getTarget().get(null)[0];
		PropertyEntityDefinition d = target.getDefinition();
		def localName = d.getDefinition().getName().getLocalPart();
		def typeName = d.getType().getName().getLocalPart();

		def prefix = typeName + "_"+ localName

		"""
			$prefix<xsl:value-of select="position()" />_<xsl:value-of select="generate-id()" />
		"""
	}
}
