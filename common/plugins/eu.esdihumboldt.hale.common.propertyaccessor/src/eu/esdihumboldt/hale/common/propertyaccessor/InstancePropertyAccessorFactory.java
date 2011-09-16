/**
 * 
 */
package eu.esdihumboldt.hale.common.propertyaccessor;

import javax.xml.namespace.QName;

import org.geotools.factory.Hints;
import org.geotools.filter.expression.PropertyAccessor;
import org.geotools.filter.expression.PropertyAccessorFactory;

import eu.esdihumboldt.hale.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.instance.model.Instance;

/**
 * @author Basti
 *
 */
public class InstancePropertyAccessorFactory implements PropertyAccessorFactory {

	/* (non-Javadoc)
	 * @see org.geotools.filter.expression.PropertyAccessorFactory#createPropertyAccessor(java.lang.Class, java.lang.String, java.lang.Class, org.geotools.factory.Hints)
	 */
	@Override
	public PropertyAccessor createPropertyAccessor(Class type, String xpath,
			Class target, Hints hints) {
		
		
		if ( xpath == null ) 
    		return null;
		
		if(!Instance.class.isAssignableFrom(type)){
			return null;  // we only work with instances 
		}

		return new InstancePropertyAccessor();
	}

	static String stripPrefix(String xpath) {
        int split = xpath.indexOf(":");
        if (split != -1) {
            return xpath.substring(split + 1);
        }
        return xpath;
	}
	
	
	static class InstancePropertyAccessor implements PropertyAccessor {
        public boolean canHandle(Object object, String xpath, Class target) {
        	xpath = stripPrefix(xpath);
        	
        	if ( object instanceof Instance ) {
        		
        			return PropertyResolver.hasProperty((Instance)object, xpath);
        			
        			//the old way
        			//return ((Instance) object).getProperty(new QName(xpath)) != null;
        	}
        	
        	return false;
        }
        
        public Object get(Object object, String xpath, Class target) {
        	xpath = stripPrefix(xpath);
        	
        	if ( object instanceof Instance ) {
        		
        		//theold way
        		//Object[] property = ((Instance) object).getProperty(new QName(xpath));
        		
        		return PropertyResolver.getValues((Instance)object, xpath);
        			
        		}
        	
        	return null;
        }		

        public void set(Object object, String xpath, Object value, Class target){
        /*	xpath = stripPrefix(xpath);
        	
        	if ( object instanceof OInstance ) {
        		((OInstance) object).setProperty( xpath, value );
        	}
        	
        	*/
        	
        }
    }
	
	
	
	
	
}
