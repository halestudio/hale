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

package eu.esdihumboldt.hale.io.appschema.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class holding configuration metadata for a group of workspaces.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class WorkspaceConfiguration {

	private final Map<String, WorkspaceMetadata> workspaceMap;

	/**
	 * Default constructor.
	 */
	public WorkspaceConfiguration() {
		this.workspaceMap = new TreeMap<>();
	}

	/**
	 * Removes all configuration data.
	 */
	public void clear() {
		workspaceMap.clear();
	}

	/**
	 * Adds metadata about a single workspace to the configuration.
	 * 
	 * <p>
	 * Note that internally the workspace metadata are stored in a dictionary
	 * with namespace URIs as keys, so if another workspace metadata object with
	 * the same namespace URI as the one passed to
	 * {@link #addWorkspace(WorkspaceMetadata)} is already present in the
	 * configuration, it will be replaced by it.
	 * </p>
	 * 
	 * @param workspace the workspace metadata
	 */
	public void addWorkspace(WorkspaceMetadata workspace) {
		if (workspace != null) {
			workspaceMap.put(workspace.getNamespaceUri(), workspace);
		}
	}

	/**
	 * Returns the metadata object stored in the configuration for the specified
	 * workspace (if any exists).
	 * 
	 * @param namespaceUri the namespace URI of the workspace whose metadata
	 *            should be retrieved
	 * @return the workspace metadata object
	 */
	public WorkspaceMetadata getWorkspace(String namespaceUri) {
		return workspaceMap.get(namespaceUri);
	}

	/**
	 * Returns all metadata object that are currently stored in the
	 * configuration.
	 * 
	 * <p>
	 * The returned collection is a copy of the collection held internally.
	 * </p>
	 * 
	 * @return all the workspace metadata objects in the configuration
	 */
	public List<WorkspaceMetadata> getWorkspaces() {
		List<WorkspaceMetadata> workspaces = new ArrayList<>(workspaceMap.values());
		return workspaces;
	}

	/**
	 * Checks whether metadata exist for a specific workspace.
	 * 
	 * @param namespaceUri the namespace URI of the workspace
	 * @return {@code true} if metadata exist for the specified workspace,
	 *         {@code false} otherwise
	 */
	public boolean hasWorkspace(String namespaceUri) {
		return workspaceMap.get(namespaceUri) != null;
	}

	/**
	 * Removes the metadata object for the specified workspace from the
	 * configuration (if any exists).
	 * 
	 * @param namespaceUri the namespace URI of the workspace whose metadata
	 *            should be removed
	 * @return the removed workspace metadata, or {@code null} if none was found
	 */
	public WorkspaceMetadata removeWorkspace(String namespaceUri) {
		return workspaceMap.remove(namespaceUri);
	}
}
