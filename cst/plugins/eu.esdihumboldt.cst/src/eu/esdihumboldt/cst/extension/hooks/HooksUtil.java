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

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHook.TreeState;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;

/**
 * Hooks utility methods.
 * 
 * @author Simon Templer
 */
public class HooksUtil {

	private static final ALogger log = ALoggerFactory.getLogger(HooksUtil.class);

	/**
	 * Execute transformation tree hooks according to the given state.
	 * 
	 * @param hooks the hooks, may be <code>null</code>
	 * @param state the tree state
	 * @param tree the transformation tree
	 * @param target the target instance
	 */
	public static void executeTreeHooks(TransformationTreeHooks hooks, TreeState state,
			TransformationTree tree, MutableInstance target) {
		if (hooks != null) {
			try {
				for (TransformationTreeHook hook : hooks.getActiveObjects()) {
					TransformationTreeHookFactory def = hooks.getDefinition(hook);

					if (state == def.getTreeState()) {
						try {
							hook.processTransformationTree(tree, state, target);
						} catch (Exception e) {
							log.error(
									"Error processing transformation tree hook "
											+ def.getDisplayName(), e);
						}
					}
				}
			} catch (Exception e) {
				log.error("Error trying to execute transformation tree hooks", e);
			}
		}
	}

}
