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

package eu.esdihumboldt.hale.ui.views.properties.definition.propertydefinition;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.ui.views.properties.definition.DefaultDefinitionSection;

/**
 * Properties section with cardinality information
 * 
 * @author Patrick Lieb
 */
public class PropertyTypeCardinalitySection extends DefaultDefinitionSection<PropertyDefinition> {

	private Text min;

	private Text max;

	/**
	 * @see AbstractPropertySection#createControls(Composite,
	 *      TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		abstractCreateControls(parent, aTabbedPropertySheetPage, "Minimum:", "Maximum:");
		min = getText();
		max = getText2();
	}

	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		long minlong = getDefinition().getConstraint(Cardinality.class).getMinOccurs();
		long maxlong = getDefinition().getConstraint(Cardinality.class).getMaxOccurs();
		if (minlong == Cardinality.UNBOUNDED) {
			min.setText("unbounded");
		}
		else {
			min.setText(String.valueOf(minlong));
		}
		if (maxlong == Cardinality.UNBOUNDED) {
			max.setText("unbounded");
		}
		else {
			max.setText(String.valueOf(maxlong));
		}
	}
}
