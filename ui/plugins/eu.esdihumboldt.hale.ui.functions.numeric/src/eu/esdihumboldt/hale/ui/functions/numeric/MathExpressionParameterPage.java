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
package eu.esdihumboldt.hale.ui.functions.numeric;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

import com.iabcinc.jmep.Environment;
import com.iabcinc.jmep.Expression;
import com.iabcinc.jmep.hooks.Constant;

import eu.esdihumboldt.cst.functions.numeric.MathematicalExpressionFunction;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.ui.functions.core.TextSourceListParameterPage;
import eu.esdihumboldt.hale.ui.transformation.TransformationVariableReplacer;

/**
 * Parameter page for mathematical expression function.
 * 
 * @author Kai Schwierczek
 */
public class MathExpressionParameterPage extends TextSourceListParameterPage
		implements MathematicalExpressionFunction {

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
	 * @see TextSourceListParameterPage#getParameterName()
	 */
	@Override
	protected String getParameterName() {
		return PARAMETER_EXPRESSION;
	}

	/**
	 * @see TextSourceListParameterPage#getSourcePropertyName()
	 */
	@Override
	protected String getSourcePropertyName() {
		return ENTITY_VARIABLE;
	}

	/**
	 * @see TextSourceListParameterPage#configure(Text)
	 */
	@Override
	protected void configure(final Text textField) {
		super.configure(textField);
		this.textField = textField;
		final TransformationVariableReplacer replacer = new TransformationVariableReplacer();
		textField.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				try {
					String exprStr = textField.getText();
					exprStr = replacer.replaceVariables(exprStr);
					Expression ex = new Expression(exprStr, environment);
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
	 * @see TextSourceListParameterPage#sourcePropertiesChanged(Iterable)
	 */
	@Override
	protected void sourcePropertiesChanged(Iterable<EntityDefinition> variables) {
		super.sourcePropertiesChanged(variables);

		// update environment
		environment = new Environment();
		for (EntityDefinition variable : variables) {
			environment.addVariable(getVariableName(variable), new Constant(new Double(1)));
		}

		// re set text to get modify event
		textField.setText(textField.getText());
	}
}
