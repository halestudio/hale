/*
 * Copyright (c) 2021 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.io.source;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
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
import eu.esdihumboldt.hale.common.core.io.supplier.FilesIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.ui.io.ImportSource;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.util.io.IOUtils;

/**
 * Abstract class to have common functionality to import multiple files at once.
 * 
 * @param <P> the supported {@link IOProvider} type
 * 
 * @author Kapil Agnihotri
 */
public abstract class AbstractMultipleFilesSource<P extends ImportProvider>
		extends AbstractProviderSource<P> {

	/**
	 * The file field editor for the source file
	 */
	private AbstractMultipleFilesSourceFileFieldEditor sourceFile;

	/**
	 * The set of supported content types
	 */
	private Set<IContentType> supportedTypes;

	private URI projectLocation;

	/**
	 * @return the projectLocation
	 */
	public URI getProjectLocation() {
		return projectLocation;
	}

	/**
	 * @param projectLocation the projectLocation to set
	 */
	public void setProjectLocation(URI projectLocation) {
		this.projectLocation = projectLocation;
	}

	/**
	 * @param parent parent
	 * @param projectLocation location the current project was loaded from. May
	 *            be <code>null</code>.
	 * @return
	 */
	abstract public AbstractMultipleFilesSourceFileFieldEditor getSourceFile(Composite parent,
			URI projectLocation);

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

		sourceFile = getSourceFile(parent, projectLocation);

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
					sourceFile.getStringValues().forEach(k -> updateState(true));
				}
				else if (event.getProperty().equals(FieldEditor.VALUE)) {
					sourceFile.getStringValues().forEach(k -> updateState(true));
				}
			}

		});

		Label providerLabel = new Label(parent, SWT.NONE);
		providerLabel.setText("Import as");
		// create provider combo
		ComboViewer providers = createProviders(parent);
		providers.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));

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

		if (sourceFile.isValid()) {
			// determine content type
			LocatableInputSupplier<? extends InputStream> source = getSource();
			Collection<IContentType> filteredTypes = HaleIO.findContentTypesFor(supportedTypes,
					source, sourceFile.getStringValues().get(0));
			IContentType contentType = null;
			if (!filteredTypes.isEmpty()) {
				contentType = filteredTypes.iterator().next();
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
	}

	/**
	 * @see AbstractProviderSource#isValidSource()
	 */
	@Override
	protected boolean isValidSource() {
		return sourceFile.isValid();
	}

	/**
	 * @see AbstractSource#onActivate()
	 */
	@Override
	public void onActivate() {
		sourceFile.setFocus();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.source.AbstractProviderSource#getSource()
	 */
	@Override
	protected LocatableInputSupplier<? extends InputStream> getSource() {
		List<String> stringValues = sourceFile.getStringValues();
		List<File> files = new ArrayList<File>();
		List<URI> uris = new ArrayList<>();
		File file = new File(stringValues.get(0));
		if (file.isAbsolute()) {
			for (String s : stringValues) {
				files.add(new File(s));
				uris.add(new File(s).toURI());
			}
		}
		else {
			for (String s : stringValues) {
				files.add(new File(s));
				uris.add(IOUtils.relativeFileToURI(new File(s)));
			}
		}
		FilesIOSupplier filesIOSupplier = new FilesIOSupplier(files, uris);
		return filesIOSupplier;
	}

	/**
	 * Update the provider selector when the content type has changed. This is
	 * based on the content type stored in the source configuration.
	 */
	@Override
	protected void updateProvider() {
		IContentType contentType = getConfiguration().getContentType();
		if (contentType != null) {
			IOProviderDescriptor lastSelected = null;
			ISelection provSel = getProviders().getSelection();
			if (!provSel.isEmpty() && provSel instanceof IStructuredSelection) {
				lastSelected = (IOProviderDescriptor) ((IStructuredSelection) provSel)
						.getFirstElement();
			}

			List<IOProviderDescriptor> supported = HaleIO
					.filterFactories(getConfiguration().getFactories(), contentType);
			getProviders().setInput(supported);

			if (lastSelected != null && supported.contains(lastSelected)) {
				// reuse old selection
				getProviders().setSelection(new StructuredSelection(lastSelected), true);
			}
			else if (!supported.isEmpty()) {
				// select first provider
				getProviders().setSelection(new StructuredSelection(supported.get(0)), true);
			}

			getProviders().getControl().setEnabled(supported.size() > 1);
		}
		else {
			getProviders().setInput(null);
			getProviders().getControl().setEnabled(false);
		}

	}

	/**
	 * Update the page state. This includes setting a provider factory on the
	 * wizard if applicable and setting the complete state of the page.<br>
	 * <br>
	 * This should be called in {@link #createControls(Composite)} to initialize
	 * the page state.
	 * 
	 * @param updateContentType if <code>true</code> the content type and the
	 *            supported providers will be updated before updating the page
	 *            state
	 */
	@Override
	protected void updateState(boolean updateContentType) {
		if (updateContentType) {
			updateContentType();
		}

		// update provider factory
		ISelection provSel = getProviders().getSelection();
		if (!provSel.isEmpty() && provSel instanceof IStructuredSelection) {
			getConfiguration().setProviderFactory(
					(IOProviderDescriptor) ((IStructuredSelection) provSel).getFirstElement());
		}
		else {
			getConfiguration().setProviderFactory(null);
		}

		getPage().setPageComplete(isValidSource() && getConfiguration().getContentType() != null
				&& getConfiguration().getProviderFactory() != null);
	}

}
