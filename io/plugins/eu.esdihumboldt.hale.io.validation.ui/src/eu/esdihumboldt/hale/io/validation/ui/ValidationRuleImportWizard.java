/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.validation.ui;

import eu.esdihumboldt.hale.io.validation.ValidationRuleReader;
import eu.esdihumboldt.hale.ui.io.ImportWizard;

/**
 * Import wizard for validation rules
 * 
 * @author Florian Esser
 */
public class ValidationRuleImportWizard extends ImportWizard<ValidationRuleReader> {

	/**
	 * Create a validation rule import wizard
	 */
	public ValidationRuleImportWizard() {
		super(ValidationRuleReader.class);
	}

}
