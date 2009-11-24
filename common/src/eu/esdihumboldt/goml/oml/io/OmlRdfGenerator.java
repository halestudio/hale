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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.namespace.QName;

import eu.esdihumboldt.cst.align.IAlignment;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.IEntity;
import eu.esdihumboldt.cst.align.IFormalism;
import eu.esdihumboldt.cst.align.ISchema;
import eu.esdihumboldt.cst.align.ICell.RelationType;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.align.ext.ITransformation;
import eu.esdihumboldt.cst.align.ext.IValueClass;
import eu.esdihumboldt.cst.align.ext.IValueExpression;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.generated.AlignmentType;
import eu.esdihumboldt.goml.generated.ApplyType;
import eu.esdihumboldt.goml.generated.CellType;
import eu.esdihumboldt.goml.generated.ClassConditionType;
import eu.esdihumboldt.goml.generated.ClassType;
import eu.esdihumboldt.goml.generated.ComparatorEnumType;
import eu.esdihumboldt.goml.generated.DomainRestrictionType;
import eu.esdihumboldt.goml.generated.Entity1;
import eu.esdihumboldt.goml.generated.Entity2;
import eu.esdihumboldt.goml.generated.EntityType;
import eu.esdihumboldt.goml.generated.FormalismType;
import eu.esdihumboldt.goml.generated.FunctionType;
import eu.esdihumboldt.goml.generated.Measure;
import eu.esdihumboldt.goml.generated.OnAttributeType;
import eu.esdihumboldt.goml.generated.OntologyType;
import eu.esdihumboldt.goml.generated.ParamType;
import eu.esdihumboldt.goml.generated.PropValueRestrictionType;
import eu.esdihumboldt.goml.generated.PropertyCollectionType;
import eu.esdihumboldt.goml.generated.PropertyCompositionType;
import eu.esdihumboldt.goml.generated.PropertyOperatorType;
import eu.esdihumboldt.goml.generated.PropertyType;
import eu.esdihumboldt.goml.generated.RangeRestrictionType;
import eu.esdihumboldt.goml.generated.RelationEnumType;
import eu.esdihumboldt.goml.generated.RestrictionType;
import eu.esdihumboldt.goml.generated.ValueClassType;
import eu.esdihumboldt.goml.generated.ValueConditionType;
import eu.esdihumboldt.goml.generated.ValueExprType;
import eu.esdihumboldt.goml.generated.AlignmentType.Map;
import eu.esdihumboldt.goml.generated.AlignmentType.Onto1;
import eu.esdihumboldt.goml.generated.AlignmentType.Onto2;
import eu.esdihumboldt.goml.generated.OntologyType.Formalism;
import eu.esdihumboldt.goml.generated.PropertyCollectionType.Item;
import eu.esdihumboldt.goml.oml.ext.Function;
import eu.esdihumboldt.goml.oml.ext.ValueClass;
import eu.esdihumboldt.goml.oml.ext.ValueExpression;
import eu.esdihumboldt.goml.omwg.ComparatorType;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.omwg.PropertyQualifier;
import eu.esdihumboldt.goml.omwg.Relation;
import eu.esdihumboldt.goml.omwg.Restriction;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.mediator.util.NamespacePrefixMapperImpl;

/**
 * This class implements methods for marshalling HUMBOLDT OML Objects to XML.
 * 
 * 
 * @author Anna Pitaev, Thorsten Reitz
 * @version $Id$
 */
public class OmlRdfGenerator {

	
	/**
	 * stack for property invocation
	 * value = 0, property parent element is cell
	 * value = 1, property parent element is ComposedProperty
	 */
	
	private int propertyStack = 0;
	/**
	 * Constant defines the path to the alignment jaxb context
	 */
	private static final String ALIGNMENT_CONTEXT = "eu.esdihumboldt.goml.generated";

