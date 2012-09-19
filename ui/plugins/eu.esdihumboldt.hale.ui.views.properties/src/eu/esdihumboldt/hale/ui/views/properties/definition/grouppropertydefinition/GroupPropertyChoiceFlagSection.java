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

package eu.esdihumboldt.hale.ui.views.properties.definition.grouppropertydefinition;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.ChoiceFlag;
import eu.esdihumboldt.hale.ui.views.properties.definition.DefaultDefinitionSection;

/**
 * Properties section with choiceflag information
 * 
 * @author Patrick Lieb
 */
public class GroupPropertyChoiceFlagSection extends
		DefaultDefinitionSection<GroupPropertyDefinition> {

	private Text choiceflag;

	/**
	 * @see AbstractPropertySection#createControls(Composite,
	 *      TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		abstractCreateControls(parent, aTabbedPropertySheetPage, "ChoiceFlag:", null);
		choiceflag = getText();
	}

	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		if (getDefinition().getConstraint(ChoiceFlag.class).isEnabled()) {
			choiceflag.setText("true");
		}
		else {
			choiceflag.setText("false");
		}
	}
}
