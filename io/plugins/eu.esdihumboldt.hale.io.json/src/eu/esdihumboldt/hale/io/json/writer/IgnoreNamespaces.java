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
 * Class to ignore the Namespace prefixes in the JSON or GeoJSON exported file
 * formats.
 * 
 * @author Kapil Agnihotri
 */
public class IgnoreNamespaces implements NamespaceManager {

	/**
	 * 
	 * This method will not set any prefix to the namespace and will simply do
	 * nothing when called
	 * 
	 * @see eu.esdihumboldt.hale.io.json.writer.NamespaceManager#setPrefix(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void setPrefix(String namespace, String prefix) {
		// Nothing to set here as we are alwaysd returning an empty prefix
	}

	/**
	 * This is the overridden method and will always return an empty string for
	 * the prefix
	 * 
	 * @see eu.esdihumboldt.hale.io.json.writer.NamespaceManager#getPrefix(java.lang.String)
	 */
	@Override
	public String getPrefix(String namespace) {
		// Ignore the prefix and return empty String
		return "";
	}

	/**
	 * This method will not add any desired prefix to the namespace and will
	 * ignore the call
	 * 
	 * @see eu.esdihumboldt.hale.io.json.writer.NamespaceManager#addNamespace(java.lang.String,
	 *      java.util.Optional)
	 */
	@Override
	public String addNamespace(String namespace, Optional<String> desiredPrefix) {
		// Do not add anything as we are ignoring the Namespace prefixes and
		// return null
		return null;
	}

	/**
	 * This method will not add any prefix to the namespace and will ignore the
	 * call
	 *
	 * @param prefixes a map of namespaces to prefixes
	 * 
	 * @see eu.esdihumboldt.hale.io.json.writer.NamespaceManager#addPrefixes(java.util.Map)
	 */
	@Override
	public void addPrefixes(Map<String, String> prefixes) {
		// do nothing
	}

	/**
	 * Return null as we don't want to add any namespace. The caller should
	 * handle null checks.
	 * 
	 * @see eu.esdihumboldt.hale.io.json.writer.NamespaceManager#getNamespaces()
	 */
	@Override
	public Map<String, String> getNamespaces() {
		return null;
	}

	/**
	 * Return null as we don't want to add any namespace. The caller should
	 * handle null checks.
	 * 
	 * @see eu.esdihumboldt.hale.io.json.writer.NamespaceManager#getNamespace(java.lang.String)
	 */
	@Override
	public Optional<String> getNamespace(String prefix) {
		return null;
	}

}
