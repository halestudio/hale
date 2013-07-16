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

package eu.esdihumboldt.hale.ui.io;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;
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

import de.cs3d.util.eclipse.extension.ExtensionObjectFactoryCollection;
import de.cs3d.util.eclipse.extension.FactoryFilter;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.io.ImportSource.SourceConfiguration;
import eu.esdihumboldt.hale.ui.io.internal.WizardPageDecorator;
import eu.esdihumboldt.hale.ui.io.source.internal.ImportSourceExtension;
import eu.esdihumboldt.hale.ui.io.source.internal.ImportSourceFactory;

/**
 * Wizard page that allows selecting a source file or provider.
 * 
 * @param <W> the concrete I/O wizard type
 * @param <P> the {@link IOProvider} type used in the wizard
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public class ImportSelectSourcePage<P extends ImportProvider, W extends ImportWizard<P>> extends
		IOWizardPage<P, W> {

	/**
	 * Import source page
	 */
	public class SourcePage extends WizardPageDecorator implements SourceConfiguration<P> {

		private final ImportSource<P> importSource;

		private final int index;

		private IOProviderDescriptor factory;

		private IContentType contentType;

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
		 *            should be initialized with, may be <code>null</code>
		 */
		public SourcePage(ImportSource<P> importSource, Composite parent,
				IContentType initialContentType) {
			super(ImportSelectSourcePage.this);

			this.importSource = importSource;

			sources.add(this);
			index = sources.size() - 1;

			setContentType(initialContentType);

			importSource.setPage(this);
			importSource.setConfiguration(this);
			importSource.createControls(parent);
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
		public Collection<IOProviderDescriptor> getFactories() {
			return getWizard().getFactories();
		}

		/**
		 * @see SourceConfiguration#setProviderFactory(IOProviderDescriptor)
		 */
		@Override
		public void setProviderFactory(IOProviderDescriptor factory) {
			this.factory = factory;

			if (isActive()) {
				getWizard().setProviderFactory(factory);
			}
		}

		/**
		 * @see ImportSource.SourceConfiguration#getProviderFactory()
		 */
		@Override
		public IOProviderDescriptor getProviderFactory() {
			return factory;
		}

		/**
		 * @see ImportSource.SourceConfiguration#setContentType(IContentType)
		 */
		@Override
		public void setContentType(IContentType contentType) {
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
		public IContentType getContentType() {
			return contentType;
		}

		/**
		 * @return the importSource
		 */
		public ImportSource<P> getImportSource() {
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
		List<IOProviderDescriptor> factories = getWizard().getFactories();
		final Set<IContentType> supportedTypes = new HashSet<IContentType>();
		for (IOProviderDescriptor factory : factories) {
			supportedTypes.addAll(factory.getSupportedTypes());
		}

		// get compatible sources
		List<ImportSourceFactory> availableSources = ImportSourceExtension.getInstance()
				.getFactories(new FactoryFilter<ImportSource<?>, ImportSourceFactory>() {

					@Override
					public boolean acceptFactory(ImportSourceFactory factory) {
						// check provider factory compatibility
						boolean providerMatch = factory.getProviderType().isAssignableFrom(
								getWizard().getProviderType());
						if (!providerMatch) {
							return false;
						}

						// check content type compatibility
						IContentType ct = factory.getContentType();
						if (ct == null) {
							return true; // any content type supported
						}
						else {
							// stated type must be present
							return supportedTypes.contains(ct);
						}
					}

					@Override
					public boolean acceptCollection(
							ExtensionObjectFactoryCollection<ImportSource<?>, ImportSourceFactory> collection) {
						return false;
					}
				});

		if (availableSources == null || availableSources.isEmpty()) {
			Label label = new Label(page, SWT.NONE);
			label.setText("No import source available.");
		}
		else if (availableSources.size() == 1) {
			// add source directly
			createSource(availableSources.iterator().next(), page, supportedTypes);

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
				wrapper.setLayout(GridLayoutFactory.swtDefaults().create()); // for
																				// minimum
																				// margin
				Composite content = new Composite(wrapper, SWT.NONE);
				content.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

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
	 * 
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
	 * 
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
	 *            implementors
	 * @param supportedTypes the set of supported content types
	 */
	@SuppressWarnings("unchecked")
	private void createSource(ImportSourceFactory sourceFactory, Composite parent,
			Set<IContentType> supportedTypes) {
		ImportSource<?> source;
		try {
			source = sourceFactory.createExtensionObject();
		} catch (Exception e) {
			throw new RuntimeException(MessageFormat.format("Could not create import source {0}",
					sourceFactory.getIdentifier()), e);
		}

		// determine initial content type
		IContentType initialContentType = sourceFactory.getContentType();
		assert supportedTypes.contains(initialContentType);

		ImportSource<P> compatibleSource = ((ImportSource<P>) source); // XXX
																		// alternative
																		// to
																		// casting?

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
