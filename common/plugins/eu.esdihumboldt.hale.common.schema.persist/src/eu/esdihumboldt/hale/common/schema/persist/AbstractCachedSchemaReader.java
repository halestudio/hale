/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.schema.persist;

import org.w3c.dom.Element;

import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.ElementValue;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.OsgiClassResolver;
import eu.esdihumboldt.hale.common.schema.persist.hsd.HaleSchemaConstants;
import eu.esdihumboldt.hale.common.schema.persist.hsd.SchemaToXml;
import eu.esdihumboldt.hale.common.schema.persist.hsd.XmlToSchema;
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder;

/**
 * Base class for schema readers caching their result as HALE Schema Definition.
 * 
 * @author Simon Templer
 */
public abstract class AbstractCachedSchemaReader extends AbstractCachedSchemaReaderBase {

	/**
	 * Stores the schema as HSD DOM.
	 * 
	 * @see AbstractCachedSchemaReaderBase#storeInCache(Schema)
	 */
	@Override
	protected Value storeInCache(Schema schema) throws Exception {
		NSDOMBuilder builder = SchemaToXml.createBuilder();
		Element root = new SchemaToXml().schemaToXml(builder, schema);
		return new ElementValue(root, null);
	}

	@Override
	protected boolean validCache(Value cache) {
		if (!super.validCache(cache) || cache.getDOMRepresentation() == null) {
			return false;
		}

		Element root = cache.getDOMRepresentation();
		return "schema".equals(root.getLocalName())
				&& HaleSchemaConstants.NS.equals(root.getNamespaceURI());
	}

	@Override
	protected Schema loadFromCache(Value cache, ProgressIndicator progress, IOReporter reporter) {
		Schema schema = null;
		progress.begin("Load schema from cached schema definition", ProgressIndicator.UNKNOWN);
		try {
			schema = XmlToSchema.parseSchema(cache.getDOMRepresentation(), new OsgiClassResolver(),
					reporter);

			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error(new IOMessageImpl(e.getMessage(), e));
			reporter.setSuccess(false);
		} finally {
			progress.end();
		}
		return schema;
	}

}
