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

package eu.esdihumboldt.hale.io.xslt.internal;

import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;

/**
 * Generate a XSLT transformation from an {@link Alignment}. Each generation
 * process has to use its own instance.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class XsltGenerator {

	private final VelocityEngine ve;
	private final IOReporter reporter;
	private final ProgressIndicator progress;
	private final Alignment alignment;
	private final XmlIndex targetSchema;
	private final File workDir;

	/**
	 * Create a XSLT generator.
	 * 
	 * @param workDir the working directory where the generator may store
	 *            temporary files, the caller is responsible for cleaning this
	 *            up, e.g. after {@link #write(LocatableOutputSupplier)} was
	 *            called
	 * @param alignment the alignment
	 * @param targetSchema the target schema
	 * @param reporter the reporter for documenting errors
	 * @param progress the progress indicator for indicating the generation
	 *            progress
	 * @throws Exception if an error occurs initializing the generator
	 */
	public XsltGenerator(File workDir, Alignment alignment, SchemaSpace targetSchema,
			IOReporter reporter, ProgressIndicator progress) throws Exception {
		this.reporter = reporter;
		this.progress = progress;
		this.alignment = alignment;
		this.workDir = workDir;

		XmlIndex index = StreamGmlWriter.getXMLIndex(targetSchema);
		if (index == null) {
			throw new IllegalArgumentException("Target schema contains no XML schema");
		}
		this.targetSchema = index;

		Templates.copyTemplates(workDir);

		ve = new VelocityEngine();

//		ve.setProperty("resource.loader", "main, file");
//		ve.setProperty("main.resource.loader.class",
//				eu.esdihumboldt.hale.io.xslt.internal.Templates.class);
		// custom resource loader does not work in OSGi context, so copy
		// templates to template folder
		ve.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, workDir.getAbsolutePath());
		ve.init();
	}

	/**
	 * Generate the XSLT transformation and write it to the given target.
	 * 
	 * @param target the target output supplier
	 * @return the report
	 * @throws Exception if a unrecoverable error occurs during the process
	 */
	public IOReport write(LocatableOutputSupplier<? extends OutputStream> target) throws Exception {
		Template root = ve.getTemplate(Templates.ROOT, "UTF-8");

		VelocityContext context = new VelocityContext();

		OutputStream out = target.getOutput();
		Writer writer = new OutputStreamWriter(out, "UTF-8");
		try {
			root.merge(context, writer);
			writer.flush();
		} finally {
			writer.close();
		}

		reporter.setSuccess(reporter.getErrors().isEmpty());
		return reporter;
	}

}
