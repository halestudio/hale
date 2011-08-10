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

	/**
	 * Tries to build a new path on two strings
	 * @param oldsource path where the file was saved to (has to be a string representation of an {@link URI})
	 * @param newlocation directory path where the program is running now
	 * @return the constructed string
	 */
	protected String changePath(String oldsource, String newlocation) {
		String src = oldsource.substring(oldsource.indexOf("/") + 1, oldsource.lastIndexOf("/"));
		String prefex = oldsource.substring(0, oldsource.indexOf("/")+ 1);
		String data = oldsource.substring(oldsource.lastIndexOf("/") +1);
		String[] locarray = newlocation.split("/");
		String[] srcarray = src.split("/");
		StringBuffer newsrc = new StringBuffer();
		newsrc.append(prefex);
		boolean changed = false;
		
		for(int i = srcarray.length - 1; i >= 0; i--){
			int first = getPosition(locarray, srcarray[i]);
			int second = -1;
			if(i-1 >= 0)
			second = getPosition(locarray, srcarray[i-1]);
			if(first != -1 && (i == 0 || second == -1)){
				changed = true;
				for(int l = 0; l <= first; l++){
					newsrc = newsrc.append(locarray[l]).append("/");
				}
				for(int d = i+1; d < srcarray.length; d++){
					newsrc = newsrc.append(srcarray[d]).append("/");
				}
				return newsrc.append(data).toString();
			}
		}
		if(!changed){
			for(int i = 0; i < locarray.length; i++){
				newsrc = newsrc.append(locarray[i]).append("/");
			}
		}
		
		return newsrc.append(data).toString();
	}
	
	private int getPosition(String[] array, String search){
		for(int i = array.length - 1; i >= 0; i--){
			if(array[i].equals(search))
				return i;
		}
		return - 1;
	}
}
