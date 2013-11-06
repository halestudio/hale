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
import eu.esdihumboldt.hale.common.instance.model.InstanceIterator;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Adds full instance iterator support to an existing instance resource
 * iterator.
 * 
 * @author Simon Templer
 */
public class FullInstanceIteratorSupport extends InstanceIteratorDecorator {

	/**
	 * Stores the next instance of type peek is not supported in the decoratee.
	 */
	private Instance peekInstance;

	/**
	 * Add full instance iterator support to a given instance resource iterator,
	 * even if it does not implement {@link InstanceIterator}.
	 * 
	 * @param decoratee the decoratee
	 */
	public FullInstanceIteratorSupport(ResourceIterator<Instance> decoratee) {
		super(decoratee);
	}

	@Override
	public boolean hasNext() {
		proceedToNext();

		if (super.supportsTypePeek()) {
			/*
			 * Type peek supported - hasNext is equivalent to decoratee
			 */
			return super.hasNext();
		}
		else {
			/*
			 * No type peek supported - has next if valid candidate was found
			 */
			return peekInstance != null;
		}
	}

	@Override
	public TypeDefinition typePeek() {
		proceedToNext();

		if (super.supportsTypePeek()) {
			/*
			 * Type peek supported - typePeek is equivalent to decoratee
			 */
			return super.typePeek();
		}
		else {
			/*
			 * No type peek supported - has next if valid candidate was found
			 */
			if (peekInstance != null) {
				return peekInstance.getDefinition();
			}
			else {
				return null;
			}
		}
	}

	@Override
	public void skip() {
		if (super.supportsTypePeek()) {
			super.skip();
		}
		else {
			doNext();
		}
	}

	private void proceedToNext() {
		if (super.supportsTypePeek()) {
			// do nothing, peek support already there
		}
		else {
			/*
			 * No type peek supported - need to peek at instance
			 */
			if (peekInstance == null) {
				if (super.hasNext()) {
					peekInstance = super.next();
				}
			}
		}
	}

	private Instance doNext() {
		proceedToNext();

		if (super.supportsTypePeek()) {
			/*
			 * Type peek supported - next is equivalent to decoratee
			 */
			return super.next();
		}
		else {
			/*
			 * No type peek supported - next is cached next instance
			 */
			Instance next = peekInstance;
			peekInstance = null;
			return next;
		}
	}

	@Override
	public Instance next() {
		return doNext();
	}

	@Override
	public boolean supportsTypePeek() {
		return true;
	}

}