	/**
	 * Stores alignment to xml
	 * 
	 * @param alignment
	 *            , to be stored
	 * @param xmlPath
	 *            , path to the xml-file
	 * @throws JAXBException
	 */
	public void write(IAlignment alignment, String xmlPath)
			throws JAXBException {
		// 1. convert OML Alignment to the jaxb generated AlignmentType
		AlignmentType aType = getAlignment(alignment);
		// 2. marshall AlignmentType to xml
		JAXBContext jc = JAXBContext.newInstance(ALIGNMENT_CONTEXT);
		Marshaller m = jc.createMarshaller();
		/*
		 * marshaller.marshal( new JAXBElement( new
		 * QName("","rootTag"),Point.class,new Point(...)));
		 * 
		 */
		try {
			m.setProperty("com.sun.xml.bind.namespacePrefixMapper",
					new NamespacePrefixMapperImpl());
		} catch (PropertyException e) {
			// if the JAXB provider doesn't recognize the prefix mapper,
			// it will throw this exception. Since being unable to specify
			// a human friendly prefix is not really a fatal problem,
			// you can just continue marshalling without failing
			;
		}

		// make the output indented. It looks nicer on screen.
		// this is a JAXB standard property, so it should work with any JAXB
		// impl.
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		m.marshal(new JAXBElement(new QName(null, "Alignment", "align"),
				AlignmentType.class, aType), new File(xmlPath));
	}

	/**
	 * Converts from HUMBOLDT alignment to the JAXB generated alignment type.
	 * 
	 * @param alignment
	 *            , HUMBOLDT OML alignment
	 * @return alignmentType
	 */
	private AlignmentType getAlignment(IAlignment alignment) {
		AlignmentType aType = new AlignmentType();
		// 1. set about,level, ontology1,2
		if (alignment!=null){
		if (alignment.getAbout()!=null) aType.setAbout(((About) alignment.getAbout()).getAbout());
		aType.setLevel(alignment.getLevel());
		aType.setOnto1(getOnto1(alignment.getSchema1()));
		aType.setOnto2(getOnto2(alignment.getSchema2()));
		// 2. add map of cells
		aType.getMap().addAll(getMaps(alignment.getMap()));
        //3. add valueClass
		aType.getValueClass().addAll(getValueClasses(alignment.getValueClasses()));
		}
		
		return aType;
	}

	/**
	 * Converts from list of the OML Classes
	 * to the list of the JAXB generated classes
	 * @param valueClasses
	 * @return
	 */
	private List<ValueClassType> getValueClasses(
			List<IValueClass> valueClasses) {
		
		List<ValueClassType> vcTypes = new ArrayList<ValueClassType>();
		if (valueClasses!=null){
		ValueClassType vcType = new ValueClassType();
		IValueClass vClass;
		Iterator<IValueClass> iterator = valueClasses.iterator();
		while(iterator.hasNext()){
			vClass = (IValueClass)iterator.next();
			vcType.setAbout(vClass.getAbout());
			vcType.setResource(vClass.getResource());
			vcType.getValue().addAll(getValueExpressionTypes(vClass.getValue()));
			vcTypes.add(vcType);
		}
		}
		return vcTypes;
	}

	/**
	 * Converts from HUMBOLDT ISchema to Onto1.
	 * 
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
	 * 
	 * @param schema
	 * @return onto2
	 */
	private Onto2 getOnto2(ISchema schema2) {
		Onto2 onto2 = new Onto2();
		onto2.setOntology(getOntologyType(schema2));
		return onto2;
	}

	/**
	 * Converts from HUMBOLDT ISchema to the Jaxb generated OntologyType
	 * 
	 * @param schema
	 * @return ontologyType
	 */
	private OntologyType getOntologyType(ISchema schema) {
		OntologyType oType = new OntologyType();
		if (schema != null) {
			About about = (About) schema.getAbout();
			if (about != null)
				oType.setAbout(about.getAbout());
			oType.setLocation(schema.getLocation());
			oType.setFormalism(getFormalism(schema.getFormalism()));
			oType.getLabel().addAll(schema.getLabels());
		}
		return oType;
	}

