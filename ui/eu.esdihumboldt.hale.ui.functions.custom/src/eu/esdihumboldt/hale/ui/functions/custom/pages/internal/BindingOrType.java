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

package eu.esdihumboldt.hale.ui.functions.custom.pages.internal;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * TODO Type description
 * 
 * @author simon
 */
public class BindingOrType {

	private Class<?> binding;

	private TypeDefinition type;

	private boolean useBinding;

	/**
	 * @return the binding
	 */
	public Class<?> getBinding() {
		return binding;
	}

	/**
	 * @param binding the binding to set
	 */
	public void setBinding(Class<?> binding) {
		this.binding = binding;
	}

	/**
	 * @return the type
	 */
	public TypeDefinition getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(TypeDefinition type) {
		this.type = type;
	}

	/**
	 * @return the useBinding
	 */
	public boolean isUseBinding() {
		return useBinding;
	}

	/**
	 * @param useBinding the useBinding to set
	 */
	public void setUseBinding(boolean useBinding) {
		this.useBinding = useBinding;
	}

}
