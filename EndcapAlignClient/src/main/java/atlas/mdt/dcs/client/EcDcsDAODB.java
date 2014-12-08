/**
 * 
 */
package atlas.mdt.dcs.client;

import io.utils.ILoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import atlas.mdt.dcs.dao.EcDcsDAO;
import atlas.mdt.dcs.dao.EcDcsRetrieveException;
import atlas.mdt.dcs.data.AlB;
import atlas.mdt.dcs.data.AlCycerr;
import atlas.mdt.dcs.data.AlD;
import atlas.mdt.dcs.data.AlR;
import atlas.mdt.dcs.data.AlSupId;
import atlas.mdt.dcs.data.AlT;
import atlas.mdt.dcs.data.BImages;
import atlas.mdt.dcs.data.DImages;
import atlas.mdt.dcs.data.RImages;
import atlas.mdt.dcs.io.AlLoader;

/**
 * @author formica
 * 
 */
public class EcDcsDAODB {

	public static Log log = LogFactory.getLog(EcDcsDAODB.class.getName());

	private EcDcsDAO ecdcsDAO = null;

	public static final Long ONE_DAY = (60 * 60 * 24) * 1000L;
	public static final Long ONE_HOUR = (60 * 60) * 1000L;

	public static SimpleDateFormat df = new SimpleDateFormat(
			"yyyy/MM/dd-HH:mm:ss:z");

	/**
	 * 
	 */
	public EcDcsDAODB() {
		super();
		String ds = System.getProperty("hb.datasource");
		System.out.println("Using datasource " + ds);
		ecdcsDAO = new EcDcsDAOClient(ds);
		EntityManager em = ecdcsDAO.getEntityManager();
		System.out.println("Entity manager has been configured..."+em.toString());
	}

