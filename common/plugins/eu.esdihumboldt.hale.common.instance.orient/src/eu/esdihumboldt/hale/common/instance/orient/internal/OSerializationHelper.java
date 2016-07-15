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

package eu.esdihumboldt.hale.common.instance.orient.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.springframework.core.convert.ConversionService;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.ORecordAbstract;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBWriter;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.orient.OGroup;
import eu.esdihumboldt.hale.common.instance.orient.OInstance;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.util.Identifiers;

/**
 * Serialization helper for storing values not support by OrientDB. Serializes
 * geometries as WKB and holds a runtime cache for CRSs.
 * 
 * @author Simon Templer
 */
public abstract class OSerializationHelper {

	/**
	 * Store information on how to convert a value back to its original form.
	 */
	public static class ConvertProxy {

		private final ConversionService cs;
		private final Class<? extends Object> original;

		/**
		 * Create a convert proxy
		 * 
		 * @param cs the conversion service to use
		 * @param original the original type
		 */
		public ConvertProxy(ConversionService cs, Class<? extends Object> original) {
			this.cs = cs;
			this.original = original;
		}

		/**
		 * Convert the string to its original form.
		 * 
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
			result = prime * result + ((original == null) ? 0 : original.hashCode());
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
			}
			else if (!original.equals(other.original))
				return false;
			return true;
		}

	}

	/**
	 * Collection types
	 */
	private static enum CollectionType {
		SET, LIST
	}

	/**
	 * Cache for resolved classes for deserialization
	 */
	private static final LinkedHashMap<String, Class<?>> resolved = new LinkedHashMap<String, Class<?>>();

	/**
	 * Binary wrapper class name
	 */
	public static final String BINARY_WRAPPER_CLASSNAME = "___BinaryWrapper___";

	/**
	 * Binary wrapper class field name
	 */
	public static final String BINARY_WRAPPER_FIELD = "___bin___";

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
	 * Collection property
	 */
	private static final int SERIALIZATION_TYPE_COLLECTION = 4;

	/**
	 * Byte array property
	 */
	private static final int SERIALIZATION_TYPE_BYTEARRAY = 5;

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
	 * Field holding the collection type
	 */
	public static final String FIELD_COLLECTION_TYPE = "___clc___";

	/**
	 * Field holding the values of a collection
	 */
	public static final String FIELD_VALUES = "___vls___";

	/**
	 * Runtime identifiers for CRSs
	 */
	private static final Identifiers<CRSDefinition> CRS_IDS = new Identifiers<CRSDefinition>("crs",
			true);

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
	 * Determines if the given field type is supported directly by the database
	 * 
	 * @param type the field type
	 * @return if the field type is supported
	 */
	private static boolean isSupportedFieldType(Class<? extends Object> type) {
		// records
		if (ORecordAbstract.class.isAssignableFrom(type)) {
			return true;
		}
		// primitives and arrays
		else if (type.isPrimitive() || type.isArray()) {
			return true;
		}
		// wrapper types
		else if (Double.class.isAssignableFrom(type) || Float.class.isAssignableFrom(type)
				|| Integer.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)
				|| Short.class.isAssignableFrom(type) || Byte.class.isAssignableFrom(type)
				|| String.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
			return true;
		}
		// date
		/*
		 * XXX OrientDB strips time information from dates. To avoid information
		 * loss, we serialize dates and derivatives manually instead
		 */
//		else if (Date.class.isAssignableFrom(type)) {
//			return true;
//		}
		// collections
		else if (Collection.class.isAssignableFrom(type)) {
			/*
			 * XXX OrientDB can't deal with nested collections/lists!(?) as a
			 * work-around we also serialize collections
			 */
//			return true;
		}

		return false;
	}

