package eu.esdihumboldt.hale.rcp.views.report;

import org.eclipse.jface.viewers.LabelProvider;
import org.xml.sax.SAXParseException;

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
