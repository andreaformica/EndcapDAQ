/**
 * 
 */
package atlas.mdt.dcs.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.mdt.dcs.server.EcDcsDAORepository;

@Named("imgbean")
/**
 * @author formica
 *
 */
@Stateful
public class ImageBean implements ImagesDAO {

	@Inject
	private Logger log;

	@Inject
	private EcDcsDAORepository dcsDAO;

	/**
	 * 
	 */
	public ImageBean() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see atlas.mdt.dcs.dao.ImagesDAO#retrieveImagesForTypes(java.lang.Object)
	 */
	@Override
	public <T> List<T> retrieveImagesForTypes(T obj) {
		try {
			log.info("Searching for images of type "+obj.getClass().getName());
			return dcsDAO.getImages("%", obj);
		} catch (EcDcsRetrieveException e) {
			e.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see atlas.mdt.dcs.dao.ImagesDAO#retrieveImageForName(java.lang.Object)
	 */
	@Override
	public <T> T retrieveImageForName(T obj) {
		try {
			log.info("Searching for images of type "+obj.getClass().getName());
			return (T) dcsDAO.findImage(null,obj);
		} catch (EcDcsRetrieveException e) {
			e.printStackTrace();
		}
		return null;
	}

}
