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

package eu.esdihumboldt.goml.oml.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;

import eu.esdihumboldt.cst.align.IAlignment;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.IEntity;
import eu.esdihumboldt.cst.align.IFormalism;
import eu.esdihumboldt.cst.align.ISchema;
import eu.esdihumboldt.cst.align.ICell.RelationType;
import eu.esdihumboldt.cst.align.ext.ITransformation;
import eu.esdihumboldt.goml.oml.ext.ValueExpression;
import eu.esdihumboldt.goml.omwg.ComparatorType;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.omwg.PropertyQualifier;
import eu.esdihumboldt.goml.omwg.Relation;
import eu.esdihumboldt.goml.omwg.Restriction;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.generated.AlignmentType;
import eu.esdihumboldt.goml.generated.CellType;
import eu.esdihumboldt.goml.generated.ClassConditionType;
import eu.esdihumboldt.goml.generated.ClassType;
import eu.esdihumboldt.goml.generated.ComparatorEnumType;
import eu.esdihumboldt.goml.generated.Entity1;
import eu.esdihumboldt.goml.generated.Entity2;
import eu.esdihumboldt.goml.generated.EntityType;
import eu.esdihumboldt.goml.generated.FormalismType;
import eu.esdihumboldt.goml.generated.FunctionType;
import eu.esdihumboldt.goml.generated.Measure;
import eu.esdihumboldt.goml.generated.OnAttributeType;
import eu.esdihumboldt.goml.generated.OntologyType;
import eu.esdihumboldt.goml.generated.RelationEnumType;
import eu.esdihumboldt.goml.generated.RestrictionType;
import eu.esdihumboldt.goml.generated.ValueClassType;
import eu.esdihumboldt.goml.generated.AlignmentType.Map;
import eu.esdihumboldt.goml.generated.AlignmentType.Onto1;
import eu.esdihumboldt.goml.generated.AlignmentType.Onto2;
import eu.esdihumboldt.goml.generated.OntologyType.Formalism;
import eu.esdihumboldt.goml.generated.PropertyType;


