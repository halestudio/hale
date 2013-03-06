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

package eu.esdihumboldt.hale.ui.views.mapping;

import eu.esdihumboldt.hale.ui.common.graph.content.CellGraphContentProvider;

/**
 * {@link CellGraphContentProvider} with reversed edges.
 * 
 * @author Kai Schwierczek
 */
public class ReverseCellGraphContentProvider extends CellGraphContentProvider {

	@Override
	public Object getSource(Object rel) {
		return super.getDestination(rel);
	}

	@Override
	public Object getDestination(Object rel) {
		return super.getSource(rel);
	}

}
