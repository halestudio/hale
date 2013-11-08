/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.instance.model.impl;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.ext.helper.FullInstanceIteratorSupport;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Iterator that only returns instances of a specific type.
 * 
 * @author Simon Templer
 */
public class TypeFilteredIterator extends FullInstanceIteratorSupport {

	private final TypeDefinition type;

	/**
	 * Create an iterator that only returns instances of a specific type.
	 * 
	 * @param it the iterator to wrap, if the iterator supports type peeking,
	 *            the type filtered iterator will make use of it
	 * @param type the type that instances have to match
	 */
	public TypeFilteredIterator(ResourceIterator<Instance> it, TypeDefinition type) {
		super(it);

		this.type = type;
	}

	@Override
	public boolean hasNext() {
		proceedToNext();
		return super.hasNext();
	}

	private void proceedToNext() {
		// proceed to next instance with matching type
		while (super.hasNext() && !type.equals(super.typePeek())) {
			super.skip();
		}
	}

	@Override
	public void skip() {
		proceedToNext();
		super.skip();
	}

	@Override
	public Instance next() {
		proceedToNext();
		return super.next();
	}

}
