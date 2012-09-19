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

package eu.esdihumboldt.hale.ui.io.project;

import org.eclipse.jface.resource.ImageDescriptor;

import eu.esdihumboldt.hale.common.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Base class for {@link ProjectReader} configuration pages. All configurations
 * for {@link ProjectReader}s must be optional to support loading projects with
 * only a file given.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public abstract class OpenProjectConfigurationPage extends
		AbstractConfigurationPage<ProjectReader, OpenProjectWizard> {

	/**
	 * @see AbstractConfigurationPage#AbstractConfigurationPage(String, String,
	 *      ImageDescriptor)
	 */
	protected OpenProjectConfigurationPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * @see AbstractConfigurationPage#AbstractConfigurationPage(String)
	 */
	protected OpenProjectConfigurationPage(String pageName) {
		super(pageName);
	}

}
