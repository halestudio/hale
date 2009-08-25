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
import javax.xml.namespace.QName;

import eu.esdihumboldt.cst.align.IAlignment;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.IEntity;
import eu.esdihumboldt.cst.align.IFormalism;
import eu.esdihumboldt.cst.align.ISchema;
import eu.esdihumboldt.cst.align.ICell.RelationType;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.align.ext.ITransformation;
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
import eu.esdihumboldt.goml.generated.PropertyType;
import eu.esdihumboldt.goml.generated.RelationEnumType;
import eu.esdihumboldt.goml.generated.RestrictionType;
import eu.esdihumboldt.goml.generated.ValueClassType;
import eu.esdihumboldt.goml.generated.ValueConditionType;
import eu.esdihumboldt.goml.generated.ValueExprType;
import eu.esdihumboldt.goml.generated.AlignmentType.Map;
import eu.esdihumboldt.goml.generated.AlignmentType.Onto1;
import eu.esdihumboldt.goml.generated.AlignmentType.Onto2;
import eu.esdihumboldt.goml.generated.OntologyType.Formalism;
import eu.esdihumboldt.goml.oml.ext.Function;
import eu.esdihumboldt.goml.oml.ext.ValueExpression;
import eu.esdihumboldt.goml.omwg.ComparatorType;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.omwg.PropertyQualifier;
import eu.esdihumboldt.goml.omwg.Relation;
import eu.esdihumboldt.goml.omwg.Restriction;
import eu.esdihumboldt.goml.rdf.About;

/**
 * This class implements methods for marshalling HUMBOLDT OML Objects to XML.
 * 
 * 
 * @author Anna Pitaev, Thorsten Reitz
 * @version $Id$
 */
public class OmlRdfGenerator {

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
		 */

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
		aType.setAbout(((About) alignment.getAbout()).getAbout());
		aType.setLevel(alignment.getLevel());
		aType.setOnto1(getOnto1(alignment.getSchema1()));
		aType.setOnto2(getOnto2(alignment.getSchema2()));
		// 2. add map of cells
		aType.getMap().addAll(getMaps(alignment.getMap()));

		return aType;
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
		About about = (About) schema.getAbout();
		if (about != null)
			oType.setAbout(about.getAbout());
		oType.setLocation(schema.getLocation());
		oType.setFormalism(getFormalism(schema.getFormalism()));
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
			// TODO: clear about elemenet for each map
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
			cType.setMeasure(getMeasure(cell.getMeasure()));
			cType.setRelation(getRelation(cell.getRelation()));
			cType.setEntity1(getEntity1(cell.getEntity1()));
			cType.setEntity2(getEntity2(cell.getEntity2()));
			cType.getLabel().addAll(((Cell)cell).getLabel());
		}
		return cType;
	}

	/**
	 * Converts from double to the Jaxb generated Measure
	 * 
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
				eType = new JAXBElement<PropertyType>(new QName("Property"),
						PropertyType.class, pType);

			} else if (entity instanceof FeatureClass) {
				// instantiate as ClassType
				FeatureClass feature = (FeatureClass) entity;
				ClassType cType = getClassType(feature);

				eType = new JAXBElement<ClassType>(new QName("Class"),
						ClassType.class, cType);
			} else if (entity instanceof Relation) {
				// instantiate as RelationType
				// TODO add implementation
			} else if (entity instanceof PropertyQualifier) {
				// instantiate as PropertyQualifierType
				// TODO add implementation
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
			if (about != null)
				cType.setAbout(about.getAbout());
			cType.setTransf(getTransf(feature.getTransformation()));
			cType.getAttributeTypeCondition().addAll(
					getConditions(feature.getAttributeTypeCondition()));
			cType.getAttributeValueCondition().addAll(
					getConditions(feature.getAttributeValueCondition()));
			cType.getAttributeOccurenceCondition().addAll(
					getConditions(feature.getAttributeOccurenceCondition()));
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
			rType.setOnAttribute(getOnAttributeType(restriction
					.getOnAttribute()));
			rType.setValueClass(getValueClass(restriction.getValue()));
		}
		return rType;
	}

	/**
	 * Converts from List of HUMBOLDT OML ValueExpression To the JAXB generated
	 * ValueClassType
	 * 
	 * @param value
	 * @return
	 */
	private ValueClassType getValueClass(List<ValueExpression> value) {
		ValueClassType vcType = new ValueClassType();
		// TODO add implementation after discussion with MdV
		return vcType;
	}

	/**
	 * Converts from OML Property to OnAttributeType
	 * 
	 * @param onAttribute
	 * @return
	 */
	private OnAttributeType getOnAttributeType(Property onAttribute) {
		// TODO discuss need of the relation, about fields
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
		}
		// TODO clear about otherwise-type
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
			// TODO check the resource transformation
			// fType.setResource(transformation.getService().toString());
			fType.getParam().addAll(
					getParameters(transformation.getParameters()));
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
			// TODO clear property pipe
			pType.setPipe(null);
			// TODO clear property composition
			pType.setPropertyComposition(null);
			pType.setTransf(getTransf(property.getTransformation()));
			pType.getDomainRestriction().addAll(
					getDomainRestrictionTypes(property.getDomainRestriction()));
			pType.getTypeCondition().addAll(property.getTypeCondition());
			pType.getLabel().addAll(property.getLabel());
			pType.getValueCondition().addAll(
					getValueConditions(property.getValueCondition()));
		}
		return pType;
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
		vcType.setRestriction(getPropertyValueRestrictionType(restriction));
		// TODO : clear seqstr should be of type BigInteger
		// vcType.setSeq(restriction.getCqlStr());
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
			pvrType.getValue().addAll(
					getValueExpressionTypes(restriction.getValue()));
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
			List<ValueExpression> values) {
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
		// TODO clear it with marian
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
		// TODO clear with Marian property field
		drType.setProperty(null);
		drType.setClazz(getClassType(feature));
		return drType;
	}

}