	/**
	 * Prepare a value not supported as field in OrientDB so it can be stored in
	 * the database.
	 * 
	 * @param value the value to convert
	 * @return the converted value that may be used as a property value
	 */
	public static Object convertForDB(Object value) {
		if (value == null)
			return null;
		if (value instanceof OGroup) {
			// special case: if possible use the internal document for
			// OGroup/OInstance
			return ((OGroup) value).getDocument();
		}
		else if (value instanceof Instance) {
			OInstance tmp = new OInstance((Instance) value);
			return tmp.getDocument();
		}
		else if (value instanceof Group) {
			OGroup tmp = new OGroup((Group) value);
			return tmp.getDocument();
		}
		else if (isSupportedFieldType(value.getClass())) {
			return value;
		}

		return serialize(value);
	}

	/**
	 * Serialize and/or wrap a value not supported as field in OrientDB so it
	 * can be stored in the database.
	 * 
	 * @param value the value to serialize
	 * @return the document wrapping the value
	 */
	public static ODocument serialize(Object value) {
		/*
		 * As collections of ORecordBytes are not supported (or rather of
		 * records that are no documents, see embeddedCollectionToStream in
		 * ORecordSerializerCSVAbstract ~578) they are wrapped in a document.
		 */
		ODocument doc = new ODocument();

		// try conversion to string first
		final ConversionService cs = HalePlatform.getService(ConversionService.class);
		if (cs != null) {
			// check if conversion allowed and possible
			if (CONV_WHITE_LIST.contains(value.getClass())
					&& cs.canConvert(value.getClass(), String.class)
					&& cs.canConvert(String.class, value.getClass())) {
				String stringValue = cs.convert(value, String.class);

				ConvertProxy convert = new ConvertProxy(cs, value.getClass());
				doc.field(FIELD_CONVERT_ID, CONVERTER_IDS.getId(convert));
				doc.field(FIELD_SERIALIZATION_TYPE, SERIALIZATION_TYPE_STRING);
				doc.field(FIELD_STRING_VALUE, stringValue);
				return doc;
			}
		}

		if (value instanceof Collection) {
			CollectionType type = null;
			if (value instanceof List) {
				type = CollectionType.LIST;
			}
			else if (value instanceof Set) {
				type = CollectionType.SET;
			}

			if (type != null) {
				// wrap collection values
				Collection<?> elements = (Collection<?>) value;

				List<Object> values = new ArrayList<Object>();
				for (Object element : elements) {
					Object convElement = convertForDB(element);
					values.add(convElement);
				}

				// set values
				// XXX ok to always use EMBEDDEDLIST as type?
				doc.field(FIELD_VALUES, values, OType.EMBEDDEDLIST);
				doc.field(FIELD_SERIALIZATION_TYPE, SERIALIZATION_TYPE_COLLECTION);
				doc.field(FIELD_COLLECTION_TYPE, type.name());

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
			if (value != null) {
				serType = SERIALIZATION_TYPE_GEOM_PROP;
			}
			else {
				return null;
			}
		}

		if (value.getClass().isArray() && value.getClass().getComponentType().equals(byte.class)) {
			// direct byte array support
			record.fromStream((byte[]) value);
			serType = SERIALIZATION_TYPE_BYTEARRAY;
		}
		if (value instanceof Geometry) {
			// serialize geometry as WKB
			Geometry geom = (Geometry) value;

			Coordinate sample = geom.getCoordinate();
			int dimension = (sample != null && !Double.isNaN(sample.z)) ? (3) : (2);

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
				throw new IllegalStateException(
						"Could not serialize field value of type " + value.getClass().getName());
			}
			record.fromStream(bytes.toByteArray());
		}

		/*
		 * XXX Class name is set in OGroup.configureDocument, as the class name
		 * may only bet set after the database was set.
		 */
//		doc.setClassName(BINARY_WRAPPER_CLASSNAME);
		doc.field(BINARY_WRAPPER_FIELD, record);
		doc.field(FIELD_SERIALIZATION_TYPE, serType);
		return doc;
	}

