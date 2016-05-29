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

package eu.esdihumboldt.hale.ui.service.project.internal;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.project.model.Resource;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Operation that removes resources from the project.
 * 
 * @author Kai Schwierczek
 */
public abstract class AbstractRemoveResourcesOperation extends AbstractOperation {

	private final String actionId;
	private List<? extends Resource> removedResources;

	/**
	 * Create an operation removing all the resources of the specified actionId
	 * 
	 * @param label the label to be used for the operation
	 * @param actionId the actionId
	 */
	public AbstractRemoveResourcesOperation(String label, String actionId) {
		super(label);
		this.actionId = actionId;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		removedResources = ps.removeResources(actionId);
		return Status.OK_STATUS;
	}

	/**
	 * This implementation simply calls execute (thus final).
	 * 
	 * @see org.eclipse.core.commands.operations.AbstractOperation#redo(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public final IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		for (Resource res : removedResources) {
			ps.executeAndRemember(res.copyConfiguration(false));
		}
		return Status.OK_STATUS;
	}
}
