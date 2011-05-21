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

package eu.esdihumboldt.hale.ui.views.report.service;

import eu.esdihumboldt.hale.io.xml.validator.Report;
import eu.esdihumboldt.hale.models.UpdateService;
import eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportReport;
import eu.esdihumboldt.hale.ui.views.report.ReportModel;

/**
 * Interface for {@link ReportServiceImpl}.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface ReportService extends UpdateService {
	/**
	 * Add a new {@link Report} to the list.
	 * 
	 * @param report the report
	 */
	public void addReport(Report report);
	
	/**
	 * Add a new {@link MappingExportReport} to the list.
	 * 
	 * @param report the report
	 */
	public void addReport(MappingExportReport report);
	
	/**
	 * Getter for the last added {@link Report}.
	 * 
	 * @return the last {@link Report}
	 */
	public ReportModel getLastReport();
	
	/**
	 * Getter for the requested {@link Report} at a specific position.
	 * 
	 * @param index the position
	 * 
	 * @return the requested {@link Report}.
	 */
	public ReportModel getReport(int index);
}
