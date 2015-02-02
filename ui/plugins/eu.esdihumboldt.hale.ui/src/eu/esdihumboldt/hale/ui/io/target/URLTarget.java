/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.io.target;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;

/**
 * Target selector for an URL/URI.
 * 
 * @author Simon Templer
 */
public class URLTarget extends AbstractTarget<ExportProvider> {

	private URLTargetURIFieldEditor targetURL;
	private ComboViewer types;

	/**
	 * @see eu.esdihumboldt.hale.ui.io.ExportTarget#createControls(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(3, false));

		// source file
		targetURL = new URLTargetURIFieldEditor("targetURL", "Target URL", parent) {

			@Override
			protected void onHistorySelected(URI location) {
				updateState();
			}

		};
		targetURL.setPage(getPage());

		targetURL.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					getPage().setMessage(null);
					updateState();
				}
				else if (event.getProperty().equals(FieldEditor.VALUE)) {
					getPage().setMessage(null);
					updateState();
				}
			}
		});

		// content type selection

		// label
		Label typesLabel = new Label(parent, SWT.NONE);
		typesLabel.setText("Content type");

		// types combo
		Composite group = new Composite(parent, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		group.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());

		types = new ComboViewer(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		types.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		types.setContentProvider(ArrayContentProvider.getInstance());
		types.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof IContentType) {
					return ((IContentType) element).getName();
				}
				return super.getText(element);
			}

		});

		// process selection changes
		types.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				// update content type
				ISelection sel = event.getSelection();
				if (sel.isEmpty() || !(sel instanceof IStructuredSelection)) {
					setContentType(null);
				}
				else {
					setContentType((IContentType) ((IStructuredSelection) sel).getFirstElement());
				}
			}
		});
	}

	/**
	 * Update the page state.
	 */
	protected void updateState() {
		setValid(targetURL.isValid());
	}

	@Override
	public void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		targetURL.updateHistory();

		IContentType contentType = getWizard().getProvider().getContentType();
		Set<IContentType> supportedTypes = getAllowedContentTypes();
		types.setInput(supportedTypes);
		if (supportedTypes.size() == 1) {
			types.setSelection(new StructuredSelection(supportedTypes.iterator().next()));
		}
		else if (contentType != null) {
			types.setSelection(new StructuredSelection(contentType));
		}
	}

	@Override
	public boolean updateConfiguration(ExportProvider provider) {
		if (targetURL.isValid()) {
			final URI url = targetURL.getURI(true);
			provider.setTarget(new LocatableOutputSupplier<OutputStream>() {

				@Override
				public OutputStream getOutput() throws IOException {
					return null;
				}

				@Override
				public URI getLocation() {
					return url;
				}
			});

			return true;
		}
		return false;
	}

}
