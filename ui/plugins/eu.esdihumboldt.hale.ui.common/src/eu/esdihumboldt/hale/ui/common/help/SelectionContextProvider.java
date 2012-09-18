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

package eu.esdihumboldt.hale.ui.common.help;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.help.IHelpResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;

import eu.esdihumboldt.hale.ui.common.help.internal.ContextImpl;
import eu.esdihumboldt.hale.ui.common.help.internal.ContextWrapper;

/**
 * Context provider providing a context for the selection (if possible).
 * 
 * @author Simon Templer
 */
public abstract class SelectionContextProvider implements IContextProvider {

	private final ISelectionProvider selectionProvider;

	private final String defaultContextId;

	/**
	 * Create a context provider
	 * 
	 * @param selectionProvider the selection provider to use to retrieve the
	 *            selection
	 * @param defaultContextId the ID of the default context that is
	 *            supplemented with the selection contexts, may be
	 *            <code>null</code>
	 */
	public SelectionContextProvider(ISelectionProvider selectionProvider, String defaultContextId) {
		super();
		this.selectionProvider = selectionProvider;
		this.defaultContextId = defaultContextId;
	}

	/**
	 * @see IContextProvider#getContextChangeMask()
	 */
	@Override
	public int getContextChangeMask() {
		return SELECTION;
	}

	/**
	 * @see IContextProvider#getContext(Object)
	 */
	@Override
	public IContext getContext(Object target) {
		// provide a context based on the selection
		ISelection selection = selectionProvider.getSelection();

		IContext defaultContext = null;
		if (defaultContextId != null) {
			defaultContext = HelpSystem.getContext(defaultContextId);
		}

		List<IContext> contexts = new ArrayList<IContext>();
		if (selection instanceof IStructuredSelection) {
			for (Object object : ((IStructuredSelection) selection).toList()) {
				IContext context = getSelectionContext(object);
				if (context != null) {
					contexts.add(context);
				}
			}
		}

		if (contexts.size() == 1) {
			if (defaultContext == null) {
				return contexts.get(0);
			}
			else {
				// create context enhanced with default topics
				return new ContextWrapper(contexts.get(0), Arrays.asList(defaultContext
						.getRelatedTopics()));
			}
		}
		else if (!contexts.isEmpty()) {
			LinkedHashSet<IHelpResource> topics = new LinkedHashSet<IHelpResource>();
			Set<String> hrefs = new HashSet<String>();

			// collect topics
			for (IContext context : contexts) {
				for (IHelpResource topic : context.getRelatedTopics()) {
					if (!hrefs.contains(topic.getHref())) { // ensure that the
															// same topic is
															// only added once
						topics.add(topic);
						hrefs.add(topic.getHref());
					}
				}
			}

			if (!topics.isEmpty()) {
				if (defaultContext == null) {
					return new ContextImpl(
							"Multiple selected objects, see below for related topics.", // XXX
																						// improve?!
							topics.toArray(new IHelpResource[topics.size()]));
				}
				else {
					// create a context enhanced with the selection topics
					return new ContextWrapper(defaultContext, topics);
				}
			}
		}

		// by default, get the view context
		return defaultContext;
	}

	/**
	 * Get the context for the given selected object.
	 * 
	 * @param object the selected object
	 * @return the associated context or <code>null</code>
	 */
	protected IContext getSelectionContext(Object object) {
		String contextId = getContextId(object);

		if (contextId != null) {
			return HelpSystem.getContext(contextId);
		}

		return null;
	}

	/**
	 * Get the context ID for the given selected object.
	 * 
	 * @param object the selected object
	 * @return the context ID or <code>null</code> if none is available
	 */
	protected abstract String getContextId(Object object);

	/**
	 * @see IContextProvider#getSearchExpression(Object)
	 */
	@Override
	public String getSearchExpression(Object target) {
		// override me
		return null;
	}

}
