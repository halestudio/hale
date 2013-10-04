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

package eu.esdihumboldt.hale.io.xslt.ui.filter;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Condition;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;
import eu.esdihumboldt.hale.io.xslt.xpath.XPathFilter;
import eu.esdihumboldt.hale.ui.filter.TypeFilterField;

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
		Condition condition = AlignmentUtil.getContextCondition(entity);
		String text = condition == null ? null : AlignmentUtil.getFilterText(condition.getFilter());
		if (text != null)
			setFilterExpression(text);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.filter.TypeFilterField#selectVariable()
	 */
	@Override
	protected String selectVariable() {
		XPathPropertyDefinitionDialog dialog = new XPathPropertyDefinitionDialog(Display
				.getCurrent().getActiveShell(), entity, "Insert attribute name", null);

		if (dialog.open() == XPathPropertyDefinitionDialog.OK && dialog.getObject() != null) {
			EntityDefinition entityDef = dialog.getObject();
			StringBuilder var = new StringBuilder();
			for (int i = 0; i < dialog.getParentCount(); i++)
				var.append("../");

			// skip the first path element if we didn't start at top level
			int start = dialog.atTopLevel() ? 0 : 1;

			// if the element itself was selected simply use a single dot
			if (dialog.getParentCount() == 0 && entityDef.getPropertyPath().size() == start)
				var.append(".");

			boolean first = true;
			for (int i = start; i < entityDef.getPropertyPath().size(); i++) {
				PropertyDefinition propDef = entityDef.getPropertyPath().get(i).getChild()
						.asProperty();
				if (propDef != null) {
					if (first)
						first = false;
					else
						var.append("/");
					if (propDef.getConstraint(XmlAttributeFlag.class).isEnabled())
						var.append('@');
					QName name = entityDef.getPropertyPath().get(i).getChild().getName();
					if (!XMLConstants.NULL_NS_URI.equals(name.getNamespaceURI()))
						var.append("\"").append(name.getNamespaceURI()).append("\":");
					var.append(name.getLocalPart());
				}
			}
			return var.toString();
		}
		else
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
