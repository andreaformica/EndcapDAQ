/**
 * 
 */
package atlas.mdt.dcs.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import atlas.mdt.dcs.data.AlB;
import atlas.mdt.dcs.data.AlCycerr;
import atlas.mdt.dcs.data.AlD;
import atlas.mdt.dcs.data.AlR;
import atlas.mdt.dcs.data.AlSupId;
import atlas.mdt.dcs.data.AlT;
import atlas.mdt.dcs.data.BImages;
import atlas.mdt.dcs.data.DImages;
import atlas.mdt.dcs.data.EcConfImage;
import atlas.mdt.dcs.data.RImages;

/**
 * @author formica
 * 
 */
public abstract class EcDcsDAOTemplate implements EcDcsDAO {

	public static Log log = LogFactory.getLog(EcDcsDAOTemplate.class.getName());

	protected Query getAlHistoryQuery(String qry, Date since, Date until,
			Long imgId) {
		Query q;
		q = getEntityManager().createNamedQuery(qry);
		q.setParameter("id", imgId);
		BigDecimal start = new BigDecimal(since.getTime() / 1000L);
		BigDecimal end = new BigDecimal(until.getTime() / 1000L);
		q.setParameter("since", start);
		q.setParameter("until", end);
		return q;
	}

	protected Query getImageByNameQuery(String qry, String imgname) {
		Query q;
		q = getEntityManager().createNamedQuery(qry);
		q.setParameter("imgname", (imgname != null) ? imgname : "%");
		return q;
	}
	
	protected Query getImageByCycleQuery(String qry, BigDecimal cycle) {
		Query q;
		q = getEntityManager().createNamedQuery(qry);
		q.setParameter("cycle", (cycle != null) ? cycle : 0L);
		return q;
	}


	protected Query getImageByParamsQuery(String qry, String imgname,
			String crate, Long driver, Long mux) {
		Query q;
		q = getEntityManager().createNamedQuery(qry);
		q.setParameter("imgname", (imgname != null) ? imgname : "%");
		q.setParameter("camcrate", (crate != null) ? crate : "%");
		q.setParameter("camdriver", (driver != null) ? driver : -1L);
		q.setParameter("cammux", (mux != null) ? mux : -1L);
		return q;
	}

