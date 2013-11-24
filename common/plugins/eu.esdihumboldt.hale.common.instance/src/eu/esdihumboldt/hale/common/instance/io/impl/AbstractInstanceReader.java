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

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.impl.GZipEnabledImport;
import eu.esdihumboldt.hale.common.instance.geometry.CRSProvider;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Abstract {@link InstanceReader} base implementation
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractInstanceReader extends GZipEnabledImport implements InstanceReader {

	/**
	 * the parameter specifying the reader setting
	 */
	public static final String PARAM_SKIP_FIRST_LINE = "skip";

	private TypeIndex sourceSchema;

	private CRSProvider crsProvider;

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
	 * Get the CRS provider.
	 * 
	 * @return the CRS provider
	 */
	protected CRSProvider getCrsProvider() {
		return crsProvider;
	}

}
