/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
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
import eu.esdihumboldt.hale.ui.common.graph.content.CellGraphContentProvider;
import eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider;
import eu.esdihumboldt.hale.ui.util.DisplayThread;
import eu.esdihumboldt.hale.ui.util.graph.OffscreenGraph;
import eu.esdihumboldt.util.Identifiers;

/**
 * Export a Mapping to HTML for documentation purposes.
 * 
 * @author Kevin Mais
 */
public class HtmlMappingExporter extends AbstractAlignmentWriter implements ProjectInfoAware {

	private VelocityContext context;
	private VelocityEngine ve;
	private ProjectInfo pi;
	private File file_template;
	private File tempDir;
	private Alignment alignment = null;
	private Identifiers<Cell> cellIds;

	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.project.ProjectInfoAware#setProjectInfo(eu.esdihumboldt.hale.common.core.io.project.ProjectInfo)
	 */
	@Override
	public void setProjectInfo(ProjectInfo projectInfo) {
		pi = projectInfo;
	}

	@Override
	protected String getDefaultTypeName() {
		return "HTML mapping documentation";
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		context = new VelocityContext();
		cellIds = new Identifiers<Cell>(Cell.class, false);

		alignment = getAlignment();

		Template template = null;
		URL headlinePath = this.getClass().getResource("bg-headline.png"); //$NON-NLS-1$
		URL cssPath = this.getClass().getResource("style.css"); //$NON-NLS-1$
		URL linkPath = this.getClass().getResource("int_link.png"); //$NON-NLS-1$
		final String filesSubDir = FilenameUtils.removeExtension(FilenameUtils.getName(getTarget()
				.getLocation().getPath())) + "_files"; //$NON-NLS-1$
		final File filesDir = new File(FilenameUtils.getFullPath(getTarget().getLocation()
				.getPath()), filesSubDir);

		filesDir.mkdirs();
		context.put("filesDir", filesSubDir);

		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// generates a byteArray out of the style sheet
		byte[] cssByteArray = null;
		try {
			cssByteArray = this.urlToByteArray(cssPath);
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		// Create CSS export file
		File cssOutputFile = new File(filesDir, "style.css"); //$NON-NLS-1$
		try {
			this.byteArrayToFile(cssOutputFile, cssByteArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// generates a byteArray out of the headline picture
		byte[] headlineByteArray = null;
		try {
			headlineByteArray = this.urlToByteArray(headlinePath);
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		// Create headline picture

		File headlineOutputFile = new File(filesDir, "bg-headline.png"); //$NON-NLS-1$
		try {
			byteArrayToFile(headlineOutputFile, headlineByteArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// generates a byteArray out of the link picture
		byte[] linkByteArray = null;
		try {
			linkByteArray = this.urlToByteArray(linkPath);
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		// Create link picture

		File linkOutputFile = new File(filesDir, "int_link.png"); //$NON-NLS-1$
		try {
			this.byteArrayToFile(linkOutputFile, linkByteArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		File htmlExportFile = new File(getTarget().getLocation().getPath());
		if (pi != null) {
			Date date = new Date();
			DateFormat dfm = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
//			SimpleDateFormat dfm = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"); //$NON-NLS-1$
			if (getTarget().getLocation() == null) {
				return null;
			}

			// associate variables with information data
			String exportDate = dfm.format(date);
			context.put("exportDate", exportDate);

			if (pi.getCreated() != null) {
				String created = dfm.format(pi.getCreated());
				context.put("createdDate", created);
			}

			context.put("pi", pi);
		}

		if (alignment != null) {
			Collection<TypeCellInfo> typeCellInfos = new ArrayList<TypeCellInfo>();
			Collection<? extends Cell> cells = alignment.getTypeCells();
			Iterator<? extends Cell> it = cells.iterator();
			while (it.hasNext()) {
				final Cell cell = it.next();
				// this is the collection of type cell info
				TypeCellInfo tci = new TypeCellInfo(cell, alignment, cellIds, filesSubDir);
				typeCellInfos.add(tci);
			}
			// the full collection of type cell info put to the context (for the
			// template)
			context.put("typeCellInfos", typeCellInfos);
			createImages(filesDir);
		}

		try {
			template = ve.getTemplate(file_template.getName(), "UTF-8");
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (template != null) {
			FileWriter fw = new FileWriter(htmlExportFile);
			template.merge(context, fw);
			fw.close();
		}

		// delete tempDir for cleanup
		tempDir.deleteOnExit();

		return null;
	}

	/**
	 * Initialize temporary directory and template engine.
	 * 
	 * @throws Exception if an error occurs during the initialization
	 */
	private void init() throws Exception {
		synchronized (this) {
			if (ve == null) {
				ve = new VelocityEngine();
				// create a temporary directory
				tempDir = Files.createTempDir();

				file_template = new File(tempDir, "template.vm");
				URL templatePath = this.getClass().getResource("template.html");
				OutputStream fos = new BufferedOutputStream(new FileOutputStream(file_template));
				InputStream stream = templatePath.openStream();

				// copys the InputStream into FileOutputStream
				IOUtils.copy(stream, fos);

				stream.close();
				fos.close();

				ve.setProperty("file.resource.loader.path", tempDir.getAbsolutePath());
				// initialize VelocityEngine
				ve.init();
			}
		}
	}

	private void byteArrayToFile(File file, byte[] byteArray) throws FileNotFoundException,
			IOException {
		if (byteArray != null) {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(byteArray);
			fileOutputStream.close();
		}
	}

	private byte[] urlToByteArray(URL url) throws Exception, IOException,
			UnsupportedEncodingException {
		URLConnection connection = url.openConnection();
		int contentLength = connection.getContentLength();
		InputStream inputStream = url.openStream();
		byte[] data = new byte[contentLength];
		inputStream.read(data);
		inputStream.close();
		return data;
	}

	private void createImages(File filesDir) {

		Collection<? extends Cell> _cells = alignment.getCells();
		Iterator<? extends Cell> ite = _cells.iterator();
		while (ite.hasNext()) {
			Cell _cell = ite.next();
			saveImageToFile(_cell, filesDir);
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
				OffscreenGraph off_graph = new OffscreenGraph(600, 200) {

					@Override
					protected void configureViewer(GraphViewer viewer) {
						IContentProvider cgcp = new CellGraphContentProvider();
						GraphLabelProvider glp = new GraphLabelProvider();
						viewer.setContentProvider(cgcp);
						viewer.setLabelProvider(glp);
						viewer.setInput(cell);
					}
				};

				Graph graph = off_graph.getGraph();
				Dimension dim = computeSize(graph);
				int width;
				if (dim.width > 600) {
					width = dim.width;
				}
				else {
					// minimum width = 600
					width = 600;
				}

				int height = dim.height;

				off_graph.resize(width, height);

				try {
					off_graph.saveImage(new BufferedOutputStream(new FileOutputStream(file)), null);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					off_graph.dispose();
				}
			}
		});
	}

	private Dimension computeSize(Graph graph) {
		@SuppressWarnings("unchecked")
		List<GraphNode> list = graph.getNodes();
		int height = 0;
		int width = 0;
		List<GraphNode> tempSourceList = new ArrayList<GraphNode>();
		List<GraphNode> tempTargetList = new ArrayList<GraphNode>();
		for (GraphNode gn : list) {
			int sourceCons = gn.getSourceConnections().size();
			int targetCons = gn.getTargetConnections().size();
			if (sourceCons == 0 && targetCons == 1) {
				tempSourceList.add(gn);
			}
			else if (sourceCons >= 1 && targetCons >= 1) {
				width = width + gn.getFigure().getBounds().width + 10;
				height = height + gn.getFigure().getBounds().height;
			}
			else {
				tempTargetList.add(gn);
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

		Dimension d = new Dimension();
		d.setSize(width, height);

		return d;
	}
}
