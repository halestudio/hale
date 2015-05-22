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

package eu.esdihumboldt.hale.ui.transformation;

import org.eclipse.jface.wizard.IWizard;

import eu.esdihumboldt.hale.ui.util.handler.AbstractWizardHandler;

/**
 * Show the {@link TransformDataWizard} with the source data configured in the
 * project.
 * 
 * @author Simon Templer
 */
public class TransformProjectDataHandler extends AbstractWizardHandler<IWizard> {

	/**
	 * @see AbstractWizardHandler#createWizard()
	 */
	@Override
	protected IWizard createWizard() {
		return new TransformDataWizard(true);
	}
}
