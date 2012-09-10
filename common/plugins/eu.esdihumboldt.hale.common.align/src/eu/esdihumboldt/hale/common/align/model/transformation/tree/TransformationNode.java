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
