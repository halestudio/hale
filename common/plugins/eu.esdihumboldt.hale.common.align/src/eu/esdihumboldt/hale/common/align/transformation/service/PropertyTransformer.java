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

package eu.esdihumboldt.hale.common.align.transformation.service;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;

/**
 * Executes property transformations on source/target instance pairs.
 * 
 * @author Simon Templer
 */
public interface PropertyTransformer {

	/**
	 * Publish a source/target instance pair for property transformation.
	 * 
	 * @param source the source instances
	 * @param target the target instance
	 * @param typeLog the type transformation log
	 * @param typeCell the type cell
	 */
	public void publish(FamilyInstance source, MutableInstance target, TransformationLog typeLog,
			Cell typeCell);

	/**
	 * Join with the property transformer and wait for its completion, e.g. if
	 * the property transformer executes tasks in worker threads.
	 * 
	 * @param cancel if still pending transformations should be canceled
	 */
	void join(boolean cancel);

}
