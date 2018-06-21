/*
 * Copyright (c) 2018 wetransform GmbH
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

import java.util.Optional;

/**
 * 
 * 
 * @author Simon Templer
 */
public interface TransformationSettings {

	/**
	 * If present, states if the temporary database should be used. If not
	 * present, default behavior should be used.
	 * 
	 * @return if the temporary database should be used
	 */
	Optional<Boolean> useTemporaryDatabase();

}
