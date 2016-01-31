/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
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

import org.eclipse.core.internal.content.ILazySource;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.content.*;
import org.eclipse.core.runtime.content.IContentTypeManager.ISelectionPolicy;
import org.eclipse.core.runtime.preferences.IScopeContext;

@SuppressWarnings({"restriction", "rawtypes", "unchecked", "deprecation"})
public final class ContentTypeCatalog {
	private static final IContentType[] NO_CONTENT_TYPES = new IContentType[0];

	/**
	 * All fields are guarded by lock on "this"
	 */
	private final Map allChildren = new HashMap();
	private final Map contentTypes = new HashMap();
	private final Map fileExtensions = new HashMap();
	private final Map fileNames = new HashMap();
	private int generation;
	private ContentTypeManager manager;

	/**
	 * A sorting policy where the more generic content type wins. Lexicographical comparison is done
	 * as a last resort when all other criteria fail.  
	 */
	private final Comparator policyConstantGeneralIsBetter = new Comparator() {
		public int compare(Object o1, Object o2) {
			ContentType type1 = (ContentType) o1;
			ContentType type2 = (ContentType) o2;
			// first criteria: depth - the lower, the better
			int depthCriteria = type1.getDepth() - type2.getDepth();
			if (depthCriteria != 0)
				return depthCriteria;
			// second criteria: priority - the higher, the better
			int priorityCriteria = type1.getPriority() - type2.getPriority();
			if (priorityCriteria != 0)
				return -priorityCriteria;
			// they have same depth and priority - choose one arbitrarily (stability is important)
			return type1.getId().compareTo(type2.getId());
		}
	};

	/**
	 * A sorting policy where the more specific content type wins. Lexicographical comparison is done
	 * as a last resort when all other criteria fail.  
	 */
	private Comparator policyConstantSpecificIsBetter = new Comparator() {
		public int compare(Object o1, Object o2) {
			ContentType type1 = (ContentType) o1;
			ContentType type2 = (ContentType) o2;
			// first criteria: depth - the higher, the better
			int depthCriteria = type1.getDepth() - type2.getDepth();
			if (depthCriteria != 0)
				return -depthCriteria;
			// second criteria: priority - the higher, the better
			int priorityCriteria = type1.getPriority() - type2.getPriority();
			if (priorityCriteria != 0)
				return -priorityCriteria;
			// they have same depth and priority - choose one arbitrarily (stability is important)
			return type1.getId().compareTo(type2.getId());
		}
	};

	/**
	 * A sorting policy where the more general content type wins.  
	 */
	private Comparator policyGeneralIsBetter = new Comparator() {
		public int compare(Object o1, Object o2) {
			ContentType type1 = (ContentType) o1;
			ContentType type2 = (ContentType) o2;
			// first criteria: depth - the lower, the better
			int depthCriteria = type1.getDepth() - type2.getDepth();
			if (depthCriteria != 0)
				return depthCriteria;
			// second criteria: priority - the higher, the better
			int priorityCriteria = type1.getPriority() - type2.getPriority();
			if (priorityCriteria != 0)
				return -priorityCriteria;
			return 0;
		}
	};

	/**
	 * A sorting policy where content types are sorted by id.
	 */
	private Comparator policyLexicographical = new Comparator() {
		public int compare(Object o1, Object o2) {
			ContentType type1 = (ContentType) o1;
			ContentType type2 = (ContentType) o2;
			return type1.getId().compareTo(type2.getId());
		}
	};
	/**
	 * A sorting policy where the more specific content type wins.  
	 */
	private Comparator policySpecificIsBetter = new Comparator() {
		public int compare(Object o1, Object o2) {
			ContentType type1 = (ContentType) o1;
			ContentType type2 = (ContentType) o2;
			// first criteria: depth - the higher, the better
			int depthCriteria = type1.getDepth() - type2.getDepth();
			if (depthCriteria != 0)
				return -depthCriteria;
			// second criteria: priority - the higher, the better
			int priorityCriteria = type1.getPriority() - type2.getPriority();
			if (priorityCriteria != 0)
				return -priorityCriteria;
			return 0;
		}
	};

