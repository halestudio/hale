package eu.esdihumboldt.hale.models.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeImpl;
import org.geotools.xml.SchemaFactory;
import org.geotools.xml.gml.GMLComplexTypes.AbstractFeatureType;
import org.geotools.xml.schema.Attribute;
import org.geotools.xml.schema.ComplexType;
import org.geotools.xml.schema.Element;
import org.geotools.xml.schema.Schema;
import org.geotools.xml.schema.SimpleType;
import org.geotools.xml.xsi.XSISimpleTypes;
import org.geotools.xml.xsi.XSISimpleTypes.String;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.xml.sax.SAXException;

import test.eu.esdihumboldt.hale.models.factory.FeatureCollectionUtilities;

import eu.esdihumboldt.hale.models.SchemaService;

/**
 * Implementation of {@link SchemaService}
 */
public class SchemaServiceImpl implements SchemaService {

	/** FeatureType collection of the source schema */
	Collection<FeatureType> sourceSchema;

	/** FeatureType collection of the target schema */
	Collection<FeatureType> targetSchema;

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#cleanSourceSchema()
	 */
	@Override
	public boolean cleanSourceSchema() {
		this.sourceSchema.clear();
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#cleanTargetSchema()
	 */
	@Override
	public boolean cleanTargetSchema() {
		this.targetSchema.clear();
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#getSourceSchema()
	 */
	@Override
	public Collection<FeatureType> getSourceSchema() {
		return sourceSchema;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#getTargetSchema()
	 */
	@Override
	public Collection<FeatureType> getTargetSchema() {
		return this.targetSchema;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#loadSourceSchema(java.net.URI)
	 */
	@Override
	public boolean loadSourceSchema(URI file) {
		this.sourceSchema = loadSchema(file);
		if (this.sourceSchema != null) {
			return true;
		} else
			return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#loadTargetSchema(java.net.URI)
	 */
	@Override
	public boolean loadTargetSchema(URI file) {
		this.targetSchema = loadSchema(file);
		if (targetSchema != null)
			return true;
		else
			return false;
	}

	/**
	 * Method to load a XSD schema file and build a collection of FeatureTypes.
	 * 
	 * @param file
	 *            URI which represents a file
	 * @return Collection FeatureType collection.
	 */
	private Collection<FeatureType> loadSchema(URI file) {
		Collection<FeatureType> collection = new ArrayList<org.opengis.feature.type.FeatureType>();
		// try {
		//			
		//Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*",
		// new XSDResourceFactoryImpl());
		//
		//			
		// ResourceSet resourceSet = new ResourceSetImpl();
		//resourceSet.getResource(org.eclipse.emf.common.util.URI.createURI(file
		// .toString()), true);
		//
		// XSDSchema xsdSchema = null;
		// for (Iterator resources = resourceSet.getResources().iterator();
		// resources
		// .hasNext();) {
		// Resource res = (Resource) resources.next();
		// if (res instanceof XSDResourceImpl) {
		// xsdSchema = ((XSDResourceImpl) res).getSchema();
		// break;
		// }
		// }
		//
		// Configuration config = new org.geotools.gml3.GMLConfiguration();
		// MutablePicoContainer ctx = new DefaultPicoContainer();
		// ctx = config.setupContext(ctx);
		// BindingLoader bindingLoader = new BindingLoader(config
		// .setupBindings());
		// BindingWalkerFactory bwf = new BindingWalkerFactoryImpl(
		// bindingLoader, ctx);
		//
		// List eltDecs = xsdSchema.getElementDeclarations();
		// org.opengis.feature.type.FeatureType ft = null;
		// for (int i = 0; i < eltDecs.size(); i++) {
		// XSDElementDeclaration xsdElt = (XSDElementDeclaration) eltDecs
		// .get(i);
		// ft = GML3ParsingUtils.featureType(xsdElt, bwf);
		// System.out.println("ft is " + ft);
		// schema.add(ft);
		// }
		//
		// System.out.println("schema loaded");
		//
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (SAXException e) {
		// e.printStackTrace();
		// } catch (URISyntaxException e) {
		// e.printStackTrace();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		InputStream is, is2;
		try {
			is = new FileInputStream(file.toString());
			is2 = new FileInputStream(
					"resources/schema/inheritance/gmlsf2composite_and_featcoll.xsd");

			SchemaFactory factory = new SchemaFactory();
			Schema schema2 = factory.getInstance(null, is2);
			Schema schema = factory.getInstance(null, is);

			Collection<SimpleFeatureType> inTypes = new HashSet<SimpleFeatureType>();

			// Build first a list of FeatureTypes
			for (ComplexType type : schema.getComplexTypes()) {
				SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
				builder.setName(type.getName());
				builder.setNamespaceURI(type.getNamespace());
				builder.setAbstract(type.isAbstract());

				if (type.getParent() != null) {
					System.out.println("Feature type: " + type.getName()
							+ ", parent feature type: "
							+ type.getParent().getName());

					for (Element element : type.getChildElements()) {
						if (element.getType() instanceof SimpleType) {
							builder.add(element.getName(), element.getType()
									.getClass());
						}
						System.out.println("\telement: " + element.getName()
								+ ", " + element.getType().getName());
					}
					inTypes.add(builder.buildFeatureType());
				}
			}

			for (ComplexType type : schema.getComplexTypes()) {
				if (type.getParent() instanceof ComplexType) {
					// Create builder
					SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
					builder.setName(type.getName());
					builder.setNamespaceURI(type.getNamespace());
					builder.setAbstract(type.isAbstract());

					if (type.getParent() != null) {
						System.out.println("Feature type: " + type.getName()
								+ ", parent feature type: "
								+ type.getParent().getName());

						for (Element element : type.getChildElements()) {
							if (element.getType() instanceof SimpleType) {
								// System.out.println("\tsimpl0e type element: "
								// + element.getName());
								builder.add(element.getName(), element
										.getType().getClass());
							}
							System.out.println("\telement: "
									+ element.getName() + ", "
									+ element.getType().getName());
						}

						if (type.getParent().getName().equals(
								"AbstractFeatureType")) {
							builder.setSuperType(null);
						} else {
							for (SimpleFeatureType featureType : inTypes) {
								if (featureType.getName().getLocalPart()
										.equals(type.getParent().getName())) {
									builder.setSuperType(featureType);
									System.out.println("Parent type set to "
											+ featureType.getName());
								}
							}
						}
						collection.add(builder.buildFeatureType());
					}
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		return collection;
	}
}
