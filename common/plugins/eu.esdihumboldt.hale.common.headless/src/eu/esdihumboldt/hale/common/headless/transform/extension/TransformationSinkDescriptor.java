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

package eu.esdihumboldt.hale.common.headless.transform.extension;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.common.headless.transform.TransformationSink;

/**
 * Transformation sink descriptor.
 * 
 * @author Simon Templer
 */
public interface TransformationSinkDescriptor extends ExtensionObjectFactory<TransformationSink> {

	/**
	 * States of the sink's provided instance collection is reiterable.
	 * 
	 * @return if the sink is reiterable
	 */
	public boolean isReiterable();

}
