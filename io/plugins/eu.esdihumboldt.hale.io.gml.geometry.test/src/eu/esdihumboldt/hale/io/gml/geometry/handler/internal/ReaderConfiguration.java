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

package eu.esdihumboldt.hale.io.gml.geometry.handler.internal;

import java.util.function.Consumer;

import org.locationtech.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.core.io.IOProvider;

/**
 * Interpolation configuration.
 * 
 * @author Simon Templer
 */
public interface ReaderConfiguration {

	/**
	 * Apply the configuration to an instance reader.
	 * 
	 * @param instanceReader the instance reader
	 */
	void apply(IOProvider instanceReader);

	/**
	 * Geometry check for tests.
	 * 
	 * @return the geometry checker (which should throw an exception on error)
	 */
	default Consumer<Geometry> geometryChecker() {
		return (geom) -> {
			// do nothing
		};
	}

}