	/**
	 * Converts from HUMBOLDT IFormalism to the JAXB generated Formalism
	 * 
	 * @param formalism
	 * @return
	 */
	private Formalism getFormalism(IFormalism formalism) {
		Formalism jFormalism = new Formalism();
		jFormalism.setFormalism(getFormalismType(formalism));
		return jFormalism;
	}

	/**
	 * Converts from HUMBOLDT IFormalism to the JAXB generated FormalismType
	 * 
	 * @param formalism
	 * @return
	 */
	private FormalismType getFormalismType(IFormalism formalism) {
		FormalismType fType = new FormalismType();
		if (formalism != null) {
			fType.setName(formalism.getName());
			fType.setUri(formalism.getLocation().toString());
		}
		return fType;
	}

	/**
	 * Converts from List of ICell to the List of Map
	 * 
	 * @param map
	 * @return
	 */
	private Collection<? extends Map> getMaps(List<ICell> map) {
		ArrayList<Map> maps = new ArrayList<Map>(map.size());
		Iterator iterator = map.iterator();
		Map jMap;
		ICell cell;

		while (iterator.hasNext()) {
			cell = (ICell) iterator.next();
			jMap = new Map();
			jMap.setCell(getCellType(cell));
			maps.add(jMap);

		}
		return maps;
	}

	/**
	 * Converts from HUMBOLDT ICell to the JAXB CellType
	 * 
	 * @param cell
	 * @return
	 */
	private CellType getCellType(ICell cell) {
		CellType cType = new CellType();
		if (cell != null) {
			About about = (About) cell.getAbout();
			if (about != null)
				cType.setAbout(about.getAbout());
			//keep Measure optional
			cType.setMeasure(new Float(cell.getMeasure()));
			cType.setRelation(getRelation(cell.getRelation()));
			cType.setEntity1(getEntity1(cell.getEntity1()));
			cType.setEntity2(getEntity2(cell.getEntity2()));
			if (cell.getLabel() != null) {
				cType.getLabel().addAll(((Cell)cell).getLabel());
			}
		}
		return cType;
	}

	/**
	 * Converts from double to the Jaxb generated Measure
	 * 
	 * @param measure
	 * @return
	 */
	/*private Float getMeasure(double measure) {
		//TODO changed structure of the Measure element in the schema
		
		Measure jMeasure = new Measure();
		jMeasure.setDatatype("xsd:float");
		jMeasure.setValue(new Double(measure).floatValue());
		return jMeasure;
	}
*/
	/**
	 * converts from RelationType to RelationEnumType
	 * 
	 * @param relation
	 * @return
	 */
	private RelationEnumType getRelation(RelationType relation) {
		if (relation != null) {
			if (relation.equals(RelationType.Disjoint)) {
				return RelationEnumType.DISJOINT;
			} else if (relation.equals(RelationType.Equivalence)) {
				return RelationEnumType.EQUIVALENCE;
			} else if (relation.equals(RelationType.Extra)) {
				return RelationEnumType.EXTRA;
			} else if (relation.equals(RelationType.HasInstance)) {
				return RelationEnumType.HAS_INSTANCE;
			} else if (relation.equals(RelationType.InstanceOf)) {
				return RelationEnumType.INSTANCE_OF;
			} else if (relation.equals(RelationType.Missing)) {
				return RelationEnumType.MISSING;
			} else if (relation.equals(RelationType.PartOf)) {
				return RelationEnumType.PART_OF;
			} else if (relation.equals(RelationType.SubsumedBy)) {
				return RelationEnumType.SUBSUMED_BY;
			} else if (relation.equals(RelationType.Subsumes)) {
				return RelationEnumType.SUBSUMES;
			}
		}
		return null;

	}

