/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.wizards.functions.math;

import java.util.Set;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

import com.iabcinc.jmep.Environment;
import com.iabcinc.jmep.Expression;
import com.iabcinc.jmep.hooks.Constant;

/**
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 *
 */
public class MathExpressionFieldEditor extends StringFieldEditor {
	
	private final Set<String> variables; 

	/**
	 * Constructor
	 * 
	 * @param name the field name
	 * @param labelText the label text 
	 * @param parent the parent composite
	 * @param variables the variables that can be used in an expression
	 */
	public MathExpressionFieldEditor(String name, String labelText,
			Composite parent, Set<String> variables) {
		super(name, labelText, parent);
		
		this.variables = variables;
		
		setValidateStrategy(VALIDATE_ON_KEY_STROKE);
	}

	/**
	 * @see StringFieldEditor#doCheckState()
	 */
	@Override
	protected boolean doCheckState() {
		String expression = getStringValue();
		
		Environment env = new Environment();
		for (String var : variables) {
			// add dummy variable
			env.addVariable(var, new Constant(new Double(1)));
		}

		try {
			Expression ex = new Expression(expression, env);
			ex.evaluate();
			
			setErrorMessage(null);
			
			return true;
		} catch (Throwable e) {
			String message = e.getLocalizedMessage();
			if (message != null && !message.isEmpty()) {
				setErrorMessage(e.getLocalizedMessage());
			}
			else {
				setErrorMessage("Invalid variable");
			}
			
			return false;
		}
	}

	/**
	 * Insert a value at the current caret position in the expression
	 * 
	 * @param value the value to insert
	 */
	public void insert(String value) {
		getTextControl().insert(value);
		refreshValidState();
	}

}
