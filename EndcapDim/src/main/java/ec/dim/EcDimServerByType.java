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
import atlas.mdt.dcs.data.EcImage;
import atlas.mdt.dcs.io.AlLoader;
import dim.DimClient;
import dim.DimCommand;
import dim.DimErrorHandler;
import dim.DimExitHandler;
import dim.DimServer;
import dim.DimTimer;
import ec.dim.buffer.BufferStorage;
import ec.dim.tools.ChannelNameResolver;
import ec.dim.tools.ServerState;

/**
 * @author formica
 * 
 */
public class EcDimServerByType {

	public static Log log = LogFactory
			.getLog(EcDimServerByType.class.getName());

	public static boolean store2db = false;

	public static int MAX_LATENCY = 200; // latency of N seconds before a
											// flush occurs
	// public Date lastStorage = new Date();

	private EcDcsDAODB db = null;

	public static SimpleDateFormat df = new SimpleDateFormat(
			"yyyy/MM/dd-HH:mm:ss:z");

	Options options = new Options();

	String typeName = "none";

	public static String dimcommand = null;

	protected BufferStorage<?> alBuffer = null;

	protected String log4jconfig = "file://" + System.getProperty("user.dir");

	private Integer countStoredImages = 0;

	private Long cycleNumber = 0L;

	private String serverStatus = "INIT";

	private ServerState savedState = ServerState.valueOf(serverStatus);

