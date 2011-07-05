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

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.core.io.ContentType;
import eu.esdihumboldt.hale.core.io.HaleIO;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.core.io.ImportProvider;
import eu.esdihumboldt.hale.core.io.service.ContentTypeService;
import eu.esdihumboldt.hale.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;
import eu.esdihumboldt.hale.ui.io.ImportSource;
import eu.esdihumboldt.hale.ui.io.util.URLFieldEditor;

/**
 * URL import source
 * @param <P> the supported {@link IOProvider} type
 * @param <T> the supported {@link IOProviderFactory} type
 * 
 * @author Simon Templer
 * @since 2.2 
 */
public class URLSource<P extends ImportProvider, T extends IOProviderFactory<P>> extends AbstractSource<P, T> {
	
	/**
	 * The file field editor for the source URL
	 */
	private URLFieldEditor sourceURL;
	
	/**
	 * The set of supported content types
	 */
	private Set<ContentType> supportedTypes;

	private ComboViewer providers;

	private ComboViewer types;

	private Button detect;
	
	private Image detectImage;

	/**
	 * @see ImportSource#createControls(Composite)
	 */
	@Override
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		
		detectImage = HALEUIPlugin.getImageDescriptor("icons/find_obj.gif").createImage();
		
		// source file
		sourceURL = new URLFieldEditor("sourceURL", "Source URL:", parent);
		sourceURL.setPage(getPage());
		
		// set content types for file field
		Collection<T> factories = getConfiguration().getFactories();
		supportedTypes = new HashSet<ContentType>();
		for (T factory : factories) {
			supportedTypes.addAll(factory.getSupportedTypes());
		}
		
