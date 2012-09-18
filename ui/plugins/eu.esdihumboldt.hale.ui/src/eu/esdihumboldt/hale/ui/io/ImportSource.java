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

import java.util.Collection;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;

/**
 * Import source for an {@link ImportWizard}
 * 
 * @param <P> the supported {@link IOProvider} type
 * 
 * @author Simon Templer
 * @since 2.5
 */
public interface ImportSource<P extends ImportProvider> {

	/**
	 * Configuration based on an import source.
	 * 
	 * @param <P> the supported {@link IOProvider} type
	 */
	public static interface SourceConfiguration<P extends ImportProvider> {

		/**
		 * Get the available provider descriptors.
		 * 
		 * @return the available factories
		 */
		public Collection<IOProviderDescriptor> getFactories();

		/**
		 * Assign an I/O provider factory to the configuration
		 * 
		 * @param descriptor the provider descriptor to set
		 */
		public void setProviderFactory(IOProviderDescriptor descriptor);

		/**
		 * Get the provider factory assigned to the configuration.
		 * 
		 * @return the I/O provider factory
		 */
		public IOProviderDescriptor getProviderFactory();

		/**
		 * Assign a content type to the configuration
		 * 
		 * @param contentType the content type to set
		 */
		public void setContentType(IContentType contentType);

		/**
		 * Get the content type assigned to the configuration
		 * 
		 * @return the content type, may be <code>null</code>
		 */
		public IContentType getContentType();

	}

	/**
	 * Sets the containing wizard page. It may be used for displaying messages.
	 * 
	 * @param page the wizard page
	 */
	public void setPage(WizardPage page);

	/**
	 * Sets the source configuration to populate.
	 * 
	 * @param config the source configuration
	 */
	public void setConfiguration(SourceConfiguration<P> config); // TODO replace
																	// by
																	// interface?
																	// change
																	// generic
																	// typisation?

	/**
	 * Create the controls that enable the user to define the import source.
	 * {@link #setPage(WizardPage)} and
	 * {@link #setConfiguration(SourceConfiguration)} must have been called
	 * before calling this method.
	 * 
	 * @param parent the parent composite, implementors may assign a custom
	 *            layout to this composite
	 */
	public void createControls(Composite parent);

	/**
	 * Update the configuration (of the I/O provider). This is executed right
	 * before the execution.
	 * 
	 * @param provider the I/O provider to update
	 * @return if the source is valid and updating the provider was successful
	 */
	public boolean updateConfiguration(P provider);

	/**
	 * Method that is called when the source page is activated.
	 */
	public void onActivate();

	/**
	 * Dispose any resources when the source page is disposed.
	 */
	public void dispose();

}
