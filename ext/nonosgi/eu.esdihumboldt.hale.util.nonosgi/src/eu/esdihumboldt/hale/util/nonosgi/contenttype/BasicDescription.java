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

import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;

public abstract class BasicDescription implements IContentDescription {

	protected IContentTypeInfo contentTypeInfo;

	public BasicDescription(IContentTypeInfo contentTypeInfo) {
		this.contentTypeInfo = contentTypeInfo;
	}

	/**
	 * @see IContentDescription
	 */
	public IContentType getContentType() {
		ContentType contentType = contentTypeInfo.getContentType();
		//TODO performance: potential creation of garbage		
		return new ContentTypeHandler(contentType, contentType.getCatalog().getGeneration());
	}

	public IContentTypeInfo getContentTypeInfo() {
		return contentTypeInfo;
	}
}
