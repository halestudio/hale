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

package eu.esdihumboldt.hale.ui.functions.groovy;

import eu.esdihumboldt.cst.functions.groovy.GroovyConstants;

/**
 * Base page for editing a Groovy script for type relations.
 * 
 * @author Simon Templer
 */
public class GroovyScriptPage extends SourceViewerPage {

	/**
	 * Default constructor.
	 */
	public GroovyScriptPage() {
		super("groovyScript", GroovyConstants.PARAMETER_SCRIPT, GroovyConstants.BINDING_TARGET
				+ " = {\n\n}");
	}

}