	/**
	 * converts from IEntity to the JAXB Entity2
	 * 
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
	 * 
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
	 * 
	 * @param entity
	 * @return
	 */
	private JAXBElement<? extends EntityType> getEntityType(IEntity entity) {
		JAXBElement<? extends EntityType> eType = null;
		if (entity != null) {
			if (entity instanceof Property) {
				// instantiate as PropertyType
				Property property = (Property) entity;
				PropertyType pType = getPropertyType(property);
				eType = new JAXBElement<PropertyType>(new QName("http://www.omwg.org/TR/d7/ontology/alignment","Property"),
						PropertyType.class, pType);

			} else if (entity instanceof FeatureClass) {
				// instantiate as ClassType
				FeatureClass feature = (FeatureClass) entity;
				ClassType cType = getClassType(feature);

				eType = new JAXBElement<ClassType>(new QName("http://www.omwg.org/TR/d7/ontology/alignment", "Class"),
						ClassType.class, cType);
			} else if (entity instanceof Relation) {
				// instantiate as RelationType
				// TODO add implementation, for the next release
			} else if (entity instanceof PropertyQualifier) {
				// instantiate as PropertyQualifierType
				// TODO add implementation, will get the examples from MDV
			}
		}

		return eType;
	}

	/**
	 * Converts from OML FeatureClass to the JAXB ClassType
	 * 
	 * @param feature
	 * @return
	 */
	private ClassType getClassType(FeatureClass feature) {
		ClassType cType = new ClassType();
		if (feature != null) {
			About about = ((About) feature.getAbout());
			if (feature.getLabel() != null) {
				cType.getLabel().addAll(feature.getLabel());
			}
			if (about != null) {
				cType.setAbout(about.getAbout());
			}
			cType.setTransf(getTransf(feature.getTransformation()));
			if (feature.getAttributeTypeCondition() != null) {
				cType.getAttributeTypeCondition().addAll(
					getConditions(feature.getAttributeTypeCondition()));
			}
			if (feature.getAttributeValueCondition() != null) {
				cType.getAttributeValueCondition().addAll(
					getConditions(feature.getAttributeValueCondition()));
			}
			if (feature.getAttributeOccurenceCondition() != null) {
				cType.getAttributeOccurenceCondition().addAll(
					getConditions(feature.getAttributeOccurenceCondition()));
			}
		}
		return cType;
	}

	/**
	 * Converts from List of OML Restrictions to the List of the Jaxb
	 * ClassConditionType
	 * 
	 * @param attributeTypeCondition
	 * @return
	 */
	private Collection<? extends ClassConditionType> getConditions(
			List<Restriction> restrictions) {
		if (restrictions != null) {
			ArrayList<ClassConditionType> conditions = new ArrayList<ClassConditionType>(
					restrictions.size());
			ClassConditionType condition;
			Restriction restriction;
			Iterator iterator = restrictions.iterator();
			while (iterator.hasNext()) {
				restriction = (Restriction) iterator.next();
				condition = new ClassConditionType();
				condition.setRestriction(getRestrictionType(restriction));
				conditions.add(condition);
			}
			return conditions;
		}
		return new ArrayList<ClassConditionType>();
	}

	/**
	 * Converts from HUMBOLDT OML Restriction to the JAXB RestrictionType
	 * 
	 * @param restriction
	 * @return
	 */
	private RestrictionType getRestrictionType(Restriction restriction) {
		RestrictionType rType = new RestrictionType();
		if (restriction != null) {
			rType.setComparator(getComparator(restriction.getComparator()));
			rType.setCqlStr(restriction.getCqlStr());
            //TODO: clear with MdV 
			//			rType.setOnAttribute(getOnAttributeType(restriction
			//					.getOnAttribute()));

			// if list of value expressions for this restriction is empty
			// use ValueClass
			List<IValueExpression> values = restriction.getValue();
			if (values != null && values.size() > 0) {
				rType.setValueClass(getValueClass(values));
			} else {
				rType.setValueClass(getValueClass(restriction.getValueClass()));
			}
		}
		return rType;
	}

