package eu.esdihumboldt.hale.rcp.actions;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.swt.widgets.FileDialog;
import org.geotools.data.shapefile.indexed.IndexedShapefileDataStore;


/**
 * Action to load user data from a shapefile.
 * @author cjauss
 *
 */
public class LoadShapefileAction extends Action {
	
	private final IWorkbenchWindow window;
	
	public LoadShapefileAction(String _name, IWorkbenchWindow _window){
		super(_name);
		this.window = _window;
		
		// The id is used to refer to the action in a menu
		setId(IClientCommandIDs.CMD_OPENFILE);
		//Associate action with a command to allow key bindings
		setActionDefinitionId(IClientCommandIDs.CMD_OPENFILE);
	}
	
	@Override
	public void run(){
		FileDialog geoFileDialog = new FileDialog(window.getShell());
		String[] extensions = new String[2]; 
		extensions[0]= "*.shp";
		extensions[1]= "*.*";
		geoFileDialog.setFilterExtensions(extensions);
		geoFileDialog.open();
		String filename = geoFileDialog.getFilterPath()+"/"+geoFileDialog.getFileName();
		
		
		IndexedShapefileDataStore dstore = null;
		// if file is a shape file
		if(filename.endsWith("shp")){
			dstore = readShapeFile(filename);
			if(dstore != null){
				//TODO show User Data Model, Display Map for dstore
			}
		}
	}
	
	
	/**
	 * Creates and returns a DataSore for a shape file. 
	 * !!!CURRENTLY RETURNS AN EMPTY DATASTORE.
	 * 
	 * @param path of the shape file
	 * @return a DataStore
	 */
	private IndexedShapefileDataStore readShapeFile(String _filename){
		IndexedShapefileDataStore dStore = null;
		try {
		File shapeFile = new File(_filename);
		Map<String, URL> connectorMap = new HashMap<String, URL>();
		connectorMap.put( "url", shapeFile.toURL());
		dStore = new IndexedShapefileDataStore(shapeFile.toURL());
		}catch (IOException e) {
            e.printStackTrace();
        }
		return dStore;
	}
}