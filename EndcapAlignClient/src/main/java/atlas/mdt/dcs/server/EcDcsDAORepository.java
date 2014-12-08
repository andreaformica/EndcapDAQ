/**
 * 
 */
package atlas.mdt.dcs.server;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import persistence.server.data.reflection.DynamicBusinessDelegate;
import atlas.mdt.dcs.dao.EcDcsDAO;
import atlas.mdt.dcs.dao.EcDcsDAOTemplate;

/**
 * @author formica
 *
 */
@ApplicationScoped
public class EcDcsDAORepository extends EcDcsDAOTemplate implements EcDcsDAO, DynamicBusinessDelegate {

	@Inject
	private EntityManager em;

	@Inject
	private Logger log;

	/* (non-Javadoc)
	 * @see persistence.utils.DataAccessObject#getEntityManager()
	 */
	@Override
	public EntityManager getEntityManager() {
		return em;
	}

	@Override
	public void updateEntityManager() {
		em = null;
	}

}
