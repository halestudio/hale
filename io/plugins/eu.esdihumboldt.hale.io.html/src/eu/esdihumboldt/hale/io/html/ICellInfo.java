/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
	 * Get an explanation for the cell, could be <code>null</code> if none is
	 * available
	 * 
	 * @return the explanation
	 */
	public String getExplanation();

	/**
	 * Get the explanation in HTML format for the given cell
	 * 
	 * @return the cell explanation in HTML format
	 */
	public String getExplanationAsHtml();

	/**
	 * Get the cell notes, could be <code>null</code>
	 * 
	 * @return the notes
	 */
	public String getNotes();

	/**
	 * Returns the displayed name of the source cell(s), may be
	 * <code>null</code>
	 * 
	 * @return the name of the source cell(s)
	 */
	public String getSourceName();

	/**
	 * Returns the displayed name of the target cell(s), may be
	 * <code>null</code>
	 * 
	 * @return the name of the target cell(s)
	 */
	public String getTargetName();

	/**
	 * @return the complete name of the source cell(s), may be <code>null</code>
	 */
	public String getCompleteSourceName();

	/**
	 * @return the complete name of the source cell(s), may be <code>null</code>
	 */
	public String getCompleteTargetName();

}
