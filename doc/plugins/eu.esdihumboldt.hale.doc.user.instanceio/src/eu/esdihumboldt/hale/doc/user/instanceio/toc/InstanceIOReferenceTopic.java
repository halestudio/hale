/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.doc.user.instanceio.toc;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.help.ITopic;
import org.eclipse.help.IUAElement;

import eu.esdihumboldt.hale.doc.user.instanceio.InstanceIOReferenceConstants;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;

/**
 * Table of contents for the instance IO reference
 * 
 * @author Yasmina Kammeyer
 */
public class InstanceIOReferenceTopic implements ITopic, InstanceIOReferenceConstants {

	private ITopic[] readerWriterTopics;

	/**
	 * Default constructor
	 */
	public InstanceIOReferenceTopic() {
		super();
		// create topics
	}

	/**
	 * @see org.eclipse.help.IUAElement#isEnabled(org.eclipse.core.expressions.IEvaluationContext)
	 */
	@Override
	public boolean isEnabled(IEvaluationContext context) {
		return true;
	}

	/**
	 * @see org.eclipse.help.IUAElement#getChildren()
	 */
	@Override
	public IUAElement[] getChildren() {
		return getSubtopics();
	}

	/**
	 * @see org.eclipse.help.IHelpResource#getHref()
	 */
	@Override
	public String getHref() {
		return "/" + PLUGIN_ID + "/" + INSTANCEIO_TOPIC_PATH + INSTANCEIO_OVERVIEW_PATH;
	}

	/**
	 * @see org.eclipse.help.IHelpResource#getLabel()
	 */
	@Override
	public String getLabel() {
		return "InstanceIO";
	}

	/**
	 * @see org.eclipse.help.ITopic#getSubtopics()
	 */
	@Override
	public ITopic[] getSubtopics() {
		if (readerWriterTopics == null) {
			// build subtopic with reader and writer
			Collection<ITopic> topics = new ArrayList<>();
			for (IOProviderDescriptor io : HaleIO.getProviderFactories(InstanceReader.class)) {
				topics.add(new InstanceIOTopic(io));
			}
			for (IOProviderDescriptor io : HaleIO.getProviderFactories(InstanceWriter.class)) {
				topics.add(new InstanceIOTopic(io));
			}
			if (topics.isEmpty()) {
				readerWriterTopics = NO_TOPICS;
			}
			else
				readerWriterTopics = topics.toArray(new ITopic[topics.size()]);
		}
		// return NO_TOPICS;
		return readerWriterTopics;
	}
}
