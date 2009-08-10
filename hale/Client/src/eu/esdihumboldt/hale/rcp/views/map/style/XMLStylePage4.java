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
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.OverviewRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;
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
public class XMLStylePage4 extends FeatureStylePage {
	
	private final StyleFactory styleFactory = 
		CommonFactoryFinder.getStyleFactory(null);
	
	private StructuredTextViewer viewer;
	
	/**
	 * Create a XML style editor page
	 * 
	 * @param parent the parent dialog
	 */
	public XMLStylePage4(FeatureStyleDialog parent) {
		super(parent, "XML (src)");
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.VERTICAL;
		parent.setLayout(fillLayout);
		
		IAnnotationAccess annotationAccess = new DefaultMarkerAnnotationAccess();
		ISharedTextColors sharedTextColors = EditorsPlugin.getDefault().getSharedTextColors();
		IOverviewRuler overviewRuler = new OverviewRuler(annotationAccess, 12, sharedTextColors);
		CompositeRuler ruler = new CompositeRuler(4);
		
		viewer = new StructuredTextViewer(
				parent, 
				ruler,
				overviewRuler,
				true,
				SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		StructuredTextViewerConfigurationXML conf = new StructuredTextViewerConfigurationXML();
		
		SLDTransformer trans = new SLDTransformer();
		trans.setIndentation(2);
		String xml;
		try {
			xml = trans.transform(getParent().getStyle());
		} catch (TransformerException e) {
			xml = "Error: " + e.getMessage();
		}
		
		((StructuredTextViewer) viewer).getTextWidget().setFont(JFaceResources.getFont("org.eclipse.wst.sse.ui.textfont"));
		IStructuredModel scratchModel = StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		IDocument document = scratchModel.getStructuredDocument();
		document.set(xml);
		viewer.configure(conf);
		viewer.setDocument(document);
		
		/*AnnotationModel annotationModel = new AnnotationModel();
		annotationModel.connect(document);
		
		SourceViewerDecorationSupport sds = new SourceViewerDecorationSupport(viewer, overviewRuler, annotationAccess, sharedTextColors);
		sds.install(EditorsPlugin.getDefault().getPreferenceStore());
		
		viewer.setDocument(document, annotationModel);*/
		
		viewer.setDocument(document);
		
		final Display display = Display.getCurrent();
		
		LineNumberRulerColumn lineNumbers = new LineNumberRulerColumn();
		lineNumbers.setBackground(display.getSystemColor(SWT.COLOR_GRAY)); //SWT.COLOR_INFO_BACKGROUND));
		lineNumbers.setForeground(display.getSystemColor(SWT.COLOR_BLACK)); //SWT.COLOR_INFO_FOREGROUND));
		//lineNumbers.setFont(JFaceResources.getBannerFont());
		ruler.addDecorator(0, lineNumbers);
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
