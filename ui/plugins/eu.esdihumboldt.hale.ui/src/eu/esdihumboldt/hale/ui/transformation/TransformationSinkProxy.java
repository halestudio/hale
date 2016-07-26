/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.transformation;

import eu.esdihumboldt.hale.common.headless.transform.TransformationSink;
import eu.esdihumboldt.hale.common.headless.transform.extension.TransformationSinkExtension;
import eu.esdihumboldt.hale.common.headless.transform.validate.TransformedInstanceValidator;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Transformation sink proxy that must be initialized using
 * {@link #init(boolean)}. The reference to the instance collection is available
 * even before the initialization.
 * 
 * @author Simon Templer
 */
public class TransformationSinkProxy implements TransformationSink {

	/**
	 * The internal instance sink that is proxied.
	 */
	protected TransformationSink sink;

	private final InstanceCollection instanceCollection = new InstanceCollection() {

		@Override
		public InstanceReference getReference(Instance instance) {
			return sink.getInstanceCollection().getReference(instance);
		}

		@Override
		public Instance getInstance(InstanceReference reference) {
			return sink.getInstanceCollection().getInstance(reference);
		}

		@Override
		public int size() {
			if (sink == null) {
				return UNKNOWN_SIZE;
			}
			return sink.getInstanceCollection().size();
		}

		@Override
		public InstanceCollection select(Filter filter) {
			return sink.getInstanceCollection().select(filter);
		}

		@Override
		public ResourceIterator<Instance> iterator() {
			return sink.getInstanceCollection().iterator();
		}

		@Override
		public boolean isEmpty() {
			if (sink == null)
				return false;
			return sink.getInstanceCollection().isEmpty();
		}

		@Override
		public boolean hasSize() {
			if (sink == null) {
				return false;
			}
			return sink.getInstanceCollection().hasSize();
		}
	};

	/**
	 * Initialize the proxy with a sink obtained from the extension point.
	 * 
	 * @param reiterable if the sink's instance collection should be reiterable
	 * @throws Exception if creating the sink fails
	 */
	public void init(boolean reiterable) throws Exception {
		sink = TransformationSinkExtension.getInstance().createSink(reiterable);
	}

	@Override
	public void addInstance(Instance instance) {
		sink.addInstance(instance);
	}

	@Override
	public void setTypes(TypeIndex types) {
		sink.setTypes(types);
	}

	@Override
	public void done(boolean cancel) {
		sink.done(cancel);
	}

	@Override
	public InstanceCollection getInstanceCollection() {
		return instanceCollection;
	}

	@Override
	public void dispose() {
		sink.dispose();
	}

	@Override
	public void addValidator(TransformedInstanceValidator validator) {
		sink.addValidator(validator);
	}

}
