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
