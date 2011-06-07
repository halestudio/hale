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
package eu.esdihumboldt.hale.ui.service.instance.internal;

import eu.esdihumboldt.hale.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.ui.service.instance.DataSet;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;

/**
 * Default implementation of {@link InstanceService}.
 * 
 * @author Thorsten Reitz, Fraunhofer IGD
 * @author Simon Templer
 */
public class InstanceServiceImpl extends AbstractInstanceService {
	
//	private CRSDefinition crs;

	/**
	 * @see eu.esdihumboldt.hale.ui.service.instance.InstanceService#getInstances(eu.esdihumboldt.hale.ui.service.instance.DataSet)
	 */
	@Override
	public InstanceCollection getInstances(DataSet dataset) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.instance.InstanceService#addSourceInstances(eu.esdihumboldt.hale.instance.model.InstanceCollection)
	 */
	@Override
	public void addSourceInstances(InstanceCollection sourceInstances) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.instance.InstanceService#clearInstances()
	 */
	@Override
	public void clearInstances() {
		// TODO Auto-generated method stub
		
	}
	
//	private void updateCRS(
//			FeatureCollection<?, Feature> fc) {
//		if (getCRS() == null) {
//			CRSDefinition crsDef = determineCRS(fc);
//			setCRS(crsDef);
//		}
//	}

//	/**
//	 * @see InstanceService#setCRS(CRSDefinition)
//	 */
//	@Override
//	public void setCRS(CRSDefinition crs) {
//		this.crs = crs;
//		
//		notifyCRSChanged(crs);
//	}
//
//	/**
//	 * @see InstanceService#getCRS()
//	 */
//	@Override
//	public CRSDefinition getCRS() {
//		return crs;
//	}
//	
//	/**
//	 * Determine the coordinate reference system for a feature collection
//	 * 
//	 * @param fc the feature collection
//	 * 
//	 * @return the coordinate reference system or null
//	 */
//	public static CRSDefinition determineCRS(
//			FeatureCollection<? extends FeatureType, ? extends Feature> fc) {
//		CoordinateReferenceSystem crs = null;
//		
//		// try the instance data first.
//		if (fc != null && !fc.isEmpty()) {
//			Feature f = fc.features().next();
//			if (f.getDefaultGeometryProperty() != null) {
//				GeometryAttribute gp = f.getDefaultGeometryProperty();
//				crs = gp.getDescriptor().getCoordinateReferenceSystem();
//				
//				if (crs == null) {
//					// next try - user data of value
//					Object value = gp.getValue();
//					if (value instanceof Geometry) {
//						Object userData = ((Geometry) value).getUserData();
//						if (userData instanceof CoordinateReferenceSystem) {
//							crs = (CoordinateReferenceSystem) userData;
//						}
//					}
//				}
//			}
//		}
//		
//		// then check the schema.
//		if (crs == null && fc != null) {
//			crs = fc.getSchema().getCoordinateReferenceSystem();
//		}
//		
//		final AtomicReference<CRSDefinition> crsDef = new AtomicReference<CRSDefinition>();
//		
//		// if none is available, use a default.
//		if (crs == null) {
//			final Display display = PlatformUI.getWorkbench().getDisplay();
//			
//			display.syncExec(new Runnable() {
//				
//				@Override
//				public void run() {
//					SelectCRSDialog dialog = new SelectCRSDialog(display.getActiveShell(), null);
//					while (crsDef.get() == null) {
//						if (dialog.open() != SelectCRSDialog.OK) {
//							break;
//						}
//						else {
//							crsDef.set(dialog.getValue());
//						}
//					}
//				}
//			});
//		}
//		else {
//			try {
//				crsDef.set(new CodeDefinition(crs.getIdentifiers().iterator().next().toString(), crs));
//			} catch (Exception e) {
//				crsDef.set(new WKTDefinition(crs.toWKT(), crs));
//			}
//		}
//		
//		return crsDef.get();
//	}

}
