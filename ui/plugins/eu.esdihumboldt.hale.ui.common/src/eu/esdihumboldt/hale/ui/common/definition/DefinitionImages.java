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

package eu.esdihumboldt.hale.ui.common.definition;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.geotools.map.legend.Drawer;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.Symbolizer;
import org.opengis.feature.simple.SimpleFeature;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.schema.Classification;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.CommonSharedImagesConstants;
import eu.esdihumboldt.hale.ui.common.internal.CommonUIPlugin;
import eu.esdihumboldt.hale.ui.common.service.population.Population;
import eu.esdihumboldt.hale.ui.common.service.population.PopulationService;
import eu.esdihumboldt.hale.ui.common.service.style.StyleService;
import eu.esdihumboldt.hale.ui.geometry.DefaultGeometryUtil;
import eu.esdihumboldt.hale.ui.geometry.service.GeometrySchemaService;
import eu.esdihumboldt.hale.ui.util.swing.SwingRcpUtilities;

/**
 * Manages images for definitions. Should be {@link #dispose()}d when the images
 * are not used any more.
 * 
 * @author Simon Templer
 */
public class DefinitionImages implements CommonSharedImagesConstants {

	// constants for style images

	private static final int WIDTH = 16;

	private static final int HEIGHT = 16;

	private static final int[] LINE_POINTS = new int[] { 0, HEIGHT - 1, WIDTH - 1, 0 };

