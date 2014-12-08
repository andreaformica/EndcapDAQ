/**
 * 
 */
package ec.dim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import dim.DataDecoder;
import dim.Memory;
import dim.Server;

/**
 * @author formica
 *
 */
public class DaqDataDecoder implements DataDecoder {

	public static Log log = LogFactory
			.getLog(DaqDataDecoder.class.getName());


	/* (non-Javadoc)
	 * @see dim.DataDecoder#decodeData(dim.Memory)
	 */
	@Override
	public void decodeData(Memory data) {
		// TODO Auto-generated method stub
		log.info("Receiving data "+data.getDataSize());
		log.info(" services :"+Server.getServices());
	}

}
