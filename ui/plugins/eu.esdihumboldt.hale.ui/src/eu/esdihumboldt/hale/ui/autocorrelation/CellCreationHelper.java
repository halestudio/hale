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

package eu.esdihumboldt.hale.ui.autocorrelation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.ui.PlatformUI;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.RenameFunction;
import eu.esdihumboldt.hale.common.align.model.functions.RetypeFunction;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.util.Pair;

/**
 * Helper class which accepts source, target and transformation parameter to
 * create cells.
 * 
 * @author Yasmina Kammeyer
 */
public class CellCreationHelper {

	/**
	 * Creates a cell
	 * 
	 * @param source The source entities, can be Type or Property
	 * @param target The target entities, can be Type or Property
	 * @param transformationParameter The transformation parameters
	 * @param transformationIdentifier The id of the transformation
	 * @return The created MutableCell
	 */
	public static MutableCell createCell(ListMultimap<String, ? extends Entity> source,
			ListMultimap<String, ? extends Entity> target,
			ListMultimap<String, ParameterValue> transformationParameter,
			String transformationIdentifier) {

		DefaultCell cell = new DefaultCell();
		cell.setTransformationIdentifier(transformationIdentifier);
		cell.setTransformationParameters(transformationParameter);
		cell.setSource(source);
		cell.setTarget(target);

		return cell;
	}

	/**
	 * Creates a cell for every given pair.
	 * 
	 * @param pairs Pairs of types which will be source and target of this cell
	 * @param ignoreNamespace true, if the namespace should be ignored
	 * @param structuralRename true, if properties or types should use
	 *            structuralRename/Retype
	 * @return a collection of cells
	 */
	public static Collection<MutableCell> createRetypeCellCollection(
			Collection<Pair<TypeEntityDefinition, TypeEntityDefinition>> pairs,
			boolean ignoreNamespace, boolean structuralRename) {

		Collection<MutableCell> cells = new ArrayList<MutableCell>();

		for (Pair<TypeEntityDefinition, TypeEntityDefinition> pair : pairs) {
			cells.add(createRetypeTypeCell(pair.getFirst(), pair.getSecond(), ignoreNamespace,
					structuralRename));
		}

		return cells;
	}

