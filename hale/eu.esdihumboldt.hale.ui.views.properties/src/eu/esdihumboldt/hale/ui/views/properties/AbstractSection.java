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
 * TODO Type description
 * @author Patrick Lieb
 */
public abstract class AbstractSection extends AbstractPropertySection{
	
	protected static Text TEXT;
	
	protected static Text TEXT2;

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
	
	/**
	 * @param parent
	 * @param aTabbedPropertySheetPage
	 * @param text
	 * @param title
	 * @param marker 
	 * @param text2 
	 * @param title2 
	 * @param section 
	 */
	protected void abstractCreateControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage, String title, boolean marker, String title2){
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory()
				.createFlatFormComposite(parent);
		FormData data;
		
		TEXT = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		TEXT.setEditable(false);
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		TEXT.setLayoutData(data);

		CLabel namespaceLabel = getWidgetFactory()
				.createCLabel(composite, title); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(TEXT,
				-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(TEXT, 0, SWT.CENTER);
		namespaceLabel.setLayoutData(data);
		
		if(marker){
			TEXT2 = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
			TEXT2.setEditable(false);
			data = new FormData();
			data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
			data.right = new FormAttachment(100, 0);
			data.top = new FormAttachment(TEXT, ITabbedPropertyConstants.VSPACE);
			TEXT2.setLayoutData(data);

			CLabel LocalNameLabel = getWidgetFactory()
					.createCLabel(composite, title2); //$NON-NLS-1$
			data = new FormData();
			data.left = new FormAttachment(0, 0);
			data.right = new FormAttachment(TEXT2,
					-ITabbedPropertyConstants.HSPACE);
			data.top = new FormAttachment(TEXT2, 0, SWT.CENTER);
			LocalNameLabel.setLayoutData(data);
		}
	}
}