	/**
	 * Convert a value received from the database, e.g. {@link ODocument}s to
	 * {@link Instance}s, {@link Group}s or unwraps contained values.
	 * 
	 * @param value the value
	 * @param parent the parent group
	 * @param childName the name of the child the value is associated to
	 * @return the converted object
	 */
	public static Object convertFromDB(Object value, OGroup parent, QName childName) {
		if (value instanceof ODocument) {
			ODocument doc = (ODocument) value;
			if (doc.containsField(BINARY_WRAPPER_FIELD)
					|| doc.containsField(OSerializationHelper.FIELD_SERIALIZATION_TYPE)) {
				// extract wrapped ORecordBytes
				return OSerializationHelper.deserialize(doc, parent, childName);
			}
			else {
				ChildDefinition<?> child = parent.getDefinition().getChild(childName);
				if (child.asProperty() != null) {
					return new OInstance((ODocument) value, child.asProperty().getPropertyType(),
							parent.getDb(), null); // no data set necessary for
													// nested instances
				}
				else if (child.asGroup() != null) {
					return new OGroup((ODocument) value, child.asGroup(), parent.getDb());
				}
				else {
					throw new IllegalStateException("Field " + childName
							+ " is associated neither with a property nor a group.");
				}
			}
		}
		// TODO also treat collections etc?

		// TODO objects that are not supported inside document
		if (value instanceof ORecordBytes) {
			// XXX should not be reached as every ORecordBytes should be
			// contained in a wrapper
			// TODO try conversion first?!

			// object deserialization
			ORecordBytes record = (ORecordBytes) value;
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
						return result;
					}
				};
				return in.readObject();
			} catch (Exception e) {
				throw new IllegalStateException("Could not deserialize field value.", e);
			}
		}

		return value;
	}

	/**
	 * Deserialize a serialized value wrapped in the given document.
	 * 
	 * @param doc the document
	 * @param parent the parent group
	 * @param childName the name of the child the value is associated to
	 * @return the deserialized value
	 */
	public static Object deserialize(ODocument doc, OGroup parent, QName childName) {
		int serType = doc.field(FIELD_SERIALIZATION_TYPE);

		switch (serType) {
		case SERIALIZATION_TYPE_STRING: {
			// convert a string value back to its original form

			Object val = doc.field(FIELD_STRING_VALUE);
			String stringVal = (val == null) ? (null) : (val.toString());
			ConvertProxy cp = CONVERTER_IDS.getObject((String) doc.field(FIELD_CONVERT_ID));
			if (cp != null) {
				return cp.convert(stringVal);
			}
			return stringVal;
		}
		case SERIALIZATION_TYPE_COLLECTION: {
			// recreate collection

			Object val = doc.field(FIELD_VALUES);
			Object typeVal = doc.field(FIELD_COLLECTION_TYPE);
			CollectionType type = (typeVal != null) ? (CollectionType.valueOf(typeVal.toString()))
					: (CollectionType.LIST);
			if (val instanceof Collection<?>) {
				Collection<?> values = (Collection<?>) val;
				Collection<Object> target = createCollection(type);

				for (Object element : values) {
					Object convElement = convertFromDB(element, parent, childName);
					target.add(convElement);
				}

				return target;
			}
		}
			break;
		}

		ORecordBytes record = (ORecordBytes) doc.field(BINARY_WRAPPER_FIELD);
		Object result;

		switch (serType) {
		case SERIALIZATION_TYPE_BYTEARRAY:
			result = record.toStream();
			break;
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
							throw new IllegalStateException(
									"Class " + desc.getName() + " not found");
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
			GeometryProperty<Geometry> prop = new DefaultGeometryProperty<Geometry>(crs,
					(Geometry) result);
			return prop;
		}

		return result;
	}

	private static Collection<Object> createCollection(CollectionType type) {
		switch (type) {
		case SET:
			return new HashSet<Object>();
		case LIST:
		default:
			return new ArrayList<Object>();
		}
	}

}
