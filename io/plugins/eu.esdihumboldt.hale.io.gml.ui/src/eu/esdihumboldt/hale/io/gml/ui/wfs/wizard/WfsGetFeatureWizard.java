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

package eu.esdihumboldt.hale.io.gml.ui.wfs.wizard;

import eu.esdihumboldt.hale.io.gml.ui.internal.Messages;

/**
 * WFS GetFeature wizard
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class WfsGetFeatureWizard extends AbstractWfsWizard<WfsGetFeatureConfiguration> {

	/**
	 * @see AbstractWfsWizard#AbstractWfsWizard(WfsConfiguration)
	 */
	public WfsGetFeatureWizard(WfsGetFeatureConfiguration configuration) {
		super(configuration);

		setWindowTitle(Messages.WfsGetFeatureWizard_0); //$NON-NLS-1$
	}

	/**
	 * @see AbstractWfsWizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();

		addPage(new FilterPage(configuration, getTypes()));
	}

}
