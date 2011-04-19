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

import org.eclipse.jface.wizard.Wizard;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;

/**
 * Abstract I/O wizard based on {@link IOProvider} factories
 * @param <P> the {@link IOProvider} type used in the wizard
 * @param <T> the {@link IOProviderFactory} type used in the wizard
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class IOWizard<P extends IOProvider, T extends IOProviderFactory<P>> extends Wizard {

	private final Class<T> factoryClass;
	
	private P provider;
	
	private T factory;

	/**
	 * Create an I/O wizard
	 * 
	 * @param factoryClass the class of the I/O provider factory
	 */
	public IOWizard(Class<T> factoryClass) {
		super();
		this.factoryClass = factoryClass;
	}
	
	/**
	 * Get the available factories. 
	 * To filter or sort them you can override this method.
	 * 
	 * @return the available factories
	 */
	protected Collection<T> getFactories() {
		return OsgiUtils.getServices(factoryClass);
	}

	/**
	 * Get the provider assigned to the wizard. It will be <code>null</code> if
	 * no page assigned a provider factory to the wizard yet.
	 * 
	 * @return the I/O provider
	 */
	protected P getProvider() {
		if (provider == null && factory != null) {
			provider = factory.createProvider();
		}
		
		return provider;
	}

	/**
	 * Assign an I/O provider factory to the wizard
	 * 
	 * @param factory the provider to set
	 */
	protected void setProviderFactory(T factory) {
		this.factory = factory;
		
		// reset provider
		provider = null;
	}

}
