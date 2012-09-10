package eu.esdihumboldt.commons.mediator.usermanagement.impl;

import org.exolab.castor.mapping.GeneralizedFieldHandler;

/**
 * @author pitaeva
 * 
 */
public class LanguageFieldHandler extends GeneralizedFieldHandler {

	/**
	 * IETF RFC 1766 tag separator
	 */
	public final static char IETF_SEPARATOR = '-';

	/**
	 * empty string declarator
	 */
	public final static String EMPTY_STRING = "";

	/**
	 * Default Constructor
	 */
	public LanguageFieldHandler() {
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
	 *            be a Locale object
	 * @return RFC-1766 String-xsd:language element
	 */
	@Override
	public Object convertUponGet(Object locale) {

		// Locale loc = (Locale) locale;
		// String xsdLanguage = EMPTY_STRING;
		// if (!(locale == null)) {
		// xsdLanguage = loc.getLanguage().toLowerCase();
		// if (!loc.getCountry().equals(EMPTY_STRING)) {
		// xsdLanguage = xsdLanguage + IETF_SEPARATOR
		// + loc.getCountry().toUpperCase();
		// }
		// }
		//
		// return xsdLanguage;
		return locale;

	}

	/**
	 * This method is used to convert the value when the setValue method is
	 * called. The setValue method will call this method to obtain the converted
	 * value. The converted value will then be used as the value to set for the
	 * field.
	 * 
	 * @param xsd
	 *            -language element:RFC-1766 String
	 * @return a java.util.Locale
	 */
	@Override
	public Object convertUponSet(Object xsdLanguage) {
		// //return (stringUUID == null)? null :UUID.fromString((String)
		// stringUUID);
		// String lang = (String) xsdLanguage;
		// if ((lang == null) || lang.equals(EMPTY_STRING)) { // not specified
		// => getDefault
		// return Locale.getDefault();
		// }
		// String language = EMPTY_STRING;
		// String country = EMPTY_STRING;
		//
		// int i1 = lang.indexOf(IETF_SEPARATOR);
		// if (i1 < 0) {
		// language = lang;
		// } else {
		// language = lang.substring(0, i1);
		// ++i1;
		// int i2 = lang.indexOf(IETF_SEPARATOR, i1);
		// if (i2 < 0) {
		// country = lang.substring(i1);
		// } else {
		// country = lang.substring(i1, i2);
		//
		// }
		// }
		//
		// if (language.length() == 2) {
		// language = language.toLowerCase();
		// } else {
		// language = EMPTY_STRING;
		// }
		//
		// if (country.length() == 2) {
		// country = country.toUpperCase();
		// } else {
		// country = EMPTY_STRING;
		// }
		//
		// return new Locale(language, country);
		return xsdLanguage;
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
		// return java.util.Locale.class;
		return String.class;
	}

}
