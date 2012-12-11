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

package eu.esdihumboldt.hale.io.xslt.transformations.property.rename;

import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.io.xslt.functions.InlineFunction


/**
 * TODO Type description
 * @author Simon Templer
 */
class RenameValue implements InlineFunction {

	@Override
	public String getSequence(Cell cell) {
		"""<xsl:value-of select="$sel" />""";

		// def sw = new StringWriter()
		// new MarkupBuilder(sw).'xsl:value'(select: $sel)
		// sw.toString()
	}
}
