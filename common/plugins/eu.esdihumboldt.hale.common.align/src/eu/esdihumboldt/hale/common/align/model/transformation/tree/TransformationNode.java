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

package eu.esdihumboldt.hale.common.align.model.transformation.tree;

/**
 * Interface for nodes that temporarily store transformation relevant data
 * regarding specific source instances as annotations.
 * 
 * @author Simon Templer
 */
public interface TransformationNode {

	/**
	 * Clear all temporary transformation information in the node, i.e all
	 * annotations.
	 */
	public void reset();

	/**
	 * Determines if the node has any annotations.
	 * 
	 * @return if the node has annotations
	 */
	public boolean hasAnnotations();

	/**
	 * Get the annotation with the given name.
	 * 
	 * @param name the annotation name
	 * @return the annotation or <code>null</code> if there is none
	 */
	public Object getAnnotation(String name);

	/**
	 * Set an annotation.
	 * 
	 * @param name the annotation name
	 * @param annotation the annotation value
	 */
	public void setAnnotation(String name, Object annotation);

	/**
	 * Accept a transformation node visitor.
	 * 
	 * @param visitor the visitor
	 */
	public void accept(TransformationNodeVisitor visitor);

}
