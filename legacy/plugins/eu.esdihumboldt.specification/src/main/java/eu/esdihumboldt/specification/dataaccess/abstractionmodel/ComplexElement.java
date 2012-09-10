/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.dataaccess.abstractionmodel;

import java.util.Collection;

/**
 * This type represents a structure consisting of multiple other Elements, such
 * as multiple Primitives or Features.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface ComplexElement extends BasicElement {

	/**
	 * @return a {@link Collection} of {@link BasicElement} objects (which can
	 *         in turn be {@link ComplexElement} objects) that are a child of
	 *         this {@link ComplexElement}. By using this structure, trees
	 *         (unidirectional acyclic graphs) may be built.
	 */
	public Collection<BasicElement> getChildren();

}
