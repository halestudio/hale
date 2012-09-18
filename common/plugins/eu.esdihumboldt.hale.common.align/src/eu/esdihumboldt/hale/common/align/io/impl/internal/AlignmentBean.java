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

package eu.esdihumboldt.hale.common.align.io.impl.internal;

import java.util.Collection;
import java.util.LinkedHashSet;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Alignment bean serving as model for alignment I/O
 * 
 * @author Simon Templer
 */
public class AlignmentBean {

	private Collection<CellBean> cells = new LinkedHashSet<CellBean>();

	/**
	 * Default constructor
	 */
	public AlignmentBean() {
		super();
	}

	/**
	 * Create a bean for the given alignment
	 * 
	 * @param alignment the alignment
	 */
	public AlignmentBean(Alignment alignment) {
		super();

		// populate bean from alignment
		for (Cell cell : alignment.getCells()) {
			CellBean cellBean = new CellBean(cell);
			cells.add(cellBean);
		}
	}

	/**
	 * Create an alignment from the information in the bean
	 * 
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 * @param sourceTypes the source types to use for resolving definition
	 *            references
	 * @param targetTypes the target types to use for resolving definition
	 *            references
	 * @return the alignment
	 */
	public MutableAlignment createAlignment(IOReporter reporter, TypeIndex sourceTypes,
			TypeIndex targetTypes) {
		MutableAlignment alignment = new DefaultAlignment();

		for (CellBean cellBean : cells) {
			MutableCell cell = cellBean.createCell(reporter, sourceTypes, targetTypes);
			if (cell != null) {
				alignment.addCell(cell);
			}
		}

		return alignment;
	}

	/**
	 * Get the defined cells
	 * 
	 * @return the cells
	 */
	public Collection<CellBean> getCells() {
		return cells;
	}

	/**
	 * Set the defined cells
	 * 
	 * @param cells the cells to set
	 */
	public void setCells(Collection<CellBean> cells) {
		this.cells = cells;
	}

}
