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

package eu.esdihumboldt.hale.ui.service.groovy;

import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.util.groovy.sandbox.DefaultGroovyService;

/**
 * Groovy service utilizing preferences to save project restriction exceptions.
 * 
 * @author Kai Schwierczek
 */
public class PreferencesGroovyService extends DefaultGroovyService {

	/**
	 * @param projectService
	 * @param alignmentService
	 */
	public PreferencesGroovyService(ProjectService projectService, AlignmentService alignmentService) {
		// TODO
	}

	/**
	 * @see eu.esdihumboldt.util.groovy.sandbox.DefaultGroovyService#setRestrictionActive(boolean)
	 */
	@Override
	public void setRestrictionActive(boolean active) {
		// TODO Auto-generated method stub
		super.setRestrictionActive(active);
	}

	/**
	 * @see eu.esdihumboldt.util.groovy.sandbox.DefaultGroovyService#isRestrictionActive()
	 */
	@Override
	public boolean isRestrictionActive() {
		// TODO Auto-generated method stub
		return super.isRestrictionActive();
	}
}
