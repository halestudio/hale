package eu.esdihumboldt.hale.ui.status.validation;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;

public class InstanceValidationStatusContributionItem extends ContributionItem {
	InstanceValidationStatusAction action;
	ActionContributionItem actionContribution;

	public InstanceValidationStatusContributionItem() {
		createActionContributionItem();
	}

	public InstanceValidationStatusContributionItem(String id) {
		super(id);
		createActionContributionItem();
	}

	@Override
	public void dispose() {
		actionContribution.dispose();
		action.dispose();
		super.dispose();
	}

	/**
	 * Create the action contribution item, with the action for the
	 * instance validation status.
	 */
	private void createActionContributionItem() {
		action = new InstanceValidationStatusAction();
		actionContribution = new ActionContributionItem(action);
	}

	@Override
	public void fill(Composite parent) {
		actionContribution.fill(parent);
	}

	@Override
	public void fill(CoolBar parent, int index) {
		actionContribution.fill(parent, index);
	}

	@Override
	public void fill(Menu parent, int index) {
		actionContribution.fill(parent, index);
	}

	@Override
	public void fill(ToolBar parent, int index) {
		actionContribution.fill(parent, index);
	}
}
