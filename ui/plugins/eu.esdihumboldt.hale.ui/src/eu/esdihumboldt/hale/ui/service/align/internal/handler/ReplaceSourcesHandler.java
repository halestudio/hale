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

package eu.esdihumboldt.hale.ui.service.align.internal.handler;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;

/**
 * Handler for replacing cell sources.
 * 
 * @author Simon Templer
 */
public class ReplaceSourcesHandler extends ReplaceEntitiesHandler {

	/**
	 * Default constructor
	 */
	public ReplaceSourcesHandler() {
		super(SchemaSpaceID.SOURCE);
	}

}
