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

package eu.esdihumboldt.hale.ui.common.service.compatibility;

import java.util.Set;

import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.common.align.compatibility.CompatibilityMode;

/**
 * The compatibility mode factory interface
 * 
 * @author Sebastian Reinhardt
 */
public interface CompatibilityModeFactory extends ExtensionObjectFactory<CompatibilityMode> {

	/**
	 * Get the filter definition identifiers of filters supported by the
	 * compatibility mode.
	 * 
	 * @return the set of filter definition identifiers
	 */
	public Set<String> getSupportedFilters();

}
