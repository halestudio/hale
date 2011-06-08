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

package eu.esdihumboldt.hale.instance.model.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.namespace.QName;

import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.record.ORecordAbstract;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.instance.internal.InstanceBundle;
import eu.esdihumboldt.hale.instance.model.Group;
import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.instance.model.MutableGroup;
import eu.esdihumboldt.hale.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.schema.model.constraint.property.Cardinality;

/**
 * Group implementation based on {@link ODocument}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class OGroup implements MutableGroup {

	private static final ALogger log = ALoggerFactory.getLogger(OGroup.class);
	
	/**
	 * The document backing the group
	 */
	protected final ODocument document;
	
	/**
	 * The definition group
	 */
	private final DefinitionGroup definition;
	
	/**
	 * Creates an empty group with an associated definition group.
	 * @param definition the associated group
	 */
	public OGroup(DefinitionGroup definition) {
		document = new ODocument();
		this.definition = definition;
	}
	
	/**
	 * Configure the internal document with the given database and return it
	 * @param db the database
	 * @return the internal document configured with the database
	 */
	public ODocument configureDocument(ODatabaseRecord db) {
		configureDocument(document, db, definition);
		return document;
	}
	
	private void configureDocument(ORecordAbstract<?> document, ODatabaseRecord db,
			DefinitionGroup definition) {
		// configure document
		document.setDatabase(db);
		if (document instanceof ODocument) {
			// reset class name
			ODocument doc = (ODocument) document;
			doc.setClassName(ONameUtil.encodeName(definition.getIdentifier()));
			
			// configure children
			for (Entry<String, Object> field : doc) {
				if (field.getValue() instanceof ODocument) {
					ChildDefinition<?> child = definition.getChild(decodeProperty(field.getKey()));
					DefinitionGroup childGroup;
					if (child.asProperty() != null) {
						childGroup = child.asProperty().getPropertyType();
					}
					else if (child.asGroup() != null) {
						childGroup = child.asGroup();
					}
					else {
						throw new IllegalStateException("Document is associated neither with a property nor a property group.");
					}
					configureDocument((ODocument) field.getValue(), db, childGroup);
				}
				else if (field.getValue() instanceof ORecordAbstract<?>) {
					configureDocument((ORecordAbstract<?>) field.getValue(), db, null);
				}
			}
		}
	}

	/**
	 * Creates a group based on the given document
	 * 
	 * @param document the document
	 * @param definition the definition of the associated group
	 */
	public OGroup(ODocument document, DefinitionGroup definition) {
		this.document = document;
		this.definition = definition;
	}
	
	/**
	 * Copy constructor.
	 * Creates a group based on the properties and values of the given 
	 * group.
	 * 
	 * @param org the instance to copy
	 */
	public OGroup(Group org) {
		this(org.getDefinition());
		
		for (QName property : org.getPropertyNames()) {
			setProperty(property, org.getProperty(property).clone());
		}
	}

	/**
	 * @see MutableGroup#addProperty(QName, Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addProperty(QName propertyName, Object value) {
		// convert instances to documents
		value = convertInstance(value);
		
		String pName = encodeProperty(propertyName);
		
		boolean collection = isCollectionProperty(propertyName);
		if (collection) {
			// combine value with previous ones
			Object oldValue = document.field(pName);
			if (oldValue == null) {
				// default: use list
				List<Object> valueList = new ArrayList<Object>();
				valueList.add(value);
				document.field(pName, valueList); //XXX need to add OType.EMBEDDEDLIST?
			}
			else if (oldValue instanceof Collection<?>) {
				// add value to collection
				((Collection<Object>) oldValue).add(value);
			}
			else if (oldValue.getClass().isArray()) {
				// create new array
				Object[] oldArray = (Object[]) oldValue;
				Object[] values = new Object[oldArray.length + 1];
				System.arraycopy(oldArray, 0, values, 0, oldArray.length);
				values[oldArray.length] = value;
				document.field(pName, values); //XXX need to add OType.EMBEDDEDLIST?
			}
		}
		else {
			// just set the field
			document.field(pName, value);
		}
	}

	/**
	 * Converts {@link Group}s and {@link Instance}s to {@link ODocument} but 
	 * leaves other objects untouched.
	 * 
	 * @param value the object to convert
	 * @return the converted object
	 */
	private Object convertInstance(Object value) {
		if (value instanceof OGroup) {
			// special case: if possible use the internal document for OGroup/OInstance
			return ((OGroup) value).document;
		}
		else if (value instanceof Instance) {
			OInstance tmp = new OInstance((Instance) value);
			return tmp.document;
		}
		else if (value instanceof Group) {
			OGroup tmp = new OGroup((Group) value);
			return tmp.document;
		}
		//TODO also treat collections etc?
		
		//TODO objects that are not supported inside document
		else if (!isSupportedFieldType(value.getClass())) {
			//TODO try conversion first?!
			
			// object serialization
			ORecordBytes record = new ORecordBytes();
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			try {
				ObjectOutputStream out = new ObjectOutputStream(bytes);
				out.writeObject(value);
			} catch (IOException e) {
				throw new IllegalStateException("Could not serialize field value.");
			}
			record.fromStream(bytes.toByteArray());
			return record;
		}
		
		return value;
	}

	/**
	 * Determines if the given field type is supported directly by the database
	 * @param type the field type
	 * @return if the field type is supported
	 */
	private boolean isSupportedFieldType(Class<? extends Object> type) {
		// records
		if (ORecordAbstract.class.isAssignableFrom(type)) {
			return true;
		}
		// primitives and arrays
		else if (type.isPrimitive() || type.isArray()) {
			return true;
		}
		// wrapper types
		else if (Number.class.isAssignableFrom(type) ||
				String.class.isAssignableFrom(type) ||
				Boolean.class.isAssignableFrom(type)) {
			return true;
		}
		// date
		else if (Date.class.isAssignableFrom(type)) {
			return true;
		}
		// collections
		else if (Collection.class.isAssignableFrom(type)) {
			return true;
		}
		
		return false;
	}

	/**
	 * Determines if a property can have multiple values
	 * 
	 * @param propertyName the property name
	 * @return if the property can have multiple values
	 */
	private boolean isCollectionProperty(QName propertyName) {
		ChildDefinition<?> child = definition.getChild(propertyName);
		if (child instanceof PropertyDefinition) {
			return ((PropertyDefinition) child).getConstraint(Cardinality.class).getMaxOccurs() > 1;
		}
		else if (child instanceof GroupPropertyDefinition) {
			return ((GroupPropertyDefinition) child).getConstraint(Cardinality.class).getMaxOccurs() > 1;
		}
		
		// default to true
		return true;
	}

	/**
	 * @see MutableGroup#setProperty(QName, Object[])
	 */
	@Override
	public void setProperty(QName propertyName, Object... values) {
		String pName = encodeProperty(propertyName);
		
		if (values == null || values.length == 0) {
			document.removeField(pName);
			return;
		}
		
		boolean collection = isCollectionProperty(propertyName);
		
		if (!collection) {
			if (values.length > 1) {
				//TODO log type and property
				log.warn("Attempt to set multiple values on a property that supports only one, using only the first value");
			}
			
			document.field(pName, convertInstance(values[0]));
		}
		else {
			List<Object> valueList = new ArrayList<Object>();
			for (Object value : values) {
				valueList.add(convertInstance(value));
			}
			document.field(pName, valueList); //XXX need to add OType.EMBEDDEDLIST?
		}
	}

	/**
	 * Encode a qualified property name to a string
	 * 
	 * @param propertyName the qualified property name
	 * @return the name encoded as a single string
	 */
	protected String encodeProperty(QName propertyName) {
		//TODO handle namespace separately (prefix map?)
		return ONameUtil.encodeName(propertyName.toString());
	}
	
	/**
	 * Decode an encoded property name to a qualified name
	 * 
	 * @param encodedProperty the encoded property name
	 * @return the qualified property name
	 */
	protected QName decodeProperty(String encodedProperty) {
		try {
			return QName.valueOf(ONameUtil.decodeName(encodedProperty));
		} catch (Throwable e) {
			throw new RuntimeException("Could not encode property name", e);
		}
	}

	/**
	 * @see Instance#getProperty(QName)
	 */
	@Override
	public Object[] getProperty(QName propertyName) {
		String pName = encodeProperty(propertyName);
		Object value = document.field(pName);
		
		if (value == null) {
			return null;
		}
		// cannot check for Iterable as ODocument is also an Iterable
		else if (value instanceof Collection<?> || value.getClass().isArray()) {
			List<Object> valueList = new ArrayList<Object>();
			for (Object val : (Iterable<?>) value) {
				valueList.add(convertDocument(val, propertyName));
			}
			return valueList.toArray();
		}
		else {
			return new Object[]{convertDocument(value, propertyName)};
		}
	}

	/**
	 * Converts {@link ODocument}s to {@link Instance}s but leaves other objects
	 * untouched.
	 * 
	 * @param value the object to convert
	 * @param propertyName the name of the property the value is associated with
	 * @return the converted object
	 */
	private Object convertDocument(Object value, QName propertyName) {
		if (value instanceof ODocument) {
			ChildDefinition<?> child = definition.getChild(propertyName);
			if (child.asProperty() != null) {
				return new OInstance((ODocument) value, child.asProperty().getPropertyType());
			}
			else if (child.asGroup() != null) {
				return new OGroup((ODocument) value, child.asGroup());
			}
			else {
				throw new IllegalStateException("Field " + propertyName + 
						" is associated neither with a property nor a group.");
			}
		}
		//TODO also treat collections etc?
		
		//TODO objects that are not supported inside document
		else if (value instanceof ORecordBytes) {
			//TODO try conversion first?!
			
			// object deserialization
			ORecordBytes record = (ORecordBytes) value;
			ByteArrayInputStream bytes = new ByteArrayInputStream(record.toStream());
			try {
				ObjectInputStream in = new ObjectInputStream(bytes) {
					@Override
					protected Class<?> resolveClass(ObjectStreamClass desc)
							throws IOException, ClassNotFoundException {
						return InstanceBundle.loadClass(desc.getName(), null);
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
	 * @see Group#getPropertyNames()
	 */
	@Override
	public Iterable<QName> getPropertyNames() {
		Set<String> fields = new HashSet<String>(document.fieldNames());
		
		// remove value field
		fields.removeAll(getSpecialFieldNames());
		
		Set<QName> qFields = new HashSet<QName>();
		for (String field : fields) {
			qFields.add(decodeProperty(field));
		}
		
		return qFields;
	}

	/**
	 * Get the special field names, e.g. for metadata.
	 * @return the collection of special field names. 
	 */
	protected Collection<String> getSpecialFieldNames() {
		return Collections.emptyList();
	}

	/**
	 * @see Group#getDefinition()
	 */
	@Override
	public DefinitionGroup getDefinition() {
		return definition;
	}

}
