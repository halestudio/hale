/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.templates.contribution;

import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.templates.extension.ProjectTemplate;

/**
 * Action that loads a template project.
 * 
 * @author Simon Templer
 */
public class LoadTemplateAction extends Action {

	private static final ALogger log = ALoggerFactory.getLogger(LoadTemplateAction.class);

	private final ProjectTemplate template;

	/**
	 * Create an action to load the given template.
	 * 
	 * @param template the project template
	 */
	public LoadTemplateAction(ProjectTemplate template) {
		super(template.getName(), AS_PUSH_BUTTON);

		this.template = template;

		URL iconUrl = template.getIconURL();
		if (iconUrl != null) {
			setImageDescriptor(ImageDescriptor.createFromURL(iconUrl));
		}
	}

	@Override
	public void run() {
		super.run();

		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		try {
			ps.load(template.getLocation().getLocation());
		} catch (URISyntaxException e) {
			log.error("Invalid project template location", e);
		}
	}

}
