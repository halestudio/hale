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

package eu.esdihumboldt.hale.ui.util.bbr.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.ui.util.bbr.Documentation;
import eu.esdihumboldt.hale.ui.util.bbr.DocumentationService;
import eu.esdihumboldt.hale.ui.views.properties.definition.DefaultDefinitionSection;

/**
 * Properties section with text from a BBR documentation.
 * 
 * @author Simon Templer
 */
public abstract class AbstractDocumentationTextSection
		extends DefaultDefinitionSection<Definition<?>> {

	private Text descriptionText;

	private CLabel label;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data;

		if (useMultilineText()) {
			descriptionText = getWidgetFactory().createText(composite, "", //$NON-NLS-1$
					SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
		}
		else {
			descriptionText = getWidgetFactory().createText(composite, "", //$NON-NLS-1$
					SWT.SINGLE | SWT.BORDER);
		}
		descriptionText.setEditable(false);
		// TODO improve layout
		data = new FormData();
		data.width = 100;
		if (useMultilineText()) {
			data.height = 85;
		}
		data.left = new FormAttachment(0, 100); // STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		data.bottom = new FormAttachment(100, -ITabbedPropertyConstants.VSPACE);
		descriptionText.setLayoutData(data);

		label = getWidgetFactory().createCLabel(composite, getDocumentationLabel()); // $NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(descriptionText, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(descriptionText, 0, SWT.TOP);
		label.setLayoutData(data);
	}

	/**
	 * @see AbstractPropertySection#shouldUseExtraSpace()
	 */
	@Override
	public boolean shouldUseExtraSpace() {
		return false;
	}

	/**
	 * States if a multi-line text field should be used. This implementation
	 * returns <code>true</code>. Override to change behavior.
	 * 
	 * @return if a multi-line text field should be used
	 */
	protected boolean useMultilineText() {
		return true;
	}

	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		DocumentationService ds = PlatformUI.getWorkbench().getService(DocumentationService.class);

		Documentation doc = ds.getDocumentation(getDefinition());

		String desc = (doc == null) ? (null) : (getDocumentationText(doc));
		if (desc == null) {
			desc = "";
		}
		descriptionText.setText(desc);
	}

	/**
	 * Get the label text to display in the section.
	 * 
	 * @return the label text
	 */
	protected abstract String getDocumentationLabel();

	/**
	 * Get the documentation text from a given BBR documentation.
	 * 
	 * @param doc the documentation object
	 * @return the text to display
	 */
	protected abstract String getDocumentationText(Documentation doc);
}
