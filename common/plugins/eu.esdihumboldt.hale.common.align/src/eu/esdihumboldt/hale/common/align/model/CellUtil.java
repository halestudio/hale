/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.model;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;

/**
 * Cell related utility methods.
 * 
 * @author Simon Templer
 */
public abstract class CellUtil {

	/**
	 * Get the first entity from the given entities map (as contained e.g. as
	 * source or target in a cell).
	 * 
	 * @param entities the entities map
	 * @return first entity or <code>null</code> if there is none
	 */
	public static Entity getFirstEntity(ListMultimap<String, ? extends Entity> entities) {
		if (entities == null || entities.isEmpty()) {
			return null;
		}
		return entities.entries().iterator().next().getValue();
	}

	/**
	 * Get the first parameter with the given name in the given cell.
	 * 
	 * @param cell the cell
	 * @param parameterName the parameter name
	 * @return the parameter value or <code>null</code>
	 */
	public static String getFirstParameter(Cell cell, String parameterName) {
		ListMultimap<String, String> params = cell.getTransformationParameters();
		if (params != null) {
			List<String> values = params.get(parameterName);
			if (values != null && !values.isEmpty()) {
				return values.get(0);
			}
		}

		return null;
	}

	/**
	 * Get a short description of a cell.
	 * 
	 * @param cell the cell
	 * @return the cell description
	 */
	public static String getCellDescription(Cell cell) {
		StringBuffer result = new StringBuffer();

		// include function name if possible
		String functionId = cell.getTransformationIdentifier();
		AbstractFunction<?> function = FunctionUtil.getFunction(functionId);
		if (function != null) {
			result.append(function.getDisplayName());
			result.append(": ");
		}

		if (cell.getSource() != null) {
			result.append(entitiesText(cell.getSource().values()));
			result.append(" to ");
		}
		result.append(entitiesText(cell.getTarget().values()));

		return result.toString();
	}

	private static String entitiesText(Collection<? extends Entity> entities) {
		return Joiner.on(", ").join(
				Collections2.transform(entities, new Function<Entity, String>() {

					@Override
					public String apply(Entity input) {
						return input.getDefinition().getDefinition().getDisplayName();
					}
				}));
	}

}
