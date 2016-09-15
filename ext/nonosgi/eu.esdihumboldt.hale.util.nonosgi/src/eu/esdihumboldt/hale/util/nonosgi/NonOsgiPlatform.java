package eu.esdihumboldt.hale.util.nonosgi;

import org.eclipse.core.runtime.content.IContentTypeManager;

import eu.esdihumboldt.hale.util.nonosgi.contenttype.ContentTypeManager;

public class NonOsgiPlatform {
	
	public static IContentTypeManager getContentTypeManager() {
		return ContentTypeManager.getInstance();
	}

}
