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

package eu.esdihumboldt.hale.ui.service.project.internal;

import java.net.URI;

/**
 * Updater class for a path based on a string
 * @author Patrick Lieb
 */
public class FilePathUpdate {
	
//	private final URI newlocation;
	
	private final URI oldLocation;
	
	private final StringBuffer newfolder;
	
	private String[] subfolder;
	
	private String[] parentfolder;
	
	/**
	 * Create a path updater based on a pair of known old and new locations
	 * @param oldLocation the old location of a file
	 * @param newLocation the new location of the same file
	 *   (though the file name may be different)
	 */
	public FilePathUpdate(URI oldLocation, URI newLocation) {
		super();
		
		/* 
		 * TODO
		 * analyze paths (w/o file name) of both URIs to find out which of the 
		 * later parts are equal, to determine which part of the old location 
		 * has to be replaced by which part of the new location for other files 
		 * that have been moved in a similar way to the analyzed file.
		 */
//		this.newlocation = newLocation;
		this.oldLocation = oldLocation;
		newfolder = analysePaths(oldLocation, newLocation);
	}

	/**
	 * Create an alternative path for the given location if the corresponding
	 * file has been moved along to the project file
	 * @param oldsource path where the file was saved to (has to be a string 
	 *   representation of an {@link URI}) FIXME then it should be typed as a URI to be sure!
	 * @return the constructed string
	 */
	protected String changePath(URI oldsource) {
		String old = oldsource.getPath().substring(1);
		String data = old.substring(old.lastIndexOf("/") +1);
		
		setSubFolder(oldLocation, oldsource);
		setParentFolder(oldLocation, oldsource);
		if(subfolder != null){
			for(String fold : subfolder){
				newfolder.append("/").append(fold);
			}
			return newfolder.append("/").append(data).toString();
		}
		if(parentfolder != null){
			String[] folders = newfolder.toString().split("/");
			int pos;
			for(pos = folders.length -1; pos >= 0; pos--){
				if(folders[pos].equals(parentfolder[0])){
					break;
				}
			}
			StringBuffer buffer = new StringBuffer();
			for(int i = 0; i < pos; i++){
				buffer.append(folders[i]).append("/");
			}
			return buffer.append(data).toString();
		}
		return newfolder.append("/").append(data).toString();
		
		
//		String old = oldsource.getPath().substring(1);
//		String data = old.substring(old.lastIndexOf("/") +1);
//		old = old.substring(0, old.lastIndexOf("/"));
//		String newpath = newlocation.substring(0, newlocation.lastIndexOf("/"));
//		String[] newlocarray = newpath.split("/");
//		String[] srcarray = old.split("/");
//		StringBuffer newsrc = new StringBuffer();
//		newsrc.append("file:/");
//		boolean changed = false;
//		
//		int max = Math.max(newlocarray.length, srcarray.length);
//		int pos;
//		// Check which parent folder are equal
//		for(pos = 0; pos < max; pos++){
//			if(newlocarray[pos].equals(srcarray[pos])){
//				newsrc.append(newlocarray[pos]).append("/");
//			} else break;
//		}
//		// Append new folders
//		for(int i = pos; i < newlocarray.length; i++){
//			newsrc.append(newlocarray[i]).append("/");
//		}
//		for(int i = srcarray.length - 1; i >= 0; i--){
//			int first = getPosition(newlocarray, srcarray[i]);
//			int second = -1;
//			if(i-1 >= 0)
//			second = getPosition(newlocarray, srcarray[i-1]);
//			if(first != -1 && (i == 0 || second == -1)){
//				changed = true;
//				for(int l = 0; l <= first; l++){
//					newsrc = newsrc.append(newlocarray[l]).append("/");
//				}
//				for(int d = i+1; d < srcarray.length; d++){
//					newsrc = newsrc.append(srcarray[d]).append("/");
//				}
//				return newsrc.append(data).toString();
//			}
//		}
//		if(!changed){
//			for(int i = 0; i < newlocarray.length; i++){
//				newsrc = newsrc.append(newlocarray[i]).append("/");
//			}
//		}
		
//		return newsrc.append(data).toString();
	}
	// Analyses the old and the new project path and tries to return the new one
	private static StringBuffer analysePaths(URI oldlocation, URI newlocation){
		String old = oldlocation.getPath().substring(1);
		String newloc = newlocation.getPath().substring(1);
//		String data = old.substring(old.lastIndexOf("/") +1);
		old = old.substring(0, old.lastIndexOf("/"));
		String newpath = newloc.substring(0, newloc.lastIndexOf("/"));
		String[] newlocarray = newpath.split("/");
		String[] srcarray = old.split("/");
		StringBuffer newsrc = new StringBuffer();
		newsrc.append("file:");
		
		int min = Math.min(newlocarray.length, srcarray.length);
		int pos;
		// Check which parent folder are equal
		for(pos = 0; pos < min; pos++){
			if(newlocarray[pos].equals(srcarray[pos])){
				newsrc.append("/").append(newlocarray[pos]);
			} else break;
		}
		// Append new folders
		for(int i = pos; i < newlocarray.length; i++){
			newsrc.append("/").append(newlocarray[i]);
		}
		return newsrc;
	}
	
//	private int getPosition(String[] array, String search){
//		for(int i = array.length - 1; i >= 0; i--){
//			if(array[i].equals(search))
//				return i;
//		}
//		return - 1;
//	}
	
	// returns the subfolders in which the file is (in relation to the project file) - null if no sub folder
	private void setSubFolder(URI project, URI file){
		String pct = project.getPath().substring(1);
		pct = pct.substring(0, pct.lastIndexOf("/"));
		String[] pctarray = pct.split("/");
		String fle = file.getPath().substring(1);
		fle = fle.substring(0, fle.lastIndexOf("/"));
		String[] flearray = fle.split("/");
		StringBuffer newsrc = new StringBuffer();
		if(pctarray.length < flearray.length){
			int i;
			for(i = 0; i<pctarray.length; i++){
				if(!pctarray[i].equals(flearray[i])){
					break;
				}
			}
			for(int p = i; p < flearray.length; p++){
				newsrc.append(flearray[p]).append("/");
			}
			subfolder = newsrc.toString().split("/");
		} else subfolder = null; 
	}
	
	// returns the parent folder of the folder in which the file is placed - null if there is no parent folder
	private void setParentFolder(URI project, URI file){
		String pct = project.getPath().substring(1);
		pct = pct.substring(0, pct.lastIndexOf("/"));
		String[] pctarray = pct.split("/");
		String fle = file.getPath().substring(1);
		fle = fle.substring(0, fle.lastIndexOf("/"));
		String[] flearray = fle.split("/");
		StringBuffer newsrc = new StringBuffer();
		if(flearray.length < pctarray.length){
			int i;
			for(i = 0; i<flearray.length; i++){
				if(!pctarray[i].equals(flearray[i])){
					break;
				}
			}
			for(int p = i; p < pctarray.length; p++){
				newsrc.append(pctarray[p]).append("/");
			}
			parentfolder = newsrc.toString().split("/");
		} else parentfolder = null; 
	}
}