	/**
	 * Converts from the OML ValueClass to the Jaxb ValueClassType
	 * @param valueClass
	 * @return
	 */
	private ValueClassType getValueClass(ValueClass valueClass) {
		ValueClassType vcType = new ValueClassType();
		if (valueClass != null) {
			vcType.setAbout(valueClass.getAbout());
			vcType.setResource(valueClass.getResource());
			if (getJAXBValueExpressions(valueClass.getValue()) != null) {
				vcType.getValue().addAll(getJAXBValueExpressions(valueClass.getValue()));
			}
		}
		return vcType;
	}

	private Collection<? extends ValueExprType> getJAXBValueExpressions(
			List<IValueExpression> value) {
		List<ValueExprType> vExpressions = new ArrayList<ValueExprType>(value.size());
		Iterator iterator = value.iterator();
		ValueExprType veType;
		while(iterator.hasNext()){
			ValueExpression ve = (ValueExpression)iterator.next();
			veType = new ValueExprType();
			veType.setLiteral(ve.getLiteral());
			veType.setMax(ve.getMax());
			veType.setMin(ve.getMin());
			veType.setApply(getApplayType(ve.getApply()));
			vExpressions.add(veType);
		}
		return vExpressions;
	}

	/**
	 * Converts from List of HUMBOLDT OML ValueExpression To the JAXB generated
	 * ValueClassType
	 * 
	 * @param value
	 * @return
	 */
	private ValueClassType getValueClass(List<IValueExpression> value) {
		ValueClassType vcType = new ValueClassType();
		if (getJAXBValueExpressions(value) != null) {
			vcType.getValue().addAll(getJAXBValueExpressions(value));
		}
		return vcType;
	}

	/**
	 * Converts from OML Property to OnAttributeType
	 * 
	 * @param onAttribute
	 * @return
	 */
	private OnAttributeType getOnAttributeType(Property onAttribute) {
		// uses property as onAttribute until we have the implementation for the realation 
		//TODO clear the need of the about attribute fot the onAttribute-element 
		OnAttributeType oaType = new OnAttributeType();
		oaType.setProperty(getPropertyType(onAttribute));
		return oaType;
	}

	/**
	 * converts from the ComparatorType to ComparatorEnumType
	 * 
	 * @param comparator
	 * @return
	 */
	private ComparatorEnumType getComparator(ComparatorType comparator) {
		if (comparator != null) {
			if (comparator.equals(ComparatorType.BETWEEN))
				return ComparatorEnumType.BETWEEN;
			else if (comparator.equals(ComparatorType.COLLECTION_CONTAINS))
				return ComparatorEnumType.COLLECTION_CONTAINS;
			else if (comparator.equals(ComparatorType.CONTAINS))
				return ComparatorEnumType.CONTAINS;
			else if (comparator.equals(ComparatorType.EMPTY))
				return ComparatorEnumType.EMPTY;
			else if (comparator.equals(ComparatorType.ENDS_WITH))
				return ComparatorEnumType.ENDS_WITH;
			else if (comparator.equals(ComparatorType.EQUAL))
				return ComparatorEnumType.EQUAL;
			else if (comparator.equals(ComparatorType.GREATER_THAN))
				return ComparatorEnumType.GREATER_THAN;
			else if (comparator.equals(ComparatorType.GREATER_THAN_OR_EQUAL))
				return ComparatorEnumType.GREATER_THAN_OR_EQUAL;
			else if (comparator.equals(ComparatorType.INCLUDES))
				return ComparatorEnumType.INCLUDES;
			else if (comparator.equals(ComparatorType.INCLUDES_STRICTLY))
				return ComparatorEnumType.INCLUDES_STRICTLY;
			else if (comparator.equals(ComparatorType.LESS_THAN))
				return ComparatorEnumType.LESS_THAN;
			else if (comparator.equals(ComparatorType.GREATER_THAN_OR_EQUAL))
				return ComparatorEnumType.GREATER_THAN_OR_EQUAL;
			else if (comparator.equals(ComparatorType.MATCHES))
				return ComparatorEnumType.MATCHES;
			else if (comparator.equals(ComparatorType.NOT_EQUAL))
				return ComparatorEnumType.NOT_EQUAL;
			else if (comparator.equals(ComparatorType.ONE_OF))
				return ComparatorEnumType.ONE_OF;
			else if (comparator.equals(ComparatorType.STARTS_WITH))
				return ComparatorEnumType.STARTS_WITH;
			
			else  if (comparator.equals(ComparatorType.OTHERWISE))return ComparatorEnumType.OTHERWISE;
		}
		
		return null;
	}

