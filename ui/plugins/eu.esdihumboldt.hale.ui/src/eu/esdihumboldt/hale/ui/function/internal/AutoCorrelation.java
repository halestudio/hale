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

import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;

/**
 * Class to create Retype and Rename cells for multiple sources. It is used to
 * create mappings based on matching between source and target types.
 * 
 * @author Yasmina Kammeyer
 */
public class AutoCorrelation {

	/**
	 * Creates only retype cells
	 * 
	 * @param sourceAndTarget
	 * @param transformationParameter
	 * @param ignoreInherited
	 */
	public void retype(SchemaSelection sourceAndTarget,
			ListMultimap<String, ParameterValue> transformationParameter, boolean ignoreInherited) {
		// TODO
	}

	/**
	 * Creates only rename cells
	 * 
	 * @param sourceAndTarget
	 * @param transformationParameter
	 * @param ignoreInherited
	 */
	public void rename(SchemaSelection sourceAndTarget,
			ListMultimap<String, ParameterValue> transformationParameter, boolean ignoreInherited) {
		// TODO
	}

	/**
	 * 
	 * @param sourceAndTarget
	 * @param transformationParameter
	 * @param ignoreInherited
	 */
	public void retypeAndRename(SchemaSelection sourceAndTarget,
			ListMultimap<String, ParameterValue> transformationParameter, boolean ignoreInherited) {
		// TODO
	}

}
