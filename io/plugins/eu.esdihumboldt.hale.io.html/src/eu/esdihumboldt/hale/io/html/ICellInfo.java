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

package eu.esdihumboldt.hale.io.html;

/**
 * The Interface for representing information about a Cell
 * 
 * @author Kevin Mais
 */
public interface ICellInfo {

	/**
	 * Gets the location of the image for a Cell
	 * 
	 * @return the image location
	 */
	public String getImageLocation();

	/**
	 * Get an explanation for the cell.
	 * 
	 * @return the explanation or <code>null</code> if none is available
	 */
	public String getExplanation();

	/**
	 * Get the explanation in html format for the given cell
	 * 
	 * @return the cell explanation in html format
	 */
	public String getExplanationAsHtml();

}
