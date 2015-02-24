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
import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.io.wfs.AbstractWFSWriter;
import eu.esdihumboldt.hale.ui.io.target.AbstractTarget;
import eu.esdihumboldt.hale.ui.io.target.URLTargetURIFieldEditor;

/**
 * TODO Type description
 * 
 * @author Simon Templer
 */
public class WFSTarget extends AbstractTarget<AbstractWFSWriter<?>> {

	private URLTargetURIFieldEditor targetURL;

	@Override
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(3, false));

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

			return true;
		}
		return false;
	}
}