	/**
	 * Converts from OML ITransformation to the JAXB generated FunctionType
	 * 
	 * @param transformation
	 * @return
	 */
	private FunctionType getTransf(ITransformation transformation) {
		FunctionType fType = new FunctionType();
		if (transformation != null) {
			
			if(transformation.getService()!=null) fType.setResource(transformation.getService().getLocation());
			/*//Uli will provide us with examples
			fType.setResource(transformation.getLabel());*/
			if (transformation.getParameters() != null) {
				fType.getParam().addAll(
					getParameters(transformation.getParameters()));
			}
		}
		return fType;
	}

	/**
	 * Converts from List of OML IParameter to the collecion of the JAXB
	 * ParameterType
	 * 
	 * @param parameters
	 * @return
	 */
	private Collection<? extends ParamType> getParameters(
			List<IParameter> parameters) {
		if (parameters != null) {
			ArrayList<ParamType> pTypes = new ArrayList<ParamType>(
					parameters.size());
			ParamType pType;
			IParameter param;
			Iterator iterator = parameters.iterator();
			while (iterator.hasNext()) {
				param = (IParameter) iterator.next();
				pType = getParameterType(param);
				pTypes.add(pType);
			}
			return pTypes;
		}
		return new ArrayList<ParamType>();
	}

	/**
	 * Converts from OML IParameter to the JAXB generated ParamType
	 * 
	 * @param param
	 * @return
	 */
	private ParamType getParameterType(IParameter param) {
		ParamType pType = new ParamType();
		if (param != null) {
			pType.setName(param.getName());
			pType.getValue().add(param.getValue());
		}
		return pType;
	}

	/**
	 * Converts from OML Property to the JAXB PropertyType
	 * 
	 * @param property
	 * @return
	 */
	private PropertyType getPropertyType(Property property) {
		PropertyType pType = new PropertyType();
		if (property != null) {
			About about = (About) property.getAbout();
			if (about != null)
				pType.setAbout(about.getAbout());
			if(property instanceof ComposedProperty&& this.propertyStack == 0){
				this.propertyStack ++;
			//incase it  is a composed property add the property composition elmenet
			// TODO keep the property comsposition as optional element
			// property composition is used to define the merge on the attributes 
			PropertyCompositionType propCompType = new PropertyCompositionType();
			//set Relation
		    propCompType.setRelation(getRelation(((ComposedProperty)property).getRelation()));
		    //set  property collection
		    propCompType.setCollection(getPropertyCollection(((ComposedProperty)property).getCollection()));
		    //set PropertyOperatorType
		    propCompType.setOperator(getOperatorType(((ComposedProperty)property).getPropertyOperatorType()));
			pType.setPropertyComposition(propCompType);
			}
			pType.setTransf(getTransf(property.getTransformation()));
			if (property.getDomainRestriction() != null) {
				pType.getDomainRestriction().addAll(
					getDomainRestrictionTypes(property.getDomainRestriction()));
			}
			if (property.getTypeCondition() != null) {
				pType.getTypeCondition().addAll(property.getTypeCondition());
			}
			if (property.getLabel() != null) {
				pType.getLabel().addAll(property.getLabel());
			}
			if (property.getValueCondition() != null) {
				pType.getValueCondition().addAll(
					getValueConditions(property.getValueCondition()));
			}
		}
		return pType;
	}



