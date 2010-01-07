package eu.esdihumboldt.hale.rcp.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Action to store a Transformation Rule.
 * @author cjauss
 *
 */
public class StoreTransformationRuleLocalAction extends Action{
	
	private final IWorkbenchWindow window;
	
	public StoreTransformationRuleLocalAction(String _name, IWorkbenchWindow _window){
		super(_name);
		this.window = _window;
		
		// The id is used to refer to the action in a menu
		setId(IClientCommandIDs.CMD_STORETRANSFORMATIONRULELOCAL);
		//Associate action with a command to allow key bindings
		setActionDefinitionId(IClientCommandIDs.CMD_STORETRANSFORMATIONRULELOCAL);
		
	}
	
	
	@Override
	public void run(){
		
	}
}
