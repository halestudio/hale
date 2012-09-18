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


package eu.esdihumboldt.hale.io.oml.internal.model.generated.oml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.esdihumboldt.hale.io.oml.internal.model.generated.oml package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ValueClass_QNAME = new QName("http://www.esdi-humboldt.eu/goml", "ValueClass");
    private final static QName _Value_QNAME = new QName("http://www.omwg.org/TR/d7/ontology/alignment", "value");
    private final static QName _Formalism_QNAME = new QName("http://knowledgeweb.semanticweb.org/heterogeneity/alignment", "Formalism");
    private final static QName _FeatureClass_QNAME = new QName("http://www.omwg.org/TR/d7/ontology/alignment", "FeatureClass");
    private final static QName _RangeRestriction_QNAME = new QName("http://www.omwg.org/TR/d7/ontology/alignment", "rangeRestriction");
    private final static QName _Entity_QNAME = new QName("http://www.omwg.org/TR/d7/ontology/alignment", "_Entity");
    private final static QName _PropertyQualifier_QNAME = new QName("http://www.omwg.org/TR/d7/ontology/alignment", "PropertyQualifier");
    private final static QName _Transformation_QNAME = new QName("http://www.omwg.org/TR/d7/ontology/alignment", "_transformation");
    private final static QName _Pipe_QNAME = new QName("http://www.omwg.org/TR/d7/ontology/alignment", "pipe");
    private final static QName _Transf_QNAME = new QName("http://www.omwg.org/TR/d7/ontology/alignment", "transf");
    private final static QName _TypeCondition_QNAME = new QName("http://www.omwg.org/TR/d7/ontology/alignment", "typeCondition");
    private final static QName _Property_QNAME = new QName("http://www.omwg.org/TR/d7/ontology/alignment", "Property");
    private final static QName _Alignment_QNAME = new QName("http://knowledgeweb.semanticweb.org/heterogeneity/alignment", "Alignment");
    private final static QName _Instance_QNAME = new QName("http://www.omwg.org/TR/d7/ontology/alignment", "Instance");
    private final static QName _ValueCondition_QNAME = new QName("http://www.omwg.org/TR/d7/ontology/alignment", "valueCondition");
    private final static QName _Cell_QNAME = new QName("http://knowledgeweb.semanticweb.org/heterogeneity/alignment", "Cell");
    private final static QName _Service_QNAME = new QName("http://www.omwg.org/TR/d7/ontology/alignment", "service");
    private final static QName _Measure_QNAME = new QName("http://knowledgeweb.semanticweb.org/heterogeneity/alignment", "measure");
    private final static QName _OnAttribute_QNAME = new QName("http://www.omwg.org/TR/d7/ontology/alignment", "onAttribute");
    private final static QName _Class_QNAME = new QName("http://www.omwg.org/TR/d7/ontology/alignment", "Class");
    private final static QName _Label_QNAME = new QName("http://www.omwg.org/TR/d7/ontology/alignment", "label");
    private final static QName _Comparator_QNAME = new QName("http://www.omwg.org/TR/d7/ontology/alignment", "comparator");
    private final static QName _Ontology_QNAME = new QName("http://knowledgeweb.semanticweb.org/heterogeneity/alignment", "Ontology");
    private final static QName _DomainRestriction_QNAME = new QName("http://www.omwg.org/TR/d7/ontology/alignment", "domainRestriction");
    private final static QName _CqlStr_QNAME = new QName("http://www.esdi-humboldt.eu/goml", "cqlStr");
    private final static QName _Relation_QNAME = new QName("http://www.omwg.org/TR/d7/ontology/alignment", "Relation");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.esdihumboldt.hale.io.oml.internal.model.generated.oml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RelationCompositionType }
     * 
     */
    public RelationCompositionType createRelationCompositionType() {
        return new RelationCompositionType();
    }

    /**
     * Create an instance of {@link TransfPipeType }
     * 
     */
    public TransfPipeType createTransfPipeType() {
        return new TransfPipeType();
    }

    /**
     * Create an instance of {@link RangeRestrictionType }
     * 
     */
    public RangeRestrictionType createRangeRestrictionType() {
        return new RangeRestrictionType();
    }

    /**
     * Create an instance of {@link RelationType }
     * 
     */
    public RelationType createRelationType() {
        return new RelationType();
    }

    /**
     * Create an instance of {@link AlignmentType.Onto2 }
     * 
     */
    public AlignmentType.Onto2 createAlignmentTypeOnto2() {
        return new AlignmentType.Onto2();
    }

    /**
     * Create an instance of {@link Entity2 }
     * 
     */
    public Entity2 createEntity2() {
        return new Entity2();
    }

    /**
     * Create an instance of {@link AlignmentType.Onto1 }
     * 
     */
    public AlignmentType.Onto1 createAlignmentTypeOnto1() {
        return new AlignmentType.Onto1();
    }

    /**
     * Create an instance of {@link OntologyType.Formalism }
     * 
     */
    public OntologyType.Formalism createOntologyTypeFormalism() {
        return new OntologyType.Formalism();
    }

    /**
     * Create an instance of {@link RDF }
     * 
     */
    public RDF createRDF() {
        return new RDF();
    }

    /**
     * Create an instance of {@link DomainRestrictionType }
     * 
     */
    public DomainRestrictionType createDomainRestrictionType() {
        return new DomainRestrictionType();
    }

    /**
     * Create an instance of {@link ClassType }
     * 
     */
    public ClassType createClassType() {
        return new ClassType();
    }

    /**
     * Create an instance of {@link OnAttributeType }
     * 
     */
    public OnAttributeType createOnAttributeType() {
        return new OnAttributeType();
    }

    /**
     * Create an instance of {@link AlignmentType }
     * 
     */
    public AlignmentType createAlignmentType() {
        return new AlignmentType();
    }

    /**
     * Create an instance of {@link PropertyType }
     * 
     */
    public PropertyType createPropertyType() {
        return new PropertyType();
    }

    /**
     * Create an instance of {@link Entity1 }
     * 
     */
    public Entity1 createEntity1() {
        return new Entity1();
    }

    /**
     * Create an instance of {@link ClassCollectionType.Item }
     * 
     */
    public ClassCollectionType.Item createClassCollectionTypeItem() {
        return new ClassCollectionType.Item();
    }

    /**
     * Create an instance of {@link RestrictionType }
     * 
     */
    public RestrictionType createRestrictionType() {
        return new RestrictionType();
    }

    /**
     * Create an instance of {@link FormalismType }
     * 
     */
    public FormalismType createFormalismType() {
        return new FormalismType();
    }

    /**
     * Create an instance of {@link InstanceType }
     * 
     */
    public InstanceType createInstanceType() {
        return new InstanceType();
    }

    /**
     * Create an instance of {@link PropertyCompositionType }
     * 
     */
    public PropertyCompositionType createPropertyCompositionType() {
        return new PropertyCompositionType();
    }

    /**
     * Create an instance of {@link AlignmentType.Map }
     * 
     */
    public AlignmentType.Map createAlignmentTypeMap() {
        return new AlignmentType.Map();
    }

    /**
     * Create an instance of {@link ClassConditionType }
     * 
     */
    public ClassConditionType createClassConditionType() {
        return new ClassConditionType();
    }

    /**
     * Create an instance of {@link ValueExprType }
     * 
     */
    public ValueExprType createValueExprType() {
        return new ValueExprType();
    }

    /**
     * Create an instance of {@link ServiceType }
     * 
     */
    public ServiceType createServiceType() {
        return new ServiceType();
    }

    /**
     * Create an instance of {@link PropertyQualifierType }
     * 
     */
    public PropertyQualifierType createPropertyQualifierType() {
        return new PropertyQualifierType();
    }

    /**
     * Create an instance of {@link ParamType }
     * 
     */
    public ParamType createParamType() {
        return new ParamType();
    }

    /**
     * Create an instance of {@link ValueConditionType }
     * 
     */
    public ValueConditionType createValueConditionType() {
        return new ValueConditionType();
    }

    /**
     * Create an instance of {@link OntologyType }
     * 
     */
    public OntologyType createOntologyType() {
        return new OntologyType();
    }

    /**
     * Create an instance of {@link CellType }
     * 
     */
    public CellType createCellType() {
        return new CellType();
    }

    /**
     * Create an instance of {@link PropertyCollectionType.Item }
     * 
     */
    public PropertyCollectionType.Item createPropertyCollectionTypeItem() {
        return new PropertyCollectionType.Item();
    }

    /**
     * Create an instance of {@link ClassCompositionType }
     * 
     */
    public ClassCompositionType createClassCompositionType() {
        return new ClassCompositionType();
    }

    /**
     * Create an instance of {@link ValueClassType }
     * 
     */
    public ValueClassType createValueClassType() {
        return new ValueClassType();
    }

    /**
     * Create an instance of {@link ApplyType }
     * 
     */
    public ApplyType createApplyType() {
        return new ApplyType();
    }

    /**
     * Create an instance of {@link FunctionType }
     * 
     */
    public FunctionType createFunctionType() {
        return new FunctionType();
    }

    /**
     * Create an instance of {@link ClassCollectionType }
     * 
     */
    public ClassCollectionType createClassCollectionType() {
        return new ClassCollectionType();
    }

    /**
     * Create an instance of {@link PropertyCollectionType }
     * 
     */
    public PropertyCollectionType createPropertyCollectionType() {
        return new PropertyCollectionType();
    }

    /**
     * Create an instance of {@link PropValueRestrictionType }
     * 
     */
    public PropValueRestrictionType createPropValueRestrictionType() {
        return new PropValueRestrictionType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValueClassType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.esdi-humboldt.eu/goml", name = "ValueClass")
    public JAXBElement<ValueClassType> createValueClass(ValueClassType value) {
        return new JAXBElement<ValueClassType>(_ValueClass_QNAME, ValueClassType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValueExprType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omwg.org/TR/d7/ontology/alignment", name = "value")
    public JAXBElement<ValueExprType> createValue(ValueExprType value) {
        return new JAXBElement<ValueExprType>(_Value_QNAME, ValueExprType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FormalismType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://knowledgeweb.semanticweb.org/heterogeneity/alignment", name = "Formalism")
    public JAXBElement<FormalismType> createFormalism(FormalismType value) {
        return new JAXBElement<FormalismType>(_Formalism_QNAME, FormalismType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClassType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omwg.org/TR/d7/ontology/alignment", name = "FeatureClass", substitutionHeadNamespace = "http://www.omwg.org/TR/d7/ontology/alignment", substitutionHeadName = "_Entity")
    public JAXBElement<ClassType> createFeatureClass(ClassType value) {
        return new JAXBElement<ClassType>(_FeatureClass_QNAME, ClassType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RangeRestrictionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omwg.org/TR/d7/ontology/alignment", name = "rangeRestriction")
    public JAXBElement<RangeRestrictionType> createRangeRestriction(RangeRestrictionType value) {
        return new JAXBElement<RangeRestrictionType>(_RangeRestriction_QNAME, RangeRestrictionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EntityType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omwg.org/TR/d7/ontology/alignment", name = "_Entity")
    public JAXBElement<EntityType> createEntity(EntityType value) {
        return new JAXBElement<EntityType>(_Entity_QNAME, EntityType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PropertyQualifierType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omwg.org/TR/d7/ontology/alignment", name = "PropertyQualifier", substitutionHeadNamespace = "http://www.omwg.org/TR/d7/ontology/alignment", substitutionHeadName = "_Entity")
    public JAXBElement<PropertyQualifierType> createPropertyQualifier(PropertyQualifierType value) {
        return new JAXBElement<PropertyQualifierType>(_PropertyQualifier_QNAME, PropertyQualifierType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransformationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omwg.org/TR/d7/ontology/alignment", name = "_transformation")
    public JAXBElement<TransformationType> createTransformation(TransformationType value) {
        return new JAXBElement<TransformationType>(_Transformation_QNAME, TransformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransfPipeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omwg.org/TR/d7/ontology/alignment", name = "pipe", substitutionHeadNamespace = "http://www.omwg.org/TR/d7/ontology/alignment", substitutionHeadName = "_transformation")
    public JAXBElement<TransfPipeType> createPipe(TransfPipeType value) {
        return new JAXBElement<TransfPipeType>(_Pipe_QNAME, TransfPipeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FunctionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omwg.org/TR/d7/ontology/alignment", name = "transf", substitutionHeadNamespace = "http://www.omwg.org/TR/d7/ontology/alignment", substitutionHeadName = "_transformation")
    public JAXBElement<FunctionType> createTransf(FunctionType value) {
        return new JAXBElement<FunctionType>(_Transf_QNAME, FunctionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omwg.org/TR/d7/ontology/alignment", name = "typeCondition")
    public JAXBElement<String> createTypeCondition(String value) {
        return new JAXBElement<String>(_TypeCondition_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omwg.org/TR/d7/ontology/alignment", name = "Property", substitutionHeadNamespace = "http://www.omwg.org/TR/d7/ontology/alignment", substitutionHeadName = "_Entity")
    public JAXBElement<PropertyType> createProperty(PropertyType value) {
        return new JAXBElement<PropertyType>(_Property_QNAME, PropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AlignmentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://knowledgeweb.semanticweb.org/heterogeneity/alignment", name = "Alignment")
    public JAXBElement<AlignmentType> createAlignment(AlignmentType value) {
        return new JAXBElement<AlignmentType>(_Alignment_QNAME, AlignmentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InstanceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omwg.org/TR/d7/ontology/alignment", name = "Instance", substitutionHeadNamespace = "http://www.omwg.org/TR/d7/ontology/alignment", substitutionHeadName = "_Entity")
    public JAXBElement<InstanceType> createInstance(InstanceType value) {
        return new JAXBElement<InstanceType>(_Instance_QNAME, InstanceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValueConditionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omwg.org/TR/d7/ontology/alignment", name = "valueCondition")
    public JAXBElement<ValueConditionType> createValueCondition(ValueConditionType value) {
        return new JAXBElement<ValueConditionType>(_ValueCondition_QNAME, ValueConditionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CellType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://knowledgeweb.semanticweb.org/heterogeneity/alignment", name = "Cell")
    public JAXBElement<CellType> createCell(CellType value) {
        return new JAXBElement<CellType>(_Cell_QNAME, CellType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omwg.org/TR/d7/ontology/alignment", name = "service", substitutionHeadNamespace = "http://www.omwg.org/TR/d7/ontology/alignment", substitutionHeadName = "_transformation")
    public JAXBElement<ServiceType> createService(ServiceType value) {
        return new JAXBElement<ServiceType>(_Service_QNAME, ServiceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Float }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://knowledgeweb.semanticweb.org/heterogeneity/alignment", name = "measure")
    public JAXBElement<Float> createMeasure(Float value) {
        return new JAXBElement<Float>(_Measure_QNAME, Float.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnAttributeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omwg.org/TR/d7/ontology/alignment", name = "onAttribute")
    public JAXBElement<OnAttributeType> createOnAttribute(OnAttributeType value) {
        return new JAXBElement<OnAttributeType>(_OnAttribute_QNAME, OnAttributeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClassType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omwg.org/TR/d7/ontology/alignment", name = "Class", substitutionHeadNamespace = "http://www.omwg.org/TR/d7/ontology/alignment", substitutionHeadName = "_Entity")
    public JAXBElement<ClassType> createClass(ClassType value) {
        return new JAXBElement<ClassType>(_Class_QNAME, ClassType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omwg.org/TR/d7/ontology/alignment", name = "label")
    public JAXBElement<String> createLabel(String value) {
        return new JAXBElement<String>(_Label_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComparatorEnumType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omwg.org/TR/d7/ontology/alignment", name = "comparator")
    public JAXBElement<ComparatorEnumType> createComparator(ComparatorEnumType value) {
        return new JAXBElement<ComparatorEnumType>(_Comparator_QNAME, ComparatorEnumType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OntologyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://knowledgeweb.semanticweb.org/heterogeneity/alignment", name = "Ontology")
    public JAXBElement<OntologyType> createOntology(OntologyType value) {
        return new JAXBElement<OntologyType>(_Ontology_QNAME, OntologyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DomainRestrictionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omwg.org/TR/d7/ontology/alignment", name = "domainRestriction")
    public JAXBElement<DomainRestrictionType> createDomainRestriction(DomainRestrictionType value) {
        return new JAXBElement<DomainRestrictionType>(_DomainRestriction_QNAME, DomainRestrictionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.esdi-humboldt.eu/goml", name = "cqlStr")
    public JAXBElement<String> createCqlStr(String value) {
        return new JAXBElement<String>(_CqlStr_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RelationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.omwg.org/TR/d7/ontology/alignment", name = "Relation", substitutionHeadNamespace = "http://www.omwg.org/TR/d7/ontology/alignment", substitutionHeadName = "_Entity")
    public JAXBElement<RelationType> createRelation(RelationType value) {
        return new JAXBElement<RelationType>(_Relation_QNAME, RelationType.class, null, value);
    }

}
