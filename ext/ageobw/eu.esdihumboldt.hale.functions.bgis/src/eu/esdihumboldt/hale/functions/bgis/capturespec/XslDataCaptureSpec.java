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

package eu.esdihumboldt.hale.functions.bgis.capturespec;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.io.xslt.XsltGenerationContext;
import eu.esdihumboldt.hale.io.xslt.functions.XslVariable;
import eu.esdihumboldt.hale.io.xslt.transformations.base.AbstractFunctionTransformation;

/**
 * XSL implementation of data capture specification function.
 * 
 * @author Simon Templer
 */
public class XslDataCaptureSpec extends AbstractFunctionTransformation {

	@Override
	public String getSequence(Cell cell, ListMultimap<String, XslVariable> variables,
			XsltGenerationContext xsltContext, Cell typeCell) {
		String specString = DataCaptureSpecUtil.getDataCaptureSpec(typeCell);

		if (specString != null) {
			StringBuilder builder = new StringBuilder();

			builder.append("<xsl:text>");
			builder.append(specString);
			builder.append("</xsl:text>");

			return builder.toString();
		}

		// otherwise a null result
		return "<def:null/>";
	}

}
