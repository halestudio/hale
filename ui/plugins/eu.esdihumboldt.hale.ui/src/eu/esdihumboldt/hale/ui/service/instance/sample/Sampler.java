/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.service.instance.sample;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.ui.common.AttributeEditor;

/**
 * Provides samples from given instance collections. A sampler may not hold any
 * state.
 * 
 * @author Simon Templer
 */
public interface Sampler {

	/**
	 * Create a view on the given instance collection providing a reduced
	 * sub-set of instances as sample data set.
	 * 
	 * @param instances the instances
	 * @param settings the value representing the sampler settings, or an empty
	 *            value if explicit settings are not defined
	 * @return the instance collection containing only the configured samples,
	 *         or the original instance collection if sampling is disabled
	 */
	public InstanceCollection sample(InstanceCollection instances, Value settings);

	/**
	 * Determine the sampler display name.
	 * 
	 * @param settings the value representing the sampler settings, or an empty
	 *            value if a generic display name should be created
	 * @return the display name
	 */
	public String getDisplayName(Value settings);

	/**
	 * Create an editor for the sampler's settings value.
	 * 
	 * @param parent the parent composite
	 * @return the settings editor
	 */
	public AttributeEditor<Value> createEditor(Composite parent);

	/**
	 * Get the default settings for the sampler.
	 * 
	 * @return the sampler default settings
	 */
	public Value getDefaultSettings();

}
