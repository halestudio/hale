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
