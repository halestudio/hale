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

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;
import eu.esdihumboldt.hale.common.core.io.IOProvider;

/**
 * Extension point for {@link IOProvider} aliases.
 * 
 * @author Simon Templer
 */
public class IOProviderAliasExtension extends IdentifiableExtension<IOProviderAlias> {

	/**
	 * The extension instance.
	 */
	public static final IOProviderAliasExtension INSTANCE = new IOProviderAliasExtension();

	/**
	 * Default constructor.
	 */
	private IOProviderAliasExtension() {
		super(IOProviderExtension.ID);
	}

	@Override
	protected IOProviderAlias create(String id, IConfigurationElement conf) {
		if ("alias".equals(conf.getName())) {
			return new IOProviderAlias(id, conf);
		}
		else {
			return null;
		}
	}

	@Override
	protected String getIdAttributeName() {
		return "id";
	}

}