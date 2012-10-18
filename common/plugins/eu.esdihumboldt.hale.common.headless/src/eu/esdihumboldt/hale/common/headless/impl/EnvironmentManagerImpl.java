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

package eu.esdihumboldt.hale.common.headless.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import eu.esdihumboldt.hale.common.headless.EnvironmentManager;
import eu.esdihumboldt.hale.common.headless.TransformationEnvironment;

/**
 * Manages the set of active {@link TransformationEnvironment}s.
 * 
 * @author Simon Templer
 */
public class EnvironmentManagerImpl implements EnvironmentManager {

	private final Map<String, TransformationEnvironment> envs = new TreeMap<String, TransformationEnvironment>();

	@Override
	public Collection<TransformationEnvironment> getEnvironments() {
		synchronized (envs) {
			return new ArrayList<TransformationEnvironment>(envs.values());
		}
	}

	@Override
	public TransformationEnvironment getEnvironment(String id) {
		synchronized (envs) {
			return envs.get(id);
		}
	}

	@Override
	public void addEnvironment(TransformationEnvironment environment) {
		synchronized (envs) {
			envs.put(environment.getId(), environment);
		}
	}

	@Override
	public void removeEnvironment(String id) {
		synchronized (envs) {
			envs.remove(id);
		}
	}

}
