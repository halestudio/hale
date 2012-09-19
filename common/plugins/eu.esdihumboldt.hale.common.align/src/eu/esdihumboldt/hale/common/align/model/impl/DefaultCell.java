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

package eu.esdihumboldt.hale.common.align.model.impl;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.MutableCell;

/**
 * Default implementation of an alignment cell
 * 
 * @author Simon Templer
 */
public class DefaultCell implements Cell, MutableCell {

	private ListMultimap<String, ? extends Entity> source;
	private ListMultimap<String, ? extends Entity> target;
	private ListMultimap<String, String> parameters;
	private String transformation;

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.MutableCell#setTransformationIdentifier(java.lang.String)
	 */
	@Override
	public void setTransformationIdentifier(String transformation) {
		this.transformation = transformation;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.MutableCell#setTransformationParameters(com.google.common.collect.ListMultimap)
	 */
	@Override
	public void setTransformationParameters(ListMultimap<String, String> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.MutableCell#setSource(com.google.common.collect.ListMultimap)
	 */
	@Override
	public void setSource(ListMultimap<String, ? extends Entity> source) {
		this.source = source;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.MutableCell#setTarget(com.google.common.collect.ListMultimap)
	 */
	@Override
	public void setTarget(ListMultimap<String, ? extends Entity> target) {
		this.target = target;
	}

	/**
	 * @see Cell#getSource()
	 */
	@Override
	public ListMultimap<String, ? extends Entity> getSource() {
		if (source == null) {
			return null;
		}
		return Multimaps.unmodifiableListMultimap(source);
	}

	/**
	 * @see Cell#getTarget()
	 */
	@Override
	public ListMultimap<String, ? extends Entity> getTarget() {
		if (target == null) {
			return null;
		}
		return Multimaps.unmodifiableListMultimap(target);
	}

	/**
	 * @see Cell#getTransformationParameters()
	 */
	@Override
	public ListMultimap<String, String> getTransformationParameters() {
		if (parameters == null) {
			return null;
		}
		return Multimaps.unmodifiableListMultimap(parameters);
	}

	/**
	 * @see Cell#getTransformationIdentifier()
	 */
	@Override
	public String getTransformationIdentifier() {
		return transformation;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		try {
			return CellUtil.getCellDescription(this);
		} catch (Throwable e) {
			return super.toString();
		}
	}

}
