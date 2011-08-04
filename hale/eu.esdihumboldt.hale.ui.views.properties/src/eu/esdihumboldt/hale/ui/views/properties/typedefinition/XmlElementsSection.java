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

import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;

import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;

/**
 * TODO Type description
 * @author Patrick Lieb
 */
public class XmlElementsSection extends AbstractTypeDefinitionSection{
	
	private XmlElementsSection section;
	
	private Text xmlelements;

	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		Collection<? extends XmlElement> elements = TYPEDEFINITION.getConstraint(XmlElements.class).getElements();
			
	}
}
