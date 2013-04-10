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

package eu.esdihumboldt.hale.io.xslt.xpath;

import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinition;

/**
 * XPath filter string representation.
 * 
 * @author Simon Templer
 */
public class XPathFilterDefinition implements FilterDefinition<XPathFilter> {

	@Override
	public String getIdentifier() {
		return "XPath";
	}

	@Override
	public Class<XPathFilter> getObjectClass() {
		return XPathFilter.class;
	}

	@Override
	public XPathFilter parse(String value) {
		return new XPathFilter(value);
	}

	@Override
	public String asString(XPathFilter object) {
		return object.getExpression();
	}

}
