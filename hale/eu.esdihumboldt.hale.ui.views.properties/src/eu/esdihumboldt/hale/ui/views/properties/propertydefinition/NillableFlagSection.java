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

package eu.esdihumboldt.hale.ui.views.properties.propertydefinition;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.schema.model.constraint.property.NillableFlag;

/**
 * TODO Type description
 * @author Patrick Lieb
 */
public class NillableFlagSection extends AbstractPropertyDefinition{
	
	private NillableFlagSection section;
	
	private Text nillableflag;

	/**
	 * @see AbstractPropertySection#createControls(Composite, TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		abstractCreateControls(parent, aTabbedPropertySheetPage, "NillableFlag:", false, null);
		nillableflag = TEXT;
	}
	
	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		if(PROPERTYDEFINITION.getConstraint(NillableFlag.class).isEnabled()){
			nillableflag.setText("true");
		} else {
			nillableflag.setText("false");
		}
	}
}
