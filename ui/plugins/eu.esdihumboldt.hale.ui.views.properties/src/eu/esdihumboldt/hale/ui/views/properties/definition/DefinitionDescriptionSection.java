/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
