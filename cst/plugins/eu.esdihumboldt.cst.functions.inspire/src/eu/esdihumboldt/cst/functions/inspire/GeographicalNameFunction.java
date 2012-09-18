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
