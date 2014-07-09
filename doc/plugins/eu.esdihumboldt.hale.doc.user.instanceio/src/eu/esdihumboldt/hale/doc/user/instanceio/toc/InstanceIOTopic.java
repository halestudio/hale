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

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.help.ITopic;
import org.eclipse.help.IUAElement;

import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.doc.user.instanceio.InstanceIOReferenceConstants;

/**
 * One topic for reader or writer of instances
 * 
 * @author Yasmina Kammeyer
 */
public class InstanceIOTopic implements ITopic, InstanceIOReferenceConstants {

	private final IOProviderDescriptor readerWriter;

	/**
	 * @param readerWriter The instance reader or instance writer
	 */
	public InstanceIOTopic(IOProviderDescriptor readerWriter) {
		super();

		this.readerWriter = readerWriter;
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
		// readerWriter.getIdentifier(); <- not allowed due to restrictions
		return "/" + PLUGIN_ID + "/" + INSTANCEIO_TOPIC_PATH + readerWriter.getIdentifier()
				+ ".html";
	}

	/**
	 * @see org.eclipse.help.IHelpResource#getLabel()
	 */
	@Override
	public String getLabel() {
		// readerWriter.getDisplayName(); <- not allowed due to restrictions
		return readerWriter.getDisplayName();
	}

	/**
	 * @see org.eclipse.help.ITopic#getSubtopics()
	 */
	@Override
	public ITopic[] getSubtopics() {
		// TODO Auto-generated method stub
		return NO_TOPICS;
	}

}
