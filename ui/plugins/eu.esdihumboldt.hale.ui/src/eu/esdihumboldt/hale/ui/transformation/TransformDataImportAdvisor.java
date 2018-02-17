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

import java.net.URI;

import eu.esdihumboldt.hale.common.instance.io.InstanceIO;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.io.instance.InstanceImportAdvisor;

/**
 * Import advisor for the transform data wizard.
 * 
 * @author Kai Schwierczek
 */
public class TransformDataImportAdvisor extends InstanceImportAdvisor {

	private InstanceReader provider;

	/**
	 * Default constructor.
	 */
	public TransformDataImportAdvisor() {
		super();

		setServiceProvider(HaleUI.getServiceProvider());
		setActionId(InstanceIO.ACTION_LOAD_SOURCE_DATA);
	}

	/**
	 * @see InstanceImportAdvisor#handleResults(InstanceReader)
	 */
	@Override
	public void handleResults(InstanceReader provider) {
		this.provider = provider;
	}

	/**
	 * Returns the created instance collection.
	 * 
	 * @return the created instance collection
	 */
	public InstanceCollection getInstances() {
		return provider.getInstances();
	}

	/**
	 * Returns the location of the source.
	 * 
	 * @return the location of the source
	 */
	public URI getLocation() {
		return provider.getSource().getLocation();
	}
}
