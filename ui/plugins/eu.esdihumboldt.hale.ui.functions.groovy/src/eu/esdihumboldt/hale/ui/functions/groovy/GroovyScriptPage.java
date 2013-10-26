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

import org.eclipse.jface.text.source.SourceViewerConfiguration;

import eu.esdihumboldt.cst.functions.groovy.GroovyConstants;
import eu.esdihumboldt.hale.ui.util.groovy.ColorManager;
import eu.esdihumboldt.hale.ui.util.groovy.SimpleGroovySourceViewerConfiguration;
import eu.esdihumboldt.hale.ui.util.groovy.IColorManager;

/**
 * Base page for editing a Groovy script for type relations.
 * 
 * @author Simon Templer
 */
public class GroovyScriptPage extends SourceViewerPage {

	private IColorManager colorManager;

	/**
	 * Default constructor.
	 */
	public GroovyScriptPage() {
		super("groovyScript", GroovyConstants.PARAMETER_SCRIPT, GroovyConstants.BINDING_TARGET
				+ " = {\n\n}");
	}

	@Override
	protected SourceViewerConfiguration createConfiguration() {
		if (colorManager == null) {
			colorManager = new ColorManager();
		}
		return new SimpleGroovySourceViewerConfiguration(colorManager);
	}

	@Override
	public void dispose() {
		colorManager.dispose();

		super.dispose();
	}

}
