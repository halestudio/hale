/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.transformation;

import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.ui.io.instance.InstanceImportAdvisor;

/**
 * Import advisor for the transform data wizard.
 *
 * @author Kai Schwierczek
 */
public class TransformDataImportAdvisor extends InstanceImportAdvisor {
	private InstanceCollection instances;

	/**
	 * @see InstanceImportAdvisor#handleResults(InstanceReader)
	 */
	@Override
	public void handleResults(InstanceReader provider) {
		instances = provider.getInstances();
	}

	/**
	 * Returns the created instance collection.
	 *
	 * @return the created instance collection
	 */
	public InstanceCollection getInstances() {
		return instances;
	}
}
