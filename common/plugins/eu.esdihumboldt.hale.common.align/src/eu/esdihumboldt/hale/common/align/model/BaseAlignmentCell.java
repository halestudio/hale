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

package eu.esdihumboldt.hale.common.align.model;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;

/**
 * Cell wrapper for cells of a base alignment.
 * 
 * @author Kai Schwierczek
 */
public class BaseAlignmentCell implements Cell {

	private final Cell base;
	private final URI baseAlignment;
	private final String prefix;
	private final Set<Cell> disabledFor = new HashSet<Cell>();

	/**
	 * Constructor.
	 * 
	 * @param base the base cell
	 * @param baseAlignment the uri of the alignment the cell is from
	 * @param prefix the alignment's prefix
	 */
	public BaseAlignmentCell(Cell base, URI baseAlignment, String prefix) {
		this.base = base;
		this.baseAlignment = baseAlignment;
		this.prefix = prefix;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getSource()
	 */
	@Override
	public ListMultimap<String, ? extends Entity> getSource() {
		return base.getSource();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getTarget()
	 */
	@Override
	public ListMultimap<String, ? extends Entity> getTarget() {
		return base.getTarget();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getTransformationParameters()
	 */
	@Override
	public ListMultimap<String, ParameterValue> getTransformationParameters() {
		return base.getTransformationParameters();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getTransformationIdentifier()
	 */
	@Override
	public String getTransformationIdentifier() {
		return base.getTransformationIdentifier();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getId()
	 */
	@Override
	public String getId() {
		return prefix + ':' + base.getId();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getDisabledFor()
	 */
	@Override
	public Set<Cell> getDisabledFor() {
		return Collections.unmodifiableSet(new HashSet<Cell>(Sets.union(disabledFor,
				base.getDisabledFor())));
	}

	/**
	 * Returns the base alignment.
	 * 
	 * @return the base alignment
	 */
	public URI getBaseAlignment() {
		return baseAlignment;
	}

	/**
	 * Returns the result of {@link #getDisabledFor()} from the base cell.
	 * 
	 * @return the result of {@link #getDisabledFor()} from the base cell
	 */
	public Set<Cell> getBaseDisabledFor() {
		return base.getDisabledFor();
	}

	/**
	 * Returns the additional disabled for entries of the extended cell.
	 * 
	 * @return the additional disabled for entries of the extended cell
	 */
	public Set<Cell> getAdditionalDisabledFor() {
		return disabledFor;
	}

	/**
	 * Re-enables/disables this cell for the given cell. Trying to re-enable a
	 * cell that was disabled in the base cell causes an
	 * {@link IllegalArgumentException}.
	 * 
	 * @param cell the cell to disable/enable this cell for.
	 * @param disabled whether to disable/re-enable this cell
	 */
	public void setDisabledFor(Cell cell, boolean disabled) {
		if (disabled && !base.getDisabledFor().contains(cell))
			disabledFor.add(cell);
		if (!disabled && base.getDisabledFor().contains(cell))
			throw new IllegalArgumentException(
					"Can not re-enable a cell disabled in the base alignment.");
	}
}
