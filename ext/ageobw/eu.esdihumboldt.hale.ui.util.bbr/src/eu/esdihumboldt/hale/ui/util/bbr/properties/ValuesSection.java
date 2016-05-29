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

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
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
 * Properties section showing values from the BBR documentation.
 * 
 * @author Simon Templer
 */
public class ValuesSection extends DefaultDefinitionSection<Definition<?>> {

	private Text descriptionText;

	private ListViewer values;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data;

		descriptionText = getWidgetFactory().createText(composite, "", //$NON-NLS-1$
				SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
		descriptionText.setEditable(false);

		data = new FormData();
		data.width = 100;
		data.height = 100;
		data.left = new FormAttachment(0, 250); // STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		data.bottom = new FormAttachment(100, -ITabbedPropertyConstants.VSPACE);
		descriptionText.setLayoutData(data);

		values = new ListViewer(getWidgetFactory().createList(composite,
				SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL));
		data = new FormData();
		data.height = 100;
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(descriptionText, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(descriptionText, 0, SWT.TOP);
		values.getControl().setLayoutData(data);

		values.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof Documentation) {
					Documentation doc = (Documentation) element;
					StringBuilder result = new StringBuilder();

					boolean usable = (doc.isInUse() && !doc.getUseDiffers())
							|| (!doc.isInUse() && doc.getUseDiffers());

					if (usable) {
						result.append(doc.getCode());
					}
					else {
						// display values that are not in use in brackets
						result.append('(');
						result.append(doc.getCode());
						result.append(')');
					}

					/*
					 * Mark use conflict with an asterisk. Only a conflict if
					 * something is wrongly classified as not in use.
					 */
					if (!doc.isInUse() && doc.getUseDiffers()) {
						result.append('*');
					}

					return result.toString();
				}

				return super.getText(element);
			}

		});
		values.setContentProvider(ArrayContentProvider.getInstance());

		values.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				// display documentation on selected value
				ISelection sel = event.getSelection();
				if (!sel.isEmpty() && sel instanceof IStructuredSelection) {
					Documentation doc = (Documentation) ((IStructuredSelection) sel)
							.getFirstElement();
					String text = getDocumentationText(doc);
					if (text != null) {
						descriptionText.setText(text);
					}
					else {
						descriptionText.setText("");
					}
					descriptionText.setEnabled(text != null);
				}
				else {
					descriptionText.setText("");
					descriptionText.setEnabled(false);
				}
			}
		});
	}

	/**
	 * Get the text representation of a documentation.
	 * 
	 * @param doc the documentation
	 * @return the text representation or <code>null</code>
	 */
	protected String getDocumentationText(Documentation doc) {
		StringBuilder str = new StringBuilder();

		if (doc.getDefinition() != null && !doc.getDefinition().isEmpty()) {
			str.append("--Definition--\n");
			str.append(doc.getDefinition());
			str.append("\n\n");
		}

		if (doc.getDescription() != null && !doc.getDescription().isEmpty()) {
			str.append("--Description--\n");
			str.append(doc.getDescription());
			str.append("\n\n");
		}

		if (str.length() == 0)
			return null;
		else
			return str.toString();
	}

	/**
	 * @see AbstractPropertySection#shouldUseExtraSpace()
	 */
	@Override
	public boolean shouldUseExtraSpace() {
		return false;
	}

	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		DocumentationService ds = PlatformUI.getWorkbench().getService(DocumentationService.class);

		Documentation doc = ds.getDocumentation(getDefinition());

		if (doc != null) {
			values.setInput(doc.getValues());
			if (!doc.getValues().isEmpty()) {
				// select the first entry
				values.setSelection(new StructuredSelection(doc.getValues().iterator().next()));
			}
		}
		else {
			values.setInput(null);
		}
	}
}
