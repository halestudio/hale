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
 * The information of one cell in the table mapping
 * 
 * @author Patrick Lieb
 */
public class CellInfo {

	// the entries of the same position in hierarchy and text belong together
	private final List<Integer> position = new ArrayList<Integer>();
	private final List<String> text = new ArrayList<String>();

	/**
	 * @param text the text to add to the cell
	 */
	public void addText(String info, int pos) {
		text.add(info);
		position.add(pos);
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
	public List<Integer> getPositions() {
		return position;
	}
//
//	/**
//	 * @param position the hierarchy level to add
//	 */
//	public void addPosition(int position) {
//		this.position.add(position);
//	}

}
