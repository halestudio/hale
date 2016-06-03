/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.functions.custom;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionDefinition;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyTransformation;
import eu.esdihumboldt.hale.ui.functions.custom.pages.CustomFunctionExplanationPage;
import eu.esdihumboldt.hale.ui.functions.custom.pages.CustomPropertyFunctionEntitiesPage;
import eu.esdihumboldt.hale.ui.functions.custom.pages.MainPage;
import eu.esdihumboldt.hale.ui.functions.custom.pages.PropertyFunctionScriptPage;

/**
 * TODO Type description
 * 
 * @author Simon Templer
 */
public class CustomPropertyFunctionWizard extends
		AbstractGenericCustomFunctionWizard<DefaultCustomPropertyFunction, PropertyFunctionDefinition, PropertyTransformation<?>> {

	private CustomPropertyFunctionEntitiesPage entityPage;
	private PropertyFunctionScriptPage scriptPage;
	private CustomFunctionExplanationPage explanationPage;

	/**
	 * Default constructor
	 */
	public CustomPropertyFunctionWizard() {
		this(null);
	}

	/**
	 * Create a wizard based on an existing function.
	 * 
	 * @param function the function, may be <code>null</code>
	 */
	public CustomPropertyFunctionWizard(@Nullable DefaultCustomPropertyFunction function) {
		super(function);
	}

	@Override
	protected DefaultCustomPropertyFunction createCustomFunction(
			DefaultCustomPropertyFunction org) {
		if (org == null) {
			return new DefaultCustomPropertyFunction();
		}
		else {
			return new DefaultCustomPropertyFunction(org);
		}
	}

	@Override
	public void addPages() {
		super.addPages();

		addPage(new MainPage());

		// variables page
		entityPage = new CustomPropertyFunctionEntitiesPage();
		addPage(entityPage);

		// TODO parameters page

		// script page
		scriptPage = new PropertyFunctionScriptPage();
		addPage(scriptPage);

		// explanation page
		explanationPage = new CustomFunctionExplanationPage();
		addPage(explanationPage);
	}

}
