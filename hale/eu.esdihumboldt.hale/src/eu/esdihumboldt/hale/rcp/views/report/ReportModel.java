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
import java.util.Map.Entry;

import org.xml.sax.SAXParseException;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.transformer.CellUtils;
import eu.esdihumboldt.hale.gmlvalidate.Report;
import eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportReport;

/**
 * The model for {@link ReportView#viewer}.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class ReportModel {
	
	@SuppressWarnings("rawtypes")
	private List warning = new ArrayList();
	
	@SuppressWarnings("rawtypes")
	private List error = new ArrayList();
	
	private String identifier = "";
	
	/**
	 * Constructor.
	 * 
	 * @param report report to analyze
	 */
	public ReportModel(Report report) {
		this.setSAXParseExceptionError(report.getErrors());
		this.setSAXParseExceptionWarning(report.getWarnings());
		
		this.identifier = "GML Export";
	}
	
	/**
	 * Constructor.
	 * 
	 * @param report report to analyze
	 */
	@SuppressWarnings("unchecked")
	public ReportModel(MappingExportReport report) {
		for (Entry<ICell, String> entry : report.getFailed().entrySet()) {
			this.error.add(entry.getValue()+" "+CellUtils.asString(entry.getKey()));
		}
		
		for (Entry<ICell, String> entry : report.getWarnings().entrySet()) {
			this.warning.add(entry.getValue()+" "+CellUtils.asString(entry.getKey()));
		}
		
		this.identifier = "Mapping Export";
	}
	
	/**
	 * Returns a sorted list with all warnings.
	 * 
	 * @return sorted list
	 */
	@SuppressWarnings("unchecked")
	public List<TransformationResultItem> getWarnings() {
		if (!this.warning.isEmpty() && this.warning.get(0) instanceof SAXParseException) {
			return this.getList((ArrayList<SAXParseException>) this.warning);
		}
		return this.getListString(this.warning);
	}
	
	/**
	 * Returns a sorted list wit all errors.
	 * 
	 * @return sorted list
	 */
	@SuppressWarnings("unchecked")
	public List<TransformationResultItem> getErrors() {
		if (!this.warning.isEmpty() && this.warning.get(0) instanceof SAXParseException) {
			return this.getList((ArrayList<SAXParseException>) this.error);
		}
		return this.getListString(this.error);
	}
	
	/**
	 * 
	 * @return the identifier
	 */
	public String getIdentifier() {
		return this.identifier;
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
	 * Creates a sorted list.
	 * 
	 * @param list the list to sort
	 * 
	 * @return sorted list
	 */
	private List<TransformationResultItem> getListString(List<String> list) {
		List<TransformationResultItem> items = new ArrayList<TransformationResultItem>();
		
		for (String str : list) {
			items.add(new TransformationResultItem(str, ""));
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
