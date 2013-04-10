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

package eu.esdihumboldt.hale.io.xslt.filter;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Filter based on XPath expression for use with XSLT mappings.
 * 
 * @author Simon Templer
 */
public class XPathFilter implements Filter {

	private final String expression;

	/**
	 * Create a XPath filter.
	 * 
	 * @param expression the XPath expression
	 */
	public XPathFilter(String expression) {
		super();
		this.expression = expression;
	}

	@Override
	public boolean match(Instance instance) {
		/*
		 * FIXME filter cannot be evaluated in HALE/CST context (at least for
		 * now)
		 */
		return false;
	}

	/**
	 * @return the XPath expression defining the filter
	 */
	public String getExpression() {
		return expression;
	}

}
