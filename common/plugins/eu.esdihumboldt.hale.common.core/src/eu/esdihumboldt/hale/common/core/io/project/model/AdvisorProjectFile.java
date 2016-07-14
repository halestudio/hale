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

package eu.esdihumboldt.hale.common.core.io.project.model;

import eu.esdihumboldt.hale.common.core.io.IOAdvisorRegister;

/**
 * Project file that uses an advisor register.
 * 
 * @author Simon Templer
 */
public interface AdvisorProjectFile extends ProjectFile {

	/**
	 * Set the I/O advisor register.
	 * 
	 * @param register the I/O advisor register
	 */
	public void setAdvisorRegister(IOAdvisorRegister register);

}
