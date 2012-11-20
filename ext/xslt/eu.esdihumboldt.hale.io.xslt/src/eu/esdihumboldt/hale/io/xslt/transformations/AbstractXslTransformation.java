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

import eu.esdihumboldt.hale.io.xslt.XslTransformation;
import eu.esdihumboldt.hale.io.xslt.XsltGenerationContext;

/**
 * Base class for XSLT transformations.
 * 
 * @author Simon Templer
 */
public abstract class AbstractXslTransformation implements XslTransformation {

	private XsltGenerationContext context;

	@Override
	public void setContext(XsltGenerationContext context) {
		this.context = context;
	}

	/**
	 * Get the XSLT generation context.
	 * 
	 * @return the context of the current XSLT generation process
	 */
	protected XsltGenerationContext context() {
		return context;
	}

}
