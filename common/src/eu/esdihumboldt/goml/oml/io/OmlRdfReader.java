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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.IEntity;

import eu.esdihumboldt.cst.align.ISchema;
import eu.esdihumboldt.cst.align.ICell.RelationType;
import eu.esdihumboldt.cst.rdf.IAbout;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.align.Formalism;
import eu.esdihumboldt.goml.align.Schema;
import eu.esdihumboldt.goml.generated.AlignmentType;
import eu.esdihumboldt.goml.generated.ApplyType;
import eu.esdihumboldt.goml.generated.CellType;
import eu.esdihumboldt.goml.generated.ClassConditionType;
import eu.esdihumboldt.goml.generated.ClassType;
import eu.esdihumboldt.goml.generated.ComparatorEnumType;
import eu.esdihumboldt.goml.generated.DomainRestrictionType;
import eu.esdihumboldt.goml.generated.EntityType;
import eu.esdihumboldt.goml.generated.FormalismType;
import eu.esdihumboldt.goml.generated.Measure;
import eu.esdihumboldt.goml.generated.OntologyType;
import eu.esdihumboldt.goml.generated.PropertyType;
import eu.esdihumboldt.goml.generated.RelationEnumType;
import eu.esdihumboldt.goml.generated.RestrictionType;
import eu.esdihumboldt.goml.generated.ValueClassType;
import eu.esdihumboldt.goml.generated.ValueConditionType;
import eu.esdihumboldt.goml.generated.ValueExprType;
import eu.esdihumboldt.goml.generated.AlignmentType.Map;
import eu.esdihumboldt.goml.generated.AlignmentType.Onto1;
import eu.esdihumboldt.goml.oml.ext.Function;
import eu.esdihumboldt.goml.oml.ext.ValueClass;
import eu.esdihumboldt.goml.oml.ext.ValueExpression;
import eu.esdihumboldt.goml.omwg.ComparatorType;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.omwg.Restriction;
import eu.esdihumboldt.goml.rdf.About;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
/**
 * This class reads the OML Rdf Document into Java Object.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class OmlRdfReader {
	/**
	 * Constant defines the path to the alignment jaxb context
	 */
	private static final String ALIGNMENT_CONTEXT = "eu.esdihumboldt.goml.generated";

	/**
	 * Unmarshalls oml-mapping to the HUMBOLDT Alignment.
	 * 
	 * @param rdfFile
	 *            path to the oml-mapping file
	 * @return Alignment object
	 */
	public Alignment read(String rdfFile) {
		// 1. unmarshal rdf
		JAXBContext jc;
		JAXBElement<AlignmentType> root = null;
		try {
			jc = JAXBContext.newInstance(ALIGNMENT_CONTEXT);
            Unmarshaller u = jc.createUnmarshaller();

			// it will debug problems while unmarchalling
            u.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
            root = u.unmarshal(new StreamSource(new File(rdfFile)),
					AlignmentType.class);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AlignmentType genAlignment = root.getValue();
		
		// 2. create humboldt alignment object and fulfill the required fields
		Alignment al = new Alignment();
		//set about
		al.setAbout(new About(UUID.randomUUID()));
		//set level
		al.setLevel(genAlignment.getLevel());
		//set map with cells
		al.setMap(getMap(genAlignment.getMap()));
		//set schema1,2 containing information about ontologies1,2
		al.setSchema1(getSchema(genAlignment.getOnto1().getOntology()));
		al.setSchema2(getSchema(genAlignment.getOnto2().getOntology() ));
		return al;
	}

	/**
	 * converts from JAXB Ontology {@link OntologyType} 
	 * to OML schema   {@link ISchema}
	 * @param onto Ontology
	 * @return schema
	 */
	private ISchema getSchema(OntologyType onto) {
		//creates formalism
		// create Formalism
		Formalism formalism = getFormalism(onto.getFormalism());
		ISchema schema = new Schema(onto.getLocation(),formalism);
		return schema;
		
	}

	/**
	 * converts from JAXB FromalismType {@link FormalismType}
	 * to OML {@link Formalism} 
	 * @param jaxbFormalism
	 * @return Formalism
	 */
	private Formalism getFormalism(
			eu.esdihumboldt.goml.generated.OntologyType.Formalism jaxbFormalism) {
		FormalismType fType = jaxbFormalism.getFormalism();
		URI uri = null;
		try {
			uri = new URI(fType.getUri());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Formalism formalism = new Formalism(fType.getName(), uri);
		return formalism;
	}

	/**
	 * 
	 * @param map List of the generated maps, containing cell{@link CellType} and String about 
	 * @return List of Cells {@link Cell}
	 */
	private List<ICell> getMap(List<Map> maps) {
		List<ICell> cells = new ArrayList<ICell>(maps.size());
		Cell cell;
		Map map;
		Iterator iterator = maps.iterator();
		while(iterator.hasNext()){
			 map = (Map)iterator.next();
			 cell = getCell(map.getCell());
			 cells.add(cell);
			
		}
		return cells;
	}

	/**
	 * converts from {@link CellType} to {@link Cell}
	 * @param cell 
	 * @return
	 */
	private Cell getCell(CellType cellType) {
		Cell cell = new Cell();
		
		
		List<String> labels = cellType.getLabel();
		cell.setLabel(labels);
		
		//TODO check with Marian set about as UUID from string about 
		//cell.setAbout(new About(UUID.fromString(cellType.getAbout())));
		About about = new About(UUID.randomUUID());
		about.setAbout(cellType.getAbout());
		cell.setAbout(about);
		//set entity1
		cell.setEntity1(getEntity(cellType.getEntity1().getEntity()));
		//set entity2
		cell.setEntity2(getEntity(cellType.getEntity2().getEntity()));
		cell.setMeasure(cellType.getMeasure().getValue());
		cell.setRelation(getRelation(cellType.getRelation()));
		
		return cell;
	}
    /**
     * converts from RelationEnumType to RelationType
     * @param relation
     * @return
     */
	private RelationType getRelation(RelationEnumType relation) {
		
		if (relation.equals(RelationEnumType.DISJOINT)) {return RelationType.Disjoint;}
		else if (relation.equals(RelationEnumType.EQUIVALENCE)) {return RelationType.Equivalence;}
		else if (relation.equals(RelationEnumType.EXTRA)) {return RelationType.Extra;}
		else if (relation.equals(RelationEnumType.HAS_INSTANCE)){return RelationType.HasInstance;}
		else if (relation.equals(RelationEnumType.INSTANCE_OF)){ return RelationType.InstanceOf;}
		else if (relation.equals(RelationEnumType.MISSING)) {return RelationType.Missing;}
		else if (relation.equals(RelationEnumType.PART_OF)) {return RelationType.PartOf;}
		else if (relation.equals(RelationEnumType.SUBSUMED_BY)){return RelationType.SubsumedBy;}
		else if (relation.equals(RelationEnumType.SUBSUMES)){ return RelationType.Subsumes;}
		return null;
	}

	
	
   /**
    * Converts from the JAXB generated EntityType to the 
    *{@link IEntity} 
    * @param entity
    * @return
    */
	private IEntity getEntity(JAXBElement<? extends EntityType> jaxbEntity) {
		EntityType entityType = jaxbEntity.getValue();
		//TODO allow to instantiate entity as Property, FeatureClass, Relation
		//instantiate entity es property
		
		Entity entity = null;
		
		
		//TODO add convertion to the RelationType if needed
		if (entityType instanceof PropertyType){
	       entity = new Property(entityType.getLabel());
		PropertyType propertyType = ((PropertyType)entityType);
		//TODO add  Transformation to Entity
		//set property-specific members to the entity
		//set domainRestriction
		((Property)entity).setDomainRestriction(getDomainRestriction(propertyType.getDomainRestriction()));
		//set typeCondition
		((Property)entity).setTypeCondition(propertyType.getTypeCondition());
		//set value conditions
		((Property)entity).setValueCondition(getValueCondition(propertyType.getValueCondition()));
		}else if (entityType instanceof ClassType){
			//initiates entity as FeatureType
			ClassType cType = (ClassType)entityType;
			entity = new FeatureClass(null);
			((FeatureClass)entity).setAttributeOccurenceCondition(getRestrictions(cType.getAttributeOccurenceCondition()));
			((FeatureClass)entity).setAttributeTypeCondition(getRestrictions(cType.getAttributeTypeCondition()));
			((FeatureClass)entity).setAttributeValueCondition(getRestrictions(cType.getAttributeValueCondition()));
		}
		//set About
		About about = new About(UUID.randomUUID());
		about.setAbout(entityType.getAbout());
		entity.setAbout(about);
		return entity;
	}

	/**
	 * Converts from the List of DomainRestrictionType
	 * to the List of the FeatureClass 
	 * @param domainRestriction
	 * @return
	 */
	private List<FeatureClass> getDomainRestriction(
		//TODO discuss the propertytype-field
		List<DomainRestrictionType> domainRestriction) {
		List<FeatureClass> classes = new ArrayList<FeatureClass>(domainRestriction.size());
		DomainRestrictionType restriction = null;
		FeatureClass fClass;
		ClassType clazz;
		Iterator iterator = domainRestriction.iterator();
		while(iterator.hasNext()){
			restriction = (DomainRestrictionType)iterator.next();
			clazz = restriction.getClazz();
			fClass = new FeatureClass(null);
			//set about
			About fAbout = new About(java.util.UUID.randomUUID());
			fAbout.setAbout(restriction.getClazz().getAbout());
			fClass.setAbout(fAbout);
			//set attributeValueCondition list
			fClass.setAttributeValueCondition(getRestrictions(clazz.getAttributeValueCondition()));
			//set attributeTypeCondition list
			fClass.setAttributeTypeCondition(getRestrictions(clazz.getAttributeTypeCondition()));
			//set attributeOccurenceCondition
			fClass.setAttributeOccurenceCondition(getRestrictions(clazz.getAttributeOccurenceCondition()));
			classes.add(fClass);
		}
	return classes;
}
 /**
  * converts from a list of the ClassConditionType to
  *  the list of the Restriction type
  * @param List of ClassConditionType
  * @return
  */
	private List<Restriction> getRestrictions(
			List<ClassConditionType> classConditions) {
		List<Restriction> restrictions = new ArrayList<Restriction>(classConditions.size());
		Iterator iterator = classConditions.iterator();
		Restriction restriction;
		ClassConditionType classCondition;
		while (iterator.hasNext()){
			classCondition = (ClassConditionType)iterator.next();
			RestrictionType rType = classCondition.getRestriction();
			List<ValueExprType> valueExpr = rType.getValue();
			restriction = new Restriction(null, getValueExpression(valueExpr));
			//set value class to add about and resource document
			ValueClass vClass = new ValueClass();
			ValueClassType vcType = rType.getValueClass();
			vClass.setAbout(vcType.getAbout());
			vClass.setResource(vcType.getResource());
			vClass.getValue().addAll(getValueExpression(vcType.getValue()));
			restriction.setValueClass(vClass);
			if (rType.getComparator()!=null)restriction.setComparator(getComparator(rType.getComparator()));
			restriction.setCqlStr(rType.getCqlStr());
			restrictions.add(restriction);
		
			
		}
		
		return restrictions;
	}

	/**
	 * Converts from List<ValueConditionType>
	 *  to List<Restriction> 
	 * @param valueCondition
	 * @return
	 */
private List<Restriction> getValueCondition(
		List<ValueConditionType> valueCondition) {
	
	List<Restriction> restrictions = new ArrayList<Restriction>(valueCondition.size());
	Iterator iterator = valueCondition.iterator();
	Restriction restriction;
	while(iterator.hasNext()){
		ValueConditionType condition = (ValueConditionType)iterator.next();
		//get List<ValueExpressionType>
		List<ValueExprType> valueExpr = condition.getRestriction().getValue();
		restriction = new Restriction(null, getValueExpression(valueExpr));
		restriction.setComparator(getComparator(condition.getRestriction().getComparator()));
		//add CqlStr if exists
		if (condition.getSeq()!=null)restriction.setCqlStr(condition.getSeq().toString());
		//TODO add Property
		restrictions.add(restriction);
		
	}
	
	
	return restrictions;
}

	/**
	 * converts from the ComparatorEnumType to 
	 * ComparatorType
	 * @param comparator
	 * @return
	 */
    private ComparatorType getComparator(ComparatorEnumType comparator) {
		 
    	
    	if (comparator.equals(ComparatorEnumType.BETWEEN)) return ComparatorType.BETWEEN;
    	else if (comparator.equals(ComparatorEnumType.COLLECTION_CONTAINS)) return ComparatorType.COLLECTION_CONTAINS;
    	else if (comparator.equals(ComparatorEnumType.CONTAINS)) return ComparatorType.CONTAINS;
    	else if (comparator.equals(ComparatorEnumType.EMPTY)) return ComparatorType.EMPTY;
    	else if (comparator.equals(ComparatorEnumType.ENDS_WITH)) return ComparatorType.ENDS_WITH;
    	else if (comparator.equals(ComparatorEnumType.EQUAL)) return ComparatorType.EQUAL;
    	else if (comparator.equals(ComparatorEnumType.GREATER_THAN)) return ComparatorType.GREATER_THAN;
    	else if (comparator.equals(ComparatorEnumType.GREATER_THAN_OR_EQUAL)) return ComparatorType.GREATER_THAN_OR_EQUAL;
    	else if (comparator.equals(ComparatorEnumType.INCLUDES)) return ComparatorType.INCLUDES;
    	else if (comparator.equals(ComparatorEnumType.INCLUDES_STRICTLY)) return ComparatorType.INCLUDES_STRICTLY;
    	else if (comparator.equals(ComparatorEnumType.LESS_THAN)) return ComparatorType.LESS_THAN;
    	else if (comparator.equals(ComparatorEnumType.LESS_THAN_OR_EQUAL)) return ComparatorType.GREATER_THAN_OR_EQUAL;
    	else if (comparator.equals(ComparatorEnumType.MATCHES)) return ComparatorType.MATCHES;
    	else if (comparator.equals(ComparatorEnumType.NOT_EQUAL)) return ComparatorType.NOT_EQUAL;
    	else if (comparator.equals(ComparatorEnumType.ONE_OF)) return ComparatorType.ONE_OF;
    	else if (comparator.equals(ComparatorEnumType.STARTS_WITH)) return ComparatorType.STARTS_WITH;
		//TODO clear about otherwise-type 
    	return null;
	}

	/**
	 * Conversts from the list of <ValueExprType> 
	 * to the list of ValueExpression
	 * @param valueExpr
	 * @return
	 */
    private List<ValueExpression> getValueExpression(
			List<ValueExprType> valueExpr) {
		List<ValueExpression> omlExpressions = new ArrayList<ValueExpression>(valueExpr.size());
		ValueExpression omlExpr;
		Iterator iterator = valueExpr.iterator();
		while(iterator.hasNext()){
			ValueExprType jaxbExpr = (ValueExprType)iterator.next();
			omlExpr = new ValueExpression(jaxbExpr.getLiteral());
			omlExpr.setMax(jaxbExpr.getMax());
			omlExpr.setMin(jaxbExpr.getMin());
			//TODO implement set Apply 
			//omlExpr.setApply(getFunction(jaxbExpr.getApply()));
			omlExpressions.add(omlExpr);
			
		}
			
		return omlExpressions;
	}

	/**
	 * Converts from the ApplyType
	 * to the FunctionType
	 * @param apply
	 * @return
	 */
    private Function getFunction(ApplyType apply) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
