/*******************************************************************************
 *  Copyright (c) 2004, 2009 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package eu.esdihumboldt.hale.util.nonosgi.contenttype;

import java.io.InputStream;
import java.io.Reader;

import org.eclipse.core.internal.content.IContentConstants;
import org.eclipse.core.internal.content.ILazySource;
import org.eclipse.core.internal.content.LazyInputStream;
import org.eclipse.core.internal.content.LazyReader;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.content.*;
import org.eclipse.core.runtime.preferences.*;

@SuppressWarnings("restriction")
public class ContentTypeManager extends ContentTypeMatcher implements IContentTypeManager, IRegistryChangeListener {
	private static ContentTypeManager instance;

	public static final int BLOCK_SIZE = 0x400;
	public static final String CONTENT_TYPE_PREF_NODE = IContentConstants.RUNTIME_NAME + IPath.SEPARATOR + "content-types"; //$NON-NLS-1$
	private ContentTypeCatalog catalog;
	private int catalogGeneration;

	/** 
	 * List of registered listeners (element type: 
	 * <code>IContentTypeChangeListener</code>).
	 * These listeners are to be informed when 
	 * something in a content type changes.
	 */
	protected final ListenerList contentTypeListeners = new ListenerList();

	public static void addRegistryChangeListener(IExtensionRegistry registry) {
		if (registry == null)
			return;
		registry.addRegistryChangeListener(getInstance(), IContentConstants.RUNTIME_NAME);
		registry.addRegistryChangeListener(getInstance(), IContentConstants.CONTENT_NAME);
	}

	/**
	 * Shuts down the platform's content type manager. After this call returns,
	 * the content type manager will be closed for business.
	 */
	public static void shutdown() {
		// there really is nothing left to do except null the instance.
		instance = null;
	}

	public static void removeRegistryChangeListener(IExtensionRegistry registry) {
		if (registry == null)
			return;
		getInstance().invalidate();
		registry.removeRegistryChangeListener(getInstance());
	}

	/**
	 * Obtains this platform's content type manager. 
	 * 
	 * @return the content type manager
	 */
	public static ContentTypeManager getInstance() {
		if (instance == null)
			instance = new ContentTypeManager();
		return instance;
	}

	/*
	 * Returns the extension for a file name (omitting the leading '.').
	 */
	static String getFileExtension(String fileName) {
		int dotPosition = fileName.lastIndexOf('.');
		return (dotPosition == -1 || dotPosition == fileName.length() - 1) ? "" : fileName.substring(dotPosition + 1); //$NON-NLS-1$
	}

	protected static ILazySource readBuffer(InputStream contents) {
		return new LazyInputStream(contents, BLOCK_SIZE);
	}

	protected static ILazySource readBuffer(Reader contents) {
		return new LazyReader(contents, BLOCK_SIZE);
	}

	@SuppressWarnings("deprecation")
	public ContentTypeManager() {
		super(null, new InstanceScope());
	}

	protected ContentTypeBuilder createBuilder(ContentTypeCatalog newCatalog) {
		return new ContentTypeBuilder(newCatalog);
	}

	public IContentType[] getAllContentTypes() {
		ContentTypeCatalog currentCatalog = getCatalog();
		IContentType[] types = currentCatalog.getAllContentTypes();
		IContentType[] result = new IContentType[types.length];
		int generation = currentCatalog.getGeneration();
		for (int i = 0; i < result.length; i++)
			result[i] = new ContentTypeHandler((ContentType) types[i], generation);
		return result;
	}

	protected synchronized ContentTypeCatalog getCatalog() {
		if (catalog != null)
			// already has one			
			return catalog;
		// create new catalog 
		ContentTypeCatalog newCatalog = new ContentTypeCatalog(this, catalogGeneration++);
		// build catalog by parsing the extension registry
		ContentTypeBuilder builder = createBuilder(newCatalog);
		try {
			builder.buildCatalog();
			// only remember catalog if building it was successful 
			catalog = newCatalog;
		} catch (InvalidRegistryObjectException e) {
			// the registry has stale objects... just don't remember the returned (incomplete) catalog
		}
		newCatalog.organize();
		return newCatalog;
	}

	public IContentType getContentType(String contentTypeIdentifier) {
		ContentTypeCatalog currentCatalog = getCatalog();
		ContentType type = currentCatalog.getContentType(contentTypeIdentifier);
		return type == null ? null : new ContentTypeHandler(type, currentCatalog.getGeneration());
	}

	public IContentTypeMatcher getMatcher(final ISelectionPolicy customPolicy, final IScopeContext context) {
		return new ContentTypeMatcher(customPolicy, context == null ? getContext() : context);
	}

	IEclipsePreferences getPreferences() {
		return getPreferences(getContext());
	}

	IEclipsePreferences getPreferences(IScopeContext context) {
		return context.getNode(CONTENT_TYPE_PREF_NODE);
	}

	public void registryChanged(IRegistryChangeEvent event) {
		// no changes related to the content type registry
		if (event.getExtensionDeltas(IContentConstants.RUNTIME_NAME, ContentTypeBuilder.PT_CONTENTTYPES).length == 0 && 
			event.getExtensionDeltas(IContentConstants.CONTENT_NAME, ContentTypeBuilder.PT_CONTENTTYPES).length == 0)
			return;
		invalidate();
	}

	/**
	 * Causes a new catalog to be built afresh next time an API call is made.
	 */
	synchronized void invalidate() {
		catalog = null;
	}

	/* (non-Javadoc)
	 * @see IContentTypeManager#addContentTypeChangeListener(IContentTypeChangeListener)
	 */
	public void addContentTypeChangeListener(IContentTypeChangeListener listener) {
		contentTypeListeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see IContentTypeManager#removeContentTypeChangeListener(IContentTypeChangeListener)
	 */
	public void removeContentTypeChangeListener(IContentTypeChangeListener listener) {
		contentTypeListeners.remove(listener);
	}

	public void fireContentTypeChangeEvent(ContentType type) {
		Object[] listeners = this.contentTypeListeners.getListeners();
		IContentType eventObject = new ContentTypeHandler(type, type.getCatalog().getGeneration());
		for (int i = 0; i < listeners.length; i++) {
			final ContentTypeChangeEvent event = new ContentTypeChangeEvent(eventObject);
			final IContentTypeChangeListener listener = (IContentTypeChangeListener) listeners[i];
			ISafeRunnable job = new ISafeRunnable() {
				public void handleException(Throwable exception) {
					// already logged in SafeRunner#run()
				}

				public void run() throws Exception {
					listener.contentTypeChanged(event);
				}
			};
			SafeRunner.run(job);
		}
	}

	public IContentDescription getSpecificDescription(BasicDescription description) {
		// this is the platform content type manager, no specificities
		return description;
	}

	@Override
	public IContentType addContentType(String contentTypeIdentifier, String name, IContentType baseType)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeContentType(String contentTypeIdentifier) throws CoreException {
		// TODO Auto-generated method stub
		
	}
}
