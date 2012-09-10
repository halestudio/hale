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

import org.eclipse.help.IContext;
import org.eclipse.help.IHelpResource;

/**
 * Context implementation
 * 
 * @author Simon Templer
 */
public class ContextImpl implements IContext {

	private final String description;
	private final IHelpResource[] relatedTopics;

	/**
	 * Create a context
	 * 
	 * @param description the description
	 * @param relatedTopics the related topics
	 */
	public ContextImpl(String description, IHelpResource[] relatedTopics) {
		super();
		this.description = description;
		this.relatedTopics = relatedTopics;
	}

	/**
	 * @see IContext#getRelatedTopics()
	 */
	@Override
	public IHelpResource[] getRelatedTopics() {
		return relatedTopics;
	}

	/**
	 * @see IContext#getText()
	 */
	@Override
	public String getText() {
		return description;
	}

}
