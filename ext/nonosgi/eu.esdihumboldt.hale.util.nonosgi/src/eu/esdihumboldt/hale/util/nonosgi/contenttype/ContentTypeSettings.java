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

import java.util.List;

import org.eclipse.core.internal.content.ContentMessages;
import org.eclipse.core.internal.content.Util;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentTypeSettings;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.osgi.util.NLS;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

@SuppressWarnings({"restriction", "rawtypes"})
public class ContentTypeSettings implements IContentTypeSettings, IContentTypeInfo {

	private ContentType contentType;
	private IScopeContext context;

	@SuppressWarnings("unchecked")
	static void addFileSpec(IScopeContext context, String contentTypeId, String fileSpec, int type) throws CoreException {
		Preferences contentTypeNode = ContentTypeManager.getInstance().getPreferences(context).node(contentTypeId);
		String key = ContentType.getPreferenceKey(type);
		List existingValues = Util.parseItemsIntoList(contentTypeNode.get(key, null));
		for (int i = 0; i < existingValues.size(); i++)
			if (((String) existingValues.get(i)).equalsIgnoreCase(fileSpec))
				// don't do anything if already exists
				return;
		existingValues.add(fileSpec);
		// set new preference value		
		String newValue = Util.toListString(existingValues.toArray());
		ContentType.setPreference(contentTypeNode, key, newValue);
		try {
			contentTypeNode.flush();
		} catch (BackingStoreException bse) {
			String message = NLS.bind(ContentMessages.content_errorSavingSettings, contentTypeId);
			IStatus status = new Status(IStatus.ERROR, ContentMessages.OWNER_NAME, 0, message, bse);
			throw new CoreException(status);
		}
	}

	static String[] getFileSpecs(IScopeContext context, String contentTypeId, int type) {
		Preferences contentTypeNode = ContentTypeManager.getInstance().getPreferences(context).node(contentTypeId);
		return getFileSpecs(contentTypeNode, type);
	}

	static String[] getFileSpecs(Preferences contentTypeNode, int type) {
		String key = ContentType.getPreferenceKey(type);
		String existing = contentTypeNode.get(key, null);
		return Util.parseItems(existing);
	}

	public static String internalGetDefaultProperty(ContentType current, final Preferences contentTypePrefs, final QualifiedName key) throws BackingStoreException {
		String id = current.getId();
		if (contentTypePrefs.nodeExists(id)) {
			Preferences contentTypeNode = contentTypePrefs.node(id);
			String propertyValue = contentTypeNode.get(key.getLocalName(), null);
			if (propertyValue != null)
				return propertyValue;
		}
		// try built-in settings
		String propertyValue = current.basicGetDefaultProperty(key);
		if (propertyValue != null)
			return propertyValue;
		// try ancestor
		ContentType baseType = (ContentType) current.getBaseType();
		return baseType == null ? null : internalGetDefaultProperty(baseType, contentTypePrefs, key);
	}

	static void removeFileSpec(IScopeContext context, String contentTypeId, String fileSpec, int type) throws CoreException {
		Preferences contentTypeNode = ContentTypeManager.getInstance().getPreferences(context).node(contentTypeId);
		String key = ContentType.getPreferenceKey(type);
		String existing = contentTypeNode.get(key, null);
		if (existing == null)
			// content type has no settings - nothing to do
			return;
		List existingValues = Util.parseItemsIntoList(contentTypeNode.get(key, null));
		int index = -1;
		int existingCount = existingValues.size();
		for (int i = 0; index == -1 && i < existingCount; i++)
			if (((String) existingValues.get(i)).equalsIgnoreCase(fileSpec))
				index = i;
		if (index == -1)
			// did not find the file spec to be removed - nothing to do
			return;
		existingValues.remove(index);
		// set new preference value
		String newValue = Util.toListString(existingValues.toArray());
		ContentType.setPreference(contentTypeNode, key, newValue);
		try {
			contentTypeNode.flush();
		} catch (BackingStoreException bse) {
			String message = NLS.bind(ContentMessages.content_errorSavingSettings, contentTypeId);
			IStatus status = new Status(IStatus.ERROR, ContentMessages.OWNER_NAME, 0, message, bse);
			throw new CoreException(status);
		}
	}

	public ContentTypeSettings(ContentType contentType, IScopeContext context) {
		this.context = context;
		this.contentType = contentType;
	}

	/*
	 * @see IContentTypeSettings 
	 */
	public void addFileSpec(String fileSpec, int type) throws CoreException {
		addFileSpec(context, contentType.getId(), fileSpec, type);
	}

	public ContentType getContentType() {
		return contentType;
	}

	public String getDefaultCharset() {
		return getDefaultProperty(IContentDescription.CHARSET);
	}

	public String getDefaultProperty(final QualifiedName key) {
		final Preferences contentTypePrefs = ContentTypeManager.getInstance().getPreferences(context);
		try {
			String propertyValue = internalGetDefaultProperty(contentType, contentTypePrefs, key);
			return "".equals(propertyValue) ? null : propertyValue; //$NON-NLS-1$
		} catch (BackingStoreException e) {
			return null;
		}
	}

	public String[] getFileSpecs(int type) {
		return getFileSpecs(context, contentType.getId(), type);
	}

	public String getId() {
		return contentType.getId();
	}

	public void removeFileSpec(String fileSpec, int type) throws CoreException {
		removeFileSpec(context, contentType.getId(), fileSpec, type);
	}

	public void setDefaultCharset(String userCharset) throws CoreException {
		Preferences contentTypeNode = ContentTypeManager.getInstance().getPreferences(context).node(contentType.getId());
		ContentType.setPreference(contentTypeNode, ContentType.PREF_DEFAULT_CHARSET, userCharset);
		try {
			contentTypeNode.flush();
		} catch (BackingStoreException bse) {
			String message = NLS.bind(ContentMessages.content_errorSavingSettings, contentType.getId());
			IStatus status = new Status(IStatus.ERROR, ContentMessages.OWNER_NAME, 0, message, bse);
			throw new CoreException(status);
		}
	}

	@Override
	public boolean isUserDefined() {
		// TODO Auto-generated method stub
		return false;
	}

}
