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

package eu.esdihumboldt.cst.doc.functions.internal.context;

import java.text.MessageFormat;

import org.apache.commons.codec.DecoderException;
import org.eclipse.help.AbstractContextProvider;
import org.eclipse.help.IContext;

import eu.esdihumboldt.cst.doc.functions.FunctionReferenceConstants;
import eu.esdihumboldt.cst.doc.functions.internal.toc.FunctionTopic;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.doc.util.context.SingleTopicContext;

/**
 * Provides contexts related to functions
 * 
 * @author Simon Templer
 */
public class FunctionContextProvider extends AbstractContextProvider
		implements FunctionReferenceConstants {

	/**
	 * Context function topic
	 */
	public class ContextFunctionTopic extends FunctionTopic {

		/**
		 * @see FunctionTopic#FunctionTopic(FunctionDefinition)
		 */
		public ContextFunctionTopic(FunctionDefinition<?> function) {
			super(function);
		}

		/**
		 * @see FunctionTopic#getLabel()
		 */
		@Override
		public String getLabel() {
			return MessageFormat.format("{0} (Function Reference)", super.getLabel());
		}

	}

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
				FunctionDefinition<?> function = FunctionUtil.getFunction(functionId, null);
				if (function != null) {
					FunctionTopic topic = new ContextFunctionTopic(function);
					String description = function.getDescription();
					if (description == null) {
						description = function.getDisplayName();
					}
					return new SingleTopicContext(function.getDisplayName(), description, topic);
					// XXX add more info to context (e.g. title?)
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
		return new String[] { PLUGIN_ID };
	}

}
