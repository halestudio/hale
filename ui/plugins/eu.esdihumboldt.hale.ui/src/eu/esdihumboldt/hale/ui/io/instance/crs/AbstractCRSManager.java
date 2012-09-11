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

package eu.esdihumboldt.hale.ui.io.instance.crs;

import eu.esdihumboldt.hale.common.instance.geometry.CRSDefinitionManager;
import eu.esdihumboldt.hale.common.instance.geometry.CRSProvider;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;

/**
 * Base class for CRS managers storing/loading preferences about assigned CRSs.
 * 
 * @author Simon Templer
 */
public abstract class AbstractCRSManager implements CRSProvider {

	/**
	 * The configuration parameter name for the default CRS definition.
	 * {@link CRSDefinitionManager#parse(String)} is used to handle any values,
	 * so {@link CRSDefinitionManager#asString(CRSDefinition)} should be used to
	 * create them.
	 */
	public static final String PARAM_DEFAULT_CRS = "defaultCRS";

	/**
	 * The prefix for configuration parameter names for the default CRS
	 * definition for a property. The configuration parameter is the prefix
	 * concatenated with the {@link PropertyDefinition} identifier.
	 * {@link CRSDefinitionManager#parse(String)} is used to handle any values,
	 * so {@link CRSDefinitionManager#asString(CRSDefinition)} should be used to
	 * create them.
	 */
	public static final String PREFIX_PARAM_CRS = "defaultCRS:";

	private final CRSProvider provider;

	private final InstanceReader reader;

	/**
	 * Create a CRS manager.
	 * 
	 * @param reader the instance reader
	 * @param provider the internal CRS provider to use
	 */
	public AbstractCRSManager(InstanceReader reader, CRSProvider provider) {
		super();
		this.provider = provider;
		this.reader = reader;
	}

	@Override
	public CRSDefinition getCRS(PropertyDefinition property) {
		CRSDefinition result = null;
		String resourceId = reader.getResourceIdentifier();
		if (resourceId == null) {
			// TODO warn about no resource Id?
			// setting for any resource
			resourceId = "";
		}
		else {
			resourceId = "resource-" + resourceId + ":";
		}

		// first, try configuration

		// configuration for property
		final String pkey = resourceId + PREFIX_PARAM_CRS + property.getIdentifier();
		result = CRSDefinitionManager.getInstance().parse(loadValue(pkey));

		// overall configuration for resource
		if (result == null && !resourceId.isEmpty()) {
			result = CRSDefinitionManager.getInstance().parse(
					loadValue(resourceId + PARAM_DEFAULT_CRS));
		}
		// overall configuration
		if (result == null) {
			result = CRSDefinitionManager.getInstance().parse(loadValue(PARAM_DEFAULT_CRS));
		}

		if (result == null && provider != null) {
			// consult default CRS provider
			result = provider.getCRS(property);
			if (result != null) {
				// store in configuration
				storeValue(pkey, CRSDefinitionManager.getInstance().asString(result));
			}
		}

		return result;
	}

	/**
	 * Store a configuration value.
	 * 
	 * @param key the configuration key
	 * @param value the associated value
	 */
	protected abstract void storeValue(String key, String value);

	/**
	 * Load a configuration value.
	 * 
	 * @param key the configuration key
	 * @return the associated value
	 */
	protected abstract String loadValue(String key);

}
