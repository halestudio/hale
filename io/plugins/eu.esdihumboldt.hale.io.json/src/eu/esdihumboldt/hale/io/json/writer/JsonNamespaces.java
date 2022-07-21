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

import java.util.Optional;

/**
 * Namespace used in generating JSON from instances.
 * 
 * @author Simon Templer
 */
public abstract class JsonNamespaces implements NamespaceManager, InstanceJsonConstants {

	/**
	 * Constructor.
	 */
	public JsonNamespaces() {
		super();

		addNamespace(NAMESPACE_INSTANCE_JSON, Optional.of(PREFIX_INSTANCE_JSON));
	}

}
