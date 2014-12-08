/**
 * 
 */
package ec.dim;

import io.utils.ILoader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;

import atlas.mdt.dcs.client.EcDcsDAODB;
import atlas.mdt.dcs.data.AlB;
import atlas.mdt.dcs.data.AlD;
import atlas.mdt.dcs.data.AlR;
import atlas.mdt.dcs.data.AlT;
import atlas.mdt.dcs.io.AlLoader;
import ec.dim.buffer.BufferStorage;
import ec.dim.tools.EndcapImageComparator;

/**
 * @author formica
 * 
 */
public class EcDimMonitoringServer {

	public static Log log = LogFactory.getLog(EcDimMonitoringServer.class
			.getName());

	public static boolean store2db = false;

	private EcDcsDAODB db = null;

	Options options = new Options();
	String typeName = "none";
	public static String dimcommand = "check";
	public static Date sinceTime = null;
	public static Date untilTime = null;

	public static String filename = "./alb.out";

	public static SimpleDateFormat df = new SimpleDateFormat(
			"yyyy/MM/dd-HH:mm:ss:z");

	protected BufferStorage<AlB> albBuffer = null;
	protected BufferStorage<AlD> aldBuffer = null;
	protected BufferStorage<AlR> alrBuffer = null;
	protected BufferStorage<AlT> altBuffer = null;

	protected String log4jconfig = "file://" + System.getProperty("user.dir");

