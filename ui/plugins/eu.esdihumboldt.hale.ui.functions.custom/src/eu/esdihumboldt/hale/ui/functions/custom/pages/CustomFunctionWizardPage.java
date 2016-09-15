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

package eu.esdihumboldt.hale.ui.functions.custom.pages;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * Function wizard page interface
 * 
 * @author Simon Templer
 */
public interface CustomFunctionWizardPage extends IWizardPage {

	/**
	 * Configure the parent wizard's custom function.
	 */
	public abstract void apply();

}
