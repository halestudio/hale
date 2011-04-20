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

package eu.esdihumboldt.hale.ui.io.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.ui.io.IOWizard;


/**
 * Utilities for the configuration page extension point
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2 
 */
public abstract class ConfigurationPageExtension {
	
	private static final ALogger log = ALoggerFactory.getLogger(ConfigurationPageExtension.class);

	/**
	 * Extension point ID
	 */
	public static final String EXTENSION_POINT_ID = "eu.esdihumboldt.hale.ui.io.config";
	
	/**
	 * Get the configuration pages registered for the given I/O provider factory
	 * @param <P> the {@link IOProvider} type used in the wizard
	 * @param <T> the {@link IOProviderFactory} type used in the wizard
	 * 
	 * @param factory the provider factory
	 * @return the configuration pages
	 */
	@SuppressWarnings("unchecked")
	public static <P extends IOProvider, T extends IOProviderFactory<P>> 
			Collection<AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>>> getConfigurationPages(T factory) {
		IConfigurationElement[] confArray = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID);
		
		List<AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>>> result = new ArrayList<AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>>>();
		
		for (IConfigurationElement conf : confArray) {
			IConfigurationElement[] factories = conf.getChildren("providerFactory");
			boolean match = false;
			for (int i = 0; i < factories.length && !match; i++) {
				IConfigurationElement fDef = factories[i];
				String id = fDef.getAttribute("id");
				if (id != null && id.equals(factory.getIdentifier())) {
					// match
					match = true;
				}
			}
			
			if (match) {
				try {
					result.add((AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>>) conf.createExecutableExtension("class"));
				} catch (CoreException e) {
					log.error("Error creating configuration page " + conf.getAttribute("class"), e);
				}
			}
		}
		
		return result;
	}
	
}
