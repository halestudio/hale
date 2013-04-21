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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.namespace.QName;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

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
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
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

	private Set<TypeDefinition> sourceTypes;

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
	public void accept(TypeEntityDefinition ted) {
		// find possible source types
		Set<String> sourceTypeNames = featureMap.getPossibleSourceTypes(ted.getType()
				.getDisplayName());

		System.out.println("Possible source types for target type "
				+ ted.getType().getDisplayName() + "...");

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

				System.out.println(type.getName());
			}
		}
		sourceTypes = Collections.unmodifiableSet(types);

		super.accept(ted);
	}

	@Override
	protected boolean visit(PropertyEntityDefinition ped) {
		if (ADE_NS.equals(ped.getDefinition().getName().getNamespaceURI())) {
			// property is from ADE

			for (Cell exampleCell : bgisExamples.get(ped.getDefinition().getName().getLocalPart())) {
				// handle each example cell

				TypeEntityIndex<List<ChildContext>> index = new TypeEntityIndex<List<ChildContext>>();
				Collection<TypeDefinition> sourceTypes = findSourceTypes(exampleCell, index);
				if (sourceTypes != null) {
					for (TypeDefinition sourceType : sourceTypes) {
						// copy cell
						DefaultCell cell = new DefaultCell(exampleCell);
						// reset ID
						cell.setId(null);
						// assign new target
						ListMultimap<String, Entity> target = ArrayListMultimap.create();
						target.put(cell.getTarget().keys().iterator().next(), new DefaultProperty(
								ped));
						cell.setTarget(target);
						// assign new source(s)
						ListMultimap<String, Entity> source = ArrayListMultimap.create();
						for (Entry<String, ? extends Entity> entry : cell.getSource().entries()) {
							// create new source entity
							List<ChildContext> path = index.get(sourceType, entry.getValue());
							if (path == null) {
								throw new IllegalStateException(
										"No replacement property path computed");
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

			return true;
		}

		// FIXME handle CityGML target properties

		return false;
	}

	/**
	 * Find source types to use to propagate the given example cell. If
	 * possible, common super types will be returned.
	 * 
	 * @param exampleCell the example cell
	 * @param index the index to store the replacement property paths in
	 * @return the source types to propagate the cell to
	 */
	private Collection<TypeDefinition> findSourceTypes(Cell exampleCell,
			TypeEntityIndex<List<ChildContext>> index) {
		Set<TypeDefinition> possibleSources = new HashSet<TypeDefinition>(sourceTypes);

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
