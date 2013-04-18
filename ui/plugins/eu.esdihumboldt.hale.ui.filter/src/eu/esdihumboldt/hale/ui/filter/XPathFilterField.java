/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.filter;

import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.io.xslt.xpath.XPathFilter;

/**
 * Field for editing a XPath filter.
 * 
 * @author Kai Schwierczek
 */
public class XPathFilterField extends TypeFilterField {

	private final EntityDefinition entity;

	/**
	 * Creates a XPath filter field for the given entity.
	 * 
	 * @param entity the entity definition
	 * @param parent the parent composite
	 * @param style the composite style
	 */
	public XPathFilterField(EntityDefinition entity, Composite parent, int style) {
		super(parent, style);
		this.entity = entity;
		// XXX check filter type?
		List<ChildContext> path = entity.getPropertyPath();
		if (path == null || path.isEmpty()) {
			if (entity.getFilter() != null)
				setFilterExpression(AlignmentUtil.getContextText(entity));
		}
		else {
			ChildContext last = path.get(path.size() - 1);
			if (last.getCondition() != null && last.getCondition().getFilter() != null)
				setFilterExpression(AlignmentUtil.getContextText(entity));
		}

		setVariableSelectEnabled(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.filter.TypeFilterField#selectVariable()
	 */
	@Override
	protected String selectVariable() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.filter.TypeFilterField#createFilter(java.lang.String)
	 */
	@Override
	protected Filter createFilter(String filterString) throws XPathExpressionException {
		// XXX check disabled for now
//		XPathFactory.newInstance().newXPath().compile("/dummy[" + filterString + "]");
		return new XPathFilter(filterString);
	}
}
