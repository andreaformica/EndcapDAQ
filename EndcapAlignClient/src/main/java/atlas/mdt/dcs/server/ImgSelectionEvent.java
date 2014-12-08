/**
 * 
 */
package atlas.mdt.dcs.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author formica
 *
 */
public class ImgSelectionEvent implements Serializable {

//	@Inject
//	private Logger log;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3644374418622904916L;

	private String updateFieldName;
	
	private Map<String,Object> selMap = new HashMap<String,Object>();
	
	public ImgSelectionEvent() {
		
	}
	
	public void setField(String key, Object val) {
		if (key == null || val == null) return;
//		this.log.info("Set key "+key);
//		if (val != null) 
//			this.log.info(" ...with value "+val.toString());
//		else 
//			return;
		selMap.put(key, val);
	}
	
	public Object getField(String key) {
		if (selMap.containsKey(key)) {
//			this.log.info("return existing key "+key);
			return selMap.get(key);
		}
		return null;
	}

	public Set<String> getKeys() {
		return selMap.keySet();
	}
	/**
	 * @return the updateFieldName
	 */
	public String getUpdateFieldName() {
		return updateFieldName;
	}

	/**
	 * @param updateFieldName the updateFieldName to set
	 */
	public void setUpdateFieldName(String updateFieldName) {
		this.updateFieldName = updateFieldName;
	}
	
	
}
