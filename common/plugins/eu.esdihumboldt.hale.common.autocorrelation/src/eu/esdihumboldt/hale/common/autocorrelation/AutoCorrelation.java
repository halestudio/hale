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

package eu.esdihumboldt.hale.common.autocorrelation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.util.Pair;

/**
 * Class to create Retype and Rename cells for multiple sources. It is used to
 * create mappings based on matching between source and target types.
 * 
 * @author Yasmina Kammeyer
 */
public class AutoCorrelation {

	/**
	 * Build and returns pairs of types. A pair represent a match between a
	 * source and a target type.
	 * 
	 * @param sourceTypes The source TypeDefinitions which will be compared with
	 *            target types
	 * @param targetTypes The target TypeDefinitions which will be compared with
	 *            source types
	 * @param ignoreNamespace Indicates if the namespace is irrelevant for type
	 *            comparison
	 * @param comparator The comparator Object which implements the compare
	 *            Method used for various compares.
	 * @return All pairs which should be retyped (only
	 *         {@link TypeEntityDefinition}s)
	 */
	public static Collection<Pair<TypeEntityDefinition, TypeEntityDefinition>> retype(
			Collection<TypeDefinition> sourceTypes, Collection<TypeDefinition> targetTypes,
			boolean ignoreNamespace, AutoCorrelationComparatorObj comparator) {

		Collection<TypeDefinition> allSourceTypes = new ArrayList<TypeDefinition>();
		Collection<TypeDefinition> allTargetTypes = new ArrayList<TypeDefinition>();

		collectTypeDefinitions(sourceTypes, allSourceTypes);
		collectTypeDefinitions(targetTypes, allTargetTypes);

		Collection<Pair<TypeEntityDefinition, TypeEntityDefinition>> pairs = new ArrayList<Pair<TypeEntityDefinition, TypeEntityDefinition>>();

		createPairsThroughTypeComparison(allSourceTypes, allTargetTypes, pairs, ignoreNamespace,
				comparator);

		return pairs;
	}

	/**
	 * Iterate recursively through all children - preprocessing step
	 * 
	 * @param source The TypeDefinition to add
	 * @param result The result
	 */
	private static void collectTypeDefinitions(Collection<? extends TypeDefinition> source,
			Collection<TypeDefinition> result) {

		for (TypeDefinition def : source) {
			// entity is type definition
			if (!def.getConstraint(AbstractFlag.class).isEnabled()) {
				// entity is concrete type
				if (!result.contains(def))
					result.add(def);
			}
			if (def.getSubTypes() != null && !def.getSubTypes().isEmpty()) {
				// there are some subtypes
				collectTypeDefinitions(def.getSubTypes(), result);
			}
		}
	}

	/**
	 * Every source and target type will be compared. If they are a match, than
	 * they will be stored as a pair
	 * 
	 * @param sourceTypes The source types
	 * @param targetTypes The target types
	 * @param pairs The {@link Set} to collect the pairs, will contain all
	 *            matches
	 * @param ignoreNamespace The name space is irrelevant for types to be
	 *            compared
	 * @param comparator The comparator Object which implements the compare
	 *            Method used for various compares.
	 */
	private static void createPairsThroughTypeComparison(Collection<TypeDefinition> sourceTypes,
			Collection<TypeDefinition> targetTypes,
			Collection<Pair<TypeEntityDefinition, TypeEntityDefinition>> pairs,
			boolean ignoreNamespace, AutoCorrelationComparatorObj comparator) {

		if (sourceTypes == null || sourceTypes.size() <= 0 || targetTypes == null
				|| targetTypes.size() <= 0 || pairs == null) {
			return;
		}

		for (TypeDefinition targetTypeDef : targetTypes) {
			// note: there will be one TypeEntityDefinition, the dafault one
			// without a filter
			createAllPairsForTarget(targetTypeDef, sourceTypes, pairs, ignoreNamespace, comparator);
		}
	}

	/**
	 * The given target will be compared with all given source types. If they
	 * are a match, than they will be stored as a pair
	 * 
	 * @param targetTypeDef The target TypeDefinition
	 * @param sourceTypes The source types
	 * @param pairs The {@link Set} to collect the pairs, will contain all
	 *            matches
	 * @param ignoreNamespace The name space is irrelevant for types to be
	 *            compared
	 * @param comparator The comparator Object which implements the compare
	 *            Method used for various compares.
	 */
	private static void createAllPairsForTarget(TypeDefinition targetTypeDef,
			Collection<TypeDefinition> sourceTypes,
			Collection<Pair<TypeEntityDefinition, TypeEntityDefinition>> pairs,
			boolean ignoreNamespace, AutoCorrelationComparatorObj comparator) {

		TypeEntityDefinition source;
		TypeEntityDefinition target;

		target = new TypeEntityDefinition(targetTypeDef, SchemaSpaceID.TARGET, null);

		for (TypeDefinition sourceTypeDef : sourceTypes) {
			if (comparator.comparator(sourceTypeDef, targetTypeDef, ignoreNamespace)) {
				source = new TypeEntityDefinition(sourceTypeDef, SchemaSpaceID.SOURCE, null);
				pairs.add(new Pair<TypeEntityDefinition, TypeEntityDefinition>(source, target));
			}
		}
	}

