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

package eu.esdihumboldt.hale.io.wfs.ui;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.io.wfs.AbstractWFSWriter;
import eu.esdihumboldt.hale.io.wfs.WFSConstants;
import eu.esdihumboldt.hale.io.wfs.WFSVersion;
import eu.esdihumboldt.hale.ui.io.target.AbstractTarget;
import eu.esdihumboldt.hale.ui.io.target.URLTargetURIFieldEditor;
import eu.esdihumboldt.hale.ui.util.viewer.EnumContentProvider;

/**
 * Select a WFS as target.
 * 
 * @author Simon Templer
 */
public class WFSTarget extends AbstractTarget<AbstractWFSWriter<?>> implements WFSConstants {

	private URLTargetURIFieldEditor targetURL;
	private ComboViewer selectVersion;

	@Override
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(3, false));

		// target URL field
		targetURL = new URLTargetURIFieldEditor("targetURL", "Transaction URL", parent) {

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

		// TODO button to determine from capabilities

		// select version
		Label versionLabel = new Label(parent, SWT.NONE);
		versionLabel.setText("WFS Version");

		selectVersion = new ComboViewer(parent);
		GridDataFactory.swtDefaults().span(2, 1).grab(true, false).align(SWT.FILL, SWT.CENTER)
				.applyTo(selectVersion.getControl());
		selectVersion.setContentProvider(EnumContentProvider.getInstance());
		selectVersion.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof WFSVersion) {
					return ((WFSVersion) element).versionString;
				}
				return super.getText(element);
			}

		});
		selectVersion.setInput(WFSVersion.class);

		selectVersion.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateState();
			}
		});

		// fixed content type
		setContentType(Platform.getContentTypeManager().getContentType(CONTENT_TYPE_ID_WFST));

		// initial state update
		updateState();
	}

	/**
	 * Update the page state.
	 */
	protected void updateState() {
		setValid(targetURL.isValid() && !selectVersion.getSelection().isEmpty());
	}

	@Override
	public void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		targetURL.updateHistory();
	}

	@Override
	public boolean updateConfiguration(AbstractWFSWriter<?> provider) {
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

			// WFS version
			ISelection versionSel = selectVersion.getSelection();
			if (!versionSel.isEmpty() && versionSel instanceof IStructuredSelection) {
				Object sel = ((IStructuredSelection) versionSel).getFirstElement();
				if (sel instanceof WFSVersion) {
					provider.setWFSVersion((WFSVersion) sel);
				}
			}

			return true;
		}
		return false;
	}
}
