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

package eu.esdihumboldt.hale.ui.io.align;

import org.eclipse.jface.resource.ImageDescriptor;

import eu.esdihumboldt.hale.common.align.io.AlignmentWriter;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Base class for {@link AlignmentWriter} configuration pages
 * 
 * @author Simon Templer
 * @since 2.5
 */
public abstract class AlignmentWriterConfigurationPage extends
		AbstractConfigurationPage<AlignmentWriter, AlignmentExportWizard> {

	/**
	 * @see AbstractConfigurationPage#AbstractConfigurationPage(String, String,
	 *      ImageDescriptor)
	 */
	protected AlignmentWriterConfigurationPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * @see AbstractConfigurationPage#AbstractConfigurationPage(String)
	 */
	protected AlignmentWriterConfigurationPage(String pageName) {
		super(pageName);
	}

}
