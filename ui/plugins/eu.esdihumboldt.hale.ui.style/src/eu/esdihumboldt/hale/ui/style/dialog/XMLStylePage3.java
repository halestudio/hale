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
package eu.esdihumboldt.hale.ui.style.dialog;

import java.io.StringReader;

import javax.xml.transform.TransformerException;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.xml.styling.SLDParser;
import org.geotools.xml.styling.SLDTransformer;

import eu.esdihumboldt.hale.ui.style.internal.Messages;

/**
 * Page for editing a style as XML
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class XMLStylePage3 extends FeatureStylePage {

	private final StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);

	private SourceViewer viewer;

	private boolean changed = false;

	/**
	 * Create a XML style editor page
	 * 
	 * @param parent the parent dialog
	 */
	public XMLStylePage3(FeatureStyleDialog parent) {
		super(parent, Messages.XMLStylePage3_SuperTitle);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		changed = false;

		final Display display = parent.getDisplay();

		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.VERTICAL;
		parent.setLayout(fillLayout);

		CompositeRuler ruler = new CompositeRuler(3);
		LineNumberRulerColumn lineNumbers = new LineNumberRulerColumn();
		lineNumbers.setBackground(display.getSystemColor(SWT.COLOR_GRAY)); // SWT.COLOR_INFO_BACKGROUND));
		lineNumbers.setForeground(display.getSystemColor(SWT.COLOR_BLACK)); // SWT.COLOR_INFO_FOREGROUND));
		lineNumbers.setFont(JFaceResources.getTextFont());
		ruler.addDecorator(0, lineNumbers);

		viewer = new SourceViewer(parent, ruler, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);

		viewer.getTextWidget().setFont(JFaceResources.getTextFont());

		SourceViewerConfiguration conf = new SourceViewerConfiguration();
		viewer.configure(conf);

		SLDTransformer trans = new SLDTransformer();
		trans.setIndentation(2);
		String xml;
		try {
			xml = trans.transform(getParent().getStyle());
		} catch (TransformerException e) {
			xml = "Error: " + e.getMessage(); //$NON-NLS-1$
		}
		IDocument doc = new Document();
		doc.set(xml);
		doc.addDocumentListener(new IDocumentListener() {

			@Override
			public void documentChanged(DocumentEvent event) {
				changed = true;
			}

			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
				// ignore
			}
		});
		viewer.setInput(doc);

		setControl(viewer.getControl());
	}

	/**
	 * @see FeatureStylePage#getStyle(boolean)
	 */
	@Override
	public Style getStyle(boolean force) throws Exception {
		if (viewer == null || (!force && !changed)) {
			return null;
		}

		IDocument doc = viewer.getDocument();

		SLDParser parser = new SLDParser(styleFactory, new StringReader(doc.get()));
		Style[] styles = parser.readXML();

		return styles[0];
	}

}
