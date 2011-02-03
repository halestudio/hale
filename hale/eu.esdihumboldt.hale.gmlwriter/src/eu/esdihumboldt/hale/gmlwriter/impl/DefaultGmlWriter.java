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

package eu.esdihumboldt.hale.gmlwriter.impl;

import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.gmlwriter.GmlWriter;
import eu.esdihumboldt.hale.gmlwriter.impl.internal.StreamGmlWriter;
import eu.esdihumboldt.hale.schemaprovider.Schema;

/**
 * The default {@link GmlWriter} implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class DefaultGmlWriter implements GmlWriter {

	/**
	 * @see GmlWriter#writeFeatures(FeatureCollection, Schema, OutputStream)
	 */
	@Override
	public void writeFeatures(FeatureCollection<FeatureType, Feature> features,
			Schema targetSchema, OutputStream out) {
		try {
			new StreamGmlWriter(targetSchema, out).write(features);
		} catch (XMLStreamException e) {
			throw new RuntimeException("Error writing GML", e);
		}
	}

}
