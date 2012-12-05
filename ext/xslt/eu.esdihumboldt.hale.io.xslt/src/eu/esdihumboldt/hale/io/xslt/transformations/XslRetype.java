/*
 * Copyright (c) 2012 Fraunhofer IGD
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

package eu.esdihumboldt.hale.io.xslt.transformations;

import java.io.OutputStream;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xslt.XslTypeTransformation;

/**
 * XSLT representation of the Retype function.
 * 
 * @author Simon Templer
 */
public class XslRetype extends AbstractXslTransformation implements XslTypeTransformation {

	@Override
	public void generateTemplate(String templateName, XmlElement targetElement, Cell typeCell,
			LocatableOutputSupplier<? extends OutputStream> out) {
		// TODO Auto-generated method stub

	}

}
