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
package eu.esdihumboldt.hale.ui.functions.groovy;

import eu.esdihumboldt.cst.functions.groovy.GroovyConstants;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.ui.functions.core.SourceViewerParameterPage;

/**
 * Parameter page for Groovy function.
 * 
 * @author Kai Schwierczek
 */
public class GroovyParameterPage extends SourceViewerParameterPage implements GroovyConstants {

	/**
	 * Default constructor.
	 */
	public GroovyParameterPage() {
		super("script");

		setTitle("Function parameters");
		setDescription("Enter a groovy script");

		setPageComplete(false);
	}

	/**
	 * @see SourceViewerParameterPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);
		setPageComplete(true);
	}

	/**
	 * @see SourceViewerParameterPage#getParameterName()
	 */
	@Override
	protected String getParameterName() {
		return PARAMETER_SCRIPT;
	}

	/**
	 * @see SourceViewerParameterPage#getSourcePropertyName()
	 */
	@Override
	protected String getSourcePropertyName() {
		return ENTITY_VARIABLE;
	}

	/**
	 * @see SourceViewerParameterPage#getVariableName(EntityDefinition)
	 */
	@Override
	protected String getVariableName(EntityDefinition variable) {
		// dots are not allowed in variable names, an underscore is used instead
		return super.getVariableName(variable).replace('.', '_');
	}
}
