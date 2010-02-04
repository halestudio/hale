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

package eu.esdihumboldt.cst.corefunctions;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.AbstractCstFunction;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.rdf.About;

/**
 * This function will populate the nilReason attribute of any properties that 
 * have not yet been assigned a value.
 *
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class NilReasonFunction extends AbstractCstFunction {
	
	/**
	 * Name of the NilReasonType parameter
	 */
	public static final String PARAMETER_NIL_REASON_TYPE = "NilReasonType";
	
	private String nilReason = null;
	
	private Entity onEntity = null; 

	/**
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#configure(eu.esdihumboldt.cst.align.ICell)
	 */
	public boolean configure(ICell cell) {
		this.onEntity = (Entity) cell.getEntity2();
		for(IParameter ip : cell.getEntity2().getTransformation().getParameters()) {
			if (ip.getName().equals(PARAMETER_NIL_REASON_TYPE)) {
				this.nilReason = ip.getValue();
			}
		}
		return false;
	}


	public Cell getParameters() {
		Cell parameterCell = new Cell();	
				
		eu.esdihumboldt.goml.omwg.Property entity2 = 
			new eu.esdihumboldt.goml.omwg.Property(new About(""));
		// Setting of type condition for entity2
		List <String> entity2Types = new ArrayList <String>();
		entity2Types.add(com.vividsolutions.jts.geom.Geometry.class.getName());
		entity2Types.add(org.opengis.geometry.Geometry.class.getName());
		entity2Types.add(String.class.getName());
		entity2Types.add(Number.class.getName());
		entity2Types.add(Boolean.class.getName());
		entity2Types.add(Date.class.getName());
		entity2Types.add(Collection.class.getName());
		entity2.setTypeCondition(entity2Types);
	
		Transformation t = new Transformation();
		List<IParameter> params = new ArrayList<IParameter>(); 
			
		Parameter p   = 
			new Parameter(PARAMETER_NIL_REASON_TYPE,"");
		
		params.add(p);		
		entity2.setTransformation(t);	
		parameterCell.setEntity2(entity2);
		return parameterCell;
	}

	/**
	 * @see eu.esdihumboldt.cst.transformer.CstFunction#transform(org.opengis.feature.Feature, org.opengis.feature.Feature)
	 */
	public Feature transform(Feature source, Feature target) {
		if (NilReasonType.unpopulated.equals(this.nilReason)) {
			if (this.onEntity.getLocalname().equals(target.getName().getLocalPart())) {
				// affects the entire entity
				for (org.opengis.feature.Property p : target.getProperties()) {
					this.setNilReason(target, p, NilReasonType.unpopulated);
				}
			}
			else {
				// affects only a single property
				this.setNilReason(target, target.getProperty(
						this.onEntity.getLocalname()), NilReasonType.unpopulated);
			}
		}
		else if (NilReasonType.unknown.equals(this.nilReason)) {
			if (this.onEntity.getLocalname().equals(target.getName().getLocalPart())) {
				// affects the entire entity
				for (org.opengis.feature.Property p : target.getProperties()) {
					if (p.getValue() == null) {
						this.setNilReason(target, p, NilReasonType.unknown);
					}
				}
			}
			else {
				// affects only a single property
				Property p = target.getProperty(this.onEntity.getLocalname());
				if (p.getValue() == null) { 
					this.setNilReason(target, p, NilReasonType.unknown);
				}
			}
		}
		return target;
	}
	
	
	private void setNilReason(Feature target, Property p, NilReasonType nrt) {
//		LenientAttribute at = new LenientAttribute(
//				"<" + p.getName().getLocalPart() +"/>", ((AttributeDescriptor) p.getDescriptor()), null);
//		((SimpleFeature)target).setAttribute(p.getName(), at);
	}


	/**
	 * Nil reason types
	 */
	public enum NilReasonType {
		/** unpopulated: there was no corresponding data in the source at all */
		unpopulated,
		/** unknown: there was no corresponding data in a single source instance */
		unknown
	}

	
}
