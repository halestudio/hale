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

package eu.esdihumboldt.hale.common.align.custom;

import java.util.HashSet;
import java.util.Set;

import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Default implementation of a custom function parameter.
 * 
 * @author Simon Templer
 */
public class DefaultCustomPropertyFunctionParameter extends MinimalParameter {

	private Class<?> bindingClass;
	private Set<String> enumeration;
	private Value defaultValue;

	/**
	 * Default constructor.
	 */
	public DefaultCustomPropertyFunctionParameter() {
		super();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param other the object to copy
	 */
	public DefaultCustomPropertyFunctionParameter(DefaultCustomPropertyFunctionParameter other) {
		super(other);
		setBindingClass(other.getBindingClass());
		if (other.getEnumeration() != null) {
			setEnumeration(new HashSet<>(other.getEnumeration()));
		}
		else {
			setEnumeration(null);
		}
		setDefaultValue(other.getDefaultValue());
	}

	/**
	 * @return the bindingClass
	 */
	public Class<?> getBindingClass() {
		return bindingClass;
	}

	/**
	 * @param bindingClass the bindingClass to set
	 */
	public void setBindingClass(Class<?> bindingClass) {
		this.bindingClass = bindingClass;
	}

	/**
	 * @return the enumeration
	 */
	public Set<String> getEnumeration() {
		return enumeration;
	}

	/**
	 * @param enumeration the enumeration to set
	 */
	public void setEnumeration(Set<String> enumeration) {
		this.enumeration = enumeration;
	}

	/**
	 * @return the defaultValue
	 */
	public Value getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(Value defaultValue) {
		this.defaultValue = defaultValue;
	}

}
