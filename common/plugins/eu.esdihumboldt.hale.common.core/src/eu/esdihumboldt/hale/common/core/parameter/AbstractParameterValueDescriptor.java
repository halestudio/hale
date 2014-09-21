/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.core.parameter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Base class for simple parameter value descriptors.
 * 
 * @author Simon Templer
 */
public abstract class AbstractParameterValueDescriptor implements ParameterValueDescriptor {

	private final Value defaultValue;
	private final Value sampleValue;

	/**
	 * Create with a default and sample value.
	 * 
	 * @param defaultValue the default value
	 * @param sampleValue the sample value
	 */
	public AbstractParameterValueDescriptor(@Nullable Value defaultValue,
			@Nullable Value sampleValue) {
		this.defaultValue = defaultValue;
		this.sampleValue = sampleValue;
	}

	/**
	 * Create with a default value.
	 * 
	 * @param defaultValue the default value
	 */
	public AbstractParameterValueDescriptor(@Nonnull Value defaultValue) {
		this(defaultValue, defaultValue);
	}

	@Override
	public String getDocumentationRepresentation() {
		// override me
		return null;
	}

	@Override
	public Value getDefaultValue() {
		return defaultValue;
	}

	@Override
	public Value getSampleData() {
		return sampleValue;
	}

}
