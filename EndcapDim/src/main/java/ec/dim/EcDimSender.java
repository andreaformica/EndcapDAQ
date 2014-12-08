/**
 * 
 */
package ec.dim;

import io.utils.ILoader;

import java.io.File;
import java.io.FileInputStream;
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
import dim.DimClient;
import dim.DimTimer;
import ec.dim.tools.ChannelNameResolver;

/**
 * @author formica
 * 
 */
public class EcDimSender {

	public static Log log = LogFactory.getLog(EcDimSender.class.getName());

	EcDcsDAODB db = null;
	public static SimpleDateFormat df = new SimpleDateFormat(
			"yyyy/MM/dd-HH:mm:ss:z");

	Options options = new Options();

	String typeName = "none";
	public static String dimcommand = null;
	public static String action = null;

	public static long icycle = 0;

	protected String log4jconfig = "file://" + System.getProperty("user.dir");

	/**
	 * 
	 */
	public EcDimSender() {
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

	@SuppressWarnings("static-access")
	private void initOptions() {

		Option type = OptionBuilder.withLongOpt("type").hasArg()
				.withArgName("AlB|AlD|AlR|AlT")
				.withDescription("Object class type name").create("T");
		Option command = OptionBuilder.withLongOpt("command").hasArg()
				.withArgName("COMMAND")
				.withDescription("Command string value for DIM").create("C");

		Option action = OptionBuilder
				.withLongOpt("action")
				.hasArg()
				.withArgName("ACTION")
				.withDescription(
						"The action to be performed determine the content of the send")
				.create("A");

		Option log4j = OptionBuilder.withLongOpt("log4j").hasArg()
				.withDescription("path to log4j.xml file").create("l");

		options.addOption("h", "help", false, "this help message");
		options.addOption(type);
		options.addOption(command);
		options.addOption(action);
		options.addOption(log4j);
	}

	private CommandLine parse(final String[] args) throws ParseException {
		CommandLineParser parser = new PosixParser();
		// parse the command line arguments
		CommandLine line = parser.parse(options, args);
		if (line.hasOption("help")) {
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("EcDimServerByType", options);
			return null;
		}
		if (line.hasOption("type")) {
			typeName = line.getOptionValue("type");
			log.info("Type selected " + typeName);
			System.setProperty("ecchantype", "Sender" + typeName);
		}
		if (line.hasOption("command")) {
			dimcommand = line.getOptionValue("command");
			log.info("DIM command path " + dimcommand);
		}
		if (line.hasOption("action")) {
			action = line.getOptionValue("action");
			log.info("Action is " + action);
		}
		if (line.hasOption("log4j")) {
			log4jconfig = line.getOptionValue("log4j");
			log.info("log4j file configuration path " + log4jconfig);
		} else {
			log4jconfig = (log4jconfig + "/log4j.xml");
		}

		return line;
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
				throw new Exception("");
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

	public <T> List<T> getAlHistory(Date since, Date until, T obj) {
		return getDB().getAlHistory(since, until, obj);
	}

	public void loadData(String since, String until) {
		// db.testRetrievalAl("2000/01/10-00:00:00:GMT","2000/01/11-15:00:00:GMT");
		db.testRetrievalAl(null, until);
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

	public synchronized <T> ILoader<T> getLoader(T obj) {
		log.debug("Get loader for obj " + obj.getClass());
		ILoader<T> loader = null;
		loader = (ILoader<T>) new AlLoader<T>(obj);
		return loader;
	}

	public <T> StringBuffer createBufferFromList(List<T> imagelist, int from,
			int to) {
		StringBuffer buf = new StringBuffer();
		// Date refuntil = df.parse("2000/03/01-00:00:00:GMT");
		int i = 0;
		for (T t : imagelist) {
			if (i >= from && i < to) {
				T shifted = getDB().shiftdates(icycle, t);
				if (i++ < 20) {
					System.out.println("...shifting time for " + t.toString());
					// System.out.println("...new time is       "+shifted.toString());
				}
				String line = shifted.toString();
				buf.append(line + "\n");
			}
		}
		buf.append("#end");
		return buf;
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.getProperties().setProperty("hb.datasource", "EcDcsDB");
		// System.getProperties().setProperty("dim.storage.flag", "on");
		System.setProperty("ecchantype", "SenderClient");
		Properties props = System.getProperties();

		final EcDimSender ecsrv = new EcDimSender();

		if (System.getProperty("log4j.configuration") != null) {
			System.out.println("Setting logging configuration...");
			ecsrv.setLog4jConfig(System.getProperty("log4j.configuration"));
		}

		try {
			ecsrv.init(args);
			System.out.print("Start server of type ...");
			System.out.println(": " + ecsrv.typeName);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		EcDcsDAODB mydb = ecsrv.getDB();
		log.info("Initialized EntityManager before DimCommand..." + mydb);

		try {

			/*
			 * DimInfo srvinfo = new DimInfo("CYCLE_LOGIC/STARTENDRESPONSE",
			 * "not available") { public void infoHandler() {
			 * log.info("Service updated: CYCLE_LOGIC/STARTENDRESPONSE ");
			 * log.info("	Received: "+getString()+" at time "+getTimestamp());
			 * //System.out.println("Quality: "+getQuality());
			 * //System.out.println("Timestamp: "+getTimestamp()); } };
			 */
			// DimServer.start("DimSrv_ClientListener");
			Object objtype = ecsrv.getType(ecsrv.typeName);
			List<?> imageList = ecsrv.getListFromFile(objtype, ecsrv.typeName
					+ ".out");

			//DimTimer.sleep(1);
			// Then the rest of the main loop
			EcDimSender.icycle = 0;
			while (true) {
				//DimTimer.sleep(7);
				Thread.sleep(3000);
				if (action.equals("startcycle")) {
					DimClient.sendCommand("CYCLE_LOGIC/STARTEND/"
							+ ChannelNameResolver.valueOf(ecsrv.typeName)
									.dimChannel(), "start " + icycle);
				} else if (action.equals("endcycle")) {
					DimClient.sendCommand("CYCLE_LOGIC/STARTEND/"
							+ ChannelNameResolver.valueOf(ecsrv.typeName)
									.dimChannel(), "end " + icycle);
				} else if (action.equals("docycle")) {
					log.info("Cycle number is " + icycle);
					DimClient.sendCommand("CYCLE_LOGIC/STARTEND/"
							+ ChannelNameResolver.valueOf(ecsrv.typeName)
									.dimChannel(), "start " + icycle+" #end");
					DimTimer.sleep(5);
					int from = 0;
					int buflength = 2;
					while (from < 100) {
						int fromorig = from;
						from += buflength;
						StringBuffer buf = ecsrv.createBufferFromList(
								imageList, fromorig, from);
						log.info("Sending buffer "+buf.toString()+" to "+ecsrv.dimcommand);
						DimClient.sendCommand(ecsrv.dimcommand, buf.toString());
//						DimTimer.sleep(5);
						Thread.sleep(10);
					}
					DimTimer.sleep(2);
					log.info("End of cycle number " + icycle);
					DimClient.sendCommand("CYCLE_LOGIC/STARTEND/"
							+ ChannelNameResolver.valueOf(ecsrv.typeName)
									.dimChannel(), "end " + icycle+" #end");
				}

				EcDimSender.icycle++;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

}
