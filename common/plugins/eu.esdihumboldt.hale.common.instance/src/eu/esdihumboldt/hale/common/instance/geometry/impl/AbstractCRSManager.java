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

package eu.esdihumboldt.hale.common.instance.geometry.impl;

import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.geometry.CRSDefinitionManager;
import eu.esdihumboldt.hale.common.instance.geometry.CRSProvider;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

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
	 * @param provider the internal CRS provider to use, may be
	 *            <code>null</code>
	 */
	public AbstractCRSManager(InstanceReader reader, CRSProvider provider) {
		super();
		this.provider = provider;
		this.reader = reader;
	}

	@Override
	public CRSDefinition getCRS(TypeDefinition parentType, List<QName> propertyPath) {
		return getCRS(parentType, propertyPath, null);
	}

	@Override
	public CRSDefinition getCRS(TypeDefinition parentType, List<QName> propertyPath,
			CRSDefinition defaultCrs) {

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
		StringBuffer keybuilder = new StringBuffer();
		keybuilder.append(resourceId);
		keybuilder.append(PREFIX_PARAM_CRS);
		keybuilder.append(parentType.getName());
		for (QName property : propertyPath) {
			keybuilder.append('/');
			keybuilder.append(property);
		}
		final String propertyKey = keybuilder.toString();
		result = CRSDefinitionManager.getInstance().parse(loadValue(propertyKey));

		// overall configuration for resource
		if (result == null && !resourceId.isEmpty()) {
			result = CRSDefinitionManager.getInstance()
					.parse(loadValue(resourceId + PARAM_DEFAULT_CRS));
		}
		// overall configuration
		if (result == null) {
			result = CRSDefinitionManager.getInstance().parse(loadValue(PARAM_DEFAULT_CRS));
		}

		if (result == null && provider != null) {
			// consult default CRS provider
			result = provider.getCRS(parentType, propertyPath, defaultCrs);
			if (result != null) {
				// store in configuration
				storeValue(propertyKey, CRSDefinitionManager.getInstance().asString(result));
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
