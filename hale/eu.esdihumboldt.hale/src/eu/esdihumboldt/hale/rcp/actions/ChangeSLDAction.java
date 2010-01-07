package eu.esdihumboldt.hale.rcp.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * 
 * @author cjauss
 *
 */
public class ChangeSLDAction extends Action{
	
	
	public ChangeSLDAction(IWorkbenchWindow _window, String _name, int _map){
		super(_name);
		
		setId(IClientCommandIDs.CMD_CHANGESLD);
		//if change of SLD for User Data Map
		if(_map==1){
			
		}
		//if change of SLD for Target Schema Map
		else if(_map==2){
			
		}
		else{
			//Error
		}
	}
	
	
	@Override
	public void run(){
		
	}
}
