package eu.esdihumboldt.hale.io.xsd.ui.properties;

import java.util.Collection;

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
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;

public class TypeDefinitionXmlElementsSection extends AbstractPropertySection{
	
	private TypeDefinition typedefinition;
	
	private Text[] textarray;
	
	private Text text;


	/**
	 * @see AbstractPropertySection#setInput(IWorkbenchPart, ISelection)
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		if (input instanceof EntityDefinition) {
			typedefinition = (TypeDefinition) ((EntityDefinition) input);
		}
	}
	
	/**
	 * @see AbstractPropertySection#createControls(Composite, TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		Collection<? extends XmlElement> elements = typedefinition.getConstraint(XmlElements.class).getElements();
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
		Collection<? extends XmlElement> elements = typedefinition.getConstraint(XmlElements.class).getElements();
		for(XmlElement element : elements){
			textarray[0].setText(element.getName().getNamespaceURI().toString());
		}
	}
}
