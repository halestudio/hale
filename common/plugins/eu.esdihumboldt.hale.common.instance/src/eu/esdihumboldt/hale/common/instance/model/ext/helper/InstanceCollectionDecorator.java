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

package eu.esdihumboldt.hale.common.instance.model.ext.helper;

import java.util.Map;

import eu.esdihumboldt.hale.common.core.report.LogAware;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceCollection2;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Decorator for an instance collection.
 * 
 * @author Simon Templer
 */
public abstract class InstanceCollectionDecorator implements InstanceCollection2, LogAware {

	/**
	 * The decorated instance collection.
	 */
	protected final InstanceCollection decoratee;

	/**
	 * Create an instance collection decorator.
	 * 
	 * @param decoratee the instance collection to perform the selection on
	 */
	public InstanceCollectionDecorator(InstanceCollection decoratee) {
		super();
		this.decoratee = decoratee;
	}

	@Override
	public ResourceIterator<Instance> iterator() {
		return decoratee.iterator();
	}

	@Override
	public boolean hasSize() {
		return decoratee.hasSize();
	}

	@Override
	public int size() {
		return decoratee.size();
	}

	@Override
	public boolean isEmpty() {
		return decoratee.isEmpty();
	}

	@Override
	public InstanceCollection select(Filter filter) {
		return decoratee.select(filter);
	}

	@Override
	public InstanceReference getReference(Instance instance) {
		return decoratee.getReference(instance);
	}

	@Override
	public Instance getInstance(InstanceReference reference) {
		return decoratee.getInstance(reference);
	}

	@Override
	public boolean supportsFanout() {
		return decoratee instanceof InstanceCollection2
				&& ((InstanceCollection2) decoratee).supportsFanout();
	}

	@Override
	public Map<TypeDefinition, InstanceCollection> fanout() {
		if (decoratee instanceof InstanceCollection2) {
			return ((InstanceCollection2) decoratee).fanout();
		}
		return null;
	}

	@Override
	public void setLog(SimpleLog log) {
		if (decoratee instanceof LogAware) {
			((LogAware) decoratee).setLog(log);
		}
	}

}
