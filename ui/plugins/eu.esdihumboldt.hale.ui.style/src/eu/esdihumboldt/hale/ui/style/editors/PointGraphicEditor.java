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

package eu.esdihumboldt.hale.ui.style.editors;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.ImageIO;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.geotools.styling.Graphic;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.StyleBuilder;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.ui.style.internal.Messages;

/**
 * Editor for external {@link Graphic}.
 * 
 * Doesn't support editing of an existing symbolizer.
 * 
 * @author Sebastian Reinhardt
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */

public class PointGraphicEditor implements Editor<PointSymbolizer> {

	private static final StyleBuilder styleBuilder = new StyleBuilder();

	private final Composite page;

	private FileDialog fd;

	private final TextViewer path;

	private final Button dialogButton;

	private final boolean changed = false;

	private URL chosenFile;

	private final Text supportText;

	private static final ALogger log = ALoggerFactory.getLogger(PointGraphicEditor.class);

	/**
	 * Creates a {@link Graphic} editor
	 * 
	 * @param parent the parent composite
	 */
	public PointGraphicEditor(Composite parent) {
		page = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		page.setLayout(layout);

		Label graphLabel = new Label(page, SWT.NONE);
		graphLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		graphLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		graphLabel.setText(Messages.PointGraphicEditor_UrlTextField);

		path = new TextViewer(page, SWT.SINGLE | SWT.BORDER);
		path.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		path.setDocument(new Document());

		dialogButton = new Button(page, SWT.PUSH);
		dialogButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		dialogButton.setText(Messages.PointGraphicEditor_FileDialogButton);
		dialogButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				openFileDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}
		});

		Label supportLabel = new Label(page, SWT.NONE);
		supportLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		supportLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		supportLabel.setText("\n" + Messages.PointGraphicEditor_SupportedTypes);

		supportText = new Text(page, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		supportText.setLayoutData(new GridData(SWT.BOTTOM, SWT.CENTER, false, false, 2, 1));
		supportText.setText(new HashSet<String>(Arrays.asList(ImageIO.getReaderMIMETypes()))
				.toString());
	}

	/**
	 * Opens a filedialog for the user to select a file and saves its path in a
	 * field
	 */
	private void openFileDialog() {
		fd = new FileDialog(Display.getCurrent().getActiveShell());
		fd.open();
		File file = new File(fd.getFilterPath() + "/" + fd.getFileName());

		try {
			path.getDocument().set(file.toURI().toURL().toString());
		} catch (MalformedURLException e) {
			log.userError(e.getMessage());
		}
	}

	/**
	 * generates the string representation of the MIME-Type of a selected file
	 * 
	 * @param url the URL of a selected file
	 * @return the format of the file as a string
	 */
	private String getFormat(String url) {
		Set<String> supportedGraphicFormats = new HashSet<String>(Arrays.asList(ImageIO
				.getReaderMIMETypes()));
		String extension = url.substring(url.lastIndexOf(".") + 1);

		Iterator<String> it = supportedGraphicFormats.iterator();
		String format = null;

		while (it.hasNext()) {
			String next = it.next();
			if (next.contains(extension.toLowerCase())) {
				format = next;
			}
		}

		if (format == null) {
			log.userError("PointGraphicEditor_ErrorMessageFormat");
			return "";
		}
		else
			return format;

	}

	/**
	 * @see Editor#getControl()
	 */
	@Override
	public Control getControl() {
		return page;
	}

	/**
	 * @see Editor#getValue()
	 */
	@Override
	public PointSymbolizer getValue() throws Exception {

		try {
			chosenFile = new URL(path.getDocument().get());
		} catch (MalformedURLException e) {
			throw new IllegalStateException(Messages.PointGraphicEditor_ErrorMessageFile, e);
		}

		return styleBuilder.createPointSymbolizer(styleBuilder.createGraphic(
				styleBuilder.createExternalGraphic(chosenFile.toString(),
						getFormat(chosenFile.toString())), null, null));
	}

	/**
	 * @see Editor#isChanged()
	 */
	@Override
	public boolean isChanged() {
		return changed;
	}

	/**
	 * @see Editor#setValue(Object)
	 */
	@Override
	public void setValue(PointSymbolizer pointSym) {
		// unused
	}

}
