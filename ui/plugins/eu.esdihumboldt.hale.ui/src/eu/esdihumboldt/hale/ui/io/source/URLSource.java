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

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentType;
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

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;
import eu.esdihumboldt.hale.ui.io.ImportSource;
import eu.esdihumboldt.hale.ui.io.util.URLFieldEditor;

/**
 * URL import source
 * @param <P> the supported {@link IOProvider} type
 * 
 * @author Simon Templer
 * @since 2.5 
 */
public class URLSource<P extends ImportProvider> extends AbstractProviderSource<P> {
	
	/**
	 * The file field editor for the source URL
	 */
	private URLFieldEditor sourceURL;
	
	/**
	 * The set of supported content types
	 */
	private Set<IContentType> supportedTypes;

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
		Collection<IOProviderDescriptor> factories = getConfiguration().getFactories();
		supportedTypes = new HashSet<IContentType>();
		for (IOProviderDescriptor factory : factories) {
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
				if (element instanceof IContentType) {
					return ((IContentType) element).getName();
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
					getPage().getWizard().getContainer().run(false, false, new IRunnableWithProgress() {
						
						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException,
								InterruptedException {
							monitor.beginTask("Detect content type", IProgressMonitor.UNKNOWN);
							
							final IContentType detected = detectContentType();
							
							PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
								
								@Override
								public void run() {
									if (detected != null) {
										types.setSelection(new StructuredSelection(detected));
										getPage().setMessage(MessageFormat.format(
												"Detected {0} as content type",
												detected.getName()), 
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
		ComboViewer providers = createProviders(parent);
		providers.getControl().setLayoutData(new GridData(SWT.FILL, 
				SWT.BEGINNING, true, false));
		
		// initial state update
		updateState(true);
	}
	
	/**
	 * Detect the content type
	 * @return the detected content type or <code>null</code>
	 */
	private IContentType detectContentType() {
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
			Collection<IContentType> filteredTypes;
			filteredTypes = HaleIO.findContentTypesFor(
					supportedTypes, new DefaultInputSupplier(sourceURI.get()), 
					sourceString.get());
			
			if (!filteredTypes.isEmpty()) {
				return filteredTypes.iterator().next();
			}
		}
		
		return null;
	}
	
	/**
	 * @see AbstractProviderSource#updateContentType()
	 */
	@Override
	protected void updateContentType() {
		IContentType ct = null;
		ISelection typeSel = types.getSelection();
		if (!typeSel.isEmpty() && typeSel instanceof IStructuredSelection) {
			ct = (IContentType) ((IStructuredSelection) typeSel).getFirstElement();
		}
		
		getConfiguration().setContentType(ct);
		
		super.updateContentType();
	}

	/**
	 * @see AbstractProviderSource#updateState(boolean)
	 */
	@Override
	protected void updateState(boolean updateContentType) {
		boolean enableSelection = sourceURL.isValid() && sourceURL.getURL() != null;
		
		detect.setEnabled(enableSelection);
		types.getControl().setEnabled(enableSelection);
		
		if (!enableSelection && types.getSelection() != null && !types.getSelection().isEmpty()) {
			types.setSelection(new StructuredSelection());
			updateContentType = true;
		}
		
		super.updateState(updateContentType);
	}

	/**
	 * @see AbstractProviderSource#isValidSource()
	 */
	@Override
	protected boolean isValidSource() {
		return sourceURL.isValid() && sourceURL.getURL() != null;
	}

	/**
	 * @see AbstractProviderSource#getSource()
	 */
	@Override
	protected LocatableInputSupplier<? extends InputStream> getSource() {
		URL url = sourceURL.getURL();
		if (url != null) {
			URI uri;
			try {
				uri = url.toURI();
			} catch (URISyntaxException e) {
				//TODO set error message?
				return null;
			}
			return new DefaultInputSupplier(uri);
		}
		
		return null;
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
