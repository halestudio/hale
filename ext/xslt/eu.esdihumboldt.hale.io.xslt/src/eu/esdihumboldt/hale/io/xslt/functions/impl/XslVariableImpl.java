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

package eu.esdihumboldt.hale.io.xslt.functions.impl;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.io.xslt.functions.XslVariable;

/**
 * Immutable default implementation of {@link XslVariable}.
 * 
 * @author Simon Templer
 */
public class XslVariableImpl implements XslVariable {

	private final EntityDefinition entity;

	private final String xpath;

	/**
	 * Create a XSL variable.
	 * 
	 * @param entity the associated entity definition as specified in the cell
	 * @param xpath the XPath expression that can be used to reference the
	 *            variable in the current context
	 */
	public XslVariableImpl(EntityDefinition entity, String xpath) {
		super();
		this.entity = entity;
		this.xpath = xpath;
	}

	@Override
	public EntityDefinition getEntity() {
		return entity;
	}

	@Override
	public String getXPath() {
		return xpath;
	}

}
