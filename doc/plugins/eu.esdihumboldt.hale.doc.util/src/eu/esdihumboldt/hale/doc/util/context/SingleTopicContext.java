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

package eu.esdihumboldt.hale.doc.util.context;

import org.eclipse.help.IContext;
import org.eclipse.help.IContext2;
import org.eclipse.help.IHelpResource;

/**
 * A context with a single topic.
 * 
 * @author Simon Templer
 */
public class SingleTopicContext implements IContext2 {

	private final IHelpResource topic;
	private final String title;
	private final String description;

	/**
	 * Create a context
	 * 
	 * @param title the title, may be <code>null</code>
	 * @param description the context description
	 * @param topic the context topic
	 */
	public SingleTopicContext(String title, String description, IHelpResource topic) {
		this.title = title;
		this.description = description;
		this.topic = topic;
	}

	/**
	 * @see IContext#getRelatedTopics()
	 */
	@Override
	public IHelpResource[] getRelatedTopics() {
		return new IHelpResource[] { topic };
	}

	/**
	 * @see IContext#getText()
	 */
	@Override
	public String getText() {
		return description;
	}

	/**
	 * @see IContext2#getTitle()
	 */
	@Override
	public String getTitle() {
		return title;
	}

	/**
	 * @see IContext2#getStyledText()
	 */
	@Override
	public String getStyledText() {
		return getText();
	}

	/**
	 * @see IContext2#getCategory(IHelpResource)
	 */
	@Override
	public String getCategory(IHelpResource topic) {
		return null;
	}

}
