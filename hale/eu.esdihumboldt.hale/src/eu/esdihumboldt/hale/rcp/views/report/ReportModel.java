package eu.esdihumboldt.hale.rcp.views.report;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXParseException;

import eu.esdihumboldt.hale.gmlvalidate.Report;

public class ReportModel {
	
	private List<SAXParseException> warning = new ArrayList<SAXParseException>();
	private List<SAXParseException> error = new ArrayList<SAXParseException>();
	
	public ReportModel(Report report) {
		this.setSAXParseExceptionError(report.getErrors());
		this.setSAXParseExceptionWarning(report.getWarnings());
	}
	
	
	public List<TransformationResultItem> getWarnings() {
		List<TransformationResultItem> items = new ArrayList<TransformationResultItem>();
		
		boolean added;
		for (SAXParseException e : this.warning) {
			added = false;
			for (TransformationResultItem i : items) {
				if (i.getMessage().equals(e.getLocalizedMessage())) {
					i.addLine(e.getLineNumber());
					added = true;
					break;
				}
			}
			
			if (!added) {
				items.add(new TransformationResultItem(e.getLocalizedMessage(), e.getLineNumber()));
			}
		}
		
		
		return items;
	}
	
	public List<TransformationResultItem> getErrors() {
		List<TransformationResultItem> items = new ArrayList<TransformationResultItem>();
		
		boolean added;
		for (SAXParseException e : this.error) {
			added = false;
			for (TransformationResultItem i : items) {
				if (i.getMessage().equals(e.getLocalizedMessage())) {
					i.addLine(e.getLineNumber());
					added = true;
					break;
				}
			}
			
			if (!added) {
				items.add(new TransformationResultItem(e.getLocalizedMessage(), e.getLineNumber()));
			}
		}
		
		
		return items;
	}
	
	public void setSAXParseExceptionWarning(List<SAXParseException> warning) {
		this.warning = warning;
	}

	public void setSAXParseExceptionError(List<SAXParseException> error) {
		this.error = error;
	}
}