	/**
	 * Uses the {@link AlignmentService} to check for doubles. Create a cell for
	 * every pair, if the cell is not part of the current mapping.
	 * 
	 * @param pairs pairs of types which will be source and target of this cell
	 * @param ignoreNamespace true, if the namespace should be ignored
	 * @param structuralRename true, if properties or types should use
	 *            structuralRename/Retype
	 * @return a collection of cells
	 */
	public static Collection<MutableCell> createRetypeCellsWithoutDoubles(
			final Collection<Pair<TypeEntityDefinition, TypeEntityDefinition>> pairs,
			final boolean ignoreNamespace, final boolean structuralRename) {

		final Collection<MutableCell> cells = new ArrayList<MutableCell>();

		final Alignment align = ((AlignmentService) PlatformUI.getWorkbench().getService(
				AlignmentService.class)).getAlignment();

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				for (Pair<TypeEntityDefinition, TypeEntityDefinition> pair : pairs) {
					// check if types already mapped or not
					if (!AlignmentUtil.hasTypeRelation(align)
							|| !AlignmentUtil.hasTypeRelation(align, pair.getFirst(),
									pair.getSecond())) {
						cells.add(createRetypeTypeCell(pair.getFirst(), pair.getSecond(),
								ignoreNamespace, structuralRename));
					}
				}
			}
		});

		return cells;
	}

	/**
	 * Creates a mutable Type Cell
	 * 
	 * @param source The source TypeEntityDefinition
	 * @param target The target TypeEntityDefinition
	 * @param ignoreNamespace True indicates that the namespace will be ignored
	 * @param structuralRename True, if properties should use structuralRename
	 * @return The created MutableCell
	 */
	public static MutableCell createRetypeTypeCell(TypeEntityDefinition source,
			TypeEntityDefinition target, boolean ignoreNamespace, boolean structuralRename) {

		MutableCell cell = new DefaultCell();

		ListMultimap<String, Entity> sourceType = ArrayListMultimap.create();
		sourceType.put(null, new DefaultType(source));
		cell.setSource(sourceType);

		ListMultimap<String, Entity> targetType = ArrayListMultimap.create();
		targetType.put(null, new DefaultType(target));
		cell.setTarget(targetType);

		cell.setTransformationParameters(createParameter(ignoreNamespace, structuralRename));
		cell.setTransformationIdentifier(RetypeFunction.ID);

		return cell;
	}

	/**
	 * Uses the {@link AlignmentService} to check for doubles. Create a cell for
	 * every pair, if the cell is not part of the current mapping.
	 * 
	 * @param propertyPairs Keys are pairs of types which will be source and
	 *            target type of this cell and the properties are the values
	 * @param ignoreNamespace true, if the name space should be ignored
	 * @param structuralRename true, if properties or types should use
	 *            structuralRename/Retype
	 * 
	 * @return all rename cells
	 */
	public static Collection<MutableCell> createRenameCellCollection(
			final Map<Pair<TypeEntityDefinition, TypeEntityDefinition>, Collection<Pair<PropertyEntityDefinition, PropertyEntityDefinition>>> propertyPairs,
			boolean ignoreNamespace, boolean structuralRename) {

		boolean useStructuralRename;

		Collection<MutableCell> cells = new ArrayList<MutableCell>();

		for (Pair<TypeEntityDefinition, TypeEntityDefinition> typePair : propertyPairs.keySet()) {
			for (Pair<PropertyEntityDefinition, PropertyEntityDefinition> properties : propertyPairs
					.get(typePair)) {

				useStructuralRename = structuralRename
						&& structuralComparator(properties.getFirst().getDefinition(), properties
								.getSecond().getDefinition(), ignoreNamespace);

				cells.add(createRenameCell(properties.getFirst(), properties.getSecond(),
						ignoreNamespace, useStructuralRename));

			}
		}

		return cells;
	}

	/**
	 * Create cells without doubles means, that if there is an existing cell
	 * which this source and target property, no new cell will be created.
	 * 
	 * @param propertyPairs Collection of all pairs to create cells for
	 * @param ignoreNamespace true, if the name space should be ignored
	 * @param structuralRename true, if properties or types should use
	 *            structuralRename/Retype
	 * 
	 * @return all rename cells
	 */
	public static Collection<MutableCell> createRenameCellsWithoutDoubles(
			final Collection<Pair<PropertyEntityDefinition, PropertyEntityDefinition>> propertyPairs,
			boolean ignoreNamespace, boolean structuralRename) {

		Collection<MutableCell> cells = new ArrayList<MutableCell>();

		final Alignment align = ((AlignmentService) PlatformUI.getWorkbench().getService(
				AlignmentService.class)).getAlignment();

		for (Pair<PropertyEntityDefinition, PropertyEntityDefinition> properties : propertyPairs) {

			if (align.getCells(properties.getFirst()).isEmpty()
					&& align.getCells(properties.getSecond()).isEmpty()) {
				cells.add(createRenameCell(properties.getFirst(), properties.getSecond(),
						ignoreNamespace, structuralRename));
			}

		}

		return cells;
	}

	/**
	 * Creates a mutable Property Cell
	 * 
	 * @param sourceProp The source PropertyEntityDefinition
	 * @param targetProp The target PropertyEntityDefinition
	 * @param ignoreNamespace True indicates that the name space will be ignored
	 * @param structuralRename True, if properties should use structuralRename
	 * @return The created MutableCell
	 */
	public static MutableCell createRenameCell(PropertyEntityDefinition sourceProp,
			PropertyEntityDefinition targetProp, boolean ignoreNamespace, boolean structuralRename) {

		MutableCell cell = new DefaultCell();

		ListMultimap<String, Entity> sourceList = ArrayListMultimap.create();
		// sourceList.put(null, new DefaultType(source));
		sourceList.put(null, new DefaultProperty(sourceProp));
		cell.setSource(sourceList);

		ListMultimap<String, Entity> targetList = ArrayListMultimap.create();
		// targetType.put(null, new DefaultType(target));
		targetList.put(null, new DefaultProperty(targetProp));
		cell.setTarget(targetList);

		cell.setTransformationParameters(createParameter(ignoreNamespace, structuralRename));
		cell.setTransformationIdentifier(RenameFunction.ID);

		return cell;
	}

	/**
	 * Create a ListMultimap with the given boolean
	 * 
	 * @param ignoreNamespace True indicates that the namespace will be ignored
	 * @param structuralRename True, if properties should use structuralRename
	 * @return the parameter list
	 */
	public static ListMultimap<String, ParameterValue> createParameter(boolean ignoreNamespace,
			boolean structuralRename) {
		ListMultimap<String, ParameterValue> parameter = ArrayListMultimap.create();
		parameter.put(RenameFunction.PARAMETER_IGNORE_NAMESPACES,
				new ParameterValue(Value.of(ignoreNamespace)));
		parameter.put(RenameFunction.PARAMETER_STRUCTURAL_RENAME,
				new ParameterValue(Value.of(structuralRename)));
		return parameter;
	}

	/**
	 * Comparator, which basically compares two definitions based on their QName
	 * 
	 * @param source The source ChildDefinition
	 * @param target The target ChildDefinition
	 * @param ignoreNamespace The name space is irrelevant for types to be
	 *            compared
	 * @return true, if the two given TypeDefinitions are a match, false
	 *         otherwise
	 */
	public static boolean structuralComparator(ChildDefinition<?> source,
			ChildDefinition<?> target, boolean ignoreNamespace) {
		if (source == null || target == null) {
			return false;
		}

		// return true, if at least one child wourld be matched through
		// comparison
		if (source.asGroup() != null && target.asGroup() != null) {
			boolean identical = false;
			for (ChildDefinition<?> sourceChild : source.asGroup().getDeclaredChildren()) {
				for (ChildDefinition<?> targetChild : target.asGroup().getDeclaredChildren()) {
					identical = identical
							|| sourceChild.getName().getLocalPart()
									.equals(targetChild.getName().getLocalPart());
				}
			}
			return identical;
		}

		// return true, if at least one child would be matched through
		// comparison
		if (source.asProperty() != null && target.asProperty() != null) {
			boolean identical = false;
			for (ChildDefinition<?> sourceChild : source.getDeclaringGroup().getDeclaredChildren()) {
				for (ChildDefinition<?> targetChild : target.getDeclaringGroup()
						.getDeclaredChildren()) {
					identical = identical
							|| sourceChild.getName().getLocalPart()
									.equals(targetChild.getName().getLocalPart());
				}
			}
			return identical;
		}

		return false;
	}

}
