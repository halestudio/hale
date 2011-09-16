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

package eu.esdihumboldt.hale.instanceprovider.gml;

import java.io.IOException;
import java.net.URI;

import org.geotools.feature.FeatureCollection;
import org.geotools.util.Version;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.cs3d.util.logging.ATransaction;

import eu.esdihumboldt.hale.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.gmlparser.GmlHelper;
import eu.esdihumboldt.hale.gmlparser.GmlHelper.ConfigurationType;
import eu.esdihumboldt.hale.instanceprovider.AbstractInstanceProvider;
import eu.esdihumboldt.hale.instanceprovider.InstanceConfiguration;
import eu.esdihumboldt.hale.instanceprovider.InstanceProvider;

import eu.esdihumboldt.hale.cache.Request;

/**
 * {@link InstanceProvider} that loads GML files
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class GmlInstanceProvider extends AbstractInstanceProvider {
	
	private static final ALogger log = ALoggerFactory.getLogger(GmlInstanceProvider.class);
	
	/**
	 * GML 3 version
	 */
	public static final Version GML3 = new Version("3.0.0"); //$NON-NLS-1$
	
	/**
	 * GML 3.2 version
	 */
	public static final Version GML32 = new Version("3.2.0"); //$NON-NLS-1$

	/**
	 * Default constructor 
	 */
	public GmlInstanceProvider() {
		super();
		
		addSupportedSchemaFormat("xsd"); //$NON-NLS-1$
		addSupportedSchemaFormat("xml"); //$NON-NLS-1$
		addSupportedSchemaFormat("gml"); //$NON-NLS-1$
		
		addSupportedInstanceFormat("gml"); //$NON-NLS-1$
		addSupportedInstanceFormat("xml"); //$NON-NLS-1$
	}

	/**
	 * @see InstanceProvider#loadInstances(URI, InstanceConfiguration, ProgressIndicator)
	 */
	@Override
	public FeatureCollection<FeatureType, Feature> loadInstances(URI location,
			InstanceConfiguration configuration, ProgressIndicator progress)
			throws IOException {
		ATransaction trans = log.begin("Loading GML features from " + location.toString()); //$NON-NLS-1$
		try {
			Version version = configuration.getInstanceVersion();
			ConfigurationType type;
			if (GML32.compareTo(version) <= 0) {
				type = ConfigurationType.GML3_2;
			}
			else if (GML3.compareTo(version) <= 0) {
				type = ConfigurationType.GML3;
			}
			else {
				type = ConfigurationType.GML2;
			}
			
			return GmlHelper.loadGml(Request.getInstance().get(location), type, 
					configuration.getNamespace(), 
					configuration.getSchemaLocation().toString(), 
					configuration.getSchema());
			
//			return GmlHelper.loadGml(location.toURL().openStream(), type, 
//					configuration.getNamespace(), 
//					configuration.getSchemaLocation().toString(), 
//					configuration.getSchema());
		} catch (Exception e) {
			return null;
		} finally {
			trans.end();
		}
	}

}
