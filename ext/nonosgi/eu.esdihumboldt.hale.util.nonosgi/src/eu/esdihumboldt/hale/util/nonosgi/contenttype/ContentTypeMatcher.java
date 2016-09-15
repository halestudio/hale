/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
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
import java.util.*;

import org.eclipse.core.internal.content.ContentMessages;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.*;
import org.eclipse.core.runtime.preferences.*;
import org.osgi.service.prefs.BackingStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 3.1
 */
@SuppressWarnings({"restriction", "rawtypes"})
public class ContentTypeMatcher implements IContentTypeMatcher {
	
	private static final Logger log = LoggerFactory.getLogger(ContentTypeMatcher.class);

	private IScopeContext context;
	private IContentTypeManager.ISelectionPolicy policy;

	public ContentTypeMatcher(IContentTypeManager.ISelectionPolicy policy, IScopeContext context) {
		this.policy = policy;
		this.context = context;
	}

	/**
	 * @see IContentTypeMatcher
	 */
	public IContentType findContentTypeFor(InputStream contents, String fileName) throws IOException {
		ContentTypeCatalog currentCatalog = getCatalog();
		IContentType[] all = currentCatalog.findContentTypesFor(this, contents, fileName);
		return all.length > 0 ? new ContentTypeHandler((ContentType) all[0], currentCatalog.getGeneration()) : null;
	}

	/**
	 * @see IContentTypeMatcher
	 */
	public IContentType findContentTypeFor(String fileName) {
		// basic implementation just gets all content types
		ContentTypeCatalog currentCatalog = getCatalog();
		IContentType[] associated = currentCatalog.findContentTypesFor(this, fileName);
		return associated.length == 0 ? null : new ContentTypeHandler((ContentType) associated[0], currentCatalog.getGeneration());
	}

	/**
	 * @see IContentTypeMatcher
	 */
	public IContentType[] findContentTypesFor(InputStream contents, String fileName) throws IOException {
		ContentTypeCatalog currentCatalog = getCatalog();
		IContentType[] types = currentCatalog.findContentTypesFor(this, contents, fileName);
		IContentType[] result = new IContentType[types.length];
		int generation = currentCatalog.getGeneration();
		for (int i = 0; i < result.length; i++)
			result[i] = new ContentTypeHandler((ContentType) types[i], generation);
		return result;
	}

	/**
	 * @see IContentTypeMatcher
	 */
	public IContentType[] findContentTypesFor(String fileName) {
		ContentTypeCatalog currentCatalog = getCatalog();
		IContentType[] types = currentCatalog.findContentTypesFor(this, fileName);
		IContentType[] result = new IContentType[types.length];
		int generation = currentCatalog.getGeneration();
		for (int i = 0; i < result.length; i++)
			result[i] = new ContentTypeHandler((ContentType) types[i], generation);
		return result;
	}

	private ContentTypeCatalog getCatalog() {
		return ContentTypeManager.getInstance().getCatalog();
	}

	/**
	 * @see IContentTypeMatcher
	 */
	public IContentDescription getDescriptionFor(InputStream contents, String fileName, QualifiedName[] options) throws IOException {
		return getCatalog().getDescriptionFor(this, contents, fileName, options);
	}

	/**
	 * @see IContentTypeMatcher
	 */
	public IContentDescription getDescriptionFor(Reader contents, String fileName, QualifiedName[] options) throws IOException {
		return getCatalog().getDescriptionFor(this, contents, fileName, options);
	}

	public IScopeContext getContext() {
		return context;
	}

	public IContentTypeManager.ISelectionPolicy getPolicy() {
		return policy;
	}

	/**
	 * Enumerates all content types whose settings satisfy the given file spec type mask.
	 */
	public Collection getDirectlyAssociated(final ContentTypeCatalog catalog, final String fileSpec, final int typeMask) {
		//TODO: make sure we include built-in associations as well
		final IEclipsePreferences root = context.getNode(ContentTypeManager.CONTENT_TYPE_PREF_NODE);
		final Set result = new HashSet(3);
		try {
			root.accept(new IPreferenceNodeVisitor() {
				@SuppressWarnings("unchecked")
				public boolean visit(IEclipsePreferences node) {
					if (node == root)
						return true;
					String[] fileSpecs = ContentTypeSettings.getFileSpecs(node, typeMask);
					for (int i = 0; i < fileSpecs.length; i++)
						if (fileSpecs[i].equalsIgnoreCase(fileSpec)) {
							ContentType associated = catalog.getContentType(node.name());
							if (associated != null)
								result.add(associated);
							break;
						}
					return false;
				}

			});
		} catch (BackingStoreException bse) {
			log.error(ContentMessages.content_errorLoadingSettings, bse);
		}
		return result == null ? Collections.EMPTY_SET : result;
	}

	public IContentDescription getSpecificDescription(BasicDescription description) {
		if (description == null || ContentTypeManager.getInstance().getContext().equals(getContext()))
			// no need for specific content descriptions
			return description;
		// default description
		if (description instanceof DefaultDescription)
			// return an context specific description instead
			return new DefaultDescription(new ContentTypeSettings((ContentType) description.getContentTypeInfo(), context));
		// non-default description
		// replace info object with context specific settings
		((ContentDescription) description).setContentTypeInfo(new ContentTypeSettings((ContentType) description.getContentTypeInfo(), context));
		return description;
	}
}
