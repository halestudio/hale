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

package eu.esdihumboldt.hale.ui.views.properties.definition.typedefinition;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.hale.ui.views.properties.definition.DefaultDefinitionSection;

/**
 * Properties section with AbstractFlag information
 * 
 * @author Patrick Lieb
 */
public class TypeDefinitionAbstractFlagSection extends DefaultDefinitionSection<TypeDefinition> {

	private Text abstractFlag;

	/**
	 * @see AbstractPropertySection#createControls(Composite,
	 *      TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		abstractCreateControls(parent, aTabbedPropertySheetPage, "AbstractFlag:", null);
		abstractFlag = getText();
	}

	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		if (getDefinition().getConstraint(AbstractFlag.class).isEnabled()) {
			abstractFlag.setText("true");
		}
		else {
			abstractFlag.setText("false");
		}
	}
}
