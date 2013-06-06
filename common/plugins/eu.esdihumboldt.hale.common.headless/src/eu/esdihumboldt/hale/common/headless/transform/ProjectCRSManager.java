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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.headless.transform;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.instance.geometry.CRSProvider;
import eu.esdihumboldt.hale.common.instance.geometry.impl.AbstractCRSManager;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;

/**
 * CRS manager that retrieves information about configured CRS from a project.
 * 
 * @author Simon Templer
 */
public class ProjectCRSManager extends AbstractCRSManager {

	private final Project project;

	/**
	 * Create a CRS manager based on the given project.
	 * 
	 * @param reader the instance reader
	 * @param provider the CRS provider
	 * @param project the project
	 * 
	 * @see AbstractCRSManager#AbstractCRSManager(InstanceReader, CRSProvider)
	 */
	public ProjectCRSManager(InstanceReader reader, CRSProvider provider, Project project) {
		super(reader, provider);
		this.project = project;
	}

	/**
	 * @see AbstractCRSManager#storeValue(String, String)
	 */
	@Override
	protected void storeValue(String key, String value) {
		project.getProperties().put(key, Value.of(value));
	}

	/**
	 * @see AbstractCRSManager#loadValue(String)
	 */
	@Override
	protected String loadValue(String key) {
		Value value = project.getProperties().get(key);
		if (value != null) {
			return value.as(String.class);
		}
		return null;
	}

}
