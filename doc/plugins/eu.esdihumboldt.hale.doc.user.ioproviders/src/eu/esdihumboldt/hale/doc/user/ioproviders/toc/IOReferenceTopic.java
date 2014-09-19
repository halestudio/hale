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

package eu.esdihumboldt.hale.doc.user.ioproviders.toc;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.help.ITopic;
import org.eclipse.help.IUAElement;

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.doc.user.ioproviders.IOReferenceConstants;

/**
 * Table of contents for I/O provider reference.
 * 
 * @author Yasmina Kammeyer
 */
public class IOReferenceTopic implements ITopic, IOReferenceConstants {

	private ITopic[] readerWriterTopics;
	private final String name;
	private final Class<? extends IOProvider> providerClass;

	/**
	 * Default constructor
	 * 
	 * @param name the topic name
	 * @param providerClass the I/O provider class
	 */
	public IOReferenceTopic(String name, Class<? extends IOProvider> providerClass) {
		super();
		this.name = name;
		this.providerClass = providerClass;
	}

	@Override
	public boolean isEnabled(IEvaluationContext context) {
		return true;
	}

	@Override
	public IUAElement[] getChildren() {
		return getSubtopics();
	}

	@Override
	public String getHref() {
		return "/" + PLUGIN_ID + "/" + OVERVIEW_TOPIC_PATH + providerClass.getSimpleName()
				+ ".html";
	}

	@Override
	public String getLabel() {
		return name;
	}

	@Override
	public ITopic[] getSubtopics() {
		if (readerWriterTopics == null) {
			// build subtopic with reader and writer
			Collection<ITopic> topics = new ArrayList<>();
			for (IOProviderDescriptor io : HaleIO.getProviderFactories(providerClass)) {
				topics.add(new IOProviderTopic(io));
			}
			if (topics.isEmpty()) {
				readerWriterTopics = NO_TOPICS;
			}
			else
				readerWriterTopics = topics.toArray(new ITopic[topics.size()]);
		}
		return readerWriterTopics;
	}

	/**
	 * @return the provider class
	 */
	public Class<? extends IOProvider> getProviderClass() {
		return providerClass;
	}
}
