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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.align.io.impl.AbstractAlignmentWriter;
import eu.esdihumboldt.hale.common.config.ProviderConfig;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.util.config.ConfigYaml;
import eu.esdihumboldt.util.io.EntryOutputStream;

/**
 * Creates a deegree SQL mapping configuration from a schema.
 * 
 * @author Simon Templer
 */
public class MappingAlignmentWriter extends AbstractAlignmentWriter {

	private static final Object CONTENT_TYPE_DEEGREE_ARCHIVE = "eu.esdihumboldt.hale.io.deegree.archive";

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Generate deegree SQL mapping", ProgressIndicator.UNKNOWN);
		try {
			Schema targetSchema = getTargetSchema().getSchemas().iterator().next();
			GenericMappingConfiguration config = new GenericMappingConfiguration(
					ProviderConfig.get(this));
			MappingWriter writer = new MappingWriter(targetSchema, getAlignment(), config);

			writeResult(writer, getTarget(), getContentType(), config);

			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error("Error writing deegree SQL mapping", e);
			reporter.setSuccess(false);
		} finally {
			progress.end();
		}

		return reporter;
	}

	/**
	 * Write the deegree configuration.
	 * 
	 * @param writer the mapping writer
	 * @param target the target
	 * @param contentType the configured content type
	 * @param config the mapping configuration
	 * @throws Exception if an error occurs writing the configuration
	 */
	public static void writeResult(MappingWriter writer,
			LocatableOutputSupplier<? extends OutputStream> target, IContentType contentType,
			GenericMappingConfiguration config) throws Exception {
		boolean archive = contentType == null
				|| contentType.getId().equals(CONTENT_TYPE_DEEGREE_ARCHIVE);

		try (OutputStream out = target.getOutput()) {
			if (archive) {
				// archive w/ configuration files

				// TODO determine paths

				try (ZipOutputStream zip = new ZipOutputStream(out)) {
					// feature store config
					zip.putNextEntry(new ZipEntry("featureStore.xml"));
					try (EntryOutputStream entry = new EntryOutputStream(zip)) {
						writer.saveConfig(entry);
					}
					// DDL
					zip.putNextEntry(new ZipEntry("featureStore.ddl"));
					try (EntryOutputStream entry = new EntryOutputStream(zip)) {
						writer.saveDDL(entry);
					}
					// configuration options
					zip.putNextEntry(new ZipEntry("config.yaml"));
					try (EntryOutputStream entry = new EntryOutputStream(zip);
							Writer w = new OutputStreamWriter(entry, StandardCharsets.UTF_8)) {
						ConfigYaml.save(config.getInternalConfig(), w);
					}
				}
			}
			else {
				// just FeatureStore config
				writer.saveConfig(out);
			}
		}
	}

	@Override
	protected String getDefaultTypeName() {
		return "deegree configuration";
	}

}