	private static IContentType[] concat(IContentType[][] types) {
		if (types[0].length == 0)
			return types[1];
		if (types[1].length == 0)
			return types[0];
		IContentType[] result = new IContentType[types[0].length + types[1].length];
		System.arraycopy(types[0], 0, result, 0, types[0].length);
		System.arraycopy(types[1], 0, result, types[0].length, types[1].length);
		return result;
	}

	public ContentTypeCatalog(ContentTypeManager manager, int generation) {
		this.manager = manager;
		this.generation = generation;
	}

	synchronized void addContentType(IContentType contentType) {
		contentTypes.put(contentType.getId(), contentType);
	}

	/**
	 * Applies a client-provided selection policy. 
	 */
	private IContentType[] applyPolicy(final IContentTypeManager.ISelectionPolicy policy, final IContentType[] candidates, final boolean fileName, final boolean contents) {
		final IContentType[][] result = new IContentType[][] {candidates};
		SafeRunner.run(new ISafeRunnable() {
			public void handleException(Throwable exception) {
				// already logged in SafeRunner#run()
				// default result is the original array
				// nothing to be done
			}

			public void run() throws Exception {
				result[0] = policy.select(candidates, fileName, contents);
			}
		});
		return result[0];
	}

	private void associate(ContentType contentType) {
		String[] builtInFileNames = contentType.getFileSpecs(IContentType.IGNORE_USER_DEFINED | IContentType.FILE_NAME_SPEC);
		for (int i = 0; i < builtInFileNames.length; i++)
			associate(contentType, builtInFileNames[i], IContentType.FILE_NAME_SPEC);
		String[] builtInFileExtensions = contentType.getFileSpecs(IContentType.IGNORE_USER_DEFINED | IContentType.FILE_EXTENSION_SPEC);
		for (int i = 0; i < builtInFileExtensions.length; i++)
			associate(contentType, builtInFileExtensions[i], IContentType.FILE_EXTENSION_SPEC);
	}

	synchronized void associate(ContentType contentType, String text, int type) {
		Map fileSpecMap = ((type & IContentType.FILE_NAME_SPEC) != 0) ? fileNames : fileExtensions;
		String mappingKey = FileSpec.getMappingKeyFor(text);
		Set existing = (Set) fileSpecMap.get(mappingKey);
		if (existing == null)
			fileSpecMap.put(mappingKey, existing = new HashSet());
		existing.add(contentType);
	}

	private int collectMatchingByContents(int valid, IContentType[] subset, List destination, ILazySource contents, Map properties) throws IOException {
		for (int i = 0; i < subset.length; i++) {
			ContentType current = (ContentType) subset[i];
			IContentDescriber describer = current.getDescriber();
			int status = IContentDescriber.INDETERMINATE;
			if (describer != null) {
				if (contents.isText() && !(describer instanceof ITextContentDescriber))
					// for text streams we skip content types that do not provide text-based content describers
					continue;
				status = describe(current, contents, null, properties);
				if (status == IContentDescriber.INVALID)
					continue;
			}
			if (status == IContentDescriber.VALID)
				destination.add(valid++, current);
			else
				destination.add(current);
		}
		return valid;
	}

