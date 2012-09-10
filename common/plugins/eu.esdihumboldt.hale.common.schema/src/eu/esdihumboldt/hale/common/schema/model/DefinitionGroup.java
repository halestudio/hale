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

package eu.esdihumboldt.hale.common.schema.model;

import java.util.Collection;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.impl.AbstractChildDefinition;

/**
 * A group of children, which may be properties or property groups
 * 
 * @author Simon Templer
 */
public interface DefinitionGroup {

	/**
	 * Get the properties and property groups declared by the type
	 * 
	 * @return the definitions of the declared properties and groups
	 */
	public Collection<? extends ChildDefinition<?>> getDeclaredChildren();

	/**
	 * Get the child with the given name
	 * 
	 * @param name the child name
	 * 
	 * @return the child definition or <code>null</code> if no child with the
	 *         given name is available
	 */
	public ChildDefinition<?> getChild(QName name);

	/**
	 * Add a declared child, this is called by the
	 * {@link AbstractChildDefinition} constructor.
	 * 
	 * @param child the child definition
	 */
	public void addChild(ChildDefinition<?> child);

	/**
	 * Get the group identifier
	 * 
	 * @return the unique name of the group
	 */
	public String getIdentifier();

}