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

import java.util.Collections;
import java.util.List;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import com.google.common.base.Preconditions;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractExportProvider;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.schemaprovider.Schema;

/**
 * Abstract {@link InstanceWriter} base implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public abstract class AbstractInstanceWriter extends AbstractExportProvider implements
		InstanceWriter {
	
	private String commonSRSName;
	
	private FeatureCollection<FeatureType, Feature> instances;
	
	private Schema targetSchema;

	/**
	 * @see AbstractExportProvider#validate()
	 * 
	 * Additionally checks for instances
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		super.validate();
		
		if (instances == null || instances.isEmpty()) {
			fail("No instances for export given");
		}
	}

	/**
	 * Returns the target schema; override to return another set of schemas
	 * 
	 * @see InstanceWriter#getValidationSchemas()
	 */
	@Override
	public List<Schema> getValidationSchemas() {
		Preconditions.checkState(targetSchema != null);
		
		return Collections.singletonList(targetSchema);
	}

	/**
	 * @see InstanceWriter#setCommonSRSName(String)
	 */
	@Override
	public void setCommonSRSName(String commonSRSName) {
		this.commonSRSName = commonSRSName;
	}

	/**
	 * @see InstanceWriter#setInstances(FeatureCollection)
	 */
	@Override
	public void setInstances(FeatureCollection<FeatureType, Feature> instances) {
		this.instances = instances;
	}

	/**
	 * @see InstanceWriter#setTargetSchema(Schema)
	 */
	@Override
	public void setTargetSchema(Schema targetSchema) {
		this.targetSchema = targetSchema;
	}

	/**
	 * @return the commonSRSName
	 */
	protected String getCommonSRSName() {
		return commonSRSName;
	}

	/**
	 * @return the instances
	 */
	protected FeatureCollection<FeatureType, Feature> getInstances() {
		return instances;
	}

	/**
	 * @return the targetSchema
	 */
	protected Schema getTargetSchema() {
		return targetSchema;
	}

}
