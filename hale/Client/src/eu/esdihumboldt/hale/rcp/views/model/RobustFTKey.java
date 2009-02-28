/**
 * 
 */
package eu.esdihumboldt.hale.rcp.views.model;

import org.opengis.feature.type.FeatureType;

/**
 * @author thorsten
 *
 */
public class RobustFTKey implements Comparable {
	
	private FeatureType ft = null;

	public RobustFTKey(FeatureType ft) {
		this.ft = ft;
	}
	
	@Override
	public int hashCode() {
		return (ft.getName().getNamespaceURI() 
				+ ft.getName().getLocalPart()).hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		FeatureType ft2 = ((RobustFTKey) o).getFeatureType();
		return this.ft.hashCode() == ft2.hashCode();
	}
	
	public FeatureType getFeatureType() {
		return this.ft;
	}

	@Override
	public int compareTo(Object o) {
		FeatureType ft2 = ((RobustFTKey) o).getFeatureType();
		return this.ft.hashCode() - ft2.hashCode();
	}
}
