/*
 * Copyright (c) 2022 wetransform GmbH
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

package eu.esdihumboldt.hale.io.json.writer;

import java.util.Map;
import java.util.Optional;

/**
 * Abstract class to manage Namespaces. This class can be further extended to
 * manage and include namespace prefixes in the exported Json or geoJson
 * formats. Not Thread safe.
 * 
 * @author Kapil Agnihotri
 */
public interface NamespaceManager {

	/**
	 * Get the namespaces and their prefixes.
	 *
	 * @return the map of namespace prefix to namespace
	 */
	public Map<String, String> getNamespaces();

	/**
	 * get Namespace
	 * 
	 * @param prefix prefix
	 * @return namespace.
	 */
	public Optional<String> getNamespace(String prefix);

	/**
	 * Set the given prefix for the given namespace. If another namespace is
	 * associated to the prefix, it will be reassigned.
	 *
	 * Thus this should only be done before any prefix information is used.
	 *
	 * @param namespace the namespace to set the prefix for
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String namespace, String prefix);

	/**
	 * Get the prefix associated to the given namespace. Associates a new prefix
	 * if none was associated before.
	 *
	 * @param namespace the namespace
	 * @return the prefix
	 */
	public String getPrefix(String namespace);

	/**
	 * Add a namespace.
	 *
	 * @param namespace the namespace to add
	 * @param desiredPrefix the desired prefix, if any
	 * @return the prefix used for the namespace
	 */
	public String addNamespace(String namespace, Optional<String> desiredPrefix);

	/**
	 * Add namespace prefixes from a map. Tries to reuse the given prefixes
	 * where possible.
	 *
	 * @param prefixes a map of namespaces to prefixes
	 */
	public void addPrefixes(Map<String, String> prefixes);

}
