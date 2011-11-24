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

package eu.esdihumboldt.cst.doc.functions.internal.toc;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.help.IHelpResource;
import org.eclipse.help.IToc;
import org.eclipse.help.ITopic;
import org.eclipse.help.IUAElement;

import com.google.common.base.Objects;

/**
 * Wraps a topic in an {@link IToc}
 * @author Simon Templer
 */
public class OneTopicToc implements IToc {
	
	private final ITopic topic;

	/**
	 * Create a TOC wrapping a topic.
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
		return new ITopic[]{topic};
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
