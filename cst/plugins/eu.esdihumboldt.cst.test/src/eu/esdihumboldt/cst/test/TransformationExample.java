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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.test;

import java.io.InputStream;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.Schema;

/**
 * Transformation example for use in transformation testing.
 * 
 * @author Simon Templer
 */
public interface TransformationExample {

	/**
	 * Get the source schema, load it if it was not loaded yet.
	 * 
	 * @return the source schema
	 * @throws Exception if an error occurs while loading the source schema
	 */
	public Schema getSourceSchema() throws Exception;

	/**
	 * Get the target schema, load it if it was not loaded yet.
	 * 
	 * @return the target schema
	 * @throws Exception if an error occurs while loading the target schema
	 */
	public Schema getTargetSchema() throws Exception;

	/**
	 * Get the alignment, load it if it was not loaded yet.
	 * 
	 * @return the alignment
	 * @throws Exception if an error occurs while loading the alignment
	 */
	public Alignment getAlignment() throws Exception;

	/**
	 * Get the target container namespace (XML specific).
	 * 
	 * @return the name of the target file root element namespace,
	 *         <code>null</code> represents the target schema default namespace
	 */
	public String getTargetContainerNamespace();

	/**
	 * Get the target container name (XML specific).
	 * 
	 * @return the local name of the target file root element
	 */
	public String getTargetContainerName();

	/**
	 * Get the source instances, load them if they were not loaded yet.
	 * 
	 * @return the source instances
	 * @throws Exception if an error occurs while loading the source instances
	 */
	public InstanceCollection getSourceInstances() throws Exception;

	/**
	 * Get the target instances, load them if they were not loaded yet.
	 * 
	 * @return the target instances
	 * @throws Exception if an error occurs while loading the target instances
	 */
	public InstanceCollection getTargetInstances() throws Exception;

	/**
	 * @return the input for loading the source schema
	 */
	public LocatableInputSupplier<? extends InputStream> getSourceSchemaInput();

	/**
	 * @return the input for loading the alignment
	 */
	public LocatableInputSupplier<? extends InputStream> getAlignmentInput();

	/**
	 * @return the input for loading the target schema
	 */
	public LocatableInputSupplier<? extends InputStream> getTargetSchemaInput();

	/**
	 * @return the input for loading the source data
	 */
	public LocatableInputSupplier<? extends InputStream> getSourceDataInput();

	/**
	 * @return the input for loading the target data
	 */
	public LocatableInputSupplier<? extends InputStream> getTargetDataInput();

}
