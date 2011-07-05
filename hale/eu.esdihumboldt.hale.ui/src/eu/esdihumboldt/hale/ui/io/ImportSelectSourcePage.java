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

import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.eclipse.util.extension.FactoryFilter;
import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.core.io.ContentType;
import eu.esdihumboldt.hale.core.io.HaleIO;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.core.io.ImportProvider;
import eu.esdihumboldt.hale.core.io.service.ContentTypeService;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.io.ImportSource.SourceConfiguration;
import eu.esdihumboldt.hale.ui.io.internal.WizardPageDecorator;
import eu.esdihumboldt.hale.ui.io.source.internal.ImportSourceExtension;
import eu.esdihumboldt.hale.ui.io.source.internal.ImportSourceFactory;

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
	
	/**
	 * Import source page
	 */
	public class SourcePage extends WizardPageDecorator implements SourceConfiguration<P, T> {

		private final ImportSource<P, T> importSource;
		
		private final int index;

		private T factory;

		private ContentType contentType;

		private String message;

		private boolean complete = false;

		private int messageType = DialogPage.NONE;

		private String errorMessage;

		/**
		 * Create an import source page and add it to the {@link #sources} list.
		 * 
		 * @param importSource the corresponding import source
		 * @param parent the parent composite
		 * @param initialContentType the content type the import source page
		 *   should be initialized with, may be <code>null</code>
		 */
		public SourcePage(ImportSource<P, T> importSource, Composite parent,
				ContentType initialContentType) {
			super(ImportSelectSourcePage.this);
			
			this.importSource = importSource;
			
			importSource.setPage(this);
			importSource.setConfiguration(this);
			importSource.createControls(parent);
			
			sources.add(this);
			index = sources.size() - 1;
			
			setContentType(initialContentType);
		}

		/**
		 * @see WizardPageDecorator#getWizard()
		 */
		@Override
		public W getWizard() {
			return ImportSelectSourcePage.this.getWizard();
		}

		/**
		 * Activate the source page. This will apply the stored content type,
		 * provider factory, messages and page completeness.
		 */
		public void activate() {
			getWizard().setContentType(contentType);
			getWizard().setProviderFactory(factory);
			
			super.setMessage(message, messageType);
			super.setErrorMessage(errorMessage);
			super.setPageComplete(complete);
			
			importSource.onActivate();
		}
		
		private boolean isActive() {
			synchronized (ImportSelectSourcePage.this) {
				return index == activeIndex;
			}
		}
		
		/**
		 * @see SourceConfiguration#getFactories()
		 */
		@Override
		public Collection<T> getFactories() {
			return getWizard().getFactories();
		}

		/**
		 * @see SourceConfiguration#setProviderFactory(IOProviderFactory)
		 */
		@Override
		public void setProviderFactory(T factory) {
			this.factory = factory;
						
			if (isActive()) {
				getWizard().setProviderFactory(factory);
			}
		}

		/**
		 * @see ImportSource.SourceConfiguration#getProviderFactory()
		 */
		@Override
		public T getProviderFactory() {
			return factory;
		}

		/**
		 * @see ImportSource.SourceConfiguration#setContentType(ContentType)
		 */
		@Override
		public void setContentType(ContentType contentType) {
			this.contentType = contentType;
			
			if (isActive()) {
				getWizard().setContentType(contentType);
			}
		}

		/**
		 * @see WizardPageDecorator#getErrorMessage()
		 */
		@Override
		public String getErrorMessage() {
			return errorMessage;
		}

		/**
		 * @see WizardPageDecorator#isPageComplete()
		 */
		@Override
		public boolean isPageComplete() {
			return complete;
		}

		/**
		 * @see WizardPageDecorator#getMessage()
		 */
		@Override
		public String getMessage() {
			return message;
		}

		/**
		 * @see WizardPageDecorator#getMessageType()
		 */
		@Override
		public int getMessageType() {
			return messageType;
		}

		/**
		 * @see WizardPageDecorator#setErrorMessage(String)
		 */
		@Override
		public void setErrorMessage(String newMessage) {
			this.errorMessage = newMessage;
			
			if (isActive()) {
				super.setErrorMessage(newMessage);
			}
		}

		/**
		 * @see WizardPageDecorator#setMessage(String, int)
		 */
		@Override
		public void setMessage(String newMessage, int newType) {
			this.message = newMessage;
			this.messageType = newType;
			
			if (isActive()) {
				super.setMessage(newMessage, newType);
			}
		}

		/**
		 * @see WizardPageDecorator#setPageComplete(boolean)
		 */
		@Override
		public void setPageComplete(boolean complete) {
			this.complete = complete;
			
			if (isActive()) {
				super.setPageComplete(complete);
			}
		}

		/**
		 * @see WizardPageDecorator#setMessage(String)
		 */
		@Override
		public void setMessage(String newMessage) {
			this.message = newMessage;
			
			if (isActive()) {
				super.setMessage(newMessage);
			}
		}

		/**
		 * @see SourceConfiguration#getContentType()
		 */
		@Override
		public ContentType getContentType() {
			return contentType;
		}

		/**
		 * @return the importSource
		 */
		public ImportSource<P, T> getImportSource() {
			return importSource;
		}

		/**
		 * @return the index
		 */
		public int getIndex() {
			return index;
		}

		/**
		 * @see WizardPageDecorator#dispose()
		 */
		@Override
		public void dispose() {
			importSource.dispose(); // dispose the import source
		}

	}
	
	private final List<SourcePage> sources = new ArrayList<SourcePage>();
	
	private int activeIndex = 0;
	
	private final Set<Image> images = new HashSet<Image>();
	
	/**
	 * Default constructor
	 */
	public ImportSelectSourcePage() {
		super("import.selSource");
		setTitle("Import location");
		setDescription("Please select a source for the import");
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		// set content types for file field
		Collection<T> factories = getWizard().getFactories();
		final Set<ContentType> supportedTypes = new HashSet<ContentType>();
		for (T factory : factories) {
			supportedTypes.addAll(factory.getSupportedTypes());
		}
		
		// get compatible sources
		List<ImportSourceFactory> availableSources = ImportSourceExtension.getInstance().getFactories(new FactoryFilter<ImportSource<?,?>, ImportSourceFactory>() {
			
			@Override
			public boolean acceptFactory(ImportSourceFactory factory) {
				// check provider factory compatibility
				boolean providerMatch = factory.getProviderFactoryType().isAssignableFrom(getWizard().getFactoryClass());
				if (!providerMatch) {
					return false;
				}
				
				// check content type compatibility
				ContentType ct = factory.getContentType();
				if (ct == null) {
					return true; // any content type supported
				}
				else {
					for (ContentType candidate : supportedTypes) {
						if (HaleIO.isCompatibleContentType(candidate, ct)) {
							// at least one supported type is compatible
							return true;
						}
					}
					
					// no supported type is compatible
					return false;
				}
			}
			
			@Override
			public boolean acceptCollection(
					ExtensionObjectFactoryCollection<ImportSource<?, ?>, ImportSourceFactory> collection) {
				return false;
			}
		});
		
		if (availableSources == null || availableSources.isEmpty()) {
			Label label = new Label(page, SWT.NONE);
			label.setText("No import source available.");
		}
		else if (availableSources.size() == 1) {
			// add source directly
			createSource(availableSources.iterator().next(), page,
					supportedTypes);
			
			setActiveSource(0);
		}
		else {
			// add tab for each source
			page.setLayout(new FillLayout());
			final TabFolder tabs = new TabFolder(page, SWT.NONE);
			
			for (ImportSourceFactory sourceFactory : availableSources) {
				TabItem item = new TabItem(tabs, SWT.NONE);
				item.setText(MessageFormat.format("From {0}", sourceFactory.getDisplayName()));
				// image
				URL iconURL = sourceFactory.getIconURL();
				if (iconURL != null) {
					Image image = ImageDescriptor.createFromURL(iconURL).createImage();
					if (image != null) {
						images.add(image); // remember for disposal 
						item.setImage(image);
					}
				}
				// tooltip
				item.setToolTipText(sourceFactory.getDescription());
				
				// content
				Composite wrapper = new Composite(tabs, SWT.NONE);
				wrapper.setLayout(GridLayoutFactory.swtDefaults().create()); // for minimum margin
				Composite content = new Composite(wrapper, SWT.NONE);
				content.setLayoutData(GridDataFactory.fillDefaults().
						grab(true, true).create());
				
				createSource(sourceFactory, content, supportedTypes);
				
				item.setControl(wrapper);
			}
			
			tabs.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					setActiveSource(tabs.getSelectionIndex());
				}
				
			});
			
			setActiveSource(0);
		}
	}
	
	/**
	 * Set the active source.
	 * @param selectionIndex the index of the source to be activated
	 */
	private void setActiveSource(int selectionIndex) {
		synchronized (this) {
			activeIndex = selectionIndex;
			getActiveSource().activate();
		}
	}
	
	/**
	 * Get the active source or <code>null</code>.
	 * @return the active source
	 */
	private SourcePage getActiveSource() {
		synchronized (this) {
			if (activeIndex < sources.size()) {
				return sources.get(activeIndex);
			}
		}
		return null;
	}

	/**
	 * Create an import source and add its controls to the given composite.
	 * 
	 * @param sourceFactory the {@link ImportSource} factory
	 * @param parent the parent composite, a custom layout may be assigned by
	 *   implementors
	 * @param supportedTypes the set of supported content types
	 */
	@SuppressWarnings("unchecked")
	private void createSource(ImportSourceFactory sourceFactory,
			Composite parent, Set<ContentType> supportedTypes) {
		ImportSource<?, ?> source;
		try {
			source = sourceFactory.createExtensionObject();
		} catch (Exception e) {
			throw new RuntimeException(MessageFormat.format(
					"Could not create import source {0}", 
					sourceFactory.getIdentifier()), e);
		}
		
		// determine initial content type
		ContentType initialContentType = null;
		ContentType ct = sourceFactory.getContentType();
		while (initialContentType == null && ct != null) {
			if (supportedTypes.contains(ct)) {
				initialContentType = ct; // perfect match or parent match
			}
			
			ContentTypeService cts = OsgiUtils.getService(ContentTypeService.class);
			ct = cts.getParentType(ct);
		}
		
		ImportSource<P, T> compatibleSource = ((ImportSource<P, T>) source); //XXX alternative to casting?
		// create the source page
		new SourcePage(compatibleSource, parent, initialContentType);
	}

	/**
	 * @see IOWizardPage#updateConfiguration(IOProvider)
	 */
	@Override
	public boolean updateConfiguration(P provider) {
		SourcePage source = getActiveSource();
		if (source != null) {
			return source.getImportSource().updateConfiguration(provider);
		}
		
		return false;
	}

	/**
	 * @see HaleWizardPage#dispose()
	 */
	@Override
	public void dispose() {
		for (Image image : images) {
			image.dispose();
		}
		
		for (SourcePage source : sources) {
			source.dispose();
		}
		
		super.dispose();
	}

}
