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

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;

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
	 * @param transformationParameter
	 * @param transformationIdentifier
	 * @return
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

}