	/**
	 * 
	 */
	public EcDimMonitoringServer() {
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

	/**
	 * add options.
	 */
	@SuppressWarnings("static-access")
	private void initOptions() {

		Option type = OptionBuilder.withLongOpt("type").hasArg()
				.withArgName("AlB|AlD|AlR|AlT")
				.withDescription("Object class type name").create("T");
		Option command = OptionBuilder
				.withLongOpt("command")
				.hasArg()
				.withArgName("COMMAND [check]")
				.withDescription(
						"Command string value for Monitoring: history | check")
				.create("C");

		Option since = OptionBuilder.withLongOpt("since").hasArg()
				.withArgName("SINCE_TIME")
				.withDescription("Since time, used only with -C check command")
				.create("s");

		Option until = OptionBuilder.withLongOpt("until").hasArg()
				.withArgName("UNTIL_TIME")
				.withDescription("Until time, used only with -C check command")
				.create("u");

		Option log4j = OptionBuilder.withLongOpt("log4j").hasArg()
				.withDescription("path to log4j.xml file").create("l");

		options.addOption("h", "help", false, "this help message");
		options.addOption(type);
		options.addOption(since);
		options.addOption(until);
		options.addOption(command);
		options.addOption(log4j);
	}

	private CommandLine parse(final String[] args) throws ParseException {
		CommandLineParser parser = new PosixParser();
		// parse the command line arguments
		CommandLine line = parser.parse(options, args);
		if (line.hasOption("help")) {
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("EcDimMonitoringServer", options);
			return null;
		}
		if (line.hasOption("type")) {
			typeName = line.getOptionValue("type");
			filename = "./" + typeName.toLowerCase() + ".out";
			log.info("Type selected " + typeName);
			System.setProperty("ecchantype", typeName);
		}
		if (line.hasOption("command")) {
			dimcommand = line.getOptionValue("command");
			log.info("DIM command path " + dimcommand);
		}
		if (line.hasOption("s")) {
			try {
				sinceTime = df.parse(line.getOptionValue("s"));
			} catch (java.text.ParseException e) {
				throw new ParseException(e.getMessage());
			}
		}
		if (line.hasOption("u")) {
			try {
				untilTime = df.parse(line.getOptionValue("u"));
			} catch (java.text.ParseException e) {
				throw new ParseException(e.getMessage());
			}
		}

		if (line.hasOption("log4j")) {
			log4jconfig = line.getOptionValue("log4j");
			log.info("log4j file configuration path " + log4jconfig);
		} else {
			log4jconfig = (log4jconfig + "/log4j.xml");
		}

		return line;
	}

	public synchronized InputStream getIstreamFromString(String buf) {
		InputStream is = new ByteArrayInputStream(buf.getBytes());
		return is;
	}

	public synchronized InputStream getInputStreamFromFile(String fname) {
		File inpf = new File(fname);
		try {
			FileInputStream is = new FileInputStream(inpf);
			return is;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
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

	public synchronized <T> ILoader<T> getLoader(T obj) {
		log.debug("Get loader for obj " + obj.getClass());
		ILoader<T> loader = null;
		loader = (ILoader<T>) new AlLoader<T>(obj);
		
		return loader;
	}

	public synchronized <T> T getType(String type) {
		T obj = null;
		if (type.equals("AlB")) {
			obj = (T) new AlB();
		} else if (type.equals("AlD")) {
			obj = (T) new AlD();
		} else if (type.equals("AlT")) {
			obj = (T) new AlT();
		} else if (type.equals("AlR")) {
			obj = (T) new AlR();
		}
		return obj;
	}

	public <T> List<T> getListFromFile(T obj, String fname) {
		ILoader<T> loader = getLoader(obj);
		log.debug("Get list from file " + fname + " using loader " + loader);
		List<T> allist = null;
		try {
			allist = (List<T>) loader.parse(getInputStreamFromFile(fname));
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allist;
	}

	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
	public synchronized <T> EndcapImageComparator<T> getEndcapImageComparator(
			T obj) {
		if (obj instanceof AlB) {
			return (EndcapImageComparator<T>) new EndcapImageComparator<AlB>(
					getDB());
		} else if (obj instanceof AlD) {
			return (EndcapImageComparator<T>) new EndcapImageComparator<AlD>(
					getDB());
		} else if (obj instanceof AlT) {
			return (EndcapImageComparator<T>) new EndcapImageComparator<AlT>(
					getDB());
		} else if (obj instanceof AlR) {
			return (EndcapImageComparator<T>) new EndcapImageComparator<AlR>(
					getDB());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> void compareList(String fname, T obj) {
		List<T> listal = getListFromFile(obj, fname);
		for (T al : listal) {
			log.info("Found " + al);
		}
		EndcapImageComparator<T> comparator = getEndcapImageComparator(obj);
		comparator.verifyInsertion(listal);
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
			System.out.println("Configuring logger using " + url.getPath());
			DOMConfigurator.configure(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see align.client.cli.base.ActionCommand#init()
	 */
	public void init(final String[] args) throws Exception {
		try {
			initOptions();
			if (parse(args) == null) {
				throw new Exception("exit after parsing arguments");
			}
			initLogger();
		} catch (ParseException e) {
			throw new Exception(e);
		} catch (UnsupportedOperationException e) {
			throw new Exception(e);
		} catch (FileNotFoundException e) {
			throw new Exception(e);
		} catch (MalformedURLException e) {
			throw new Exception(e);
		}
	}

	public <T> StringBuffer createBufferFromList(List<T> imagelist) {
		StringBuffer buf = new StringBuffer();
		// Date refuntil = df.parse("2000/03/01-00:00:00:GMT");
		int i = 0;
		for (T t : imagelist) {

			// T shifted = getDB().shiftdates(null, t);
			// if (i++ < 20) {
			// System.out.println("...shifting time for " + t.toString());
			// //
			// System.out.println("...new time is       "+shifted.toString());
			// }
			String line = t.toString();
			buf.append(line + "\n");
		}
		return buf;
	}

	public static void main(String[] args) {

		System.getProperties().setProperty("hb.datasource", "EcDcsDB");
		// System.getProperties().setProperty("dim.storage.flag", "on");
		Properties props = System.getProperties();
		if (props != null && props.containsKey("dim.storage.flag")) {
			String val = props.getProperty("dim.storage.flag");
			if (val != null && val.equals("on")) {
				store2db = true;
			}
		}
		final EcDimMonitoringServer ecsrv = new EcDimMonitoringServer();

		try {
			ecsrv.init(args);
			System.out.print("Start server of type ...");
			System.out.println(": " + ecsrv.typeName);

			Object objtype = ecsrv.getType(ecsrv.typeName);
			if (ecsrv.dimcommand.equals("history")) {
				EcDcsDAODB mydb = ecsrv.getDB();
				log.info("Initialized EntityManager..." + mydb);
				StringBuffer buf = ecsrv.createBufferFromList(ecsrv
						.getAlHistory(sinceTime, untilTime, objtype));
				FileOutputStream fos = new FileOutputStream(ecsrv.filename);
				fos.write(buf.toString().getBytes());
				fos.close();
			} else if (ecsrv.dimcommand.equals("check")) {
				EcDcsDAODB mydb = ecsrv.getDB();
				log.info("Initialized EntityManager..." + mydb);
				if (!ecsrv.typeName.equals("none")) {

					/*
					 * Object objtype = ecsrv.getType(ecsrv.typeName); List<?>
					 * listal = ecsrv.getListFromFile(objtype, "./alb.out"); for
					 * (Object al : listal) { log.info("Found " + al); }
					 */
					ecsrv.compareList(ecsrv.filename, objtype);
				}
			} else {
				log.error("Cannot execute any command for args " + args.length
						+ " !");
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		/*
		 * DimErrorHandler eid = new DimErrorHandler() { public void
		 * errorHandler(int severity, int code, String msg) { if (code ==
		 * DIMSVCDUPLC) log.warn("Service already declared");
		 * log.error("Error: " + msg + " sev: " + severity); } };
		 * DimServer.addErrorHandler(eid);
		 * 
		 * DimExitHandler exid = new DimExitHandler() { public void
		 * exitHandler(int code) { log.info("Exit: " + code); } };
		 * DimServer.addExitHandler(exid);
		 * 
		 * 
		 * DimServer.start("TEST"); // Then the rest of the main loop while
		 * (true) { DimTimer.sleep(1); }
		 */
	}

}
