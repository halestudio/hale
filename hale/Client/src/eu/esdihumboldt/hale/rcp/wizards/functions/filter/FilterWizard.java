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
package eu.esdihumboldt.hale.rcp.wizards.functions.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Param;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView;

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
		FilterWizardSecondPage secondPage;
		
		/**
		 * constructor
		 */
		public FilterWizard(){
			super();
			this.mainPage = new FilterWizardMainPage(
					"Configure Filter Expression", "Configure Filter Expression"); 
			this.secondPage = new FilterWizardSecondPage(
					"Configure Filter Expression", "Configure Filter Expression"); 
			super.setWindowTitle("Configure Function"); 
			super.setNeedsProgressMonitor(true);
			
		}

		
		/**
		 * @see org.eclipse.jface.wizard.Wizard#canFinish()
		 */
		@Override
		public boolean canFinish() {
			_log.debug("Wizard.canFinish: " + this.secondPage.isPageComplete());
			return true;
		}


		/**
		 * @see org.eclipse.jface.wizard.Wizard#performFinish()
		 */
		
		@Override
		public boolean performFinish() {
			//get service
			SchemaService service = (SchemaService)ModelNavigationView.site.getService(SchemaService.class);
			String typeNameSource = secondPage.getSourceViewer().getTree().getSelection()[0].getText();
			FeatureType ft_source = service.getFeatureTypeByName(typeNameSource);
			
			
			
			//get URI and local name
			List<String> nameparts = new ArrayList<String>(); 
			nameparts.add(ft_source.getName().getNamespaceURI());
			nameparts.add(ft_source.getName().getLocalPart());


			
			//evtl. move to performFinish
			Cell c = new Cell();
			FeatureClass entity1 = new FeatureClass(nameparts);
			Transformation t = new Transformation();
			t.setLabel("Filter");
			List parameters = new ArrayList<IParameter>();
         	parameters.add(new Param("CQLExpression", secondPage.buildCQL()));
            t.setParameters(parameters);
			entity1.setTransformation(t); 
			c.setEntity1(entity1);
			c.setEntity2(entity1);
			AlignmentService alservice = (AlignmentService)ModelNavigationView.site.getService(AlignmentService.class);
			//store transformation in AS
			alservice.addOrUpdateCell(c);
			//higlight Feature Type
			
			Color color = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
			secondPage.getSourceViewer().getTree().getSelection()[0].setBackground(0, color);
			secondPage.getSourceViewer().getControl().redraw();
           _log.debug("Transformation finished");
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
	        addPage(secondPage);
	    }
	

}
