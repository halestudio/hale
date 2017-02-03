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

import java.util.List;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.core.io.util.ExportProviderDecorator;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;

/**
 * Decorator for {@link InstanceWriter}s.
 * 
 * @param <T> the provider type
 * @author Simon Templer
 */
public abstract class InstanceWriterDecorator<T extends InstanceWriter>
		extends ExportProviderDecorator<T>implements InstanceWriter {

	/**
	 * @see ExportProviderDecorator#ExportProviderDecorator(eu.esdihumboldt.hale.common.core.io.ExportProvider)
	 */
	public InstanceWriterDecorator(T internalProvider) {
		super(internalProvider);
	}

	@Override
	public void setInstances(InstanceCollection instances) {
		internalProvider.setInstances(instances);
	}

	@Override
	public void setTargetSchema(SchemaSpace targetSchema) {
		internalProvider.setTargetSchema(targetSchema);
	}

	@Override
	public SchemaSpace getTargetSchema() {
		return internalProvider.getTargetSchema();
	}

	@Override
	public List<? extends Locatable> getValidationSchemas() {
		return internalProvider.getValidationSchemas();
	}

	@Override
	public void checkCompatibility() throws IOProviderConfigurationException {
		internalProvider.checkCompatibility();
	}

	@Override
	public boolean isPassthrough() {
		return internalProvider.isPassthrough();
	}

}