	int describe(ContentType type, ILazySource contents, ContentDescription description, Map properties) throws IOException {
		IContentDescriber describer = type.getDescriber();
		try {
			if (contents.isText()) {
				if (describer instanceof eu.esdihumboldt.hale.util.nonosgi.contenttype.describer.XMLRootElementContentDescriber2) {
					return ((eu.esdihumboldt.hale.util.nonosgi.contenttype.describer.XMLRootElementContentDescriber2) describer).describe((Reader) contents, description, properties);
				} else if (describer instanceof XMLRootElementContentDescriber2) {
					// replace with new instance that works also if not in OSGi
					return (new eu.esdihumboldt.hale.util.nonosgi.contenttype.describer.XMLRootElementContentDescriber2().describe((Reader) contents, description, properties));
				} else if (describer instanceof XMLRootElementContentDescriber) {
					return ((XMLRootElementContentDescriber) describer).describe((Reader) contents, description, properties);
				}
				return ((ITextContentDescriber) describer).describe((Reader) contents, description);
			} else {
				if (describer instanceof eu.esdihumboldt.hale.util.nonosgi.contenttype.describer.XMLRootElementContentDescriber2) {
					return ((eu.esdihumboldt.hale.util.nonosgi.contenttype.describer.XMLRootElementContentDescriber2) describer).describe((InputStream) contents, description, properties);
				} else if (describer instanceof XMLRootElementContentDescriber2) {
					// replace with new instance that works also if not in OSGi
					return (new eu.esdihumboldt.hale.util.nonosgi.contenttype.describer.XMLRootElementContentDescriber2().describe((InputStream) contents, description, properties));
				} else if (describer instanceof XMLRootElementContentDescriber) {
					return ((XMLRootElementContentDescriber) describer).describe((InputStream) contents, description, properties);
				}
				return (describer).describe((InputStream) contents, description);
			}
		} catch (RuntimeException re) {
			// describer seems to be buggy. just disable it (logging the reason)
			type.invalidateDescriber(re);
		} catch (Error e) {
			// describer got some serious problem. disable it (logging the reason) and throw the error again 
			type.invalidateDescriber(e);
			throw e;
//		} catch (LowLevelIOException llioe) {
//			// throw the actual exception
//			throw llioe.getActualException();
		} catch (IOException ioe) {
			// we don't know what the describer would say if the exception didn't occur
			return IContentDescriber.INDETERMINATE;
		} finally {
			contents.rewind();
		}
		return IContentDescriber.INVALID;
	}

	synchronized void dissociate(ContentType contentType, String text, int type) {
		Map fileSpecMap = ((type & IContentType.FILE_NAME_SPEC) != 0) ? fileNames : fileExtensions;
		String mappingKey = FileSpec.getMappingKeyFor(text);
		Set existing = (Set) fileSpecMap.get(mappingKey);
		if (existing == null)
			return;
		existing.remove(contentType);
	}

	/**
	 * A content type will be valid if:
	 * <ol>
	 * <li>it does not designate a base type, or</li>
	 * <li>it designates a base type that exists and is valid</li>
	 * </ol>
	 * <p>And</p>:
	 * <ol>
	 * <li>it does not designate an alias type, or</li>
	 * <li>it designates an alias type that does not exist, or</li>
	 * <li>it designates an alias type that exists and is valid</li>
	 * </ol> 
	 */
	private boolean ensureValid(ContentType type) {
		if (type.getValidation() != ContentType.STATUS_UNKNOWN)
			// already processed
			return type.isValid();
		// set this type temporarily as invalid to prevent cycles
		// all types in a cycle would remain as invalid
		type.setValidation(ContentType.STATUS_INVALID);
		if (type.isAlias())
			// it is an alias, leave as invalid
			return false;
		// check base type
		ContentType baseType = null;
		if (type.getBaseTypeId() != null) {
			baseType = (ContentType) contentTypes.get(type.getBaseTypeId());
			if (baseType == null)
				// invalid: specified base type is not known
				return false;
			// base type exists, ensure it is valid
			baseType = baseType.getAliasTarget(true);
			ensureValid(baseType);
			if (baseType.getValidation() != ContentType.STATUS_VALID)
				// invalid: base type was invalid
				return false;
		}
		// valid: all conditions satisfied
		type.setValidation(ContentType.STATUS_VALID);
		type.setBaseType(baseType);
		return true;
	}

	IContentType[] findContentTypesFor(ContentTypeMatcher matcher, InputStream contents, String fileName) throws IOException {
		final ILazySource buffer = ContentTypeManager.readBuffer(contents);
		IContentType[] selected = internalFindContentTypesFor(matcher, buffer, fileName, true);
		// give the policy a chance to change the results
		ISelectionPolicy policy = matcher.getPolicy();
		if (policy != null)
			selected = applyPolicy(policy, selected, fileName != null, true);
		return selected;
	}

	IContentType[] findContentTypesFor(ContentTypeMatcher matcher, final String fileName) {
		IContentType[] selected = concat(internalFindContentTypesFor(matcher, fileName, policyConstantGeneralIsBetter));
		// give the policy a chance to change the results
		ISelectionPolicy policy = matcher.getPolicy();
		if (policy != null)
			selected = applyPolicy(policy, selected, true, false);
		return selected;
	}

