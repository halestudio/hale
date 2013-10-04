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

package eu.esdihumboldt.hale.server.templates.war.pages;

import eu.esdihumboldt.hale.server.templates.war.components.TemplateForm;
import eu.esdihumboldt.hale.server.webapp.util.PageDescription;

/**
 * Page that is shown to the user after a template has initially been created.
 * 
 * @author Simon Templer
 */
@PageDescription(title = "New Template")
public class NewTemplatePage extends TemplatesBasePage {

	private static final long serialVersionUID = 1062587755750029869L;

	/**
	 * Constructor
	 * 
	 * @param templateId the identifier of the template just created
	 */
	public NewTemplatePage(String templateId) {
		super();

		add(new TemplateForm("edit-form", true, templateId));
	}

}
