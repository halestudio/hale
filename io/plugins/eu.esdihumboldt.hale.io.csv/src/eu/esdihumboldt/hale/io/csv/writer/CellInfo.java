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

package eu.esdihumboldt.hale.io.csv.writer;

import java.util.ArrayList;
import java.util.List;

/**
 * The information of one cell in the mapping
 * 
 * @author Patrick Lieb
 */
public class CellInfo {

	private final List<Integer> hierarchy = new ArrayList<Integer>();

	private final List<String> text = new ArrayList<String>();

	/**
	 * @param text the text to add to the cell
	 */
	public void addText(String text) {
		this.text.add(text);
	}

	/**
	 * @return the text of the cell
	 */
	public List<String> getText() {
		return text;
	}

	/**
	 * The hierarchy levels list is bound to the text list Each entry in levels
	 * list is corresponding to the entry in the text list at the same position.
	 * 
	 * @return the hierarchy levels
	 */
	public List<Integer> getHierarchyLevels() {
		return hierarchy;
	}

	/**
	 * @param hierarchyLevel the hierarchy level to add
	 */
	public void addHierarchyLevel(int hierarchyLevel) {
		hierarchy.add(hierarchyLevel);
	}

}
