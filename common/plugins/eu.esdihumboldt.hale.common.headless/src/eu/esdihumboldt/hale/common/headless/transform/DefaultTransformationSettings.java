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
 * Transformation settings default implementation.
 * 
 * @author Simon Templer
 */
public class DefaultTransformationSettings implements TransformationSettings {

	private final Optional<Boolean> useTemporaryDatabase;

	/**
	 * Create with custom settings.
	 * 
	 * @param useTemporaryDatabase if the temporary database should be used
	 */
	public DefaultTransformationSettings(Optional<Boolean> useTemporaryDatabase) {
		super();
		this.useTemporaryDatabase = useTemporaryDatabase;
	}

	/**
	 * Create with default settings.
	 */
	public DefaultTransformationSettings() {
		this(Optional.empty());
	}

	@Override
	public Optional<Boolean> useTemporaryDatabase() {
		return useTemporaryDatabase;
	}

}
