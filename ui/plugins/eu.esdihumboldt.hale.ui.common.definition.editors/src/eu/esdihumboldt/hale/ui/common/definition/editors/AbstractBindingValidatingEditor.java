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

package eu.esdihumboldt.hale.ui.common.definition.editors;

import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.ui.common.editors.AbstractEditor;

/**
 * Abstract editor that is based on a binding and a validator. Expects the input
 * to happen with strings.
 * 
 * @author Kai Schwierczek
 * @param <T> the attribute value type/binding
 */
public abstract class AbstractBindingValidatingEditor<T> extends AbstractEditor<T> {

	private final ConversionService cs = OsgiUtils.getService(ConversionService.class);
	private final Class<? extends T> binding;
	private String stringValue;
	private T objectValue;
	private boolean validated = false;
	private String validationResult;

	/**
	 * Constructor with the binding class.
	 * 
	 * @param binding the binding class
	 */
	public AbstractBindingValidatingEditor(Class<? extends T> binding) {
		this.binding = binding;
	}

	/**
	 * Updates the local value, valid status and fires necessary events.
	 * 
	 * @param newValue the new value
	 * @return the validation result string, <code>null</code> if everything was
	 *         okay, otherwise the error text
	 */
	protected String valueChanged(String newValue) {
		// get old status
		boolean oldValid = isValid();
		T oldObjectValue = objectValue;

		// set new value and validate
		stringValue = newValue;
		validate();
		boolean newValid = isValid();

		// fire events
		// XXX currently fire events with object value
		fireValueChanged(VALUE, oldObjectValue, objectValue);
		if (oldValid != isValid())
			fireStateChanged(IS_VALID, oldValid, newValid);

		return validationResult;
	}

	/**
	 * Validates the current string value and sets validationResult.<br>
	 * Also sets object result if possible and updates the ControlDecoration.
	 */
	private void validate() {
		validationResult = null;
		validated = true;

		// check binding first
		try {
			// for example boolean converter returns null for empty string...
			objectValue = cs.convert(stringValue, binding);
			if (objectValue == null)
				validationResult = stringValue + " cannot be converted to "
						+ binding.getSimpleName();
		} catch (ConversionException ce) {
			objectValue = null;
			validationResult = stringValue + " cannot be converted to " + binding.getSimpleName();
		}

		// validators
		if (validationResult == null)
			validationResult = additionalValidate(stringValue, objectValue);
	}

	/**
	 * Validates the given value. The returned string should be
	 * <code>null</code> if the input validates, it should contain an error
	 * message otherwise. <br>
	 * The default implementation always returns <code>null</code>.
	 * 
	 * @param stringValue the string value
	 * @param objectValue the according to the binding converted string value
	 * @return <code>null</code> or an error message
	 */
	protected String additionalValidate(String stringValue, T objectValue) {
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(T value) {
		setAsText(cs.convert(value, String.class));
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#getValue()
	 * 
	 * @throws IllegalStateException if the current input is not valid
	 */
	@Override
	public T getValue() {
		if (isValid())
			return objectValue;
		else
			throw new IllegalStateException();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#getAsText()
	 */
	@Override
	public String getAsText() {
		if (isValid()) {
			// return converted value, as that SHOULD be XML conform
			// in contrast to input value where the converter maybe allows more.
			return cs.convert(objectValue, String.class);
		}
		else
			return stringValue;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#isValid()
	 */
	@Override
	public boolean isValid() {
		if (!validated)
			validate();
		return validationResult == null;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#getValueType()
	 */
	@Override
	public String getValueType() {
		return ParameterValue.DEFAULT_TYPE;
	}
}
