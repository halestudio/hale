/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.firststeps;

import java.io.File;
import java.io.PrintWriter;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.intro.config.IIntroContentProvider;
import org.eclipse.ui.intro.config.IIntroContentProviderSite;

import eu.esdihumboldt.hale.ui.service.project.RecentProjectsMenu;
import eu.esdihumboldt.hale.ui.service.project.RecentProjectsService;

/**
 * A content provider to show and link to recently opened projects.
 * 
 * @author Kai Schwierczek
 */
public class RecentProjectsContentProvider implements IIntroContentProvider {

	private boolean disposed = false;

	/**
	 * Max length of the project file path shown.
	 */
	public static final int MAX_LENGTH = 60;

	/**
	 * @see org.eclipse.ui.intro.config.IIntroContentProvider#init(org.eclipse.ui.intro.config.IIntroContentProviderSite)
	 */
	@Override
	public void init(IIntroContentProviderSite site) {
		// site isn't needed or directly react to changes in recent entries?
		// -> somehow add listener to recent entries, reflow after changes,
		// remove listener in dispose ?
	}

	/**
	 * Create the html content for recent entries.
	 * 
	 * @see org.eclipse.ui.intro.config.IIntroContentProvider#createContent(java.lang.String,
	 *      java.io.PrintWriter)
	 */
	@Override
	public void createContent(String id, PrintWriter out) {
		if (disposed)
			return;
		RecentProjectsService.Entry[] entries = getRecentFiles();
		if (entries.length > 0) {
			out.print("<ul id=\"recent-files-list\">"); //$NON-NLS-1$
			for (int i = entries.length - 1; i >= 0; i--) {
				RecentProjectsService.Entry entry = entries[i];
				out.print("<li>"); //$NON-NLS-1$
				if (entry.getProjectName() != null) {
					out.print(entry.getProjectName());
					out.print("<br>"); //$NON-NLS-1$
				}
				out.print("<a class=\"recentFile\" href=\""); //$NON-NLS-1$
				out.print("http://org.eclipse.ui.intro/runAction?"); //$NON-NLS-1$
				out.print("pluginId=eu.esdihumboldt.hale.ui.firststeps&"); //$NON-NLS-1$
				out.print("class=eu.esdihumboldt.hale.ui.firststeps.LoadProjectAction&"); //$NON-NLS-1$
				out.print("closeIntro=true&path="); //$NON-NLS-1$
				out.print(entry.getFile());
				out.print("\">"); //$NON-NLS-1$
				out.print(RecentProjectsMenu.shorten(entry.getFile(), MAX_LENGTH,
						new File(entry.getFile()).getName().length()));
				out.print("</a>"); //$NON-NLS-1$
				out.print("</li>"); //$NON-NLS-1$
			}
			out.print("</ul>"); //$NON-NLS-1$
		}
		else {
			out.print("<p class=\"status-text\">"); //$NON-NLS-1$
			out.print("No recently opened projects.");
			out.print("</p>"); //$NON-NLS-1$
		}
	}

	/**
	 * Create widgets to display the recent entries when using the SWT
	 * presentation
	 * 
	 * XXX not tested, how to make eclipse use the SWT version?
	 * 
	 * @see org.eclipse.ui.intro.config.IIntroContentProvider#createContent(String,
	 *      Composite, FormToolkit)
	 */
	@Override
	public void createContent(String id, Composite parent, FormToolkit toolkit) {
		if (disposed)
			return;

		FormText formText = toolkit.createFormText(parent, true);
		formText.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				new LoadProjectAction().execute(true, (String) e.getHref());
			}
		});

		StringBuffer buffer = new StringBuffer();
		buffer.append("<form>"); //$NON-NLS-1$

		RecentProjectsService.Entry[] entries = getRecentFiles();
		if (entries.length > 0) {
			for (int i = entries.length - 1; i >= 0; i--) {
				RecentProjectsService.Entry entry = entries[i];
				buffer.append("<li style=\"none\">"); //$NON-NLS-1$
				if (entry.getProjectName() != null) {
					buffer.append(entry.getProjectName());
					buffer.append("<br>"); //$NON-NLS-1$
				}
				buffer.append("<a href=\""); //$NON-NLS-1$
				buffer.append(entry.getFile());
				buffer.append("\">"); //$NON-NLS-1$
				buffer.append(RecentProjectsMenu.shorten(entry.getFile(), MAX_LENGTH,
						new File(entry.getFile()).getName().length()));
				buffer.append("</a>"); //$NON-NLS-1$
				buffer.append("</li>"); //$NON-NLS-1$
			}
		}
		else {
			buffer.append("<p>"); //$NON-NLS-1$
			buffer.append("No recently opened projects.");
			buffer.append("</p>"); //$NON-NLS-1$
		}

		buffer.append("</form>"); //$NON-NLS-1$

		String text = buffer.toString();
		text = text.replaceAll("&{1}", "&amp;"); //$NON-NLS-1$ //$NON-NLS-2$
		formText.setText(text, true, false);
	}

	private RecentProjectsService.Entry[] getRecentFiles() {
		RecentProjectsService rfs = PlatformUI.getWorkbench()
				.getService(RecentProjectsService.class);
		RecentProjectsService.Entry[] entries = rfs.getRecentFiles();
		if (entries == null)
			entries = new RecentProjectsService.Entry[0];
		return entries;
	}

	/**
	 * @see org.eclipse.ui.intro.config.IIntroContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		disposed = true;
	}
}
