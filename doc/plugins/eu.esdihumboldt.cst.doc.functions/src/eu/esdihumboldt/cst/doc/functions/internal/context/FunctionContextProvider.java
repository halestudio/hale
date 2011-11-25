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

package eu.esdihumboldt.cst.doc.functions.internal.context;

import org.apache.commons.codec.DecoderException;
import org.eclipse.help.AbstractContextProvider;
import org.eclipse.help.IContext;

import eu.esdihumboldt.cst.doc.functions.FunctionReferenceConstants;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.instance.model.impl.ONameUtil;

/**
 * Provides contexts related to functions
 * @author Simon Templer
 */
public class FunctionContextProvider extends AbstractContextProvider implements FunctionReferenceConstants {

	/**
	 * Default constructor
	 */
	public FunctionContextProvider() {
		super();
	}

	/**
	 * @see AbstractContextProvider#getContext(String, String)
	 */
	@Override
	public IContext getContext(String contextId, String locale) {
		// It is not possible to use dots (.) in the context id to identify the
		// function, because it will be treated as part of the bundle name. So
		// the function identifier is encoded using ONameUtil 
		int index = contextId.lastIndexOf('.');
		String pluginId = contextId.substring(0, index);
		String shortContextId = contextId.substring(index + 1);
		
		if (pluginId.equals(PLUGIN_ID)) {
			try {
				String functionId = ONameUtil.decodeName(shortContextId);
				AbstractFunction<?> function = FunctionUtil.getFunction(functionId);
				if (function != null) {
					//XXX
				}
			} catch (DecoderException e) {
				// no valid function ID
			}
		}
		
		return null;
	}

	/**
	 * @see AbstractContextProvider#getPlugins()
	 */
	@Override
	public String[] getPlugins() {
		// provides only contexts for this bundle
		return new String[]{PLUGIN_ID};
	}

}
