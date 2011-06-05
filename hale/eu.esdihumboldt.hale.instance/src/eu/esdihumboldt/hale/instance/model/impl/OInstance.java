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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.OBase64Utils;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.instance.model.MutableInstance;
import eu.esdihumboldt.hale.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.schema.model.constraint.property.Cardinality;

/**
 * Instance implementation based on {@link ODocument}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class OInstance implements MutableInstance {

	private static final ALogger log = ALoggerFactory.getLogger(OInstance.class);
	
	/**
	 * Name for the special field for an instance value
	 */
	public static final String FIELD_VALUE = "___value___";
	
	//FIXME do encoding for other field names to support any characters in them? e.g. using OBase64Utils
	
	/**
	 * The document backing the instance
	 */
	private final ODocument document;
	
	/**
	 * The associated type definition
	 */
	private final TypeDefinition typeDefinition;
	
	/**
	 * Creates an empty instance associated with the given type.
	 * 
	 * @param typeDef the definition of the instance's type 
	 */
	public OInstance(TypeDefinition typeDef) {
		typeDefinition = typeDef;
		document = new ODocument();
		document.setClassName(typeDef.getIdentifier());
	}
	
	/**
	 * Creates an instance based on the given document
	 * 
	 * @param document the document
	 * @param typeDef the definition of the instance's type
	 */
	public OInstance(ODocument document, TypeDefinition typeDef) {
		this.document = document;
		this.typeDefinition = typeDef;
	}
	
	/**
	 * Copy constructor.
	 * Creates an instance based on the properties and values of the given 
	 * instance.
	 * 
	 * @param org the instance to copy
	 */
	public OInstance(Instance org) {
		this(org.getType());
		
		for (QName property : org.getPropertyNames()) {
			setProperty(property, org.getProperty(property).clone());
		}
		
		setValue(org.getValue());
	}

	/**
	 * @see MutableInstance#setValue(Object)
	 */
	@Override
	public void setValue(Object value) {
		document.field(FIELD_VALUE, value);
	}

	/**
	 * @see Instance#getValue()
	 */
	@Override
	public Object getValue() {
		return document.field(FIELD_VALUE);
	}

	/**
	 * @see MutableInstance#addProperty(QName, Object)
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
	 * Converts {@link Instance}s to {@link ODocument} but leaves other objects
	 * untouched.
	 * 
	 * @param value the object to convert
	 * @return the converted object
	 */
	private Object convertInstance(Object value) {
		if (value instanceof OInstance) {
			// special case: if possible use the internal document
			return ((OInstance) value).document;
		}
		else if (value instanceof Instance) {
			//FIXME also convert internal instances?
			OInstance tmp = new OInstance((Instance) value);
			return tmp.document;
		}
		//TODO also treat collections etc?
		
		return value;
	}

	/**
	 * Determines if a property can have multiple values
	 * 
	 * @param propertyName the property name
	 * @return if the property can have multiple values
	 */
	private boolean isCollectionProperty(QName propertyName) {
		PropertyDefinition property = typeDefinition.getChild(propertyName);
		if (property == null) {
			// default to true
			return true;
		}
		
		return property.getConstraint(Cardinality.class).getMaxOccurs() > 1;
	}

	/**
	 * @see MutableInstance#setProperty(QName, Object[])
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
		try {
			return OBase64Utils.encodeObject(propertyName, OBase64Utils.GZIP);
		} catch (IOException e) {
			throw new RuntimeException("Could not encode property name", e);
		}
	}
	
	/**
	 * Decode an encoded property name to a qualified name
	 * 
	 * @param encodedProperty the encoded property name
	 * @return the qualified property name
	 */
	protected QName decodeProperty(String encodedProperty) {
		try {
			return (QName) OBase64Utils.decodeToObject(encodedProperty, OBase64Utils.GZIP, QName.class.getClassLoader());
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
			PropertyDefinition property = typeDefinition.getChild(propertyName);
			return new OInstance((ODocument) value, property.getPropertyType());
		}
		//TODO also treat collections etc?
		
		return value;
	}

	/**
	 * @see Instance#getPropertyNames()
	 */
	@Override
	public Iterable<QName> getPropertyNames() {
		Set<String> fields = new HashSet<String>(document.fieldNames());
		
		// remove value field
		fields.remove(FIELD_VALUE);
		
		Set<QName> qFields = new HashSet<QName>();
		for (String field : fields) {
			qFields.add(decodeProperty(field));
		}
		
		return qFields;
	}

	/**
	 * @see Instance#getType()
	 */
	@Override
	public TypeDefinition getType() {
		return typeDefinition;
	}

}
