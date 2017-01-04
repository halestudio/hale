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

import eu.esdihumboldt.hale.io.validation.ValidatorConfigurationReader;
import eu.esdihumboldt.hale.ui.io.ImportWizard;

/**
 * Import wizard for validator configurations
 * 
 * @author Florian Esser
 */
public class ValidatorConfigurationImportWizard extends ImportWizard<ValidatorConfigurationReader> {

	/**
	 * Creates a validation configuration import wizard
	 */
	public ValidatorConfigurationImportWizard() {
		super(ValidatorConfigurationReader.class);
	}

}
