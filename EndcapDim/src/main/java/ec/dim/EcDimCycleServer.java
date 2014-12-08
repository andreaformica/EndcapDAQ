/**
 * 
 */
package ec.dim;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;

import dim.DimClient;
import dim.DimCommand;
import dim.DimServer;
import dim.DimService;
import dim.DimTimer;

/**
 * @author formica
 * 
 */
@SuppressWarnings("unused")
public class EcDimCycleServer {

	public static Log log = LogFactory.getLog(EcDimCycleServer.class
			.getName());

	protected String log4jconfig = "file://" + System.getProperty("user.dir");

	protected Map<String, String> serverStatusMap = new HashMap<String,String>();
	
	public static int MAX_LATENCY = 10; // latency of N seconds before sending status to cycle_logic

	public Date lastTimeStatusSend = new Date();
	
	public boolean serverStatusChanged = false;
	
	/**
	 * 
	 */
	public EcDimCycleServer() {
		super();
		System.setProperty("ecchantype", "CycleSrv");
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
			log4jconfig = (log4jconfig + "/log4j.xml");
			initLogger();
		} catch (UnsupportedOperationException e) {
			throw new Exception(e);
		} catch (FileNotFoundException e) {
			throw new Exception(e);
		} catch (MalformedURLException e) {
			throw new Exception(e);
		}
	}

	public void setServerStatus(String srvname, String status) {
		if (srvname == null) {
			log.info("Cannot add server null...");
			return;
		}
		if (!serverStatusMap.containsKey(srvname)) {
			log.info("Adding server "+srvname+" with status "+status);
		} else {
			String oldstatus = serverStatusMap.get(srvname);
			if (!oldstatus.equals(status)) {
				serverStatusChanged = true;
			}
		}
		serverStatusMap.put(srvname, status);
	}

	public String getServersStatus() {
		StringBuffer buf = new StringBuffer();
		buf.append("Server status report:\n");
		for(String srvname : serverStatusMap.keySet()) {
			buf.append(srvname+" : "+serverStatusMap.get(srvname)+"\n");
		}
		log.debug("CYCLE_LOGIC/STARTENDRESPONSE status report is \n"+buf.toString());
		return buf.toString();
	}
		
	public static void main(String[] args) {

		final EcDimCycleServer ecsrv = new EcDimCycleServer();
		try {
			ecsrv.init(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final StringBuffer response = new StringBuffer();

		final DimService responseSvc = new DimService("EC/CYCLE_LOGIC/STARTENDRESPONSE","empty");

//		DimTimer atimer = new DimTimer(MAX_LATENCY) {
//			public void timerHandler() {
//				Date now = new Date();
//				log.info("Latency time of "+MAX_LATENCY+" seconds is active!");
//				responseSvc.updateService(ecsrv.getServersStatus());
//				log.info("Sending information to CYCLE_LOGIC/STARTENDRESPONSE "+ecsrv.getServersStatus());
//				DimClient.sendCommandNB("CYCLE_LOGIC/STARTENDRESPONSE",ecsrv.getServersStatus());
//				start(MAX_LATENCY);
//			}
//		};
		
		
		DimCommand cmndStartEndCycle = new DimCommand("EC/DAQ/STARTENDRESPONSE", "C") {

			public void commandHandler() {
//				log.debug("Received type cycle response " +  " : "
//						+ getString() + " with length "
//						+ getString().length() + " and datasize "
//						+ getDataSize());
				// call db storage after parsing
				int maxlength = Math.min(getString().length(),
						getDataSize());
				String clob = getString().substring(0, maxlength);
				log.debug("Received CLOB : " + clob);
				String[] srvstatus = clob.split(" ");
				String srvname = null;
				String status = null;
				Date now = new Date();
				if (srvstatus.length>5) {
					srvname = srvstatus[1].trim();
					status = srvstatus[2].trim()+" "+now.toString()+" cycle "+srvstatus[4].trim()+" nimages "+srvstatus[6];
				}
				ecsrv.setServerStatus(srvname, status);
				if (status.contains("ERROR")) {
					log.warn("An ERROR status has been detected... "+ecsrv.getServersStatus());
				}
				Date receivedTimeFromEcDim = new Date();
//				DimClient.sendCommandNB("CYCLE_LOGIC/STARTENDRESPONSE",ecsrv.getServersStatus());
//responseSvc.updateService(getString());
				// Send command is status has changed
				if (ecsrv.serverStatusChanged) {
					log.info("Sending information to CYCLE_LOGIC/STARTENDRESPONSE "+ecsrv.getServersStatus());
					DimClient.sendCommandNB("CYCLE_LOGIC/STARTENDRESPONSE",ecsrv.getServersStatus());
					ecsrv.lastTimeStatusSend = new Date();
					ecsrv.serverStatusChanged = false;					
				}
				// send command if time is elapsed...
				if ((receivedTimeFromEcDim.getTime()-ecsrv.lastTimeStatusSend.getTime())>(MAX_LATENCY*1000L)) {
					log.info("Latency time of "+MAX_LATENCY+" seconds is active!");
					responseSvc.updateService(ecsrv.getServersStatus());
					log.info("Sending information to CYCLE_LOGIC/STARTENDRESPONSE "+ecsrv.getServersStatus());
					DimClient.sendCommandNB("CYCLE_LOGIC/STARTENDRESPONSE",ecsrv.getServersStatus());
					ecsrv.lastTimeStatusSend = new Date();
					ecsrv.serverStatusChanged = false;
				}
			}
		};

		log.info("Created dim command " + cmndStartEndCycle);
		log.info("  data address:" + cmndStartEndCycle.getDataAddress());

		String[] clientservices = DimServer.getClientServices();
		for (int i = 0; i > clientservices.length; i++) {
			log.info("client service " + clientservices[i]);
		}

		DimServer.start("DimSrv_CycleListener");
		DimTimer.sleep(1);
		// Then the rest of the main loop
		while (true) {
			DimTimer.sleep(1);
		}

	}
}