	private static final int[] POLY_POINTS = new int[] { 0, 0, WIDTH - 1, 0, WIDTH - 1, HEIGHT - 1,
			0, HEIGHT - 1 };

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
		 * @param def if the image is to be marked a default (e.g. default
		 *            geometry)
		 * @param mandatory if the image is to be marked as mandatory
		 * @param faded if the image is displayed faded
		 */
		public ImageConf(String identifier, boolean attribute, boolean def, boolean mandatory,
				boolean faded) {
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
			result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
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
			}
			else if (!identifier.equals(other.identifier))
				return false;
			if (mandatory != other.mandatory)
				return false;
			return true;
		}

	}

	private final Image attribOverlay = CommonUIPlugin
			.getImageDescriptor("/icons/attrib_overlay2.gif").createImage(); //$NON-NLS-1$

	private final Image defOverlay = CommonUIPlugin.getImageDescriptor("/icons/def_overlay.gif") //$NON-NLS-1$
			.createImage();

	private final Image mandatoryOverlay = CommonUIPlugin
			.getImageDescriptor("/icons/mandatory_ov2.gif").createImage(); //$NON-NLS-1$

	private final Map<ImageConf, Image> overlayedImages = new HashMap<ImageConf, Image>();

	private final Map<String, Image> styleImages = new HashMap<String, Image>();

	private boolean suppressMandatory = false;

	private boolean showStyleLegend = true;

	private final ImageHandles handles = new ImageHandles();

	/**
	 * Dispose all images. {@link #getImage(Definition)} may not be called after
	 * calling this method.
	 */
	public void dispose() {
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
	}

	/**
	 * Get the image for the given definition
	 * 
	 * @param def the definition
	 * @return the image, may be <code>null</code>
	 */
	public Image getImage(Definition<?> def) {
		return getImage(null, def);
	}

	/**
	 * Get the image for the given entity definition
	 * 
	 * @param entityDef the entity definition
	 * @return the image, may be <code>null</code>
	 */
	public Image getImage(EntityDefinition entityDef) {
		return getImage(entityDef, entityDef.getDefinition());
	}

	/**
	 * Determines if a type definition has a geometry.
	 * 
	 * @param type the type definition to test
	 * @return <code>true</code> if the type has a geometry, false otherwise
	 */
	protected boolean hasGeometry(TypeDefinition type) {
		GeometrySchemaService gss = PlatformUI.getWorkbench()
				.getService(GeometrySchemaService.class);

		return gss.getDefaultGeometry(type) != null;
	}

	/**
	 * Get a legend image for a given type definition
	 * 
	 * @param type the type definition
	 * @param dataSet the data set the type definition belongs to
	 * @param definedOnly if only for defined styles a image shall be created
	 * @return the legend image or <code>null</code>
	 */
	protected BufferedImage getLegendImage(TypeDefinition type, DataSet dataSet,
			boolean definedOnly) {
		StyleService ss = PlatformUI.getWorkbench().getService(StyleService.class);
		Style style = (definedOnly) ? (ss.getDefinedStyle(type)) : (ss.getStyle(type, dataSet));
		if (style == null) {
			return null;
		}

		// create a dummy feature based on the style
		Drawer d = Drawer.create();
		SimpleFeature feature = null;
		Symbolizer[] symbolizers = SLD.symbolizers(style);
		if (symbolizers.length > 0) {
			Symbolizer symbolizer = symbolizers[0];

			if (symbolizer instanceof LineSymbolizer) {
				feature = d.feature(d.line(LINE_POINTS));
			}
			else if (symbolizer instanceof PointSymbolizer) {
				feature = d.feature(d.point(WIDTH / 2, HEIGHT / 2));
			}
			if (symbolizer instanceof PolygonSymbolizer) {
				feature = d.feature(d.polygon(POLY_POINTS));
			}
		}

		if (feature != null) {
			BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
//				GraphicsEnvironment.getLocalGraphicsEnvironment().
//    				getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(WIDTH, HEIGHT,
//    				Transparency.TRANSLUCENT);

			// use white background to have a neutral color even if selected
			Color bg = Color.WHITE;
			Graphics2D g = image.createGraphics();
			try {
				g.setColor(bg);
				g.fillRect(0, 0, WIDTH, HEIGHT);
			} finally {
				g.dispose();
			}

			d.drawDirect(image, feature, style);
			return image;
		}

		return null;
	}

	/**
	 * Get the image for the given definition
	 * 
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
		else {
			image = CommonSharedImages.getImageRegistry().get(imageName);
		}

		// legend image
		// XXX not supported yet
		if (def instanceof TypeDefinition
				&& !((TypeDefinition) def).getConstraint(AbstractFlag.class).isEnabled()
				&& hasGeometry((TypeDefinition) def)) {
			TypeDefinition type = (TypeDefinition) def;

			DataSet dataSet = DataSet.SOURCE;
			// XXX dataSet: how to find out? - does not matter with only using
			// defined styles
			if (entityDef != null) {
				dataSet = DataSet.forSchemaSpace(entityDef.getSchemaSpace());
			}
			String typeKey = dataSet.name() + "::" + type.getIdentifier();

			// XXX check if style image is already there?
			// XXX how to handle style changes?

			BufferedImage img = getLegendImage(type, dataSet, true);
			if (img != null) {
				// replace image with style image
				ImageData imgData = SwingRcpUtilities.convertToSWT(img);
				image = new Image(Display.getCurrent(), imgData);

				final Image old;
				if (styleImages.containsKey(typeKey)) {
					old = styleImages.get(typeKey);
				}
				else {
					old = null;
				}
				styleImages.put(typeKey, image);
				if (old != null) {
					// schedule image to be disposed when there are no
					// references to it
					handles.addReference(image);
				}
			}
		}
		// check for inline attributes
		else {
			boolean attribute = (def instanceof PropertyDefinition)
					&& ((PropertyDefinition) def).getConstraint(XmlAttributeFlag.class).isEnabled();
			boolean mandatory = false;
			if (!suppressMandatory) {
				if (def instanceof PropertyDefinition) {
					Cardinality cardinality = ((PropertyDefinition) def)
							.getConstraint(Cardinality.class);
					mandatory = cardinality.getMinOccurs() > 0 && !((PropertyDefinition) def)
							.getConstraint(NillableFlag.class).isEnabled();
				}
				else if (def instanceof GroupPropertyDefinition) {
					Cardinality cardinality = ((GroupPropertyDefinition) def)
							.getConstraint(Cardinality.class);
					mandatory = cardinality.getMinOccurs() > 0;
				}
			}

			boolean deflt = false;
			boolean faded = false;
			if (entityDef != null) {
				// entity definition needed to determine if item is a default
				// geometry
				deflt = DefaultGeometryUtil.isDefaultGeometry(entityDef);

				// and to determine population
				PopulationService ps = PlatformUI.getWorkbench()
						.getService(PopulationService.class);
				if (ps != null && ps.hasPopulation(entityDef.getSchemaSpace())) {
					Population pop = ps.getPopulation(entityDef);
					faded = (pop != null && pop.getOverallCount() == 0);
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
		}

		return image;
	}

	/**
	 * Get the image name for the given classification. The image name
	 * represents a file residing in the icons folder.
	 * 
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

	/**
	 * @return the showStyleLegend
	 */
	public boolean isShowStyleLegend() {
		return showStyleLegend;
	}

	/**
	 * @param showStyleLegend the showStyleLegend to set
	 */
	public void setShowStyleLegend(boolean showStyleLegend) {
		this.showStyleLegend = showStyleLegend;
	}

}
