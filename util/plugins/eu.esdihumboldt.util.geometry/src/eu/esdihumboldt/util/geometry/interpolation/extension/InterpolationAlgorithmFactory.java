/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.util.geometry.interpolation.extension;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.util.geometry.interpolation.InterpolationAlgorithm;

/**
 * Interface for {@link InterpolationAlgorithm} factory.
 * 
 * @author Simon Templer
 */
public interface InterpolationAlgorithmFactory
		extends ExtensionObjectFactory<InterpolationAlgorithm> {

	// statically typed interface

}
