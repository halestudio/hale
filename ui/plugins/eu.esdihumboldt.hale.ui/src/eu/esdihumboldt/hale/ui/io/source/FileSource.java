/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.io.source;

import java.io.File;
import java.io.InputStream;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.ui.io.ImportSource;
import eu.esdihumboldt.hale.ui.io.util.OpenFileFieldEditor;

/**
 * File import source
 * 
 * @param <P> the supported {@link IOProvider} type
 * 
 * @author Simon Templer
 * @since 2.5
 */
public class FileSource<P extends ImportProvider> extends AbstractProviderSource<P> {

	/**
	 * The file field editor for the source file
	 */
	private OpenFileFieldEditor sourceFile;

	/**
	 * The set of supported content types
	 */
	private Set<IContentType> supportedTypes;

	/**
	 * @see ImportSource#createControls(Composite)
	 */
	@Override
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(3, false));

		// source file
		sourceFile = new OpenFileFieldEditor("sourceFile", "Source file:", true,
				FileFieldEditor.VALIDATE_ON_KEY_STROKE, parent);
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
		providers.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

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
					null, sourceFile.getStringValue());
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
		return new FileIOSupplier(file);
	}

	/**
	 * @see AbstractSource#onActivate()
	 */
	@Override
	public void onActivate() {
		sourceFile.setFocus();
	}

}
