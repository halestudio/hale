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

package eu.esdihumboldt.hale.common.instance.io;

import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.instance.geometry.CRSProvider;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Provides support for reading instances
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface InstanceReader extends ImportProvider {

	/**
	 * The configuration parameter name for the default SRS.
	 */
	public static final String PARAM_DEFAULT_SRS = "defaultSrs";

	/**
	 * Set the instance source schema
	 * 
	 * @param sourceSchema the source schema
	 */
	public void setSourceSchema(TypeIndex sourceSchema);

	/**
	 * Set a CRS provider that is queried if no CRS can be determined for a
	 * property value and no default CRS is configured for the associated
	 * property definition.
	 * 
	 * @param crsProvider the CRS provider
	 */
	public void setCRSProvider(CRSProvider crsProvider);

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
