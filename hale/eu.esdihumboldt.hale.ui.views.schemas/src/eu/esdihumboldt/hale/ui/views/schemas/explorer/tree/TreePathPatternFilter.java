/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package eu.esdihumboldt.hale.ui.views.schemas.explorer.tree;

import org.eclipse.ui.dialogs.PatternFilter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.internal.misc.StringMatcher;

import eu.esdihumboldt.hale.ui.common.definition.viewer.TreePathProviderAdapter;

/**
 * A filter used in conjunction with <code>FilteredTree</code>. In order to
 * determine if a node should be filtered it uses the content and label provider
 * of the tree to do pattern matching on its children. This causes the entire
 * tree structure to be realized. Note that the label provider must implement
 * ILabelProvider.
 * 
 * @see org.eclipse.ui.dialogs.FilteredTree
 * @since 3.2
 */
@SuppressWarnings("restriction")
public class TreePathPatternFilter extends PatternFilter {
	/*
	 * Cache of filtered elements in the tree
	 */
    private Map cache = new HashMap();
    
    /*
     * Maps parent elements to TRUE or FALSE
     */
    private Map foundAnyCache = new HashMap();
    
    private boolean useCache = false;
    
	/**
	 * Whether to include a leading wildcard for all provided patterns.  A
	 * trailing wildcard is always included.
	 */
	private boolean includeLeadingWildcard = false;

	/**
	 * The string pattern matcher used for this pattern filter.  
	 */
    private StringMatcher matcher;
    
    private boolean useEarlyReturnIfMatcherIsNull = true;
    
    private static Object[] EMPTY = new Object[0];

    /**
     * Returns true if any of the elements makes it through the filter.
     * This method uses caching if enabled; the computation is done in
     * computeAnyVisible.
     *  
     * @param viewer
     * @param parent
     * @param elements the elements (must not be an empty array)
     * @return true if any of the elements makes it through the filter.
     */
    private boolean isAnyVisible(Viewer viewer, Object parent, Object[] elements) {
    	if (matcher == null) {
    		return true;
    	}
    	
    	if (!useCache) {
    		return computeAnyVisible(viewer, elements);
    	}
    	
    	Object[] filtered = (Object[]) cache.get(parent);
    	if (filtered != null) {
    		return filtered.length > 0;
    	}
    	Boolean foundAny = (Boolean) foundAnyCache.get(parent);
    	if (foundAny == null) {
    		foundAny = computeAnyVisible(viewer, elements) ? Boolean.TRUE : Boolean.FALSE;
    		foundAnyCache.put(parent, foundAny);
    	}
    	return foundAny.booleanValue();
    }

	/**
	 * Returns true if any of the elements makes it through the filter.
	 * 
	 * @param viewer the viewer
	 * @param elements the elements to test
	 * @return <code>true</code> if any of the elements makes it through the filter
	 */
	private boolean computeAnyVisible(Viewer viewer, Object[] elements) {
		boolean elementFound = false;
		for (int i = 0; i < elements.length && !elementFound; i++) {
			Object element = elements[i];
			elementFound = isElementVisible(viewer, element);
		}
		return elementFound;
	}
    
    /**
	 * Sets whether a leading wildcard should be attached to each pattern
	 * string.
	 * 
	 * @param includeLeadingWildcard
	 *            Whether a leading wildcard should be added.
	 */
	public void setTreePathIncludeLeadingWildcard(
			final boolean includeLeadingWildcard) {
		this.includeLeadingWildcard = includeLeadingWildcard;
	}

    /**
	 * @see org.eclipse.jface.viewers.ViewerFilter#filter(org.eclipse.jface.viewers.Viewer, org.eclipse.jface.viewers.TreePath, java.lang.Object[])
	 */
	@Override
	public Object[] filter(Viewer viewer, TreePath parentPath, Object[] elements) {
		// break if there is a cycle
		if (elements != null) {
			//TODO only above a max depth? (parentPath check?)
			
			for (Object element : elements) {
				TreePath elementPath = (TreePath) element;
				
				Set<Object> segments = new HashSet<Object>();
				for (int i = 0; i < elementPath.getSegmentCount(); i++) {
					Object segment = elementPath.getSegment(i);
					if (segments.contains(segment)) {
						// return none for cycle
						return new Object[0];
					}
					segments.add(segment);
				}
			}
		}
		
		// we don't want to optimize if we've extended the filter ... this
    	// needs to be addressed in 3.4
    	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=186404
        if (matcher == null && useEarlyReturnIfMatcherIsNull) {
			return elements;
		}

        if (!useCache) {
        	return super.filter(viewer, parentPath, elements);
        }
        
        Object[] filtered = (Object[]) cache.get(parentPath);
        if (filtered == null) {
        	Boolean foundAny = (Boolean) foundAnyCache.get(parentPath);
        	if (foundAny != null && !foundAny.booleanValue()) {
        		filtered = EMPTY;
        	} else {
        		filtered = super.filter(viewer, parentPath, elements);
        	}
            cache.put(parentPath, filtered);
        }
        return filtered;
	}

