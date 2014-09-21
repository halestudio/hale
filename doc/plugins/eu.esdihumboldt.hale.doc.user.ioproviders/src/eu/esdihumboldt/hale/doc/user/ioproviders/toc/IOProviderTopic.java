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

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.help.ITopic;
import org.eclipse.help.IUAElement;

import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.doc.user.ioproviders.IOReferenceConstants;

/**
 * One topic for reader or writer of instances
 * 
 * @author Yasmina Kammeyer
 */
public class IOProviderTopic implements ITopic, IOReferenceConstants {

	private final IOProviderDescriptor readerWriter;

	/**
	 * @param readerWriter The instance reader or instance writer
	 */
	public IOProviderTopic(IOProviderDescriptor readerWriter) {
		super();

		this.readerWriter = readerWriter;
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
		// readerWriter.getIdentifier(); <- not allowed due to restrictions
		return "/" + PLUGIN_ID + "/" + IO_PROVIDERS_TOPIC_PATH + readerWriter.getIdentifier()
				+ ".html";
	}

	@Override
	public String getLabel() {
		// readerWriter.getDisplayName(); <- not allowed due to restrictions
		return readerWriter.getDisplayName();
	}

	@Override
	public ITopic[] getSubtopics() {
		return NO_TOPICS;
	}

}
