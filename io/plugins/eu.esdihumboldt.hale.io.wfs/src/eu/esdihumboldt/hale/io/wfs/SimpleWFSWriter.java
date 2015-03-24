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
 * Simple WFS writer that is not configurable that writes directly to the
 * service endpoint via HTTP Post.
 * 
 * @author Simon Templer
 */
public class SimpleWFSWriter extends AbstractWFSWriter<GmlInstanceWriter> {

	/**
	 * Name of the parameter specifying the data's input format.
	 */
	public static final String PARAM_INPUT_FORMAT = "inputFormat";

	/**
	 * Name of the parameter specifying the ID generation strategy.
	 */
	public static final String PARAM_ID_GEN = "idgen";

	/**
	 * Default input format to use if none is provided.
	 */
	public static final String DEFAULT_INPUT_FORMAT = "text/xml; subtype=gml/3.2.1";

	/**
	 * Default constructor.
	 */
	public SimpleWFSWriter() {
		super(new GmlInstanceWriter() {

			@Override
			protected String getTaskName() {
				return "WFS-T Insert transaction";
			}

		});
	}

	@Override
	protected XmlWrapper createTransaction() {
		String idgen = getParameter(PARAM_ID_GEN).as(String.class);
		String inputFormat = getParameter(PARAM_INPUT_FORMAT)
				.as(String.class, DEFAULT_INPUT_FORMAT);
		return new WFSInsert(getWFSVersion(), idgen, inputFormat);
	}

}
