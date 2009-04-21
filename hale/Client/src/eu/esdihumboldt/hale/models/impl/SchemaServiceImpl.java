package eu.esdihumboldt.hale.models.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.xml.SchemaFactory;
import org.geotools.xml.schema.ComplexType;
import org.geotools.xml.schema.Element;
import org.geotools.xml.schema.Schema;
import org.geotools.xml.schema.SimpleType;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.xml.sax.SAXException;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.rcp.Application;

/**
 * Implementation of {@link SchemaService}.
 */
public class SchemaServiceImpl implements SchemaService {
	
	private static Logger _log = Logger.getLogger(SchemaServiceImpl.class);
	
	private static SchemaServiceImpl instance = new SchemaServiceImpl();

	/** FeatureType collection of the source schema */
	Collection<FeatureType> sourceSchema;

	/** FeatureType collection of the target schema */
	Collection<FeatureType> targetSchema;
	
	private Collection<HaleServiceListener> listeners = new HashSet<HaleServiceListener>();
	
	private SchemaServiceImpl() {
		_log.setLevel(Level.INFO);
	}
	
	public static SchemaService getInstance() {
		return SchemaServiceImpl.instance;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#cleanSourceSchema()
	 */
	@Override
	public boolean cleanSourceSchema() {
		this.sourceSchema.clear();
		this.updateListeners();
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#cleanTargetSchema()
	 */
	@Override
	public boolean cleanTargetSchema() {
		this.targetSchema.clear();
		this.updateListeners();
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
			this.updateListeners();
			return true;
		} 
		else {
			return false;
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#loadTargetSchema(java.net.URI)
	 */
	@Override
	public boolean loadTargetSchema(URI file) {
		this.targetSchema = loadSchema(file);
		if (targetSchema != null) {
			this.updateListeners();
			return true;
		}
		else {
			return false;
		}
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
		
		InputStream is, is2;
		try {
			is = new FileInputStream(file.toString().replaceAll("\\+", " "));
			
			// FIXME: Find good way of automatically importing parent schemas
			is2 = new FileInputStream(Application.getBasePath().replaceAll("\\+", " ") + 
					"resources/schema/inheritance/gmlsf2composite_and_featcoll.xsd");

			Schema schema = null;
			try {
				SchemaFactory.getInstance(null, is2);
			} catch (Exception uhe) {
				_log.error("Imported Schema only available on-line, but " +
						"cannot be retrieved.", uhe);
			}
			try {
				schema = SchemaFactory.getInstance(null, is);
			} catch (Exception uhe) {
				_log.error("Imported Schema only available on-line, but " +
						"cannot be retrieved.", uhe);
			}

			Schema[] imports = schema.getImports();
			for (Schema s : imports) {
				_log.debug("Imported URI + Name: " + s.getURI() + " " + s.getTargetNamespace());
			}
						
			Collection<SimpleFeatureType> inTypes = new HashSet<SimpleFeatureType>();

			// Build first a list of FeatureTypes
			for (ComplexType type : schema.getComplexTypes()) {
				SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
				builder.setName(type.getName());
				builder.setNamespaceURI(type.getNamespace());
				builder.setAbstract(type.isAbstract());

				if (type.getParent() != null) {
					_log.debug("Feature type: " + type.getName()
							+ ", parent feature type: "
							+ type.getParent().getName());

					for (Element element : type.getChildElements()) {
						if (element.getType() instanceof SimpleType) {
							builder.add(element.getName(), element.getType()
									.getClass());
						}
						_log.debug("\telement: " + element.getName()
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
						_log.debug("Feature type: " + type.getName()
								+ ", parent feature type: "
								+ type.getParent().getName());

						for (Element element : type.getChildElements()) {
							if (element.getType() instanceof SimpleType) {
								_log.debug("\tsimpl0e type element: "
								 + element.getName());
								builder.add(element.getName(), element
										.getType().getClass());
							}
							_log.debug("\telement: "
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
									_log.debug("Parent type set to "
											+ featureType.getName());
								}
							}
						}
						collection.add(builder.buildFeatureType());
					}
				}
			}

		} catch (FileNotFoundException e) {
			_log.error(e);
		}

		return collection;
	}

	@Override
	public boolean addListener(HaleServiceListener sl) {
		return this.listeners.add(sl);
	}
	
	/**
	 * Inform {@link HaleServiceListener}s of an update.
	 */
	private void updateListeners() {
		for (HaleServiceListener hsl : this.listeners) {
			hsl.update();
		}
	}
}
