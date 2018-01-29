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

package eu.esdihumboldt.hale.common.instance.io.impl;

import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.impl.GZipEnabledImport;
import eu.esdihumboldt.hale.common.instance.geometry.CRSDefinitionManager;
import eu.esdihumboldt.hale.common.instance.geometry.CRSProvider;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Abstract {@link InstanceReader} base implementation
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractInstanceReader extends GZipEnabledImport implements InstanceReader {

	private TypeIndex sourceSchema;

	private CRSProvider crsProvider;

	private final CRSProvider wrappingProvider = new CRSProvider() {

		@Override
		public CRSDefinition getCRS(TypeDefinition parentType, List<QName> propertyPath,
				CRSDefinition defaultCrs) {
			CRSDefinition result = getDefaultSRS();

			// Extend interface (default) to be able to provide default if
			// provider can't resolve it
			if (result == null && crsProvider != null) {
				result = crsProvider.getCRS(parentType, propertyPath, defaultCrs);
			}

			if (result == null) {
				return defaultCrs;
			}

			return result;
		}

		@Override
		public CRSDefinition getCRS(TypeDefinition parentType, List<QName> propertyPath) {
			return getCRS(parentType, propertyPath, null);
		}
	};

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
	 * @see InstanceReader#setCRSProvider(CRSProvider)
	 */
	@Override
	public void setCRSProvider(CRSProvider crsProvider) {
		this.crsProvider = crsProvider;
	}

	/**
	 * Get the default SRS if it is configured.
	 * 
	 * @return the default SRS or <code>null</code>
	 */
	protected CRSDefinition getDefaultSRS() {
		String srsString = getParameter(PARAM_DEFAULT_SRS).as(String.class, null);
		if (srsString == null || srsString.isEmpty()) {
			return null;
		}
		return CRSDefinitionManager.getInstance().parse(srsString);
	}

	/**
	 * Get the CRS provider. It also respects if a default SRS is set on the
	 * input.
	 * 
	 * @return the CRS provider
	 */
	protected CRSProvider getCrsProvider() {
		return wrappingProvider;
	}

}
