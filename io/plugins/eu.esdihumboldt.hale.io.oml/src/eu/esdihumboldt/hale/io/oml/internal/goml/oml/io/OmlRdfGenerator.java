/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.oml.internal.goml.oml.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.io.oml.internal.goml.align.Cell;
import eu.esdihumboldt.hale.io.oml.internal.goml.oml.ext.Function;
import eu.esdihumboldt.hale.io.oml.internal.goml.oml.ext.ValueClass;
import eu.esdihumboldt.hale.io.oml.internal.goml.oml.ext.ValueExpression;
import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.ComparatorType;
import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.ComposedProperty;
import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.FeatureClass;
import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.Property;
import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.PropertyQualifier;
import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.Relation;
import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.Restriction;
import eu.esdihumboldt.hale.io.oml.internal.model.align.IAlignment;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ICell;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ICell.RelationType;
import eu.esdihumboldt.hale.io.oml.internal.model.align.IEntity;
import eu.esdihumboldt.hale.io.oml.internal.model.align.IFormalism;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ISchema;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.IParameter;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.ITransformation;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.IValueClass;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.IValueExpression;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.AlignmentType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.AlignmentType.Map;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.AlignmentType.Onto1;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.AlignmentType.Onto2;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.ApplyType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.CellType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.ClassConditionType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.ClassType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.ComparatorEnumType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.DomainRestrictionType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.Entity1;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.Entity2;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.EntityType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.FormalismType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.FunctionType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.ObjectFactory;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.OntologyType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.OntologyType.Formalism;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.ParamType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.PropertyCollectionType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.PropertyCollectionType.Item;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.PropertyCompositionType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.PropertyOperatorType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.PropertyType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.RangeRestrictionType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.RelationEnumType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.RestrictionType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.ValueClassType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.ValueConditionType;
import eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.ValueExprType;
import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IAbout;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

/**
 * This class implements methods for marshalling HUMBOLDT OML Objects to XML.
 * 
 * @author Anna Pitaev, Thorsten Reitz
 */
@SuppressWarnings("javadoc")
public class OmlRdfGenerator {

	/**
	 * property stack size parameter name
	 */
	public static final String PROPERTY_STACK_SIZE = "composedPropertyStackSize";

	/**
	 * stack for property invocation value = 0, property parent element is cell
	 * value >0 and value < @link{propertyStackSize}, property parent element is
	 * ComposedProperty
	 */
	// private int propertyStack;

	/**
	 * max size of the property stack
	 */

	// private static final int propertyStackSize = new
	// Integer(ConfigurationManager.getComponentProperty(PROPERTY_STACK_SIZE)).intValue();

	/**
	 * Constant defines the path to the alignment jaxb context
	 */
	private static final String ALIGNMENT_CONTEXT = "eu.esdihumboldt.hale.io.oml.internal.model.generated.oml";

	/**
	 * Stores alignment to xml
	 * 
	 * @param alignment , to be stored
	 * @param xmlPath , path to the xml-file
	 * @throws JAXBException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void write(IAlignment alignment, String xmlPath) throws JAXBException {
		// 1. convert OML Alignment to the jaxb generated AlignmentType
		AlignmentType aType = getAlignment(alignment);
		// 2. marshall AlignmentType to xml
		JAXBContext jc = JAXBContext.newInstance(ALIGNMENT_CONTEXT,
				ObjectFactory.class.getClassLoader());
		Marshaller m = jc.createMarshaller();

		configurePrefixMapper(m);

		// make the output indented. It looks nicer on screen.
		// this is a JAXB standard property, so it should work with any JAXB
		// impl.
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
				"http://knowledgeweb.semanticweb.org/heterogeneity/alignment align.xsd");

		m.marshal(
				new JAXBElement(
						new QName("http://knowledgeweb.semanticweb.org/heterogeneity/alignment",
								"Alignment", "align"),
						AlignmentType.class, aType),
				new File(xmlPath));
		/*
		 * try { URLConnection connection = new URL("file", null,
		 * xmlPath).openConnection(); connection.setDoOutput(true);
		 * 
		 * m.marshal(new JAXBElement(new
		 * QName("http://knowledgeweb.semanticweb.org/heterogeneity/alignment",
		 * "Alignment", "align"), AlignmentType.class, aType),
		 * connection.getOutputStream()); } catch (MalformedURLException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
	}

	/**
	 * Override this method to configure a prefix mapper.
	 * 
	 * @param m the marshaller
	 */
	protected void configurePrefixMapper(Marshaller m) {
//		try {
//			m.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapperImpl());
//
//		} catch (PropertyException e) {
//			// if the JAXB provider doesn't recognize the prefix mapper,
//			// it will throw this exception. Since being unable to specify
//			// a human friendly prefix is not really a fatal problem,
//			// you can just continue marshalling without failing
//		}
	}

