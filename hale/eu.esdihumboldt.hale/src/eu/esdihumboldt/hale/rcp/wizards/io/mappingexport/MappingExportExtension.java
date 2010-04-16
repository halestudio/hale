/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.wizards.io.mappingexport;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/**
 *handles programmatic access to the configuration of all loaded 
 *{@link MappingExportProvider}s.
 * 
 * @author Thorsten Reitz
 */
public class MappingExportExtension {
	
	/**
	 * The extension point ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.MappingExport";
	
	
	private static Map<String, IConfigurationElement> confMap = null;
	
	/**
	 * @return a Map with Information on the formats supported by the 
	 * registered export providers. The keys of the map are the name of the format,
	 * the values the file extension that will be used.
	 */
	public static Map<String, String> getRegisteredExportProviderInfo() {
		if (getConfMap() == null) {
			initializeConfArray();
		}
		Map<String, String> result = new HashMap<String, String>();
		for (IConfigurationElement confElement : confMap.values()) {
			result.put(
					confElement.getAttribute("name"), 
					confElement.getAttribute("extension"));
		}
		return result;
	}
	
	/**
	 * @param name the name of a {@link MappingExportProvider}.
	 * @return the {@link MappingExportProvider} matching the given name.
	 */
	public static MappingExportProvider getExportProvider(final String name) {
		if (getConfMap() == null) {
			initializeConfArray();
		}
		IConfigurationElement configElement = confMap.get(name);
		if (configElement != null) {
			try {
				return (MappingExportProvider) configElement.createExecutableExtension("providerClass");
			} catch (CoreException e) {
				throw new RuntimeException("Error creating the export provider.", e);
			}
		}
		else {
			throw new RuntimeException("The name " + name + " does notindicate " +
					"a known MappingExportProvider.");
		}
	}
	
	private static synchronized Map<String, IConfigurationElement>  getConfMap() {
		return confMap;
	}
	
	private static synchronized void initializeConfArray() {
		IConfigurationElement[] confArray = Platform.getExtensionRegistry().getConfigurationElementsFor(ID);
		confMap = new HashMap<String, IConfigurationElement>();
		for (IConfigurationElement conf : confArray) {
			confMap.put(conf.getAttribute("name"), conf);
		}
	}

}
