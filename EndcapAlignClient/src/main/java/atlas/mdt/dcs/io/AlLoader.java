/**
 * 
 */
package atlas.mdt.dcs.io;

import io.utils.ASCIILoader;

import java.math.BigDecimal;
import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import atlas.mdt.dcs.data.AlBId;
import atlas.mdt.dcs.data.AlDId;
import atlas.mdt.dcs.data.AlRId;
import atlas.mdt.dcs.data.AlTId;

/**
 * @author formica
 *
 */
public class AlLoader<T> extends ASCIILoader<T> {

	public static Log log = LogFactory.getLog(AlLoader.class
			.getName());

	
	public AlLoader(final T obj) {
		init(obj, null, null,null);
		localinit();
	}
	
	/**
	 * Constructor.
	 */
	public AlLoader(final T obj, final String separator) {
		init(obj, separator, null, null);
		localinit();
		if (separator != null) {
			this.separator = separator;
		}	
	}
	
	private void localinit() {
		this.separator = "[ \t\n\f\r]+";
		this.dateformat = "yyyy-MM-dd HH:mm:ss.SSS";
	}

	public Object buildParam(String[] result, int j) throws ParseException {
		Object value = null;
		String field = result[parsingIndex++].trim();
		log.debug("alloader is parsing value " + field + " --> into class "
				+ fieldtypes[j]);
		Class<?> _clazz = fieldtypes[j];
		value = getField(_clazz, field);
		log.debug("Returning value " + value);
		if (value == null) {
			parsingIndex--;
			value = getSpecialField(_clazz, result);
		}
		return value;
	}

	private Object getSpecialField(Class<?> _clz, String[] fields) {
		
		try {
			String idfield = fields[parsingIndex++].trim();
			String stimefield = fields[parsingIndex++].trim();
		log.debug("special parsing value " + stimefield + " and "+ idfield+" --> into class "
				+ _clz);
		Object value = null;
		if (_clz.equals(AlBId.class)) {
			idfield = ((idfield != null) && (!idfield.equals(""))) ? idfield
					: null;
			Long id = new Long(idfield);
			BigDecimal stime = new BigDecimal(stimefield);
			value = new AlBId(stime,id);
		} else if (_clz.equals(AlDId.class)) {
			idfield = ((idfield != null) && (!idfield.equals(""))) ? idfield
					: null;
			Long id = new Long(idfield);
			BigDecimal stime = new BigDecimal(stimefield);
			value = new AlDId(stime,id);
		} else if (_clz.equals(AlRId.class)) {
			idfield = ((idfield != null) && (!idfield.equals(""))) ? idfield
					: null;
			Long id = new Long(idfield);
			BigDecimal stime = new BigDecimal(stimefield);
			value = new AlRId(stime,id);
		} else if (_clz.equals(AlTId.class)) {
			idfield = ((idfield != null) && (!idfield.equals(""))) ? idfield
					: null;
			Long id = new Long(idfield);
			BigDecimal stime = new BigDecimal(stimefield);
			value = new AlTId(stime,id);
		}
		log.debug("Returning value...."+value);
		return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
