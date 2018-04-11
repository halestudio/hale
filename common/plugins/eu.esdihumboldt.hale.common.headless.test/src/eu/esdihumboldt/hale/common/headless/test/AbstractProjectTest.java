/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.common.headless.test;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import eu.esdihumboldt.hale.common.cli.HaleCLIUtil;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.headless.impl.ProjectTransformationEnvironment;

/**
 * Base class for tests working with loaded transformation projects.
 * 
 * @author Simon Templer
 */
public abstract class AbstractProjectTest {

	/**
	 * Cache of loaded projects
	 */
	protected final LoadingCache<URI, ProjectTransformationEnvironment> projectCache = CacheBuilder
			.newBuilder().build(new CacheLoader<URI, ProjectTransformationEnvironment>() {

				@Override
				public ProjectTransformationEnvironment load(URI key) throws Exception {
					return loadProject(key);
				}

			});

	/**
	 * Load a transformation project.
	 * 
	 * @param uri the project location
	 * @return the loaded project
	 * @throws IOException if loading the project fails
	 */
	private static ProjectTransformationEnvironment loadProject(URI uri) throws IOException {
		return new ProjectTransformationEnvironment(null, new DefaultInputSupplier(uri),
				HaleCLIUtil.createReportHandler());
	}

	/**
	 * Get a loaded transformation project.
	 * 
	 * @param location the project location
	 * @return the loaded project
	 * @throws Exception if loading the project fails
	 */
	protected ProjectTransformationEnvironment getProject(URL location) throws Exception {
		return projectCache.get(location.toURI());
	}

}
