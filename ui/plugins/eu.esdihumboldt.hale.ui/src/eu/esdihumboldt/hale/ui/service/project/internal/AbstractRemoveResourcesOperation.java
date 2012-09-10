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

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Operation that removes resources from the project.
 * 
 * @author Kai Schwierczek
 */
public abstract class AbstractRemoveResourcesOperation extends AbstractOperation {

	private final String actionId;
	private List<IOConfiguration> removedResources;

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

	/**
	 * @see org.eclipse.core.commands.operations.AbstractOperation#execute(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(
				ProjectService.class);
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

	/**
	 * @see org.eclipse.core.commands.operations.AbstractOperation#undo(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(
				ProjectService.class);
		for (IOConfiguration conf : removedResources) {
			ps.executeAndRemember(conf);
		}
		return Status.OK_STATUS;
	}
}