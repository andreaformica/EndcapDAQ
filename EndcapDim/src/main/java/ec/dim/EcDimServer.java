/**
 * 
 */
package ec.dim;

import io.utils.ILoader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;

import atlas.mdt.dcs.client.EcDcsDAODB;
import atlas.mdt.dcs.data.AlB;
import atlas.mdt.dcs.data.AlD;
import atlas.mdt.dcs.data.AlR;
import atlas.mdt.dcs.data.AlT;
import atlas.mdt.dcs.io.AlLoader;
import dim.DimCommand;
import dim.DimErrorHandler;
import dim.DimExitHandler;
import dim.DimServer;
import dim.DimTimer;
import ec.dim.buffer.BufferStorage;

/**
 * @author formica
 * 
 */
public class EcDimServer {

	public static Log log = LogFactory.getLog(EcDimServer.class
			.getName());

	public static boolean store2db = false;

	private EcDcsDAODB db = null;

	public static SimpleDateFormat df = new SimpleDateFormat(
			"yyyy/MM/dd-HH:mm:ss:z");

	protected BufferStorage<AlB> albBuffer = null;
	protected BufferStorage<AlD> aldBuffer = null;
	protected BufferStorage<AlR> alrBuffer = null;
	protected BufferStorage<AlT> altBuffer = null;

	protected String log4jconfig = "file://" + System.getProperty("user.dir")
			+ "/log4j.xml";

	/**
	 * 
	 */
	public EcDimServer() {
		super();
	}

	public synchronized EcDcsDAODB getDB() {
		if (db == null) {
			String ds = System.getProperty("hb.datasource");
			log.debug("getDB : Using datasource " + ds);
			db = new EcDcsDAODB();
		}
		return db;
	}

	public synchronized InputStream getIstreamFromString(String buf) {
		InputStream is = new ByteArrayInputStream(buf.getBytes());
		return is;
	}

	public synchronized <T> List<T> getAlHistory(Date since, Date until, T obj) {
		return getDB().getAlHistory(since, until, obj);
	}

	public synchronized <T> void storeAl(List<T> alimages) {
		getDB().storeAl(alimages);
	}

	public void loadData(String since, String until) {
		// db.testRetrievalAl("2000/01/10-00:00:00:GMT","2000/01/11-15:00:00:GMT");
		db.testRetrievalAl(null, until);
	}

	public synchronized <T> BufferStorage<T> getBufferStorage(T obj) {
		if (obj instanceof AlB) {
			if (albBuffer == null) {
				albBuffer = new BufferStorage<AlB>();
			}
			return (BufferStorage<T>) albBuffer;
		} else if (obj instanceof AlD) {
			if (aldBuffer == null) {
				aldBuffer = new BufferStorage<AlD>();
			}
			return (BufferStorage<T>) aldBuffer;
		} else if (obj instanceof AlT) {
			if (altBuffer == null) {
				altBuffer = new BufferStorage<AlT>();
			}
			return (BufferStorage<T>) altBuffer;
		} else if (obj instanceof AlR) {
			if (alrBuffer == null) {
				alrBuffer = new BufferStorage<AlR>();
			}
			return (BufferStorage<T>) alrBuffer;
		}
		return null;
	}

	public void setLog4jConfig(String log4j) {
		log4jconfig = log4j;
	}

