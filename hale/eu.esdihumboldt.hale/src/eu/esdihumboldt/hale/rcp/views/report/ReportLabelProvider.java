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

package eu.esdihumboldt.hale.rcp.views.report;

import org.eclipse.jface.viewers.LabelProvider;
import org.xml.sax.SAXParseException;

/**
 * LabelProvider for {@link ReportView#viewer}.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class ReportLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		String output;
		if (element instanceof SAXParseException) {
			SAXParseException e = (SAXParseException) element;
			
			output = e.getLocalizedMessage()+" at line "+e.getLineNumber();
		}
		else if (element instanceof TransformationResultItem) {
			output = ((TransformationResultItem) element).getMessage();
		}
		else {
			if (((String)element).startsWith("Warning") ||
					((String)element).startsWith("Error")	) {
				output = element.toString();
			} else {
				output = "line "+element.toString();
			}
		}
		
		return output;
	}
}
