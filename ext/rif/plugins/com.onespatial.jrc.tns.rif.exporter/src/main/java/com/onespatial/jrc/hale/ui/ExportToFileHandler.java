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
 *     1Spatial PLC <http://www.1spatial.com>
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */
package com.onespatial.jrc.hale.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.esdihumboldt.hale.ui.io.legacy.MappingExportWizard;

/**
 * The export to file handler is used by the Humboldt Alignment Editor to
 * initiate an export of a schema mapping definition document to the selected
 * output format.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 * @version $Id$
 */
public class ExportToFileHandler extends AbstractHandler implements IHandler
{
    /**
     * {@inheritDoc}
     */
    @Override
	public Object execute(ExecutionEvent event) throws ExecutionException
    {
        // Instantiates the wizard container with the wizard and opens it
        IExportWizard iw = new MappingExportWizard();
        // Instantiates the wizard container with the wizard and opens it
        Shell shell = HandlerUtil.getActiveShell(event);
        WizardDialog dialog = new WizardDialog(shell, iw);
        dialog.open();
        return null;
    }
}
