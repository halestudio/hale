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

import java.io.*;
import java.lang.ref.SoftReference;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.*;
import org.eclipse.core.runtime.preferences.IScopeContext;

/**
 * The only content types exposed to clients. Allows the content type registry to change 
 * underneath preserving handlers kept by clients.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ContentTypeHandler implements IContentType {

	/**
	 * A dummy description object to be returned by getDescription when this 
	 * handler's target cannot be determined. 
	 */
	private class DummyContentDescription implements IContentDescription {
		public String getCharset() {
			return null;
		}

		public IContentType getContentType() {
			return ContentTypeHandler.this;
		}

		public Object getProperty(QualifiedName key) {
			return null;
		}

		public boolean isRequested(QualifiedName key) {
			return false;
		}

		public void setProperty(QualifiedName key, Object value) {
			// don't do anything
		}
	}

	private int generation;
	String id;
	private SoftReference targetRef;

	ContentTypeHandler(ContentType target, int generation) {
		this.id = target.getId();
		this.targetRef = new SoftReference(target);
		this.generation = generation;
	}

	public void addFileSpec(String fileSpec, int type) throws CoreException {
		final IContentType target = getTarget();
		if (target != null)
			target.addFileSpec(fileSpec, type);
	}

	public boolean equals(Object another) {
		if (another instanceof ContentType)
			return id.equals(((ContentType) another).id);
		if (another instanceof ContentTypeHandler)
			return id.equals(((ContentTypeHandler) another).id);
		return false;
	}

	public IContentType getBaseType() {
		final ContentType target = getTarget();
		if (target == null)
			return null;
		final ContentType baseType = (ContentType) target.getBaseType();
		return (baseType != null) ? new ContentTypeHandler(baseType, baseType.getCatalog().getGeneration()) : null;
	}

	public String getDefaultCharset() {
		final IContentType target = getTarget();
		return (target != null) ? target.getDefaultCharset() : null;
	}

	public IContentDescription getDefaultDescription() {
		final IContentType target = getTarget();
		return (target != null) ? target.getDefaultDescription() : new DummyContentDescription();
	}

	public IContentDescription getDescriptionFor(InputStream contents, QualifiedName[] options) throws IOException {
		final IContentType target = getTarget();
		return (target != null) ? target.getDescriptionFor(contents, options) : null;
	}

	public IContentDescription getDescriptionFor(Reader contents, QualifiedName[] options) throws IOException {
		final IContentType target = getTarget();
		return (target != null) ? target.getDescriptionFor(contents, options) : null;
	}

	public String[] getFileSpecs(int type) {
		final IContentType target = getTarget();
		return (target != null) ? target.getFileSpecs(type) : new String[0];
	}

	public String getId() {
		return id;
	}

	public String getName() {
		final IContentType target = getTarget();
		return (target != null) ? target.getName() : id;
	}

	public IContentTypeSettings getSettings(IScopeContext context) throws CoreException {
		final ContentType target = getTarget();
		if (target == null)
			return null;
		// the content type may returned itself as the settings object (instance scope context)
		final IContentTypeSettings settings = target.getSettings(context);
		// in that case, return this same handler; otherwise, just return the settings 
		return settings == target ? this : settings;
	}

	/**
	 * Returns the content type this handler represents. 
	 * Note that this handles the case of aliasing.
	 * 
	 * Public for testing purposes only.
	 */
	public ContentType getTarget() {
		ContentType target = (ContentType) targetRef.get();
		ContentTypeCatalog catalog = ContentTypeManager.getInstance().getCatalog();
		if (target == null || catalog.getGeneration() != generation) {
			target = catalog.getContentType(id);
			targetRef = new SoftReference(target);
			generation = catalog.getGeneration();
		}
		return target == null ? null : target.getAliasTarget(true);
	}

	public int hashCode() {
		return id.hashCode();
	}

	public boolean isAssociatedWith(String fileName) {
		final IContentType target = getTarget();
		return (target != null) ? target.isAssociatedWith(fileName) : false;
	}

	public boolean isAssociatedWith(String fileName, IScopeContext context) {
		final IContentType target = getTarget();
		return (target != null) ? target.isAssociatedWith(fileName, context) : false;
	}

	public boolean isKindOf(IContentType another) {
		if (another instanceof ContentTypeHandler)
			another = ((ContentTypeHandler) another).getTarget();
		final IContentType target = getTarget();
		return (target != null) ? target.isKindOf(another) : false;
	}

	public void removeFileSpec(String fileSpec, int type) throws CoreException {
		final IContentType target = getTarget();
		if (target != null)
			target.removeFileSpec(fileSpec, type);
	}

	public void setDefaultCharset(String userCharset) throws CoreException {
		final IContentType target = getTarget();
		if (target != null)
			target.setDefaultCharset(userCharset);
	}

	public String toString() {
		return id;
	}

	@Override
	public boolean isUserDefined() {
		// TODO Auto-generated method stub
		return false;
	}

}
