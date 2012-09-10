/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
