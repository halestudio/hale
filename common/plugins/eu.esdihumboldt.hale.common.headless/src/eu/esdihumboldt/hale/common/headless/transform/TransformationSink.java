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

package eu.esdihumboldt.hale.common.headless.transform;

import eu.esdihumboldt.hale.common.align.transformation.service.InstanceSink;
import eu.esdihumboldt.hale.common.headless.transform.validate.TransformedInstanceValidator;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Instance sink that provides an instance collection of added instances for
 * further processing, e.g. by an {@link InstanceWriter}.
 * 
 * @author Simon Templer
 */
public interface TransformationSink extends InstanceSink {

	/**
	 * Set the schema/types of the transformed data set.
	 * 
	 * @param types the transformed data set types
	 */
	public void setTypes(TypeIndex types);

	/**
	 * Called if the transformation is done or cancelled. Subsequent calls to
	 * {@link #addInstance(Instance)} result in undetermined behavior.
	 * 
	 * @param cancel whether the operation was cancelled or simply finished
	 */
	public void done(boolean cancel);

	/**
	 * Returns the associated instance collection, whose iterator will receive
	 * the instances that are added to the instance sink.
	 * 
	 * @return the instance collection for this sink
	 */
	public InstanceCollection getInstanceCollection();

	/**
	 * Dispose the transformation sink when it is no longer needed.
	 */
	public void dispose();

	/**
	 * Add a validator to the sink. Should be done before the first instance has
	 * been passed in.
	 * 
	 * @param validator a validator for transformed instances
	 */
	public void addValidator(TransformedInstanceValidator validator);

}
