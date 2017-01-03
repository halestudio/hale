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

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractExportProvider;
import eu.esdihumboldt.hale.common.core.io.impl.GZipEnabledExport;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;

/**
 * Abstract {@link InstanceWriter} base implementation
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public abstract class AbstractInstanceWriter extends GZipEnabledExport implements InstanceWriter {

	private InstanceCollection instances;

	private SchemaSpace targetSchema;

	/**
	 * Additionally checks for instances.
	 * 
	 * @see AbstractExportProvider#validate()
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		super.validate();

		if (instances == null || instances.isEmpty()) {
			fail("No instances for export given");
		}
	}

	/**
	 * @see InstanceWriter#checkCompatibility()
	 */
	@Override
	public void checkCompatibility() throws IOProviderConfigurationException {
		if (targetSchema == null) {
			fail("Target schema not defined.");
		}
	}

	/**
	 * Returns the target schema; override to return another set of schemas
	 * 
	 * @see InstanceWriter#getValidationSchemas()
	 */
	@Override
	public List<? extends Locatable> getValidationSchemas() {
		Preconditions.checkState(targetSchema != null);

		List<Locatable> result = new ArrayList<Locatable>();
		Iterables.addAll(result, targetSchema.getSchemas());
		return result;
	}

	/**
	 * @see InstanceWriter#setInstances(InstanceCollection)
	 */
	@Override
	public void setInstances(InstanceCollection instances) {
		this.instances = instances;
	}

	/**
	 * @see InstanceWriter#setTargetSchema(SchemaSpace)
	 */
	@Override
	public void setTargetSchema(SchemaSpace targetSchema) {
		this.targetSchema = targetSchema;
	}

	/**
	 * @return the instances
	 */
	protected InstanceCollection getInstances() {
		return instances;
	}

	/**
	 * @see InstanceWriter#getTargetSchema()
	 */
	@Override
	public SchemaSpace getTargetSchema() {
		return targetSchema;
	}

}
