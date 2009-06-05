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
package eu.esdihumboldt.rcp.wizards.functions.filter;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.rcp.views.model.AttributeView;
import eu.esdihumboldt.rcp.wizards.functions.literal.RenamingFunctionWizard;
import eu.esdihumboldt.rcp.wizards.functions.literal.RenamingFunctionWizardMainPage;
import eu.esdihumboldt.transformers.cst.RenameTransformer;

/**
 * This {@link Wizard} is used to invoke a Renaming Transformer for the Source Feature Type
 * 
 * @author Anna Pitaev, Logica
 * @version $Id$
 */
public class FilterWizard extends Wizard 
implements INewWizard {
	
	private static Logger _log = Logger.getLogger(FilterWizard.class);
		
		FilterWizardMainPage mainPage;
		
		/**
		 * constructor
		 */
		public FilterWizard(){
			super();
			this.mainPage = new FilterWizardMainPage(
					"Configure Filter Expression", "Configure Filter Expression"); 
			super.setWindowTitle("Configure Function"); 
			super.setNeedsProgressMonitor(true);
			
		}

		
		/**
		 * @see org.eclipse.jface.wizard.Wizard#canFinish()
		 */
		@Override
		public boolean canFinish() {
			_log.debug("Wizard.canFinish: " + this.mainPage.isPageComplete());
			return true;
		}


		/**
		 * @see org.eclipse.jface.wizard.Wizard#performFinish()
		 */
		
		@Override
		public boolean performFinish() {
			//TODO implement it
			return true;
		}

		public void init(IWorkbench workbench, IStructuredSelection selection) {
			_log.debug("in init..");
			
				
			}
		
		 /*
		 * @see org.eclipse.jface.wizard.IWizard#addPages()
	     */
	    public void addPages() {
	        super.addPages(); 
	        addPage(mainPage);        
	    }
	

}
