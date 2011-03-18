package eu.esdihumboldt.hale.eap2uml.xsltprocess;
import java.io.*;
public class xsltprocess {
	
	
	//Main method
	public static void main(String[] args)
    throws javax.xml.transform.TransformerException, IOException 
    {
		if (args.length < 2) {
			System.err.println("Usage:"); //$NON-NLS-1$
			System.err.println(" java " + Process.class.getName( ) //$NON-NLS-1$
					+ " xmlFileName xsltFileName1 xsltFileName2..."); //$NON-NLS-1$
			System.exit(1);
		}

		// Source xml File
		File xmlFile = new File("src/main/resources/"+args[0]); //$NON-NLS-1$
		
		// Creation of an array of Files containing xslt files passed as argument
		File[] xslts = new File[args.length-1];
		for (int i=1;i<args.length;i++)
		{
			xslts[i-1]=new File("src/main/resources/"+args[i]); //$NON-NLS-1$
		}
  
		//Call to function chaintransform
		chaintransform(xslts, xmlFile);
   }
	
	public static void chaintransform(File[] xslts, File source)throws javax.xml.transform.TransformerException, IOException
	{
		// Creation of Source (java Transform API class) containing XML file
		javax.xml.transform.Source xmlSource=new javax.xml.transform.stream.StreamSource(source);
		
		// Creation of Source (java Transform API class) containing first XSLT passed
		javax.xml.transform.Source xsltSource=new javax.xml.transform.stream.StreamSource(xslts[0]);
		
		// Creation of Result (java Transform API class)
		File resultfile = new File("Output.xml"); //$NON-NLS-1$
		resultfile.createNewFile();
		javax.xml.transform.Result result=new javax.xml.transform.stream.StreamResult(resultfile);

		// create an instance of TransformerFactory
		javax.xml.transform.TransformerFactory transFact=javax.xml.transform.TransformerFactory.newInstance( );
		javax.xml.transform.Transformer trans=transFact.newTransformer(xsltSource);
		
		// Perform transformation
		trans.transform(xmlSource, result);

		// If there are more than one XSLT file, continue processing...
		int i=1;
		while(i<xslts.length)
		{
			xmlSource=new javax.xml.transform.stream.StreamSource(result.getSystemId());
			xsltSource=new javax.xml.transform.stream.StreamSource(xslts[i]);
			
			result=new javax.xml.transform.stream.StreamResult(resultfile);
			trans=transFact.newTransformer(xsltSource);
			
			trans.transform(xmlSource, result);
			i++;
		}
		
		// Finally Print out the content of result file (output.xml)
		
		FileReader reader = new FileReader(resultfile);
		BufferedReader bf = new BufferedReader(reader);
		String linea = bf.readLine();
		while (linea!=null)
        {
          	System.out.println (linea);
          	linea = bf.readLine();
        }
	}

}
