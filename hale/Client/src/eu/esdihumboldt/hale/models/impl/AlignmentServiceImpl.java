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
package eu.esdihumboldt.hale.models.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureImpl;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.rcp.utils.ModelNavigationViewHelper;
import eu.esdihumboldt.hale.rcp.utils.ModelNavigationViewHelper.SelectionType;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject.TreeObjectType;

/**
 * This is a simple default implementation that manages a single Alignment
 * document.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class AlignmentServiceImpl implements AlignmentService {

	private static Logger _log = Logger.getLogger(AlignmentServiceImpl.class);

	private Alignment alignment;

	private static AlignmentService instance = new AlignmentServiceImpl();

	private Set<HaleServiceListener> listeners = new HashSet<HaleServiceListener>();

	// Constructor/ instance access ............................................

	private AlignmentServiceImpl() {
		super();
		this.initNewAlignment();

	}

	/**
	 * 
	 */
	private void initNewAlignment() {
		this.alignment = new Alignment();
		this.alignment.setAbout(new About(UUID.randomUUID()));
		this.alignment.setLevel("");
		this.alignment.setMap(new ArrayList<ICell>());
	}

	public static AlignmentService getInstance() {
		return AlignmentServiceImpl.instance;
	}

	// AlignmentService operations .............................................

	private void copyFeatures() {
		InstanceService instanceService = InstanceServiceImpl.getInstance();

		// Get the original reference data
		FeatureCollection<?, Feature> fc = instanceService.getFeatures(DatasetType.reference);
		FeatureIterator<Feature> iterator = fc.features();

		// Checks if already transformed data is available
		// If not, copy the reference data into the transformed data.
		FeatureCollection<?, Feature> trans = instanceService.getFeatures(DatasetType.transformed);
		if (trans == null) {
			System.err.println("No transformed data available. creating a copy of reference data");
			FeatureCollection referenceCopy = FeatureCollections
					.newCollection();
//			for (Feature feature : features) {
//				referenceCopy.add(feature);
//			}
//			instanceService.addInstances(DatasetType.transformed, referenceCopy);
			System.err.println("Transformed data added. Number of features: " + referenceCopy.size());
		}
		else System.err.println("Transformed data available.");
		
		// Create a new feature collection
		FeatureCollection fc1 = FeatureCollections.newCollection();
		while (iterator.hasNext()) {
			Feature feature = iterator.next();
			Feature copy = (Feature)SimpleFeatureBuilder.deep((SimpleFeature) feature);
			fc1.add(copy);
		}
		System.err.println("New feature collection created.");

//		// Replaces the transformed features with the new features
//		instanceService.replaceInstances(DatasetType.transformed, fc1);

	}
	
	/**
	 * @see eu.esdihumboldt.hale.models.AlignmentService#addOrUpdateCell(eu.esdihumboldt.goml.align.Cell)
	 */
	public boolean addOrUpdateCell(Cell cell) {
		boolean result = this.alignment.getMap().add(cell);
		return result;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.AlignmentService#cleanModel()
	 */
	public boolean cleanModel() {
		this.initNewAlignment();
		this.updateListeners();
		return true;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see eu.esdihumboldt.hale.models.AlignmentService#getCell(eu.esdihumboldt.goml.align.Entity)
	 */
	@Override
	public List<ICell> getCell(Entity entity) {
		List<ICell> result = new ArrayList<ICell>();
		for (ICell c : this.alignment.getMap()) {
			boolean e1NamespaceMatch = false;
			boolean e1LocalnameMatch = false;
			boolean e2NamespaceMatch = false;
			boolean e2LocalnameMatch = false;
			if (c.getEntity1() != null) {
				e1NamespaceMatch = c.getEntity1().getLabel().get(0).equals(
						entity.getLabel().get(0));
				e1LocalnameMatch = c.getEntity1().getLabel().get(1).equals(
						entity.getLabel().get(1));
			}
			if (c.getEntity2() != null) {
				e2NamespaceMatch = c.getEntity2().getLabel().get(0).equals(
						entity.getLabel().get(0));
				e2LocalnameMatch = c.getEntity2().getLabel().get(1).equals(
						entity.getLabel().get(1));
			}
			if ((e1NamespaceMatch && e1LocalnameMatch)
					|| (e2NamespaceMatch && e2LocalnameMatch)) {
				result.add(c);
			}
		}
		return result;
	}

	public ICell getCell(Entity e1, Entity e2) {
		for (ICell c : this.alignment.getMap()) {
			boolean e1NamespaceMatch = false;
			boolean e1LocalnameMatch = false;
			boolean e2NamespaceMatch = false;
			boolean e2LocalnameMatch = false;
			if (c.getEntity1() != null) {
				e1NamespaceMatch = c.getEntity1().getLabel().get(0).equals(
						e1.getLabel().get(0));
				e1LocalnameMatch = c.getEntity1().getLabel().get(1).equals(
						e1.getLabel().get(1));
			}
			if (c.getEntity2() != null) {
				e2NamespaceMatch = c.getEntity2().getLabel().get(0).equals(
						e2.getLabel().get(0));
				e2LocalnameMatch = c.getEntity2().getLabel().get(1).equals(
						e2.getLabel().get(1));
			}
			if ((e1NamespaceMatch && e1LocalnameMatch)
					&& (e2NamespaceMatch && e2LocalnameMatch)) {
				return c;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.esdihumboldt.hale.models.AlignmentService#loadAlignment(java.net.URI)
	 */
	@Override
	public boolean loadAlignment(URI file) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not yet implemented, sorry.");
	}

	/**
	 * This method allows to set the properties of the Alignment object itself,
	 * such as the schemas being mapped. It does not update the Cells list.
	 * 
	 * @see eu.esdihumboldt.hale.models.AlignmentService#addOrUpdateAlignment(eu.esdihumboldt.goml.align.Alignment)
	 */
	@Override
	public boolean addOrUpdateAlignment(Alignment alignment) {
		System.out.println("Updating alignment");
		if (alignment.getSchema1() != null) {
			this.alignment.setSchema1(alignment.getSchema1());
		}
		if (alignment.getSchema2() != null) {
			this.alignment.setSchema1(alignment.getSchema2());
		}
		if (alignment.getLevel() != null) {
			this.alignment.setLevel(alignment.getLevel());
		}
		if (alignment.getAbout() != null) {
			this.alignment.setAbout(alignment.getAbout());
		}
		System.out.println("Alignment updated");
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.AlignmentService#getAlignment()
	 */
	@Override
	public Alignment getAlignment() {
		return this.alignment;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.AlignmentService#getAlignmentForType(org.opengis.feature.type.FeatureType)
	 */
	@Override
	public List<ICell> getAlignmentForType(FeatureType type) {
		List<String> labels = new ArrayList<String>();
		labels.add(type.getName().getNamespaceURI());
		labels.add(type.getName().getLocalPart());
		Entity e = new Entity(labels);
		return this.getCell(e);
	}

	public ICell getAlignmentForType(FeatureType type1, FeatureType type2) {
		List<String> labels = new ArrayList<String>();
		labels.add(type1.getName().getNamespaceURI());
		labels.add(type1.getName().getLocalPart());
		Entity e1 = new Entity(labels);
		labels = new ArrayList<String>();
		labels.add(type2.getName().getNamespaceURI());
		labels.add(type2.getName().getLocalPart());
		Entity e2 = new Entity(labels);
		return this.getCell(e1, e2);
	}

	// UpdateService operations ................................................

	/**
	 * @see eu.esdihumboldt.hale.models.UpdateService#addListener(eu.esdihumboldt.hale.models.HaleServiceListener)
	 */
	public boolean addListener(HaleServiceListener sl) {
		return this.listeners.add(sl);
	}

	/**
	 * Inform {@link HaleServiceListener}s of an update.
	 */
	private void updateListeners() {
		for (HaleServiceListener hsl : this.listeners) {
			_log.info("Updating a listener.");
			hsl.update();
		}
	}

}
