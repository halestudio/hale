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
package eu.esdihumboldt.hale.rcp.views.mapping;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;

/**
 * Label provider for {@link TableItem}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TableItemLabelProvider extends LabelProvider implements
		ITableLabelProvider, ITableFontProvider {
	
	private Font boldFont;

	/**
	 * @see ITableLabelProvider#getColumnImage(Object, int)
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	/**
	 * @see ITableLabelProvider#getColumnText(Object, int)
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof TableItem) {
			return ((TableItem) element).getValue(columnIndex);
		}
		
		return null;
	}

	/**
	 * @see ITableFontProvider#getFont(Object, int)
	 */
	@Override
	public Font getFont(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			if (boldFont == null) {
				Font font = JFaceResources.getDefaultFont();
				FontData data = font.getFontData()[0];
				data = new FontData(data.getName(), data.getHeight(), SWT.BOLD);
				boldFont = new Font(font.getDevice(), data);
			}
			return boldFont;
		default:
			return JFaceResources.getDefaultFont();
		}
	}

	/**
	 * @see BaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		if (boldFont != null) {
			boldFont.dispose();
		}
		
		super.dispose();
	}

}