	/**
     * The pattern string for which this filter should select 
     * elements in the viewer.
     * 
     * @param patternString
     */
    @Override
	public void setPattern(String patternString) {
    	// these 2 strings allow the PatternFilter to be extended in
    	// 3.3 - https://bugs.eclipse.org/bugs/show_bug.cgi?id=186404
    	if ("org.eclipse.ui.keys.optimization.true".equals(patternString)) { //$NON-NLS-1$
    		useEarlyReturnIfMatcherIsNull = true;
    		return;
    	} else if ("org.eclipse.ui.keys.optimization.false".equals(patternString)) { //$NON-NLS-1$
    		useEarlyReturnIfMatcherIsNull = false;
    		return;
    	}
        clearCaches();
        if (patternString == null || patternString.equals("")) { //$NON-NLS-1$
			matcher = null;
		} else {
			String pattern = patternString + "*"; //$NON-NLS-1$
			if (includeLeadingWildcard) {
				pattern = "*" + pattern; //$NON-NLS-1$
			}
			matcher = new StringMatcher(pattern, true, false);
		}
        
        super.setPattern(patternString);
    }

	/**
	 * Clears the caches used for optimizing this filter. Needs to be called whenever
	 * the tree content changes.
	 */
	/* package */ void clearCaches() {
		cache.clear();
        foundAnyCache.clear();
	}

    /**
     * Answers whether the given String matches the pattern.
     * 
     * @param string the String to test
     * 
     * @return whether the string matches the pattern
     */
    private boolean match(String string) {
    	if (matcher == null) {
			return true;
		}
        return matcher.match(string);
    }
    
    /**
     * Check if the parent (category) is a match to the filter text.  The default 
     * behavior returns true if the element has at least one child element that is 
     * a match with the filter text.
     * 
     * Subclasses may override this method.
     * 
     * @param viewer the viewer that contains the element
     * @param element the tree element to check
     * @return true if the given element has children that matches the filter text
     */
    @Override
	protected boolean isParentMatch(Viewer viewer, Object element){
		TreePath elementPath = (TreePath) element;

		if (elementPath != null) {
			// only below a max depth?
			//TODO configurable
			if (elementPath.getSegmentCount() > 5) {
				return false;
			}
			
			Set<Object> segments = new HashSet<Object>();
			for (int i = 0; i < elementPath.getSegmentCount(); i++) {
				Object segment = elementPath.getSegment(i);
				if (segments.contains(segment)) {
					// return none for cycle
					return false;
				}
				segments.add(segment);
			}
		}

		Object[] children;
		ITreePathContentProvider cp = ((ITreePathContentProvider) ((AbstractTreeViewer) viewer)
				.getContentProvider());

		children = cp.getChildren(elementPath);

		if ((children != null) && (children.length > 0)) {
			// convert children to tree paths
			List<TreePath> pathChildren = TreePathTreeViewer.getPathsForElements(elementPath, children);
			
			return isAnyVisible(viewer, element, pathChildren.toArray());
		}
		return false;
    }
    
    /**
     * Check if the current (leaf) element is a match with the filter text.  
     * The default behavior checks that the label of the element is a match. 
     * 
     * Subclasses should override this method.
     * 
     * @param viewer the viewer that contains the element
     * @param element the tree element to check
     * @return true if the given element's label matches the filter text
     */
    @Override
	protected boolean isLeafMatch(Viewer viewer, Object element){
    	if (element instanceof TreePath) {
    		element = ((TreePath) element).getLastSegment();
    	}
    	
        String labelText = ((ILabelProvider) ((StructuredViewer) viewer)
                .getLabelProvider()).getText(element);
        
        if(labelText == null) {
			return false;
		}
        return wordMatches(labelText);  
    }
    
	/**
	 * Can be called by the filtered tree to turn on caching.
	 * 
	 * @param useCache The useCache to set.
	 */
	void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}    
}

