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

package eu.esdihumboldt.hale.common.align.transformation.function.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.impl.IdentifiableInstanceDecorator;

/**
 * Decorate a given instance with family function.
 * 
 * @author Kai Schwierczek
 */
public class FamilyInstanceImpl extends IdentifiableInstanceDecorator implements FamilyInstance {

	private final Collection<FamilyInstance> children;

	/**
	 * Decorate the given instance with family function.
	 * 
	 * @param instance the instance to decorate
	 */
	public FamilyInstanceImpl(Instance instance) {
		super(instance);
		children = new ArrayList<FamilyInstance>();
	}

	/**
	 * @see FamilyInstance#getChildren()
	 */
	@Override
	public Collection<FamilyInstance> getChildren() {
		return Collections.unmodifiableCollection(children);
	}

	/**
	 * 
	 * @see FamilyInstance#addChild(FamilyInstance)
	 */
	@Override
	public void addChild(FamilyInstance child) {
		children.add(child);
	}
}