	/**
	 * Build and returns pairs of properties. A pair represent a match between a
	 * source and a target property.
	 * 
	 * @param types The TypeDefinitions which will be used to collect the
	 *            properties
	 * @param recursionDepth Indicates how deep the properties should be
	 *            iterated through to find pairs. 1 stands for: only the topmost
	 *            properties should be compared and collected.
	 * @param ignoreNamespace Indicates if the name space is irrelevant for type
	 *            comparison
	 * @param useSuperType True, if the top most parent should be used
	 * @param useStructuralRename False, if also children properties of matched
	 *            parent properties should be created as a direct mapping.
	 * @param comparator The comparator Object which implements the compare
	 *            Method used for various compares.
	 * @return result All pairs which should be renamed (only
	 *         {@link PropertyDefinition}s)
	 */
	public static Map<Pair<TypeEntityDefinition, TypeEntityDefinition>, Collection<Pair<PropertyEntityDefinition, PropertyEntityDefinition>>> rename(
			Collection<Pair<TypeEntityDefinition, TypeEntityDefinition>> types, int recursionDepth,
			boolean ignoreNamespace, boolean useSuperType, boolean useStructuralRename,
			AutoCorrelationComparatorObj comparator) {

		Map<Pair<TypeEntityDefinition, TypeEntityDefinition>, Collection<Pair<PropertyEntityDefinition, PropertyEntityDefinition>>> result = new HashMap<>();
		Collection<Pair<PropertyEntityDefinition, PropertyEntityDefinition>> pairs = new ArrayList<>();
		Map<String, Collection<Pair<PropertyEntityDefinition, PropertyEntityDefinition>>> alreadyContained = new HashMap<>();

		for (Pair<TypeEntityDefinition, TypeEntityDefinition> type : types) {
			pairs.clear();

			collectPropEntityDefinitions(AlignmentUtil.getDefaultChildren(type.getFirst()),
					AlignmentUtil.getDefaultChildren(type.getSecond()), pairs, ignoreNamespace,
					recursionDepth, useStructuralRename, comparator);

			if (useSuperType) {
				result.put(type, reparentPropEntDef(pairs, alreadyContained));
			}
			else {
				result.put(type, pairs);
			}

		}

		return result;
	}

	/**
	 * Create a collection of reparented {@link PropertyEntityDefinition}s out
	 * of a collection of {@link PropertyEntityDefinition}s
	 * 
	 * @param properties The properties, which will be converted
	 * @param alreadyContained This collection is checked for doubles; it is
	 *            used like a filter. It will be modified if a new
	 *            PropertyEntityDefinition is added as a result.
	 * @return a collection of {@TypeEntityDefinition}
	 *         and {@link PropertyEntityDefinition}s
	 */
	private static Collection<Pair<PropertyEntityDefinition, PropertyEntityDefinition>> reparentPropEntDef(
			Collection<Pair<PropertyEntityDefinition, PropertyEntityDefinition>> properties,
			Map<String, Collection<Pair<PropertyEntityDefinition, PropertyEntityDefinition>>> alreadyContained) {

		Collection<Pair<PropertyEntityDefinition, PropertyEntityDefinition>> result = new ArrayList<Pair<PropertyEntityDefinition, PropertyEntityDefinition>>();
		Pair<PropertyEntityDefinition, PropertyEntityDefinition> pair;

		for (Pair<PropertyEntityDefinition, PropertyEntityDefinition> prop : properties) {
			EntityDefinition sourceProp = prop.getFirst();
			EntityDefinition targetProp = prop.getSecond();

			if (sourceProp instanceof PropertyEntityDefinition
					&& targetProp instanceof PropertyEntityDefinition) {

				pair = createPropertyWithTopMostParent(sourceProp, targetProp);

				if (checkAndUpdateAlreadyMappedMap(alreadyContained, pair)) {
					result.add(pair);
				}
			}
		}

		return result;
	}

