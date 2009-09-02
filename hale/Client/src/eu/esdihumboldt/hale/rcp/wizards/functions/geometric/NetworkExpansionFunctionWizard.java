/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.wizards.functions.geometric;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.xml.xsi.XSISimpleTypes.ENTITIES;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.buffer.BufferBuilder;
import com.vividsolutions.jts.operation.buffer.BufferParameters;

import eu.esdihumboldt.cst.transformer.TransformerFactory;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.models.impl.InstanceServiceImpl;
import eu.esdihumboldt.hale.rcp.utils.ModelNavigationViewHelper;
import eu.esdihumboldt.hale.rcp.utils.ModelNavigationViewHelper.SelectionType;
import eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView;
import eu.esdihumboldt.hale.rcp.views.model.ModelNavigationViewLabelProvider;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject.TreeObjectType;

/**
 * A simplified Wizard for the configuration of the Network Expansion function,
 * which takes any MultiLineString and buffers it to a MultiPolygon.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class NetworkExpansionFunctionWizard 
		extends Wizard 
		implements INewWizard, ISelectionListener {
	
	private static Logger _log = Logger.getLogger(NetworkExpansionFunctionWizard.class);
	
	NetworkExpansionFunctionWizardPage mainPage;
	
	public NetworkExpansionFunctionWizard() {
		super();
		this.mainPage = new NetworkExpansionFunctionWizardPage(
				"Configure Network Expansion"); 
		super.setWindowTitle("Configure Function"); 
		super.setNeedsProgressMonitor(true);
	}

	
	private Entity getEntity(SelectionType selectionType) {
		ModelNavigationViewHelper helper = new ModelNavigationViewHelper();
		
		List<String> label1 = new ArrayList<String>();
		Entity entity = new Entity(label1);
		
		String attributeName = helper.getSelectedAttributeName(selectionType);
		FeatureType featureType = helper.getSelectedFeatureType(selectionType);
		
		// The first label is always the type of the selected item.
		entity.getLabel().add(helper.getSelectedType(SelectionType.SOURCE).toString());
		
		// The second label is the name of the selected item. It could be an feature type or an
		// attribute. If it is an attribute, we need also to set as third label the parent
		// feature type name.
		if (featureType != null) {
			entity.getLabel().add(featureType.getName().getLocalPart());
			entity.getLabel().add(featureType.getName().getNamespaceURI());
		}
		else {
			entity.getLabel().add(attributeName);
			FeatureType parent = helper.getSelectedAttributeParent(selectionType);
			entity.getLabel().add(parent.getName().getLocalPart());
			entity.getLabel().add(parent.getName().getNamespaceURI());
		}
		return entity;
	}

	
	@Override
	public boolean performFinish() {
		_log.debug("Wizard.canFinish: " + this.mainPage.isPageComplete());
		
		// get alignment service
		AlignmentService alignmentService = 
			(AlignmentService)ModelNavigationView.site.getService(
					AlignmentService.class);
		
		// Create the cell
		Cell cell = new Cell();
		Entity entity1 = getEntity(SelectionType.SOURCE);
		Entity entity2 = getEntity(SelectionType.TARGET);
		
		System.err.println("debug1");
		Transformation transformation = new Transformation();
		transformation.setLabel(TransformerFactory.BUFFER_GEOMETRY);
		transformation.getParameters().add(new Parameter("Expansion", "50"));
		System.err.println("debug1");
		entity1.setTransformation(transformation);
		System.err.println("debug1");

		cell.setEntity1(entity1);
		cell.setEntity2(entity2);
		alignmentService.addOrUpdateCell(cell);
		
//		InstanceService is = InstanceServiceImpl.getInstance();
//
////		// Get instances
//		FeatureCollection<FeatureType, Feature> features = is.getFeatures(DatasetType.reference);
//		
//		// transform geometry in instances
//		FeatureCollection<FeatureType, Feature> transformedFeatures = this.bufferGeometry(features);
//		
//		// put instances in InstanceService
//		is.addInstances(DatasetType.transformed, transformedFeatures);
//		
//		_log.info("transformedFeatures size: " + transformedFeatures.size());
		
		return this.mainPage.isPageComplete();
	}
	
	/**
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    public void addPages() {
        super.addPages(); 
        addPage(this.mainPage);
    }

	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			final Object selectionObject = ((IStructuredSelection) selection)
					.getFirstElement();
			if (selectionObject != null) {

				TreeItem treeItem = (TreeItem) selectionObject;
				String selectedFeatureType = treeItem.getText();
				System.out.println(selectedFeatureType);
			}
		}

	}
	
	private FeatureCollection<FeatureType, Feature> bufferGeometry(FeatureCollection<FeatureType, Feature> features) {
			FeatureCollection result = FeatureCollections.newCollection();
			FeatureIterator fi = features.features();
	
			
			int counter = 0;
			boolean odd = true;
			while (fi.hasNext()) {
				SimpleFeature f = (SimpleFeature) fi.next();
				Geometry old_geometry = (Geometry)f.getDefaultGeometry();
				if (old_geometry != null) {
					Geometry new_geometry = null;
					try {
						BufferBuilder bb = new BufferBuilder(new BufferParameters());
						new_geometry = bb.buffer((Geometry)f.getDefaultGeometry(), 50);
						SimpleFeature newFeature = SimpleFeatureBuilder.deep(f);
						newFeature.setDefaultGeometry(new_geometry);
						result.add(newFeature);
					} catch (Exception ex) {
						counter++;
					}
				}
			}
			_log.debug("Exceptions: " + counter);
			return result;
	}

}
