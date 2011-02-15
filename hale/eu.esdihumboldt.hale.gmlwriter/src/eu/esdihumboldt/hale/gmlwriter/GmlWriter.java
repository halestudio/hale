/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.gmlwriter;

import java.io.OutputStream;
import java.util.List;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.schemaprovider.Schema;

/**
 * GML writer interface 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface GmlWriter {
	
	/**
	 * Write features to a GML stream
	 * 
	 * @param features the features to write
	 * @param targetSchema the target schema
	 * @param out the output stream to write the GML to, the caller is 
	 *   responsible for closing it 
	 * @param commonSrsName the name of the common SRS of the features,
	 *   may be <code>null</code>
	 * @return additional schemas used in the document that are needed for 
	 * validation, may be <code>null</code>
	 */
	public List<Schema> writeFeatures(FeatureCollection<FeatureType, Feature> features,
			Schema targetSchema, OutputStream out, String commonSrsName);

}
