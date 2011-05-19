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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.orientechnologies.orient.core.record.impl.ODocument;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.instance.model.InstanceModel;
import eu.esdihumboldt.hale.instance.model.MutableInstance;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Instance implementation based on {@link ODocument}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class OInstance implements MutableInstance {
	
	private static final ALogger log = ALoggerFactory.getLogger(OInstance.class);
	
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
		
		InstanceModel.copyProperties(org, this);
	}

	/**
	 * @see MutableInstance#addProperty(String, Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addProperty(String propertyName, Object value) {
		// convert instances to documents
		value = convertInstance(value);
		
		boolean collection = isCollectionProperty(propertyName);
		if (collection) {
			// combine value with previous ones
			Object oldValue = document.field(propertyName);
			if (oldValue == null) {
				// default: use list
				List<Object> valueList = new ArrayList<Object>();
				valueList.add(value);
				document.field(propertyName, valueList); //XXX need to add OType.EMBEDDEDLIST?
			}
			else if (oldValue instanceof Collection<?>) {
				// add value to collection
				((Collection) oldValue).add(value);
			}
			else if (oldValue.getClass().isArray()) {
				// create new array
				Object[] oldArray = (Object[]) oldValue;
				Object[] values = new Object[oldArray.length + 1];
				System.arraycopy(oldArray, 0, values, 0, oldArray.length);
				values[oldArray.length] = value;
				document.field(propertyName, values); //XXX need to add OType.EMBEDDEDLIST?
			}
		}
		else {
			// just set the field
			document.field(propertyName, convertInstance(value));
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
	private boolean isCollectionProperty(String propertyName) {
		AttributeDefinition property = typeDefinition.getAttribute(propertyName);
		if (property == null) {
			// default to true
			return true;
		}
		
		return property.getMaxOccurs() > 1;
	}

	/**
	 * @see MutableInstance#setProperty(String, Object[])
	 */
	@Override
	public void setProperty(String propertyName, Object... values) {
		if (values == null || values.length == 0) {
			document.removeField(propertyName);
			return;
		}
		
		boolean collection = isCollectionProperty(propertyName);
		
		if (!collection) {
			if (values.length > 1) {
				//TODO log type and property
				log.warn("Attempt to set multiple values on a property that supports only one, using only the first value");
			}
			
			document.field(propertyName, convertInstance(values[0]));
		}
		else {
			List<Object> valueList = new ArrayList<Object>();
			for (Object value : values) {
				valueList.add(convertInstance(value));
			}
			document.field(propertyName, valueList); //XXX need to add OType.EMBEDDEDLIST?
		}
	}

	/**
	 * @see Instance#getProperty(String)
	 */
	@Override
	public Object[] getProperty(String propertyName) {
		Object value = document.field(propertyName);
		
		if (value == null) {
			return null;
		}
		else if (value instanceof Iterable<?>) {
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
	private Object convertDocument(Object value, String propertyName) {
		if (value instanceof ODocument) {
			AttributeDefinition property = typeDefinition.getAttribute(propertyName);
			return new OInstance((ODocument) value, property.getAttributeType());
		}
		//TODO also treat collections etc?
		
		return value;
	}

	/**
	 * @see Instance#getPropertyNames()
	 */
	@Override
	public Iterable<String> getPropertyNames() {
		return document.fieldNames();
	}

	/**
	 * @see Instance#getType()
	 */
	@Override
	public TypeDefinition getType() {
		return typeDefinition;
	}

}
