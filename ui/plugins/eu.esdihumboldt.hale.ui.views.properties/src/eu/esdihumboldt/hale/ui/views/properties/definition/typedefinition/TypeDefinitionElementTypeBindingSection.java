/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.properties.definition.typedefinition;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ElementType;
import eu.esdihumboldt.hale.ui.views.properties.definition.TypeDefinitionSection;

/**
 * Properties section with element type and binding information
 * 
 * @author Patrick Lieb
 */
public class TypeDefinitionElementTypeBindingSection extends TypeDefinitionSection {

	private Text elementType;

	private Text binding;

	/**
	 * @see AbstractPropertySection#createControls(Composite,
	 *      TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		abstractCreateControls(parent, aTabbedPropertySheetPage, "ElementType:", "Binding:");
		elementType = getText();
		binding = getText2();
	}

	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		Binding bind = getDefinition().getConstraint(Binding.class);
		if (Collection.class.isAssignableFrom(bind.getBinding())) {
			ElementType element = getDefinition().getConstraint(ElementType.class);
			elementType.setText(element.getBinding().getName());
		}
		else {
			elementType.setText(""); // XXX should not be displayed
		}
		binding.setText(bind.getBinding().getName());
	}

}
