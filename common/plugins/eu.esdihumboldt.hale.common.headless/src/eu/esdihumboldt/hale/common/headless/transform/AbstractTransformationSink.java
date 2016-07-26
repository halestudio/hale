/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.headless.transform;

import java.util.concurrent.CopyOnWriteArrayList;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.headless.transform.validate.TransformedInstanceValidator;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Base class for transformation sinks handling validation of incoming
 * instances.
 * 
 * @author Simon Templer
 */
public abstract class AbstractTransformationSink implements TransformationSink {

	private static final ALogger log = ALoggerFactory.getLogger(AbstractTransformationSink.class);

	private final CopyOnWriteArrayList<TransformedInstanceValidator> validators = new CopyOnWriteArrayList<>();

	@Override
	public void addInstance(Instance instance) {
		for (TransformedInstanceValidator validator : validators) {
			try {
				validator.validateInstance(instance);
			} catch (Exception e) {
				log.error("Error performing validation on an instance", e);
			}
		}

		internalAddInstance(instance);
	}

	/**
	 * Adds an instance to the sink
	 * 
	 * @param instance the instance to add
	 */
	protected abstract void internalAddInstance(Instance instance);

	@Override
	public void done(boolean cancel) {
		if (!cancel) {
			for (TransformedInstanceValidator validator : validators) {
				try {
					validator.validateCompleted();
				} catch (Exception e) {
					log.error("Error completing validation", e);
				}
			}
		}

		internalDone(cancel);
	}

	/**
	 * Called if the transformation is done or cancelled. Subsequent calls to
	 * {@link #addInstance(Instance)} result in undetermined behavior.
	 * 
	 * @param cancel whether the operation was cancelled or simply finished
	 */
	protected abstract void internalDone(boolean cancel);

	@Override
	public void addValidator(TransformedInstanceValidator validator) {
		validators.add(validator);
	}

	@Override
	public void dispose() {
		// clear references to validators
		validators.clear();
	}

}
