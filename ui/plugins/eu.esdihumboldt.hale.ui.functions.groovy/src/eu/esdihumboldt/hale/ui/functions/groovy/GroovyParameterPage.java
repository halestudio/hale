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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;

import eu.esdihumboldt.cst.functions.groovy.GroovyConstants;
import eu.esdihumboldt.cst.functions.groovy.GroovyTransformation;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.PropertyValueImpl;
import eu.esdihumboldt.hale.ui.functions.core.SourceListParameterPage;
import eu.esdihumboldt.hale.ui.functions.core.SourceViewerParameterPage;
import eu.esdihumboldt.hale.ui.scripting.groovy.InstanceTestValues;
import eu.esdihumboldt.hale.ui.scripting.groovy.TestValues;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * Parameter page for Groovy function.
 * 
 * @author Kai Schwierczek
 */
public class GroovyParameterPage extends SourceViewerParameterPage implements GroovyConstants {

	private Iterable<EntityDefinition> variables;
	private final TestValues testValues;

	/**
	 * Default constructor.
	 */
	public GroovyParameterPage() {
		super("script");

		setTitle("Function parameters");
		setDescription("Enter a groovy script");

		testValues = new InstanceTestValues();
	}

	/**
	 * @see SourceViewerParameterPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		// variables may have changed
		updateState(getDocument());
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
	 * @see SourceViewerParameterPage#validate(IDocument)
	 */
	@Override
	protected boolean validate(IDocument document) {
		List<PropertyValue> values = new ArrayList<PropertyValue>();
		if (variables != null) {
			for (EntityDefinition var : variables) {
				if (var instanceof PropertyEntityDefinition) {
					PropertyEntityDefinition property = (PropertyEntityDefinition) var;
					values.add(new PropertyValueImpl(testValues.get(property), property));
				}
			}
		}
		// TODO specify classloader?
		GroovyShell shell = new GroovyShell(GroovyTransformation.createGroovyBinding(values, false));
		try {
			Script script = shell.parse(document.get());
			script.run();
		} catch (Exception e) {
			setMessage(e.getMessage(), ERROR);
			// return valid if NPE, as this might be caused by null test values
			return e instanceof NullPointerException;
//			return false;
		}

		setMessage(null);
		return true;
	}

	/**
	 * @see SourceListParameterPage#sourcePropertiesChanged(Iterable)
	 */
	@Override
	protected void sourcePropertiesChanged(Iterable<EntityDefinition> variables) {
		this.variables = variables;
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
