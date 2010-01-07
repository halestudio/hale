package eu.esdihumboldt.hale.rcp.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Action to load transformation rules into the client.
 * @author cjauss
 *
 */
public class LoadTransformationRuleLocalAction extends Action{
	
	private final IWorkbenchWindow window;
	
	public LoadTransformationRuleLocalAction(String _name, IWorkbenchWindow _window){
		super(_name);
		this.window = _window;
		
		// The id is used to refer to the action in a menu
		setId(IClientCommandIDs.CMD_LOADTRANSFORMATIONRULELOCAL);
		//Associate action with a command to allow key bindings
		setActionDefinitionId(IClientCommandIDs.CMD_LOADTRANSFORMATIONRULELOCAL);
	}
	
	
	@Override
	public void run(){
		
	}
}
