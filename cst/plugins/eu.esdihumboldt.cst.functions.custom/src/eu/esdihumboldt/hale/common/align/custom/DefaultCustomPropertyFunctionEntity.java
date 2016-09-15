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

import java.util.Collections;
import java.util.List;

import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomPropertyFunctionEntity;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.condition.PropertyCondition;
import eu.esdihumboldt.hale.common.align.model.condition.PropertyTypeCondition;
import eu.esdihumboldt.hale.common.align.model.condition.TypeCondition;
import eu.esdihumboldt.hale.common.align.model.condition.impl.BindingCondition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Default implementation of a property entity for a custom function.
 * 
 * @author Simon Templer
 */
public class DefaultCustomPropertyFunctionEntity extends MinimalParameter
		implements CustomPropertyFunctionEntity {

	private boolean eager;
	private Class<?> bindingClass;
	private TypeDefinition bindingType;

	/**
	 * Default constructor.
	 */
	public DefaultCustomPropertyFunctionEntity() {
		super();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param other the entity to copy
	 */
	public DefaultCustomPropertyFunctionEntity(DefaultCustomPropertyFunctionEntity other) {
		super(other);
		setEager(other.isEager());
		setBindingClass(other.getBindingClass());
		setBindingType(other.getBindingType());
	}

	/**
	 * @param eager the eager to set
	 */
	public void setEager(boolean eager) {
		this.eager = eager;
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
	 * @return the bindingType
	 */
	public TypeDefinition getBindingType() {
		return bindingType;
	}

	/**
	 * @param bindingType the bindingType to set
	 */
	public void setBindingType(TypeDefinition bindingType) {
		this.bindingType = bindingType;
	}

	@Override
	public List<PropertyCondition> getConditions() {
		if (bindingType != null) {
			TypeCondition typeCondition = new TypeCondition() {

				@Override
				public boolean accept(Type entity) {
					return entity.getDefinition().getDefinition().equals(bindingType);
				}
			};
			return Collections
					.<PropertyCondition> singletonList(new PropertyTypeCondition(typeCondition));
		}
		else if (bindingClass != null) {
			TypeCondition typeCondition = new BindingCondition(bindingClass, true, false);
			return Collections
					.<PropertyCondition> singletonList(new PropertyTypeCondition(typeCondition));
		}
		else
			return Collections.emptyList();
	}

	@Override
	public boolean isEager() {
		return eager;
	}

}
