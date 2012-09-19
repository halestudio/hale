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

package eu.esdihumboldt.hale.doc.util.toc;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.help.IHelpResource;
import org.eclipse.help.IToc;
import org.eclipse.help.ITopic;
import org.eclipse.help.IUAElement;

import com.google.common.base.Objects;

/**
 * Wraps a topic in an {@link IToc}
 * 
 * @author Simon Templer
 */
public class OneTopicToc implements IToc {

	private final ITopic topic;

	/**
	 * Create a TOC wrapping a topic.
	 * 
	 * @param topic the topic
	 */
	public OneTopicToc(ITopic topic) {
		super();
		this.topic = topic;
	}

	/**
	 * @see IUAElement#isEnabled(IEvaluationContext)
	 */
	@Override
	public boolean isEnabled(IEvaluationContext context) {
		return true;
	}

	/**
	 * @see IUAElement#getChildren()
	 */
	@Override
	public IUAElement[] getChildren() {
		return getTopics();
	}

	/**
	 * @see IHelpResource#getHref()
	 */
	@Override
	public String getHref() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see IHelpResource#getLabel()
	 */
	@Override
	public String getLabel() {
		return topic.getLabel();
	}

	/**
	 * @see IToc#getTopics()
	 */
	@Override
	public ITopic[] getTopics() {
		return new ITopic[] { topic };
	}

	/**
	 * @see IToc#getTopic(String)
	 */
	@Override
	public ITopic getTopic(String href) {
		if (Objects.equal(topic.getHref(), href)) {
			return topic;
		}

		return null;
	}

}
