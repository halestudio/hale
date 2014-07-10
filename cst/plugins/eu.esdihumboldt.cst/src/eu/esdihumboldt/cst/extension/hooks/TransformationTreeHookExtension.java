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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.extension.hooks;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHook.TreeState;
import eu.esdihumboldt.cst.extension.hooks.internal.DefaultTreeHookFactory;

/**
 * Extension for {@link TransformationTreeHook}s
 * 
 * @author Simon Templer
 */
public class TransformationTreeHookExtension extends
		AbstractExtension<TransformationTreeHook, TransformationTreeHookFactory> {

	/**
	 * The extension ID
	 */
	public static final String EXTENSION_ID = "eu.esdihumboldt.cst.instancetransformation";

	/**
	 * Default constructor
	 */
	public TransformationTreeHookExtension() {
		super(EXTENSION_ID);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected TransformationTreeHookFactory createFactory(IConfigurationElement conf)
			throws Exception {
		String hookname = conf.getName();

		TreeState state = null;
		if ("minimal-tree".equals(hookname)) {
			state = TreeState.MINIMAL;
		}
		if ("source-tree".equals(hookname)) {
			state = TreeState.SOURCE_POPULATED;
		}
		if ("full-tree".equals(hookname)) {
			state = TreeState.FULL;
		}

		if (state != null) {
			return new DefaultTreeHookFactory(conf, state);
		}
		return null;
	}

}
