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

package eu.esdihumboldt.hale.instanceprovider.shape;

import java.io.IOException;
import java.net.URI;

import org.geotools.data.DataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.cs3d.util.logging.ATransaction;

import eu.esdihumboldt.hale.gmlparser.CstFeatureCollection;
import eu.esdihumboldt.hale.instanceprovider.AbstractInstanceProvider;
import eu.esdihumboldt.hale.instanceprovider.InstanceConfiguration;
import eu.esdihumboldt.hale.instanceprovider.InstanceProvider;
import eu.esdihumboldt.hale.schemaprovider.ProgressIndicator;

/**
 * {@link InstanceProvider} for shape files
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class ShapeInstanceProvider extends AbstractInstanceProvider {
	
	private static final ALogger log = ALoggerFactory.getLogger(ShapeInstanceProvider.class);

	/**
	 * Default constructor
	 */
	public ShapeInstanceProvider() {
		addSupportedSchemaFormat("shp"); //$NON-NLS-1$
		
		addSupportedInstanceFormat("shp"); //$NON-NLS-1$
	}

	/**
	 * @see InstanceProvider#loadInstances(URI, InstanceConfiguration, ProgressIndicator)
	 */
	@Override
	public FeatureCollection<FeatureType, Feature> loadInstances(URI location,
			InstanceConfiguration configuration, ProgressIndicator progress) throws IOException {
		ATransaction trans = log.begin("Loading GML features from " + location.toString()); //$NON-NLS-1$
		try {
			DataStore store = FileDataStoreFinder.getDataStore(location.toURL());
			CstFeatureCollection fc = new CstFeatureCollection();
			
			for (Name name : store.getNames()) {
				fc.addAll(store.getFeatureSource(name).getFeatures());
			}
			
			return fc;
		} finally {
			trans.end();
		}
	}

}
