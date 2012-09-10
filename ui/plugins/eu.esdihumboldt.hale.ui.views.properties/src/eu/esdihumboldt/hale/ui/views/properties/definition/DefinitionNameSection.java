/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
