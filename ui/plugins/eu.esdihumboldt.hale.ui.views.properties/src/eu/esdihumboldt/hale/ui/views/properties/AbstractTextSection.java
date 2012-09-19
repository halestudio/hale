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

package eu.esdihumboldt.hale.ui.views.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Abstract section for properties views
 * 
 * @author Patrick Lieb
 */
public abstract class AbstractTextSection extends AbstractSingleObjectSection {

	private Text text;

	private Text text2;

	/**
	 * Creates the controls for two lines
	 * 
	 * @see AbstractPropertySection#createControls(Composite,TabbedPropertySheetPage)
	 * 
	 * @param parent the parent composite for the section
	 * @param aTabbedPropertySheetPage the tabbed property sheet page
	 * @param title the title for the property
	 * @param title2 the title for the second property (could be null)
	 */
	protected void abstractCreateControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage, String title, String title2) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data;

		text = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		text.setEditable(false);
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		text.setLayoutData(data);

		CLabel namespaceLabel = getWidgetFactory().createCLabel(composite, title); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(text, 10);
		data.top = new FormAttachment(text, 0, SWT.CENTER);
		namespaceLabel.setLayoutData(data);

		if (title2 != null) {
			text2 = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
			text2.setEditable(false);
			data = new FormData();
			data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
			data.right = new FormAttachment(100, 0);
			data.top = new FormAttachment(text, ITabbedPropertyConstants.VSPACE);
			text2.setLayoutData(data);

			CLabel label2 = getWidgetFactory().createCLabel(composite, title2); //$NON-NLS-1$
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
	public Text getText() {
		return text;
	}

	/**
	 * @return the configured second text
	 */
	public Text getText2() {
		return text2;
	}
}
