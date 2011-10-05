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

package eu.esdihumboldt.hale.ui.views.properties;

import java.util.HashMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Abstract section for properties views
 * @author Patrick Lieb
 */
public abstract class AbstractSection extends AbstractPropertySection{
	
	
	private HashMap<String, Text> textmap = new HashMap<String, Text>();
	
	private Text text;
	
	private Text text2;
	
//	private int pos = -1;
//	
//	private String[] list;

	/**
	 * @see AbstractPropertySection#setInput(IWorkbenchPart, ISelection)
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		setInput(input);
	}

	/**
	 * @param input the definition as object
	 */
	protected abstract void setInput(Object input);
	
//	/**
//	 * Abstract version of createControls for more than 
//	 * one control in one line
//	 * @param parent the parent composite for the section
//	 * @param aTabbedPropertySheetPage the tabbed property sheet page
//	 * @param list the identifier for the property
//	 */
//	protected void createControlsOnList(Composite parent,
//			TabbedPropertySheetPage aTabbedPropertySheetPage, String[] list){
//		this.list = list;
//		for(pos = 0; pos < list.length; pos++){
//			abstractCreateControls(parent, aTabbedPropertySheetPage, list[pos]);
//			textmap.put(list[pos], text);
//		}
//	}

	/**
	 * Creates the controls for two lines @seeAbstractPropertySection#createControls(Composite, TabbedPropertySheetPage)
	 * @param parent the parent composite for the section
	 * @param aTabbedPropertySheetPage the tabbed property sheet page
	 * @param title the title for the property
	 * @param title2 the title for the second property (could be null)
	 */
	protected void abstractCreateControls(Composite parent,
		TabbedPropertySheetPage aTabbedPropertySheetPage, String title, String title2){
//		if (pos <= 0)
			super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory()
		.createFlatFormComposite(parent);
		FormData data;
		
		text = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		text.setEditable(false);
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
//		if(pos <= 0 ){
			data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
//		} else {
//			data.top = new FormAttachment(textmap.get(list[pos-1]), ITabbedPropertyConstants.VSPACE);
//		}
		text.setLayoutData(data);
	
		CLabel namespaceLabel = getWidgetFactory()
				.createCLabel(composite, title); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(text,
				10);
		data.top = new FormAttachment(text, 0, SWT.CENTER);
		namespaceLabel.setLayoutData(data);
		
		if(title2 != null){
			text2 = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
			text2.setEditable(false);
			data = new FormData();
			data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
			data.right = new FormAttachment(100, 0);
			data.top = new FormAttachment(text, ITabbedPropertyConstants.VSPACE);
			text2.setLayoutData(data);
		
			CLabel label2 = getWidgetFactory()
					.createCLabel(composite, title2); //$NON-NLS-1$
			data = new FormData();
			data.left = new FormAttachment(0, 0);
			data.right = new FormAttachment(text2, 55);
			data.top = new FormAttachment(text2, 0, SWT.CENTER);
			label2.setLayoutData(data);
		}
	}
	/**
	 * @return the configured text
	 */
	public Text getText(){
		return text;
	}
	
	/**
	 * @return the configured second text
	 */
	public Text getText2(){
		return text2;
	}
	
	/**
	 * @param key the key for the text element
	 * @return the text element
	 */
	public Text getMapText(String key){
		return textmap.get(key);
	}
}