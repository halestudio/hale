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

package eu.esdihumboldt.hale.io.xslt.transformations.property;

import com.google.common.collect.ListMultimap

import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.functions.FormattedStringFunction
import eu.esdihumboldt.hale.io.xslt.functions.InlineFunction
import eu.esdihumboldt.hale.io.xslt.transformations.base.AbstractFunctionTransformation


/**
 * XSLT representation of the FormattedString function.
 * 
 * @author Simon Templer
 */
class XslFormattedString extends AbstractFunctionTransformation implements InlineFunction, FormattedStringFunction {

	@Override
	public String getSequence(Cell cell, ListMultimap<String, String> variables) {
		//XXX something like
		"""
		<xsl:value-of select="concat('text', @id, 'text2')" />
		"""
	}
}
