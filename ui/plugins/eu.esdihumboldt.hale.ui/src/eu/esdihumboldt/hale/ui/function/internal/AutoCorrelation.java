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

package eu.esdihumboldt.hale.ui.function.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.eclipse.ui.PlatformUI;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.util.Pair;

/**
 * Class to create Retype and Rename cells for multiple sources. It is used to
 * create mappings based on matching between source and target types.
 * 
 * @author Yasmina Kammeyer
 */
public class AutoCorrelation {

	private EntityDefinitionService eds;

	/**
	 * Creates only retype cells
	 * 
	 * @param sourceAndTarget
	 * @param ignoreNamespace
	 * @param ignoreInherited
	 */
	public void retype(SchemaSelection sourceAndTarget, boolean ignoreNamespace,
			boolean ignoreInherited) {
		// TODO
		eds = (EntityDefinitionService) PlatformUI.getWorkbench().getService(
				EntityDefinitionService.class);

		Set<TypeDefinition> sourceTypes = Collections.emptySet();
		Set<TypeDefinition> targetTypes = Collections.emptySet();

		collectTypeDefinitions(sourceAndTarget.getSourceItems(), sourceTypes);
		collectTypeDefinitions(sourceAndTarget.getTargetItems(), targetTypes);

		Set<Pair<TypeDefinition, TypeDefinition>> pairs = Collections.emptySet();

		createPairsThroughTypeMatching(sourceTypes, targetTypes, pairs, ignoreNamespace);

	}

	/**
	 * @param sourceTypes
	 * @param targetTypes
	 * @param pairs
	 * @param ignoreNamespace
	 */
	private void createPairsThroughTypeMatching(Collection<TypeDefinition> sourceTypes,
			Collection<TypeDefinition> targetTypes,
			Set<Pair<TypeDefinition, TypeDefinition>> pairs, boolean ignoreNamespace) {
		// TODO Auto-generated method stub
		for (TypeDefinition targetTypeDef : targetTypes) {
			for (TypeDefinition sourceTypeDef : sourceTypes) {
				// best match - same qname
				if (targetTypeDef.getName().equals(sourceTypeDef.getName())) {
					pairs.add(new Pair<TypeDefinition, TypeDefinition>(sourceTypeDef, targetTypeDef));
				}
				else if (ignoreNamespace
						&& targetTypeDef.getName().getLocalPart()
								.equals(sourceTypeDef.getName().getLocalPart())) {
					// weaker match - same local part (e.g. name)
					pairs.add(new Pair<TypeDefinition, TypeDefinition>(sourceTypeDef, targetTypeDef));
				}
			}
		}
	}

	/**
	 * Used to collect all TypeDefinitions
	 * 
	 * @param source Contains the TypeDefinitions to add
	 * @param result The result
	 */
	private void collectTypeDefinitions(Set<EntityDefinition> source, Set<TypeDefinition> result) {

		for (EntityDefinition entity : source) {
			if (entity instanceof TypeEntityDefinition) {
				// entity is type definition
				if (entity.getType().getSubTypes().isEmpty()) {
					// entity is concrete type
					result.add(entity.getType());
				}
				else {
					collectTypeDefinitions(entity.getType().getSubTypes(), result);
				}
			}
		}
	}

	/**
	 * Iterate recursively through all children
	 * 
	 * @param source The TypeDefinition to add
	 * @param result The result
	 */
	private void collectTypeDefinitions(Collection<? extends TypeDefinition> source,
			Set<TypeDefinition> result) {

		for (TypeDefinition def : source) {
			// entity is type definition
			if (def.getSubTypes().isEmpty()) {
				// entity is concrete type
				result.add(def);
			}
			else {
				collectTypeDefinitions(def.getSubTypes(), result);
			}
		}
	}

	/**
	 * Creates only rename cells
	 * 
	 * @param sourceAndTarget
	 * @param transformationParameter
	 * @param ignoreInherited
	 */
	public void rename(SchemaSelection sourceAndTarget,
			ListMultimap<String, ParameterValue> transformationParameter, boolean ignoreInherited) {
		// TODO
	}

	/**
	 * Create rename and retype cells
	 * 
	 * @param sourceAndTarget
	 * @param transformationParameter
	 * @param ignoreInherited
	 */
	public void retypeAndRename(SchemaSelection sourceAndTarget,
			ListMultimap<String, ParameterValue> transformationParameter, boolean ignoreInherited) {
		// TODO
	}

}
