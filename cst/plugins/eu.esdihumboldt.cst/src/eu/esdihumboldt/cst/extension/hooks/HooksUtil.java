/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.cst.extension.hooks;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
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
			for (TransformationTreeHook hook : hooks.getActiveObjects()) {
				TransformationTreeHookFactory def = hooks.getDefinition(hook);

				if (state == def.getTreeState()) {
					try {
						hook.processTransformationTree(tree, state, target);
					} catch (Exception e) {
						log.error(
								"Error processing transformation tree hook " + def.getDisplayName(),
								e);
					}
				}
			}
		}
	}

}
