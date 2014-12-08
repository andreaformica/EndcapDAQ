/**
 * 
 */
package atlas.mdt.dcs.dao;

import java.util.Date;
import java.util.List;

import atlas.mdt.dcs.data.AlB;
import atlas.mdt.dcs.data.AlCycerr;
import atlas.mdt.dcs.data.AlD;
import atlas.mdt.dcs.data.AlR;
import atlas.mdt.dcs.data.AlSupId;
import atlas.mdt.dcs.data.BImages;
import atlas.mdt.dcs.data.DImages;
import atlas.mdt.dcs.data.RImages;

import persistence.utils.DataAccessObject;

/**
 * @author formica
 *
 */
public interface EcDcsDAO extends DataAccessObject {

	public List<AlB> getAlBHistory(Date since, Date until, Long imgId) throws EcDcsRetrieveException;
	
	public List<AlD> getAlDHistory(Date since, Date until, Long imgId) throws EcDcsRetrieveException;

	public List<AlR> getAlRHistory(Date since, Date until, Long imgId) throws EcDcsRetrieveException;
	
	public <T> List<T> getAlHistory(Date since, Date until, Long imgId, T obj) throws EcDcsRetrieveException;

	public <T,M extends AlSupId> T getAl(M alId, T obj) throws EcDcsRetrieveException;

	public <T> List<T> getImages(String imgname, T obj) throws EcDcsRetrieveException;

	public <T> List<T> getImagesByCycle(Long cycle, T obj) throws EcDcsRetrieveException;

	public <T> List<T> getImagesByParams(String imgname, String crate, Long driver, Long mux, T obj) throws EcDcsRetrieveException;

	public <T> T findImage(String imgname, T obj) throws EcDcsRetrieveException;

	public <T> T findImageById(Long imgid, T obj) throws EcDcsRetrieveException;

	public List<BImages> getBImages(String imgname) throws EcDcsRetrieveException;

	public BImages findBImages(Long imgid) throws EcDcsRetrieveException;

	public List<DImages> getDImages(String imgname) throws EcDcsRetrieveException;

	public DImages findDImages(Long imgid) throws EcDcsRetrieveException;

	public List<RImages> getRImages(String imgname) throws EcDcsRetrieveException;

	public RImages findRImages(Long imgid) throws EcDcsRetrieveException;
	
	public <T> void insertImages(List<T> imagelist, T obj) throws EcDcsIoException;

	public List<AlCycerr> getAlCycerrHistory(Date since, Date until) throws EcDcsRetrieveException;

	public AlCycerr findAlCycerr(Long cycid) throws EcDcsRetrieveException;

}
