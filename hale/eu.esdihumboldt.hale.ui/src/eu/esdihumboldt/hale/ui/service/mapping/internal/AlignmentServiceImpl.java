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
package eu.esdihumboldt.hale.ui.service.mapping.internal;

import static eu.esdihumboldt.cst.transformer.EntityUtils.entitiesMatch;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.opengis.feature.type.FeatureType;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

import eu.esdihumboldt.commons.goml.align.Alignment;
import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.commons.goml.omwg.FeatureClass;
import eu.esdihumboldt.commons.goml.rdf.About;
import eu.esdihumboldt.hale.ui.service.mapping.AlignmentService;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.IEntity;

/**
 * This is a simple default implementation that manages a single Alignment
 * document.
 * 
 * @author Thorsten Reitz, Simon Templer 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class AlignmentServiceImpl extends AbstractAlignmentService {

	private static ALogger _log = ALoggerFactory.getLogger(AlignmentServiceImpl.class);

	private Alignment alignment;

	private static AlignmentService instance = new AlignmentServiceImpl();

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
		this.alignment.setLevel(""); //$NON-NLS-1$
		this.alignment.setMap(new ArrayList<ICell>());
	}

	/**
	 * Get the {@link AlignmentServiceImpl} instance
	 * 
	 * @return the {@link AlignmentServiceImpl} instance
	 */
	public static AlignmentService getInstance() {
		return AlignmentServiceImpl.instance;
	}

	// AlignmentService operations .............................................
	
	/**
	 * @see AlignmentService#addOrUpdateCell(ICell)
	 */
	@Override
	public boolean addOrUpdateCell(ICell cell) {
		Collection<ICell> added = new ArrayList<ICell>();
		Collection<ICell> updated = new ArrayList<ICell>();
		boolean result = internalAddOrUpdateCell(cell, added, updated);
		if (!added.isEmpty()) {
			notifyCellsAdded(added);
		}
		if (!updated.isEmpty()) {
			notifyCellsUpdated(updated);
		}
		return result;
	}

	/**
	 * Add or update a cell without firing an event
	 * 
	 * @param cell the cell to add/update
	 * @param added a collection the cell will be added to if it was added
	 * @param updated a collection the cell will be added to if it was updated
	 * 
	 * @return if the cell was added/updated
	 */
	private boolean internalAddOrUpdateCell(ICell cell, Collection<ICell> added, Collection<ICell> updated) {
		ICell oldCell = getCellInternal(cell.getEntity1(), cell.getEntity2());
		if (oldCell != null) {
			alignment.getMap().remove(oldCell);
			_log.info("Replacing alignment cell"); //$NON-NLS-1$
			updated.add(cell);
		}
		else {
			added.add(cell);
		}
		
		return this.alignment.getMap().add(cell);
	}

	/**
	 * @see AlignmentService#cleanModel()
	 */
	@Override
	public boolean cleanModel() {
		initNewAlignment();
		notifyAlignmentCleared();
		return true;
	}

	/**
	 * @see AlignmentService#getCell(Entity)
	 */
	@Override
	public List<ICell> getCell(Entity entity) {
		List<ICell> result = new ArrayList<ICell>();
		for (ICell c : this.alignment.getMap()) {
			if (entitiesMatch(entity, c.getEntity1())
					|| (entitiesMatch(entity, c.getEntity2()))) {
				result.add(c);
			}
		}
		return result;
	}

	/**
	 * @see AlignmentService#getCells()
	 */
	@Override
	public List<ICell> getCells() {
		return new ArrayList<ICell>(alignment.getMap());
	}

	/**
	 * @see AlignmentService#getCell(Entity, Entity)
	 */
	@Override
	public ICell getCell(Entity e1, Entity e2) {
		return getCellInternal(e1, e2);
	}
	
	private ICell getCellInternal(IEntity e1, IEntity e2) {
		for (ICell c : this.alignment.getMap()) {
			if (entitiesMatch(e1, c.getEntity1()) && 
					entitiesMatch(e2, c.getEntity2())) {
				return c;
			}
		}
		return null;
	}

	/**
	 * @see AlignmentService#loadAlignment(URI)
	 */
	@Override
	public boolean loadAlignment(URI uri) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not yet implemented, sorry."); //$NON-NLS-1$
	}

	/**
	 * This method allows to set the properties of the Alignment object itself,
	 * such as the schemas being mapped. It does not update the Cells list.
	 * 
	 * @see AlignmentService#addOrUpdateAlignment(Alignment)
	 */
	@Override
	public boolean addOrUpdateAlignment(Alignment alignment) {
		Collection<ICell> added = new ArrayList<ICell>();
		Collection<ICell> updated = new ArrayList<ICell>();
		
		if (alignment.getSchema1() != null) {
			this.alignment.setSchema1(alignment.getSchema1());
		}
		if (alignment.getSchema2() != null) {
			this.alignment.setSchema2(alignment.getSchema2());
		}
		if (alignment.getLevel() != null) {
			this.alignment.setLevel(alignment.getLevel());
		}
		if (alignment.getAbout() != null) {
			this.alignment.setAbout(alignment.getAbout());
		}
		
		// add cells
		for (ICell cell : alignment.getMap()) {
			internalAddOrUpdateCell(cell, added, updated);
		}
		
		if (!added.isEmpty()) {
			notifyCellsAdded(added);
		}
		if (!updated.isEmpty()) {
			notifyCellsUpdated(updated);
		}
		
		return true;
	}

	/**
	 * @see AlignmentService#getAlignment()
	 */
	@Override
	public Alignment getAlignment() {
		return this.alignment;
	}

	/**
	 * @see AlignmentService#getAlignmentForType(FeatureType)
	 */
	@Override
	public List<ICell> getAlignmentForType(FeatureType type) {
		Entity e = new FeatureClass(
				new About(type.getName().getNamespaceURI(), 
						type.getName().getLocalPart()));
		return this.getCell(e);
	}

	/**
	 * @see AlignmentService#getAlignmentForType(FeatureType, FeatureType)
	 */
	@Override
	public ICell getAlignmentForType(FeatureType type1, FeatureType type2) {
		Entity e1 = new FeatureClass(new About(type1.getName().getNamespaceURI(), 
				type1.getName().getLocalPart()));
		Entity e2 = new FeatureClass(new About(type2.getName().getNamespaceURI(), 
				type2.getName().getLocalPart()));
		return this.getCell(e1, e2);
	}

	// UpdateService operations ................................................

	/**
	 * @see AlignmentService#removeCell(ICell)
	 */
	@Override
	public void removeCell(ICell cell) {
		alignment.getMap().remove(cell);
		
		notifyCellRemoved(cell);
	}

}