	/**
	 * Creates a pair with the topmost parent.
	 * 
	 * @param sourceProp The source/first entry of the pair
	 * @param targetProp The target/second entry of the pair
	 * @return The pair with the topmost parent for the first and second entry
	 */
	private static Pair<PropertyEntityDefinition, PropertyEntityDefinition> createPropertyWithTopMostParent(
			EntityDefinition sourceProp, EntityDefinition targetProp) {

		TypeDefinition sourceType = getTopMostType((PropertyEntityDefinition) sourceProp);
		TypeDefinition targetType = getTopMostType((PropertyEntityDefinition) targetProp);

		sourceProp = new PropertyEntityDefinition(sourceType, sourceProp.getPropertyPath(),
				sourceProp.getSchemaSpace(), sourceProp.getFilter());

		targetProp = new PropertyEntityDefinition(targetType, targetProp.getPropertyPath(),
				targetProp.getSchemaSpace(), targetProp.getFilter());

		return new Pair<PropertyEntityDefinition, PropertyEntityDefinition>(
				(PropertyEntityDefinition) sourceProp, (PropertyEntityDefinition) targetProp);

	}

	/**
	 * This method adds a property to the alreadyContained Map. There can be
	 * multiple pairs of one source and target PropertyEntityDefinition, if
	 * useSuperType is true and they get mapped to the same superType. To avoid
	 * doubles at this state, a Map of Types as keys and a Collection of
	 * Properties is used. The key String represent a composition of the Type's
	 * QName of the given property pair. (SourceType.QName + TargetType.QName)
	 * 
	 * @param alreadyContained A Map with all already mapped properties.
	 * @param entry The pair tried to add
	 * @return False, if the property was mapped already and the
	 *         alreadyContained collection is not modified, true otherwise.
	 */
	private static boolean checkAndUpdateAlreadyMappedMap(
			Map<String, Collection<Pair<PropertyEntityDefinition, PropertyEntityDefinition>>> alreadyContained,
			Pair<PropertyEntityDefinition, PropertyEntityDefinition> entry) {

		String key = entry.getFirst().getType().getName().toString()
				+ entry.getSecond().getType().getName().toString();

		// there is an existing entry of this pair for this super
		// type
		if (alreadyContained.get(key) != null && alreadyContained.get(key).contains(entry)) {
			// do not add the pair to collection
			return false;
		}
		// there is not an entry but there is a collection, so add
		// the new pair to alreadyContained
		else if (alreadyContained.get(key) != null) {
			alreadyContained.get(key).add(entry);
			return true;
		}
		// There is not existing key entry for this type, so create
		// an empty entry and add the pair to it
		else {
			Collection<Pair<PropertyEntityDefinition, PropertyEntityDefinition>> tmp = new ArrayList<>(
					1);
			tmp.add(entry);
			alreadyContained.put(key, tmp);
			return true;
		}

	}

	/**
	 * Returns the top most parent of the given property
	 * 
	 * @param property The property
	 * @return The top most type (parent) of the given property
	 */
	private static TypeDefinition getTopMostType(PropertyEntityDefinition property) {
		TypeDefinition type = property.getType();
		TypeDefinition superType = type.getSuperType();
		while (superType != null) {
			if (superType.getChild(property.getDefinition().getName()) == null) {
				// the super type does not contain the property anymore
				return type;
			}

			type = superType;
			superType = type.getSuperType();
		}

		return type;
	}

