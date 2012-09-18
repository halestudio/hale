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
