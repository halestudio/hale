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
 *     GeoSolutions <https://www.geo-solutions.it>
 */

package eu.esdihumboldt.hale.io.appschema.model;

import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Strings;

/**
 * Class holding metadata about a single workspace.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class WorkspaceMetadata implements Comparable<WorkspaceMetadata> {

	private final String defaultName;
	private String name;
	private final String namespace;
	private boolean isolated;
	private Set<String> featureTypes;

	/**
	 * Constructor.
	 * 
	 * <p>
	 * Both arguments must be not null nor empty.
	 * </p>
	 * 
	 * @param defaultName the default workspace name, i.e. the one that would be
	 *            picked by the mapping generator
	 * @param namespace the namespace URI associated to the workspace
	 */
	public WorkspaceMetadata(String defaultName, String namespace) {
		if (Strings.isNullOrEmpty(defaultName)) {
			throw new IllegalArgumentException("Workspace default name must be provided");
		}
		if (Strings.isNullOrEmpty(namespace)) {
			throw new IllegalArgumentException("Workspace namespace must be provided");
		}
		this.defaultName = defaultName;
		this.name = this.defaultName;
		this.namespace = namespace;
		this.isolated = false;
	}

	/**
	 * @return the default workspace name
	 */
	public String getDefaultName() {
		return defaultName;
	}

	/**
	 * @return the namespace URI
	 */
	public String getNamespaceUri() {
		return namespace;
	}

	/**
	 * @return the actual workspace name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the workspace name to set
	 */
	public void setName(String name) {
		if (Strings.isNullOrEmpty(name)) {
			throw new IllegalArgumentException("Workspace name cannot be empty");
		}
		this.name = name;
	}

	/**
	 * Tests if the currently set name matches the default name.
	 * 
	 * @return {@code true} if the current name is equal to the default name,
	 *         {@code false} otherwise
	 */
	public boolean hasDefaultName() {
		return name.equals(defaultName);
	}

	/**
	 * The value of the {@code isolated} attribute. By default is is
	 * {@code false}.
	 * 
	 * @return {@code true} if the workspace is isolated, {@code false}
	 *         otherwise
	 */
	public boolean isIsolated() {
		return isolated;
	}

	/**
	 * Sets the value of the {@code isolated} attribute.
	 * 
	 * @param isolated {@code true} if the workspace is isolated, {@code false}
	 *            otherwise
	 */
	public void setIsolated(boolean isolated) {
		this.isolated = isolated;
	}

	/**
	 * Returns the names of the feature types which belong to the workspace.
	 * 
	 * <p>
	 * The returned collection is a reference to the one held internally, so it
	 * is intended to be modified by client code.
	 * </p>
	 * 
	 * @return the names of the feature types belonging to the workspace
	 */
	public Set<String> getFeatureTypes() {
		if (featureTypes == null) {
			featureTypes = new TreeSet<>();
		}
		return featureTypes;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(WorkspaceMetadata o) {
		if (o == null) {
			return -1;
		}
		return getName().compareToIgnoreCase(o.getName());
	}

}
