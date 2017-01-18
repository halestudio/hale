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

package eu.esdihumboldt.hale.ui.functions.core;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;

/**
 * Parameter page for formatted string function.
 * 
 * @author Kai Schwierczek
 */
public class FormattedStringParameterPage extends TextSourceListParameterPage {

	/**
	 * Default constructor.
	 */
	public FormattedStringParameterPage() {
		super("pattern");

		setTitle("Function parameters");
		setDescription("Enter a pattern. Press Ctrl+Space for content assistance.");

		setPageComplete(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.functions.core.TextSourceListParameterPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);
		setPageComplete(true);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.functions.core.TextSourceListParameterPage#getParameterName()
	 */
	@Override
	protected String getParameterName() {
		return "pattern";
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.functions.core.TextSourceListParameterPage#getSourcePropertyName()
	 */
	@Override
	protected String getSourcePropertyName() {
		return "var";
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.functions.core.TextSourceListParameterPage#getVariableName(eu.esdihumboldt.hale.common.align.model.EntityDefinition)
	 */
	@Override
	protected String getVariableName(EntityDefinition variable) {
		return '{' + super.getVariableName(variable) + '}';
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.functions.core.TextSourceListParameterPage#useMultilineInput()
	 */
	@Override
	protected boolean useMultilineInput() {
		return true;
	}

}
