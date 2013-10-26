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

package eu.esdihumboldt.hale.ui.functions.groovy;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationAccessExtension;
import org.eclipse.jface.text.source.ImageUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Objects;

/**
 * Simple marker annotations implementation.
 * 
 * @author Simon Templer
 */
public class SimpleAnnotations implements IAnnotationAccess, IAnnotationAccessExtension {

	/**
	 * Type for error annotations.
	 */
	public static final String TYPE_ERROR = "error";

	@Override
	public String getTypeLabel(Annotation annotation) {
		switch (annotation.getType()) {
		case TYPE_ERROR:
			return "Error";
		}
		return annotation.getType();
	}

	@Override
	public int getLayer(Annotation annotation) {
		return IAnnotationAccessExtension.DEFAULT_LAYER;
	}

	@Override
	public void paint(Annotation annotation, GC gc, Canvas canvas, Rectangle bounds) {
		Image image = null;

		switch (annotation.getType()) {
		case TYPE_ERROR:
			image = PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
		}

		if (image != null) {
			ImageUtilities.drawImage(image, gc, canvas, bounds, SWT.CENTER, SWT.TOP);
			return;
		}
	}

	@Override
	public boolean isPaintable(Annotation annotation) {
		switch (annotation.getType()) {
		case TYPE_ERROR:
			return true;
		}
		return false;
	}

	@Override
	public boolean isSubtype(Object annotationType, Object potentialSupertype) {
		return Objects.equal(annotationType, potentialSupertype);
	}

	@Override
	public Object[] getSupertypes(Object annotationType) {
		return new Object[] {};
	}

	@Deprecated
	@Override
	public Object getType(Annotation annotation) {
		return annotation.getType();
	}

	@Deprecated
	@Override
	public boolean isMultiLine(Annotation annotation) {
		// TODO Auto-generated method stub
		return false;
	}

	@Deprecated
	@Override
	public boolean isTemporary(Annotation annotation) {
		return false;
	}

}
