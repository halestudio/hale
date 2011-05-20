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

package eu.esdihumboldt.cst.transformer.configuration.osgi;

import java.util.List;
import java.util.Set;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.cs3d.util.logging.ATransaction;

import eu.esdihumboldt.cst.transformer.CstService;
import eu.esdihumboldt.cst.transformer.capabilities.CstServiceCapabilities;
import eu.esdihumboldt.specification.cst.align.IAlignment;
import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * Provides log transaction support for transformation execution
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class CstServiceWrapper implements CstService {
	
	private static final ALogger log = ALoggerFactory.getLogger(CstServiceWrapper.class);
	
	/**
	 * The decoratee
	 */
	private CstService cstService;

	/**
	 * Constructor
	 * 
	 * @param cstService the cst service to wrap
	 */
	public CstServiceWrapper(CstService cstService) {
		super();
		this.cstService = cstService;
	}

	/**
	 * @see CstService#getCapabilities()
	 */
	public CstServiceCapabilities getCapabilities() {
		return cstService.getCapabilities();
	}

	/**
	 * @see CstService#registerCstFunctions(String)
	 */
	public List<String> registerCstFunctions(String packageName) {
		return cstService.registerCstFunctions(packageName);
	}

	/**
	 * @see CstService#transform(Feature, ICell)
	 */
	public Feature transform(Feature f, ICell c) {
		ATransaction trans = log.begin("CST: Feature transformation"); //$NON-NLS-1$
		try {
			return cstService.transform(f, c);
		}
		finally {
			trans.end();
		}
	}

	/**
	 * @see CstService#transform(FeatureCollection, IAlignment, Set)
	 */
	public FeatureCollection<? extends FeatureType, ? extends Feature> transform(
			FeatureCollection<? extends FeatureType, ? extends Feature> fc,
			IAlignment al, Set<FeatureType> targetSchema) {
		ATransaction trans = log.begin("CST: FeatureCollection transformation"); //$NON-NLS-1$
		try {
			return cstService.transform(fc, al, targetSchema);
		}
		finally {
			trans.end();
		}
	}

}
