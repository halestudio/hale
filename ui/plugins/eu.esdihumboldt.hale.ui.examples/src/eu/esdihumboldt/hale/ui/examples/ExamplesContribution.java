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

package eu.esdihumboldt.hale.ui.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

import eu.esdihumboldt.hale.doc.user.examples.internal.ExamplesConstants;
import eu.esdihumboldt.hale.doc.user.examples.internal.extension.ExampleProject;
import eu.esdihumboldt.hale.doc.user.examples.internal.extension.ExampleProjectExtension;
import eu.esdihumboldt.hale.ui.util.DynamicActionsContribution;

/**
 * Contribution offering to show examples project in the help.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class ExamplesContribution extends DynamicActionsContribution {

	/**
	 * Shows the example project overview in the help.
	 */
	private static class ExampleOverviewAction extends Action implements ExamplesConstants {

		/**
		 * Default constructor
		 */
		public ExampleOverviewAction() {
			super();
			setText("Overview");
		}

		/**
		 * @see Action#run()
		 */
		@Override
		public void run() {
			final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
			helpSystem.displayHelpResource("/" + PLUGIN_ID + "/" + PATH_OVERVIEW);
		}

	}

	/**
	 * Show an example project in the help.
	 */
	private static class ShowProjectAction extends Action implements ExamplesConstants {

		private final String helpPath;

		/**
		 * Create an action that shows the given example project in the help.
		 * 
		 * @param project the example project
		 */
		public ShowProjectAction(ExampleProject project) {
			helpPath = "/" + PLUGIN_ID + "/" + PATH_PREFIX_PROJECT + project.getId() + ".html";

			setText(project.getInfo().getName());
		}

		/**
		 * @see Action#run()
		 */
		@Override
		public void run() {
			final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
			helpSystem.displayHelpResource(helpPath);
		}

	}

	/**
	 * @see DynamicActionsContribution#getActions()
	 */
	@Override
	protected Iterable<IAction> getActions() {
		final AtomicReference<List<IAction>> actionsRef = new AtomicReference<List<IAction>>();
		final AtomicBoolean finished = new AtomicBoolean(false);
		BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), new Runnable() {

			@Override
			public void run() {
				try {
					List<IAction> actions = new ArrayList<IAction>();

					for (ExampleProject project : ExampleProjectExtension.getInstance()
							.getElements()) {
						actions.add(new ShowProjectAction(project));
					}

					actionsRef.set(actions);
				} finally {
					finished.set(true);
				}
			}
		});

		while (!finished.get()) {
			if (Display.getCurrent() != null) {
				while (Display.getCurrent().readAndDispatch()) {
					// repeat
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// ignore
			}
		}

		List<IAction> actions = actionsRef.get();
		if (actions == null) {
			// populating the list failed
			actions = new ArrayList<IAction>();
		}

		if (!actions.isEmpty()) {
			actions.add(0, null);
		}
		actions.add(0, new ExampleOverviewAction());

		return actions;
	}
}
