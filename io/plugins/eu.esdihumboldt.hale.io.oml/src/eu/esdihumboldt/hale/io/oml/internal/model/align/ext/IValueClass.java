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
package eu.esdihumboldt.hale.io.oml.internal.model.align.ext;

import java.util.List;

/**
 * The superinterface for all ValueClasses.
 * 
 * @author A. Pitaev, Logica
 */
@SuppressWarnings("javadoc")
public interface IValueClass {

	/**
	 * returns a list of the ValueExpressions
	 */
	public List<IValueExpression> getValue();

	/**
	 * return a class/attribute in case of reference
	 * 
	 * @return
	 */
	public String getResource();

	/**
	 * Gets the value of the about property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getAbout();

}
