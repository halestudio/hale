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

import java.util.ArrayList;
import java.util.Collection;

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
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.Value;
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
	 * @param pairs pairs of types which will be source and target of this cell
	 * @param ignoreNamespace true, if the namespace should be ignored
	 * @param structuralRename true, if properties or types should use
	 *            structuralRename/Retype
	 * @return a collection of cells
	 */
	public static Collection<MutableCell> createTypeCellRetypeCollection(
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
	 * @param pairs pairs of types which will be source and target of this cell
	 * @param ignoreNamespace true, if the namespace should be ignored
	 * @param structuralRename true, if properties or types should use
	 *            structuralRename/Retype
	 * @param alignment The alignment which should be checked for existing type
	 *            cells
	 * @param log The log to at a info to, if a type mapping between two types
	 *            already exists. Can be null and nothing will be logged
	 * @return a collection of cells
	 */
	public static Collection<MutableCell> createTypeCellRetypeCollectionWithoutDoubles(
			final Collection<Pair<TypeEntityDefinition, TypeEntityDefinition>> pairs,
			final boolean ignoreNamespace, final boolean structuralRename, Alignment alignment) {

		final Collection<MutableCell> cells = new ArrayList<MutableCell>();

		final Alignment align = alignment;

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
//					else {
//						if (log != null)
//							log.info("Existing type mapping between source: " + pair.getFirst()
//									+ " and target: " + pair.getSecond());
//					}
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

}
