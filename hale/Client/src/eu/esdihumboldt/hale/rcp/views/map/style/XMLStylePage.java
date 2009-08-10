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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
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
public class XMLStylePage extends FeatureStylePage {
	
	private final StyleFactory styleFactory = 
		CommonFactoryFinder.getStyleFactory(null);
	
	private Text text;
	
	/**
	 * Create a XML style editor page
	 * 
	 * @param parent the parent dialog
	 */
	public XMLStylePage(FeatureStyleDialog parent) {
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
		
		text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		text.setEditable(true);
		//text.setEnabled(true);
		
		SLDTransformer trans = new SLDTransformer();
		trans.setIndentation(2);
		String xml;
		try {
			xml = trans.transform(getParent().getStyle());
		} catch (TransformerException e) {
			xml = "Error: " + e.getMessage();
		}
		text.setText(xml);
	}

	/**
	 * @see FeatureStylePage#getStyle()
	 */
	@Override
	public Style getStyle() throws Exception {
		if (text == null) {
			return null;
		}
		
		SLDParser parser = new SLDParser(styleFactory, new StringReader(text.getText()));
		Style[] styles = parser.readXML();
		
		return styles[0];
	}

}