	public void initLogger() throws MalformedURLException,
			FileNotFoundException {
		File log4j = null;
		try {
			URL url = getClass().getResource(log4jconfig);
			if (url == null) {
				log4j = new File(new URI(log4jconfig));
				if (log4j.exists()) {
					url = new URL(log4jconfig);
				} else {
					throw new FileNotFoundException("file " + log4jconfig
							+ " does not exist");
				}
			}
			DOMConfigurator.configure(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	///// This does not work
	public DimCommand createDimCommandAlB(String cmdname) {
		// Create dim command handlers...DOES NOT WORK !!!!!!
		DimCommand cmndB = new DimCommand(cmdname, "C") {

			public BufferStorage<AlB> albBuffer = getBufferStorage(new AlB());

			public void commandHandler() {
				log.debug("Received ALB: " + getString()+" with length "+getString().length()+" and datasize "+getDataSize());
				// call db storage after parsing
				int maxlength = Math.min(getString().length(), getDataSize());
				String clob = getString().substring(0, maxlength);
				log.debug("Received ALB CLOB : " + clob);
				ILoader<AlB> bloader = new AlLoader<AlB>(new AlB());

				List<AlB> albimages = getDB().loadAl(
						getIstreamFromString(clob), bloader);
				int cycle = -1;
				if (albimages.size()>0) {
					AlB _alb = albimages.get(0);
					cycle = _alb.getCycleNumber().intValue();
				}
				log.info("Loaded B images at " + new Date()
						+ " size " + albimages.size()+ " cycle "+cycle);
				if (albBuffer.addEntryList(albimages)) {
					int stsize = albBuffer.getData().size();
					if (store2db) storeAl(albBuffer.getData());
					albBuffer.clearData();
					if (store2db) log.info("Stored B images at " + new Date()
							+ " size " + stsize);
				}
			}
		};
		return cmndB;
	}
	
	public static void main(String[] args) {

		System.getProperties().setProperty("hb.datasource", "EcDcsDB");
//		System.getProperties().setProperty("dim.storage.flag", "on");
		Properties props = System.getProperties();
		if (props != null && props.containsKey("dim.storage.flag")) {
			String val = props.getProperty("dim.storage.flag");
			if (val != null && val.equals("on")) {
				store2db=true;
			}
		}
		final EcDimServer ecsrv = new EcDimServer();
		EcDcsDAODB mydb = ecsrv.getDB();
		log.info("Initialized EntityManager before DimCommand..."
				+ mydb);

		if (System.getProperty("log4j.configuration") != null) {
			ecsrv.setLog4jConfig(System.getProperty("log4j.configuration"));
		}
		try {
			ecsrv.initLogger();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DimErrorHandler eid = new DimErrorHandler() {
			public void errorHandler(int severity, int code, String msg) {
				if (code == DIMSVCDUPLC)
					log.warn("Service already declared");
				log.error("Error: " + msg + " sev: " + severity);
			}
		};
		DimServer.addErrorHandler(eid);

		DimExitHandler exid = new DimExitHandler() {
			public void exitHandler(int code) {
				log.info("Exit: " + code);
			}
		};
		DimServer.addExitHandler(exid);

		// Create dim command handlers
//		DimCommand cmndB = ecsrv.createDimCommandAlB("/EC/DAQ/ALB");
//		DimCommand cmndB2000 = ecsrv.createDimCommandAlB("/EC/DAQ/2000/ALB");
//		DimCommand cmndB3000 = ecsrv.createDimCommandAlB("/EC/DAQ/3000/ALB");
//		DimCommand cmndB4000 = ecsrv.createDimCommandAlB("/EC/DAQ/4000/ALB");

		DimCommand cmndB = new DimCommand("EC/DAQ/ALB", "C") {

//			public BufferStorage<AlB> albBuffer = ecsrv
//					.getBufferStorage(new AlB());

			public void commandHandler() {
				log.debug("Received ALB: " + getString()+" with length "+getString().length()+" and datasize "+getDataSize());
				// call db storage after parsing
				int maxlength = Math.min(getString().length(), getDataSize());
				String clob = getString().substring(0, maxlength);
				log.debug("Received ALB CLOB : " + clob);
				ILoader<AlB> bloader = new AlLoader<AlB>(new AlB());

/*				List<AlB> albimages = ecsrv.getDB().loadAl(
						ecsrv.getIstreamFromString(clob), bloader);
				int cycle = -1;
				if (albimages.size()>0) {
					AlB _alb = albimages.get(0);
					cycle = _alb.getCycleNumber().intValue();
				}
				log.info("Loaded B images at " + new Date()
						+ " size " + albimages.size()+ " cycle "+cycle);
				if (albBuffer.addEntryList(albimages)) {
					int stsize = albBuffer.getData().size();
					if (store2db) ecsrv.storeAl(albBuffer.getData());
					albBuffer.clearData();
					if (store2db) log.info("Stored B images at " + new Date()
							+ " size " + stsize);
				}
*/			}
		};
		
		log.info("Created dim command "+cmndB);
		log.info("   data address:"+cmndB.getDataAddress());



		DimCommand cmnd2B = new DimCommand("EC/DAQ/ALB/2000", "C") {

//			public BufferStorage<AlB> albBuffer = ecsrv
//					.getBufferStorage(new AlB());

			public void commandHandler() {
				log.debug("Received ALB: " + getString()+" with length "+getString().length()+" and datasize "+getDataSize());
				// call db storage after parsing
				int maxlength = Math.min(getString().length(), getDataSize());
				String clob = getString().substring(0, maxlength);
				log.debug("Received ALB CLOB : " + clob);
//				ILoader bloader = new AlbLoader(null);
				ILoader<AlB> bloader = new AlLoader<AlB>(new AlB());

//				List<AlB> albimages = ecsrv.getDB().loadAl(
//						ecsrv.getIstreamFromString(clob), bloader);
//				int cycle = -1;
//				if (albimages.size()>0) {
//					AlB _alb = albimages.get(0);
//					cycle = _alb.getCycleNumber().intValue();
//				}
//				log.info("Loaded B images at " + new Date()
//						+ " size " + albimages.size()+ " cycle "+cycle);
//				if (albBuffer.addEntryList(albimages)) {
//					int stsize = albBuffer.getData().size();
//					if (store2db) ecsrv.storeAl(albBuffer.getData());
//					albBuffer.clearData();
//					if (store2db) log.info("Stored B images at " + new Date()
//							+ " size " + stsize);
//				}
			}
		};

		DimCommand cmnd3B = new DimCommand("EC/DAQ/3000/ALB", "C") {

			public BufferStorage<AlB> albBuffer = ecsrv
					.getBufferStorage(new AlB());

			public void commandHandler() {
				log.debug("Received ALB: " + getString()+" with length "+getString().length()+" and datasize "+getDataSize());
				// call db storage after parsing
				int maxlength = Math.min(getString().length(), getDataSize());
				String clob = getString().substring(0, maxlength);
				log.debug("Received ALB CLOB : " + clob);
//				ILoader bloader = new AlbLoader(null);
				ILoader<AlB> bloader = new AlLoader<AlB>(new AlB());

				List<AlB> albimages = ecsrv.getDB().loadAl(
						ecsrv.getIstreamFromString(clob), bloader);
				int cycle = -1;
				if (albimages.size()>0) {
					AlB _alb = albimages.get(0);
					cycle = _alb.getCycleNumber().intValue();
				}
				log.info("Loaded B images at " + new Date()
						+ " size " + albimages.size()+ " cycle "+cycle);
				if (albBuffer.addEntryList(albimages)) {
					int stsize = albBuffer.getData().size();
					if (store2db) ecsrv.storeAl(albBuffer.getData());
					albBuffer.clearData();
					if (store2db) log.info("Stored B images at " + new Date()
							+ " size " + stsize);
				}
			}
		};

		DimCommand cmndT = new DimCommand("EC/DAQ/7000/ALT", "C") {

			public BufferStorage<AlT> altBuffer = ecsrv
					.getBufferStorage(new AlT());

			public void commandHandler() {
				log.debug("Received ALT: " + getString()+" with length "+getString().length()+" and datasize "+getDataSize());
				// call db storage after parsing
				int maxlength = Math.min(getString().length(), getDataSize());
				String clob = getString().substring(0, maxlength);
				log.debug("Received ALT CLOB : " + clob);
//				ILoader tloader = new AltLoader(null);
				ILoader<AlT> tloader = new AlLoader<AlT>(new AlT());
				List<AlT> altimages = ecsrv.getDB().loadAl(
						ecsrv.getIstreamFromString(clob), tloader);
				int cycle = -1;
				if (altimages.size()>0) {
					AlT _alt = altimages.get(0);
					cycle = _alt.getCycleNumber().intValue();
				}
				log.info("Loaded T images at " + new Date()
						+ " size " + altimages.size()+ " cycle "+cycle);
				if (altBuffer.addEntryList(altimages)) {
					int stsize = altBuffer.getData().size();
					if (store2db)  ecsrv.storeAl(altBuffer.getData());
					altBuffer.clearData();
					if (store2db) log.info("Stored T images at " + new Date()
							+ " size " + stsize);
				}
			}
		};
		
		DimCommand cmnd8T = new DimCommand("EC/DAQ/8000/ALT", "C") {

			public BufferStorage<AlT> altBuffer = ecsrv
					.getBufferStorage(new AlT());

			public void commandHandler() {
				log.debug("Received ALT: " + getString()+" with length "+getString().length()+" and datasize "+getDataSize());
				// call db storage after parsing
				int maxlength = Math.min(getString().length(), getDataSize());
				String clob = getString().substring(0, maxlength);
				log.debug("Received ALT CLOB : " + clob);
//				ILoader tloader = new AltLoader(null);
				ILoader<AlT> tloader = new AlLoader<AlT>(new AlT());
				List<AlT> altimages = ecsrv.getDB().loadAl(
						ecsrv.getIstreamFromString(clob), tloader);
				int cycle = -1;
				if (altimages.size()>0) {
					AlT _alt = altimages.get(0);
					cycle = _alt.getCycleNumber().intValue();
				}
				log.info("Loaded T images at " + new Date()
						+ " size " + altimages.size()+ " cycle "+cycle);
				if (altBuffer.addEntryList(altimages)) {
					int stsize = altBuffer.getData().size();
					if (store2db)  ecsrv.storeAl(altBuffer.getData());
					altBuffer.clearData();
					if (store2db) log.info("Stored T images at " + new Date()
							+ " size " + stsize);
				}
			}
		};

		DimCommand cmnd9T = new DimCommand("EC/DAQ/9000/ALT", "C") {

			public BufferStorage<AlT> altBuffer = ecsrv
					.getBufferStorage(new AlT());

			public void commandHandler() {
				log.debug("Received ALT: " + getString()+" with length "+getString().length()+" and datasize "+getDataSize());
				// call db storage after parsing
				int maxlength = Math.min(getString().length(), getDataSize());
				String clob = getString().substring(0, maxlength);
				log.debug("Received ALT CLOB : " + clob);
//				ILoader tloader = new AltLoader(null);
				ILoader<AlT> tloader = new AlLoader<AlT>(new AlT());
				List<AlT> altimages = ecsrv.getDB().loadAl(
						ecsrv.getIstreamFromString(clob), tloader);
				int cycle = -1;
				if (altimages.size()>0) {
					AlT _alt = altimages.get(0);
					cycle = _alt.getCycleNumber().intValue();
				}
				log.info("Loaded T images at " + new Date()
						+ " size " + altimages.size()+ " cycle "+cycle);
				if (altBuffer.addEntryList(altimages)) {
					int stsize = altBuffer.getData().size();
					if (store2db)  ecsrv.storeAl(altBuffer.getData());
					altBuffer.clearData();
					if (store2db) log.info("Stored T images at " + new Date()
							+ " size " + stsize);
				}
			}
		};

		DimCommand cmndD = new DimCommand("EC/DAQ/ALD/4000", "C") {

//			public BufferStorage<AlD> aldBuffer = ecsrv
//					.getBufferStorage(new AlD());

			public void commandHandler() {
				log.debug("Received ALD: " + getString()+" with length "+getString().length()+" and datasize "+getDataSize());
				// call db storage after parsing
				int maxlength = Math.min(getString().length(), getDataSize());
				String clob = getString().substring(0, maxlength);
				log.debug("Received ALD CLOB : " + clob);
//				ILoader tloader = new AltLoader(null);
				ILoader<AlD> dloader = new AlLoader<AlD>(new AlD());
/*
				List<AlD> aldimages = ecsrv.getDB().loadAl(
						ecsrv.getIstreamFromString(clob), dloader);
				int cycle = -1;
				if (aldimages.size()>0) {
					AlD _ald = aldimages.get(0);
					cycle = _ald.getCycleNumber().intValue();
				}
				log.info("Loaded D images at " + new Date()
						+ " size " + aldimages.size()+ " cycle "+cycle);
				if (aldBuffer.addEntryList(aldimages)) {
					int stsize = aldBuffer.getData().size();
					if (store2db)  ecsrv.storeAl(aldBuffer.getData());
					aldBuffer.clearData();
					if (store2db) log.info("Stored D images at " + new Date()
							+ " size " + stsize);
				}
				*/
			}
		};
		
		DimCommand cmnd5D = new DimCommand("EC/DAQ/5000/ALD", "C") {

			public BufferStorage<AlD> aldBuffer = ecsrv
					.getBufferStorage(new AlD());

			public void commandHandler() {
				log.debug("Received ALD: " + getString()+" with length "+getString().length()+" and datasize "+getDataSize());
				// call db storage after parsing
				int maxlength = Math.min(getString().length(), getDataSize());
				String clob = getString().substring(0, maxlength);
				log.debug("Received ALD CLOB : " + clob);
//				ILoader tloader = new AltLoader(null);
				ILoader<AlD> dloader = new AlLoader<AlD>(new AlD());
				List<AlD> aldimages = ecsrv.getDB().loadAl(
						ecsrv.getIstreamFromString(clob), dloader);
				int cycle = -1;
				if (aldimages.size()>0) {
					AlD _ald = aldimages.get(0);
					cycle = _ald.getCycleNumber().intValue();
				}
				log.info("Loaded D images at " + new Date()
						+ " size " + aldimages.size()+ " cycle "+cycle);
				if (aldBuffer.addEntryList(aldimages)) {
					int stsize = aldBuffer.getData().size();
					if (store2db)  ecsrv.storeAl(aldBuffer.getData());
					aldBuffer.clearData();
					if (store2db) log.info("Stored D images at " + new Date()
							+ " size " + stsize);
				}
			}
		};
		
		DimCommand cmnd6D = new DimCommand("EC/DAQ/6000/ALD", "C") {

			public BufferStorage<AlD> aldBuffer = ecsrv
					.getBufferStorage(new AlD());

			public void commandHandler() {
				log.debug("Received ALD: " + getString()+" with length "+getString().length()+" and datasize "+getDataSize());
				// call db storage after parsing
				int maxlength = Math.min(getString().length(), getDataSize());
				String clob = getString().substring(0, maxlength);
				log.debug("Received ALD CLOB : " + clob);
//				ILoader dloader = new AldLoader(null);
				ILoader<AlD> dloader = new AlLoader<AlD>(new AlD());
				List<AlD> aldimages = ecsrv.getDB().loadAl(
						ecsrv.getIstreamFromString(clob), dloader);
				int cycle = -1;
				if (aldimages.size()>0) {
					AlD _ald = aldimages.get(0);
					cycle = _ald.getCycleNumber().intValue();
				}
				log.info("Loaded D images at " + new Date()
						+ " size " + aldimages.size()+ " cycle "+cycle);
				if (aldBuffer.addEntryList(aldimages)) {
					int stsize = aldBuffer.getData().size();
					if (store2db)  ecsrv.storeAl(aldBuffer.getData());
					aldBuffer.clearData();
					if (store2db) log.info("Stored D images at " + new Date()
							+ " size " + stsize);
				}
			}
		};


		DimCommand cmndR = new DimCommand("EC/DAQ/ALR", "C") {

			private BufferStorage<AlR> alrBuffer = ecsrv
					.getBufferStorage(new AlR());

			public void commandHandler() {
				log.debug("Received ALR: " + getString()+" with length "+getString().length()+" and datasize "+getDataSize());
//				System.out.println("Size of string "+getDataSize()+" allocated "+getAllocatedSize());
				int maxlength = Math.min(getString().length(), getDataSize());
				String clob = getString().substring(0, maxlength);
				log.debug("Received ALR CLOB : " + clob);
				// call db storage after parsing
//				ILoader rloader = new AlrLoader(null);
				ILoader<AlR> rloader = new AlLoader<AlR>(new AlR());
				List<AlR> alrimages = ecsrv.getDB().loadAl(
						ecsrv.getIstreamFromString(clob), rloader);
				int cycle = -1;
				if (alrimages.size()>0) {
					AlR _alr = alrimages.get(0);
					cycle = _alr.getCycleNumber().intValue();
				}
				log.info("Loaded R images at " + new Date()
						+ " size " + alrimages.size()+" cycle "+cycle);
				if (alrBuffer.addEntryList(alrimages)) {
					int stsize = alrBuffer.getData().size();
					if (store2db)  ecsrv.storeAl(alrBuffer.getData());
					alrBuffer.clearData();
					if (store2db) log.info("Stored R images at " + new Date()
							+ " size " + stsize);
				}
			}
		};

		DimCommand cmndBBufSize = new DimCommand("EC/DAQ/ALB/BUFSIZE", "I") {

			public void commandHandler() {
				log.info("Received B Buf Size: " + getInt());
				// call db storage after parsing
				ecsrv.getBufferStorage(new AlB()).setBufferSize(getInt());
			}
		};

		DimCommand cmndDBufSize = new DimCommand("EC/DAQ/ALD/BUFSIZE", "I") {

			public void commandHandler() {
				log.info("Received D Buf Size: " + getInt());
				// call db storage after parsing
				ecsrv.getBufferStorage(new AlD()).setBufferSize(getInt());
			}
		};

		DimCommand cmndRBufSize = new DimCommand("EC/DAQ/ALR/BUFSIZE", "I") {

			public void commandHandler() {
				log.info("Received R Buf Size: " + getInt());
				// call db storage after parsing
				ecsrv.getBufferStorage(new AlR()).setBufferSize(getInt());
			}
		};
		DimCommand cmndTBufSize = new DimCommand("EC/DAQ/ALT/BUFSIZE", "I") {

			public void commandHandler() {
				log.info("Received T Buf Size: " + getInt());
				// call db storage after parsing
				ecsrv.getBufferStorage(new AlT()).setBufferSize(getInt());
			}
		};

		DimServer.start("TEST");
		// Then the rest of the main loop
		while (true) {
			DimTimer.sleep(1);
		}
	}

}
