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

package eu.esdihumboldt.hale.common.instance.model.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.namespace.QName;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.ORecordAbstract;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import com.orientechnologies.orient.core.storage.OStorage.CLUSTER_TYPE;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableGroup;
import eu.esdihumboldt.hale.common.instance.model.impl.internal.ONamespaceMap;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;

/**
 * Group implementation based on {@link ODocument}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class OGroup implements MutableGroup {

	/**
	 * Binary wrapper class field name
	 */
	private static final String BINARY_WRAPPER_FIELD = "___bin___";

	/**
	 * Binary wrapper class name
	 */
	public static final String BINARY_WRAPPER_CLASSNAME = "___BinaryWrapper___";

	private static final ALogger log = ALoggerFactory.getLogger(OGroup.class);
	
	/**
	 * The set of special field names, e.g. for the binary wrapper field
	 */
	private static final Set<String> SPECIAL_FIELDS = new HashSet<String>();
	static {
		SPECIAL_FIELDS.add(BINARY_WRAPPER_FIELD);
	}
	
	/**
	 * Cache for resolved classes for deserialization
	 */
	private static final LinkedHashMap<String, Class<?>> resolved = new LinkedHashMap<String, Class<?>>();
	
	/**
	 * The document backing the group
	 */
	protected final ODocument document;
	
	/**
	 * The associated database record.
	 */
	protected ODatabaseRecord db;
	
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
		ODatabaseRecordThreadLocal.INSTANCE.set(db);
		configureDocument(document, db, definition);
		return document;
	}
	
	/**
	 * Get the internal document.
	 * @return the internal document
	 */
	public ODocument getDocument() {
		return document;
	}
	
	private void configureDocument(ORecordAbstract<?> document, ODatabaseRecord db,
			DefinitionGroup definition) {
		// configure document
		
		// as of OrientDB 1.0rc8 the database may no longer be set on the document
		// instead the current database can be set using
		// ODatabaseRecordThreadLocal.INSTANCE.set(db);
//		document.setDatabase(db);
		if (document instanceof ODocument) {
			// reset class name
			ODocument doc = (ODocument) document;
			String className = null;
			if (definition != null) {
				className = ONameUtil.encodeName(definition.getIdentifier());
			}
			else if (doc.containsField(BINARY_WRAPPER_FIELD)) {
				className = BINARY_WRAPPER_CLASSNAME;
			}
			
			if (className != null) {
				OSchema schema = db.getMetadata().getSchema();
				if (!schema.existsClass(className)) {
					// if the class doesn't exist yet, create a physical cluster manually for it
					int cluster = db.addCluster(className, CLUSTER_TYPE.PHYSICAL);
					schema.createClass(className, cluster);
				}
				doc.setClassName(className);
			}
			
			// configure children
			for (Entry<String, Object> field : doc) {
				List<ODocument> docs = new ArrayList<ODocument>();
				List<ORecordAbstract<?>> recs = new ArrayList<ORecordAbstract<?>>();
				if (field.getValue() instanceof Collection<?>) {
					for (Object value : (Collection<?>)field.getValue()) {
						if (value instanceof ODocument) {
							docs.add((ODocument) value);
						}
						else if (value instanceof ORecordAbstract<?>) {
							recs.add((ORecordAbstract<?>) value);
						}
					}
				}
				else if (field.getValue() instanceof ODocument 
						&& !getSpecialFieldNames().contains(field.getKey())) { 
					docs.add((ODocument) field.getValue());
				}
				else if (field.getValue() instanceof ORecordAbstract<?>) {
					recs.add((ORecordAbstract<?>) field.getValue());
				}
				
				if (definition != null) {
					for (ODocument valueDoc : docs) {
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
						configureDocument(valueDoc, db, childGroup);
					}
				}
				
				for (ORecordAbstract<?> fieldRec : recs) {
					configureDocument(fieldRec, db, null);
				}
			}
		}
	}

	/**
	 * Creates a group based on the given document
	 * 
	 * @param document the document
	 * @param definition the definition of the associated group
	 * @param db the database 
	 */
	public OGroup(ODocument document, DefinitionGroup definition,
			ODatabaseRecord db) {
		this.document = document;
		this.definition = definition;
		this.db = db;
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
	@Override
	public void addProperty(QName propertyName, Object value) {
		addProperty(propertyName, value, document);
	}
	
	/**
	 * Adds a property value to a given {@link ODocument}
	 * 
	 * @param propertyName the property name
	 * @param value the property value
	 * @param document the {link ODocument} where the value is to add
	 */
	@SuppressWarnings("unchecked")
	protected void addProperty(QName propertyName, Object value, ODocument document) {
		
		boolean isInstanceDocument = document == this.document;
		
		// convert instances to documents
		value = convertInstance(value);
		
		String pName = encodeProperty(propertyName);
		
		boolean collection = !isInstanceDocument || isCollectionProperty(propertyName);
		if (collection) {
			// combine value with previous ones
			Object oldValue = document.field(pName);
			if (oldValue == null) {
				// default: use list
				List<Object> valueList = new ArrayList<Object>();
				valueList.add(value);
				document.field(
						pName,
						valueList,
						(isInstanceDocument) ? (getCollectionType(propertyName))
								: (OType.EMBEDDEDLIST));
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
				document.field(pName, values, 
						(isInstanceDocument) ? (getCollectionType(propertyName))
						: (OType.EMBEDDEDLIST));
			}
		}
		else {
			// just set the field
			document.field(pName, value);
		}
	}

	/**
	 * Get the OrientDB collection type for the given property name
	 * @param propertyName the property name
	 * @return the collection type, either {@link OType#EMBEDDEDLIST} or 
	 *   {@link OType#LINKLIST}
	 */
	private OType getCollectionType(QName propertyName) {
		ChildDefinition<?> child = definition.getChild(propertyName);
		if (child != null) {
			if (child.asProperty() != null) {
				TypeDefinition propType = child.asProperty().getPropertyType();
				if (propType.getConstraint(HasValueFlag.class).isEnabled()) {
					return OType.EMBEDDEDLIST;
				}
				else {
					return OType.LINKLIST;
				}
			}
			else if (child.asGroup() != null) {
				// values must be OGroups
				return OType.LINKLIST;
			}
		}
		
		// default to embedded llist
		return OType.EMBEDDEDLIST;
	}

	/**
	 * Converts {@link Group}s and {@link Instance}s to {@link ODocument} but 
	 * leaves other objects untouched.
	 * 
	 * @param value the object to convert
	 * @return the converted object
	 */
	protected Object convertInstance(Object value) {
		if (value == null) 
			return null;
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
		/*
		 * XXX OrientDB can't deal with nested collections/lists!(?)
		 * as a work-around we also serialize collections
		 * see isSupportedFieldType 
		 */
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

			/*
			 * As collections of ORecordBytes are not supported (or rather 
			 * of records that are no documents, see embeddedCollectionToStream
			 * in ORecordSerializerCSVAbstract ~578) they are wrapped in a
			 * document.
			 */
//			return record;			
			ODocument doc = new ODocument();
			/*
			 * XXX Class name is set in configureDocument, as the class name
			 * may only bet set after the database was set.
			 */
//			doc.setClassName(BINARY_WRAPPER_CLASSNAME);
			doc.field(BINARY_WRAPPER_FIELD, record);
			return doc;
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
		else if (Double.class.isAssignableFrom(type) ||
				Float.class.isAssignableFrom(type) ||
				Integer.class.isAssignableFrom(type) ||
				Long.class.isAssignableFrom(type) ||
				Short.class.isAssignableFrom(type) ||
				Byte.class.isAssignableFrom(type) ||
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
			/*
			 * XXX OrientDB can't deal with nested collections/lists!(?)
			 * as a work-around we also serialize collections
			 */
//			return true;
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
//		ChildDefinition<?> child = definition.getChild(propertyName);
//		long max;
//		if (child instanceof PropertyDefinition) {
//			max = ((PropertyDefinition) child).getConstraint(Cardinality.class).getMaxOccurs();
//		}
//		else if (child instanceof GroupPropertyDefinition) {
//			max = ((GroupPropertyDefinition) child).getConstraint(Cardinality.class).getMaxOccurs();
//		}
//		else {
//			// default to true
//			return true;
//		}
//		
//		return max == Cardinality.UNBOUNDED || max > 1;
		//XXX treat everything as a collection property, as we may deal with merged instances
		return true;
	}

	/**
	 * @see MutableGroup#setProperty(QName, Object[])
	 */
	@Override
	public void setProperty(QName propertyName, Object... values) {
		setPropertyInternal(this.document, propertyName, values);
	}
			
	/**
	 * Sets values for a property in a certain ODocument
	 * 
	 * @param propertyName the property name
	 * @param values the values for the property
	 * @param document the document which should contain the data
	 */
	protected void setPropertyInternal(ODocument document, QName propertyName, Object... values) {
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
			document.field(pName, valueList,getCollectionType(propertyName));
		}
	}

	/**
	 * Encode a qualified property name to a string
	 * 
	 * @param propertyName the qualified property name
	 * @return the name encoded as a single string
	 */
	protected String encodeProperty(QName propertyName) {
		//TODO optimize encoding?
		// map namespace to short identifier
		propertyName = ONamespaceMap.map(propertyName);
		// encode name
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
			// decode name
			QName name = QName.valueOf(ONameUtil.decodeName(encodedProperty));
			// unmap namespace
			return ONamespaceMap.unmap(name);
		} catch (Throwable e) {
			throw new RuntimeException("Could not encode property name", e);
		}
	}


	/**
	 * @see Instance#getProperty(QName)
	 */
	@Override
	public Object[] getProperty(QName propertyName) {
		return getProperty(propertyName, this.document);
	}
	
	
	
	/**
	 * Gets a property value from a given {@link ODocument}
	 * 
	 * @param propertyName the property name
	 * @param document the {link ODocument} which contains the property
	 * @return an Array of Objects containing the needed property
	 */
	protected Object[] getProperty(QName propertyName, ODocument document) {
		associatedDbWithThread();
		
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
	 * Associate the database with the current thread (if set on the group)
	 */
	protected void associatedDbWithThread() {
		if (db != null) {
			ODatabaseRecordThreadLocal.INSTANCE.set(db);
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
	protected Object convertDocument(Object value, QName propertyName) {
		if (value instanceof ODocument) {
			ODocument doc = (ODocument) value;
			if (doc.containsField(BINARY_WRAPPER_FIELD)) {
				// extract wrapped ORecordBytes
				value = doc.field(BINARY_WRAPPER_FIELD);
			}
			else {
				ChildDefinition<?> child = definition.getChild(propertyName);
				if (child.asProperty() != null) {
					return new OInstance((ODocument) value, 
							child.asProperty().getPropertyType(), db,
							null); // no data set necessary for nested instances
				}
				else if (child.asGroup() != null) {
					return new OGroup((ODocument) value, child.asGroup(), db);
				}
				else {
					throw new IllegalStateException("Field " + propertyName + 
							" is associated neither with a property nor a group.");
				}
			}
		}
		//TODO also treat collections etc?
		
		//TODO objects that are not supported inside document
		if (value instanceof ORecordBytes) {
			//TODO try conversion first?!
			
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
	 * @see Group#getPropertyNames()
	 */
	@Override
	public Iterable<QName> getPropertyNames() {
		return getPropertyNames(this.document);
	}
		
	
	/**
	 * Returns the index keys of a certain ODocument
	 * @param document the keys are retrieved from
	 * @return an Iterable with the keys as QNames
	 */
	protected Iterable<QName> getPropertyNames(ODocument document) {
		associatedDbWithThread();
		
		Set<String> fields = new HashSet<String>(
				Arrays.asList(document.fieldNames()));
		
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
		return SPECIAL_FIELDS;
	}

	/**
	 * @see Group#getDefinition()
	 */
	@Override
	public DefinitionGroup getDefinition() {
		return definition;
	}

}
