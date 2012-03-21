/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.functions.core;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;

/**
 * Parameter page for formatted string function.
 *
 * @author Kai Schwierczek
 */
public class FormattedStringParameterPage extends SourceListParameterPage {
	/**
	 * Default constructor.
	 */
	public FormattedStringParameterPage() {
		super("pattern");

		setTitle("Function parameters");
		setDescription("Enter a pattern");

		setPageComplete(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.functions.core.SourceListParameterPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);
		setPageComplete(true);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.functions.core.SourceListParameterPage#getParameterName()
	 */
	@Override
	protected String getParameterName() {
		return "pattern";
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.functions.core.SourceListParameterPage#getSourcePropertyName()
	 */
	@Override
	protected String getSourcePropertyName() {
		return "var";
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.functions.core.SourceListParameterPage#getVariableName(eu.esdihumboldt.hale.common.align.model.EntityDefinition)
	 */
	@Override
	protected String getVariableName(EntityDefinition variable) {
		return '{' + super.getVariableName(variable) + '}';
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.functions.core.SourceListParameterPage#useMultilineInput()
	 */
	@Override
	protected boolean useMultilineInput() {
		return true;
	}
	
}
