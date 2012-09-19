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

package eu.esdihumboldt.hale.io.oml.internal.goml.rdf;

import java.util.List;

import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IAbout;

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