	/**
	 * Converts from HUMBOLDT alignment to the JAXB generated alignment type.
	 * 
	 * @param alignment , HUMBOLDT OML alignment
	 * @return alignmentType
	 */
	private AlignmentType getAlignment(IAlignment alignment) {
		AlignmentType aType = new AlignmentType();
		// 1. set about,level, ontology1,2
		if (alignment != null) {
			if (alignment.getAbout() != null)
				aType.setAbout(alignment.getAbout().getAbout());
			if (alignment.getLevel() != null) {
				aType.setLevel(alignment.getLevel());
			}
			if (alignment.getSchema1() != null) {
				aType.setOnto1(getOnto1(alignment.getSchema1()));
			}
			if (alignment.getSchema2() != null) {
				aType.setOnto2(getOnto2(alignment.getSchema2()));
			}
			// 2. add map of cells
			if (alignment.getMap() != null) {
				aType.getMap().addAll(getMaps(alignment.getMap()));
			}
			// 3. add valueClass
			if (alignment.getValueClasses() != null) {
				aType.getValueClass().addAll(getValueClasses(alignment.getValueClasses()));
			}
		}

		return aType;
	}

	/**
	 * Converts from list of the OML Classes to the list of the JAXB generated
	 * classes
	 * 
	 * @param valueClasses
	 * @return
	 */
	private List<ValueClassType> getValueClasses(List<IValueClass> valueClasses) {

		List<ValueClassType> vcTypes = new ArrayList<ValueClassType>();
		if (valueClasses != null) {
			ValueClassType vcType = new ValueClassType();
			IValueClass vClass;
			Iterator<IValueClass> iterator = valueClasses.iterator();
			while (iterator.hasNext()) {
				vClass = iterator.next();
				if (vClass.getAbout() != null) {
					vcType.setAbout(vClass.getAbout());
				}
				if (vClass.getResource() != null) {
					vcType.setResource(vClass.getResource());
				}
				if (vClass.getValue() != null) {
					vcType.getValue().addAll(getValueExpressionTypes(vClass.getValue()));
				}
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
			IAbout about = schema.getAbout();
			if (about != null)
				oType.setAbout(about.getAbout());
			if (schema.getLocation() != null) {
				oType.setLocation(schema.getLocation());
			}
			if (schema.getFormalism() != null) {
				oType.setFormalism(getFormalism(schema.getFormalism()));
			}
			if (schema.getLabels() != null) {
				oType.getLabel().addAll(schema.getLabels());
			}
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
			if (formalism.getName() != null) {
				fType.setName(formalism.getName());
			}
			if (formalism.getLocation() != null) {
				fType.setUri(formalism.getLocation().toString());
			}
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
		Iterator<?> iterator = map.iterator();
		Map jMap;
		ICell cell;

		while (iterator.hasNext()) {
			cell = (ICell) iterator.next();
			if (cell != null) {
				jMap = new Map();
				jMap.setCell(getCellType(cell));
				maps.add(jMap);
			}

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
			IAbout about = cell.getAbout();
			if (about != null)
				cType.setAbout(about.getAbout());
			// keep Measure optional
			if (cell.getMeasure() != 0) {
				cType.setMeasure(new Float(cell.getMeasure()));
			}
			if (cell.getRelation() != null) {
				cType.setRelation(getRelation(cell.getRelation()));
			}
			if (cell.getEntity1() != null) {
				cType.setEntity1(getEntity1(cell.getEntity1()));
			}
			if (cell.getEntity2() != null) {
				cType.setEntity2(getEntity2(cell.getEntity2()));
			}
			if (cell.getLabel() != null) {
				cType.getLabel().addAll(((Cell) cell).getLabel());
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
	/*
	 * private Float getMeasure(double measure) { //TODO changed structure of
	 * the Measure element in the schema
	 * 
	 * Measure jMeasure = new Measure(); jMeasure.setDatatype("xsd:float");
	 * jMeasure.setValue(new Double(measure).floatValue()); return jMeasure; }
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
			}
			else if (relation.equals(RelationType.Equivalence)) {
				return RelationEnumType.EQUIVALENCE;
			}
			else if (relation.equals(RelationType.Extra)) {
				return RelationEnumType.EXTRA;
			}
			else if (relation.equals(RelationType.HasInstance)) {
				return RelationEnumType.HAS_INSTANCE;
			}
			else if (relation.equals(RelationType.InstanceOf)) {
				return RelationEnumType.INSTANCE_OF;
			}
			else if (relation.equals(RelationType.Missing)) {
				return RelationEnumType.MISSING;
			}
			else if (relation.equals(RelationType.PartOf)) {
				return RelationEnumType.PART_OF;
			}
			else if (relation.equals(RelationType.SubsumedBy)) {
				return RelationEnumType.SUBSUMED_BY;
			}
			else if (relation.equals(RelationType.Subsumes)) {
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
				eType = new JAXBElement<PropertyType>(
						new QName("http://www.omwg.org/TR/d7/ontology/alignment", "Property"),
						PropertyType.class, pType);

			}
			else if (entity instanceof FeatureClass) {
				// instantiate as ClassType
				FeatureClass feature = (FeatureClass) entity;
				ClassType cType = getClassType(feature);

				eType = new JAXBElement<ClassType>(
						new QName("http://www.omwg.org/TR/d7/ontology/alignment", "Class"),
						ClassType.class, cType);
			}
			else if (entity instanceof Relation) {
				// instantiate as RelationType
				// TODO add implementation, for the next release
			}
			else if (entity instanceof PropertyQualifier) {
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
		IAbout about = null;
		if (feature != null) {
			if (feature.getAbout() != null) {
				about = feature.getAbout();
			}
			if (feature.getLabel() != null) {
				cType.getLabel().addAll(feature.getLabel());
			}
			if (about != null) {
				cType.setAbout(about.getAbout());
			}
			if (feature.getTransformation() != null) {
				cType.setTransf(getTransf(feature.getTransformation()));
			}
			if (feature.getAttributeTypeCondition() != null) {
				cType.getAttributeTypeCondition()
						.addAll(getConditions(feature.getAttributeTypeCondition()));
			}
			if (feature.getAttributeValueCondition() != null) {
				cType.getAttributeValueCondition()
						.addAll(getConditions(feature.getAttributeValueCondition()));
			}
			if (feature.getAttributeOccurenceCondition() != null) {
				cType.getAttributeOccurenceCondition()
						.addAll(getConditions(feature.getAttributeOccurenceCondition()));
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
	private Collection<? extends ClassConditionType> getConditions(List<Restriction> restrictions) {
		if (restrictions != null) {
			ArrayList<ClassConditionType> conditions = new ArrayList<ClassConditionType>(
					restrictions.size());
			ClassConditionType condition;
			Restriction restriction;
			Iterator<?> iterator = restrictions.iterator();
			while (iterator.hasNext()) {
				restriction = (Restriction) iterator.next();
				if (restriction != null) {
					condition = new ClassConditionType();
					condition.setRestriction(getRestrictionType(restriction));
					conditions.add(condition);
				}

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
			if (restriction.getComparator() != null) {
				rType.setComparator(getComparator(restriction.getComparator()));
			}
			if (restriction.getCqlStr() != null) {
				rType.setCqlStr(restriction.getCqlStr());
			}
			// TODO: clear with MdV
			// rType.setOnAttribute(getOnAttributeType(restriction
			// .getOnAttribute()));

			// if a list of value expressions for this restriction is empty
			// use ValueClass
			List<IValueExpression> values = restriction.getValue();
			if (values != null && values.size() > 0) {
				if (getValueClass(values) != null) {
					rType.setValueClass(getValueClass(values));
				}
			}
			else {
				if (getValueClass(restriction.getValueClass()) != null) {
					rType.setValueClass(getValueClass(restriction.getValueClass()));
				}
			}
		}
		return rType;
	}

	/**
	 * Converts from the OML ValueClass to the Jaxb ValueClassType
	 * 
	 * @param valueClass
	 * @return
	 */
	private ValueClassType getValueClass(ValueClass valueClass) {

		if (valueClass != null) {
			ValueClassType vcType = new ValueClassType();
			if (valueClass.getAbout() != null) {
				vcType.setAbout(valueClass.getAbout());
			}
			if (valueClass.getResource() != null) {
				vcType.setResource(valueClass.getResource());
			}
			if (getJAXBValueExpressions(valueClass.getValue()) != null) {
				vcType.getValue().addAll(getJAXBValueExpressions(valueClass.getValue()));
			}
			return vcType;
		}
		return null;
	}

	private Collection<? extends ValueExprType> getJAXBValueExpressions(
			List<IValueExpression> value) {
		List<ValueExprType> vExpressions = new ArrayList<ValueExprType>(value.size());
		Iterator<?> iterator = value.iterator();
		ValueExprType veType;
		while (iterator.hasNext()) {
			ValueExpression ve = (ValueExpression) iterator.next();
			veType = new ValueExprType();
			if (ve.getLiteral() != null) {
				veType.setLiteral(ve.getLiteral());
			}
			if (ve.getMax() != null) {
				veType.setMax(ve.getMax());
			}
			if (ve.getMin() != null) {
				veType.setMin(ve.getMin());
			}
			if (ve.getApply() != null) {
				veType.setApply(getApplayType(ve.getApply()));
			}
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

		if (getJAXBValueExpressions(value) != null) {
			ValueClassType vcType = new ValueClassType();
			vcType.getValue().addAll(getJAXBValueExpressions(value));
			return vcType;
		}
		return null;
	}

//	/**
//	 * Converts from OML Property to OnAttributeType
//	 * 
//	 * @param onAttribute
//	 * @return
//	 */
//	private OnAttributeType getOnAttributeType(Property onAttribute) {
//		// uses property as onAttribute until we have the implementation for the
//		// realation
//		// TODO clear the need of the about attribute fot the
//		// onAttribute-element
//		OnAttributeType oaType = new OnAttributeType();
//		oaType.setProperty(getPropertyType(onAttribute));
//		return oaType;
//	}

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

			else if (comparator.equals(ComparatorType.OTHERWISE))
				return ComparatorEnumType.OTHERWISE;
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

			if (transformation.getService() != null) {
				fType.setResource(transformation.getService().getLocation());
			}
			/*
			 * //Uli will provide us with examples
			 * fType.setResource(transformation.getLabel());
			 */
			if (transformation.getParameters() != null) {
				fType.getParam().addAll(getParameters(transformation.getParameters()));
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
	private Collection<? extends ParamType> getParameters(List<IParameter> parameters) {
		if (parameters != null) {
			ArrayList<ParamType> pTypes = new ArrayList<ParamType>(parameters.size());
			ParamType pType;
			IParameter param;
			Iterator<?> iterator = parameters.iterator();
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
			if (param.getName() != null) {
				pType.setName(param.getName());
			}
			if (param.getValue() != null) {
				pType.getValue().add(param.getValue());
			}
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
			IAbout about = property.getAbout();
			if (about != null)
				pType.setAbout(about.getAbout());
			if (property instanceof ComposedProperty
			/* && this.propertyStack < propertyStackSize */) {
				// this.propertyStack++;
				// in case it is a composed property add the property
				// composition
				// element
				PropertyCompositionType propCompType = new PropertyCompositionType();
				// set Relation
				propCompType.setRelation(getRelation(((ComposedProperty) property).getRelation()));
				// set property collection or single property
				if (((ComposedProperty) property).getCollection().size() > 1) {
					// set collection
					propCompType.setCollection(
							getPropertyCollection(((ComposedProperty) property).getCollection()));
				}
				else if (((ComposedProperty) property).getCollection().size() == 1) {
					// set single property
					propCompType.setProperty(
							getPropertyType(((ComposedProperty) property).getCollection().get(0)));
				}

				// set PropertyOperatorType
				propCompType.setOperator(
						getOperatorType(((ComposedProperty) property).getPropertyOperatorType()));
				pType.setPropertyComposition(propCompType);
			}

			if (property.getTransformation() != null) {
				pType.setTransf(getTransf(property.getTransformation()));
			}
			if (property.getDomainRestriction() != null) {
				pType.getDomainRestriction()
						.addAll(getDomainRestrictionTypes(property.getDomainRestriction()));
			}
			if (property.getTypeCondition() != null) {
				pType.getTypeCondition().addAll(property.getTypeCondition());
			}
			if (property.getLabel() != null) {
				pType.getLabel().addAll(property.getLabel());
			}
			if (property.getValueCondition() != null) {
				pType.getValueCondition().addAll(getValueConditions(property.getValueCondition()));
			}

		}
		return pType;
	}

	/**
	 * Converts propertyOperator instance from the OML enum to the JAXB-based
	 * enum
	 * 
	 * @param propertyOperatorType
	 * @return
	 */
	private PropertyOperatorType getOperatorType(
			eu.esdihumboldt.hale.io.oml.internal.goml.omwg.ComposedProperty.PropertyOperatorType propertyOperatorType) {
		if (propertyOperatorType != null) {
			// TODO clear mapping
			if (propertyOperatorType.equals(
					eu.esdihumboldt.hale.io.oml.internal.goml.omwg.ComposedProperty.PropertyOperatorType.AND))
				return PropertyOperatorType.INTERSECTION;
			if ((propertyOperatorType.equals(
					eu.esdihumboldt.hale.io.oml.internal.goml.omwg.ComposedProperty.PropertyOperatorType.OR)))
				return PropertyOperatorType.UNION;
			if ((propertyOperatorType.equals(
					eu.esdihumboldt.hale.io.oml.internal.goml.omwg.ComposedProperty.PropertyOperatorType.FIRST)))
				return PropertyOperatorType.FIRST;
			if ((propertyOperatorType.equals(
					eu.esdihumboldt.hale.io.oml.internal.goml.omwg.ComposedProperty.PropertyOperatorType.NEXT)))
				return PropertyOperatorType.NEXT;
		}
		return null;
	}

	/**
	 * Translate the list of the OML Properties To the Jaxb-based
	 * PropertyCollectionType
	 * 
	 * @param collection
	 * @return
	 */
	private PropertyCollectionType getPropertyCollection(List<Property> collection) {
		PropertyCollectionType propCollectionType = new PropertyCollectionType();
		if (collection != null) {
			Iterator<?> iterator = collection.iterator();
			while (iterator.hasNext()) {
				// get property from a list
				Property property = (Property) iterator.next();
				// convert property to the property type
				// TODO could be the circular dependencies in case of
				// ComposedProperty
				PropertyType pType = getPropertyType(property);
				// add to the collection
				Item item = new Item();
				item.setProperty(pType);
				propCollectionType.getItem().add(item);
			}

		}
		return propCollectionType;
	}

	/**
	 * Translate the list of the OML Relation To the Jaxb-based RelationType
	 * 
	 * @param relation
	 * @return
	 */
	private eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.RelationType getRelation(
			Relation relation) {
		eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.RelationType relType = new eu.esdihumboldt.hale.io.oml.internal.model.generated.oml.RelationType();
		if (relation != null) {
			// TODO clear with MdV
			if (relation.getDomainRestriction() != null
					&& relation.getDomainRestriction().get(0) != null) {
				relType.setDomainRestriction(
						getDomainRestrictionType(relation.getDomainRestriction().get(0)));
			}
			// TODO clear with MdV
			relType.setPipe(null);
			if (relation.getAbout() != null) {
				relType.setAbout(relation.getAbout().getAbout());
			}
			// TODO clear with MdV
			relType.setRelationComposition(null);
			if (relation.getRangeRestriction() != null
					&& relation.getRangeRestriction().get(0) != null) {
				relType.setRangeRestriction(
						getRangeRestrictionType(relation.getRangeRestriction().get(0)));
			}
			if (relation.getTransformation() != null) {
				relType.setTransf(getTransf(relation.getTransformation()));
			}
			// set label list
			List<String> labels = relation.getLabel();
			if (labels != null) {
				if (labels.size() > 0)
					relType.getLabel().addAll(labels);
			}

		}

		return relType;
	}

	/**
	 * Translates from OML FeatureClass To the schema based FeatureClass
	 * 
	 * @param featureClass
	 * @return
	 */
	private RangeRestrictionType getRangeRestrictionType(FeatureClass featureClass) {
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
			Iterator<?> iterator = restrictions.iterator();
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
		if (restriction.getSeq() != null) {
			vcType.setSeq(restriction.getSeq());
		}
		return vcType;
	}

//	/**
//	 * Converts from OML Restriction to the JAXB PropertyValueRestrictionType
//	 * 
//	 * @param restriction
//	 * @return
//	 */
//	private PropValueRestrictionType getPropertyValueRestrictionType(Restriction restriction) {
//		PropValueRestrictionType pvrType = new PropValueRestrictionType();
//		if (restriction != null) {
//			if (restriction.getComparator() != null) {
//				pvrType.setComparator(getComparator(restriction.getComparator()));
//			}
//			if (restriction.getValue() != null) {
//				pvrType.getValue().addAll(getValueExpressionTypes(restriction.getValue()));
//			}
//		}
//		return pvrType;
//	}

	/**
	 * Converts from List of ValueExpression to the Collection of ValueExprType
	 * 
	 * @param value
	 * @return
	 */
	private Collection<? extends ValueExprType> getValueExpressionTypes(
			List<IValueExpression> values) {
		if (values != null) {
			ArrayList<ValueExprType> veTypes = new ArrayList<ValueExprType>(values.size());
			ValueExprType veType;
			ValueExpression expression;
			Iterator<?> iterator = values.iterator();
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
			if (expression.getApply() != null) {
				veType.setApply(getApplayType(expression.getApply()));
			}
			if (expression.getLiteral() != null) {
				veType.setLiteral(expression.getLiteral());
			}
			if (expression.getMax() != null) {
				veType.setMax(expression.getMax());
			}
			if (expression.getMin() != null) {
				veType.setMin(expression.getMin());
			}
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
		// ApplyType aType = new ApplyType();
		// TODO implement it for the next release, in case we have some examples
		// aType.setOperation(function.getService().toString());
		// aType.setValue(null);

		return null;
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
			Iterator<?> iterator = features.iterator();
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
		// set one property in case of the PropertyQualifier only
		drType.setClazz(getClassType(feature));
		return drType;
	}

}
