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
package eu.esdihumboldt.hale.models.alignment;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.IEntity;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.models.UpdateService;

/**
 * This is a simple default implementation that manages a single Alignment
 * document.
 * 
 * @author Thorsten Reitz, Simon Templer 
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
	public boolean addOrUpdateCell(ICell cell) {
		ICell oldCell = getCellInternal(cell.getEntity1(), cell.getEntity2());
		if (oldCell != null) {
			alignment.getMap().remove(oldCell);
			_log.info("Replacing alignment cell");
		}
		
		boolean result = this.alignment.getMap().add(cell);
		this.updateListeners();
		return result;
	}

	/**
	 * @see AlignmentService#cleanModel()
	 */
	public boolean cleanModel() {
		this.initNewAlignment();
		this.updateListeners();
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
	 * Determine if two non-null entities match
	 * 
	 * @param e1 the first entity
	 * @param e2 the second entity
	 * @return if the entities match
	 */
	protected boolean entitiesMatch(IEntity e1, IEntity e2) {
		if (e1 != null && e2 != null) {
			String about1 = e1.getAbout().getAbout();
			String about2 = e2.getAbout().getAbout();
			
			if (about1.equals(about2)) {
				return true;
			}
			else {
				// about doesn't match
				
				// check composed properties
				if (e1 instanceof ComposedProperty && e2 instanceof ComposedProperty) {
					// both entities are composed properties
					return compositionsMatch((ComposedProperty) e1, (ComposedProperty) e2);
				}
				else if (e1 instanceof ComposedProperty && e2 instanceof Property) {
					// e1 is composed, e2 is a property
					return compositionContains((ComposedProperty) e1, (Property) e2);
				}
				else if (e1 instanceof Property && e2 instanceof ComposedProperty) {
					// e1 is property, e2 is composed
					return compositionContains((ComposedProperty) e2, (Property) e1);
				}
				//TODO check composed feature types
				
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	/**
	 * Determine if two composed properties match
	 * 
	 * @param c1 the first composed property
	 * @param c2 the second composed property
	 * 
	 * @return if both compositions contain the same properties
	 */
	private boolean compositionsMatch(ComposedProperty c1, ComposedProperty c2) {
		boolean c2Empty = c2.getCollection() == null || c2.getCollection().isEmpty();
		
		if (c1.getCollection() == null || c1.getCollection().isEmpty()) {
			return c2Empty;
		}
		else {
			if (c2Empty) {
				return false;
			}
			
			List<Property> c2Properties = new ArrayList<Property>(c2.getCollection());
			
			for (Property c1Property : c1.getCollection()) {
				Property match = null;
				
				Iterator<Property> itC2 = c2Properties.iterator();
				while (itC2.hasNext() && match == null) {
					Property c2Property = itC2.next();
					
					if (entitiesMatch(c1Property, c2Property)) {
						match = c2Property;
					}
				}
				
				if (match == null) {
					// no match for a property found
					return false;
				}
				else {
					c2Properties.remove(match);
				}
			}
			
			// all properties were matched
			// if the collection is empty there are no additional properties in c2
			return c2Properties.isEmpty();
		}
	}

	/**
	 * Determine if the given composed property contains the given property
	 * 
	 * @param composition the composed property
	 * @param property the property
	 * 
	 * @return if the property is contained in the composition
	 */
	private boolean compositionContains(ComposedProperty composition, Property property) {
		if (composition.getCollection() != null) {
			for (Property candidate : composition.getCollection()) {
				if (entitiesMatch(candidate, property)) {
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * @see AlignmentService#getCell(Entity, Entity)
	 */
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
		this.alignment.getMap().addAll(alignment.getMap()); // FIXME check whether a cell is there.
		this.updateListeners();
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
	public ICell getAlignmentForType(FeatureType type1, FeatureType type2) {
		Entity e1 = new FeatureClass(new About(type1.getName().getNamespaceURI(), 
				type1.getName().getLocalPart()));
		Entity e2 = new FeatureClass(new About(type2.getName().getNamespaceURI(), 
				type2.getName().getLocalPart()));
		return this.getCell(e1, e2);
	}

	// UpdateService operations ................................................

	/**
	 * @see UpdateService#addListener(HaleServiceListener)
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
			hsl.update(new UpdateMessage(AlignmentService.class, null)); // FIXME
		}
	}

	/**
	 * @see AlignmentService#removeCell(ICell)
	 */
	@Override
	public void removeCell(ICell cell) {
		alignment.getMap().remove(cell);
		
		updateListeners();
	}

}
