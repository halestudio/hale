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

package eu.esdihumboldt.hale.common.instance.model.ext.helper;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceIterator;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Instance iterator decorator.
 * 
 * @author Simon Templer
 */
public abstract class InstanceIteratorDecorator implements InstanceIterator {

	/**
	 * The decorated resource iterator.
	 */
	protected final ResourceIterator<Instance> decoratee;

	/**
	 * Create a resource iterator decorator.
	 * 
	 * @param decoratee the resource iterator to decorated
	 */
	public InstanceIteratorDecorator(ResourceIterator<Instance> decoratee) {
		super();
		this.decoratee = decoratee;
	}

	@Override
	public boolean hasNext() {
		return decoratee.hasNext();
	}

	@Override
	public void close() {
		decoratee.close();
	}

	@Override
	public Instance next() {
		return decoratee.next();
	}

	@Override
	public void remove() {
		decoratee.remove();
	}

	@Override
	public TypeDefinition typePeek() {
		if (decoratee instanceof InstanceIterator) {
			((InstanceIterator) decoratee).typePeek();
		}
		return null;
	}

	@Override
	public boolean supportsTypePeek() {
		if (decoratee instanceof InstanceIterator) {
			((InstanceIterator) decoratee).supportsTypePeek();
		}
		return false;
	}

	@Override
	public void skip() {
		if (decoratee instanceof InstanceIterator) {
			((InstanceIterator) decoratee).skip();
		}
		else {
			decoratee.next();
		}
	}

}
