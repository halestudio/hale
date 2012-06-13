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

package eu.esdihumboldt.cst.functions.inspire;
/**
 * Constants for the geographical name function
 * 
 * @author Kevin Mais
 *
 */
@SuppressWarnings("javadoc")
public interface GeographicalNameFunction {

	public static final String ID = "eu.esdihumboldt.cst.functions.inspire.geographicalname";
	
	public static final String PROPERTY_TEXT = "text"; //$NON-NLS-1$
	public static final String PROPERTY_SCRIPT = "script"; //$NON-NLS-1$
	public static final String PROPERTY_TRANSLITERATION = "transliterationScheme"; //$NON-NLS-1$
	public static final String PROPERTY_NAMESTATUS = "nameStatus"; //$NON-NLS-1$
	public static final String PROPERTY_LANGUAGE = "language"; //$NON-NLS-1$
	public static final String PROPERTY_NATIVENESS = "nativeness"; //$NON-NLS-1$
	public static final String PROPERTY_SOURCEOFNAME = "sourceOfName"; //$NON-NLS-1$
	public static final String PROPERTY_PRONUNCIATIONIPA = "pronunciationIPA"; //$NON-NLS-1$
	public static final String PROPERTY_PRONUNCIATIONSOUNDLINK = "pronunciationSoundLink"; //$NON-NLS-1$
	public static final String PROPERTY_GRAMMA_GENDER = "grammaticalGender"; //$NON-NLS-1$
	public static final String PROPERTY_GRAMMA_NUMBER = "grammaticalNumber"; //$NON-NLS-1$
	
	public static final String INSPIRE_IDENTIFIER_PREFIX = "urn:x-inspire:object:id"; //$NON-NLS-1$
	
}
