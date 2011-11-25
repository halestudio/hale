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

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;

/**
 * Provides support for writing instances
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public interface InstanceWriter extends ExportProvider {
	
//	/**
//	 * Write instances to a stream
//	 * FIXME legacy method, to be replaced when new instance model is ready
//	 * 
//	 * @param instances the features to write
//	 * @param targetSchema the target schema
//	 * @param out the output stream to write the instances to, the caller is 
//	 *   responsible for closing it 
//	 * @param commonSrsName the name of the common SRS of the instances,
//	 *   may be <code>null</code>
//	 * @return additional schemas used in the document that are needed for 
//	 *   validation, may be <code>null</code>
//	 */
//	public List<Schema> writeInstances(FeatureCollection<FeatureType, Feature> instances,
//			Schema targetSchema, String commonSrsName);
	
	/**
	 * Set the instances to write
	 * FIXME legacy method, to be replaced when new instance model is ready
	 * 
	 * @param instances the instances to write
	 */
	public void setInstances(FeatureCollection<FeatureType, Feature> instances);
	
//	/**
//	 * Set the target schema for the output
//	 * FIXME legacy method, to be replaced when new schema model is ready
//	 * 
//	 * @param targetSchema the target schema
//	 */
//	public void setTargetSchema(Schema targetSchema);
	
	/**
	 * Set the common SRS name
	 * FIXME can we do without?
	 * 
	 * @param commonSRSName the name of the common SRS of the instances,
	 *   may be <code>null</code>
	 */
	public void setCommonSRSName(String commonSRSName);
	
//	/**
//	 * Get the schemas needed for validation of the output written using 
//	 * {@link #execute(ProgressIndicator)}, this usually is at least the 
//	 * target schema.
//	 * 
//	 * @return the schemas needed for validation
//	 */
//	public List<Schema> getValidationSchemas();

}
