/**
 * 
 */
package atlas.mdt.dcs.client;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import persistence.server.data.reflection.DynamicBusinessDelegate;
import persistence.utils.EntityManagerFactoryPool;
import atlas.mdt.dcs.dao.EcDcsDAO;
import atlas.mdt.dcs.dao.EcDcsDAOTemplate;

/**
 * @author formica
 *
 */
public class EcDcsDAOClient extends EcDcsDAOTemplate implements EcDcsDAO,
		DynamicBusinessDelegate {

	/**
	 * The entity manager: handles relational-object mapping
	 */
	private EntityManager em;
	/**
	 * The name of the persistence unit to use (declared in a persistence.xml).
	 */
	private String persistenceUnit;
	
	
	/**
	 * Default constructor.
	 * Uses the default persistence unit. 
	 */
	public EcDcsDAOClient() {
		this(DEFAULT_PERSISTENCE_UNIT);
	}

	/**
	 * Constructor.
	 * @param persistenceUnit The name of the persistence unit to use (declared
	 * in a persistence.xml).
	 */
	public EcDcsDAOClient(String persistenceUnit) {
		super();
		this.persistenceUnit = persistenceUnit;
	}

	/**
	 * Redefines the entity manager used by this class
	 * @param em The new entity manager
	 */
	public void setEntityManager(EntityManager em) {
		this.em.close();
		this.em = em;
	}
	
	/* (non-Javadoc)
	 * @see persistence.utils.DataAccessObject#getEntityManager()
	 */
	public EntityManager getEntityManager() {
		if(em == null) {
			EntityManagerFactory emf = EntityManagerFactoryPool.getEntityManager(persistenceUnit);
			em = emf.createEntityManager();
		}
		return em;
	}

	/* (non-Javadoc)
	 * @see persistence.server.data.reflection.DynamicBusinessDelegate#updateEntityManager()
	 */
	public void updateEntityManager() {
		em = null;
	}

}
