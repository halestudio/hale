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

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XPathFilter other = (XPathFilter) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		}
		else if (!expression.equals(other.expression))
			return false;
		return true;
	}

}
