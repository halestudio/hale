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

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractExportProvider;
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
public abstract class AbstractInstanceWriter extends AbstractExportProvider implements
		InstanceWriter {

//	private String commonSRSName;

	private InstanceCollection instances;

	private SchemaSpace targetSchema;

	/**
	 * @see AbstractExportProvider#validate()
	 * 
	 *      Additionally checks for instances
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
		if (targetSchema == null || instances == null) {
			fail("Target schema or instances not defined.");
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

//	/**
//	 * @see InstanceWriter#setCommonSRSName(String)
//	 */
//	@Override
//	public void setCommonSRSName(String commonSRSName) {
//		this.commonSRSName = commonSRSName;
//	}

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

//	/**
//	 * @return the commonSRSName
//	 */
//	protected String getCommonSRSName() {
//		return commonSRSName;
//	}

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