	synchronized public IContentType[] getAllContentTypes() {
		List result = new ArrayList(contentTypes.size());
		for (Iterator i = contentTypes.values().iterator(); i.hasNext();) {
			ContentType type = (ContentType) i.next();
			if (type.isValid() && !type.isAlias())
				result.add(type);
		}
		return (IContentType[]) result.toArray(new IContentType[result.size()]);
	}

	private ContentType[] getChildren(ContentType parent) {
		ContentType[] children = (ContentType[]) allChildren.get(parent);
		if (children != null)
			return children;
		List result = new ArrayList(5);
		for (Iterator i = this.contentTypes.values().iterator(); i.hasNext();) {
			ContentType next = (ContentType) i.next();
			if (next.getBaseType() == parent)
				result.add(next);
		}
		children = (ContentType[]) result.toArray(new ContentType[result.size()]);
		allChildren.put(parent, children);
		return children;
	}

	public ContentType getContentType(String contentTypeIdentifier) {
		ContentType type = internalGetContentType(contentTypeIdentifier);
		return (type != null && type.isValid() && !type.isAlias()) ? type : null;
	}

	private IContentDescription getDescriptionFor(ContentTypeMatcher matcher, ILazySource contents, String fileName, QualifiedName[] options) throws IOException {
		IContentType[] selected = internalFindContentTypesFor(matcher, contents, fileName, false);
		if (selected.length == 0)
			return null;
		// give the policy a chance to change the results
		ISelectionPolicy policy = matcher.getPolicy();
		if (policy != null) {
			selected = applyPolicy(policy, selected, fileName != null, true);
			if (selected.length == 0)
				return null;
		}
		return matcher.getSpecificDescription(((ContentType) selected[0]).internalGetDescriptionFor(contents, options));
	}

	public IContentDescription getDescriptionFor(ContentTypeMatcher matcher, InputStream contents, String fileName, QualifiedName[] options) throws IOException {
		return getDescriptionFor(matcher, ContentTypeManager.readBuffer(contents), fileName, options);
	}

	public IContentDescription getDescriptionFor(ContentTypeMatcher matcher, Reader contents, String fileName, QualifiedName[] options) throws IOException {
		return getDescriptionFor(matcher, ContentTypeManager.readBuffer(contents), fileName, options);
	}

	public int getGeneration() {
		return generation;
	}

	public ContentTypeManager getManager() {
		return manager;
	}

	private boolean internalAccept(ContentTypeVisitor visitor, ContentType root) {
		if (!root.isValid() || root.isAlias())
			return true;
		int result = visitor.visit(root);
		switch (result) {
			// stop traversing the tree
			case ContentTypeVisitor.STOP :
				return false;
			// stop traversing this subtree
			case ContentTypeVisitor.RETURN :
				return true;
		}
		ContentType[] children = getChildren(root);
		if (children == null)
			// this content type has no sub-types - keep traversing the tree
			return true;
		for (int i = 0; i < children.length; i++)
			if (!internalAccept(visitor, children[i]))
				// stop the traversal
				return false;
		return true;
	}

	private IContentType[] internalFindContentTypesFor(ILazySource buffer, IContentType[][] subset, Comparator validPolicy, Comparator indeterminatePolicy) throws IOException {
		Map properties = new HashMap();
		final List appropriate = new ArrayList(5);
		final int validFullName = collectMatchingByContents(0, subset[0], appropriate, buffer, properties);
		final int appropriateFullName = appropriate.size();
		final int validExtension = collectMatchingByContents(validFullName, subset[1], appropriate, buffer, properties) - validFullName;
		final int appropriateExtension = appropriate.size() - appropriateFullName;
		IContentType[] result = (IContentType[]) appropriate.toArray(new IContentType[appropriate.size()]);
		if (validFullName > 1)
			Arrays.sort(result, 0, validFullName, validPolicy);
		if (validExtension > 1)
			Arrays.sort(result, validFullName, validFullName + validExtension, validPolicy);
		if (appropriateFullName - validFullName > 1)
			Arrays.sort(result, validFullName + validExtension, appropriateFullName + validExtension, indeterminatePolicy);
		if (appropriateExtension - validExtension > 1)
			Arrays.sort(result, appropriateFullName + validExtension, appropriate.size(), indeterminatePolicy);
		return result;
	}

