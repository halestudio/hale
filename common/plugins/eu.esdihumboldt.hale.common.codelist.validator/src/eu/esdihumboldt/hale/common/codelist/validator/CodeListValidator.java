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

package eu.esdihumboldt.hale.common.codelist.validator;

import java.text.MessageFormat;

import javax.annotation.Nullable;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.common.codelist.CodeList.CodeEntry;
import eu.esdihumboldt.hale.common.codelist.config.CodeListAssociations;
import eu.esdihumboldt.hale.common.codelist.config.CodeListReference;
import eu.esdihumboldt.hale.common.codelist.service.CodeListRegistry;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoService;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.extension.validation.InstanceValidationContext;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationException;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instancevalidator.InstanceModelValidator;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;

/**
 * Instance validator checking values w/ associated code lists.
 * 
 * @author Simon Templer
 */
public class CodeListValidator implements InstanceModelValidator {

	private static final ALogger log = ALoggerFactory.getLogger(CodeListValidator.class);

	private CodeListRegistry codeLists;
	private ProjectInfoService projectService;

	@Override
	public void setServiceProvider(ServiceProvider services) {
		// initialize registry and associations via service provider
		if (services != null) {
			codeLists = services.getService(CodeListRegistry.class);
			projectService = services.getService(ProjectInfoService.class);
		}
	}

	private void validateCodeListValue(@Nullable Object value, @Nullable EntityDefinition entity)
			throws ValidationException {
		CodeListAssociations associations = null;
		if (projectService != null) {
			associations = projectService.getProperty(CodeListAssociations.KEY_ASSOCIATIONS)
					.as(CodeListAssociations.class);
		}

		if (value != null && entity != null && associations != null && codeLists != null) {
			CodeListReference clRef = associations.getCodeList(entity);
			if (clRef != null) {
				CodeList cl = codeLists.findCodeList(clRef);
				if (cl != null) {
					String strValue = value.toString();
					CodeEntry entry = cl.getEntryByIdentifier(strValue);
					if (entry == null) {
						throw new ValidationException(MessageFormat.format(
								"Value ''{0}'' not found in associated code list", strValue));
					}
				}
				else {
					log.warn("Code list " + clRef + " not found for validation");
				}
			}
		}
	}

	@Override
	public void validateProperty(Object value, PropertyDefinition property, EntityDefinition entity,
			InstanceValidationContext context) throws ValidationException {
		validateCodeListValue(value, entity);
	}

	@Override
	public void validateInstance(Instance instance, EntityDefinition entity,
			InstanceValidationContext context) throws ValidationException {
		validateCodeListValue(instance.getValue(), entity);
	}

	@Override
	public void validateGroup(Group group, GroupPropertyDefinition property,
			EntityDefinition entity, InstanceValidationContext context) throws ValidationException {
		// ignore groups
	}

	@Override
	public String getCategory() {
		return "Code list";
	}

}
