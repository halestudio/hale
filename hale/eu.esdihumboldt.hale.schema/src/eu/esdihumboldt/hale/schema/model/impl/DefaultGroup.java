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

package eu.esdihumboldt.hale.schema.model.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.schema.model.Group;

/**
 * Default {@link Group} implementation used internally in 
 * {@link DefaultTypeDefinition} and XXX
 * @author Simon Templer
 */
public class DefaultGroup implements Group {
	
	/**
	 * The list of declared children (qualified name mapped to child definition, LinkedHashMap because order must be maintained for writing)
	 */
	private final LinkedHashMap<QName, ChildDefinition<?>> declaredChildren = new LinkedHashMap<QName, ChildDefinition<?>>();

	/**
	 * @see Group#getDeclaredChildren()
	 */
	@Override
	public Collection<? extends ChildDefinition<?>> getDeclaredChildren() {
		return Collections.unmodifiableCollection(declaredChildren.values());
	}

	/**
	 * @see Group#getChild(QName)
	 */
	@Override
	public ChildDefinition<?> getChild(QName name) {
		return declaredChildren.get(name);
	}

	/**
	 * @see Group#addChild(ChildDefinition)
	 */
	@Override
	public void addChild(ChildDefinition<?> child) {
		declaredChildren.put(child.getName(), child);
	}

}