	/**
	 * Converts propertyOperator instance 
	 * from the OML enum
	 * to the JAXB-based enum
	 * 
	 * @param propertyOperatorType
	 * @return
	 */
	private PropertyOperatorType getOperatorType(
			eu.esdihumboldt.goml.omwg.ComposedProperty.PropertyOperatorType propertyOperatorType) {
	    if(propertyOperatorType!= null){
	    	//TODO clear mapping
	    	if (propertyOperatorType.equals(eu.esdihumboldt.goml.omwg.ComposedProperty.PropertyOperatorType.AND)) return PropertyOperatorType.INTERSECTION;
	    	if((propertyOperatorType.equals(eu.esdihumboldt.goml.omwg.ComposedProperty.PropertyOperatorType.OR))) return PropertyOperatorType.UNION;
	    	if((propertyOperatorType.equals(eu.esdihumboldt.goml.omwg.ComposedProperty.PropertyOperatorType.FIRST))) return PropertyOperatorType.FIRST;
	    	if((propertyOperatorType.equals(eu.esdihumboldt.goml.omwg.ComposedProperty.PropertyOperatorType.NEXT))) return PropertyOperatorType.NEXT;
	    }
		return null;
	}

	/**
	 * Translate the list of the OML Properties
	 * To the Jaxb-based PropertyCollectionType
	 * @param collection
	 * @return
	 */
	private PropertyCollectionType getPropertyCollection(
			List<Property> collection) {
	PropertyCollectionType propCollectionType = new PropertyCollectionType();
	if(collection!=null){
		Iterator iterator = collection.iterator();
		while(iterator.hasNext()){
			//get property from a list
			Property property = (Property)iterator.next();
			//convert property to the property type
			//TODO could be the circular dependencies in case of ComposedProperty
			PropertyType pType = getPropertyType(property);
			//add to the collection
			Item item = new Item();
			item.setProperty(pType);
			propCollectionType.getItem().add(item);
		}
			
	}
		return propCollectionType;
	}

	/**
	 * Translate the list of the OML Relation
	 * To the Jaxb-based RelationType
	 * @param relation
	 * @return
	 */
	private eu.esdihumboldt.goml.generated.RelationType getRelation(
			Relation relation) {
		eu.esdihumboldt.goml.generated.RelationType relType = new eu.esdihumboldt.goml.generated.RelationType();
		if (relation!=null){
			//TODO clear with MdV
			if (relation.getDomainRestriction()!=null)relType.setDomainRestriction(getDomainRestrictionType(relation.getDomainRestriction().get(0)));
			//TODO clear with MdV
			relType.setPipe(null);
			if (relation.getAbout()!=null) relType.setAbout(relation.getAbout().getAbout());
			//TODO clear with MdV
			relType.setRelationComposition(null);
			if (relation.getRangeRestriction()!=null)relType.setRangeRestriction(getRangeRestrictionType(relation.getRangeRestriction().get(0)));
			if (relation.getTransformation()!=null)relType.setTransf(getTransf(relation.getTransformation()));
			
		}
			
		return relType;
	}

	/**
	 * Translates from OML FeatureClass
	 * To the schema based FeatureClass
	 * @param featureClass
	 * @return
	 */
	private RangeRestrictionType getRangeRestrictionType(
			FeatureClass featureClass) {
		RangeRestrictionType rangeRestriction = new RangeRestrictionType();
		rangeRestriction.setClazz(getClassType(featureClass));
		return rangeRestriction;
	}

	/**
	 * Converts from the list of restrictions to the collection of the
	 * ValueConditionType
	 * 
	 * @param restrictions
	 * @return
	 */

	private Collection<? extends ValueConditionType> getValueConditions(
			List<Restriction> restrictions) {
		if (restrictions != null) {
			ArrayList<ValueConditionType> vcTypes = new ArrayList<ValueConditionType>(
					restrictions.size());
			ValueConditionType vcType;
			Restriction restriction;
			Iterator iterator = restrictions.iterator();
			while (iterator.hasNext()) {
				restriction = (Restriction) iterator.next();
				vcType = getValueConditionType(restriction);
				vcTypes.add(vcType);
			}
			return vcTypes;
		}
		return new ArrayList<ValueConditionType>();
	}

