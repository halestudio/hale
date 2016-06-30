/*
 * Copyright (c) 2016 Data Harmonisation Panel
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
 *     wetransform GmbH
 */

package eu.esdihumboldt.hale.common.align.transformation.function.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.impl.SoftInstanceDelegate;

/**
 * Family instance delegating to a soft reference that is refreshed via an
 * InstanceReference.
 * 
 * @author Simon Templer
 */
public class FamilyInstanceDelegate extends SoftInstanceDelegate implements FamilyInstance {

	private final Collection<FamilyInstance> children;

	/**
	 * Constructs the delegating instance.
	 * 
	 * @param ref the instance reference
	 * @param collection the instance collection to resolved the reference from
	 */
	public FamilyInstanceDelegate(InstanceReference ref, InstanceCollection collection) {
		super(ref, collection);
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
