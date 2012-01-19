package eu.esdihumboldt.hale.io.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.osgi.framework.Version;

import com.google.common.io.Files;

import eu.esdihumboldt.commons.goml.align.Alignment;
import eu.esdihumboldt.hale.common.align.io.impl.AbstractAlignmentWriter;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoAware;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * Export a Mapping to HTML for documentation purposes.
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("unused") // TODO: remove
public class HtmlMappingExporter extends AbstractAlignmentWriter implements
		ProjectInfoAware {

	private VelocityContext context;
	private VelocityEngine ve;
	private File file_template;
	private File tempDir;
	private Alignment alignment = null;
	private List<ICell> retypes = new ArrayList<ICell>();
	private List<ICell> transformations = new ArrayList<ICell>();
	private List<ICell> augmentations = new ArrayList<ICell>();

	@Override
	public boolean isCancelable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String getDefaultTypeName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Initialize temporary directory and template engine.
	 * 
	 * @throws Exception
	 *             if an error occurs during the initialization
	 */
	private void init() throws Exception {
		synchronized (this) {
			if (ve == null) {
				ve = new VelocityEngine();
				// create a temporary directory
				tempDir = Files.createTempDir();

				file_template = new File(tempDir, "template.vm");
				URL templatePath = this.getClass().getResource("template.html");
				FileOutputStream fos = new FileOutputStream(file_template);
				InputStream stream = templatePath.openStream();

				// copys the InputStream into FileOutputStream
				IOUtils.copy(stream, fos);

				stream.close();
				fos.close();

				ve.setProperty("file.resource.loader.path",
						tempDir.getAbsolutePath());
				// initialize VelocityEngine
				ve.init();
			}
		}
	}

	
	private void sortAlignment() {
		for (Iterator<ICell> iterator = this.alignment.getMap().iterator(); iterator
				.hasNext();) {
			ICell cell = iterator.next();

			// Retype
			String cellName;
			if (cell.getEntity1().getTransformation() == null) {
				cellName = cell.getEntity2().getTransformation().getService()
						.getLocation();
			} else {
				cellName = cell.getEntity1().getTransformation().getService()
						.getLocation();
			}
			String[] tempSplit = cellName.split("\\."); //$NON-NLS-1$
			String graphConnectionNodeName = tempSplit[tempSplit.length - 1];
			if (graphConnectionNodeName.equals("RenameFeatureFunction")) { //$NON-NLS-1$
				this.retypes.add(cell);
			}

			// Augmentation
			if (cell.getEntity1().getTransformation() == null
					|| cell.getEntity1().getAbout().getAbout()
							.equals("entity/null")) { //$NON-NLS-1$
				this.augmentations.add(cell);
			}

			// Transformation
			if (cell.getEntity1().getTransformation() != null) {
				this.transformations.add(cell);
			}
		}
	}

	private void byteArrayToFile(File file, byte[] byteArray)
			throws FileNotFoundException, IOException {
		if (byteArray != null) {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(byteArray);
			fileOutputStream.close();
		}
	}

	private void stringWriterToFile(File file, StringWriter writer)
			throws FileNotFoundException, IOException {
		if (writer != null && file != null) {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(writer.toString().getBytes());
			fileOutputStream.close();
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.project.ProjectInfoAware#setProjectInfo(eu.esdihumboldt.hale.common.core.io.project.ProjectInfo)
	 */
	@Override
	public void setProjectInfo(ProjectInfo projectInfo) {

		if (projectInfo != null) {
			Date date = new Date();
			SimpleDateFormat dfm = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"); //$NON-NLS-1$

			String projectName = "Project Name: " + projectInfo.getName();
			String author = "Project Author: " + projectInfo.getAuthor();
			Version haleVers = projectInfo.getHaleVersion();
			String exportDate = "Export Date: " + dfm.format(date);
			String description = "Description: " + projectInfo.getDescription();
			String created = "Created Date: "
					+ dfm.format(projectInfo.getCreated());

			// associate variables with information datas
			context.put("author", author);
			context.put("project", projectName);
			context.put("haleVers", haleVers);
			context.put("exportDate", exportDate);
			context.put("createdDate", created);
			context.put("description", description);
		}
		else {
			// do nothing
		}

	}

}
