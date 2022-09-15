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

/**
 * Groovy Merge script configuration page.
 * 
 * @author Simon Templer
 */
public class GroovyMergePage extends GroovyRetypePage {

	/**
	 * Default constructor
	 */
	public GroovyMergePage() {
		super();

		setDescription(
				"Specify a Groovy script to build a target instance from a merged source instance");
	}

	@Override
	public String getHelpContext() {
		return "eu.esdihumboldt.cst.functions.groovy.merge";
	}

}
