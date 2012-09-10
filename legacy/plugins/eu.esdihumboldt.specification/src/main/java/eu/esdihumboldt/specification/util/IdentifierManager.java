/*
 * HUMBOLDT: A Framework for Data Harmonistation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.util;

/**
 * The IdentifierManager is used internally during one session to generate
 * simple identifiers that need not be persisted.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id: IdentifierManager.java,v 1.1 2007-11-06 10:26:29 pitaeva Exp $
 */
public class IdentifierManager {

	/**
	 * The internal counter for the UIDs used in the application.
	 */
	private static long current = 0;

	/**
	 * @return the next long value that has not been used yet.
	 */
	public static synchronized long next() {
		IdentifierManager.current++;
		return IdentifierManager.current;
	}
}
