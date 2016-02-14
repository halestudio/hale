/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package eu.esdihumboldt.hale.util.nonosgi.contenttype;

import org.eclipse.core.runtime.QualifiedName;

/**
 * @since 3.1
 */
public abstract interface IContentTypeInfo {
	/**
	 * Returns a reference to the corresponding content type. 
	 */
	public abstract ContentType getContentType();

	/**
	 * Returns the default value for the given property, delegating to the
	 * ancestor type if necessary. 
	 */
	public abstract String getDefaultProperty(QualifiedName key);
}
