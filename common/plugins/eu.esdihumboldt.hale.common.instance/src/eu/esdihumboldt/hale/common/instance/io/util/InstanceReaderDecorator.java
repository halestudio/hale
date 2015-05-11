/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.instance.io.util;

import eu.esdihumboldt.hale.common.core.io.util.ImportProviderDecorator;
import eu.esdihumboldt.hale.common.instance.geometry.CRSProvider;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Decorator for {@link InstanceReader}s.
 * 
 * @param <T> the provider type
 * @author Stefano Costa, GeoSolutions
 */
public abstract class InstanceReaderDecorator<T extends InstanceReader> extends
		ImportProviderDecorator<T> implements InstanceReader {

	/**
	 * @see ImportProviderDecorator#ImportProviderDecorator(eu.esdihumboldt.hale.common.core.io.ImportProvider)
	 */
	public InstanceReaderDecorator(T internalProvider) {
		super(internalProvider);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.io.InstanceReader#setSourceSchema(eu.esdihumboldt.hale.common.schema.model.TypeIndex)
	 */
	@Override
	public void setSourceSchema(TypeIndex sourceSchema) {
		internalProvider.setSourceSchema(sourceSchema);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.io.InstanceReader#setCRSProvider(eu.esdihumboldt.hale.common.instance.geometry.CRSProvider)
	 */
	@Override
	public void setCRSProvider(CRSProvider crsProvider) {
		internalProvider.setCRSProvider(crsProvider);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.io.InstanceReader#getInstances()
	 */
	@Override
	public InstanceCollection getInstances() {
		return internalProvider.getInstances();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.io.InstanceReader#getSourceSchema()
	 */
	@Override
	public TypeIndex getSourceSchema() {
		return internalProvider.getSourceSchema();
	}

}
