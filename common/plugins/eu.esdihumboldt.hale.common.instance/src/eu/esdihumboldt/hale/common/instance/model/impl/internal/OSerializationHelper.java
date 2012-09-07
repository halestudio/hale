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
import java.math.BigInteger;
import java.net.URI;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.springframework.core.convert.ConversionService;

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
	 * Store information on how to convert a value back to its original form.
	 */
	public static class ConvertProxy {

		private final ConversionService cs;
		private Class<? extends Object> original;

		/**
		 * Create a convert proxy
		 * @param cs the conversion service to use
		 * @param original the original type
		 */
		public ConvertProxy(ConversionService cs, Class<? extends Object> original) {
			this.cs = cs;
			this.original = original;
		}
		
		/**
		 * Convert the string to its original form.
		 * @param value the string value
		 * @return the converted value
		 */
		public Object convert(String value) {
			return cs.convert(value, original);
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((original == null) ? 0 : original.hashCode());
			return result;
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ConvertProxy other = (ConvertProxy) obj;
			if (original == null) {
				if (other.original != null)
					return false;
			} else if (!original.equals(other.original))
				return false;
			return true;
		}

	}

	/**
	 * Cache for resolved classes for deserialization
	 */
	private static final LinkedHashMap<String, Class<?>> resolved = new LinkedHashMap<String, Class<?>>();
	
	/**
	 * Field specifying the serialization type
	 */
	public static final String FIELD_SERIALIZATION_TYPE = "___st___";
	
	/**
	 * Java object serialization (Swiss army knife)
	 */
	private static final int SERIALIZATION_TYPE_JAVA = 0;
	
	/**
	 * Converted value
	 */
	private static final int SERIALIZATION_TYPE_STRING = 1;
	
	/**
	 * WKB geometry
	 */
	private static final int SERIALIZATION_TYPE_GEOM = 2;
	
	/**
	 * Geometry property (WKB + optional CRS ID)
	 */
	private static final int SERIALIZATION_TYPE_GEOM_PROP = 3;
	
	/**
	 * Field specifying the CRS ID
	 */
	public static final String FIELD_CRS_ID = "___crs___";
	
	/**
	 * Field specifying the converter ID
	 */
	public static final String FIELD_CONVERT_ID = "___cnv___";
	
	/**
	 * Field specifying the string value
	 */
	public static final String FIELD_STRING_VALUE = "___str___";
	
	/**
	 * Runtime identifiers for CRSs
	 */
	private static final Identifiers<CRSDefinition> CRS_IDS = new Identifiers<CRSDefinition>(
			"crs", true);
	
	/**
	 * Runtime identifiers for {@link ConvertProxy}s
	 */
	private static final Identifiers<ConvertProxy> CONVERTER_IDS = new Identifiers<ConvertProxy>(
			"cnv", true);
	
	/**
	 * String conversion white list
	 */
	private static final Set<Class<?>> CONV_WHITE_LIST = new HashSet<Class<?>>();
	static {
		CONV_WHITE_LIST.add(BigInteger.class);
		CONV_WHITE_LIST.add(URI.class);
	}
	
	/**
	 * Prepare a value not supported as field in OrientDB so it can be stored
	 * in the database. 
	 * @param value the value to serialize
	 * @return the document wrapping the value
	 */
	public static ODocument serialize(Object value) {
		/*
		 * As collections of ORecordBytes are not supported (or rather 
		 * of records that are no documents, see embeddedCollectionToStream
		 * in ORecordSerializerCSVAbstract ~578) they are wrapped in a
		 * document.
		 */
		ODocument doc = new ODocument();
		
		// try conversion to string first
		final ConversionService cs = OsgiUtils.getService(ConversionService.class);
		if (cs != null) {
			// check if conversion allowed and possible
			if (CONV_WHITE_LIST.contains(value.getClass()) &&
					cs.canConvert(value.getClass(), String.class) &&
					cs.canConvert(String.class, value.getClass())) {
				String stringValue = cs.convert(value, String.class);
				
				ConvertProxy convert = new ConvertProxy(cs, value.getClass());
				doc.field(FIELD_CONVERT_ID, CONVERTER_IDS.getId(convert));
				doc.field(FIELD_SERIALIZATION_TYPE, SERIALIZATION_TYPE_STRING);
				doc.field(FIELD_STRING_VALUE, stringValue);
				return doc;
			}
		}
		
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
		
		switch (serType) {
		case SERIALIZATION_TYPE_STRING:
			// convert a string value back to its original form
			
			Object val = doc.field(FIELD_STRING_VALUE);
			String stringVal = (val == null)?(null):(val.toString());
			ConvertProxy cp = CONVERTER_IDS.getObject((String) doc.field(FIELD_CONVERT_ID));
			if (cp != null) {
				return cp.convert(stringVal);
			}
			return stringVal;
		}
		
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
