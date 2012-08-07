/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.common.definition;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.Classification;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.CommonSharedImagesConstants;
import eu.esdihumboldt.hale.ui.common.internal.CommonUIPlugin;
import eu.esdihumboldt.hale.ui.common.service.population.PopulationService;
import eu.esdihumboldt.hale.ui.geometry.DefaultGeometryUtil;

/**
 * Manages images for definitions. Should be {@link #dispose()}d when the images
 * are not used any more.
 * @author Simon Templer
 */
public class DefinitionImages implements CommonSharedImagesConstants {
	
	/**
	 * Represents a image configuration
	 */
	private static class ImageConf {
		
		private final String identifier;
		
		private final boolean attribute;
		
		private final boolean def;
		
		private final boolean mandatory;
		
		private final boolean faded;

		/**
		 * Constructor
		 * 
		 * @param identifier the image identifier/key
		 * @param attribute if an attribute is represented (not an element)
		 * @param def if the image is to be marked a default (e.g. default geometry)
		 * @param mandatory if the image is to be marked as mandatory 
		 * @param faded if the image is displayed faded
		 */
		public ImageConf(String identifier, boolean attribute, boolean def,
				boolean mandatory, boolean faded) {
			super();
			this.identifier = identifier;
			this.attribute = attribute;
			this.def = def;
			this.mandatory = mandatory;
			this.faded = faded;
		}

		/**
		 * @see Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (attribute ? 1231 : 1237);
			result = prime * result + (def ? 1231 : 1237);
			result = prime * result + (faded ? 1231 : 1237);
			result = prime * result
					+ ((identifier == null) ? 0 : identifier.hashCode());
			result = prime * result + (mandatory ? 1231 : 1237);
			return result;
		}

		/**
		 * @see Object#equals(Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ImageConf other = (ImageConf) obj;
			if (attribute != other.attribute)
				return false;
			if (def != other.def)
				return false;
			if (faded != other.faded)
				return false;
			if (identifier == null) {
				if (other.identifier != null)
					return false;
			} else if (!identifier.equals(other.identifier))
				return false;
			if (mandatory != other.mandatory)
				return false;
			return true;
		}
		
	}
	
	private final Image attribOverlay = CommonUIPlugin.getImageDescriptor(
		"/icons/attrib_overlay2.gif").createImage(); //$NON-NLS-1$

	private final Image defOverlay = CommonUIPlugin.getImageDescriptor(
		"/icons/def_overlay.gif").createImage(); //$NON-NLS-1$
	
	private final Image mandatoryOverlay = CommonUIPlugin.getImageDescriptor(
		"/icons/mandatory_ov2.gif").createImage(); //$NON-NLS-1$
	
	private final Map<ImageConf, Image> overlayedImages = new HashMap<ImageConf, Image>();
	
//	private final Map<String, Image> styleImages = new HashMap<String, Image>();
	
	private boolean suppressMandatory = false;
	
	/**
	 * Dispose all images. {@link #getImage(Definition)} may not be called after 
	 * calling this method.
	 */
	public void dispose() {
		for (Image image : overlayedImages.values()) {
			image.dispose();
		}
		overlayedImages.clear();
		
//		for (Image image : styleImages.values()) {
//			image.dispose();
//		}
//		styleImages.clear();
		
		attribOverlay.dispose();
		defOverlay.dispose();
		mandatoryOverlay.dispose();
	}
	
	/**
	 * Get the image for the given definition
	 * @param def the definition
	 * @return the image, may be <code>null</code>
	 */
	public Image getImage(Definition<?> def) {
		return getImage(null, def); 
	}
	
	/**
	 * Get the image for the given entity definition
	 * @param entityDef the entity definition
	 * @return the image, may be <code>null</code>
	 */
	public Image getImage(EntityDefinition entityDef) {
		return getImage(entityDef, entityDef.getDefinition());
	}
		
