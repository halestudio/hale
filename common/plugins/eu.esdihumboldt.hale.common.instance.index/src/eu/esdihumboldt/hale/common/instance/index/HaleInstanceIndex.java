/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.common.instance.index;

import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.ResolvableInstanceReference;

/**
 * Interface for an {@link Instance} indexes for hale studio
 * 
 * @author Florian Esser
 */
public interface HaleInstanceIndex extends
		InstanceIndex<List<IndexedPropertyValue>, PropertyEntityDefinitionMapping, ResolvableInstanceReference> {

	/**
	 * Retrieve instance references from the index grouped by the given
	 * properties.
	 * 
	 * @param keyProperties Properties to group by
	 * @return Grouped instance references
	 */
	Collection<Collection<ResolvableInstanceReference>> groupBy(List<List<QName>> keyProperties);
}