	/**
	 * 
	 */
	public EcDimServerByType() {
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

	public synchronized EcDcsDAODB resetDB() {
		db = null;
		return getDB();
	}

	/**
	 * @return the countStoredImages
	 */
	public Integer getCountStoredImages() {
		return countStoredImages;
	}

	/**
	 * @param countStoredImages
	 *            the countStoredImages to set
	 */
	public void setCountStoredImages(Integer countStoredImages) {
		this.countStoredImages = countStoredImages;
	}

	/**
	 * @return the cycleNumber
	 */
	public Long getCycleNumber() {
		return cycleNumber;
	}

	/**
	 * @param cycleNumber
	 *            the cycleNumber to set
	 */
	public void setCycleNumber(Long cycleNumber) {
		this.cycleNumber = cycleNumber;
	}

	/**
	 * @return the savedState
	 */
	public ServerState getSavedState() {
		return savedState;
	}

	/**
	 * @param savedState
	 *            the savedState to set
	 */
	public void setSavedState(ServerState savedState) {
		this.savedState = savedState;
	}

	public synchronized InputStream getIstreamFromString(String buf) {
		InputStream is = new ByteArrayInputStream(buf.getBytes());
		return is;
	}

	public synchronized <T> List<T> getAlHistory(Date since, Date until, T obj) {
		return getDB().getAlHistory(since, until, obj);
	}

	public synchronized <T> void storeAl(List<T> alimages) {
		if (alimages != null) {
			getDB().storeAl(alimages);
			countStoredImages += alimages.size();
		}
	}

	public synchronized <T> T findAl(T al) {
		if (al != null) {
			T alfromdb = getDB().findAlPrimaryKey(((EcImage) al).getId(), al);
			return alfromdb;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> T getClassType() {
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

	public synchronized BufferStorage<?> getBufferStorage() {
		return getBufferStorage(getClassType());
	}

	/**
	 * add options.
	 */
	@SuppressWarnings("static-access")
	private void initOptions() {

		Option type = OptionBuilder.withLongOpt("type").hasArg()
				.withArgName("AlB|AlD|AlR|AlT")
				.withDescription("Object class type name").create("T");
		Option command = OptionBuilder.withLongOpt("command").hasArg()
				.withArgName("COMMAND")
				.withDescription("Command string value for DIM").create("C");

		Option log4j = OptionBuilder.withLongOpt("log4j").hasArg()
				.withDescription("path to log4j.xml file").create("l");

		options.addOption("h", "help", false, "this help message");
		options.addOption(type);
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
			formatter.printHelp("EcDimServerByType", options);
			return null;
		}
		if (line.hasOption("type")) {
			typeName = line.getOptionValue("type");
			log.info("Type selected " + typeName);
			System.setProperty("ecchantype", typeName);
		}
		if (line.hasOption("command")) {
			dimcommand = line.getOptionValue("command");
			log.info("DIM command path " + dimcommand);
		}
		if (line.hasOption("log4j")) {
			log4jconfig = line.getOptionValue("log4j");
			log.info("log4j file configuration path " + log4jconfig);
		} else {
			log4jconfig = (log4jconfig + "/log4j.xml");
		}

		return line;
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> BufferStorage<T> getBufferStorage(T obj) {
		if (alBuffer == null) {
			alBuffer = new BufferStorage<T>();
			return (BufferStorage<T>) alBuffer;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> AlLoader<T> getLoader(T obj) {
		if (typeName.equals("AlB")) {
			return (AlLoader<T>) new AlLoader<AlB>(new AlB(), null);
		} else if (typeName.equals("AlD")) {
			return (AlLoader<T>) new AlLoader<AlD>(new AlD(), null);
		} else if (typeName.equals("AlR")) {
			return (AlLoader<T>) new AlLoader<AlR>(new AlR(), null);
		} else if (typeName.equals("AlT")) {
			return (AlLoader<T>) new AlLoader<AlT>(new AlT(), null);
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

	public static void main(String[] args) {

		// System.getProperties().setProperty("hb.datasource", "EcDcsDB");
		// System.getProperties().setProperty("dim.storage.flag", "on");
		Properties props = System.getProperties();
		try {
			if (props != null && props.containsKey("dim.storage.flag")) {
				String val = props.getProperty("dim.storage.flag");
				if (val != null && val.equals("on")) {
					store2db = true;
				}
			}
			if (props != null && props.containsKey("max.storage.latency")) {
				String val = props.getProperty("max.storage.latency");
				if (val != null) {
					MAX_LATENCY = new Integer(val);
				}
			}
			if (props != null && !props.containsKey("hb.datasource")) {
				props.setProperty("hb.datasource", "EcDcsDB");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		final EcDimServerByType ecsrv = new EcDimServerByType();

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

		final BufferStorage<?> _buffer = ecsrv.getBufferStorage();
		final Date _lastStorage = new Date();
		try {

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
					if (store2db) {
						log.info("EcDimServerByType exiting...flush all images in buffer!");
						ecsrv.storeAl(_buffer.getData());
						_buffer.clearData();
					}
					log.info("EcDimServerByType exit code : " + code);
				}
			};
			DimServer.addExitHandler(exid);


			DimCommand cmnd = new DimCommand(EcDimServerByType.dimcommand, "C") {

				public void commandHandler() {
					List<?> alimages = null;
					try {
						log.debug("Received type " + ecsrv.typeName + " : "
								+ getString() + " with length "
								+ getString().length() + " and datasize "
								+ getDataSize());
						// call db storage after parsing
						int maxlength = Math.min(getString().length(),
								getDataSize());
						String clob = getString().substring(0, maxlength);
						// Protect parsing against final #end string attached to
						// a
						// number field
						int hashindex = clob.lastIndexOf("#");
						clob = clob.substring(0, hashindex);
						log.info(EcDimServerByType.dimcommand
								+ ": Received CLOB : " + clob);
						ILoader<?> _loader = ecsrv.getLoader(ecsrv
								.getClassType());

						alimages = ecsrv.getDB().loadAl(
								ecsrv.getIstreamFromString(clob), _loader);
						// int cycle = -1;

						log.info("Loaded images at " + new Date() + " size "
								+ alimages.size());

						if (_buffer.addEntryList(alimages)) {
							int stsize = _buffer.getData().size();
							if (store2db) {
								ecsrv.storeAl(_buffer.getData());
								_lastStorage.setTime(new Date().getTime());
								_buffer.clearData();
								if (ecsrv.getCycleNumber() > 0
										&& ecsrv.getSavedState() == ServerState.RUNNING) {
									log.warn("Current state is RUNNING : "
											+ " for cycle "+ecsrv.getCycleNumber());
									DimClient
											.sendCommandNB(
													"EC/DAQ/STARTENDRESPONSE",
													"Server "
															+ ecsrv.typeName
															+ " "
															+ ecsrv.getSavedState()
																	.serverState()
															+ " cycle "
															+ ecsrv.getCycleNumber()
															+ " inserted "
															+ ecsrv.getCountStoredImages());
								} else {
									log.warn("Current state is not RUNNING!! ["
											+ ecsrv.getSavedState() + "]");
								}
								log.info("Stored images at " + new Date()
								+ " size " + stsize);

							} else {
								// clean up data buffer...
								_buffer.clearData();								
							}
						}
					} catch (Exception e) {
						log.error(EcDimServerByType.dimcommand
								+ " handler catched exception during execution: \n"
								+ e.getMessage());
						ecsrv.setSavedState(ServerState.ERROR);
						DimClient.sendCommandNB(
								"EC/DAQ/STARTENDRESPONSE",
								"Server " + ecsrv.typeName + " "
										+ ecsrv.getSavedState().serverState()
										+ " cycle " + ecsrv.getCycleNumber()
										+ " inserted "
										+ ecsrv.getCountStoredImages());
						// Try to re initialize the Business Delegate
						log.warn("Command Handler is trying to reinitialize the business delegate");
						ecsrv.resetDB();
						ecsrv.setSavedState(ServerState.RUNNING);

					} finally {
						// Check if last storage time is older than MAX_LATENCY
						Date now = new Date();
						Long timediff = now.getTime() - _lastStorage.getTime();
						if (timediff > MAX_LATENCY * 1000L) {
							log.info("Latency time has been exceeded: flushing buffer....");
							if (store2db) {
								ecsrv.storeAl(_buffer.getData());
								_lastStorage.setTime(new Date().getTime());
								_buffer.clearData();
							}
							if (ecsrv.getCycleNumber() > 0){
									//&& ecsrv.getSavedState() == ServerState.RUNNING) {
								DimClient.sendCommandNB(
										"EC/DAQ/STARTENDRESPONSE",
										"Server "
												+ ecsrv.typeName
												+ " "
												+ ecsrv.getSavedState()
														.serverState()
												+ " cycle "
												+ ecsrv.getCycleNumber()
												+ " inserted "
												+ ecsrv.getCountStoredImages());
							} else {
								log.info("Current state is "
										+ ecsrv.getSavedState());
							}
						}

						if (alimages != null && alimages.size() > 0) {
							Object al = alimages.get(0);
							long lastst = _lastStorage.getTime();
							// if last storage has been less then 5 seconds
							// before, try to check the DB....
							// TODO : this check can be removed
							if (new Date().getTime() - lastst < 5 * 1000L) {
								if (al instanceof EcImage) {
									log.info("Checking first image of list..."
											+ ((EcImage) al).getId().toString());
									Object alfromdb = ecsrv.findAl(al);
									if (alfromdb != null)
										log.info("Found in DB..."
												+ alfromdb.toString());
									else {
										log.error("Not found the image"
												+ ((EcImage) al).getId()
														.toString()
												+ " in DB..!!");
									}
								} else {
									log.warn("Image is not instance of a recognized type EcImage");
								}
							}
						}
					}
				}
			};

			DimCommand cmndBufSize = new DimCommand("/EC/DAQ/" + ecsrv.typeName
					+ "/BUFSIZE", "I") {

				public void commandHandler() {
					log.info("Received Buf Size: " + getInt());
					// call db storage after parsing
					ecsrv.getBufferStorage().setBufferSize(getInt());
				}
			};

			log.info("Created dim command " + cmndBufSize);
			log.info("  data address:" + cmndBufSize.getDataAddress());

			// final StringBuffer response = new StringBuffer();
			// final DimService responseSvc = new
			// DimService("CYCLE_LOGIC/STARTENDRESPONSE","empty");

			DimCommand cmndStartEndCycle = new DimCommand(
					"CYCLE_LOGIC/STARTEND/"
							+ ChannelNameResolver.valueOf(ecsrv.typeName)
									.dimChannel(), "C") {

				public void commandHandler() {
					try {
						log.debug("Received cycle logic startend for "
								+ ecsrv.typeName + " : " + getString()
								+ " with length " + getString().length()
								+ " and datasize " + getDataSize());
						// call db storage after parsing
						int maxlength = Math.min(getString().length(),
								getDataSize());
						String clob = getString().substring(0, maxlength);
						log.info("Received CLOB : " + clob);
						// Protect parsing against final #end string attached to
						// a number field
						int hashindex = clob.lastIndexOf("#");
						clob = clob.substring(0, hashindex);

						String[] cycleparams = clob.trim().split(" ");
						if (cycleparams != null && cycleparams.length >= 2) {
							log.debug("Received parameters " + cycleparams[0]
									+ " " + cycleparams[1]);
							if (cycleparams[0].equals("start")) {
								// acknowledge DAQ that server is ready
								log.info("EcDimServerByType ready for inputs....!");
								// response.delete(0, response.length());
								// response.append("Server "+ecsrv.typeName+" is ready for cycle "+cycleparams[1]);
								// responseSvc.updateService("Server "+ecsrv.typeName+" is ready for cycle "+cycleparams[1]);
								// Go to state ready
								if (ecsrv.getSavedState() == ServerState.INIT) {
									ecsrv.setSavedState(ServerState
											.valueOf(ecsrv.getSavedState()
													.next()));
									log.debug("Server state going to "
											+ ecsrv.getSavedState());
								}
								ecsrv.setCountStoredImages(0);
								ecsrv.setCycleNumber(new Long(cycleparams[1]));
								DimClient.sendCommandNB(
										"EC/DAQ/STARTENDRESPONSE",
										"Server "
												+ ecsrv.typeName
												+ " "
												+ ecsrv.getSavedState()
														.serverState()
												+ " cycle " + cycleparams[1]
												+ " received "
												+ ecsrv.getCountStoredImages());
								DimTimer.sleep(1);
								if (ecsrv.getSavedState() == ServerState.READY) {
									ecsrv.setSavedState(ServerState
											.valueOf(ecsrv.getSavedState()
													.next()));
									log.debug("Server state going to "
											+ ecsrv.getSavedState());
								}
								DimClient.sendCommandNB(
										"EC/DAQ/STARTENDRESPONSE",
										"Server "
												+ ecsrv.typeName
												+ " "
												+ ecsrv.getSavedState()
														.serverState()
												+ " cycle " + cycleparams[1]
												+ " received "
												+ ecsrv.getCountStoredImages());
								DimTimer.sleep(1);

							} else if (cycleparams[0].equals("end")) {
								int nimages = (_buffer.getData() != null) ? _buffer
										.getData().size() : 0;
								if (store2db && nimages > 0) {
									log.info("EcDimServerByType flush all images in buffer at end of cycle "
											+ ecsrv.getCycleNumber()
											+ " : "
											+ nimages + " will be stored !");
									ecsrv.storeAl(_buffer.getData());
									_buffer.clearData();
								}
								log.info("EcDimServerByType flushed buffers: now changing state...");
								if (ecsrv.getSavedState() == ServerState.RUNNING) {
									ecsrv.setSavedState(ServerState
											.valueOf(ecsrv.getSavedState()
													.next()));
									log.debug("Server state going to "
											+ ecsrv.getSavedState());
								}
								// responseSvc.updateService("Server "+ecsrv.typeName+" flushed "+nimages);
								DimClient.sendCommandNB(
										"EC/DAQ/STARTENDRESPONSE",
										"Server "
												+ ecsrv.typeName
												+ " "
												+ ecsrv.getSavedState()
														.serverState()
												+ " cycle " + cycleparams[1]
												+ " flushed "
												+ ecsrv.getCountStoredImages());
								DimTimer.sleep(1);
								ecsrv.setCountStoredImages(0);
								if (ecsrv.getSavedState() == ServerState.FINISH) {
									ecsrv.setSavedState(ServerState
											.valueOf(ecsrv.getSavedState()
													.next()));
									log.debug("Server state going to "
											+ ecsrv.getSavedState());
								}
							}
						}
					} catch (Exception e) {
						log.error("command handler for CYCLE_LOGIC/STARTEND gave exception: "
								+ e.getMessage());
					}
				}
			};

			log.info("Created dim command " + cmndStartEndCycle);
			log.info("  data address:" + cmndStartEndCycle.getDataAddress());

			String[] clientservices = DimServer.getClientServices();
			for (int i = 0; i > clientservices.length; i++) {
				log.info("client service " + clientservices[i]);
			}

			DimServer.start("DimSrv_" + ecsrv.typeName);
			DimTimer.sleep(1);
			// Then the rest of the main loop
			while (true) {
				DimTimer.sleep(1);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

}
