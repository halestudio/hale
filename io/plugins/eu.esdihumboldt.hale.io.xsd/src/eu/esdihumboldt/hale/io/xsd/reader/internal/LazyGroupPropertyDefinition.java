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
