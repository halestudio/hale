package eu.esdihumboldt.hale.eap2uml.eapconverter;

import java.util.HashMap;
import java.util.Map;

import org.openarchitectureware.workflow.WorkflowRunner;
import org.openarchitectureware.workflow.monitor.NullProgressMonitor;

public class EapToUml2 {
	static WorkflowRunner wfrunner = new WorkflowRunner();
	public static void main(String[] args)
	{
		Map<String,String> properties = new HashMap<String,String>();
		Map slotContents = new HashMap();
		
		boolean retrn= wfrunner.run("src//main//java//eu//esdihumboldt//hale//eap2uml//eapconverter//workflow.oaw", new NullProgressMonitor(),properties,slotContents);
		if(retrn)
			System.out.println("Conversion successful");
		else
			System.out.println("Conversion error");
	}
}
