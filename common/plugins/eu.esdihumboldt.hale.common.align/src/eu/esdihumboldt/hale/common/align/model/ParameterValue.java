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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.model;

import net.jcip.annotations.Immutable;

import org.w3c.dom.Element;

import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Class to represent the value of a transformation parameter.
 * 
 * @author Kai Schwierczek
 */
@Immutable
public class ParameterValue extends Value {

	private static final long serialVersionUID = -6828899280515228306L;

	/**
	 * The <code>null</code> parameter value.
	 */
	public static final ParameterValue NULL = new ParameterValue(Value.NULL);

	/**
	 * The name of the default type of value. <code>null</code> represents also
	 * the default type.
	 */
	public static final String DEFAULT_TYPE = "default";

	private final String type;
	private final Value value;

	/**
	 * Constructor specifying the type and the value.
	 * 
	 * @param type the type of the value
	 * @param value the value
	 */
	public ParameterValue(String type, Value value) {
		if (value == null) {
			value = Value.NULL;
		}

		this.type = type;
		this.value = value;
	}

	/**
	 * Constructor specifying the value only. Type will be the default.
	 * 
	 * @param value the value
	 */
	public ParameterValue(Value value) {
		this(DEFAULT_TYPE, value);
	}

	/**
	 * Create a simple string value. Type will be the default.
	 * 
	 * @param value the value
	 */
	public ParameterValue(String value) {
		this(DEFAULT_TYPE, Value.of(value));
	}

	/**
	 * Determines if the parameter needs further processing to be used, i.e. the
	 * parameter type is neither {@link #DEFAULT_TYPE} nor <code>null</code>.
	 * 
	 * @return if the parameter needs processing
	 */
	public boolean needsProcessing() {
		return type != null && !DEFAULT_TYPE.equals(type);
	}

	/**
	 * Returns the type of the value. Either {@value #DEFAULT_TYPE},
	 * <code>null</code> or a script id.
	 * 
	 * @return the associated parameter type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns the value.
	 * 
	 * @return the value
	 */
	@Override
	public Object getValue() {
		return value.getValue();
	}

	@Override
	public <T> T as(Class<T> expectedType) {
		return value.as(expectedType);
	}

	@Override
	public <T> T as(Class<T> expectedType, T defValue) {
		return value.as(expectedType, defValue);
	}

	@Override
	public boolean isEmpty() {
		return value.isEmpty();
	}

	@Override
	public boolean isRepresentedAsDOM() {
		return value.isRepresentedAsDOM();
	}

	@Override
	public Element getDOMRepresentation() {
		return value.getDOMRepresentation();
	}

	@Override
	public String getStringRepresentation() {
		return value.getStringRepresentation();
	}

	/**
	 * Get the internal value.
	 * 
	 * @return the internal value
	 */
	public Value intern() {
		return value;
	}

}
