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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.GroupLayout.Alignment;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.common.io.Files;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.io.impl.AbstractAlignmentWriter;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.io.xslt.internal.Templates;

/**
 * Exports an {@link Alignment} to XSLT.
 * 
 * @author Simon Templer
 */
public class XsltExport extends AbstractAlignmentWriter {

	private static final ALogger log = ALoggerFactory.getLogger(XsltExport.class);

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		File templateDir = Files.createTempDir();
		progress.begin("Generate XSLT", ProgressIndicator.UNKNOWN);
		try {
			log.info("Template directory: " + templateDir.getAbsolutePath());
			Templates.copyTemplates(templateDir);

			VelocityEngine ve = new VelocityEngine();

//			ve.setProperty("resource.loader", "main, file");
//			ve.setProperty("main.resource.loader.class",
//					eu.esdihumboldt.hale.io.xslt.internal.Templates.class);
			// custom resource loader does not work in OSGi context, so copy
			// templates to template folder
			ve.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, templateDir.getAbsolutePath());
			ve.init();

			Template root = ve.getTemplate(Templates.ROOT, "UTF-8");

			VelocityContext context = new VelocityContext();

			OutputStream out = getTarget().getOutput();
			Writer writer = new OutputStreamWriter(out, "UTF-8");
			try {
				root.merge(context, writer);
				writer.flush();
			} finally {
				writer.close();
			}

			reporter.setSuccess(reporter.getErrors().isEmpty());
			return reporter;
		} catch (Exception e) {
			reporter.error(new IOMessageImpl("XSLT generation failed", e));
			reporter.setSuccess(false);
			return reporter;
		} finally {
			progress.end();
			FileUtils.deleteDirectory(templateDir);
		}
	}

	@Override
	protected String getDefaultTypeName() {
		return "XSLT transformation";
	}

}
