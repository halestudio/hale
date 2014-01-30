/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.util.source;

import org.eclipse.jface.text.source.AnnotationRulerColumn;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationAccessExtension;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.OverviewRuler;
import org.eclipse.swt.graphics.RGB;

/**
 * Utility methods related to {@link SimpleAnnotations}.
 * 
 * @author Simon Templer
 */
public class SimpleAnnotationUtil {

	/**
	 * Create a default annotation ruler configured for displaying
	 * {@link SimpleAnnotations}.
	 * 
	 * @param annotationModel the annotation model
	 * @return the annotation ruler
	 */
	public static AnnotationRulerColumn createDefaultAnnotationRuler(
			IAnnotationModel annotationModel) {
		AnnotationRulerColumn annotations = new AnnotationRulerColumn(annotationModel, 12,
				new SimpleAnnotations());

		// error
		annotations.addAnnotationType(SimpleAnnotations.TYPE_ERROR);
		// warning
		annotations.addAnnotationType(SimpleAnnotations.TYPE_WARN);
		// information
		annotations.addAnnotationType(SimpleAnnotations.TYPE_INFO);

		annotations.setHover(new DefaultAnnotationHover());
		return annotations;
	}

	/**
	 * Create a default overview ruler configured for displaying
	 * {@link SimpleAnnotations}.
	 * 
	 * @param width the ruler width
	 * @param colorManager the color manager
	 * @param annotationModel the annotation model
	 * @return the overview ruler
	 */
	public static IOverviewRuler createDefaultOverviewRuler(int width,
			ISharedTextColors colorManager, IAnnotationModel annotationModel) {
		IOverviewRuler ruler = new OverviewRuler(new SimpleAnnotations(), width, colorManager);

		// type configuration

		// error (including header)
		ruler.addAnnotationType(SimpleAnnotations.TYPE_ERROR);
		ruler.addHeaderAnnotationType(SimpleAnnotations.TYPE_ERROR);
		ruler.setAnnotationTypeColor(SimpleAnnotations.TYPE_ERROR,
				colorManager.getColor(new RGB(255, 0, 0)));
		ruler.setAnnotationTypeLayer(SimpleAnnotations.TYPE_ERROR,
				IAnnotationAccessExtension.DEFAULT_LAYER);

		// warning
		ruler.addAnnotationType(SimpleAnnotations.TYPE_WARN);
		ruler.setAnnotationTypeColor(SimpleAnnotations.TYPE_WARN,
				colorManager.getColor(new RGB(255, 255, 0)));
		ruler.setAnnotationTypeLayer(SimpleAnnotations.TYPE_WARN,
				IAnnotationAccessExtension.DEFAULT_LAYER);

		// information
		ruler.addAnnotationType(SimpleAnnotations.TYPE_INFO);
		ruler.setAnnotationTypeColor(SimpleAnnotations.TYPE_INFO,
				colorManager.getColor(new RGB(0, 0, 255)));
		ruler.setAnnotationTypeLayer(SimpleAnnotations.TYPE_INFO,
				IAnnotationAccessExtension.DEFAULT_LAYER);

		ruler.setModel(annotationModel);

		return ruler;
	}

}
