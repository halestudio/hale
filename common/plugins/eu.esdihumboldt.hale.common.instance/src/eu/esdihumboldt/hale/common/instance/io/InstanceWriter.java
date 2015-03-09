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

import java.util.List;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;

/**
 * Provides support for writing instances
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public interface InstanceWriter extends ExportProvider {

	/**
	 * Set the instances to write.
	 * 
	 * @param instances the instances to write
	 */
	public void setInstances(InstanceCollection instances);

	/**
	 * Set the target schema for the output.
	 * 
	 * @param targetSchema the target schema
	 */
	public void setTargetSchema(SchemaSpace targetSchema);

	/**
	 * Get the target schema.
	 * 
	 * @return the target schema
	 */
	public SchemaSpace getTargetSchema();

	/**
	 * Get the schemas needed for validation of the output written using
	 * {@link #execute(ProgressIndicator)}, this usually is at least the target
	 * schema.
	 * 
	 * @return the schemas needed for validation
	 */
	public List<? extends Locatable> getValidationSchemas();

	/**
	 * Validate the basic {@link InstanceWriter} configuration, to determine if
	 * the target schema (and instances if set) are compatible to the writer.
	 * Other parameters should be ignored for the check.
	 * 
	 * @throws IOProviderConfigurationException if the I/O provider was not
	 *             configured properly
	 */
	public void checkCompatibility() throws IOProviderConfigurationException;

	/**
	 * States if the instance writer directly streams the supplied instances in
	 * one go, i.e. it only acquires the iterator once and consumes it.
	 * 
	 * @return the instance writer directly streams the supplied instances in
	 *         one go
	 */
	public boolean isPassthrough();

}
