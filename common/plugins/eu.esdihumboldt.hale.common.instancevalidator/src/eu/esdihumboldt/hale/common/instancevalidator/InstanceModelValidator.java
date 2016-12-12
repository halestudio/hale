/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.common.instancevalidator;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.extension.validation.InstanceValidationContext;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationException;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidatorBase;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;

/**
 * Interface for generic instance validators.
 * 
 * @author Simon Templer
 */
public interface InstanceModelValidator extends ValidatorBase {

	/**
	 * Set the context service provider if available.
	 * 
	 * @param services the service provider
	 */
	public void setServiceProvider(ServiceProvider services);

	/**
	 * Validate a property value that is not an instance.
	 * 
	 * @param value the value to validate, may be <code>null</code>
	 * @param property the property the values belong to
	 * @param entity the property entity or <code>null</code>
	 * @param context the validation context
	 * @throws ValidationException if the validation fails
	 */
	public void validateProperty(Object value, PropertyDefinition property,
			@Nullable EntityDefinition entity, InstanceValidationContext context)
					throws ValidationException;

	/**
	 * Validate an instance, may be a top instance or a property.
	 * 
	 * @param instance the instance to validate
	 * @param entity the instance entity
	 * @param context the validation context
	 * @throws ValidationException if the validation fails
	 */
	public void validateInstance(Instance instance, @Nullable EntityDefinition entity,
			InstanceValidationContext context) throws ValidationException;

	/**
	 * Validate a group property value.
	 * 
	 * @param group the group to validate
	 * @param property the group definition
	 * @param entity the group entity or <code>null</code>
	 * @param context the validation context
	 * @throws ValidationException if the validation fails
	 */
	public void validateGroup(Group group, GroupPropertyDefinition property,
			@Nullable EntityDefinition entity, InstanceValidationContext context)
					throws ValidationException;

	/**
	 * @return the validation category
	 */
	public String getCategory();

}
