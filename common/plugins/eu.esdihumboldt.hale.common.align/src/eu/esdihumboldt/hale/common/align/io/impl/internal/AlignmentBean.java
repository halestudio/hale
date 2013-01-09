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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import eu.esdihumboldt.hale.common.align.io.impl.DefaultAlignmentIO;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Alignment bean serving as model for alignment I/O
 * 
 * @author Simon Templer
 */
public class AlignmentBean {

	private String base;
	private int nextCellId;
	private Collection<CellBean> cells = new LinkedHashSet<CellBean>();
	private Collection<ModifierBean> modifiers = new ArrayList<ModifierBean>();

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
			if (!cell.isActive())
				modifiers.add(new ModifierBean(ModifierBean.DEACTIVATE_CELL, alignment
						.getCellId(cell)));
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
	 * @throws MappingException if the mapping could not be loaded
	 * @throws MarshalException if the alignment could not be read
	 * @throws ValidationException if the input stream did not provide valid XML
	 */
	public MutableAlignment createAlignment(IOReporter reporter, TypeIndex sourceTypes,
			TypeIndex targetTypes) throws MappingException, MarshalException, ValidationException {
		MutableAlignment alignment = new DefaultAlignment();

		if (base != null) {
			try {
				// TODO try to update base path according to this alignments
				// path-change?
				Alignment baseAlignment = DefaultAlignmentIO.load(new DefaultInputSupplier(new URI(
						base)).getInput(), reporter, sourceTypes, targetTypes);
				alignment.setBaseAlignment(baseAlignment);
			} catch (URISyntaxException e) {
				throw new MarshalException("Invalid base alignment URI.", e);
			} catch (IOException e) {
				throw new MarshalException("Cannot open base alignment.", e);
			}
		}

		alignment.setNextCellId(nextCellId);

		for (CellBean cellBean : cells) {
			MutableCell cell = cellBean.createCell(reporter, sourceTypes, targetTypes);
			if (cell != null) {
				alignment.addCell(cell);
			}
		}

		for (ModifierBean modifierBean : modifiers) {
			if (ModifierBean.DEACTIVATE_CELL.equals(modifierBean.getName())) {
				MutableCell cell = (MutableCell) alignment.getCell(modifierBean.getValue());
				if (cell == null)
					; // TODO log warning?
				else
					cell.setActive(false);
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

	/**
	 * Get the alignment modifiers
	 * 
	 * @return the modifiers
	 */
	public Collection<ModifierBean> getModifiers() {
		return modifiers;
	}

	/**
	 * Set the alignment modifiers
	 * 
	 * @param modifiers the modifiers to set
	 */
	public void setModifiers(Collection<ModifierBean> modifiers) {
		this.modifiers = modifiers;
	}

	/**
	 * @return the nextCellId
	 */
	public int getNextCellId() {
		return nextCellId;
	}

	/**
	 * @param nextCellId the nextCellId to set
	 */
	public void setNextCellId(int nextCellId) {
		this.nextCellId = nextCellId;
	}

	/**
	 * @return the base
	 */
	public String getBase() {
		return base;
	}

	/**
	 * @param base the base to set
	 */
	public void setBase(String base) {
		this.base = base;
	}

}
