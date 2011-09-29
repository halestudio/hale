package eu.esdihumboldt.hale.io.xsd.ui.properties;

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

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;

public class PropertyTypeXmlAttributeFlagSection extends AbstractPropertySection{
	
	private PropertyDefinition propertydefinition;
	
	private Text xmlattributeflag;


	/**
	 * @see AbstractPropertySection#setInput(IWorkbenchPart, ISelection)
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		if (input instanceof EntityDefinition) {
			propertydefinition = (PropertyDefinition) ((EntityDefinition) input);
		}
			
	}
	
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
		
		xmlattributeflag = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		xmlattributeflag.setEditable(false);
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		xmlattributeflag.setLayoutData(data);
		
		CLabel namespaceLabel = getWidgetFactory()
		.createCLabel(composite, "XML-Attribute-Flag"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(xmlattributeflag,
				10);
		data.top = new FormAttachment(xmlattributeflag, 0, SWT.CENTER);
		namespaceLabel.setLayoutData(data);
		
	}

	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		if (propertydefinition.getConstraint(XmlAttributeFlag.class).isEnabled()){
			xmlattributeflag.setText("true");
		} else {
			xmlattributeflag.setText("false");
		}
	}
}
