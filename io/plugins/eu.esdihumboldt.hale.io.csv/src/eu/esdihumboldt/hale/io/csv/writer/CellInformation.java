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
public class CellInformation {

	// the entries of the same position in hierarchy and text belong together
	private final List<Integer> position = new ArrayList<Integer>();
	private final List<String> text = new ArrayList<String>();

	/**
	 * @param info the text to show in the cell
	 * @param position the position of the text in the cell
	 */
	public void addText(String info, int position) {
		text.add(info);
		this.position.add(position);
	}

	/**
	 * @return the text of the cell
	 */
	public List<String> getText() {
		return text;
	}

	/**
	 * The position list belongs to the text list. Each entry in the position
	 * list is corresponding to the entry in the text list at the same position.
	 * 
	 * @return the hierarchy levels
	 */
	public List<Integer> getPositions() {
		return position;
	}
}
