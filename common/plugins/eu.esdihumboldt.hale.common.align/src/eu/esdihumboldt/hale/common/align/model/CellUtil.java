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

package eu.esdihumboldt.hale.common.align.model;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.service.FunctionService;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

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
	 * @return the parameter value or a parameter value representing
	 *         <code>null</code>
	 */
	public static ParameterValue getFirstParameter(Cell cell, String parameterName) {
		ParameterValue result = null;
		ListMultimap<String, ParameterValue> params = cell.getTransformationParameters();
		if (params != null) {
			List<ParameterValue> values = params.get(parameterName);
			if (values != null && !values.isEmpty()) {
				result = values.get(0);
			}
		}

		if (result == null) {
			return ParameterValue.NULL;
		}
		return result;
	}

	/**
	 * Get the first parameter with the given name in the given cell.
	 * 
	 * @param cell the cell
	 * @param parameterName the parameter name
	 * @param defaultValue the default value to return if the parameter is not
	 *            specified
	 * @return the raw parameter value or <code>null</code>
	 */
	public static ParameterValue getOptionalParameter(Cell cell, String parameterName,
			Value defaultValue) {
		ListMultimap<String, ParameterValue> params = cell.getTransformationParameters();
		if (params != null) {
			List<ParameterValue> values = params.get(parameterName);
			if (values != null && !values.isEmpty()) {
				// XXX should also be checked if the parameter value is empty?
				return values.get(0);
			}
		}

		return new ParameterValue(defaultValue);
	}

	/**
	 * Get a short description of a cell.
	 * 
	 * @param cell the cell
	 * @param serviceProvider the service provider for retrieving the function
	 *            service, may be <code>null</code>
	 * @return the cell description
	 */
	public static String getCellDescription(Cell cell, @Nullable ServiceProvider serviceProvider) {
		StringBuffer result = new StringBuffer();

		// include function name if possible
		String functionId = cell.getTransformationIdentifier();
		eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition<?> function = null;
		if (serviceProvider != null) {
			FunctionService fs = serviceProvider.getService(FunctionService.class);
			if (fs != null) {
				function = fs.getFunction(functionId);
			}
		}
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

	/**
	 * Get the default documentation entry of a cell.
	 * 
	 * @param cell the cell
	 * @return the cell notes or <code>null</code>
	 */
	public static String getNotes(Cell cell) {
		List<String> docs = cell.getDocumentation().get(null);
		if (docs.isEmpty() || docs.get(0) == null) {
			return null;
		}
		else {
			return docs.get(0);
		}
	}

	/**
	 * Set the default documentation entry of a cell.
	 * 
	 * @param cell the cell
	 * @param notes the cell notes
	 */
	public static void setNotes(Cell cell, String notes) {
		cell.getDocumentation().removeAll(null);
		if (!notes.isEmpty()) {
			cell.getDocumentation().put(null, notes);
		}
	}

}
