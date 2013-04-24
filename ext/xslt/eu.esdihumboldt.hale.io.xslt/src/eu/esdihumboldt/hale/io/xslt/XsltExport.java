/*
 * Copyright (c) 2012 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt;

import java.io.File;
import java.io.IOException;

import javax.swing.GroupLayout.Alignment;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.FileUtils;

import com.google.common.io.Files;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.io.impl.AbstractAlignmentWriter;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.io.gml.writer.XmlWriterBase;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;
import eu.esdihumboldt.hale.io.xslt.internal.XsltGenerator;

/**
 * Exports an {@link Alignment} to XSLT.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class XsltExport extends AbstractAlignmentWriter implements XmlWriterBase {

	private static final ALogger log = ALoggerFactory.getLogger(XsltExport.class);

	/**
	 * Default constructor
	 */
	public XsltExport() {
		super();

		addSupportedParameter(PARAM_ROOT_ELEMENT_NAMESPACE);
		addSupportedParameter(PARAM_ROOT_ELEMENT_NAME);
	}

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	public void validate() throws IOProviderConfigurationException {
		super.validate();

		if (getTargetSchema() == null) {
			fail("Target schema not supplied");
		}
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		File templateDir = Files.createTempDir();
		progress.begin("Generate XSLT", ProgressIndicator.UNKNOWN);
		try {
			log.info("Template directory: " + templateDir.getAbsolutePath());

			XmlIndex targetIndex = StreamGmlWriter.getXMLIndex(getTargetSchema());
			if (targetIndex == null) {
				throw new IllegalStateException("Target schema contains no XML schema");
			}
			XmlIndex sourceIndex = StreamGmlWriter.getXMLIndex(getSourceSchema());
			if (sourceIndex == null) {
				throw new IllegalStateException("Source schema contains no XML schema");
			}

			init(sourceIndex, targetIndex);

			XmlElement containerElement = StreamGmlWriter.getConfiguredContainerElement(this,
					targetIndex);
			if (containerElement == null) {
				throw new IllegalStateException("No target container element specified");
			}

			XsltGenerator generator = new XsltGenerator(templateDir, getAlignment(), sourceIndex,
					targetIndex, reporter, progress, containerElement, getSourceContext()) {

				@Override
				protected void writeContainerIntro(XMLStreamWriter writer,
						XsltGenerationContext context) throws XMLStreamException, IOException {
					XsltExport.this.writeContainerIntro(writer, context);
				}

			};
			return generator.write(getTarget());
		} catch (Exception e) {
			reporter.error(new IOMessageImpl("XSLT generation failed", e));
			reporter.setSuccess(false);
			return reporter;
		} finally {
			progress.end();
			try {
				FileUtils.deleteDirectory(templateDir);
			} catch (Exception e) {
				// failure to delete the directory is not fatal
				log.warn("Failed to delete temporary directory", e);
			}
		}
	}

	/**
	 * Get the custom source context provider to use during the export.
	 * 
	 * @return the source context provider
	 */
	protected SourceContextProvider getSourceContext() {
		return null;
	}

	/**
	 * Initialize the provider before execution.
	 * 
	 * @param sourceIndex the source schema
	 * @param targetIndex the target schema
	 * @throws IOProviderConfigurationException if the initialization was not
	 *             successful and the execution should not proceed
	 */
	@SuppressWarnings("unused")
	protected void init(XmlIndex sourceIndex, XmlIndex targetIndex)
			throws IOProviderConfigurationException {
		// override me
	}

	/**
	 * Write additional content into the container before it is populated by the
	 * type relations.
	 * 
	 * @param writer the XML stream writer
	 * @param context the XSLT generation context
	 * @throws XMLStreamException if an error occurs while writing to the
	 *             container
	 * @throws IOException if an error occurs writing to the file
	 */
	@SuppressWarnings("unused")
	protected void writeContainerIntro(XMLStreamWriter writer, XsltGenerationContext context)
			throws XMLStreamException, IOException {
		// override me
	}

	@Override
	protected String getDefaultTypeName() {
		return "XSLT transformation";
	}

	@Override
	public SchemaSpace getTargetSchema() {
		// expose target schema
		return super.getTargetSchema();
	}

}
