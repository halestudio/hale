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
	 * the target schema (and instances) are compatible to the writer. Other
	 * parameters should be ignored for the check.
	 * 
	 * @throws IOProviderConfigurationException if the I/O provider was not
	 *             configured properly
	 */
	public void checkCompatibility() throws IOProviderConfigurationException;

}
