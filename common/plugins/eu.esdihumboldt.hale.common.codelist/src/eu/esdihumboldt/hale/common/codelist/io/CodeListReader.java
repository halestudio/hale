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

package eu.esdihumboldt.hale.common.codelist.io;

import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;

/**
 * Reads a code list
 * 
 * @author Patrick Lieb
 */
public interface CodeListReader extends ImportProvider {

	/**
	 * The action identifier.
	 */
	public static final String ACTION_ID = "eu.esdihumboldt.hale.codelist.read";

	/**
	 * @return the imported CodeList
	 */
	public CodeList getCodeList();
}
