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

package eu.esdihumboldt.hale.io.xsd.reader.internal;

import java.util.Collection;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultGroupPropertyDefinition;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;

/**
 * Lazy property group definition
 * 
 * @author Simon Templer
 */
public abstract class LazyGroupPropertyDefinition extends DefaultGroupPropertyDefinition {

	/**
	 * The XML index that can be used to resolve needed objects
	 */
	protected final XmlIndex index;

	/**
	 * If the definition is yet initialized
	 */
	private boolean initialized = false;

	/**
	 * Create a lazy group property definition
	 * 
	 * @param name the group name
	 * @param parentGroup the parent group
	 * @param index the XML index
	 * @param allowFlatten if the group may be replaced by its children
	 */
	public LazyGroupPropertyDefinition(QName name, DefinitionGroup parentGroup, XmlIndex index,
			boolean allowFlatten) {
		super(name, parentGroup, allowFlatten);

		this.index = index;
	}

	/**
	 * Initialize
	 */
	private void init() {
		if (!initialized) {
			initChildren();
			initialized = true;
		}
	}

	/**
	 * Initialize the children. {@link #addChild(ChildDefinition)} can be used
	 * to add them.
	 */
	protected abstract void initChildren();

	/**
	 * @see DefaultGroupPropertyDefinition#getDeclaredChildren()
	 */
	@Override
	public Collection<? extends ChildDefinition<?>> getDeclaredChildren() {
		init();
		return super.getDeclaredChildren();
	}

	/**
	 * @see DefaultGroupPropertyDefinition#getChild(QName)
	 */
	@Override
	public ChildDefinition<?> getChild(QName name) {
		init();
		return super.getChild(name);
	}

}
