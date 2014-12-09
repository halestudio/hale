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

package eu.esdihumboldt.hale.common.autocorrelation;

import eu.esdihumboldt.hale.common.schema.model.Definition;

/**
 * Interface of the comparator used for AutoCorrelation. Override the function,
 * so it returns true or false based on your implementation. Used in
 * {@link AutoCorrelation} it will influence if a pair will be created or not.
 * 
 * @author Yasmina Kammeyer
 */
public interface AutoCorrelationComparatorObj {

	/**
	 * Implement this method. Comparator, which compares two Definitions.
	 * AutoCorrelation will call this method on various Definitions, so you can
	 * differ between TypeDefinitions, ChildDefinition.
	 * 
	 * @param source The source Definition, may be null
	 * @param target The target Definition, may be null
	 * @param ignoreNamespace The name space is irrelevant for types to be
	 *            compared
	 * @return true, if the two given Definitions are a match, false otherwise.
	 */
	public boolean comparator(Definition<?> source, Definition<?> target, boolean ignoreNamespace);
}
