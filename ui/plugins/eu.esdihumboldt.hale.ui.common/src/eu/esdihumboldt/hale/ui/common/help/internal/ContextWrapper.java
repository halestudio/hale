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
