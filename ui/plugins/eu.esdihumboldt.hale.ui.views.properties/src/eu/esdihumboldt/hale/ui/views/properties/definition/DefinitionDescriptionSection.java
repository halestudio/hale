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

/**
 * Properties section with description
 * 
 * @author Simon Templer
 */
public class DefinitionDescriptionSection extends DefaultDefinitionSection<Definition<?>> {

	private Text descriptionText;

	private CLabel namespaceLabel;

	/**
	 * @see AbstractPropertySection#createControls(Composite,
	 *      TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data;

		descriptionText = getWidgetFactory().createText(composite, "", //$NON-NLS-1$ 
				SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
		descriptionText.setEditable(false);
		// TODO improve layout
		data = new FormData();
		data.width = 100;
		data.height = 100;
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		data.bottom = new FormAttachment(100, -ITabbedPropertyConstants.VSPACE);
		descriptionText.setLayoutData(data);

		namespaceLabel = getWidgetFactory().createCLabel(composite, "Description:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(descriptionText, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(descriptionText, 0, SWT.TOP);
		namespaceLabel.setLayoutData(data);
	}

	/**
	 * @see AbstractPropertySection#shouldUseExtraSpace()
	 */
	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}

	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		String desc = getDefinition().getDescription();
		if (desc == null) {
			desc = "";
		}
		descriptionText.setText(desc);
	}

	/**
	 * @return the descriptionText
	 */
	public Text getDescription() {
		return descriptionText;
	}
}
