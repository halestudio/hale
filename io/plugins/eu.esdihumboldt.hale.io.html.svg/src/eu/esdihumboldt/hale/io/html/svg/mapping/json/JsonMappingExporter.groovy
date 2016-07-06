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

package eu.esdihumboldt.hale.io.html.svg.mapping.json;

import java.nio.charset.StandardCharsets

import eu.esdihumboldt.hale.common.align.io.impl.AbstractAlignmentWriter
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator
import eu.esdihumboldt.hale.common.core.io.report.IOReport
import eu.esdihumboldt.hale.common.core.io.report.IOReporter
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl
import eu.esdihumboldt.util.groovy.json.JsonStreamBuilder
import groovy.transform.CompileStatic

/**
 * Exports an alignment to a JSON representation.
 * 
 * @author Simon Templer
 */
@CompileStatic
class JsonMappingExporter extends AbstractAlignmentWriter {

	@Override
	boolean isCancelable() {
		false
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
	throws IOProviderConfigurationException, IOException {
		progress.begin('Generate JSON representation', ProgressIndicator.UNKNOWN)

		CellJsonExtension ext = new ExtendedCellRepresentation(alignment, serviceProvider)
		ValueRepresentation rep = new JsonValueRepresentation()

		try {
			new OutputStreamWriter(getTarget().getOutput(), StandardCharsets.UTF_8).withWriter { out ->
				JsonStreamBuilder json = new JsonStreamBuilder(out, true)
				AlignmentJson.alignmentInfoJSON(alignment, json, serviceProvider,
						projectInfo, ext, rep, Locale.getDefault())

				reporter.setSuccess(true)
			}
		} catch (Exception e) {
			reporter.error(new IOMessageImpl('Error creating JSON representation of alignment', e))
			reporter.setSuccess(false)
		} finally {
			progress.end()
		}

		reporter
	}

	@Override
	protected String getDefaultTypeName() {
		'JSON Alignment'
	}
}
