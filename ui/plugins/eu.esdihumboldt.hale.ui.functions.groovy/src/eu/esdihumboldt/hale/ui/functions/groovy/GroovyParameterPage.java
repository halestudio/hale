package eu.esdihumboldt.hale.ui.functions.groovy;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.ui.functions.core.SourceViewerParameterPage;

/**
 * Parameter page for Groovy function.
 * 
 * @author Kai Schwierczek
 */
public class GroovyParameterPage extends SourceViewerParameterPage {

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
		return "script";
	}

	/**
	 * @see SourceViewerParameterPage#getSourcePropertyName()
	 */
	@Override
	protected String getSourcePropertyName() {
		return "var";
	}

	/**
	 * @see SourceViewerParameterPage#getVariableName(EntityDefinition)
	 */
	@Override
	protected String getVariableName(EntityDefinition variable) {
		// TODO Auto-generated method stub
		return super.getVariableName(variable).replace('.', '_');
	}
}
