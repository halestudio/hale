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

package eu.esdihumboldt.hale.io.xsd.ui.properties;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.ui.views.properties.DefaultDefinitionSection;

/**
 * Properties Section with XmlElements
 * @author Patrick Lieb
 */
public class TypeDefinitionXmlElementsSection extends DefaultDefinitionSection<TypeDefinition> {
	
	private Text[] textarray;
	
	private Text text;

	/**
	 * @see AbstractPropertySection#createControls(Composite, TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		Collection<? extends XmlElement> elements = getDefinition().getConstraint(XmlElements.class).getElements();
		int length = elements.size();
		for(int pos = 0; pos < length; pos++){
			super.createControls(parent, aTabbedPropertySheetPage);
			Composite composite = getWidgetFactory()
			.createFlatFormComposite(parent);
			FormData data;
			
			text = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
			text.setEditable(false);
			data = new FormData();
			data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
			data.right = new FormAttachment(100, 0);
			if(pos <= 0 ){
				data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
			} else {
				data.top = new FormAttachment(textarray[pos-1], ITabbedPropertyConstants.VSPACE);
			}
			text.setLayoutData(data);
		
			CLabel namespaceLabel;
			if(pos <= 0 ){
				namespaceLabel = getWidgetFactory()
				.createCLabel(composite, "XML-Elements"); //$NON-NLS-1$
			} else {
				namespaceLabel = getWidgetFactory()
				.createCLabel(composite, ""); //$NON-NLS-1$
			}
			
			data = new FormData();
			data.left = new FormAttachment(0, 0);
			data.right = new FormAttachment(text,
					10);
			data.top = new FormAttachment(text, 0, SWT.CENTER);
			namespaceLabel.setLayoutData(data);
			
			textarray[pos] = text;
		}
		
		
	}

	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		Collection<? extends XmlElement> elements = getDefinition().getConstraint(XmlElements.class).getElements();
		for(XmlElement element : elements){
			textarray[0].setText(element.getName().getNamespaceURI().toString());
		}
	}
}
