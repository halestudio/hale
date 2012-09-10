/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.mediator.constraints;

import java.util.List;

/**
 * The {@link LanguageConstraint} allows the user to express his preferences for
 * the natural language that the data sets should be presented in. For this
 * purpose, ISO 639-2 and ISO 639-3 language codes may be used. </ul>
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id$
 */
public interface LanguageConstraint extends Constraint {

	/**
	 * @return a {@link List} of ISO 639-2 language codes, e.g. eng, ger or fra,
	 *         in descending order of priority. The language on position 0 has
	 *         the highest, the language on position n-1 the lowest priority.
	 */
	public List<String> getLanguageCodes();

}
