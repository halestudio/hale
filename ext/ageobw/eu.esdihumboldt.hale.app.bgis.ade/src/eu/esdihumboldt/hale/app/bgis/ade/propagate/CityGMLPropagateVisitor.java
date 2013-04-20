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
import java.util.Set;

import javax.xml.namespace.QName;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.app.bgis.ade.common.BGISAppConstants;
import eu.esdihumboldt.hale.app.bgis.ade.common.BGISAppUtil;
import eu.esdihumboldt.hale.app.bgis.ade.common.EntityVisitor;
import eu.esdihumboldt.hale.app.bgis.ade.propagate.config.FeatureMap;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
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

				Collection<TypeDefinition> sourceTypes = findSourceTypes(exampleCell, ped.getType());
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
						// FIXME assign new source(s)

						cells.add(cell);
					}
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * @param exampleCell
	 * @param targetType
	 * @return
	 */
	private Collection<TypeDefinition> findSourceTypes(Cell exampleCell, TypeDefinition targetType) {
		Set<TypeDefinition> possibleSources = new HashSet<TypeDefinition>(sourceTypes);

		/*
		 * Add all super types, because if possible, we want to do the mapping
		 * on super types.
		 */
		Set<TypeDefinition> superTypes = new HashSet<TypeDefinition>();
		for (TypeDefinition type : possibleSources) {
			TypeDefinition superType = type.getSuperType();
			while (superType != null) {
				if (!possibleSources.contains(superType) || superTypes.add(superType)) {
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
				if (!hasCompatibleProperty(type, ed)) {
					it.remove();
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
	 * @param type
	 * @param ed
	 * @return
	 */
	private boolean hasCompatibleProperty(TypeDefinition type, EntityDefinition ed) {
		// TODO Auto-generated method stub
		return false;
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