	private IContentType[] internalFindContentTypesFor(ContentTypeMatcher matcher, ILazySource buffer, String fileName, boolean forceValidation) throws IOException {
		final IContentType[][] subset;
		final Comparator validPolicy;
		Comparator indeterminatePolicy;
		if (fileName == null) {
			// we only have a single array, by need to provide a two-dimensional, 2-element array 
			subset = new IContentType[][] {getAllContentTypes(), NO_CONTENT_TYPES};
			indeterminatePolicy = policyConstantGeneralIsBetter;
			validPolicy = policyConstantSpecificIsBetter;
		} else {
			subset = internalFindContentTypesFor(matcher, fileName, policyLexicographical);
			indeterminatePolicy = policyGeneralIsBetter;
			validPolicy = policySpecificIsBetter;
		}
		int total = subset[0].length + subset[1].length;
		if (total == 0)
			// don't do further work if subset is empty
			return NO_CONTENT_TYPES;	
		if (!forceValidation && total == 1) {
			// do not do validation if not forced and only one was found (caller will validate later)
			IContentType[] found = subset[0].length == 1 ? subset[0] : subset[1];
			// bug 100032 - ignore binary content type if contents are text			
			if (!buffer.isText())
				// binary buffer, caller can call the describer with no risk
				return found;
			// text buffer, need to check describer			
			IContentDescriber describer = ((ContentType) found[0]).getDescriber();			
			if (describer == null || describer instanceof ITextContentDescriber)
				// no describer or text describer, that is fine
				return found;
			// only eligible content type is binary and contents are text, ignore it
			return NO_CONTENT_TYPES;			
		}
		return internalFindContentTypesFor(buffer, subset, validPolicy, indeterminatePolicy);
	}

	/**
	 * This is the implementation for file name based content type matching. 
	 * 
	 * @return all matching content types in the preferred order 
	 * @see IContentTypeManager#findContentTypesFor(String)
	 */
	synchronized private IContentType[][] internalFindContentTypesFor(ContentTypeMatcher matcher, final String fileName, Comparator sortingPolicy) {
		IScopeContext context = matcher.getContext();
		IContentType[][] result = {NO_CONTENT_TYPES, NO_CONTENT_TYPES};

		final Set allByFileName;

		if (context.equals(manager.getContext()))
			allByFileName = getDirectlyAssociated(fileName, IContentTypeSettings.FILE_NAME_SPEC);
		else {
			allByFileName = new HashSet(getDirectlyAssociated(fileName, IContentTypeSettings.FILE_NAME_SPEC | IContentType.IGNORE_USER_DEFINED));
			allByFileName.addAll(matcher.getDirectlyAssociated(this, fileName, IContentTypeSettings.FILE_NAME_SPEC));
		}
		Set selectedByName = selectMatchingByName(context, allByFileName, Collections.EMPTY_SET, fileName, IContentType.FILE_NAME_SPEC);
		result[0] = (IContentType[]) selectedByName.toArray(new IContentType[selectedByName.size()]);
		final String fileExtension = ContentTypeManager.getFileExtension(fileName);
		if (fileExtension != null) {
			final Set allByFileExtension;
			if (context.equals(manager.getContext()))
				allByFileExtension = getDirectlyAssociated(fileExtension, IContentTypeSettings.FILE_EXTENSION_SPEC);
			else {
				allByFileExtension = new HashSet(getDirectlyAssociated(fileExtension, IContentTypeSettings.FILE_EXTENSION_SPEC | IContentType.IGNORE_USER_DEFINED));
				allByFileExtension.addAll(matcher.getDirectlyAssociated(this, fileExtension, IContentTypeSettings.FILE_EXTENSION_SPEC));
			}
			Set selectedByExtension = selectMatchingByName(context, allByFileExtension, selectedByName, fileExtension, IContentType.FILE_EXTENSION_SPEC);
			if (!selectedByExtension.isEmpty())
				result[1] = (IContentType[]) selectedByExtension.toArray(new IContentType[selectedByExtension.size()]);
		}
		if (result[0].length > 1)
			Arrays.sort(result[0], sortingPolicy);
		if (result[1].length > 1)
			Arrays.sort(result[1], sortingPolicy);
		return result;
	}

