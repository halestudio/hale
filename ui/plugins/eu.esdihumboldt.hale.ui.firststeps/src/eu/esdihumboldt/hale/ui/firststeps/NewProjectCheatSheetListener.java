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

package eu.esdihumboldt.hale.ui.firststeps;

import org.eclipse.core.expressions.Expression;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.CheatSheetListener;
import org.eclipse.ui.cheatsheets.ICheatSheetEvent;
import org.eclipse.ui.internal.cheatsheets.views.CheatSheetEvent;
import org.eclipse.ui.services.IEvaluationReference;
import org.eclipse.ui.services.IEvaluationService;

import eu.esdihumboldt.hale.ui.io.action.ActionUI;
import eu.esdihumboldt.hale.ui.io.action.ActionUIExtension;

/**
 * A cheat sheet listener for the create new project cheat sheet. It sets needed
 * cheat sheet variables.
 * 
 * @author Kai Schwierczek
 */
public class NewProjectCheatSheetListener extends CheatSheetListener {
	IEvaluationReference evaluationReference;

	/**
	 * @see org.eclipse.ui.cheatsheets.CheatSheetListener#cheatSheetEvent(org.eclipse.ui.cheatsheets.ICheatSheetEvent)
	 */
	@Override
	public void cheatSheetEvent(final ICheatSheetEvent cse) {
		switch (cse.getEventType()) {
		case CheatSheetEvent.CHEATSHEET_OPENED:
			ActionUI sourceDataActionUI = 
					ActionUIExtension.getInstance().findActionUI("eu.esdihumboldt.hale.io.instance.read.source");
			Expression enabledWhen = sourceDataActionUI.getEnabledWhen();
			if (enabledWhen != null) {
				IEvaluationService es = (IEvaluationService) PlatformUI.getWorkbench().getService(IEvaluationService.class);
				evaluationReference = es.addEvaluationListener(enabledWhen,
						new IPropertyChangeListener() {
							@Override
							public void propertyChange(PropertyChangeEvent pce) {
								cse.getCheatSheetManager().setData("source.data.enabled", String.valueOf(pce.getNewValue()));
							}
						}, "enabled");
			}
			break;
		case CheatSheetEvent.CHEATSHEET_CLOSED:
			if (evaluationReference != null) {
				IEvaluationService es = (IEvaluationService) PlatformUI
						.getWorkbench().getService(IEvaluationService.class);
				es.removeEvaluationListener(evaluationReference);
			}
		}
	}
}
