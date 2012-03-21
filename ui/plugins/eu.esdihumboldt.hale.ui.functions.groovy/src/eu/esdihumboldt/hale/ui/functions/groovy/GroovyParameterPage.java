package eu.esdihumboldt.hale.ui.functions.groovy;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.ui.functions.core.SourceListParameterPage;

/**
 * Parameter page for Groovy function.
 * 
 * @author Kai Schwierczek
 */
public class GroovyParameterPage extends SourceListParameterPage {
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
		return "script";
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.functions.core.SourceListParameterPage#getSourcePropertyName()
	 */
	@Override
	protected String getSourcePropertyName() {
		return "var";
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.functions.core.SourceListParameterPage#useMultilineInput()
	 */
	@Override
	protected boolean useMultilineInput() {
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.functions.core.SourceListParameterPage#getVariableName(eu.esdihumboldt.hale.common.align.model.EntityDefinition)
	 */
	@Override
	protected String getVariableName(EntityDefinition variable) {
		// TODO Auto-generated method stub
		return super.getVariableName(variable).replace('.', '_');
	}
}
