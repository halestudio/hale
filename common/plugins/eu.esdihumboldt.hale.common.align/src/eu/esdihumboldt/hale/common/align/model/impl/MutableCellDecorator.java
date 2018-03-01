/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.common.align.model.impl;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Priority;
import eu.esdihumboldt.hale.common.align.model.TransformationMode;

/**
 * Decorator for {@link MutableCell}s
 * 
 * @author Florian Esser
 */
public class MutableCellDecorator implements MutableCell {

	private final MutableCell decoratee;

	/**
	 * Create the decorator
	 * 
	 * @param decoratee <code>MutableCell</code> to decorate
	 */
	public MutableCellDecorator(MutableCell decoratee) {
		this.decoratee = decoratee;
	}

	@Override
	public void setDisabledFor(String cellId, boolean disabled) {
		decoratee.setDisabledFor(cellId, disabled);
	}

	@Override
	public void setTransformationMode(TransformationMode mode) {
		decoratee.setTransformationMode(mode);
	}

	@Override
	public void setTransformationIdentifier(String transformation) {
		decoratee.setTransformationIdentifier(transformation);
	}

	@Override
	public void setTransformationParameters(ListMultimap<String, ParameterValue> parameters) {
		decoratee.setTransformationParameters(parameters);
	}

	@Override
	public void setSource(ListMultimap<String, ? extends Entity> source) {
		decoratee.setSource(source);
	}

	@Override
	public void setTarget(ListMultimap<String, ? extends Entity> target) {
		decoratee.setTarget(target);
	}

	@Override
	public void setId(String id) {
		decoratee.setId(id);
	}

	@Override
	public void setPriority(Priority priority) {
		decoratee.setPriority(priority);
	}

	@Override
	public ListMultimap<String, ? extends Entity> getSource() {
		return decoratee.getSource();
	}

	@Override
	public ListMultimap<String, ? extends Entity> getTarget() {
		return decoratee.getTarget();
	}

	@Override
	public ListMultimap<String, ParameterValue> getTransformationParameters() {
		return decoratee.getTransformationParameters();
	}

	@Override
	public List<?> getAnnotations(String type) {
		return decoratee.getAnnotations(type);
	}

	@Override
	public Set<String> getAnnotationTypes() {
		return decoratee.getAnnotationTypes();
	}

	@Override
	public Object addAnnotation(String type) {
		return decoratee.addAnnotation(type);
	}

	@Override
	public void addAnnotation(String type, Object annotation) {
		decoratee.addAnnotation(type, annotation);
	}

	@Override
	public void removeAnnotation(String type, Object annotation) {
		decoratee.removeAnnotation(type, annotation);
	}

	@Override
	public ListMultimap<String, String> getDocumentation() {
		return decoratee.getDocumentation();
	}

	@Override
	public String getTransformationIdentifier() {
		return decoratee.getTransformationIdentifier();
	}

	@Override
	public String getId() {
		return decoratee.getId();
	}

	@Override
	public Set<String> getDisabledFor() {
		return decoratee.getDisabledFor();
	}

	@Override
	public Priority getPriority() {
		return decoratee.getPriority();
	}

	@Override
	public TransformationMode getTransformationMode() {
		return decoratee.getTransformationMode();
	}

	@Override
	public boolean isBaseCell() {
		return decoratee.isBaseCell();
	}

}
