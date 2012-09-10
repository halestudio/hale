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

package eu.esdihumboldt.hale.common.instance.io.impl;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.instance.geometry.CRSDefinitionManager;
import eu.esdihumboldt.hale.common.instance.geometry.CRSProvider;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Abstract {@link InstanceReader} base implementation
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractInstanceReader extends AbstractImportProvider implements
		InstanceReader {

	private TypeIndex sourceSchema;

	private CRSProvider defaultCRSProvider;

	/**
	 * @see InstanceReader#setSourceSchema(TypeIndex)
	 */
	@Override
	public void setSourceSchema(TypeIndex sourceSchema) {
		this.sourceSchema = sourceSchema;
	}

	/**
	 * @see InstanceReader#getSourceSchema()
	 */
	@Override
	public TypeIndex getSourceSchema() {
		return sourceSchema;
	}

	/**
	 * @see AbstractImportProvider#validate()
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		super.validate();

		if (sourceSchema == null) {
			fail("No source schema given for import");
		}
	}

	/**
	 * @see InstanceReader#setDefaultCRSProvider(CRSProvider)
	 */
	@Override
	public void setDefaultCRSProvider(CRSProvider crsProvider) {
		this.defaultCRSProvider = crsProvider;
	}

	/**
	 * Get the CRS definition for values of the given property definition.
	 * 
	 * @param property the property definition
	 * @return the CRS definition or <code>null</code> if it can't be determined
	 */
	protected CRSDefinition getDefaultCRS(PropertyDefinition property) {
		CRSDefinition result = null;

		// first, try configuration
		// configuration for property
		final String pkey = PREFIX_PARAM_CRS + property.getIdentifier();
		result = CRSDefinitionManager.getInstance().parse(getParameter(pkey));
		// overall configuration
		if (result == null) {
			result = CRSDefinitionManager.getInstance().parse(getParameter(PARAM_DEFAULT_CRS));
		}

		if (result == null && defaultCRSProvider != null) {
			// consult default CRS provider
			result = defaultCRSProvider.getCRS(property);
			if (result != null) {
				// store in configuration
				setParameter(pkey, CRSDefinitionManager.getInstance().asString(result));
			}
		}

		return result;
	}

}
