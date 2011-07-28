/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * @author Sebastian Reinhardt
 * The purpose of this tool is to enable an easy upgrade mechanism of the 
 * geotools dependencies in HALE. This class merges the different (and same) 
 * service files in the various "META-INF/services" folders of the geotools 
 * jar-files..
 */
public class Servicemerger {

	
	String source;
	String dest;
	
	/**
	 * @param source - the sourcepath of the geotools jar-files.
	 * @param dest - the destinationpath where the merged files should appear
	 */
	public Servicemerger(String source, String dest){
		this.source = source;
		this.dest = dest;

		File dir = new File(source);
			
		try {
			merge(dir);
			createJar(dest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Method for creating a jar file containing the merged servicefiles
	 * @param dest the destinationpath of the jar file
	 * @throws IOException
	 */
	private void createJar(String dest) throws IOException {
		//set paths and files
		File jar = new File(dest + java.io.File.separator + "_common_services.jar");
		File destDir = new File(dest + java.io.File.separator +
								"META-INF" + java.io.File.separator +
								"services");
		
		File[] jaredFiles = destDir.listFiles();
		
		int BUFFER_SIZE = 10240 ;
		     try {
		       byte buffer [] = new byte [ BUFFER_SIZE ] ;
		       
		       FileOutputStream stream = new FileOutputStream(jar) ;
		       JarOutputStream out = new JarOutputStream(stream, new Manifest()) ;

		       for (int i = 0 ; i < jaredFiles.length; i++) {
		         if (jaredFiles[i] == null || !jaredFiles[i].exists()
		             || jaredFiles[i].isDirectory()){
		        	 continue ; 
		         }
		           
		        // System.out.println("Adding " + jaredFiles[i].getName()) ;
		         
		         //add files as JarEntrys to the JarFile
		         JarEntry entry = new JarEntry(	"META-INF" + java.io.File.separator +
							"services" + java.io.File.separator + jaredFiles[i].getName()) ;
		         entry.setTime(jaredFiles[i].lastModified()) ;
		         out.putNextEntry(entry) ;

		         
		         FileInputStream in = new FileInputStream(jaredFiles[i]) ;
		         while(true) {
		           int nRead = in.read(buffer, 0 , buffer.length) ;
		           if(nRead <= 0)
		             break ;
		           out.write(buffer, 0 , nRead) ;
		         }
		         in.close () ;
		       }

		       out.close();
		       stream.close();
		       System.out.println("Adding completed") ;
		     } catch(Exception ex){
		       ex.printStackTrace();
		       System.out.println("Error: " + ex.getMessage ()) ;
		     }
		 }


	/**
	 * This method can be used, to list all files and subdirectorys
	 *  in a specific directory
	 * @param dir the path of the directory
	 */
	public void listDir(File dir) {

		File[] files = dir.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				System.out.print(files[i].getAbsolutePath());
				if (files[i].isDirectory()) {
					System.out.print(" (Ordner)\n");
				listDir(files[i]);					}
			else {
					System.out.print(" (Datei)\n");
				}
			}
		}
	}
	
	
	/**
	 * Reads the inputstream of a file and transforms it into a String text
	 * Used to read files directly out of a jarfile
	 * @param input the stream of the file to read
	 * @return the content of the file as a text
	 * @throws IOException
	 */
	private String readFile(InputStream input) throws IOException{
		String text = "";
		   InputStreamReader isr = 
			      new InputStreamReader(input);
			       BufferedReader reader = new BufferedReader(isr);
			       String line;
			       while ((line = reader.readLine()) != null) {
			         text += (line + "\n");
			       }	
			       reader.close();
			       return text;
	 }
	
	
	/**
	 * creates the new merged file comparing the content of 2 files
	 * @param file the first file to be compared and merged	
	 * @param text the content of the second file
	 * @throws IOException
	 */
	private void writeMergedFile(File file, String text) throws IOException{

		// Create file if it does not exist
		boolean success = file.createNewFile();
		if (success) 
		{
			// File did not exist and was created
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(text.toCharArray());
			out.close();
		} 
		//file existed, compare content and merge
		else {
			BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
			BufferedReader readFile = new BufferedReader(new FileReader(file));
			BufferedReader readText = new BufferedReader(new StringReader(text));
			
			String fileLine;
			String textLine;
			ArrayList<String> fileContent = new ArrayList<String>();
			
			while ((fileLine = readFile.readLine()) != null){
				
				fileContent.add(fileLine);
				
				}
				
			while ((textLine = readText.readLine()) != null){
				
				if (!fileContent.contains(textLine)){
					
					out.write(textLine + "\n");
					
				}

			}		
			out.close();
		}
	}
		
	
	/**
	 * method for starting the creation and merging process.
	 * reads all the given jar files and their entrys.
	 * @param dir the directory of the jar files
	 * @throws IOException
	 */
	private void merge(File dir) throws IOException{	
		
		File[] files = dir.listFiles();
		Map <String, File> visited = new HashMap<String, File>();
		//are jar files in the given directory?
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				//we only work with .jar files
				if(files[i].getName().endsWith(".jar")){
						
					JarFile jar = new JarFile(files[i]);
					
					Enumeration<JarEntry> jarenu = jar.entries();
					//lets look at the entry of a specific jar file
					 while (jarenu.hasMoreElements()){
						 
						 JarEntry entry = jarenu.nextElement();
						 //do the jar file contains a servicefile?
						if(entry.getName().contains("META-INF" + java.io.File.separator + "services") && !entry.isDirectory()){
							
							if(visited.containsKey(entry.getName())){
								
								File mergedFile =	visited.get(entry.getName());
								writeMergedFile(mergedFile, readFile(jar.getInputStream(entry)));		
							
							}
							
							else {
								File mergedFile = new File(dest + java.io.File.separator + entry.getName());
								writeMergedFile(mergedFile, readFile(jar.getInputStream(entry)));
								visited.put(entry.getName(), mergedFile);
							}
		
						}
								
					 
					 
					 
				
					 }
			
				}
				
			
				
			}
		}
	}
	
	
	
	
	/**
	 * main method to start the Servicemerger
	 * @param args 0 - the path of the original jar files, 1 - the destination of the new files
	 */
	public static void main(String[] args) {
		
		Servicemerger sm = new Servicemerger(args[0], args[1]);
		}

}
