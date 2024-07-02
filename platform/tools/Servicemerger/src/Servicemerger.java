/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	ArrayList<String> bndSpecifications;
	boolean bndFlag;
	
	/**
	 * @param source - the sourcepath of the geotools jar-files.
	 * @param dest - the destinationpath where the merged files should appear
	 * @param bnd - bnd-File with specified names of jar-files to merge
	 */
	public Servicemerger(String bnd){
		
		this.bndSpecifications = new ArrayList<String>();
		this.bndFlag = true;
		
		try {
			readBND(bnd);			
			merge(new File(this.source));
			createJar(dest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param source - the sourcepath of the geotools jar-files.
	 * @param dest - the destinationpath where the merged files should appear
	 * @param bnd - bnd-File with specified names of jar-files to merge
	 */
	public Servicemerger(String source, String dest){
		this.source = source;
		this.dest = dest;
		this.bndFlag = false;
		
		try {
			merge(new File(this.source));
			createJar(dest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * @param bnd 
	 * @throws IOException 
	 * 
	 */
	private void readBND(String bnd) throws IOException {
	
		File bndFile = new File(bnd);
		BufferedReader bndReader = new BufferedReader(
				new FileReader(bndFile));
		String readLine;
		while((readLine = bndReader.readLine()).contains(",\\")){
			
			if(readLine.contains("classpath")) continue;
		    
			bndSpecifications.add(readLine.substring(readLine.lastIndexOf("/") + 1, readLine.indexOf(",\\")));			
		}
		
		//TODO
		this.source = bndFile.getParent().concat( java.io.File.separator + readLine.substring(0, readLine.lastIndexOf("/")));
		this.dest = this.source;
		//!
		
		bndSpecifications.add(readLine.substring(readLine.lastIndexOf("/") + 1));
		bndReader.close();
		
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
		       JarOutputStream out = new JarOutputStream(stream) ;

		       for (int i = 0 ; i < jaredFiles.length; i++) {
		         if (jaredFiles[i] == null || !jaredFiles[i].exists()
		             || jaredFiles[i].isDirectory()){
		        	 continue ; 
		         }
		           
		        // System.out.println("Adding " + jaredFiles[i].getName()) ;
		         
		         //add files as JarEntrys to the JarFile
		         //must use "/" for pathname so it works with bnd
		         JarEntry entry = new JarEntry("META-INF" + "/" +
							"services" + "/" + jaredFiles[i].getName()) ;
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
			ArrayList<String> toBeWritten = new ArrayList<String>();
			
			while ((fileLine = readFile.readLine()) != null){
				
				fileContent.add(fileLine);
				
				}
				
			while ((textLine = readText.readLine()) != null){
				
				if (!fileContent.contains(textLine)){					
					toBeWritten.add(textLine);				
				}
			}
			
			for(String s : toBeWritten){
				
			
			out.write(s);		
			out.newLine();
			}		
			
			out.flush();
			out.close();
		}
	}
		
	
	/**
	 * method for starting the creation and merging process.
	 * reads all the given jar files and their entrys.
	 * @param dir the directory of the jar files
	 * @throws IOException
	 */
	private void merge(File dir) throws IOException {
	    File[] files = dir.listFiles();
	    Map<String, File> visited = new HashMap<>();

	    // Are jar files in the given directory?
	    if (files != null) {
	        for (File file : files) {
	            if (file.getName().endsWith(".jar") && shouldProcessJar(file)) {
	                try (JarFile jar = new JarFile(file)) {
	                    processJarFile(jar, visited);
	                }
	            }
	        }
	    }
	}

	private boolean shouldProcessJar(File file) {
	    return !this.bndFlag || bndSpecifications.contains(file.getName());
	}

	private void processJarFile(JarFile jar, Map<String, File> visited) throws IOException {
	    Enumeration<JarEntry> jarEntries = jar.entries();
	    while (jarEntries.hasMoreElements()) {
	        JarEntry entry = jarEntries.nextElement();
	        if (isServiceFile(entry)) {
	            File mergedFile = visited.get(entry.getName());
	            if (mergedFile == null) {
	                mergedFile = new File(dest, entry.getName());
	                if (isInsideDestinationDirectory(mergedFile)) {
	                    visited.put(entry.getName(), mergedFile);
	                } else {
	                    throw new IOException("Entry is outside of the target dir: " + entry.getName());
	                }
	            }
	            writeMergedFile(mergedFile, readJarFile(jar.getInputStream(entry)));
	        }
	    }
	}

	private boolean isServiceFile(JarEntry entry) {
	    return entry.getName().contains("META-INF") && entry.getName().contains("services") && !entry.isDirectory();
	}

	private boolean isInsideDestinationDirectory(File file) throws IOException {
	    Path destDirPath = Paths.get(dest).toAbsolutePath();
	    Path filePath = file.toPath().toAbsolutePath();
	    return filePath.startsWith(destDirPath);
	}

	private byte[] readJarFile(InputStream inputStream) throws IOException {
	    return inputStream.readAllBytes();
	}

	private void writeMergedFile(File file, byte[] content) throws IOException {
	    // Append content to the file, creating directories if necessary
	    if (!file.getParentFile().exists()) {
	        file.getParentFile().mkdirs();
	    }
	    Files.write(file.toPath(), content, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
	}
	
	/**
	 * main method to start the Servicemerger
	 * @param args 0 - the path of the original jar files, 1 - the destination of the new files
	 */
	public static void main(String[] args) {
		
		if(args[0].equals("-bnd")){
			Servicemerger sm = new Servicemerger(args[1]);
		}
		else {
			Servicemerger sm = new Servicemerger(args[0], args[1]);
		}
		}

}
