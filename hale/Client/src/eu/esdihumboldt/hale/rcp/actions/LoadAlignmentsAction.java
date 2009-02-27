package eu.esdihumboldt.hale.rcp.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Action for loading Alignments.
 * @author cjauss
 *
 */
public class LoadAlignmentsAction extends Action{
	private final IWorkbenchWindow window;
	
	public LoadAlignmentsAction(String _name, IWorkbenchWindow _window){
		super(_name);
		this.window = _window;
		// The id is used to refer to the action in a menu
		setId(IClientCommandIDs.CMD_LOADALIGNMENT);
		//Associate action with a command to allow key bindings
		setActionDefinitionId(IClientCommandIDs.CMD_LOADALIGNMENT);
		
	}
	
	
	@Override
	public void run(){
		
	}
}
