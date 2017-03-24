/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.common.core.io.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;

/**
 * {@link ComplexValueDefinition} alias.
 * 
 * @author Simon Templer
 */
public class ComplexValueAlias implements Identifiable {

	private final String id;
	private final String ref;

	/**
	 * Create a new {@link ComplexValueDefinition} alias.
	 * 
	 * @param id the alias identifier/name
	 * @param conf the configuration element defining the alias
	 */
	public ComplexValueAlias(String id, IConfigurationElement conf) {
		this.id = id;
		this.ref = conf.getAttribute("for");
	}

	@Override
	public String getId() {
		return id;
	}

	/**
	 * @return the referenced value constraint
	 */
	public String getRef() {
		return ref;
	}

}