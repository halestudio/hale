/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.core.io;

import java.io.Serializable;

import org.w3c.dom.Element;

import com.google.common.base.Objects;

import eu.esdihumboldt.hale.common.core.io.extension.ComplexValueExtension;
import eu.esdihumboldt.hale.common.core.io.impl.ComplexValue;
import eu.esdihumboldt.hale.common.core.io.impl.StringValue;

/**
 * A simple or complex value with either a {@link String} or DOM {@link Element}
 * representation for serializing it.<br>
 * <br>
 * A DOM representation must be linked to a corresponding complex value type
 * registered in the {@link ComplexValueExtension} to be convertible to an
 * object.
 * 
 * @author Simon Templer
 */
public abstract class Value implements Serializable {

	private static final long serialVersionUID = 1628156172357895991L;

	/**
	 * Null value.
	 */
	public static final Value NULL = new Value() {

		private static final long serialVersionUID = -1264492211623753778L;

		@Override
		public <T> T as(Class<T> expectedType) {
			return null;
		}

		@Override
		public <T> T as(Class<T> expectedType, T defValue) {
			return defValue;
		}

		@Override
		public Object getValue() {
			return null;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public boolean isRepresentedAsDOM() {
			return false;
		}

		@Override
		public Element getDOMRepresentation() {
			return null;
		}

		@Override
		public String getStringRepresentation() {
			return null;
		}

	};

	/**
	 * Create a value from a string.
	 * 
	 * @param str the string
	 * @return the value wrapper
	 */
	public static Value of(String str) {
		return new StringValue(str);
	}

	/**
	 * Create a value from a boolean.
	 * 
	 * @param bool the boolean value
	 * @return the value wrapper
	 */
	public static Value of(Boolean bool) {
		return new StringValue(bool);
	}

	/**
	 * Create a value from a number.
	 * 
	 * @param number the number value
	 * @return the value wrapper
	 */
	public static Value of(Number number) {
		return new StringValue(number);
	}

	/**
	 * Create a simple value with a string representation for serialization.
	 * 
	 * @param value the value
	 * @return the value wrapper
	 */
	public static Value simple(Object value) {
		return new StringValue(value);
	}

	/**
	 * Create a complex value with a DOM representation for serialization.
	 * 
	 * @param value the value
	 * @return the value wrapper
	 */
	public static Value complex(Object value) {
		return new ComplexValue(value);
	}

	/**
	 * Get the value as the expected type if possible.
	 * 
	 * @param expectedType the expected value type, this must be either
	 *            {@link String}, DOM {@link Element} or a complex value type
	 *            defined in the {@link ComplexValueExtension}
	 * @return the value as the expected type or <code>null</code> if it could
	 *         not be created/converted
	 */
	public abstract <T> T as(Class<T> expectedType);

	/**
	 * Get the value as the expected type if possible, a default value
	 * otherwise.
	 * 
	 * @param expectedType the expected value type, this must be either
	 *            {@link String}, DOM {@link Element} or a complex value type
	 *            defined in the {@link ComplexValueExtension}
	 * @param defValue the default value to use if the value is
	 *            <code>null</code> or cannot be converted to the expected type
	 * @return the value as the expected type or the given default value if it
	 *         could not be created/converted
	 */
	public abstract <T> T as(Class<T> expectedType, T defValue);

	/**
	 * Get the internal value.<br>
	 * <br>
	 * In most cases it is more appropriate to use {@link #getValue()} instead.
	 * 
	 * @return the internal value
	 */
	public abstract Object getValue();

	/**
	 * Determines if the value is empty.
	 * 
	 * @return <code>true</code> if the value is <code>null</code> or another
	 *         kind of empty (e.g. empty string) depending on the value type,
	 *         <code>false</code> otherwise
	 */
	public abstract boolean isEmpty();

	/**
	 * Determines if the value is represented as DOM {@link Element} for
	 * serializing it.
	 * 
	 * @return if the value can be represented as {@link Element}
	 * @see #getDOMRepresentation()
	 * @see #getStringRepresentation()
	 */
	public abstract boolean isRepresentedAsDOM();

	/**
	 * Get the value's DOM representation if applicable.
	 * 
	 * @return an {@link Element} representing the value or <code>null</code> if
	 *         {@link #isRepresentedAsDOM()} yields <code>false</code>
	 * @see #isRepresentedAsDOM()
	 */
	public abstract Element getDOMRepresentation();

	/**
	 * Get the value's string representation.
	 * 
	 * @return a string representing the value or <code>null</code> if
	 *         {@link #isRepresentedAsDOM()} yields <code>true</code>
	 * @see #isRepresentedAsDOM()
	 */
	public abstract String getStringRepresentation();

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Value))
			return false;
		Value other = (Value) obj;
		if (!other.isRepresentedAsDOM() && !isRepresentedAsDOM()) {
			/*
			 * Both represented as String, use string representation to compare
			 */
			return Objects.equal(other.getStringRepresentation(), getStringRepresentation());
		}

		// in any other case, compare the internal values
		return Objects.equal(other.getValue(), getValue());
	}

	@Override
	public int hashCode() {
		if (!isRepresentedAsDOM()) {
			/*
			 * If represented through a string, use the string hash code.
			 */
			return Objects.hashCode(getStringRepresentation());
		}

		// in any other case, use the internal value hash code
		return Objects.hashCode(getValue());
	}

	@Override
	public String toString() {
		String def = "Value<null>";
		Object value = getValue();
		if (value != null) {
			def = value.toString();
		}
		return as(String.class, def);
	}

}
