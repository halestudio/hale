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

package eu.esdihumboldt.hale.io.wfs.ui.legacy.wizard;

import eu.esdihumboldt.hale.io.wfs.ui.internal.Messages;

/**
 * WFS DescribeFeature wizard
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class WfsDescribeFeatureWizard extends AbstractWfsWizard<WfsDescribeFeatureConfiguration> {

	/**
	 * @see AbstractWfsWizard#AbstractWfsWizard(WfsConfiguration)
	 */
	public WfsDescribeFeatureWizard(WfsDescribeFeatureConfiguration configuration) {
		super(configuration);

		setWindowTitle(Messages.WfsDescribeFeatureWizard_0); //$NON-NLS-1$
	}

}
