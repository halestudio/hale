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
package eu.esdihumboldt.hale.ui.views.schemas.legacy;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.commons.goml.omwg.FeatureClass;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.model.schema.AttributeItem;
import eu.esdihumboldt.hale.ui.model.schema.SchemaItem;
import eu.esdihumboldt.hale.ui.model.schema.TreeObject;
import eu.esdihumboldt.hale.ui.model.schema.TreeObject.TreeObjectType;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.style.helper.StyleHelper;
import eu.esdihumboldt.hale.ui.util.swing.SwingRcpUtilities;
import eu.esdihumboldt.hale.ui.util.viewer.TipProvider;
import eu.esdihumboldt.hale.ui.views.schemas.internal.SchemasViewPlugin;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.ext.ITransformation;

/**
 * Provides Images for the elements of the data models in the 
 * ModelNavigationView.
 * 
 * @author Thorsten Reitz, Fraunhofer IGD; Simon Templer, Fraunhofer IGD
 */
public class ModelNavigationViewLabelProvider extends LabelProvider
	implements IColorProvider, TipProvider {
	
	private static class ImageConf {
		
		private final String identifier;
		
		private final boolean attribute;
		
		private final boolean def;
		
		private final boolean mandatory;

		/**
		 * Constructor
		 * 
		 * @param identifier the image identifier/key
		 * @param attribute if an attribute is represented (not an element)
		 * @param def if the image is to be marked a default (e.g. default geometry)
		 * @param mandatory if the image is to be marked as mandatory 
		 */
		public ImageConf(String identifier, boolean attribute, boolean def,
				boolean mandatory) {
			super();
			this.identifier = identifier;
			this.attribute = attribute;
			this.def = def;
			this.mandatory = mandatory;
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
	
	private final Map<RGB, Color> createdColors = new HashMap<RGB, Color>();
	
	private final Image attribOverlay = SchemasViewPlugin.getImageDescriptor(
			"/icons/attrib_overlay2.gif").createImage(); //$NON-NLS-1$
	
	private final Image defOverlay = SchemasViewPlugin.getImageDescriptor(
			"/icons/def_overlay.gif").createImage(); //$NON-NLS-1$
	
	private final Image mandatoryOverlay = SchemasViewPlugin.getImageDescriptor(
			"/icons/mandatory_ov2.gif").createImage(); //$NON-NLS-1$
	
	private final Map<String, Image> images = new HashMap<String, Image>();
	
	private final Map<ImageConf, Image> overlayedImages = new HashMap<ImageConf, Image>();
	
	private final Map<String, Image> styleImages = new HashMap<String, Image>();
	
	@Override
	public String getText(Object obj) {
		return obj.toString();
	}
	
	/**
	 * @see TipProvider#getToolTip(Object)
	 */
	@Override
	public String getToolTip(Object element) {
		if (element instanceof SchemaItem) {
			String description = ((SchemaItem) element).getDefinition().getDescription();
			if (description != null && !description.isEmpty()) {
				return description;
			}
		}
		
		return null;
	}

	/**
	 * Returns an adjusted image depending on the type of the object passed in.
	 * @return an Image
	 */
	@Override
	public Image getImage(Object obj) {
		TreeObject to = (TreeObject) obj;
		String imageKey = null;
		
		// determine image key
		if (to.getType().equals(TreeObjectType.ROOT)) {
			imageKey = ISharedImages.IMG_DEF_VIEW;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}
		else {
			imageKey = ModelNavigationViewLabelProvider.getImageforTreeObjectType(to.getType());
		}
		
		// retrieve image for key
		Image image;
		if (imageKey == null) {
			// default
			imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			image = PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}
		else  {
			image = images.get(imageKey);
			if (image == null) {
				image = SchemasViewPlugin.getImageDescriptor(
					"/icons/" + imageKey).createImage(); //$NON-NLS-1$
				images.put(imageKey, image);
			}
		}
		
		// legend image
		if (to.getDefinition() != null && (to.getDefinition() instanceof TypeDefinition) 
				&& ((TypeDefinition) to.getDefinition()).hasGeometry() 
				&& to.getPropertyType() instanceof FeatureType) {
			FeatureType type = (FeatureType) to.getPropertyType();
			BufferedImage img = StyleHelper.getLegendImage(type, true);
			if (img != null) {
				// replace image with style image
				ImageData imgData = SwingRcpUtilities.convertToSWT(img);
				image = new Image(Display.getCurrent(), imgData);
				
				String key = to.getName().getURI();
				Image old = null;
				if (styleImages.containsKey(key)) {
					old = styleImages.get(key);
				}
				styleImages.put(key, image);
				if (old != null) {
					old.dispose(); // ok here?
				}
			}
		}
		// check for inline attributes
		else {
			boolean attribute;
			boolean mandatory;
			if (to instanceof AttributeItem) {
				AttributeDefinition def = ((AttributeItem) to).getAttributeDefinition();
				attribute = def.isAttribute();
				mandatory = !def.isNillable() && def.getMinOccurs() > 0;
			}
			else {
				attribute = false;
				mandatory = false;
			}
			boolean def = to.isAttribute() && to.getType() == TreeObjectType.GEOMETRIC_ATTRIBUTE && to.getParent().isType() && isDefaultGeometry((FeatureType) to.getParent().getPropertyType(), to.getName().getLocalPart());
			
			if (def || mandatory || attribute) {
				// overlayed image
				ImageConf conf = new ImageConf(imageKey, attribute, def, mandatory);
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
						if (def) {
							gc.drawImage(defOverlay, 0, 0);
						}
						if (mandatory) {
							gc.drawImage(mandatoryOverlay, 0, 0);
						}
					} finally {
						gc.dispose();
					}
					
					image = copy;
					overlayedImages.put(conf, copy);
				}
				else {
					image = overlayedImage;
				}
			}
		}
		
		return image;
	}

	private boolean isDefaultGeometry(FeatureType type, String propertyName) {
		return type.getGeometryDescriptor().getLocalName().equals(propertyName);
	}

	/**
	 * @see IColorProvider#getBackground(Object)
	 */
	@Override
	public Color getBackground(Object element) {
		return getColor(element, true);
	}
	
	/**
	 * @see IColorProvider#getForeground(Object)
	 */
	@Override
	public Color getForeground(Object element) {
		return getColor(element, false);
	}

	/**
	 * Get the element color
	 * 
	 * @param element the element
	 * @param background if the background color (or the foreground color)
	 *   is requested
	 * @return the color
	 */
	private Color getColor(Object element, boolean background) {
		if (element instanceof TreeObject) {
			Entity entity = ((SchemaItem) element).getEntity();
			
			RGB rgb = getColor(entity, background);
			if (rgb != null) {
				Color color = createdColors.get(rgb);
				if (color == null) {
					color = new Color(Display.getDefault(), rgb);
					createdColors.put(rgb, color);
				}
				return color;
			}
		}
		
		// default
		return null;
	}
	
	/**
	 * Get the entity color
	 * 
	 * @param entity the entity
	 * @param background if the background color (or the foreground color)
	 *   is requested
	 * @return the color's RGB values
	 */
	private RGB getColor(Entity entity, boolean background) {
		if (entity == null) return null;
		
		if (background) { // TODO Symbol or gradient?
			if (entity instanceof FeatureClass) {
				FeatureClass entityFC = (FeatureClass) entity;
				if (entityFC.getAttributeValueCondition() != null 
						&& entityFC.getAttributeValueCondition().get(0) != null) {
					return new RGB(250, 250, 30);
				}
			}
			if (entity instanceof Property) {
				Property entityProperty = (Property) entity;
				if (entityProperty.getValueCondition() != null 
						&& entityProperty.getValueCondition().get(0) != null) {
					return new RGB(250, 250, 30);
				}
			}
		}
		
		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
				AlignmentService.class);
		List<ICell> cells = new ArrayList<ICell>(); //FIXME as.getCell(entity);
		
		List<ITransformation> transformations = new ArrayList<ITransformation>();
		List<ITransformation> augmentations = new ArrayList<ITransformation>();
		for (ICell cell : cells) {
			ITransformation trans = cell.getEntity1().getTransformation();
			if (trans != null) {
				transformations.add(trans);
			}
			ITransformation augm = cell.getEntity2().getTransformation();
			if (augm != null) {
				augmentations.add(augm);
			}
		}
		
		boolean onlyAugmentations = transformations.isEmpty();
		if (onlyAugmentations) {
			transformations = augmentations;
		}
		
		if (background) {
			return getBackgroundColor(transformations, onlyAugmentations);
		}
		else {
			return getForegroundColor(transformations, onlyAugmentations);
		}
	}

	/**
	 * Get the foreground color for an element with the given transformations
	 * 
	 * @param transformations the transformations
	 * @param onlyAugmentations if the transformations are augmentations
	 * @return the color's RGB values, <code>null</code> for the default color
	 */
	protected RGB getForegroundColor(List<ITransformation> transformations, boolean onlyAugmentations) {
		// default color
		return null;
	}

	/**
	 * Get the background color for an element with the given transformations
	 * 
	 * @param transformations the transformations
	 * @param onlyAugmentations if the transformations are augmentations
	 * @return the color's RGB values, <code>null</code> for the default color
	 */
	protected RGB getBackgroundColor(List<ITransformation> transformations, boolean onlyAugmentations) {
		Set<String> names = new HashSet<String>();
		for (ITransformation trans : transformations) {
			names.add(trans.getService().getLocation()); //XXX service is null!
		}
		
		if (!names.isEmpty()) {
//			if (names.contains(RenameFeatureFunction.class.getName())) {
//				return new RGB(150, 190, 120);
//			}
//			else {
				if (onlyAugmentations) {
					// augmented cell
					return new RGB(184, 181, 220);
				}
				else {
					// default color
					return new RGB(190, 220, 170);
				}
//			}
		}
		
		return null;
	}

	/**
	 * @see BaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		for (Color color : createdColors.values()) {
			color.dispose();
		}
		createdColors.clear();
		
		for (Image image : images.values()) {
			image.dispose();
		}
		images.clear();
		
		for (Image image : overlayedImages.values()) {
			image.dispose();
		}
		overlayedImages.clear();
		
		for (Image image : styleImages.values()) {
			image.dispose();
		}
		styleImages.clear();
		
		attribOverlay.dispose();
		defOverlay.dispose();
		mandatoryOverlay.dispose();
			
		super.dispose();
	}
	
	/**
	 * Get the image for a tree object
	 * 
	 * @param tot the tree object type
	 * @return the right image
	 */
	public static String getImageforTreeObjectType(TreeObjectType tot) {
		String imageKey = null;
		if (tot.equals(TreeObjectType.ABSTRACT_FT)) {
			imageKey = "abstract_ft.png"; //$NON-NLS-1$
		}
		else if (tot.equals(TreeObjectType.CONCRETE_FT)) {
			imageKey = "concrete_ft.png"; //$NON-NLS-1$
		}
		else if (tot.equals(TreeObjectType.PROPERTY_TYPE)) {
			//TODO add image for property types
		}
		else if (tot.equals(TreeObjectType.STRING_ATTRIBUTE)) {
			imageKey = "string_attribute.png"; //$NON-NLS-1$
		} 
		else if (tot.equals(TreeObjectType.NUMERIC_ATTRIBUTE)) {
			imageKey = "number_attribute.png"; //$NON-NLS-1$
		}
		else if (tot.equals(TreeObjectType.GEOMETRIC_ATTRIBUTE)) {
			imageKey = "geometry_attribute.png"; //$NON-NLS-1$
		}
		else if (tot.equals(TreeObjectType.COMPLEX_ATTRIBUTE)) {
			// TODO add image for complex attributes
		}
		return imageKey;
	}
	
}
