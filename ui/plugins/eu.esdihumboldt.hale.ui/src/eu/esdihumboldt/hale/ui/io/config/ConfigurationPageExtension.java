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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderFactory;
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
	 * Get the configuration pages registered for the given I/O provider factories
	 * @param <P> the {@link IOProvider} type used in the wizard
	 * @param <T> the {@link IOProviderFactory} type used in the wizard
	 * 
	 * @param factories the provider factories
	 * @return the configuration pages in a multimap where the corresponding 
	 *   provider identifier is mapped to the configuration page, one page (the
	 *   same instance) might be mapped for multiple identifiers
	 */
	@SuppressWarnings("unchecked")
	public static <P extends IOProvider, T extends IOProviderFactory<P>> 
			Multimap<String, AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>>> getConfigurationPages(Iterable<T> factories) {
		IConfigurationElement[] confArray = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID);
		
		// collect factory IDs
		Set<String> ids = new HashSet<String>(); 
		for (T factory : factories) {
			ids.add(factory.getIdentifier());
		}
		
		List<IConfigurationElement> sortedConfs = Arrays.asList(confArray);
		Collections.sort(sortedConfs, new Comparator<IConfigurationElement>() {

			@Override
			public int compare(IConfigurationElement o1,
					IConfigurationElement o2) {
				return getOrder(o1).compareTo(getOrder(o2));
			}
			
		});
		
		ListMultimap<String, AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>>> result = ArrayListMultimap.create();
		
		for (IConfigurationElement conf : sortedConfs) {
			IConfigurationElement[] pageFactories = conf.getChildren("providerFactory");
			AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>> page = null;
			for (int i = 0; i < pageFactories.length; i++) {
				IConfigurationElement fDef = pageFactories[i];
				String id = fDef.getAttribute("id");
				if (id != null && ids.contains(id)) {
					// match
					if (page == null) {
						try {
							page = (AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>>) conf.createExecutableExtension("class");
						} catch (CoreException e) {
							log.error("Error creating configuration page " + conf.getAttribute("class"), e);
							break;
						}
					}
					
					if (page != null) {
						result.put(id, page);
					}
				}
			}
		}
		
		return result;
	}

	/**
	 * Get the order for the given configuration element
	 * @param conf the configuration element
	 * @return the element order
	 */
	protected static Integer getOrder(IConfigurationElement conf) {
		String orderString = conf.getAttribute("order");
		int order;
		if (orderString == null) {
			order = 0;
		}
		else {
			try {
				order = Integer.parseInt(orderString);
			} catch (Exception e) {
				order = 0;
			}
		}
		
		return Integer.valueOf(order);
	}
	
}
