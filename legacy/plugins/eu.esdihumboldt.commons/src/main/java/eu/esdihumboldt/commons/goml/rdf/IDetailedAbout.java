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

package eu.esdihumboldt.commons.goml.rdf;

import java.util.List;

import eu.esdihumboldt.specification.cst.rdf.IAbout;

/**
 * Detailed about that states namespace, feature class and (nested) properties
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface IDetailedAbout extends IAbout {

	/**
	 * The main delimiter in the about string, e.g between namespace and feature
	 * class
	 */
	public static final char MAIN_DELIMITER = '/';

	/**
	 * The property delimiter in the about string
	 */
	public static final char PROPERTY_DELIMITER = ';';

	/**
	 * Get the namespace
	 * 
	 * @return the namespace
	 */
	public String getNamespace();

	/**
	 * Get the feature class name
	 * 
	 * @return the feature class name
	 */
	public String getFeatureClass();

	/**
	 * Get the list of property names identifying a (nested) property. The first
	 * property name specifies a property of the feature class identified by
	 * {@link #getFeatureClass()}, the second property name specifies a property
	 * of that property and so on.
	 * 
	 * @return the list of property names, may be <code>null</code> if no
	 *         property is identified by this {@link IAbout}
	 */
	public List<String> getProperties();

}
