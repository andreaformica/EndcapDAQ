/**
 * 
 */
package ec.dim.tools;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import atlas.mdt.dcs.client.EcDcsDAODB;
import atlas.mdt.dcs.data.AlB;
import atlas.mdt.dcs.data.AlD;
import atlas.mdt.dcs.data.AlR;
import atlas.mdt.dcs.data.AlSupId;
import atlas.mdt.dcs.data.AlT;
import atlas.mdt.dcs.data.EcImage;

/**
 * @author formica
 *
 */
public class EndcapImageComparator<T> {

	public static Log log = LogFactory
			.getLog(EndcapImageComparator.class.getName());

	private EcDcsDAODB db = null;
	
	
	
	/**
	 * @param db
	 */
	public EndcapImageComparator(EcDcsDAODB db) {
		super();
		this.db = db;
	}

	public int compare(T dbobj, T fileobj) {
		log.debug("Comparing "+dbobj+" and \n   \t\t"+fileobj);
		if (fileobj instanceof AlB) {
			AlB _db = (AlB)dbobj;
			AlB _file = (AlB)fileobj;
			if (_db.equals(_file)) {
				return 0;
			}
		} else if (fileobj instanceof AlD) {
			AlD _db = (AlD)dbobj;
			AlD _file = (AlD)fileobj;
			if (_db.equals(_file)) {
				return 0;
			}
		} else if (fileobj instanceof AlT) {
			AlT _db = (AlT)dbobj;
			AlT _file = (AlT)fileobj;
			if (_db.equals(_file)) {
				return 0;
			}
		} else if (fileobj instanceof AlR) {
			AlR _db = (AlR)dbobj;
			AlR _file = (AlR)fileobj;
			if (_db.equals(_file)) {
				return 0;
			}
		}
		return -1;
	}
	
	public void verifyInsertion(List<T> alfromfile) {
		for (T alel : alfromfile) {
			log.debug("Checking image "+alel);
			// comparing image
			T dbobj = (T) db.findAlPrimaryKey((AlSupId)((EcImage)alel).getId(), alel);
			int isindb = compare(dbobj, alel);
			log.debug("Check image "+alel+" is in DB "+isindb);
			if (isindb<0) {
				log.warn("Image "+alel+" is not in DB...!!");
			}
		}
	}
}
