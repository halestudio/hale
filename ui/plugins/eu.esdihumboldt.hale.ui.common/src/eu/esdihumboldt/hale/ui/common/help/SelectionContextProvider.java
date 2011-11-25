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

package eu.esdihumboldt.hale.ui.common.help;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.help.IHelpResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * Context provider providing a context for the selection (if possible).
 * @author Simon Templer 
 */
public abstract class SelectionContextProvider implements IContextProvider {

	private final ISelectionProvider selectionProvider;
	
	private final IContext defaultContext;
	
	/**
	 * @param selectionProvider
	 * @param defaultContext
	 */
	public SelectionContextProvider(ISelectionProvider selectionProvider,
			IContext defaultContext) {
		super();
		this.selectionProvider = selectionProvider;
		this.defaultContext = defaultContext;
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
			return contexts.get(0);
		}
		else if (!contexts.isEmpty()) {
			LinkedHashSet<IHelpResource> topics = new LinkedHashSet<IHelpResource>();
			
			// collect topics
			for (IContext context : contexts) {
				for (IHelpResource topic : context.getRelatedTopics()) {
					topics.add(topic);
				}
			}
			
			return new ContextImpl(
					"Selection", //XXX improve?! 
					topics.toArray(new IHelpResource[topics.size()]));
		}
		
		// by default, get the view context
		return defaultContext;
	}

	/**
	 * Get the context for the given selected object.
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
	 * @param object the selected object
	 * @return the context ID or <code>null</code> if none is available
	 */
	protected abstract String getContextId(Object object);

	/**
	 * @see IContextProvider#getSearchExpression(Object)
	 */
	@Override
	public String getSearchExpression(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

}
