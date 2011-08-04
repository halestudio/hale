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

package eu.esdihumboldt.hale.ui.views.properties.typedefinition;


import java.util.Collection;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;

/**
 * Properties section with XML-Elements information
 * @author Patrick Lieb
 */
public class XmlElementsSection extends AbstractTypeDefinitionSection{
	
	private Text[] text;
	
	/**
	 * @see AbstractPropertySection#createControls(Composite, TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		@SuppressWarnings("unused")
		Collection<? extends XmlElement> elements;
		int i = 0;
		for(@SuppressWarnings("unused") XmlElement element : elements = TYPEDEFINITION.getConstraint(XmlElements.class).getElements()){
			abstractCreateControls(parent, aTabbedPropertySheetPage, "Name:", false, null);
			text[i] = TEXT;
			i++;
		}
	}

	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		@SuppressWarnings("unused")
		Collection<? extends XmlElement> elements;
		int i = 0;
			for(XmlElement element : elements = TYPEDEFINITION.getConstraint(XmlElements.class).getElements()){
				text[i].setText(element.getDisplayName());
				i++;
			}
	}
}
