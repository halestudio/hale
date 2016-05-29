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

package eu.esdihumboldt.hale.ui.io.source;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.ui.io.ImportSource;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.util.io.IOUtils;

/**
 * File import source
 * 
 * @param
 * 			<P>
 *            the supported {@link IOProvider} type
 * 
 * @author Simon Templer
 * @since 2.5
 */
public class FileSource<P extends ImportProvider> extends AbstractProviderSource<P> {

	/**
	 * The file field editor for the source file
	 */
	private FileSourceFileFieldEditor sourceFile;

	/**
	 * The set of supported content types
	 */
	private Set<IContentType> supportedTypes;

	private URI projectLocation;

	/**
	 * @see ImportSource#createControls(Composite)
	 */
	@Override
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(4, false));

		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		projectLocation = ps.getLoadLocation() == null ? null : ps.getLoadLocation();
		boolean projectLocAvailable = projectLocation != null
				&& "file".equals(projectLocation.getScheme());

		// source file
		sourceFile = new FileSourceFileFieldEditor("sourceFile", "Source file:",
				FileFieldEditor.VALIDATE_ON_KEY_STROKE, parent, projectLocation);
		sourceFile.setEmptyStringAllowed(false);
		sourceFile.setPage(getPage());

		// set content types for file field
		Collection<IOProviderDescriptor> factories = getConfiguration().getFactories();
		supportedTypes = new HashSet<IContentType>();
		for (IOProviderDescriptor factory : factories) {
			supportedTypes.addAll(factory.getSupportedTypes());
		}

		sourceFile.setContentTypes(supportedTypes);
		sourceFile.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					updateState(true);
				}
				else if (event.getProperty().equals(FieldEditor.VALUE)) {
					updateState(true);
				}
			}
		});

		// provider selection

		// label
		Label providerLabel = new Label(parent, SWT.NONE);
		providerLabel.setText("Import as");

		// create provider combo
		ComboViewer providers = createProviders(parent);
		providers.getControl()
				.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));

		final Button relativeCheck = new Button(parent, SWT.CHECK);
		String text = "Use relative paths if possible.";
		relativeCheck.setText("Use relative paths if possible.");
		relativeCheck.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				sourceFile.setUseRelativeIfPossible(relativeCheck.getSelection());
			}
		});
		if (!projectLocAvailable) {
			relativeCheck.setEnabled(false);
			text += " Only available once the project is saved to a file.";
		}
		relativeCheck.setText(text);
		relativeCheck.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 4, 1));

		// initial state update
		updateState(true);
	}

	/**
	 * @see AbstractProviderSource#updateContentType()
	 */
	@Override
	protected void updateContentType() {
		IContentType contentType = null;

		if (sourceFile.isValid()) {
			// determine content type
			Collection<IContentType> filteredTypes = HaleIO.findContentTypesFor(supportedTypes,
					getSource(), sourceFile.getStringValue());
			if (!filteredTypes.isEmpty()) {
				contentType = filteredTypes.iterator().next();
			}
		}

		getConfiguration().setContentType(contentType);
		if (contentType != null) {
			getPage().setMessage(contentType.getName(), DialogPage.INFORMATION);
		}
		else {
			getPage().setMessage(null);
		}

		super.updateContentType();
	}

	/**
	 * @see AbstractProviderSource#isValidSource()
	 */
	@Override
	protected boolean isValidSource() {
		return sourceFile.isValid();
	}

	/**
	 * @see AbstractProviderSource#getSource()
	 */
	@Override
	protected LocatableInputSupplier<? extends InputStream> getSource() {
		File file = new File(sourceFile.getStringValue());
		if (file.isAbsolute())
			return new FileIOSupplier(file);
		else {
			URI relativeURI = IOUtils.relativeFileToURI(file);
			File absoluteFile = new File(projectLocation.resolve(relativeURI));
			return new FileIOSupplier(absoluteFile, relativeURI);
		}
	}

	/**
	 * @see AbstractSource#onActivate()
	 */
	@Override
	public void onActivate() {
		sourceFile.setFocus();
	}

}
