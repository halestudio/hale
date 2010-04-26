package eu.esdihumboldt.hale.eap2uml.eapconverter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.openarchitectureware.workflow.WorkflowRunner;
import org.openarchitectureware.workflow.monitor.NullProgressMonitor;

public class EapToUml2 {
	static WorkflowRunner wfrunner = new WorkflowRunner();
	public static boolean Convert(File eap_file, File model_file, String eap_package)
	{
		Map<String,String> slotContents = new HashMap<String,String>();
		Map<String,String> properties = new HashMap<String,String>();
		properties.put("ea_file",eap_file.getPath());
		properties.put("model_file",model_file.getPath());
		properties.put("model_pkg",eap_package);
		boolean retrn= wfrunner.run("src//main//java//eu//esdihumboldt//hale//eap2uml//eapconverter//workflow.oaw", new NullProgressMonitor(),properties,slotContents);
		return retrn;
	}
}
