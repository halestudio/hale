/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.haleconnect.ui.projects;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import eu.esdihumboldt.hale.io.haleconnect.HaleConnectProjectInfo;
import eu.esdihumboldt.hale.io.haleconnect.Owner;

/**
 * Filter for the hale connect project list
 * 
 * @author Florian Esser
 */
public class ProjectFilter extends ViewerFilter {

	private final Viewer viewer;
	private final Set<Owner> visibleOwners = new HashSet<Owner>();
	private String searchText;

	/**
	 * Create a project filter
	 * 
	 * @param viewer Project table viewer
	 */
	public ProjectFilter(Viewer viewer) {
		super();
		this.viewer = viewer;
	}

	/**
	 * Filter projects by the given owners.
	 * 
	 * @param owners owners to filter by
	 */
	public void filterByOwners(Collection<Owner> owners) {
		clearOwnerFilter();
		visibleOwners.addAll(owners);
		viewer.refresh();
	}

	/**
	 * Sets the search text. If null or an empty String are set, no search text
	 * filter will be applied.
	 * 
	 * @param text search text, or null to clear the filter
	 */
	public void setSearchText(String text) {
		this.searchText = text;
		viewer.refresh();
	}

	/**
	 * Remove the filter.
	 */
	public void clearOwnerFilter() {
		visibleOwners.clear();
		viewer.refresh();
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (!(element instanceof HaleConnectProjectInfo)) {
			// fast exit
			return true;
		}

		HaleConnectProjectInfo pi = (HaleConnectProjectInfo) element;

		boolean match = visibleOwners.isEmpty() || visibleOwners.contains(pi.getOwner());
		match &= StringUtils.isEmpty(searchText) || containsSearchText(pi, searchText);
		return match;
	}

	private boolean containsSearchText(HaleConnectProjectInfo projectInfo, String searchText) {
		String ownerName = "";
		if (projectInfo.getUser() != null) {
			ownerName = StringUtils.isNotEmpty(projectInfo.getUser().getFullName())
					? projectInfo.getUser().getFullName() : projectInfo.getUser().getScreenName();
		}
		else if (projectInfo.getOrganisation() != null) {
			ownerName = projectInfo.getOrganisation().getName();
		}

		return Arrays.asList(projectInfo.getAuthor(), projectInfo.getName(), ownerName).stream()
				.anyMatch(str -> StringUtils.containsIgnoreCase(str, searchText));
	}

}