	protected Query getImageByIdQuery(String qry, Long imgid) {
		Query q;
		q = getEntityManager().createNamedQuery(qry);
		Long mid = (imgid != null) ? imgid : null;
		if (mid == null)
			return null;
		q.setParameter("imgid", mid);
		return q;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.mdt.dcs.dao.EcDcsDAO#findImage(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public <T> T findImage(String imgname, T obj) throws EcDcsRetrieveException {
		String qryname = "none";
		String _imgname = imgname;
		if (obj instanceof BImages) {
			qryname = BImages.QUERY_GET_IMAGES_BYNAME;
			if (imgname == null) {
				_imgname = ((BImages) obj).getImageName();
			}
		} else if (obj instanceof DImages) {
			qryname = DImages.QUERY_GET_IMAGES_BYNAME;
			if (imgname == null) {
				_imgname = ((DImages) obj).getImageName();
			}
		} else if (obj instanceof RImages) {
			qryname = RImages.QUERY_GET_IMAGES_BYNAME;
			if (imgname == null) {
				_imgname = ((RImages) obj).getImageName();
			}
		}
		if (qryname.equals("none")) {
			throw new EcDcsRetrieveException("Cannot find query...none");
		}
		Query q = getImageByNameQuery(qryname, _imgname);
		try {
			return (T) q.getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.mdt.dcs.dao.EcDcsDAO#getImagesByParams(java.lang.String,
	 * java.lang.String, java.lang.Long, java.lang.Long, java.lang.Object)
	 */
	@Override
	public <T> List<T> getImagesByParams(String imgname, String crate,
			Long driver, Long mux, T obj) throws EcDcsRetrieveException {
		String qryname = "none";
		List<T> imglist = null;
		String _imgname = imgname;
		Query q = null;
		if (obj instanceof BImages) {
			qryname = BImages.QUERY_GET_IMAGES_BYCRDR;
			if (imgname == null) {
				_imgname = ((BImages) obj).getImageName();
			}
		} else if (obj instanceof RImages) {
			qryname = RImages.QUERY_GET_IMAGES_BYCRDR;
			if (imgname == null) {
				_imgname = ((RImages) obj).getImageName();
			}
		} else if (obj instanceof DImages) {
			qryname = DImages.QUERY_GET_IMAGES_BYNAME;
			if (imgname == null) {
				_imgname = ((DImages) obj).getImageName();
			}
		}
		if (qryname.equals("none")) {
			throw new EcDcsRetrieveException("Cannot find query...none");
		} else if (qryname.equals(DImages.QUERY_GET_IMAGES_BYNAME)) {
			q = getImageByNameQuery(qryname, _imgname);
		} else {
			q = getImageByParamsQuery(qryname, _imgname, crate, driver, mux);
		}
		try {
			imglist = q.getResultList();
			return imglist;
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.mdt.dcs.dao.EcDcsDAO#getImages(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public <T> List<T> getImages(String imgname, T obj)
			throws EcDcsRetrieveException {
		String qryname = "none";
		List<T> imglist = null;
		String _imgname = imgname;
		if (obj instanceof BImages) {
			qryname = BImages.QUERY_GET_IMAGES_BYNAME;
			if (imgname == null) {
				_imgname = ((BImages) obj).getImageName();
			}
		} else if (obj instanceof DImages) {
			qryname = DImages.QUERY_GET_IMAGES_BYNAME;
			if (imgname == null) {
				_imgname = ((DImages) obj).getImageName();
			}
		} else if (obj instanceof RImages) {
			qryname = RImages.QUERY_GET_IMAGES_BYNAME;
			if (imgname == null) {
				_imgname = ((RImages) obj).getImageName();
			}
		}
		if (qryname.equals("none")) {
			throw new EcDcsRetrieveException("Cannot find query...none");
		}
		Query q = getImageByNameQuery(qryname, _imgname);
		try {
			imglist = q.getResultList();
			return imglist;
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see atlas.mdt.dcs.dao.EcDcsDAO#getImagesByCycle(java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> List<T> getImagesByCycle(Long cycle, T obj)
			throws EcDcsRetrieveException {
		String qryname = "none";
		List<T> imglist = null;
		BigDecimal _cycle = new BigDecimal(cycle);
		if (obj instanceof AlB) {
			qryname = AlB.QUERY_GET_CYCLE;
			if (cycle == null) {
				_cycle = ((AlB) obj).getCycleNumber();
			}
		} else if (obj instanceof AlD) {
			qryname = AlD.QUERY_GET_CYCLE;
			if (cycle == null) {
				_cycle = ((AlD) obj).getCycleNumber();
			}
		} else if (obj instanceof AlR) {
			qryname = AlR.QUERY_GET_CYCLE;
			if (cycle == null) {
				_cycle = ((AlR) obj).getCycleNumber();
			}
		}
		if (qryname.equals("none")) {
			throw new EcDcsRetrieveException("Cannot find query...none");
		}
		Query q = getImageByCycleQuery(qryname, _cycle);
		try {
			imglist = q.getResultList();
			return imglist;
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.mdt.dcs.dao.EcDcsDAO#findImageById(java.lang.Long,
	 * java.lang.Object)
	 */
	@Override
	public <T> T findImageById(Long imgid, T obj) throws EcDcsRetrieveException {
		String qryname = "none";
		if (obj instanceof BImages) {
			qryname = BImages.QUERY_GET_IMAGES_BYID;

		} else if (obj instanceof DImages) {
			qryname = DImages.QUERY_GET_IMAGES_BYID;

		} else if (obj instanceof RImages) {
			qryname = RImages.QUERY_GET_IMAGES_BYID;

		}
		if (qryname.equals("none")) {
			throw new EcDcsRetrieveException("Cannot find query...none");
		}
		Query q = getImageByIdQuery(qryname, imgid);
		try {
			return (T) q.getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.mdt.dcs.dao.EcDcsDAO#getAlHistory(java.util.Date,
	 * java.util.Date, java.lang.Long)
	 */
	@Override
	public <T> List<T> getAlHistory(Date since, Date until, Long imgId, T obj)
			throws EcDcsRetrieveException {
		String qryname = "none";
		List<T> imglist = null;
		if (obj instanceof AlB) {
			qryname = AlB.QUERY_GET_HISTORY;
		} else if (obj instanceof AlD) {
			qryname = AlD.QUERY_GET_HISTORY;
		} else if (obj instanceof AlR) {
			qryname = AlR.QUERY_GET_HISTORY;
		} else if (obj instanceof AlT) {
			qryname = AlT.QUERY_GET_HISTORY;
		}
		if (qryname.equals("none")) {
			throw new EcDcsRetrieveException("Cannot find query...none");
		}
		Query q = getAlHistoryQuery(qryname, since, until, imgId);
		try {
			imglist = q.getResultList();
			return imglist;
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.mdt.dcs.dao.EcDcsDAO#getAlBHistory(java.util.Date,
	 * java.util.Date, java.lang.Long)
	 */
	@Override
	public List<AlB> getAlBHistory(Date since, Date until, Long imgId)
			throws EcDcsRetrieveException {
		return getAlHistory(since, until, imgId, new AlB());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.mdt.dcs.dao.EcDcsDAO#getAlDHistory(java.util.Date,
	 * java.util.Date, java.lang.Long)
	 */
	@Override
	public List<AlD> getAlDHistory(Date since, Date until, Long imgId)
			throws EcDcsRetrieveException {
		return getAlHistory(since, until, imgId, new AlD());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.mdt.dcs.dao.EcDcsDAO#getAlRHistory(java.util.Date,
	 * java.util.Date, java.lang.Long)
	 */
	@Override
	public List<AlR> getAlRHistory(Date since, Date until, Long imgId)
			throws EcDcsRetrieveException {
		return getAlHistory(since, until, imgId, new AlR());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.mdt.dcs.dao.EcDcsDAO#getBImages(java.lang.String)
	 */
	@Override
	public List<BImages> getBImages(String imgname)
			throws EcDcsRetrieveException {
		List<BImages> coll = null;
		try {
			coll = getImages(imgname, new BImages());
			return coll;
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.mdt.dcs.dao.EcDcsDAO#getDImages(java.lang.String)
	 */
	@Override
	public List<DImages> getDImages(String imgname)
			throws EcDcsRetrieveException {
		List<DImages> coll = null;
		try {
			coll = getImages(imgname, new DImages());
			return coll;
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.mdt.dcs.dao.EcDcsDAO#getRImages(java.lang.String)
	 */
	@Override
	public List<RImages> getRImages(String imgname)
			throws EcDcsRetrieveException {
		List<RImages> coll = null;
		try {
			coll = getImages(imgname, new RImages());
			return coll;
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.mdt.dcs.dao.EcDcsDAO#findBImages(java.lang.Long)
	 */
	@Override
	public BImages findBImages(Long imgid) throws EcDcsRetrieveException {
		Query q = getImageByIdQuery(BImages.QUERY_GET_IMAGES_BYID, imgid);

		try {
			return (BImages) q.getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.mdt.dcs.dao.EcDcsDAO#findDImages(java.lang.Long)
	 */
	@Override
	public DImages findDImages(Long imgid) throws EcDcsRetrieveException {
		Query q = getImageByIdQuery(DImages.QUERY_GET_IMAGES_BYID, imgid);

		try {
			return (DImages) q.getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.mdt.dcs.dao.EcDcsDAO#findRImages(java.lang.Long)
	 */
	@Override
	public RImages findRImages(Long imgid) throws EcDcsRetrieveException {
		Query q = getImageByIdQuery(RImages.QUERY_GET_IMAGES_BYID, imgid);

		try {
			return (RImages) q.getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.mdt.dcs.dao.EcDcsDAO#getAl(atlas.mdt.dcs.data.AlSupId,
	 * java.lang.Object)
	 */
	@Override
	public <T, M extends AlSupId> T getAl(M alId, T obj)
			throws EcDcsRetrieveException {
		T dbobj = (T) getEntityManager().find(obj.getClass(), alId);
		return dbobj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.mdt.dcs.dao.EcDcsDAO#insertImages(java.util.List)
	 */
	@Override
	public <T> void insertImages(List<T> imagelist, T obj)
			throws EcDcsIoException {
		try {
			if (imagelist != null) {
				int i = 0;
				for (T img : imagelist) {
					if (img instanceof EcConfImage) {
						// verify if image name is already present
						T storedimg = findImage(
								((EcConfImage) img).getImageName(), obj);
						if (storedimg != null) {
							throw new EcDcsIoException(
									"Cannot store image with name "
											+ ((EcConfImage) storedimg)
													.getImageName());
						} else {
							i++;
							// object by name was not found, try to insert it
							log.info("Storing image " + img);
							getEntityManager().persist(img);
							if (i % 100 == 0)
								getEntityManager().flush();
						}
					} else {
						throw new EcDcsIoException(
								"Object type is not correct....");
					}
				}
				getEntityManager().flush();
			}
		} catch (Exception e) {
			throw new EcDcsIoException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.mdt.dcs.dao.EcDcsDAO#getAlCycerrHistory(java.util.Date,
	 * java.util.Date)
	 */
	@Override
	public List<AlCycerr> getAlCycerrHistory(Date since, Date until)
			throws EcDcsRetrieveException {
		Query q;
		q = getEntityManager().createNamedQuery(AlCycerr.QUERY_GET_HISTORY);
		BigDecimal start = new BigDecimal(since.getTime() / 1000L);
		BigDecimal end = new BigDecimal(until.getTime() / 1000L);
		q.setParameter("since", start);
		q.setParameter("until", end);

		List<AlCycerr> coll = null;
		try {
			coll = q.getResultList();
			return coll;
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.mdt.dcs.dao.EcDcsDAO#findAlCycerr(java.lang.Long)
	 */
	@Override
	public AlCycerr findAlCycerr(Long cycid) throws EcDcsRetrieveException {
		// TODO Auto-generated method stub
		try {
			Query q;
			q = getEntityManager().createNamedQuery(
					AlCycerr.QUERY_FIND_CYCLEBYID);
			q.setParameter("id", cycid);
			return (AlCycerr) q.getSingleResult();
		} catch (Exception e) {
			throw new EcDcsRetrieveException(e.getMessage());
		}

	}
}
