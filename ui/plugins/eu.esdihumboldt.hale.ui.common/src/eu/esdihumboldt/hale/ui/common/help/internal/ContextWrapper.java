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

package eu.esdihumboldt.hale.ui.common.help.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.help.ICommandLink;
import org.eclipse.help.IContext;
import org.eclipse.help.IContext2;
import org.eclipse.help.IContext3;
import org.eclipse.help.IHelpResource;

/**
 * Context that wraps a given context and provides additional topics.
 * 
 * @author Simon Templer
 */
public class ContextWrapper implements IContext3 {

	private final IContext context;
	private final IHelpResource[] topics;

	/**
	 * Create a wrapper for the given context.
	 * 
	 * @param context the wrapped context
	 * @param additionalTopics the additional topics
	 */
	public ContextWrapper(IContext context, Collection<IHelpResource> additionalTopics) {
		this.context = context;

		List<IHelpResource> topics = new ArrayList<IHelpResource>();
		for (IHelpResource topic : context.getRelatedTopics()) {
			topics.add(topic);
		}
		topics.addAll(additionalTopics);
		this.topics = topics.toArray(new IHelpResource[topics.size()]);
	}

	/**
	 * @see IContext3#getRelatedCommands()
	 */
	@Override
	public ICommandLink[] getRelatedCommands() {
		if (context instanceof IContext3) {
			return ((IContext3) context).getRelatedCommands();
		}
		return new ICommandLink[0];
	}

	/**
	 * @see IContext2#getTitle()
	 */
	@Override
	public String getTitle() {
		if (context instanceof IContext2) {
			return ((IContext2) context).getTitle();
		}
		return null;
	}

	/**
	 * @see IContext2#getStyledText()
	 */
	@Override
	public String getStyledText() {
		if (context instanceof IContext2) {
			return ((IContext2) context).getStyledText();
		}
		return getText(); // XXX the right thing to return?
	}

	/**
	 * @see IContext2#getCategory(IHelpResource)
	 */
	@Override
	public String getCategory(IHelpResource topic) {
		if (context instanceof IContext2) {
			return ((IContext2) context).getCategory(topic);
		}
		return null;
	}

	/**
	 * @see IContext#getRelatedTopics()
	 */
	@Override
	public IHelpResource[] getRelatedTopics() {
		return topics;
	}

	/**
	 * @see IContext#getText()
	 */
	@Override
	public String getText() {
		return context.getText();
	}

}
