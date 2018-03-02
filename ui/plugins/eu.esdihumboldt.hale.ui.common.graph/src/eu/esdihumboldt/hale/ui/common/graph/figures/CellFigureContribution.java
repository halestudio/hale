/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.common.graph.figures;

import eu.esdihumboldt.hale.common.align.model.Cell;

/**
 * Interface for UI contributions to a {@link CellFigure}
 * 
 * @author Florian Esser
 */
public interface CellFigureContribution {

	/**
	 * Method that contributes to the {@link CellFigure}
	 * 
	 * @param figure CellFigure to contribute to
	 * @param cell the represented Cell
	 */
	void contribute(CellFigure figure, Cell cell);

	/**
	 * @return the number of columns contributed to the {@link CellFigure} label
	 */
	int getLabelColumnCount();
}
