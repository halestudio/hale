/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
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

import eu.esdihumboldt.hale.rcp.wizards.io.MappingExportWizard;

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