		sourceURL.setPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					getPage().setMessage(null);
					updateState(false);
				}
				else if (event.getProperty().equals(FieldEditor.VALUE)) {
					getPage().setMessage(null);
					updateState(false);
				}
			}
		});
		
		// content type selection
		
		// label
		Label typesLabel = new Label(parent, SWT.NONE);
		typesLabel.setText("Content type");
		
		// types combo
		Composite group = new Composite(parent, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
		
		types = new ComboViewer(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		types.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		types.setContentProvider(ArrayContentProvider.getInstance());
		types.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof ContentType) {
					return HaleIO.getDisplayName((ContentType) element);
				}
				return super.getText(element);
			}
			
		});
		types.setInput(supportedTypes);
		
		// process selection changes
		types.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateState(true);
			}
		});
		
		// detect button
		detect = new Button(group, SWT.PUSH);
		detect.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		detect.setText("Detect");
		detect.setImage(detectImage);
		detect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					getPage().getWizard().getContainer().run(true, false, new IRunnableWithProgress() {
						
						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException,
								InterruptedException {
							monitor.beginTask("Detect content type", IProgressMonitor.UNKNOWN);
							
							final ContentType detected = detectContentType();
							
							PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
								
								@Override
								public void run() {
									if (detected != null) {
										types.setSelection(new StructuredSelection(detected));
										getPage().setMessage(MessageFormat.format(
												"Detected {0} as content type",
												HaleIO.getDisplayName(detected)), 
												DialogPage.INFORMATION);
										updateState(true);
									}
									else {
										types.setSelection(new StructuredSelection());
										getPage().setMessage("Could not detect content type. The resource might be not available or it has no matching content type.", DialogPage.WARNING);
										updateState(true);
									}
								}
							});
							
							monitor.done();
						}
					});
				} catch (Throwable t) {
					getPage().setErrorMessage("Starting the task to detect the content type failed");
				}
				
			}
		});
		
		// provider selection
		
		// label
		Label providerLabel = new Label(parent, SWT.NONE);
		providerLabel.setText("Import as");
		
		// create provider combo
		providers = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		providers.getControl().setLayoutData(new GridData(SWT.FILL, 
				SWT.BEGINNING, true, false));
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
	 * Detect the content type
	 * @return the detected content type or <code>null</code>
	 */
	private ContentType detectContentType() {
		ContentTypeService cts = OsgiUtils.getService(ContentTypeService.class);
		
		final Display display = PlatformUI.getWorkbench().getDisplay();
		final AtomicReference<String> sourceString = new AtomicReference<String>();
		final AtomicReference<URI> sourceURI = new AtomicReference<URI>();
		
		display.syncExec(new Runnable() {
			
			@Override
			public void run() {
				if (sourceURL.isValid() && sourceURL.getURL() != null) {
					sourceString.set(sourceURL.getStringValue());
					try {
						sourceURI.set(sourceURL.getURL().toURI());
					} catch (Throwable e) {
						getPage().setErrorMessage(e.getLocalizedMessage());
					}
				}
			}
		});
		
		if (sourceURI.get() != null && sourceString.get() != null) {
			// determine content type
			Collection<ContentType> filteredTypes;
			filteredTypes = cts.findContentTypesFor(
					supportedTypes, new DefaultInputSupplier(sourceURI.get()), 
					sourceString.get());
			
			if (!filteredTypes.isEmpty()) {
				return filteredTypes.iterator().next();
			}
		}
		
		return null;
	}
	
	private void updateContentType() {
		ContentType ct = null;
		ISelection typeSel = types.getSelection();
		if (!typeSel.isEmpty() && typeSel instanceof IStructuredSelection) {
			ct = (ContentType) ((IStructuredSelection) typeSel).getFirstElement();
		}
		
		getConfiguration().setContentType(ct);
		
		updateProvider();
	}

	/**
	 * Update the provider selector
	 */
	@SuppressWarnings("unchecked")
	private void updateProvider() {
		ContentType contentType = getConfiguration().getContentType();
		if (contentType  != null) {
			T lastSelected = null;
			ISelection provSel = providers.getSelection();
			if (!provSel.isEmpty() && provSel instanceof IStructuredSelection) {
				lastSelected = (T) ((IStructuredSelection) provSel).getFirstElement();
			}
			
			List<T> supported = HaleIO.filterFactories(getConfiguration().getFactories(), contentType);
			providers.setInput(supported);
			
			if (lastSelected != null && supported.contains(lastSelected)) {
				// reuse old selection
				providers.setSelection(new StructuredSelection(lastSelected), true);
			}
			else if (!supported.isEmpty()) {
				// select first provider
				providers.setSelection(new StructuredSelection(supported.get(0)), true);
			}
			
			providers.getControl().setEnabled(supported.size() > 1);
		}
		else {
			providers.setInput(null);
			providers.getControl().setEnabled(false);
		}
		
	}

	/**
	 * Update the page state
	 * @param updateContentType if the content type shall be updated in the 
	 *   configuration
	 */
	@SuppressWarnings("unchecked")
	private void updateState(boolean updateContentType) {
		boolean enableSelection = sourceURL.isValid() && sourceURL.getURL() != null;
		
		detect.setEnabled(enableSelection);
		types.getControl().setEnabled(enableSelection);
		
		if (!enableSelection && types.getSelection() != null && !types.getSelection().isEmpty()) {
			types.setSelection(new StructuredSelection());
			updateContentType = true;
		}
		
		if (updateContentType) {
			updateContentType();
		}
		
		// update provider factory
		ISelection provSel = providers.getSelection();
		if (!provSel.isEmpty() && provSel instanceof IStructuredSelection) {
			getConfiguration().setProviderFactory((T) ((IStructuredSelection) provSel).getFirstElement());
		}
		else {
			getConfiguration().setProviderFactory(null);
		}
		
		getPage().setPageComplete(sourceURL.isValid() && 
				getConfiguration().getContentType() != null && 
				getConfiguration().getProviderFactory() != null);
	}

	/**
	 * @see AbstractSource#updateConfiguration(ImportProvider)
	 */
	@Override
	public boolean updateConfiguration(P provider) {
		boolean ok = super.updateConfiguration(provider);
		if (!ok) {
			return ok;
		}
		
		URL url = sourceURL.getURL();
		if (url != null) {
			URI uri;
			try {
				uri = url.toURI();
			} catch (URISyntaxException e) {
				//TODO set error message?
				return false;
			}
			provider.setSource(new DefaultInputSupplier(uri));
			return true;
		}
		
		return false;
	}

	/**
	 * @see AbstractSource#dispose()
	 */
	@Override
	public void dispose() {
		if (detectImage != null) {
			detectImage.dispose();
		}
	}

	/**
	 * @see AbstractSource#onActivate()
	 */
	@Override
	public void onActivate() {
		sourceURL.setFocus();
	}

}
