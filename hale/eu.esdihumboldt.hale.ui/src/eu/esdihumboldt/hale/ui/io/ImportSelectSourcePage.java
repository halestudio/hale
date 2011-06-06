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

package eu.esdihumboldt.hale.ui.io;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
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

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.core.io.ContentType;
import eu.esdihumboldt.hale.core.io.HaleIO;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.core.io.ImportProvider;
import eu.esdihumboldt.hale.core.io.service.ContentTypeService;
import eu.esdihumboldt.hale.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.io.util.OpenFileFieldEditor;

/**
 * Wizard page that allows selecting a source file or provider
 * @param <W> the concrete I/O wizard type
 * @param <P> the {@link IOProvider} type used in the wizard
 * @param <T> the {@link IOProviderFactory} type used in the wizard
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ImportSelectSourcePage<P extends ImportProvider, T extends IOProviderFactory<P>, 
	W extends ImportWizard<P, T>> extends IOWizardPage<P, T, W> {
	
//	private static final ALogger log = ALoggerFactory.getLogger(ImportSelectSourcePage.class);
	
	/**
	 * The file field editor for the source file
	 */
	private OpenFileFieldEditor sourceFile;
	
	/**
	 * The set of supported content types
	 */
	private Set<ContentType> supportedTypes;

	private ComboViewer providers;

	/**
	 * Default constructor
	 */
	public ImportSelectSourcePage() {
		super("import.selSource");
		setTitle("Export destination");
		setDescription("Please select a destination file for the export");
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(3, false));
		
		// source file
		sourceFile = new OpenFileFieldEditor("sourceFile", "Source file:", true,
				FileFieldEditor.VALIDATE_ON_KEY_STROKE, page);
		sourceFile.setEmptyStringAllowed(false);
		sourceFile.setPage(this);
		
		// set content types for file field
		Collection<T> factories = getWizard().getFactories();
		supportedTypes = new HashSet<ContentType>();
		for (T factory : factories) {
			supportedTypes.addAll(factory.getSupportedTypes());
		}
		
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
		Label providerLabel = new Label(page, SWT.NONE);
		providerLabel.setText("Import as");
		
		// create provider combo
		providers = new ComboViewer(page, SWT.DROP_DOWN | SWT.READ_ONLY);
		providers.getControl().setLayoutData(new GridData(SWT.FILL, 
				SWT.BEGINNING, true, false, 2, 1));
		providers.setContentProvider(ArrayContentProvider.getInstance());
		providers.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof IOProviderFactory<?>) {
					return ((IOProviderFactory<?>) element).getDisplayName();
				}
				return super.getText(element);
			}
			
		});
		
		// process selection changes
		providers.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateState(false);
			}
		});
		
		updateState(true);
	}
	
	/**
	 * Update the content type
	 */
	private void updateContentType() {
		ContentType contentType = null;
		ContentTypeService cts = OsgiUtils.getService(ContentTypeService.class);
		
		if (sourceFile.isValid()) {
			// determine content type
			Collection<ContentType> filteredTypes = cts.findContentTypesFor(
					supportedTypes, null, sourceFile.getStringValue());
			if (!filteredTypes.isEmpty()) {
				contentType = filteredTypes.iterator().next();
			}
		}
		
		getWizard().setContentType(contentType);
		if (contentType != null) {
			setMessage(cts.getDisplayName(contentType), DialogPage.INFORMATION);
		}
		else {
			setMessage(null);
		}
		
		// update provider selector
		updateProvider();
	}

	/**
	 * Update the provider selector
	 */
	@SuppressWarnings("unchecked")
	private void updateProvider() {
		ContentType contentType = getWizard().getContentType();
		if (contentType  != null) {
			T lastSelected = null;
			ISelection provSel = providers.getSelection();
			if (!provSel.isEmpty() && provSel instanceof IStructuredSelection) {
				lastSelected = (T) ((IStructuredSelection) provSel).getFirstElement();
			}
			
			List<T> supported = HaleIO.filterFactories(getWizard().getFactories(), contentType);
			providers.setInput(supported);
			
			if (lastSelected != null && supported.contains(lastSelected)) {
				// reuse old selection
				providers.setSelection(new StructuredSelection(lastSelected), true);
			}
			else if (!supported.isEmpty()) {
				// select first provider
				providers.setSelection(new StructuredSelection(supported.get(0)), true);
			}
		}
		else {
			providers.setInput(null);
		}
	}

	/**
	 * Update the page state
	 * @param updateContentType if <code>true</code> the content type and the
	 *   supported providers will be updated before updating the page state
	 */
	@SuppressWarnings("unchecked")
	private void updateState(boolean updateContentType) {
		if (updateContentType) {
			updateContentType();
		}
		
		// update provider factory
		ISelection provSel = providers.getSelection();
		if (!provSel.isEmpty() && provSel instanceof IStructuredSelection) {
			getWizard().setProviderFactory((T) ((IStructuredSelection) provSel).getFirstElement());
		}
		else {
			getWizard().setProviderFactory(null);
		}
		
		setPageComplete(sourceFile.isValid() && 
				getWizard().getProviderFactory() != null);
	}

	/**
	 * @see IOWizardPage#updateConfiguration(IOProvider)
	 */
	@Override
	public boolean updateConfiguration(P provider) {
		File file = new File(sourceFile.getStringValue());
		provider.setSource(new FileIOSupplier(file));
		return true;
	}

	/**
	 * Get the source file name
	 *  
	 * @return the source file name
	 */
	public String getSourceFileName() {
		return sourceFile.getStringValue();
	}
	
}
