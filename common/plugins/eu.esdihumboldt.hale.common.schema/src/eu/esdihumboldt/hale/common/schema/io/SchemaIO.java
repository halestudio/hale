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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.schema.io;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;

import de.fhg.igd.osgi.util.configuration.IConfigurationService;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;

/**
 * Schema I/O utilities
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public abstract class SchemaIO {

	/**
	 * ID of the action to load a source schema. Reflects the ID defined in the
	 * extension.
	 */
	public static final String ACTION_LOAD_SOURCE_SCHEMA = "eu.esdihumboldt.hale.io.schema.read.source";

	/**
	 * ID of the action to load a target schema. Reflects the ID defined in the
	 * extension.
	 */
	public static final String ACTION_LOAD_TARGET_SCHEMA = "eu.esdihumboldt.hale.io.schema.read.target";

	/**
	 * ID of the action to export the target schemas. Reflects the ID defined in
	 * the extension.
	 */
	public static final String ACTION_EXPORT_TARGET_SCHEMAS = "eu.esdihumboldt.hale.io.schema.export.target";

	/**
	 * ID of the action to export the source schemas. Reflects the ID defined in
	 * the extension.
	 */
	public static final String ACTION_EXPORT_SOURCE_SCHEMAS = "eu.esdihumboldt.hale.io.schema.export.source";

	/**
	 * Creates a schema reader instance
	 * 
	 * @param contentType the content type the provider must match, may be
	 *            <code>null</code> if providerId is set
	 * @param providerId the id of the provider to use, may be <code>null</code>
	 *            if contentType is set
	 * @return the I/O provider preconfigured with the content type if it was
	 *         given or <code>null</code> if no matching I/O provider is found
	 */
	public static SchemaReader createSchemaReader(IContentType contentType, String providerId) {
		return HaleIO.createIOProvider(SchemaReader.class, contentType, providerId);
	}

	/**
	 * Load the configuration of mapping relevant types.
	 * 
	 * @param types the types
	 * @param spaceID the schema space identifier
	 * @param configurationService the configuration service
	 */
	public static void loadMappingRelevantTypesConfig(TypeIndex types, SchemaSpaceID spaceID,
			IConfigurationService configurationService) {
		String paramName = getMappingRelevantTypesParameterName(spaceID);
		List<String> config = configurationService.getList(paramName);
		if (config != null) {
			Set<String> mappableConfig = new HashSet<>(config);

			// collect types to be toggled
			List<TypeDefinition> toToggle = new ArrayList<>();

			for (TypeDefinition type : types.getTypes()) {
				boolean relevant = type.getConstraint(MappingRelevantFlag.class).isEnabled();
				boolean shouldBeRelevant = mappableConfig.contains(type.getName().toString());

				if (relevant != shouldBeRelevant) {
					toToggle.add(type);
				}
			}

			types.toggleMappingRelevant(toToggle);
		}
	}

	/**
	 * Get the name of the project settings parameter specifying the mappable
	 * types.
	 * 
	 * @param spaceID the schemas space
	 * @return the parameter name
	 */
	public static String getMappingRelevantTypesParameterName(SchemaSpaceID spaceID) {
		return "mappable" + (spaceID == SchemaSpaceID.SOURCE ? "Source" : "Target") + "Type";
	}

	/**
	 * Load the configuration of mapping relevant types.
	 * 
	 * @param types the types
	 * @param spaceID the schema space identifier
	 * @param project the project holding the configuration information
	 */
	public static void loadMappingRelevantTypesConfig(TypeIndex types, SchemaSpaceID spaceID,
			final Project project) {
		loadMappingRelevantTypesConfig(types, spaceID,
				ProjectIO.createProjectConfigService(project));
	}

}
