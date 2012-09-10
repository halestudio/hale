package eu.esdihumboldt.hale.ui.functions.numeric;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

import com.iabcinc.jmep.Environment;
import com.iabcinc.jmep.Expression;
import com.iabcinc.jmep.hooks.Constant;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.ui.functions.core.SourceListParameterPage;

/**
 * Parameter page for mathematical expression function.
 * 
 * @author Kai Schwierczek
 */
public class MathExpressionParameterPage extends SourceListParameterPage {

	private Environment environment = new Environment();
	private Text textField;

	/**
	 * Default constructor.
	 */
	public MathExpressionParameterPage() {
		super("expression");

		setTitle("Function parameters");
		setDescription("Enter a mathematical expression");

		setPageComplete(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.functions.core.SourceListParameterPage#getParameterName()
	 */
	@Override
	protected String getParameterName() {
		return "expression";
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.functions.core.SourceListParameterPage#getSourcePropertyName()
	 */
	@Override
	protected String getSourcePropertyName() {
		return "var";
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.functions.core.SourceListParameterPage#configure(org.eclipse.swt.widgets.Text)
	 */
	@Override
	protected void configure(final Text textField) {
		super.configure(textField);
		this.textField = textField;
		textField.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				try {
					Expression ex = new Expression(textField.getText(), environment);
					ex.evaluate();
					setErrorMessage(null);
					setPageComplete(true);
				} catch (Exception ex) {
					String message = ex.getLocalizedMessage();
					if (message != null && !message.isEmpty())
						setErrorMessage(ex.getLocalizedMessage());
					else
						setErrorMessage("Invalid variable");
					setPageComplete(false);
				}
			}
		});
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.functions.core.SourceListParameterPage#sourcePropertiesChanged(eu.esdihumboldt.hale.common.align.model.EntityDefinition[])
	 */
	@Override
	protected void sourcePropertiesChanged(EntityDefinition[] variables) {
		super.sourcePropertiesChanged(variables);

		// update environment
		environment = new Environment();
		for (EntityDefinition variable : variables)
			environment.addVariable(getVariableName(variable), new Constant(new Double(1)));

		// re set text to get modify event
		textField.setText(textField.getText());
	}
}
