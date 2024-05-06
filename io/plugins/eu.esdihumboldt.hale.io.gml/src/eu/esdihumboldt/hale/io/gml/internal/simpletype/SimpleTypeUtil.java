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

package eu.esdihumboldt.hale.io.gml.internal.simpletype;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;

import org.apache.xmlbeans.GDate;
import org.apache.xmlbeans.GDateBuilder;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlDate;
import org.apache.xmlbeans.XmlDateTime;
import org.apache.xmlbeans.XmlDecimal;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlFloat;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlLong;
import org.apache.xmlbeans.XmlShort;
import org.apache.xmlbeans.XmlTime;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;

import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ElementType;

/**
 * Utility methods used for simple type conversion
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class SimpleTypeUtil {

	/**
	 * XML simple type names mapped to the corresponding XmlBeans type
	 */
	private static final Map<String, Class<? extends XmlAnySimpleType>> TYPE_MAP = new HashMap<String, Class<? extends XmlAnySimpleType>>();

	static {
		// TODO add additional simple types

		// assuming number and string types are correctly converted using
		// toString

		// time/date types
		TYPE_MAP.put("dateTime", XmlDateTime.class); //$NON-NLS-1$
		TYPE_MAP.put("date", XmlDate.class); //$NON-NLS-1$
		TYPE_MAP.put("time", XmlTime.class); //$NON-NLS-1$

		TYPE_MAP.put("decimal", XmlDecimal.class); //$NON-NLS-1$
		TYPE_MAP.put("double", XmlDouble.class); //$NON-NLS-1$
		TYPE_MAP.put("float", XmlFloat.class); //$NON-NLS-1$
		TYPE_MAP.put("int", XmlInt.class); //$NON-NLS-1$
		TYPE_MAP.put("integer", XmlInteger.class); //$NON-NLS-1$
		TYPE_MAP.put("long", XmlLong.class); //$NON-NLS-1$
		TYPE_MAP.put("short", XmlShort.class); //$NON-NLS-1$
	}

	/**
	 * Convert a simple type value to a string
	 * 
	 * @param <T> the type of the value
	 * @param value the value
	 * @param type the type definition of the simple type
	 * @return the string representation of the value or <code>null</code> if
	 *         the value is <code>null</code>
	 */
	public static <T> String convertToXml(T value, TypeDefinition type) {
		if (value == null) {
			return null;
		}

		ConversionService conversionService = HalePlatform.getService(ConversionService.class);
		Class<? extends XmlAnySimpleType> simpleType = getSimpleType(type);

		if (simpleType != null) {
			try {
				XmlAnySimpleType simpleTypeValue = conversionService.convert(value, simpleType);
				if (simpleTypeValue instanceof XmlDate) {
					XmlDate xmlDate = (XmlDate) simpleTypeValue;
					Calendar calendar = xmlDate.getCalendarValue();
					GDateBuilder builder = new GDateBuilder(calendar);
					// remove time zone as value contains no time information
					builder.clearTimeZone();
					GDate gdate = builder.toGDate();

					xmlDate.setGDateValue(gdate);
				}
				else if (simpleTypeValue instanceof XmlDateTime) {
					XmlDateTime xmlDateTime = (XmlDateTime) simpleTypeValue;

					// use Zulu time to have a reproducable result
					// (as the old Java Date types always assume the current
					// time zone!)
					//
					// XXX this should be removed when time/date types are used
					// that are timezone aware
					Calendar calendar = xmlDateTime.getCalendarValue();
					GDateBuilder builder = new GDateBuilder(calendar);
					builder.normalizeToTimeZone(0);
					GDate gdate = builder.toGDate();

					xmlDateTime.setGDateValue(gdate);
				}
				else if (simpleTypeValue != null && simpleTypeValue instanceof XmlAnySimpleType) {
					// Numbers should be handled here
					return simpleTypeValue.getStringValue();
				}
			} catch (ConversionException e) {
				// ignore
			}
		}

		// try to convert to string
		try {
			String stringValue = conversionService.convert(value, String.class);
			if (stringValue != null) {
				return stringValue;
			}
		} catch (ConversionException e) {
			// ignore
		}

		// fall-back
		return value.toString();
	}

	/**
	 * Convert a string belonging to a XML simple type to the binding specified
	 * by the given type definition.
	 * 
	 * @param value the string value
	 * @param type the type definition
	 * @return <code>null</code> if the string was <code>null</code>, the
	 *         converted object with the binding type if possible, otherwise the
	 *         original string
	 */
	public static Object convertFromXml(String value, TypeDefinition type) {
		if (value == null) {
			return null;
		}

		Class<? extends XmlAnySimpleType> simpleType = getSimpleType(type);
		Class<?> binding = type.getConstraint(Binding.class).getBinding();

		if (List.class.isAssignableFrom(binding)) { // XXX also for collection
													// binding?
			// we are dealing with a simple type list
			// items separated by whitespace
			String[] elements = value.trim().split("\\s+");
			ElementType elementType = type.getConstraint(ElementType.class);

			Class<? extends XmlAnySimpleType> elementSimpleType = null;
			if (elementType.getDefinition() != null) {
				elementSimpleType = getSimpleType(elementType.getDefinition());
			}
			Class<?> elementBinding = elementType.getBinding();

			List<Object> result = new ArrayList<Object>();

			for (String element : elements) {
				Object convElement = convertFromXml(element, elementSimpleType, elementBinding);
				result.add(convElement);
			}

			return result;
		}

		// convert ordinary value
		return convertFromXml(value, simpleType, binding);
	}

	private static Object convertFromXml(String value, Class<? extends XmlAnySimpleType> simpleType,
			Class<?> binding) {
		ConversionService conversionService = HalePlatform.getService(ConversionService.class);

		// try using simple type for conversion
		if (simpleType != null && conversionService.canConvert(String.class, simpleType)) {
			try {
				XmlAnySimpleType simpleValue = conversionService.convert(value, simpleType);
				if (simpleValue != null) {
					try {
						Object bindingValue = conversionService.convert(simpleValue, binding);
						return bindingValue;
					} catch (ConversionException e) {
						// ignore
					}
				}
				return simpleValue;
			} catch (ConversionException e) {
				// ignore
			}
		}

		// try direct conversion
		try {
			Object result = conversionService.convert(value, binding);
			return result;
		} catch (ConversionException e) {
			// ignore
		}

		return value;
	}

	/**
	 * Get the XmlBeans simple type class for the given type definition
	 * 
	 * @param type the type definition
	 * 
	 * @return the XmlBeans simple type class
	 */
	private static Class<? extends XmlAnySimpleType> getSimpleType(TypeDefinition type) {
		if (type == null) {
			return null;
		}

		if (type.getName().getNamespaceURI().equals(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
			Class<? extends XmlAnySimpleType> simpleType = TYPE_MAP
					.get(type.getName().getLocalPart());

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
