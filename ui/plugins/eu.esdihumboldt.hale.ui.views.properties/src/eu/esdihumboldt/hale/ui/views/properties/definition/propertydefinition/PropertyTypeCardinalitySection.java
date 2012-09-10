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