	/**
	 * converts from OML Restriction to the JAXB ValueConditionType
	 * 
	 * @param restriction
	 * @return
	 */
	private ValueConditionType getValueConditionType(Restriction restriction) {
		ValueConditionType vcType = new ValueConditionType();
		vcType.setRestriction(getRestrictionType(restriction));
		vcType.setSeq(restriction.getSeq());
		return vcType;
	}

	/**
	 * Converts from OML Restriction to the JAXB PropertyValueRestrictionType
	 * 
	 * @param restriction
	 * @return
	 */
	private PropValueRestrictionType getPropertyValueRestrictionType(
			Restriction restriction) {
		PropValueRestrictionType pvrType = new PropValueRestrictionType();
		if (restriction != null) {
			pvrType.setComparator(getComparator(restriction.getComparator()));
			if (restriction.getValue() != null) {
				pvrType.getValue().addAll(
					getValueExpressionTypes(restriction.getValue()));
			}
		}
		return pvrType;
	}

	/**
	 * Converts from List of ValueExpression to the Collection of ValueExprType
	 * 
	 * @param value
	 * @return
	 */
	private Collection<? extends ValueExprType> getValueExpressionTypes(
			List<IValueExpression> values) {
		if (values != null) {
			ArrayList<ValueExprType> veTypes = new ArrayList<ValueExprType>(values
					.size());
			ValueExprType veType;
			ValueExpression expression;
			Iterator iterator = values.iterator();
			while (iterator.hasNext()) {
				expression = (ValueExpression) iterator.next();
				veType = getValueExprType(expression);
				veTypes.add(veType);
			}
			return veTypes;
		}
		return new ArrayList<ValueExprType>();
	}

	/**
	 * Conversts from ValueExpression to the JAXB generated ValueExprType
	 * 
	 * @param expression
	 * @return
	 */
	private ValueExprType getValueExprType(ValueExpression expression) {
		ValueExprType veType = new ValueExprType();
		if (expression != null) {
			veType.setApply(getApplayType(expression.getApply()));
			veType.setLiteral(expression.getLiteral());
			veType.setMax(expression.getMax());
			veType.setMin(expression.getMin());
		}
		return veType;
	}

	/**
	 * converts from OML Function to the JAXB generated ApplyType
	 * 
	 * @param function
	 * @return
	 */
	private ApplyType getApplayType(Function function) {
		ApplyType aType = new ApplyType();
		// TODO implement it for the next release, in case we have some examples
		// aType.setOperation(function.getService().toString());
		aType.setValue(null);

		return aType;
	}

	/**
	 * converts from the list of the FeatureClass to the collection of the
	 * FeatureClass
	 * 
	 * @param domainRestriction
	 * @return
	 */
	private Collection<? extends DomainRestrictionType> getDomainRestrictionTypes(
			List<FeatureClass> features) {
		if (features != null) {
			ArrayList<DomainRestrictionType> drTypes = new ArrayList<DomainRestrictionType>(
					features.size());
			DomainRestrictionType drType;
			FeatureClass feature;
			Iterator iterator = features.iterator();
			while (iterator.hasNext()) {
				feature = (FeatureClass) iterator.next();
				drType = getDomainRestrictionType(feature);
				drTypes.add(drType);
			}
			return drTypes;
		}
		return new ArrayList<DomainRestrictionType>();
	}

	/**
	 * Converts from OML FeatureClass to the JAXB generated
	 * DomainRestrictionType
	 * 
	 * @param feature
	 * @return
	 */
	private DomainRestrictionType getDomainRestrictionType(FeatureClass feature) {
		DomainRestrictionType drType = new DomainRestrictionType();
		//set one  property in case of the PropertyQualifier only
		drType.setClazz(getClassType(feature));
		return drType;
	}

}
