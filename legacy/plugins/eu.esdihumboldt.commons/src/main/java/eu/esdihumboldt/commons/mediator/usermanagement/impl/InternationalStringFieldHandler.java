package eu.esdihumboldt.commons.mediator.usermanagement.impl;

import java.util.Locale;

import org.exolab.castor.mapping.GeneralizedFieldHandler;
import org.geotools.util.SimpleInternationalString;

/**
 * @author pitaeva
 * 
 */
public class InternationalStringFieldHandler extends GeneralizedFieldHandler {

	/**
	 * Default Constructor
	 */
	public InternationalStringFieldHandler() {
		super();
	}

	/**
	 * This method is used to convert the value when the getValue method is
	 * called. The getValue method will obtain the actual field value from given
	 * 'parent' object. This convert method is then invoked with the field's
	 * value. The value returned from this method will be the actual value
	 * returned by getValue method.
	 * 
	 * @param should
	 *            be an InternationalString object
	 * @return String representation of the InternationalString
	 */
	@Override
	public Object convertUponGet(Object is) {
		// A simple international string consists of a single string for all
		// locales.
		// This methods uses the en-locale, to get the StringRepresentation of
		// this object.
		return (is == null) ? null : ((SimpleInternationalString) is)
				.toString(new Locale("en"));
	}

	/**
	 * This method is used to convert the value when the setValue method is
	 * called. The setValue method will call this method to obtain the converted
	 * value. The converted value will then be used as the value to set for the
	 * field.
	 * 
	 * @param String
	 *            representation of InterantionalString
	 * @return a org.geotools.util.SimpleInternationalString
	 */
	@Override
	public Object convertUponSet(Object stringIS) {
		return (stringIS == null) ? null : new SimpleInternationalString(
				(String) stringIS);
	}

	/**
	 * Returns the class type for the field that this GeneralizedFieldHandler
	 * converts to and from. This should be the type that is used in the object
	 * model.
	 * 
	 * @return String.class
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class getFieldType() {
		return org.geotools.util.SimpleInternationalString.class;
	}

}
