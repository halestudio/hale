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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import eu.esdihumboldt.hale.common.align.extension.annotation.AnnotationExtension;
import eu.esdihumboldt.hale.common.align.model.AnnotationDescriptor;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Priority;
import eu.esdihumboldt.hale.common.align.model.TransformationMode;

/**
 * Default implementation of an alignment cell
 * 
 * @author Simon Templer
 */
public class DefaultCell implements Cell, MutableCell {

	private ListMultimap<String, ? extends Entity> source;
	private ListMultimap<String, ? extends Entity> target;
	private ListMultimap<String, ParameterValue> parameters;
	private String transformation;
	private String id;

	/**
	 * The cell's transformation mode, defaults to active.
	 */
	private TransformationMode mode = DEFAULT_TRANSFORMATION_MODE;

	/**
	 * The {@link Cell}'s {@link Priority}. Defaults to {@link Priority#NORMAL}.
	 */
	private Priority priority = Priority.NORMAL;
	private final Set<String> disabledFor = new HashSet<String>();
	private boolean baseCell = false;

	private final ListMultimap<String, String> documentation = ArrayListMultimap.create();
	private final ListMultimap<String, Object> annotations = ArrayListMultimap.create();

	/**
	 * Default constructor.
	 */
	public DefaultCell() {
		// do nothing
	}

	/**
	 * Copy constructor.
	 * 
	 * @param copy the cell to copy
	 */
	public DefaultCell(Cell copy) {
		for (String annotationType : copy.getAnnotationTypes())
			annotations.putAll(annotationType, copy.getAnnotations(annotationType));
		disabledFor.addAll(copy.getDisabledFor());
		documentation.putAll(copy.getDocumentation());
		id = copy.getId();
		priority = copy.getPriority();
		mode = copy.getTransformationMode();
		// since source, target and parameter maps may not be modified
		// assignment is okay
		source = copy.getSource();
		target = copy.getTarget();
		parameters = copy.getTransformationParameters();
		transformation = copy.getTransformationIdentifier();
		baseCell = copy.isBaseCell();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.MutableCell#setTransformationIdentifier(java.lang.String)
	 */
	@Override
	public void setTransformationIdentifier(String transformation) {
		this.transformation = transformation;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.MutableCell#setTransformationParameters(ListMultimap)
	 */
	@Override
	public void setTransformationParameters(ListMultimap<String, ParameterValue> parameters) {
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
	public ListMultimap<String, ParameterValue> getTransformationParameters() {
		if (parameters == null) {
			return null;
		}
		return Multimaps.unmodifiableListMultimap(parameters);
	}

	@Override
	public List<?> getAnnotations(String type) {
		return Collections.unmodifiableList(annotations.get(type));
	}

	@Override
	public Object addAnnotation(String type) {
		AnnotationDescriptor<?> descriptor = AnnotationExtension.getInstance().get(type);
		if (descriptor != null) {
			// add and return the new annotation object
			Object annotation = descriptor.create();
			annotations.put(type, annotation);
			return annotation;
		}
		return null;
	}

	/**
	 * Add an annotation object.
	 * 
	 * @param type the annotation type
	 * @param annotation the annotation object
	 */
	@Override
	public void addAnnotation(String type, Object annotation) {
		AnnotationDescriptor<?> descriptor = AnnotationExtension.getInstance().get(type);
		if (descriptor == null) {
			throw new IllegalArgumentException("Invalid annotation type");
		}

		if (!descriptor.create().getClass().equals(annotation.getClass())) {
			throw new IllegalArgumentException(
					"Provided annotation object does not match annotation type");
		}

		annotations.put(type, annotation);
	}

	@Override
	public Set<String> getAnnotationTypes() {
		return Collections.unmodifiableSet(annotations.keySet());
	}

	@Override
	public void removeAnnotation(String type, Object annotation) {
		annotations.remove(type, annotation);
	}

	@Override
	public ListMultimap<String, String> getDocumentation() {
		return documentation;
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
			return CellUtil.getCellDescription(this, null);
		} catch (Throwable e) {
			return super.toString();
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.MutableCell#setId(String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.MutableCell#setDisabledFor(eu.esdihumboldt.hale.common.align.model.Cell,
	 *      boolean)
	 */
	@Override
	public void setDisabledFor(String cellId, boolean disabled) {
		if (disabled)
			disabledFor.add(cellId);
		else
			disabledFor.remove(cellId);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getDisabledFor()
	 */
	@Override
	public Set<String> getDisabledFor() {
		return Collections.unmodifiableSet(disabledFor);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.MutableCell#setPriority(eu.esdihumboldt.hale.common.align.model.Priority)
	 */
	@Override
	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getPriority()
	 */
	@Override
	public Priority getPriority() {
		return priority;
	}

	@Override
	public void setTransformationMode(TransformationMode mode) {
		this.mode = mode;
	}

	@Override
	public TransformationMode getTransformationMode() {
		return mode;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#isBaseCell()
	 */
	@Override
	public boolean isBaseCell() {
		return baseCell;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DefaultCell)) {
			return false;
		}
		DefaultCell other = (DefaultCell) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		}
		else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
}
