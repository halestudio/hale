/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.codelist.service;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.common.codelist.config.CodeListReference;

/**
 * Service providing information on code lists.
 * 
 * @author Simon Templer
 */
public interface CodeListRegistry {

	/**
	 * Tries to find the code list based on the given code list identification
	 * reference.
	 * 
	 * @param clRef the code list reference
	 * 
	 * @return the code list or <code>null</code>
	 */
	@Nullable
	public CodeList findCodeList(CodeListReference clRef);

}
