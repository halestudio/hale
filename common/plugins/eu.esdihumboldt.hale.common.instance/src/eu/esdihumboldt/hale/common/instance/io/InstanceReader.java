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

package eu.esdihumboldt.hale.common.instance.io;

import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.instance.geometry.CRSDefinitionManager;
import eu.esdihumboldt.hale.common.instance.geometry.CRSProvider;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Provides support for reading instances
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface InstanceReader extends ImportProvider {

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

	/**
	 * Set the instance source schema
	 * 
	 * @param sourceSchema the source schema
	 */
	public void setSourceSchema(TypeIndex sourceSchema);

	/**
	 * Set a CRS provider that is queried if no CRS can be determined for a
	 * property value and no default CRS is configured for the associated
	 * property definition. The information obtained will be used to extend the
	 * configuration.
	 * 
	 * @see #PARAM_DEFAULT_CRS
	 * @see #PREFIX_PARAM_CRS
	 * 
	 * @param crsProvider the CRS provider
	 */
	public void setDefaultCRSProvider(CRSProvider crsProvider);

	/**
	 * Get the instances
	 * 
	 * @return the instance collection
	 */
	public InstanceCollection getInstances();

	/**
	 * Get the source schema
	 * 
	 * @return the source schema
	 */
	public abstract TypeIndex getSourceSchema();

}