	/**
	 * Call this method to collect all property pairs for the given source and
	 * target properties based on the compare function. There is no explicit
	 * connection to a specific type.
	 * 
	 * @param sourceProperties A collection containing all source properties
	 * @param targetProperties A collection containing all target properties
	 * @param result The collection of found/matched pairs
	 * @param ignoreNamespace Indicates if the name space is irrelevant for
	 *            comparison
	 * @param recursionDepth The current depth. Group properties do not count as
	 *            a step.
	 * @param useStructuralRename False, if also children properties of matched
	 *            parent properties should be created as a direct mapping.
	 * @param comparator The comparator Object which implements the compare
	 *            Method used for various compares.
	 */
	public static void collectPropEntityDefinitions(
			Collection<? extends EntityDefinition> sourceProperties,
			Collection<? extends EntityDefinition> targetProperties,
			Collection<Pair<PropertyEntityDefinition, PropertyEntityDefinition>> result,
			boolean ignoreNamespace, int recursionDepth, boolean useStructuralRename,
			AutoCorrelationComparatorObj comparator) {

		// initialize
		Collection<EntityDefinition> sources = new ArrayList<>(sourceProperties);
		Collection<EntityDefinition> sourcesChildren = new ArrayList<>(sourceProperties);
		Collection<EntityDefinition> targets = new ArrayList<>(targetProperties);
		Collection<EntityDefinition> targetsChildren = new ArrayList<>(targetProperties);

		for (int i = recursionDepth; i > 0; i--) {
			// copy current source and target
			Collection<EntityDefinition> deleteFromSources = new ArrayList<>();
			Collection<EntityDefinition> deleteFromTargets = new ArrayList<>();

			createAllPropertyPairsForGivenCollections(sources, targets, ignoreNamespace,
					comparator, deleteFromSources, deleteFromTargets, result);

			// if structural Rename should be used: remove all already matched
			// properties so its children won't be collected.
			if (useStructuralRename) {
				sourcesChildren.removeAll(deleteFromSources);
				targetsChildren.removeAll(deleteFromTargets);
			}

			// collect next children
			sourcesChildren = getChildrenPropertyEntityDefinitions(new ArrayList<EntityDefinition>(
					sourcesChildren));

			targetsChildren = getChildrenPropertyEntityDefinitions(new ArrayList<EntityDefinition>(
					targetsChildren));

			if (sourcesChildren.isEmpty() && targetsChildren.isEmpty()) {
				break;
			}

			// set sources and targets for next iteration
			sources.removeAll(deleteFromSources);
			targets.removeAll(deleteFromTargets);
			// add all children to the next iteration process
			sources.addAll(sourcesChildren);
			targets.addAll(targetsChildren);
		}

	}

	/**
	 * Creates all property pairs (all matches) and add them to the result, if
	 * not already contained.
	 * 
	 * @param sources The source Collection containing the properties
	 * @param targets The target Collection containing the properties
	 * @param ignoreNamespace Indicates if the name space is irrelevant for
	 *            comparison
	 * @param comparator The comparator Object which implements the compare
	 *            Method used for various compares.
	 * @param deleteFromSources Every added source property will be added to
	 *            this collection
	 * @param deleteFromTargets Every added target property will be added to
	 *            this collection
	 * @param result The collection where to add the property pairs.
	 */
	private static void createAllPropertyPairsForGivenCollections(
			Collection<EntityDefinition> sources, Collection<EntityDefinition> targets,
			boolean ignoreNamespace, AutoCorrelationComparatorObj comparator,
			Collection<EntityDefinition> deleteFromSources,
			Collection<EntityDefinition> deleteFromTargets,
			Collection<Pair<PropertyEntityDefinition, PropertyEntityDefinition>> result) {

		// iterate threw all children
		for (EntityDefinition sourceChild : sources) {
			for (EntityDefinition targetChild : targets) {
				// ignore package/group
				if (sourceChild instanceof PropertyEntityDefinition
						&& targetChild instanceof PropertyEntityDefinition) {
					// source and target children are properties and aren't
					// null
					if (comparator.comparator(
							((PropertyEntityDefinition) sourceChild).getDefinition(),
							((PropertyEntityDefinition) targetChild).getDefinition(),
							ignoreNamespace)) {
						// match found
						Pair<PropertyEntityDefinition, PropertyEntityDefinition> pair = new Pair<PropertyEntityDefinition, PropertyEntityDefinition>(
								(PropertyEntityDefinition) sourceChild,
								(PropertyEntityDefinition) targetChild);

						if (!result.contains(pair)) {
							result.add(pair);
						}
						// delete source and target from list.
						// cyclic prop, remove and do not iterate
						// through its children
						deleteFromSources.add(sourceChild);
						deleteFromTargets.add(targetChild);

					}
				}
			}
		}
	}

	/**
	 * Collect all children from the given {@link EntityDefinition} collection
	 * 
	 * @param parentList The collection collect the children from
	 * @return The collection of children
	 */
	private static Collection<EntityDefinition> getChildrenPropertyEntityDefinitions(
			Collection<EntityDefinition> parentList) {

		Collection<EntityDefinition> sourceChildren = new ArrayList<>();

		if (parentList == null || parentList.isEmpty()) {
			return sourceChildren;
		}

		for (EntityDefinition def : parentList) {
			addAllChildren(def, sourceChildren);
		}

		return sourceChildren;
	}

	/**
	 * Adds all children of the given parent to the target collection, if not
	 * contained already.
	 * 
	 * @param parent The parent EntityDefinitions adding the children from
	 * @param target The target collection adding the children to
	 */
	private static void addAllChildren(EntityDefinition parent, Collection<EntityDefinition> target) {

		for (EntityDefinition child : AlignmentUtil.getDefaultChildren(parent)) {
			if (!target.contains(child)) {
				target.add(child);
			}
		}
	}

}
