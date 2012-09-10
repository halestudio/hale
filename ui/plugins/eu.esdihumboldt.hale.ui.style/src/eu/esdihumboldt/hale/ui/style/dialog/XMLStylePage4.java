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
package eu.esdihumboldt.hale.ui.style.dialog;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.swt.widgets.Composite;
import org.geotools.styling.Style;

import eu.esdihumboldt.hale.ui.style.internal.Messages;

/**
 * Page for editing a style as XML.
 * 
 * Using WST plugins, disabled because this dependency adds unwanted
 * contributions to the menu (e.g. Search menu).
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class XMLStylePage4 extends FeatureStylePage {

//	private final StyleFactory styleFactory = 
//		CommonFactoryFinder.getStyleFactory(null);

	// XXX WST - private StructuredTextViewer viewer;

	/**
	 * Create a XML style editor page
	 * 
	 * @param parent the parent dialog
	 */
	public XMLStylePage4(FeatureStyleDialog parent) {
		super(parent, Messages.XMLStylePage4_SuperTitle);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		/*
		 * XXX WST - IAnnotationAccess annotationAccess = new
		 * DefaultMarkerAnnotationAccess(); ISharedTextColors sharedTextColors =
		 * EditorsPlugin.getDefault().getSharedTextColors(); IOverviewRuler
		 * overviewRuler = new OverviewRuler(annotationAccess, 12,
		 * sharedTextColors); CompositeRuler ruler = new CompositeRuler(4);
		 * 
		 * viewer = new StructuredTextViewer( parent, ruler, overviewRuler,
		 * true, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		 * 
		 * StructuredTextViewerConfigurationXML conf = new
		 * StructuredTextViewerConfigurationXML();
		 * 
		 * SLDTransformer trans = new SLDTransformer(); trans.setIndentation(2);
		 * String xml; try { xml = trans.transform(getParent().getStyle()); }
		 * catch (TransformerException e) { xml = "Error: " + e.getMessage(); }
		 * 
		 * ((StructuredTextViewer)
		 * viewer).getTextWidget().setFont(JFaceResources
		 * .getFont("org.eclipse.wst.sse.ui.textfont")); IStructuredModel
		 * scratchModel = StructuredModelManager.getModelManager().
		 * createUnManagedStructuredModelFor
		 * (ContentTypeIdForXML.ContentTypeID_XML); IDocument document =
		 * scratchModel.getStructuredDocument(); document.set(xml);
		 * viewer.configure(conf); viewer.setDocument(document);
		 */

		/*
		 * AnnotationModel annotationModel = new AnnotationModel();
		 * annotationModel.connect(document);
		 * 
		 * SourceViewerDecorationSupport sds = new
		 * SourceViewerDecorationSupport(viewer, overviewRuler,
		 * annotationAccess, sharedTextColors);
		 * sds.install(EditorsPlugin.getDefault().getPreferenceStore());
		 * 
		 * viewer.setDocument(document, annotationModel);
		 */

		/*
		 * XXX WST - viewer.setDocument(document);
		 * 
		 * final Display display = Display.getCurrent();
		 * 
		 * LineNumberRulerColumn lineNumbers = new LineNumberRulerColumn();
		 * lineNumbers.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
		 * //SWT.COLOR_INFO_BACKGROUND));
		 * lineNumbers.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		 * //SWT.COLOR_INFO_FOREGROUND));
		 * //lineNumbers.setFont(JFaceResources.getBannerFont());
		 * ruler.addDecorator(0, lineNumbers);
		 * 
		 * setControl(viewer.getControl());
		 */
	}

	/**
	 * @see FeatureStylePage#getStyle(boolean)
	 */
	@Override
	public Style getStyle(boolean force) throws Exception {
		// XXX WST - if (viewer == null) {
		return null;
		/*
		 * XXX WST - }
		 * 
		 * IDocument doc = viewer.getDocument();
		 * 
		 * SLDParser parser = new SLDParser(styleFactory, new
		 * StringReader(doc.get())); Style[] styles = parser.readXML();
		 * 
		 * return styles[0];
		 */
	}

}
