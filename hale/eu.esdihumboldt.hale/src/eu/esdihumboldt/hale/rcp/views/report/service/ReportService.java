package eu.esdihumboldt.hale.rcp.views.report.service;

import java.util.ArrayList;

import eu.esdihumboldt.hale.gmlvalidate.Report;
import eu.esdihumboldt.hale.models.UpdateService;
import eu.esdihumboldt.hale.rcp.views.report.ReportModel;


public interface ReportService extends UpdateService {
	ArrayList<Report> reports = new ArrayList<Report>();
	
	public void addReport(Report report);
	public ReportModel getLastReport();
}
