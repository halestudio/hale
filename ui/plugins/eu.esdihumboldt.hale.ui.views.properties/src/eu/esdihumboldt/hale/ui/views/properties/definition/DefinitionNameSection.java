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

package eu.esdihumboldt.hale.ui.views.properties.definition;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.schema.model.Definition;

/**
 * Properties section with definition name
 * 
 * @author Simon Templer
 */
public class DefinitionNameSection extends DefaultDefinitionSection<Definition<?>> {

	private Text namespaceText;

	private Text localNameText;

	/**
	 * @see AbstractPropertySection#createControls(Composite,
	 *      TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		abstractCreateControls(parent, aTabbedPropertySheetPage, "Namespace:", "Local name:");
		namespaceText = getText();
		localNameText = getText2();
	}

	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		namespaceText.setText(getDefinition().getName().getNamespaceURI());
		localNameText.setText(getDefinition().getName().getLocalPart());
	}

	/**
	 * @return the namespaceText
	 */
	public Text getNamespace() {
		return namespaceText;
	}

	/**
	 * @return the localNameText
	 */
	public Text getLocalName() {
		return localNameText;
	}
}
