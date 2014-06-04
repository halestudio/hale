/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.common.graph.content;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.zest.core.viewers.IGraphContentProvider;

import eu.esdihumboldt.hale.common.align.model.Cell;

/**
 * Content Provider to process Dummy/Empty Nodes. This Class extends the
 * ReverseCellGraphContent Provider and is used to handle empty Cells.
 * 
 * @author Yasmina Kammeyer
 */
public class DummyCellGraphContentProvider extends ReverseCellGraphContentProvider {

	private final String leftText;
	private final String rightText;
	private final String text;

	/**
	 * The parameter set the Text used for three nodes if the input is a
	 * dummy/empty cell.
	 * 
	 * @param leftNodeText The text shown in the left Node; "Source" if null.
	 * @param middleNodeText The text shown in the middle Node; "Transformation"
	 *            if null.
	 * @param rightNodeText The text shown in the right Node; "Target" is null.
	 */
	public DummyCellGraphContentProvider(String leftNodeText, String middleNodeText,
			String rightNodeText) {
		if (leftNodeText == null) {
			leftText = "Source";
		}
		else
			leftText = leftNodeText;
		if (middleNodeText == null) {
			text = "Transformation";
		}
		else
			text = middleNodeText;
		if (rightNodeText == null) {
			rightText = "Target";
		}
		else
			rightText = rightNodeText;
	}

	/**
	 * Default Constructor creates 'Source->Transformation->Target' Graph if the
	 * input is a dummy/empty cell
	 */
	public DummyCellGraphContentProvider() {
		leftText = "Source";
		text = "Transformation";
		rightText = "Target";
	}

	/**
	 * @see IGraphContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object input) {
		List<Edge> edges = new ArrayList<Edge>();
		// proceed if the input is a "dummy" cell
		if (input instanceof Cell) {
			Cell cell = (Cell) input;
			addEdges(cell, edges);
		}
		return edges.toArray();
	}

	/**
	 * Adds the edges for the given cell to the given list. Dummy Cell if Source
	 * and Target of the Cell is null.
	 * 
	 * @param cell the cell to add
	 * @param edges the list to add the edges to
	 */
	@Override
	protected void addEdges(Cell cell, List<Edge> edges) {

		if (cell.getTarget() == null && cell.getSource() == null) {
			edges.add(new Edge(leftText, text, null));
			edges.add(new Edge(text, rightText, null));
		}
		else {
			super.addEdges(cell, edges);
		}
	}

}
