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

import java.util.Collection;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.io.ContentType;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;

/**
 * Import source for an {@link ImportWizard}
 * @param <P> the supported {@link IOProvider} type
 * @param <T> the supported {@link IOProviderFactory} type
 * 
 * @author Simon Templer
 * @since 2.2
 */
public interface ImportSource<P extends ImportProvider, T extends IOProviderFactory<P>> {

	/**
	 * Configuration based on an import source.
	 * @param <P> the supported {@link IOProvider} type
	 * @param <T> the supported {@link IOProviderFactory} type
	 */
	public static interface SourceConfiguration<P extends ImportProvider, T extends IOProviderFactory<P>> {

		/**
		 * Get the available factories. 
		 * 
		 * @return the available factories
		 */
		public Collection<T> getFactories();
		
		/**
		 * Assign an I/O provider factory to the configuration
		 * 
		 * @param factory the provider factory to set
		 */
		public void setProviderFactory(T factory);
		
		/**
		 * Get the provider factory assigned to the configuration.
		 * 
		 * @return the I/O provider factory
		 */
		public T getProviderFactory();
		
		/**
		 * Assign a content type to the configuration
		 * 
		 * @param contentType the content type to set
		 */
		public void setContentType(ContentType contentType);
		
		/**
		 * Get the content type assigned to the configuration
		 * 
		 * @return the content type, may be <code>null</code>
		 */
		public ContentType getContentType();
		
	}

	/**
	 * Sets the containing wizard page. It may be used for displaying messages.
	 * @param page the wizard page
	 */
	public void setPage(WizardPage page);

	/**
	 * Sets the source configuration to populate.
	 * @param config the source configuration
	 */
	public void setConfiguration(SourceConfiguration<P, T> config); //TODO replace by interface? change generic typisation?
	
	/**
	 * Create the controls that enable the user to define the import source.
	 * {@link #setPage(WizardPage)} and {@link #setConfiguration(SourceConfiguration)} must
	 * have been called before calling this method.
	 * @param parent the parent composite, implementors may assign a custom 
	 *   layout to this composite
	 */
	public void createControls(Composite parent);
	
	/**
	 * Update the configuration (of the I/O provider). This is executed
	 * right before the execution.
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
