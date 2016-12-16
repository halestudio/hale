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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.ui.util.io.SaveFileFieldEditor;

/**
 * File as export target.
 * 
 * @author Simon Templer
 * @param <P> the export provider type
 */
public class FileTarget<P extends ExportProvider> extends AbstractTarget<P> {

	private static final ALogger log = ALoggerFactory.getLogger(FileTarget.class);

	/**
	 * The file field editor for the target file
	 */
	private SaveFileFieldEditor targetFile;

	@Override
	public void createControls(Composite parent) {
		getPage().setDescription("Please select a destination file for the export");
		
		parent.setLayout(new GridLayout(3, false));

		targetFile = new SaveFileFieldEditor("targetFile", "Target file:", true,
				FileFieldEditor.VALIDATE_ON_KEY_STROKE, parent);
		targetFile.setEmptyStringAllowed(false);
		targetFile.setAllowUri(true);
		targetFile.setPage(getPage());
		targetFile.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					updateState();
				}
				else if (event.getProperty().equals(FieldEditor.VALUE)) {
					updateContentType();
				}
			}
		});

		updateState();
	}

	@Override
	public boolean updateConfiguration(P provider) {
		try {
			URI uri = new URI(targetFile.getStringValue());
			if (!uri.isAbsolute()) {
				// was a file
				uri = new File(targetFile.getStringValue()).toURI();
			}
			final URI location = uri;
			provider.setTarget(new LocatableOutputSupplier<OutputStream>() {

				@Override
				public OutputStream getOutput() throws IOException {
					File file = new File(location);
					return new FileOutputStream(file);

					// XXX other URIs unsupported for now
				}

				@Override
				public URI getLocation() {
					return location;
				}
			});
			return true;
		} catch (URISyntaxException e) {
			// ignore, assume it's a file
		}

		File file = new File(targetFile.getStringValue());
		provider.setTarget(new FileIOSupplier(file));
		return true;
	}

	/**
	 * Update the content type
	 */
	private void updateContentType() {
		IContentType contentType = null;

		if (getWizard().getProviderFactory() != null && targetFile.isValid()) {
			Collection<IContentType> types = getAllowedContentTypes();
			if (types != null && !types.isEmpty()) {
				if (types.size() == 1) {
					// if only one content type is possible for the export we
					// can assume that it is used
					contentType = types.iterator().next();
				}
				else {
					Collection<IContentType> filteredTypes = HaleIO.findContentTypesFor(types,
							null, targetFile.getStringValue());
					if (!filteredTypes.isEmpty()) {
						contentType = filteredTypes.iterator().next();
					}
				}
			}
			else {
				// no supported content types!
				log.error("Export provider {0} doesn't support any content types", getWizard()
						.getProviderFactory().getDisplayName());
			}
		}

		setContentType(contentType);
		if (contentType != null) {
			getPage().setMessage(contentType.getName(), DialogPage.INFORMATION);
		}
		else {
			getPage().setMessage(null);
		}
	}

	/**
	 * Update the validity state
	 */
	protected void updateState() {
		updateContentType();

		setValid(this.isValid());
	}

	/**
	 * Check if a valid target file was selected.
	 * 
	 * @return true if valid
	 */
	protected boolean isValid() {
		return targetFile.isValid();
	}

	@Override
	public void onShowPage(boolean firstShow) {
		// update file editor with possibly changed file extensions
		targetFile.setContentTypes(getAllowedContentTypes());
	}

	/**
	 * Get the target file name
	 * 
	 * @return the target file name
	 */
	public String getTargetFileName() {
		return targetFile.getStringValue();
	}

	/**
	 * @return the file field editor for the target file
	 */
	protected SaveFileFieldEditor getSaveFieldEditor() {
		return targetFile;
	}

}
