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

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXParseException;

import eu.esdihumboldt.hale.gmlvalidate.Report;

/**
 * The model for {@link ReportView#viewer}.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class ReportModel {
	
	private List<SAXParseException> warning = new ArrayList<SAXParseException>();
	private List<SAXParseException> error = new ArrayList<SAXParseException>();
	
	/**
	 * Constructor.
	 * 
	 * @param report report to analyze
	 */
	public ReportModel(Report report) {
		this.setSAXParseExceptionError(report.getErrors());
		this.setSAXParseExceptionWarning(report.getWarnings());
	}
	
	/**
	 * Returns a sorted list with all warnings.
	 * 
	 * @return sorted list
	 */
	public List<TransformationResultItem> getWarnings() {
		return this.getList(this.warning);
	}
	
	/**
	 * Returns a sorted list wit all errors.
	 * 
	 * @return sorted list
	 */
	public List<TransformationResultItem> getErrors() {
		return this.getList(this.error);
	}
	
	/**
	 * Creates a sorted list with a limitation to 10 lines per
	 * message.
	 * 
	 * @param list the list to sort
	 * 
	 * @return sorted list
	 */
	private List<TransformationResultItem> getList(List<SAXParseException> list) {
		// contains the new list
		List<TransformationResultItem> items = new ArrayList<TransformationResultItem>();
		
		boolean added;
		// iterate through all items
		for (SAXParseException e : list) {
			added = false;
			// check if it's already added
			for (TransformationResultItem i : items) {
				if (i.getMessage().equals(e.getLocalizedMessage())) {
					// are there already 10 entries?
					if (i.getLines().size() >= 10) {
						// do not add it and skip
						added = true;
						break;
					}
					
					// add the line and end for
					i.addLine(e.getLineNumber());
					added = true;
					break;
				}
			}
			
			// item not added
			if (!added) {
				items.add(new TransformationResultItem(e.getLocalizedMessage(), e.getLineNumber()));
			}
		}
		
		return items;
	}
	
	/**
	 * Setter.
	 * 
	 * @param warning the warning list
	 */
	public void setSAXParseExceptionWarning(List<SAXParseException> warning) {
		this.warning = warning;
	}

	/**
	 * Setter.
	 * 
	 * @param error the error list
	 */
	public void setSAXParseExceptionError(List<SAXParseException> error) {
		this.error = error;
	}
}
