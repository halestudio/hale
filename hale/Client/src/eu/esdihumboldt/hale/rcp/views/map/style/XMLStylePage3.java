/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.views.map.style;

import java.io.StringReader;

import javax.xml.transform.TransformerException;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.SLDParser;
import org.geotools.styling.SLDTransformer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;

/**
 * Page for editing a style as XML
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 *
 */
public class XMLStylePage3 extends FeatureStylePage {
	
	private final StyleFactory styleFactory = 
		CommonFactoryFinder.getStyleFactory(null);
	
	private SourceViewer viewer;
	
	/**
	 * Create a XML style editor page
	 * 
	 * @param parent the parent dialog
	 */
	public XMLStylePage3(FeatureStyleDialog parent) {
		super(parent, "XML");
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.VERTICAL;
		parent.setLayout(fillLayout);
		
		CompositeRuler ruler = new CompositeRuler(4);
		ruler.addDecorator(0, new LineNumberRulerColumn());
		
		viewer = new SourceViewer(parent, ruler, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		viewer.getTextWidget().setFont(JFaceResources.getFont("org.eclipse.wst.sse.ui.textfont"));
		
		SourceViewerConfiguration conf = new TextSourceViewerConfiguration();
		viewer.configure(conf);
		
		SLDTransformer trans = new SLDTransformer();
		trans.setIndentation(2);
		String xml;
		try {
			xml = trans.transform(getParent().getStyle());
		} catch (TransformerException e) {
			xml = "Error: " + e.getMessage();
		}
		IDocument doc = new Document(); //StructuredModelManager.getModelManager().createStructuredDocumentFor(ContentTypeIdForXML.ContentTypeID_XML);
		doc.set(xml);
		viewer.setInput(doc);
	}

	/**
	 * @see FeatureStylePage#getStyle()
	 */
	@Override
	public Style getStyle() throws Exception {
		if (viewer == null) {
			return null;
		}
		
		IDocument doc = viewer.getDocument();
		
		SLDParser parser = new SLDParser(styleFactory, new StringReader(doc.get()));
		Style[] styles = parser.readXML();
		
		return styles[0];
	}

}
