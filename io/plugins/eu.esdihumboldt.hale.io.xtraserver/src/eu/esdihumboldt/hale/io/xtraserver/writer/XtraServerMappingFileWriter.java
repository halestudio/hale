/*
 * Copyright (c) 2017 interactive instruments GmbH
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
 *     interactive instruments GmbH <http://www.interactive-instruments.de>
 */

package eu.esdihumboldt.hale.io.xtraserver.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.io.XtraServerMappingFile;
import eu.esdihumboldt.hale.common.align.io.impl.AbstractAlignmentWriter;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.ValueProperties;
import eu.esdihumboldt.hale.common.core.io.project.ComplexConfigurationService;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.io.xtraserver.writer.handler.UnsupportedTransformationException;

/**
 * Writes an Alignment to a XtraServer Mapping file.
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class XtraServerMappingFileWriter extends AbstractAlignmentWriter {

	private final static String WRITER_TYPE_NAME = "XtraServer Mapping Exporter";

	private static final String CONTENT_TYPE_MAPPING = "eu.esdihumboldt.hale.io.xtraserver.mapping.xml";

	private static final String CONTENT_TYPE_ARCHIVE = "eu.esdihumboldt.hale.io.xtraserver.mapping.archive";

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return WRITER_TYPE_NAME;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(final ProgressIndicator progress, final IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		ValueProperties projectProperties = null;
		if (getProjectInfo() instanceof Project) {
			final ComplexConfigurationService service = ProjectIO
					.createProjectConfigService((Project) getProjectInfo());
			projectProperties = service.getProperty("variables").as(ValueProperties.class);
		}
		if (projectProperties == null) {
			projectProperties = new ValueProperties();
		}

		progress.begin("Initialising", ProgressIndicator.UNKNOWN);
		if (getAlignment() == null) {
			throw new IOProviderConfigurationException("No alignment was provided.");
		}
		if (getTargetSchema() == null) {
			throw new IOProviderConfigurationException("No target schema was provided.");
		}
		if (getTarget() == null) {
			throw new IOProviderConfigurationException("No target was provided.");
		}
		try (final OutputStream out = getTarget().getOutput()) {
			final XtraServerMappingGenerator generator = new XtraServerMappingGenerator(
					getAlignment(), getTargetSchema(), progress,
					Collections.unmodifiableMap(projectProperties), getProjectInfo(),
					getProjectLocation(), reporter);
			final XtraServerMapping mapping = generator.generate(reporter);
			XtraServerMappingFile.Writer writer = XtraServerMappingFile.write().mapping(mapping);

			if (getContentType().getId().equals(CONTENT_TYPE_MAPPING)) {
				progress.setCurrentTask("Writing XtraServer Mapping file");
			}
			else if (getContentType().getId().equals(CONTENT_TYPE_ARCHIVE)) {
				progress.setCurrentTask("Writing XtraServer Mapping Archive");
				writer.createArchiveWithAdditionalFiles();
			}
			else {
				throw new IOProviderConfigurationException(
						"Content type not supported: " + getContentType().getName());
			}
			writer.toStream(out);
			progress.advance(1);

			final Set<String> missingAssociationTargets = generator.getMissingAssociationTargets();
			if (!missingAssociationTargets.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				int warningCount = 0;
				for (String s : missingAssociationTargets) {
					builder.append(System.getProperty("line.separator"));
					builder.append(s);
					warningCount++;
				}
				reporter.countWarning(warningCount);

				reporter.warn(
						"To determine the mapping association targets, this plugin relies on annotations "
								+ "that are described in 'ISO 19109 - Rules for application schema'. "
								+ "This requires that references possess a GML 'targetElement' elment inside an 'appInfo' "
								+ "schema annotation. Please note that the following list of properties are not mapped with "
								+ "association targets due to missing annotations: {0} ",
						builder.toString());
			}
		} catch (final UnsupportedTransformationException e) {
			reporter.error("The transformation of the type '" + e.getTransformationIdentifier()
					+ "'  is not supported. Make sure that the XtraServer compatibility mode is enabled.");
			reporter.setSuccess(false);
			return reporter;
		} catch (final JAXBException | SAXException | XMLStreamException e) {
			reporter.error("An internal error occurred", e);
			reporter.setSuccess(false);
			return reporter;
		}
		progress.end();
		reporter.setSuccess(true);
		return reporter;
	}

}
