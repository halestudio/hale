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

package eu.esdihumboldt.hale.ui.views.properties.function.abstractfunction;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.ui.views.properties.function.DefaultFunctionSection;

/**
 * Abstract function section with description information
 * 
 * @author Patrick Lieb
 */
public class AbstractFunctionDescriptionSection extends
		DefaultFunctionSection<AbstractFunction<?>> {

	private Text description;

	private CLabel label;

	/**
	 * @see AbstractPropertySection#createControls(Composite,
	 *      TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory()
				.createFlatFormComposite(parent);
		FormData data;

		description = getWidgetFactory().createText(composite, "",
				SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
		description.setEditable(false);

		data = new FormData();
		data.width = 100;
		data.height = 100;
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		data.bottom = new FormAttachment(100, -ITabbedPropertyConstants.VSPACE);
		description.setLayoutData(data);

		label = getWidgetFactory().createCLabel(composite, "Description:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(description,
				-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(description, 0, SWT.TOP);
		label.setLayoutData(data);
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
		String desc = getFunction().getDescription();
		if (desc == null) {
			desc = "";
		}
		description.setText(desc);
	}
}
