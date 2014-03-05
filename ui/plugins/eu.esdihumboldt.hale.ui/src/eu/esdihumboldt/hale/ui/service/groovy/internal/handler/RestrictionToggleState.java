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

package eu.esdihumboldt.hale.ui.service.groovy.internal.handler;

import org.eclipse.core.commands.State;

import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService;
import eu.esdihumboldt.util.groovy.sandbox.GroovyServiceListener;

/**
 * Command state that represents if the groovy restriction is enabled.
 * 
 * @author Kai Schwierczek
 */
public class RestrictionToggleState extends State {

	private final GroovyServiceListener groovyServiceListener;

	/**
	 * Default constructor.
	 */
	public RestrictionToggleState() {
		GroovyService gs = HaleUI.getServiceProvider().getService(GroovyService.class);
		gs.addListener(groovyServiceListener = new GroovyServiceListener() {

			@Override
			public void restrictionChanged(boolean restrictionActive) {
				setValue(restrictionActive);
			}
		});
		setValue(gs.isRestrictionActive());
	}

	@Override
	public void dispose() {
		GroovyService gs = HaleUI.getServiceProvider().getService(GroovyService.class);
		gs.removeListener(groovyServiceListener);
	}
}
