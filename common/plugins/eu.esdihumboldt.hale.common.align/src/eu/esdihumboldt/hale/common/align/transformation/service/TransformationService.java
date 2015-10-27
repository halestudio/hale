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

package eu.esdihumboldt.hale.common.align.transformation.service;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReport;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;

/**
 * Transformation service
 * 
 * @author Simon Templer
 * @since 2.5
 */
public interface TransformationService {

	/**
	 * Transform a set of source instances according to the given alignment.
	 * 
	 * @param alignment the alignment
	 * @param source the source instances
	 * @param target the transformed instance sink, must be thread safe
	 * @param serviceProvider provider for services that can be accessed by
	 *            transformation functions in context of the transformation
	 * @param progressIndicator the progress indicator
	 * @return the transformation report
	 */
	public TransformationReport transform(Alignment alignment, InstanceCollection source,
			InstanceSink target, ServiceProvider serviceProvider,
			ProgressIndicator progressIndicator);

	/**
	 * States if the execution of the transformation is cancelable.
	 * 
	 * @return if the transformation can be canceled
	 */
	public boolean isCancelable();

}