	/**
	 * Returns content types directly associated with the given file spec.
	 * 
	 * @param text a file name or extension
	 * @param typeMask a bit-wise or of the following flags:
	 * <ul>
	 * 		<li>IContentType.FILE_NAME, </li>
	 * 		<li>IContentType.FILE_EXTENSION, </li>
	 * 		<li>IContentType.IGNORE_PRE_DEFINED, </li>
	 * 		<li>IContentType.IGNORE_USER_DEFINED</li>
	 *	</ul>
	 * @return a set of content types
	 */
	private Set getDirectlyAssociated(String text, int typeMask) {
		Map associations = (typeMask & IContentTypeSettings.FILE_NAME_SPEC) != 0 ? fileNames : fileExtensions;
		Set result = null;
		if ((typeMask & (IContentType.IGNORE_PRE_DEFINED | IContentType.IGNORE_USER_DEFINED)) == 0)
			// no restrictions, get everything
			result = (Set) associations.get(FileSpec.getMappingKeyFor(text));
		else {
			// only those specs satisfying the type mask should be included
			Set initialSet = (Set) associations.get(FileSpec.getMappingKeyFor(text));
			if (initialSet != null && !initialSet.isEmpty()) {
				// copy so we can modify
				result = new HashSet(initialSet);
				// invert the last two bits so it is easier to compare
				typeMask ^= (IContentType.IGNORE_PRE_DEFINED | IContentType.IGNORE_USER_DEFINED);
				for (Iterator i = result.iterator(); i.hasNext();) {
					ContentType contentType = (ContentType) i.next();
					if (!contentType.hasFileSpec(text, typeMask, true))
						i.remove();
				}
			}
		}
		return result == null ? Collections.EMPTY_SET : result;
	}

	synchronized ContentType internalGetContentType(String contentTypeIdentifier) {
		return (ContentType) contentTypes.get(contentTypeIdentifier);
	}

	private void makeAliases() {
		// process all content types marking aliases appropriately
		for (Iterator i = contentTypes.values().iterator(); i.hasNext();) {
			ContentType type = (ContentType) i.next();
			String targetId = type.getAliasTargetId();
			if (targetId == null)
				continue;
			ContentType target = internalGetContentType(targetId);
			if (target != null)
				type.setAliasTarget(target);
		}
	}

	/**
	 * Resolves inter-content type associations (inheritance and aliasing).
	 */
	synchronized protected void organize() {
		// build the aliasing
		makeAliases();
		// do the validation
		for (Iterator i = contentTypes.values().iterator(); i.hasNext();) {
			ContentType type = (ContentType) i.next();
			if (ensureValid(type))
				associate(type);
		}
	}

	/**
	 * Processes all content types in source, adding those matching the given file spec to the
	 * destination collection.
	 */
	private Set selectMatchingByName(final IScopeContext context, Collection source, final Collection existing, final String fileSpecText, final int fileSpecType) {
		if (source == null || source.isEmpty())
			return Collections.EMPTY_SET;
		final Set destination = new HashSet(5);
		// process all content types in the given collection
		for (Iterator i = source.iterator(); i.hasNext();) {
			final ContentType root = (ContentType) i.next();
			// From a given content type, check if it matches, and 
			// include any children that match as well.
			internalAccept(new ContentTypeVisitor() {
				public int visit(ContentType type) {
					if (type != root && type.hasBuiltInAssociations())
						// this content type has built-in associations - visit it later as root						
						return RETURN;
					if (type == root && !type.hasFileSpec(context, fileSpecText, fileSpecType))
						// it is the root and does not match the file name - do not add it nor look into its children						
						return RETURN;
					// either the content type is the root and matches the file name or 
					// is a sub content type and does not have built-in files specs
					if (!existing.contains(type))
						destination.add(type);
					return CONTINUE;
				}
			}, root);
		}
		return destination;
	}
}