/**
 * This class implements methods 
 * for marshalling HUMBOLDT OML Objects 
 * to XML. 
 * 
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class OmlRdfGenerator {
 

/**
  * Stores alignment to xml 
  * @param alignment, to be stored
  * @param xmlPath, path to the xml-file 
  */
	public void write(IAlignment alignment, String xmlPaath){
		//1. convert OML Alignment to the jaxb generated AlignmentType
		AlignmentType aType = getAlignment(alignment);
		//2. marshall AlignmentType to xml
	 
 }

    /**
     * Converts from HUMBOLDT alignment to the
     * JAXB generated alignment type.
     * @param alignment, HUMBOLDT OML alignment 
     * @return alignmentType
     */
	private AlignmentType getAlignment(IAlignment alignment) {
	AlignmentType aType = new AlignmentType();
	//1. set about,level, ontology1,2
	aType.setAbout(((About)alignment.getAbout()).getAbout());
	aType.setLevel(alignment.getLevel());
	aType.setOnto1(getOnto1(alignment.getSchema1()));
	aType.setOnto2(getOnto2(alignment.getSchema2()));
	//2. add map of cells
	aType.getMap().addAll(getMaps(alignment.getMap()));
	
	return aType;
}

	/**
	 * Converts from HUMBOLDT ISchema to Onto1.
	 * @param schema
	 * @return onto1
	 */
	private Onto1 getOnto1(ISchema schema1) {
		Onto1 onto1 = new Onto1();
		onto1.setOntology(getOntologyType(schema1));
		return onto1;
	}
	
	
	/**
	 * Converts from HUMBOLDT ISchema to Onto1.
	 * @param schema
	 * @return onto2
	 */
	private Onto2 getOnto2(ISchema schema2) {
		Onto2 onto2 = new Onto2();
		onto2.setOntology(getOntologyType(schema2));
		return onto2;
	}

	/**
	 * Converts from HUMBOLDT ISchema 
	 * to the Jaxb generated OntologyType
	 * @param schema
	 * @return ontologyType
	 */
	private OntologyType getOntologyType(ISchema schema) {
		OntologyType oType = new OntologyType();
		oType.setAbout(((About)schema.getAbout()).getAbout());
		oType.setLocation(schema.getLocation());
		oType.setFormalism(getFormalism(schema.getFormalism()));
		return oType;
	}

	/**
	 * Converts from HUMBOLDT IFormalism
	 * to the JAXB generated Formalism
	 * @param formalism
	 * @return
	 */
	private Formalism getFormalism(IFormalism formalism) {
		Formalism jFormalism = new Formalism();
		jFormalism.setFormalism(getFormalismType(formalism));
		return jFormalism;
	}

	/**
	 * Converts from HUMBOLDT IFormalism
	 * to the JAXB generated FormalismType
	 * @param formalism
	 * @return
	 */
	private FormalismType getFormalismType(IFormalism formalism) {
		FormalismType fType = new FormalismType();
		fType.setName(formalism.getName());
		fType.setUri(formalism.getLocation().toString());
		return fType;
	}

	/**
	 * Converts from List of ICell
	 * to the List of Map
	 * @param map
	 * @return
	 */
	private Collection<? extends Map> getMaps(List<ICell> map) {
		ArrayList<Map> maps = new ArrayList<Map>(map.size());
		Iterator iterator = map.iterator();
		Map jMap;
		ICell cell;
		
		while(iterator.hasNext()){
			//TODO: clear about elemenet for each map
			cell = (ICell)iterator.next();
			jMap = new Map();
			jMap.setCell(getCellType(cell));
			 maps.add(jMap);
			
		}
		return maps;
	}

	/**
	 * Converts from HUMBOLDT ICell
	 * to the JAXB CellType
	 * @param cell
	 * @return
	 */
	private CellType getCellType(ICell cell) {
		CellType cType = new CellType();
		cType.setAbout(((About)cell.getAbout()).getAbout());
		cType.setMeasure(getMeasure(cell.getMeasure()));
		cType.setRelation(getRelation(cell.getRelation()));
		cType.setEntity1(getEntity1(cell.getEntity1()));
		cType.setEntity2(getEntity2(cell.getEntity2()));
		return cType;
	}

	/**
	 * Converts from double to 
	 * the Jaxb generated Measure
	 * @param measure
	 * @return
	 */
    private Measure getMeasure(double measure) {
		Measure jMeasure = new Measure();
		jMeasure.setDatatype("xsd:float");
		jMeasure.setValue(new Double(measure).floatValue());
		return jMeasure;
	}
	
    /**
     * converts from  RelationType
     * to RelationEnumType 
     * @param relation
     * @return
     */
    private RelationEnumType getRelation(RelationType relation) {
		
			if (relation.equals(RelationType.Disjoint)) {return RelationEnumType.DISJOINT;}
			else if (relation.equals(RelationType.Equivalence)) {return RelationEnumType.EQUIVALENCE;}
			else if (relation.equals(RelationType.Extra)) {return RelationEnumType.EXTRA;}
			else if (relation.equals(RelationType.HasInstance)){return RelationEnumType.HAS_INSTANCE;}
			else if (relation.equals(RelationType.InstanceOf)){ return RelationEnumType.INSTANCE_OF;}
			else if (relation.equals(RelationType.Missing)) {return RelationEnumType.MISSING;}
			else if (relation.equals(RelationType.PartOf)) {return RelationEnumType.PART_OF;}
			else if (relation.equals(RelationType.SubsumedBy)){return RelationEnumType.SUBSUMED_BY;}
			else if (relation.equals(RelationType.Subsumes)){ return RelationEnumType.SUBSUMES;}
			return null;
		
	}
    /**
     * converts from IEntity to the JAXB Entity2
     * @param entity2
     * @return
     */
	private Entity2 getEntity2(IEntity entity2) {
		Entity2 jE2 = new Entity2();
		jE2.setEntity(getEntityType(entity2));
		return jE2;
	}

	

	/**
     * converts from IEntity to the JAXB Entity1
     * @param entity1
     * @return
     */
	private Entity1 getEntity1(IEntity entity1) {
		Entity1 jE1 = new Entity1();
		jE1.setEntity(getEntityType(entity1));
		return jE1;
	}
	/**
	 * converts from IEntity to the Jaxb generated EntityType
	 * @param entity
	 * @return
	 */
	 private JAXBElement<? extends EntityType> getEntityType(IEntity entity) {
		 JAXBElement<? extends EntityType> eType = null;
		 
		 if (entity instanceof Property){
			 //instantiate as PropertyType
			 Property property = (Property)entity;
			 PropertyType pType = getPropertyType(property);
			 eType = new JAXBElement<PropertyType>(null, null, pType);
			 
		 }else if (entity instanceof FeatureClass){
			 //instantiate as ClassType 
			 FeatureClass feature = (FeatureClass)entity;
			 ClassType cType = getClassType(feature);
			 eType = new JAXBElement<ClassType>(null, null, cType);
		 }else if (entity instanceof Relation){
			 //instantiate as RelationType
			 //TODO add implementation
		 }else if (entity instanceof PropertyQualifier){
			//instantiate as PropertyQualifierType
			 //TODO add implementation
		 }
		 
			
			return eType;
		}

	/**
	 * Converts from OML FeatureClass
	 * to the JAXB ClassType
	 * @param feature
	 * @return
	 */
	 private ClassType getClassType(FeatureClass feature) {
		ClassType cType = new ClassType();
		cType.setAbout(((About)feature.getAbout()).getAbout());
		cType.setTransf(getTransf(feature.getTransformation()));
		cType.getAttributeTypeCondition().addAll(getConditions(feature.getAttributeTypeCondition()));
		cType.getAttributeValueCondition().addAll(getConditions(feature.getAttributeValueCondition()));
		cType.getAttributeOccurenceCondition().addAll(getConditions(feature.getAttributeOccurenceCondition()));
		return cType;
	}

	 /**
	  * Converts from List of OML Restrictions
	  * to the List of the Jaxb ClassConditionType
	  * @param attributeTypeCondition
	  * @return
	  */
	 private Collection<? extends ClassConditionType> getConditions(
			List<Restriction> restrictions) {
		ArrayList<ClassConditionType> conditions = new ArrayList<ClassConditionType>(restrictions.size());
		ClassConditionType condition;
		Restriction restriction;
		Iterator iterator = restrictions.iterator();
		while(iterator.hasNext()){
			restriction = (Restriction)iterator.next();
			condition = new ClassConditionType();
			condition.setRestriction(getRestrictionType(restriction));
			conditions.add(condition);
		}
		return conditions;
	}

	/**
	 * Converts from HUMBOLDT OML Restriction
	 * to the JAXB RestrictionType 
	 * @param restriction
	 * @return
	 */
	 private RestrictionType getRestrictionType(Restriction restriction) {
		RestrictionType rType = new RestrictionType();
		rType.setComparator(getComparator(restriction.getComparator()));
		rType.setCqlStr(restriction.getCqlStr());
		rType.setOnAttribute(getOnAttributeType(restriction.getOnAttribute()));
		rType.setValueClass(getValueClass(restriction.getValue()));
		return rType;
	}

	/**
	 * Converts from List of HUMBOLDT OML ValueExpression
	 * To the JAXB generated ValueClassType 
	 * @param value
	 * @return
	 */
	private ValueClassType getValueClass(List<ValueExpression> value) {
		ValueClassType vcType = new ValueClassType();
		//TODO add implementation after discussion with MdV
		return vcType;
	}

	/**
	 * Converts from OML Property 
	 * to OnAttributeType
	 * @param onAttribute
	 * @return
	 */
	private OnAttributeType getOnAttributeType(Property onAttribute) {
		
		return null;
	}

	private ComparatorEnumType getComparator(ComparatorType comparator) {
		// TODO Auto-generated method stub
		return null;
	}

	private FunctionType getTransf(ITransformation transformation) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	  * Converts from OML Property
	  * to the JAXB PropertyType
	  * @param property
	  * @return
	  */
	private PropertyType getPropertyType(Property property) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
