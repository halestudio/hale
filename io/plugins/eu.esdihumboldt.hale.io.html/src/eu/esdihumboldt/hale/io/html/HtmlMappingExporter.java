/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.html;

import java.awt.Dimension;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphNode;

import com.google.common.io.Files;

import eu.esdihumboldt.hale.common.align.io.impl.AbstractAlignmentWriter;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoAware;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.graph.content.CellGraphContentProvider;
import eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider;
import eu.esdihumboldt.hale.ui.util.DisplayThread;
import eu.esdihumboldt.hale.ui.util.graph.OffscreenGraph;
import eu.esdihumboldt.util.Identifiers;

//import org.eclipse.draw2d.geometry.Dimension;

/**
 * Export a Mapping to HTML for documentation purposes.
 * 
 * @author Kevin Mais
 */
public class HtmlMappingExporter extends AbstractAlignmentWriter implements ProjectInfoAware,
		HtmlMappingTemplateConstants {

	private VelocityContext context;
	private VelocityEngine velocityEngine;
	private ProjectInfo projectInfo;
	private File templateFile;
	private File tempDir;
	private Alignment alignment;
	private Identifiers<Cell> cellIds;

	private IOReporter reporter;

	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.project.ProjectInfoAware#setProjectInfo(eu.esdihumboldt.hale.common.core.io.project.ProjectInfo)
	 */
	@Override
	public void setProjectInfo(ProjectInfo projectInfo) {
		this.projectInfo = projectInfo;
	}

	@Override
	protected String getDefaultTypeName() {
		return "HTML mapping documentation";
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		this.reporter = reporter;

		context = new VelocityContext();
		cellIds = new Identifiers<Cell>(Cell.class, false);

		alignment = getAlignment();

		URL headlinePath = this.getClass().getResource("bg-headline.png"); //$NON-NLS-1$
		URL cssPath = this.getClass().getResource("style.css"); //$NON-NLS-1$
		URL linkPath = this.getClass().getResource("int_link.png"); //$NON-NLS-1$
		URL tooltipIcon = this.getClass().getResource("tooltip.png"); //$NON-NLS-1$
		final String filesSubDir = FilenameUtils.removeExtension(FilenameUtils.getName(getTarget()
				.getLocation().getPath())) + "_files"; //$NON-NLS-1$
		final File filesDir = new File(FilenameUtils.getFullPath(getTarget().getLocation()
				.getPath()), filesSubDir);

		filesDir.mkdirs();
		context.put(FILE_DIRECTORY, filesSubDir);

		try {
			init();
		} catch (Exception e) {
			return reportError(reporter, "Initializing error", e);
		}
		File cssOutputFile = new File(filesDir, "style.css");
		FileUtils.copyFile(getInputFile(cssPath), cssOutputFile);

		// create headline picture
		File headlineOutputFile = new File(filesDir, "bg-headline.png"); //$NON-NLS-1$
		FileUtils.copyFile(getInputFile(headlinePath), headlineOutputFile);

		File linkOutputFile = new File(filesDir, "int_link.png"); //$NON-NLS-1$
		FileUtils.copyFile(getInputFile(linkPath), linkOutputFile);

		File tooltipIconFile = new File(filesDir, "tooltip.png"); //$NON-NLS-1$
		FileUtils.copyFile(getInputFile(tooltipIcon), tooltipIconFile);

		File htmlExportFile = new File(getTarget().getLocation().getPath());
		if (projectInfo != null) {
			Date date = new Date();
			DateFormat dfm = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

			// associate variables with information data
			String exportDate = dfm.format(date);
			context.put(EXPORT_DATE, exportDate);

			if (projectInfo.getCreated() != null) {
				String created = dfm.format(projectInfo.getCreated());
				context.put(CREATED_DATE, created);
			}

			context.put(PROJECT_INFO, projectInfo);
		}

		if (alignment != null) {
			Collection<TypeCellInfo> typeCellInfos = new ArrayList<TypeCellInfo>();
			Collection<? extends Cell> cells = alignment.getTypeCells();
			Iterator<? extends Cell> it = cells.iterator();
			while (it.hasNext()) {
				final Cell cell = it.next();
				// this is the collection of type cell info
				TypeCellInfo typeCellInfo = new TypeCellInfo(cell, alignment, cellIds, filesSubDir);
				typeCellInfos.add(typeCellInfo);
			}
			// put the full collection of type cell info to the context (for the
			// template)
			context.put(TYPE_CELL_INFOS, typeCellInfos);
			createImages(filesDir);
		}

		context.put(TOOLTIP, getParameter("htmlMappingTooltip"));

		Template template;
		try {
			template = velocityEngine.getTemplate(templateFile.getName(), "UTF-8");
		} catch (Exception e) {
			return reportError(reporter, "Could not load template", e);
		}

		// delete template file for cleanup
		templateFile.delete();

		FileWriter fileWriter = new FileWriter(htmlExportFile);
		template.merge(context, fileWriter);
		fileWriter.close();

		// delete tempDir for cleanup
		tempDir.deleteOnExit();

		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * Initialize temporary directory and template engine.
	 * 
	 * @throws Exception if an error occurs during the initialization
	 */
	private void init() throws Exception {
		synchronized (this) {
			if (velocityEngine == null) {
				velocityEngine = new VelocityEngine();

				// create a temporary directory
				tempDir = Files.createTempDir();

				templateFile = new File(tempDir, "template.vm");
				URL templatePath = getClass().getResource("template.html");
				OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(
						templateFile));
				InputStream inputStream = templatePath.openStream();

				// copies the InputStream into OutputStream
				IOUtils.copy(inputStream, outputStream);

				inputStream.close();
				outputStream.close();

				velocityEngine.setProperty("file.resource.loader.path", tempDir.getAbsolutePath());
				// initialize VelocityEngine
				velocityEngine.init();
			}
		}
	}

	private void createImages(File filesDir) {
		Collection<? extends Cell> cells = alignment.getCells();
		Iterator<? extends Cell> ite = cells.iterator();
		while (ite.hasNext()) {
			Cell cell = ite.next();
			saveImageToFile(cell, filesDir);
		}
	}

	private void saveImageToFile(final Cell cell, File filesDir) {
		Display display;
		if (Display.getCurrent() != null) {
			// use the current display if available
			display = Display.getCurrent();
		}
		else {
			try {
				// use workbench display if available
				display = PlatformUI.getWorkbench().getDisplay();
			} catch (Throwable e) {
				// use a dedicated display thread if no workbench is
				// available
				display = DisplayThread.getInstance().getDisplay();
			}
		}

		// creates a unique id for each cell
		String cellId = cellIds.getId(cell);

		final File file = new File(filesDir, "img_" + cellId + ".png");

		display.syncExec(new Runnable() {

			@Override
			public void run() {
				OffscreenGraph offscreenGraph = new OffscreenGraph(600, 200) {

					@Override
					protected void configureViewer(GraphViewer viewer) {
						IContentProvider contentProvider = new CellGraphContentProvider();
						GraphLabelProvider labelProvider = new GraphLabelProvider(HaleUI
								.getServiceProvider());
						viewer.setContentProvider(contentProvider);
						viewer.setLabelProvider(labelProvider);
						viewer.setInput(cell);
					}
				};

				Graph graph = offscreenGraph.getGraph();
				Dimension dimension = computeSize(graph);

				// minimum width = 600
				offscreenGraph.resize(dimension.width > 600 ? dimension.width : 600,
						dimension.height);

				try {
					offscreenGraph.saveImage(new BufferedOutputStream(new FileOutputStream(file)),
							null);
				} catch (Exception e) {
					reporter.error(new IOMessageImpl("Can not create image", e));
				} finally {
					offscreenGraph.dispose();
				}
			}
		});
	}

	private Dimension computeSize(Graph graph) {
		@SuppressWarnings("unchecked")
		List<GraphNode> graphNodes = graph.getNodes();
		int height = 0;
		int width = 0;
		List<GraphNode> tempSourceList = new ArrayList<GraphNode>();
		List<GraphNode> tempTargetList = new ArrayList<GraphNode>();
		for (GraphNode node : graphNodes) {
			int sourceConnections = node.getSourceConnections().size();
			int targetConnections = node.getTargetConnections().size();
			if (sourceConnections == 0 && targetConnections == 1) {
				tempSourceList.add(node);
			}
			else if (sourceConnections >= 1 && targetConnections >= 1) {
				width = width + node.getFigure().getBounds().width + 10;
				height = height + node.getFigure().getBounds().height;
			}
			else {
				tempTargetList.add(node);
			}
		}
		int accuSourceWidth = 0;
		int accuSourceHeight = 0;
		int accuHeight = 0;
		for (GraphNode node : tempSourceList) {
			Rectangle rec = node.getFigure().getBounds();
			int sourceWidth = rec.width;
			int sourceHeight = rec.height;

			accuSourceHeight = accuSourceHeight + sourceHeight + 10;

			if (accuSourceWidth < sourceWidth) {
				accuSourceWidth = sourceWidth;
			}
			if (accuHeight < accuSourceHeight) {
				accuHeight = accuSourceHeight;
			}

		}

		int accuTargetWidth = 0;
		int accuTargetHeight = 0;
		for (GraphNode node : tempTargetList) {
			Rectangle rec = node.getFigure().getBounds();
			int targetWidth = rec.width;
			int targetHeight = rec.height;

			accuTargetHeight = accuTargetHeight + targetHeight + 10;

			if (accuTargetWidth < targetWidth) {
				accuTargetWidth = targetWidth;
			}
			if (accuHeight < accuTargetHeight) {
				accuHeight = accuTargetHeight;
			}
		}
		width = width + accuSourceWidth + accuTargetWidth + 30;
		height = accuHeight + 15;

		Dimension dimension = new Dimension();
		dimension.setSize(width, height);

		return dimension;
	}

	private File getInputFile(URL url) throws IOException, FileNotFoundException {
		File file = new File(tempDir.toString() + FilenameUtils.getName(url.toString()));
		OutputStream outputStream = new FileOutputStream(file);
		IOUtils.copy(url.openStream(), outputStream);
		outputStream.close();
		return file;
	}

	private IOReport reportError(IOReporter reporter, String message, Exception e) {
		reporter.error(new IOMessageImpl(message, e));
		reporter.setSuccess(false);
		return reporter;
	}
}