	/**
	 * Get the image for the given definition
	 * @param entityDef the entity definition, may be <code>null</code>
	 * @param def the definition
	 * @return the image, may be <code>null</code>
	 */
	protected Image getImage(EntityDefinition entityDef, Definition<?> def) {
		Classification clazz = Classification.getClassification(def); 
		
		String imageName = getImageForClassification(clazz);
		
		// retrieve image for key
		Image image;
		if (imageName == null) {
			// default
			imageName = ISharedImages.IMG_OBJ_ELEMENT;
			image = PlatformUI.getWorkbench().getSharedImages().getImage(imageName);
		}
		else  {
			image = CommonSharedImages.getImageRegistry().get(imageName);
		}
		
		// legend image
		//XXX not supported yet
//		if (to.getDefinition() != null && (to.getDefinition() instanceof TypeDefinition) 
//				&& ((TypeDefinition) to.getDefinition()).hasGeometry() 
//				&& to.getPropertyType() instanceof FeatureType) {
//			FeatureType type = (FeatureType) to.getPropertyType();
//			BufferedImage img = StyleHelper.getLegendImage(type, true);
//			if (img != null) {
//				// replace image with style image
//				ImageData imgData = SwingRcpUtilities.convertToSWT(img);
//				image = new Image(Display.getCurrent(), imgData);
//				
//				String key = to.getName().getURI();
//				Image old = null;
//				if (styleImages.containsKey(key)) {
//					old = styleImages.get(key);
//				}
//				styleImages.put(key, image);
//				if (old != null) {
//					old.dispose(); // ok here?
//				}
//			}
//		}
		// check for inline attributes
//		else {
			boolean attribute = (def instanceof PropertyDefinition) && 
				((PropertyDefinition) def).getConstraint(XmlAttributeFlag.class).isEnabled();
			boolean mandatory = false;
			if (!suppressMandatory) {
				if (def instanceof PropertyDefinition) {
					Cardinality cardinality = ((PropertyDefinition) def).getConstraint(Cardinality.class);
					mandatory = cardinality.getMinOccurs() > 0 && 
						!((PropertyDefinition) def).getConstraint(NillableFlag.class).isEnabled();
				}
				else if (def instanceof GroupPropertyDefinition) {
					Cardinality cardinality = ((GroupPropertyDefinition) def).getConstraint(Cardinality.class);
					mandatory = cardinality.getMinOccurs() > 0;
				}
			}
			
			boolean deflt = false;
			boolean faded = false;
			if (entityDef != null) {
				// entity definition needed to determine if item is a default geometry
				deflt = DefaultGeometryUtil.isDefaultGeometry(entityDef);
				
				// and to determine population
				PopulationService ps = (PopulationService) PlatformUI.getWorkbench().getService(PopulationService.class);
				if (ps != null && ps.hasPopulation(entityDef.getSchemaSpace())) {
					faded = (ps.getPopulation(entityDef) == 0);
				}
			}
			
			if (deflt || mandatory || attribute || faded) {
				// overlayed image
				ImageConf conf = new ImageConf(imageName, attribute, deflt, mandatory, faded);
				Image overlayedImage = overlayedImages.get(conf);
				
				if (overlayedImage == null) {
					// apply overlays to image
					
					Image copy = new Image(image.getDevice(), image.getBounds());
					// draw on image
					GC gc = new GC(copy);
					try {
						gc.drawImage(image, 0, 0);
						if (attribute) {
							gc.drawImage(attribOverlay, 0, 0);
						}
						if (deflt) {
							gc.drawImage(defOverlay, 0, 0);
						}
						if (mandatory) {
							gc.drawImage(mandatoryOverlay, 0, 0);
						}
					} finally {
						gc.dispose();
					}
					
					if (faded) {
						ImageData imgData = copy.getImageData();
						imgData.alpha = 150;
						Image copy2 = new Image(image.getDevice(), imgData);
						copy.dispose();
						copy = copy2;
					}
					
					image = copy;
					overlayedImages.put(conf, copy);
				}
				else {
					image = overlayedImage;
				}
			}
//		}
		
		return image;
	}

	/**
	 * Get the image name for the given classification. The image name 
	 * represents a file residing in the icons folder. 
	 * @param clazz the classification
	 * @return the image name or <code>null</code>
	 */
	private String getImageForClassification(Classification clazz) {
		switch (clazz) {
		case ABSTRACT_FT:
			return IMG_DEFINITION_ABSTRACT_FT;
		case CONCRETE_FT:
			return IMG_DEFINITION_CONCRETE_FT;
		case STRING_PROPERTY:
			return IMG_DEFINITION_STRING_PROPERTY;
		case NUMERIC_PROPERTY:
			return IMG_DEFINITION_NUMERIC_PROPERTY;
		case GEOMETRIC_PROPERTY:
			return IMG_DEFINITION_GEOMETRIC_PROPERTY;
		case GROUP:
			return IMG_DEFINITION_GROUP;
		case CHOICE:
			return IMG_DEFINITION_CHOICE;
		case CONCRETE_TYPE:
			return IMG_DEFINITION_CONCRETE_TYPE;
		case ABSTRACT_TYPE:
			return IMG_DEFINITION_ABSTRACT_TYPE; 
		default:
			// where no dedicated image is available yet
			return null;
		}
	}

	/**
	 * @return the suppressMandatory
	 */
	public boolean isSuppressMandatory() {
		return suppressMandatory;
	}

	/**
	 * @param suppressMandatory the suppressMandatory to set
	 */
	public void setSuppressMandatory(boolean suppressMandatory) {
		this.suppressMandatory = suppressMandatory;
	}

}
