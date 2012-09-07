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

package eu.esdihumboldt.hale.common.instance.model.impl.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.LinkedHashMap;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBWriter;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.model.impl.OGroup;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.util.Identifiers;

/**
 * Serialization helper for storing values not support by OrientDB.
 * Serializes geometries as WKB and holds a runtime cache for CRSs.
 * @author Simon Templer
 */
public abstract class OSerializationHelper {
	
	/**
	 * Cache for resolved classes for deserialization
	 */
	private static final LinkedHashMap<String, Class<?>> resolved = new LinkedHashMap<String, Class<?>>();
	
	private static final String FIELD_SERIALIZATION_TYPE = "___st___";
	
	private static final int SERIALIZATION_TYPE_JAVA = 0;
	private static final int SERIALIZATION_TYPE_GEOM = 1;
	private static final int SERIALIZATION_TYPE_GEOM_PROP = 2;
	
	private static final String FIELD_CRS_ID = "___crs___";
	
	private static final Identifiers<CRSDefinition> CRS_IDS = new Identifiers<CRSDefinition>(
			"crs", true);
	
	/**
	 * Prepare a value not supported as field in OrientDB so it can be stored
	 * in the database. 
	 * @param value the value to serialize
	 * @return the document wrapping the value
	 */
	public static ODocument serialize(Object value) {
		//TODO try conversion first?!
		
		/*
		 * As collections of ORecordBytes are not supported (or rather 
		 * of records that are no documents, see embeddedCollectionToStream
		 * in ORecordSerializerCSVAbstract ~578) they are wrapped in a
		 * document.
		 */
		ODocument doc = new ODocument();
		
		ORecordBytes record = new ORecordBytes();
		int serType = SERIALIZATION_TYPE_JAVA;
		
		if (value instanceof GeometryProperty<?>) {
			GeometryProperty<?> geomProp = (GeometryProperty<?>) value;
			
			// store (runtime) CRS ID (XXX OK as storage is temporary)
			doc.field(FIELD_CRS_ID, CRS_IDS.getId(geomProp.getCRSDefinition()));
			
			// extract geometry
			value = geomProp.getGeometry();
			
			serType = SERIALIZATION_TYPE_GEOM_PROP;
		}
		
		if (value instanceof Geometry) {
			// serialize geometry as WKB
			Geometry geom = (Geometry) value;
			
			Coordinate sample = geom.getCoordinate();
			int dimension = (sample != null && !Double.isNaN(sample.z))?(3):(2);
			
			WKBWriter writer = new ExtendedWKBWriter(dimension);
			record.fromStream(writer.write(geom));
			
			if (serType != SERIALIZATION_TYPE_GEOM_PROP) {
				serType = SERIALIZATION_TYPE_GEOM;
			}
		}
		else {
			// object serialization
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			try {
				ObjectOutputStream out = new ObjectOutputStream(bytes);
				out.writeObject(value);
			} catch (IOException e) {
				throw new IllegalStateException("Could not serialize field value.");
			}
			record.fromStream(bytes.toByteArray());
		}

		/*
		 * XXX Class name is set in OGroup.configureDocument, as the class name
		 * may only bet set after the database was set.
		 */
//		doc.setClassName(BINARY_WRAPPER_CLASSNAME);
		doc.field(OGroup.BINARY_WRAPPER_FIELD, record);
		doc.field(FIELD_SERIALIZATION_TYPE, serType);
		return doc;
	}

	/**
	 * Deserialize a serialized value wrapped in the given document.
	 * @param doc the document
	 * @return the deserialized value
	 */
	public static Object deserialize(ODocument doc) {
		int serType = doc.field(FIELD_SERIALIZATION_TYPE);
		
		ORecordBytes record = (ORecordBytes) doc.field(OGroup.BINARY_WRAPPER_FIELD);
		Object result;
		
		switch (serType) {
		case SERIALIZATION_TYPE_GEOM:
		case SERIALIZATION_TYPE_GEOM_PROP:
			ExtendedWKBReader reader = new ExtendedWKBReader();
			try {
				result = reader.read(record.toStream());
			} catch (ParseException e1) {
				throw new IllegalStateException("Unable to parse WKB to restore geometry", e1);
			}
			break;
		case SERIALIZATION_TYPE_JAVA:
		default:
			ByteArrayInputStream bytes = new ByteArrayInputStream(record.toStream());
			try {
				ObjectInputStream in = new ObjectInputStream(bytes) {
					
					@Override
					protected Class<?> resolveClass(ObjectStreamClass desc)
							throws IOException, ClassNotFoundException {
						Class<?> result = resolved.get(desc.getName());
						if (result == null) {
							result = OsgiUtils.loadClass(desc.getName(), null);
							
							if (resolved.size() > 200) {
								resolved.entrySet().iterator().remove();
							}
							
							resolved.put(desc.getName(), result);
						}
						if (result == null) {
							throw new IllegalStateException("Class " +
									desc.getName() + " not found");
						}
						return result;
					}
				};
				result = in.readObject();
			} catch (Exception e) {
				throw new IllegalStateException("Could not deserialize field value.", e);
			}
			break;
		}
		
		if (serType == SERIALIZATION_TYPE_GEOM_PROP) {
			// wrap geometry in geometry property
			
			// determine CRS
			CRSDefinition crs = null;
			Object crsId = doc.field(FIELD_CRS_ID);
			if (crsId != null) {
				crs = CRS_IDS.getObject(crsId.toString());
			}
			
			// create geometry property
			GeometryProperty<Geometry> prop = new DefaultGeometryProperty<Geometry>(
					crs, (Geometry) result);
			return prop;
		}
		
		return result;
	}

}
