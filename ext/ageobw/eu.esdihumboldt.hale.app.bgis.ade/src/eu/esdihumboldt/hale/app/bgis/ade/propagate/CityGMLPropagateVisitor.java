/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.app.bgis.ade.propagate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

import eu.esdihumboldt.hale.app.bgis.ade.common.BGISAppConstants;
import eu.esdihumboldt.hale.app.bgis.ade.common.BGISAppUtil;
import eu.esdihumboldt.hale.app.bgis.ade.common.EntityVisitor;
import eu.esdihumboldt.hale.app.bgis.ade.propagate.config.FeatureMap;
import eu.esdihumboldt.hale.app.bgis.ade.propagate.internal.TypeEntityIndex;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.CityGMLConstants;

/**
 * Entity visitor that creates cells from example cells.
 * 
 * @author Simon Templer
 */
public class CityGMLPropagateVisitor extends EntityVisitor implements BGISAppConstants,
		CityGMLConstants {

	/**
	 * The created cells.
	 */
	private final List<Cell> cells = new ArrayList<Cell>();

	private final Multimap<String, Cell> bgisExamples;

	private final Multimap<QName, Cell> cityGMLExamples;

	private final Schema cityGMLSource;

	private final FeatureMap featureMap;

	private final SetMultimap<Cell, TypeDefinition> handledTargets = HashMultimap.create();

	/**
	 * Create an example cell visitor creating derived cells.
	 * 
	 * @param cityGMLSource the CityGML source schema to use for the created
	 *            mapping cells
	 * @param bgisExamples example cells, with the target ADE property name as
	 *            key
	 * @param cityGMLExamples example cells, with the target CityGML property
	 *            name as key
	 * @param featureMap the feature map
	 */
	public CityGMLPropagateVisitor(Schema cityGMLSource, Multimap<String, Cell> bgisExamples,
			Multimap<QName, Cell> cityGMLExamples, FeatureMap featureMap) {
		this.bgisExamples = bgisExamples;
		this.cityGMLExamples = cityGMLExamples;
		this.cityGMLSource = cityGMLSource;
		this.featureMap = featureMap;
	}

	@Override
	protected boolean visit(PropertyEntityDefinition ped) {
		if (ADE_NS.equals(ped.getDefinition().getName().getNamespaceURI())) {
			// property is from ADE

			for (Cell exampleCell : bgisExamples.get(ped.getDefinition().getName().getLocalPart())) {
				// handle each example cell
				propagateCell(exampleCell, ped);
			}

			return true;
		}
		else if (ped.getDefinition().getName().getNamespaceURI().startsWith(CITYGML_NAMESPACE_CORE)) {
			// is a CityGML property

			/*
			 * FIXME do only for certain types, namely those the target property
			 * is defined in, to prevent duplicated cells. But those will not be
			 * supplied! XXX think about it
			 */

			Pattern nsPattern = Pattern.compile("^" + Pattern.quote(CITYGML_NAMESPACE_CORE)
					+ "(/[^/]+)?/([^/]+)$");
			Matcher matcher = nsPattern.matcher(ped.getDefinition().getName().getNamespaceURI());
			if (matcher.find()) {
				// name of the CityGML module expected
//				String module = matcher.group(1);

				for (Entry<QName, Cell> example : cityGMLExamples.entries()) {
					// check each example cell

					if (example.getKey().getLocalPart()
							.equals(ped.getDefinition().getName().getLocalPart())) {
						// local name matches
						Matcher exMatcher = nsPattern.matcher(example.getKey().getNamespaceURI());
						if (exMatcher.find()) {
							/*
							 * The module is not compared after all, as they may
							 * be different and still propagation is desired.
							 * This is the case for instance for
							 * lod1MultiSurface, which may occur with building,
							 * vegetation and other module namespaces.
							 */
//							String exampleModule = exMatcher.group(1);
//							if (Objects.equals(module, exampleModule)) {
							// CityGML module matches
							propagateCell(example.getValue(), ped);
//							}
						}
					}
				}
			}
			else {
				System.err.println("ERROR: Failure analysing CityGML namespace");
			}

			// XXX only level one CityGML target properties supported!
			return false;
		}

		return false;
	}

	/**
	 * Propagate a given cell to the given target property and possible source
	 * types.
	 * 
	 * @param exampleCell the example cell
	 * @param ped the target property
	 */
	private void propagateCell(Cell exampleCell, PropertyEntityDefinition ped) {
		/*
		 * Find the type where the property actually is defined, as if possible
		 * a super type mapping should be used.
		 */
		TypeDefinition targetType = findTypeDefining(ped);
		if (!targetType.equals(ped.getType())) {
			ped = new PropertyEntityDefinition(targetType, ped.getPropertyPath(),
					ped.getSchemaSpace(), ped.getFilter());
		}

		// check if the cell was already handled for the type
		if (handledTargets.get(exampleCell).contains(targetType)) {
			// don't produce any duplicates
			return;
		}
		handledTargets.put(exampleCell, targetType);

		TypeEntityIndex<List<ChildContext>> index = new TypeEntityIndex<List<ChildContext>>();
		Collection<TypeDefinition> sourceTypes = findSourceTypes(exampleCell, targetType, index);
		if (sourceTypes != null) {
			for (TypeDefinition sourceType : sourceTypes) {
				// copy cell
				DefaultCell cell = new DefaultCell(exampleCell);
				// reset ID
				cell.setId(null);
				// assign new target
				ListMultimap<String, Entity> target = ArrayListMultimap.create();
				target.put(cell.getTarget().keys().iterator().next(), new DefaultProperty(ped));
				cell.setTarget(target);
				// assign new source(s)
				ListMultimap<String, Entity> source = ArrayListMultimap.create();
				for (Entry<String, ? extends Entity> entry : cell.getSource().entries()) {
					// create new source entity
					List<ChildContext> path = index.get(sourceType, entry.getValue());
					if (path == null) {
						throw new IllegalStateException("No replacement property path computed");
					}
					Property newSource = new DefaultProperty(new PropertyEntityDefinition(
							sourceType, path, SchemaSpaceID.SOURCE, null));
					source.put(entry.getKey(), newSource);
				}
				cell.setSource(source);

				cells.add(cell);
			}
		}
	}

	/**
	 * Find the type that actually defines the property referenced in the given
	 * property entity definition.
	 * 
	 * @param ped the property entity definition
	 * @return the type defining the referenced property
	 */
	private TypeDefinition findTypeDefining(PropertyEntityDefinition ped) {
		/*
		 * The type we look for is either the one given in the entity
		 * definition, or a super type.
		 */
		TypeDefinition parent = ped.getType();
		TypeDefinition superType = parent.getSuperType();
		while (superType != null) {
			if (!hasProperty(superType, ped.getPropertyPath())) {
				return parent;
			}
			parent = superType;
			superType = parent.getSuperType();
		}

		return parent;
	}

	/**
	 * Tests the given type if it has the properties defined in the given
	 * property path.
	 * 
	 * @param type the type definition or definition group
	 * @param propertyPath the property path to test
	 * @return if the property path is valid for the given type
	 */
	private boolean hasProperty(DefinitionGroup type, List<ChildContext> propertyPath) {
		if (propertyPath == null || propertyPath.isEmpty()) {
			return true;
		}
		else {
			ChildDefinition<?> child = type.getChild(propertyPath.get(0).getChild().getName());
			if (child != null) {
				if (propertyPath.size() == 1) {
					return true;
				}
				else {
					return hasProperty(DefinitionUtil.getDefinitionGroup(child),
							propertyPath.subList(1, propertyPath.size()));
				}
			}
			else {
				return false;
			}
		}
	}

	/**
	 * Find source types to use to propagate the given example cell. If
	 * possible, common super types will be returned.
	 * 
	 * @param exampleCell the example cell
	 * @param targetType the target type
	 * @param index the index to store the replacement property paths in
	 * @return the source types to propagate the cell to
	 */
	private Collection<TypeDefinition> findSourceTypes(Cell exampleCell, TypeDefinition targetType,
			TypeEntityIndex<List<ChildContext>> index) {
		Set<TypeDefinition> possibleSources = findAllPossibleSources(targetType);

		/*
		 * Add all super types, because if possible, we want to do the mapping
		 * on super types.
		 */
		Set<TypeDefinition> superTypes = new HashSet<TypeDefinition>();
		for (TypeDefinition type : possibleSources) {
			TypeDefinition superType = type.getSuperType();
			while (superType != null) {
				if (superTypes.add(superType) || !possibleSources.contains(superType)) {
					superType = superType.getSuperType();
				}
				else {
					superType = null;
				}
			}
		}
		possibleSources.addAll(superTypes);

		/*
		 * Check source entities and filter all source types that don't match
		 * the entity.
		 */
		TypeDefinition originalSource = null;
		for (Entity source : exampleCell.getSource().values()) {
			EntityDefinition ed = source.getDefinition();

			// check source type
			if (originalSource == null) {
				originalSource = ed.getType();
			}
			else {
				if (!originalSource.equals(ed.getType())) {
					System.err.println("WARNING: ignoring cell with sources in different types");
					return null;
				}
			}

			if (ed.getPropertyPath().isEmpty()) {
				// don't handle type cells
				return null;
			}

			// remove all types w/o compatible property
			Iterator<TypeDefinition> it = possibleSources.iterator();
			while (it.hasNext()) {
				TypeDefinition type = it.next();
				List<ChildContext> newPath = hasCompatibleProperty(type, ed.getPropertyPath());
				if (newPath == null) {
					it.remove();
				}
				else {
					// remember child path per root type and entity
					index.put(type, source, newPath);
				}
			}
		}

		/*
		 * Remove all types that have super types contained in the set.
		 */
		Set<TypeDefinition> toTest = new HashSet<TypeDefinition>(possibleSources);
		for (TypeDefinition type : toTest) {
			TypeDefinition superType = type.getSuperType();
			while (superType != null) {
				if (possibleSources.contains(superType)) {
					possibleSources.remove(type);
					// other super types are tested on their own
					break;
				}
				superType = superType.getSuperType();
			}
		}

		return possibleSources;
	}

	/**
	 * Find all possible CityGML source types for the given target type based on
	 * the feature map configuration. Also takes into account the possible
	 * sources for sub-types of the given target type.
	 * 
	 * @param targetType the target type definition
	 * @return the set of possible source type definitions
	 */
	private Set<TypeDefinition> findAllPossibleSources(TypeDefinition targetType) {
		// find all possible source types, taking into account also sub-types
		Queue<TypeDefinition> toTest = new LinkedList<TypeDefinition>();
		Set<String> sourceTypeNames = new HashSet<String>();
		toTest.add(targetType);
		while (!toTest.isEmpty()) {
			TypeDefinition type = toTest.poll();
			sourceTypeNames.addAll(featureMap.getPossibleSourceTypes(type.getDisplayName()));
			toTest.addAll(type.getSubTypes());
		}

		Set<TypeDefinition> types = new HashSet<TypeDefinition>();
		for (TypeDefinition type : cityGMLSource.getTypes()) {
			if (type.getName().getNamespaceURI().startsWith(CITYGML_NAMESPACE_CORE)
					&& sourceTypeNames.contains(type.getDisplayName())
					&& BGISAppUtil.isFeatureType(type)) {
				/*
				 * Type is a feature type from CityGML and is one of the
				 * possible source types
				 */
				types.add(type);
			}
		}

		return types;
	}

	private List<ChildContext> hasCompatibleProperty(DefinitionGroup type,
			List<ChildContext> propertyPath) {
		int propIndex = -1;
		// find index of first property (ignoring groups)
		for (int i = 0; i < propertyPath.size() && propIndex < 0; i++) {
			if (propertyPath.get(0).getChild().asProperty() != null) {
				propIndex = i;
			}
		}

		if (propIndex < 0) {
			// now there something is not right
			return null;
		}

		QName name = propertyPath.get(propIndex).getChild().getName();
		if (!name.getNamespaceURI().startsWith(CITYGML_NAMESPACE_CORE)) {
			System.err.println("ERROR: only cells on CityGML source properties will be propagated");
			return null;
		}

		// look for a potential match
		Collection<? extends ChildDefinition<?>> children = DefinitionUtil.getAllChildren(type);
		for (ChildDefinition<?> candidate : children) {
			if (candidate.asProperty() != null) {
				if (candidate.getName().getNamespaceURI().startsWith(CITYGML_NAMESPACE_CORE)
						&& candidate.getName().getLocalPart().equals(name.getLocalPart())
						&& candidate
								.asProperty()
								.getPropertyType()
								.getName()
								.getLocalPart()
								.equals(propertyPath.get(propIndex).getChild().asProperty()
										.getPropertyType().getName().getLocalPart())) {
					/*
					 * Property has CityGML namespace, matching local name and
					 * matching property type local name.
					 */
					List<ChildContext> newPath = new ArrayList<ChildContext>();
					ChildContext org = propertyPath.get(propIndex);
					ChildContext rep = new ChildContext(org.getContextName(), org.getIndex(),
							org.getCondition(), candidate);
					newPath.add(rep);
					if (propIndex + 1 >= propertyPath.size()) {
						// last property, return
						return newPath;
					}
					else {
						// check path further
						List<ChildContext> childPath = hasCompatibleProperty(candidate.asProperty()
								.getPropertyType(), propertyPath.subList(propIndex + 1,
								propertyPath.size()));
						if (childPath != null) {
							newPath.addAll(childPath);
							return newPath;
						}
					}
				}
			}
			else if (candidate.asGroup() != null) {
				// check path further for the same property
				List<ChildContext> childPath = hasCompatibleProperty(candidate.asGroup(),
						propertyPath.subList(propIndex, propertyPath.size()));
				if (childPath != null) {
					// prepend group to path
					childPath.add(0, new ChildContext(candidate));
					return childPath;
				}
			}
		}

		return null;
	}

	/**
	 * Get the created cells.
	 * 
	 * @return the cells assigning default values
	 */
	public List<Cell> getCells() {
		return cells;
	}

}
