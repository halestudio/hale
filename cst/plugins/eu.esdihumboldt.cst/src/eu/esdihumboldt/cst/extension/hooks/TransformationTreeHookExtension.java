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

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractExtension;
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
