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

package eu.esdihumboldt.hale.io.deegree.mapping;

import java.io.OutputStream;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.deegree.commons.xml.stax.IndentingXMLStreamWriter;
import org.deegree.feature.persistence.sql.config.SQLFeatureStoreConfigWriter;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.schema.model.Schema;

/**
 * TODO Type description
 * 
 * @author simon
 */
public class MappingWriter {

	private final Schema targetSchema;

	private final Alignment alignment;

	private final String connectionId;

	/**
	 * @param targetSchema
	 * @param alignment
	 * @param connectionId
	 */
	public MappingWriter(Schema targetSchema, Alignment alignment, String connectionId) {
		super();
		this.targetSchema = targetSchema;
		this.alignment = alignment;
		this.connectionId = connectionId;
	}

	public void saveConfig(OutputStream out) {
		SQLFeatureStoreConfigWriter configWriter = new SQLFeatureStoreConfigWriter(getMappedSchema());
		
		List<String> schemaUrls = new ArrL;

		XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
		xmlWriter = new IndentingXMLStreamWriter(xmlWriter);
		try {
			configWriter.writeConfig(xmlWriter, connectionId, schemaUrls);
		} finally {
			xmlWriter.close();
		}
	}

}
