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

package eu.esdihumboldt.hale.io.gml.internal.simpletype;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.convert.ConversionException;
import org.apache.commons.convert.Converter;
import org.apache.commons.convert.Converters;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlDate;
import org.apache.xmlbeans.XmlDateTime;
import org.apache.xmlbeans.XmlTime;

import eu.esdihumboldt.hale.io.gml.internal.simpletype.converters.DateTimeConverters;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Utility methods used for simple type conversion
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SimpleTypeUtil {
	
	/**
	 * Schema namespace
	 */
	private static final String SCHEMA_NS = "http://www.w3.org/2001/XMLSchema"; //$NON-NLS-1$

	/**
	 * XML simple type names mapped to the corresponding XmlBeans type
	 */
	private static final Map<String, Class<? extends XmlAnySimpleType>> TYPE_MAP = new HashMap<String, Class<? extends XmlAnySimpleType>>();
	static {
		//TODO add additional simple types
		
		// assuming number and string types are correctly converted using toString
		
		// time/date types
		TYPE_MAP.put("dateTime", XmlDateTime.class); //$NON-NLS-1$
		TYPE_MAP.put("date", XmlDate.class); //$NON-NLS-1$
		TYPE_MAP.put("time", XmlTime.class); //$NON-NLS-1$
	}
	
	private static boolean initialized = false;
	
	/**
	 * Initialize: register converters
	 */
	private static void init() {
		if (!initialized) {
			//TODO add additional converters
			Converters.loadContainedConverters(DateTimeConverters.class);
			
			initialized = true;
		}
	}

	/**
	 * Convert a simple type value to a string
	 * 
	 * @param <T> the type of the value
	 * @param value the value
	 * @param type the type definition of the simple type
	 * @return the string representation of the value or 
	 * <code>null</code> if the value is <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public static <T> String convert(T value, TypeDefinition type) {
		init();
		
		if (value == null) {
			return null;
		}
		
		Class<? extends XmlAnySimpleType> simpleType = getSimpleType(type);
		
		if (simpleType != null) {
			try {
				Converter<T, ? extends XmlAnySimpleType> converter = 
					(Converter<T, ? extends XmlAnySimpleType>) Converters.getConverter(value.getClass(), simpleType);
				
				if (converter != null) {
					XmlAnySimpleType simpleTypeValue = converter.convert(value);
					if (simpleTypeValue != null) {
						return simpleTypeValue.getStringValue();
					}
				}
			} catch (ClassNotFoundException e) {
				// ignore
			} catch (ConversionException e) {
				// ignore
			}
		}
		
		// try to convert to string
		try {
			Converter<T, String> converter = 
				(Converter<T, String>) Converters.getConverter(value.getClass(), String.class);
			
			if (converter != null) {
				String stringValue = converter.convert(value);
				if (stringValue != null) {
					return stringValue;
				}
			}
		} catch (ClassNotFoundException e) {
			// ignore
		} catch (ConversionException e) {
			// ignore
		}
		
		// fall-back
		return value.toString();
	}

	/**
	 * Get the XmlBeans simple type class for the given type definition
	 * 
	 * @param type the type definition
	 * 
	 * @return the XmlBeans simple type class
	 */
	private static Class<? extends XmlAnySimpleType> getSimpleType(
			TypeDefinition type) {
		if (type.getName().getNamespaceURI().equals(SCHEMA_NS)) {
			Class<? extends XmlAnySimpleType> simpleType = 
				TYPE_MAP.get(type.getName().getLocalPart());
			
			if (simpleType != null) {
				return simpleType;
			}
		}
		
		if (type.getSuperType() != null) {
			return getSimpleType(type.getSuperType());
		}
		
		return null;
	}
	
}
