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

package eu.esdihumboldt.hale.common.align.model.transformation.tree.impl;

import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNode;

/**
 * Abstract transformation node implementation.
 * 
 * @author Simon Templer
 */
public abstract class AbstractTransformationNode implements TransformationNode {

	private Map<String, Object> annotations;

	/**
	 * @see TransformationNode#reset()
	 */
	@Override
	public void reset() {
		if (annotations != null) {
			annotations.clear();
		}
	}

	/**
	 * @see TransformationNode#getAnnotation(String)
	 */
	@Override
	public Object getAnnotation(String name) {
		if (annotations == null) {
			return null;
		}
		return annotations.get(name);
	}

	/**
	 * @see TransformationNode#setAnnotation(String, Object)
	 */
	@Override
	public void setAnnotation(String name, Object annotation) {
		if (annotations == null) {
			annotations = new HashMap<String, Object>();
		}
		annotations.put(name, annotation);
	}

	/**
	 * @see TransformationNode#hasAnnotations()
	 */
	@Override
	public boolean hasAnnotations() {
		return annotations != null && !annotations.isEmpty();
	}

}
