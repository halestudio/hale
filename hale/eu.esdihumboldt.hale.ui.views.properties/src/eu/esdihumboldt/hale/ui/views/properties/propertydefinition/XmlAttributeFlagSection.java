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

import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;

/**
 * Properties section with XML-Attribute-Flag information
 * @author Patrick Lieb
 */
public class XmlAttributeFlagSection extends AbstractPropertyDefinition{
	
	private Text xmlattributeflag;
	
	/**
	 * @see AbstractPropertySection#createControls(Composite, TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		abstractCreateControls(parent, aTabbedPropertySheetPage, "XML-AttributeFlag:", false, null);
		xmlattributeflag = TEXT;
	}

	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		if(PROPERTYDEFINITION.getConstraint(XmlAttributeFlag.class).isEnabled()){
			xmlattributeflag.setText("true");
		} else {
			xmlattributeflag.setText("false");
		}
	}
}
