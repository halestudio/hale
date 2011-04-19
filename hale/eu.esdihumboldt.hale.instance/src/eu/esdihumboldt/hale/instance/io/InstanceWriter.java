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

package eu.esdihumboldt.hale.instance.io;

import java.io.OutputStream;
import java.util.List;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.schemaprovider.Schema;

/**
 * Provides support for writing instances
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public interface InstanceWriter extends IOProvider {
	
	/**
	 * Write instances to a stream
	 * FIXME legacy method, to be replaced when new instance model is ready
	 * 
	 * @param instances the features to write
	 * @param targetSchema the target schema
	 * @param out the output stream to write the instances to, the caller is 
	 *   responsible for closing it 
	 * @param commonSrsName the name of the common SRS of the instances,
	 *   may be <code>null</code>
	 * @return additional schemas used in the document that are needed for 
	 *   validation, may be <code>null</code>
	 */
	public List<Schema> writeInstances(FeatureCollection<FeatureType, Feature> instances,
			Schema targetSchema, OutputStream out, String commonSrsName);
	
	// public List<Schema> writeInstances(Iterable<Instance> instances, Schema targetSchema, OutputStream out);

}