	public <T> List<T> getAlHistory(Date sincetime, Date until, T obj) {
		EntityManager em = ecdcsDAO.getEntityManager();
		EntityTransaction tx = em.getTransaction();

		try {
			Date since = null;
			if (sincetime != null) {
				since = sincetime;
			} else {
				since = new Date(until.getTime() - ONE_HOUR);
			}
			tx.begin();
			List<T> imglist = ecdcsDAO.getAlHistory(since, until, null, obj);
			tx.commit();
			return imglist;
		} catch (EcDcsRetrieveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public List<AlCycerr> getAlCycerrHistory(Date sincetime, Date until) {
		EntityManager em = ecdcsDAO.getEntityManager();
		EntityTransaction tx = em.getTransaction();

		try {
			Date since = null;
			if (sincetime != null) {
				since = sincetime;
			} else {
				since = new Date(until.getTime() - ONE_HOUR);
			}
			tx.begin();
			List<AlCycerr> cyclist = ecdcsDAO.getAlCycerrHistory(since, until);
			tx.commit();
			return cyclist;
		} catch (EcDcsRetrieveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void testRetrievalAl(String sincetime, String untiltime) {

		Map<Long, BImages> imgMap = new HashMap<Long, BImages>();
		Map<Long, DImages> imgDMap = new HashMap<Long, DImages>();
		Map<Long, RImages> imgRMap = new HashMap<Long, RImages>();
		EntityManager em = ecdcsDAO.getEntityManager();
		EntityTransaction tx = em.getTransaction();

		try {
			Date until = df.parse(untiltime);
			Date since = null;
			if (sincetime != null) {
				since = df.parse(sincetime);
			} else {
				since = new Date(until.getTime() - ONE_HOUR);
			}
			tx.begin();
			// AlB table
			List<AlB> alblist = ecdcsDAO.getAlBHistory(since, until, null);
			List<BImages> bimglist = ecdcsDAO.getBImages(null);
			for (BImages bImages : bimglist) {
				imgMap.put(bImages.getImageId(), bImages);
			}
			File outf = new File("alb.out");
			FileOutputStream fos = new FileOutputStream(outf);
			PrintStream printStream = new PrintStream(fos);
			for (AlB alB : alblist) {
				BImages bimg = imgMap.get(alB.getId().getImageId());
				System.out.println("Getting B image " + bimg + " :" + alB);
				String line = alB.toString();
				dumpFile(printStream, line);
			}
			// AlD table
			List<AlD> aldlist = ecdcsDAO.getAlDHistory(since, until, null);
			List<DImages> dimglist = ecdcsDAO.getDImages(null);
			for (DImages dImages : dimglist) {
				imgDMap.put(dImages.getImageId(), dImages);
			}
			File outfd = new File("ald.out");
			FileOutputStream fosd = new FileOutputStream(outfd);
			PrintStream printStreamD = new PrintStream(fosd);
			for (AlD alD : aldlist) {
				DImages dimg = imgDMap.get(alD.getId().getImageId());
				System.out.println("Getting D image " + dimg + " :" + alD);
				String line = alD.toString();
				dumpFile(printStreamD, line);
			}

			// AlR table
			List<AlR> alrlist = ecdcsDAO.getAlRHistory(since, until, null);
			List<RImages> rimglist = ecdcsDAO.getRImages(null);
			for (RImages rImages : rimglist) {
				imgRMap.put(rImages.getImageId(), rImages);
			}
			File outfr = new File("alr.out");
			FileOutputStream fosr = new FileOutputStream(outfr);
			PrintStream printStreamR = new PrintStream(fosr);
			for (AlR alR : alrlist) {
				RImages rimg = imgRMap.get(alR.getId().getImageId());
				System.out.println("Getting R image " + rimg + " :" + alR);
				String line = alR.toString();
				dumpFile(printStreamR, line);
			}

			tx.commit();
		} catch (EcDcsRetrieveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testLoadAl() {

		try {
			File inpf = new File("alb.out");
			FileInputStream is = new FileInputStream(inpf);
			ILoader loader = new AlLoader<AlB>(new AlB());
			// List<AlB> albimages = (List<AlB>) loader.parse(is);
			List<AlB> albimages = loadAl(is, loader);

			for (AlB alb : albimages) {
				log.info("Found alb image " + alb.toString());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public <T> List<T> loadAl(InputStream is, ILoader loader) {

		try {
			List<T> alimages = (List<T>) loader.parse(is);
			return alimages;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public InputStream getInputStreamFromFile(String fname) {
		File inpf = new File(fname);
		try {
			FileInputStream is = new FileInputStream(inpf);
			return is;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void testStoreAlB(String startingdate, List<AlB> albimages) {
		// this program is equal to the previous, then it loops over the objects
		// in order to change the stime to some fake number
		boolean rollback = false;
		EntityManager em = ecdcsDAO.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			Date refuntil = df.parse("2011/08/10-00:00:00:GMT");
			Date until = df.parse(startingdate);
			log.info("Inserting n=" + albimages.size()
					+ " optical images");
			tx.begin();
			Date since = new Date(until.getTime() - ONE_HOUR);
			BigDecimal meastime = new BigDecimal(since.getTime() / 1000L);
			for (AlB alb : albimages) {
				Long diff = alb.getId().getStime().longValue()
						- refuntil.getTime() / 1000L;
				alb.getId().setStime(
						new BigDecimal(meastime.longValue() + diff));
				em.persist(alb);
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rollback = true;
		} finally {
			if (!rollback) {
				tx.commit();
			} else {
				log.info("Rolling Back transaction... ");
				tx.rollback();
			}
		}
	}

	public void testStoreAlD(String startingdate, List<AlD> aldimages) {
		// this program is equal to the previous, then it loops over the objects
		// in order to change the stime to some fake number
		boolean rollback = false;
		EntityManager em = ecdcsDAO.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			Date refuntil = df.parse("2011/08/10-00:00:00:GMT");
			Date until = df.parse(startingdate);
			log.info("Inserting n=" + aldimages.size()
					+ " optical images");
			tx.begin();
			Date since = new Date(until.getTime() - ONE_HOUR);
			BigDecimal meastime = new BigDecimal(since.getTime() / 1000L);
			for (AlD ald : aldimages) {
				Long diff = ald.getId().getStime().longValue()
						- refuntil.getTime() / 1000L;
				ald.getId().setStime(
						new BigDecimal(meastime.longValue() + diff));
				em.persist(ald);
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rollback = true;
		} finally {
			if (!rollback) {
				tx.commit();
			} else {
				log.info("Rolling Back transaction... ");
				tx.rollback();
			}
		}
	}

	public void testStoreAlR(String startingdate, List<AlR> alrimages) {
		// this program is equal to the previous, then it loops over the objects
		// in order to change the stime to some fake number
		boolean rollback = false;
		EntityManager em = ecdcsDAO.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			Date refuntil = df.parse("2011/08/10-00:00:00:GMT");
			Date until = df.parse(startingdate);
			log.info("Inserting n=" + alrimages.size()
					+ " optical images");
			tx.begin();
			Date since = new Date(until.getTime() - ONE_HOUR);
			BigDecimal meastime = new BigDecimal(since.getTime() / 1000L);
			for (AlR alr : alrimages) {
				Long diff = alr.getId().getStime().longValue()
						- refuntil.getTime() / 1000L;
				alr.getId().setStime(
						new BigDecimal(meastime.longValue() + diff));
				em.persist(alr);
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rollback = true;
		} finally {
			if (!rollback) {
				tx.commit();
			} else {
				log.info("Rolling Back transaction... ");
				tx.rollback();
			}
		}
	}

	public <T> T shiftdates(Long nhours, T anobj) {
		Calendar cal = Calendar.getInstance();
		if (anobj instanceof AlB) {
			AlB alb=(AlB)anobj;
			cal.setTimeInMillis(alb.getId().getStime().longValue()*1000L);
			cal.set(Calendar.YEAR, 2000);
			Date since = new Date(cal.getTime().getTime()  + ONE_HOUR*nhours);
			BigDecimal meastime = new BigDecimal(since.getTime() / 1000L);
//			Long diff = alb.getId().getStime().longValue()
//					- refdate.getTime() / 1000L;
			alb.getId().setStime(
					new BigDecimal(meastime.longValue()));
			
		} else if (anobj instanceof AlD) {
			AlD ald=(AlD)anobj;
			cal.setTimeInMillis(ald.getId().getStime().longValue()*1000L);
			cal.set(Calendar.YEAR, 2000);
			Date since = new Date(cal.getTime().getTime()  + ONE_HOUR*nhours);
			BigDecimal meastime = new BigDecimal(since.getTime() / 1000L);
//			Long diff = ald.getId().getStime().longValue()
//					- refdate.getTime() / 1000L;
			ald.getId().setStime(
					new BigDecimal(meastime.longValue()));
			
		}  else if (anobj instanceof AlR) {
			AlR alr=(AlR)anobj;
			cal.setTimeInMillis(alr.getId().getStime().longValue()*1000L);
			cal.set(Calendar.YEAR, 2000);
			Date since = new Date(cal.getTime().getTime()   + ONE_HOUR*nhours);
			BigDecimal meastime = new BigDecimal(since.getTime() / 1000L);
//			Long diff = alr.getId().getStime().longValue()
//					- refdate.getTime() / 1000L;
			alr.getId().setStime(
					new BigDecimal(meastime.longValue()));
		}  else if (anobj instanceof AlT) {
			AlT alt=(AlT)anobj;
			cal.setTimeInMillis(alt.getId().getStime().longValue()*1000L);
			cal.set(Calendar.YEAR, 2000);
			Date since = new Date(cal.getTime().getTime()   + ONE_HOUR*nhours);
			BigDecimal meastime = new BigDecimal(since.getTime() / 1000L);
//			Long diff = alr.getId().getStime().longValue()
//					- refdate.getTime() / 1000L;
			alt.getId().setStime(
					new BigDecimal(meastime.longValue()));
		}
		return anobj;
	}
	
	public <T> T setCycle(BigDecimal cycle, T anobj) {
		Calendar cal = Calendar.getInstance();
		if (anobj instanceof AlB) {
			AlB alb=(AlB)anobj;
			alb.setCycleNumber(cycle);
			
		} else if (anobj instanceof AlD) {
			AlD ald=(AlD)anobj;
			ald.setCycleNumber(cycle);
			
		}  else if (anobj instanceof AlR) {
			AlR alr=(AlR)anobj;
			alr.setCycleNumber(cycle);
			
		}  else if (anobj instanceof AlT) {
			AlT alt=(AlT)anobj;
			alt.setCycleNumber(cycle);
			
		}
		return anobj;
	}

	
	public <T> void storeAl(List<T> alimages) {
		// this program is equal to the previous, then it loops over the objects
		// in order to change the stime to some fake number
		boolean rollback = false;
		EntityManager em = ecdcsDAO.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			log.info("Inserting n=" + alimages.size()
					+ " optical images");
			tx.begin();
			int counter=0;
			for (T al : alimages) {
				em.persist(al);
				counter++;
			}
			log.info("Stored n=" + counter
					+ " optical images");

		} catch (Exception e) {
			log.error("storeAl received exception while inserting a list of Alx images:\n"+e.getMessage());
			rollback=true;
		} finally {
			if (!rollback) {
				tx.commit();
			} else {
				log.info("Rolling Back transaction... ");
				tx.rollback();
			}
		}
	}

	public <T> void storeImages(List<T> images) {
		// this program is equal to the previous, then it loops over the objects
		// in order to change the stime to some fake number
		boolean rollback = false;
		EntityManager em = ecdcsDAO.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			log.info("Inserting n=" + images.size()
					+ " optical images inside configuration table");
			tx.begin();
			if (images != null && images.size()>0) {
				T animg = images.get(0);
				ecdcsDAO.insertImages(images, animg);
			}
		} catch (Exception e) {
			log.error("storeImages received exception while inserting a list of images:\n"+e.getMessage());
			rollback=true;
		} finally {
			if (!rollback) {
				tx.commit();
			} else {
				log.info("Rolling Back transaction... ");
				tx.rollback();
			}
		}
	}

	public Object findImageById(Long id, Object obj) {
		try {
			return ecdcsDAO.findImageById(id, obj);
		} catch (EcDcsRetrieveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public <T> List<T> findImageByCycle(Long cycle, T obj) {
		try {
			return ecdcsDAO.getImagesByCycle(cycle, obj);
		} catch (EcDcsRetrieveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public <T,M extends AlSupId> T findAlPrimaryKey(M id, T obj) {
		try {
			log.debug("Find id "+id+" of type "+id.getClass());
			return ecdcsDAO.getAl(id, obj);
		} catch (EcDcsRetrieveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected void dumpFile(PrintStream afileos, String line) {
		afileos.println(line);
	}

	
	public <T> void removeImage(T alimage) {
		// this program is equal to the previous, then it loops over the objects
		// in order to change the stime to some fake number
		boolean rollback = false;
		EntityManager em = ecdcsDAO.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			log.info("Removing " + alimage
					+ " optical image");
			tx.begin();
			em.remove(alimage);
		} catch (Exception e) {
			e.printStackTrace();
			rollback=true;
		} finally {
			if (!rollback) {
				tx.commit();
			} else {
				log.info("Rolling Back transaction... ");
				tx.rollback();
			}
		}
	}

	public void storeAlCycerr(AlCycerr cycerr) {
		boolean rollback = false;
		EntityManager em = ecdcsDAO.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			log.info("Storing " + cycerr
					+ " cycle information ");
			tx.begin();
			em.persist(cycerr);
		} catch (Exception e) {
			e.printStackTrace();
			rollback=true;
		} finally {
			if (!rollback) {
				tx.commit();
			} else {
				log.info("Rolling Back transaction... ");
				tx.rollback();
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		//System.getProperties().setProperty("hb.datasource", "EcDcsDB");
		EcDcsDAODB db = new EcDcsDAODB();
		// db.testRetrievalAl("2000/01/10-00:00:00:GMT","2000/01/11-15:00:00:GMT");
		// db.testRetrievalAl(null,"2011/08/10-00:00:00:GMT");
		// db.testLoadAl();
/*
		ILoader<AlB> bloader = new AlLoader<AlB>(new AlB());
		ILoader<AlD> dloader = new AlLoader<AlD>(new AlD());
		ILoader<AlR> rloader = new AlLoader<AlR>(new AlR());

		List<AlB> albimages = db.loadAl(db.getInputStreamFromFile("alb.out"),
				bloader);
		System.out.println("Loaded B images at " + new Date() + " size "
				+ albimages.size());
		List<AlR> alrimages = db.loadAl(db.getInputStreamFromFile("alr.out"),
				rloader);
		System.out.println("Loaded R images at " + new Date() + " size "
				+ alrimages.size());
		List<AlD> aldimages = db.loadAl(db.getInputStreamFromFile("ald.out"),
				dloader);
		System.out.println("Loaded D images at " + new Date() + " size "
				+ aldimages.size());
*/
		Calendar sincetime = Calendar.getInstance();
		sincetime.set(2011, 1, 1);
		Date since = sincetime.getTime();
		Calendar untiltime = Calendar.getInstance();
		sincetime.set(2011, 1, 2);
		Date until = untiltime.getTime();
		
		List<AlCycerr> cycerrlist = db.getAlCycerrHistory(since, until);
		for (AlCycerr alCycerr : cycerrlist) {
			System.out.println(" Found cycle "+alCycerr.getId().getCycleNumber()+" time "+alCycerr.getId().getStartTime());
		}
	}

}
