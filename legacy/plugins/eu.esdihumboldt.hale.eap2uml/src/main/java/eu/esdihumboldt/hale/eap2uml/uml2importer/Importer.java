package eu.esdihumboldt.hale.eap2uml.uml2importer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.uml2.uml.*;
import org.geotools.xml.EMFUtils;


public class Importer {
	
	public static boolean load(File xmlfile) {
		  // Create a resource set.
		  ResourceSet resourceSet = new ResourceSetImpl();
		  // Register the default resource factory -- only needed for stand-alone!
		  //resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
		  resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl()); //$NON-NLS-1$
		  // This is a patch to allow UML2 2.1.0 functionality
		  resourceSet.getPackageRegistry().put("http://www.eclipse.org/uml2/2.1.0/UML", UMLPackage.eINSTANCE); //$NON-NLS-1$
		  // Get the URI of the model file.
		  URI fileURI = URI.createFileURI(xmlfile.getAbsolutePath());
		  		  
		  Resource resource = resourceSet.getResource(fileURI, true);	  
		  
		  // Load the resource into EObject
		  try {
			  
			  resource.load(null);
			  
			  List<EObject> contenidos = resource.getContents();
			  for (int i=0;i<contenidos.size();i++)
			  {
				  EObject ob = contenidos.get(i);
				  System.out.println("Name: "+EMFUtils.get(ob, "name")); //$NON-NLS-1$ //$NON-NLS-2$
				  System.out.println("Type: "+ob.eClass().getName()); //$NON-NLS-1$
				  List<EObject> obs=ob.eContents();
				  for (int j=0;j<obs.size();j++)
				  {
					  EObject obss = obs.get(j);
					  System.out.println("Name: "+EMFUtils.get(obss, "name")); //$NON-NLS-1$ //$NON-NLS-2$
					  System.out.println("Type: "+obss.eClass().getName()); //$NON-NLS-1$
				  }
			  }
			  System.out.println("Charged succesful"); //$NON-NLS-1$
			  return true;
		  } catch (IOException e) 
		  {
			  System.out.println("Error while charging xmi/xml file"); //$NON-NLS-1$
			  return false;
		  }
		}
}
