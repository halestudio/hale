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

package eu.esdihumboldt.hale.ui.function.custom;

import eu.esdihumboldt.hale.common.align.extension.function.custom.impl.DefaultCustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyTransformation;
import eu.esdihumboldt.hale.ui.function.custom.pages.CustomPropertyFunctionEntitiesPage;

/**
 * TODO Type description
 * 
 * @author simon
 */
public class CustomPropertyFunctionWizard
		extends
		AbstractGenericCustomFunctionWizard<DefaultCustomPropertyFunction, PropertyTransformation<?>> {

	private CustomPropertyFunctionEntitiesPage entityPage;

	/**
	 * Default constructor.
	 */
	public CustomPropertyFunctionWizard() {
		super(null);
	}

	@Override
	protected DefaultCustomPropertyFunction createCustomFunction() {
		return new DefaultCustomPropertyFunction();
	}

	@Override
	public void addPages() {
		super.addPages();

		entityPage = new CustomPropertyFunctionEntitiesPage();
		addPage(entityPage);
	}

}
