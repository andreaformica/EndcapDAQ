/**
 * 
 */
package atlas.mdt.dcs.cli;

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
import atlas.mdt.dcs.data.BImages;
import atlas.mdt.dcs.data.DImages;
import atlas.mdt.dcs.data.EcConfImage;
import atlas.mdt.dcs.data.RImages;
import atlas.mdt.dcs.data.TImages;
import atlas.mdt.dcs.io.AlLoader;

/**
 * @author formica
 *
 */
public class EcDcsCli {

	public static Log log = LogFactory
			.getLog(EcDcsCli.class.getName());

	public static boolean store2db = false;

	public static int MAX_LATENCY = 600; // latency of 600 seconds before a
											// flush occurs
	// public Date lastStorage = new Date();

	private EcDcsDAODB db = null;

	public static SimpleDateFormat df = new SimpleDateFormat(
			"yyyy/MM/dd-HH:mm:ss:z");

	Options options = new Options();

	String typeName = "none";
	public static String clicommand = null;
	private static Long cycleid;

	protected String log4jconfig = "file://" + System.getProperty("user.dir");

	public String inputFileName = "";
	/**
	 * 
	 */
	public EcDcsCli() {
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
				.withArgName("BImages|DImages|RImages|TImages")
				.withDescription("Object class type name").create("T");
		Option command = OptionBuilder.withLongOpt("command").hasArg()
				.withArgName("COMMAND")
				.withDescription("Command string value for CLI").create("C");

		Option cycle = OptionBuilder.withLongOpt("cycle").hasArg()
				.withArgName("<cyclenumber>")
				.withDescription("The cycle number").create();

		Option log4j = OptionBuilder.withLongOpt("log4j").hasArg()
				.withDescription("path to log4j.xml file").create("l");

		Option ifile = OptionBuilder.withLongOpt("file").hasArg()
				.withDescription("file name to upload").create("f");

		options.addOption("h", "help", false, "this help message");
		options.addOption(type);
		options.addOption(cycle);
		options.addOption(command);
		options.addOption(log4j);
		options.addOption(ifile);
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
			System.setProperty("ecchantype", typeName);
		}
		if (line.hasOption("command")) {
			clicommand = line.getOptionValue("command");
			log.info("CLI command  " + clicommand);
		}
		if (line.hasOption("cycle")) {
			cycleid = new Long(line.getOptionValue("cycle"));
			log.info("CLI cycle  " + cycleid);
		}
		if (line.hasOption("log4j")) {
			log4jconfig = line.getOptionValue("log4j");
			log.info("log4j file configuration path " + log4jconfig);
		} else {
			log4jconfig = (log4jconfig + "/log4j.xml");
		}

		if (line.hasOption("file")) {
			inputFileName = line.getOptionValue("file");
			log.info("Input file name is " + inputFileName);
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

	@SuppressWarnings("unchecked")
	public synchronized <T> T getClassType() {
		if (typeName.equals("BImages")) {
			return (T) new BImages();
		} else if (typeName.equals("DImages")) {
			return (T) new DImages();
		} else if (typeName.equals("RImages")) {
			return (T) new RImages();
		} else if (typeName.equals("TImages")) {
			return (T) new TImages();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized <T> T getAlType() {
		if (typeName.equals("AlB")) {
			return (T) new AlB();
		} else if (typeName.equals("AlD")) {
			return (T) new AlD();
		} else if (typeName.equals("AlR")) {
			return (T) new AlR();
		} else if (typeName.equals("AlT")) {
			return (T) new AlT();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized <T> AlLoader<T> getLoader(T obj, String sep) {
		if (typeName.equals("BImages")) {
			return (AlLoader<T>) new AlLoader<BImages>(new BImages(), sep);
		} else if (typeName.equals("AlD")) {
			return (AlLoader<T>) new AlLoader<DImages>(new DImages(), sep);
		} else if (typeName.equals("AlR")) {
			return (AlLoader<T>) new AlLoader<RImages>(new RImages(), sep);
		} else if (typeName.equals("TImages")) {
			return (AlLoader<T>) new AlLoader<TImages>(new TImages(), sep);
		}
		return null;
	}


	public <T> List<T> loadFromFile(File inpf) {
		List<T> imageList = null;
		ILoader<?> _loader = getLoader(getClassType(),",");
		_loader.setNCtorParams(2);
		log.info("...loading images from file..."+inpf.getName());
		try {
			FileInputStream is = new FileInputStream(inpf);
			imageList = loadImages(is,_loader);
			for (T t : imageList) {
				log.info("Loaded image "+t);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imageList;
	}
	
	public <T> List<T> loadImages(InputStream is, ILoader loader) {

		List<T> images = null;
		try {
			log.info("...loading images...");
			images = (List<T>) loader.parse(is);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return images;
	}

	public <T> List<T> findAlByCycle(Long cycle) {

		List<T> als = null;
		log.info("...finding images by cycle...");
		als = (List<T>) db.findImageByCycle(cycle, getAlType());
		return als;
	}


	public static void main(String[] args) {

		//System.getProperties().setProperty("hb.datasource", "EcDcsDB");
		// System.getProperties().setProperty("dim.storage.flag", "on");
		Properties props = System.getProperties();
		try {
			if (props != null && props.containsKey("dim.storage.flag")) {
				String val = props.getProperty("dim.storage.flag");
				if (val != null && val.equals("on")) {
					store2db = true;
				}
			}
			if (props != null && !props.containsKey("hb.datasource")) {
				props.setProperty("hb.datasource", "EcDcsDB");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		final EcDcsCli ecsrv = new EcDcsCli();

		if (System.getProperty("log4j.configuration") != null) {
			System.out.println("Setting logging configuration...");
			ecsrv.setLog4jConfig(System.getProperty("log4j.configuration"));
		}

		try {
			ecsrv.init(args);
			System.out.print("Execute command for image type..."+clicommand);
			System.out.println(": " + ecsrv.typeName);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		EcDcsDAODB mydb = ecsrv.getDB();
		log.info("Initialized EntityManager before Command..." + mydb);
		try {
			// check if file exists
			File inputfile = new File(ecsrv.inputFileName);
			List<? extends EcConfImage> imageList = null;
			log.info("Acces file "+ecsrv.inputFileName+" input file existence: "+inputfile.canRead());
			if (inputfile.exists()) {
				//load file
				log.info("Acces file "+ecsrv.inputFileName);
				imageList = ecsrv.loadFromFile(inputfile);
			}
			if (clicommand.equals("storeImages")) {
				mydb.storeImages(imageList);
			} else if (clicommand.equals("listImages")) {
				for (Object img : imageList) {
					Object image = mydb.findImageById(((EcConfImage)img).getImageId(), img);
					log.info("Found Image "+image+" in DB!");
				}
			}  else if (clicommand.equals("listAlByCycle")) {
				List<?> als = ecsrv.findAlByCycle(cycleid);
				for (Object object : als) {
					log.info("Found image "+object+" for cycle "+cycleid);
				}
				
			} else if (clicommand.equals("removeImages")) {
				for (Object img : imageList) {
					Object image = mydb.findImageById(((EcConfImage)img).getImageId(), img);
					if (image != null) {
						log.info("Removing Image "+image+" from DB!");
						mydb.removeImage(image);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
