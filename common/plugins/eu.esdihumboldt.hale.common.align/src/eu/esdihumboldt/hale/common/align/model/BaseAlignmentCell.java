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
import java.util.List;
import java.util.Set;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
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
	 * @param baseAlignment the URI of the alignment the cell is from
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
	 * Disables this cell's base cell for the given cell.
	 * 
	 * @param cell the cell to disable/enable this cell for
	 * @param disabled whether the cell should be disabled or not
	 */
	public void setBaseDisabledFor(Cell cell, boolean disabled) {
		base.setDisabledFor(cell, disabled);
	}

	@Override
	public void setDisabledFor(Cell cell, boolean disabled) {
		if (disabled && !base.getDisabledFor().contains(cell))
			disabledFor.add(cell);
		if (!disabled && base.getDisabledFor().contains(cell))
			throw new IllegalArgumentException(
					"Can not re-enable a cell disabled in the base alignment.");
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getAnnotations(java.lang.String)
	 */
	@Override
	public List<?> getAnnotations(String type) {
		return base.getAnnotations(type);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getAnnotationTypes()
	 */
	@Override
	public Set<String> getAnnotationTypes() {
		return base.getAnnotationTypes();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#addAnnotation(java.lang.String)
	 */
	@Override
	public Object addAnnotation(String type) {
		// TODO allow adding annotations?
		throw new UnsupportedOperationException(
				"Cannot add annotation to a cell of a base alignment.");
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#removeAnnotation(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void removeAnnotation(String type, Object annotation) {
		// TODO allow removing annotations?
		throw new UnsupportedOperationException(
				"Cannot remove annotation to a cell of a base alignment.");
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getDocumentation()
	 */
	@Override
	public ListMultimap<String, String> getDocumentation() {
		// TODO allow editing of documentation?
		return Multimaps.unmodifiableListMultimap(base.getDocumentation());
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getPriority()
	 */
	@Override
	public Priority getPriority() {
		return base.getPriority();
	}
}
