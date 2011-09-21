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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.ui.views.properties.DefaultDefinitionSection;

/**
 * Properties section with definition name
 * @author Simon Templer
 */
public class DefinitionNameSection extends DefaultDefinitionSection<Definition<?>> {

	private Text namespaceText;
	
	private Text localNameText;

	/**
	 * @see AbstractPropertySection#createControls(Composite, TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory()
				.createFlatFormComposite(parent);
		FormData data;
		
		namespaceText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		namespaceText.setEditable(false);
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		namespaceText.setLayoutData(data);

		CLabel namespaceLabel = getWidgetFactory()
				.createCLabel(composite, "Namespace:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(namespaceText,
				-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(namespaceText, 0, SWT.CENTER);
		namespaceLabel.setLayoutData(data);
		
		localNameText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		localNameText.setEditable(false);
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(namespaceText, ITabbedPropertyConstants.VSPACE);
		localNameText.setLayoutData(data);

		CLabel LocalNameLabel = getWidgetFactory()
				.createCLabel(composite, "Local name:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(localNameText,
				-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(localNameText, 0, SWT.CENTER);
		LocalNameLabel.setLayoutData(data);
	}


	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		if((getDefinition().getName().getNamespaceURI()).equals("")){
			namespaceText.setText("(not set)");
		} else {
			namespaceText.setText(getDefinition().getName().getNamespaceURI());
		}
		if((getDefinition().getName().getLocalPart()).equals("")){
			localNameText.setText("(not set)");
		} else {
			localNameText.setText(getDefinition().getName().getLocalPart());
		}
		
	}
	
	/**
	 * @return the namespaceText
	 */
	public Text getNamespace(){
		return namespaceText;
	}
	
	/**
	 * @return the localNameText
	 */
	public Text getLocalName(){
		return localNameText;
	}
	
}
