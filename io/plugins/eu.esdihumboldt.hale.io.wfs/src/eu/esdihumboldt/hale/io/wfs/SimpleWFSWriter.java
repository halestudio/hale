/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.wfs;

import eu.esdihumboldt.hale.io.gml.writer.GmlInstanceWriter;
import eu.esdihumboldt.hale.io.gml.writer.XmlWrapper;
import eu.esdihumboldt.hale.io.wfs.transactions.WFSInsert;

/**
 * XXX Simple WFS writer that is not configurable.
 * 
 * @author Simon Templer
 */
public class SimpleWFSWriter extends AbstractWFSWriter<GmlInstanceWriter> {

	/**
	 * Default constructor.
	 */
	public SimpleWFSWriter() {
		super(new GmlInstanceWriter());
	}

	@Override
	protected XmlWrapper createTransaction() {
		return new WFSInsert(WFSVersion.V2_0_0);
	}

}
