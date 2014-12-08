/**
 * 
 */
package ec.dim;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import dim.DimClient;

import atlas.mdt.dcs.client.EcDcsDAODB;
import atlas.mdt.dcs.data.AlB;
import atlas.mdt.dcs.data.AlD;
import atlas.mdt.dcs.data.AlR;
import atlas.mdt.dcs.data.AlT;

/**
 * @author formica
 *
 */
public class EcThreadDimSender {

	EcDcsDAODB db = null;
	public static SimpleDateFormat df = new SimpleDateFormat(
			"yyyy/MM/dd-HH:mm:ss:z");

	/**
	 * 
	 */
	public EcThreadDimSender() {
		super();
		db = new EcDcsDAODB();
	}

	public EcDcsDAODB getDB() {
		return db;
	}

	public <T> List<T> getAlHistory(Date since, Date until, T obj) {
		return getDB().getAlHistory(since, until, obj);
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.getProperties().setProperty("hb.datasource", "EcDcsDB");
		EcThreadDimSender ecsend = new EcThreadDimSender();
		Date since = null;
		Date until = null;
		try {
			since = df.parse("2011/08/10-00:00:00:GMT");
			until = df.parse("2011/08/10-01:10:00:GMT");
			List<AlB> albimages = ecsend.getAlHistory(since, until,
					new AlB());
			System.out.println("Loaded B images at " + new Date()
					+ " size " + albimages.size());

			List<AlB> albimages1 = ecsend.getAlHistory(since, until,
					new AlB());
			System.out.println("Loaded B images at " + new Date()
					+ " size " + albimages1.size());

			List<AlD> aldimages = ecsend.getAlHistory(since, until,
					new AlD());
			System.out.println("Loaded D images at " + new Date()
					+ " size " + aldimages.size());

			List<AlR> alrimages = ecsend.getAlHistory(since, until,
					new AlR());
			System.out.println("Loaded R images at " + new Date()
					+ " size " + alrimages.size());

			List<AlT> altimages = ecsend.getAlHistory(since, until,
					new AlT());
			System.out.println("Loaded T images at " + new Date()
					+ " size " + altimages.size());

			DimClient.sendCommand("EC/DAQ/ALB/BUFSIZE", 20);
			
			RunnableDimSender sendalb = new RunnableDimSender<AlB>(albimages,"ALB",1000);
			RunnableDimSender sendalb1 = new RunnableDimSender<AlB>(albimages1,"ALB",2000);
//			RunnableDimSender sendalb2 = new RunnableDimSender<AlB>(albimages,"ALB",3000);
			RunnableDimSender sendald = new RunnableDimSender<AlD>(aldimages,"ALD",4000);
//			RunnableDimSender sendald1 = new RunnableDimSender<AlD>(aldimages,"ALD",5000);
//			RunnableDimSender sendald2 = new RunnableDimSender<AlD>(aldimages,"ALD",6000);
//			RunnableDimSender sendalt = new RunnableDimSender<AlT>(altimages,"ALT",7000);
//			RunnableDimSender sendalt1 = new RunnableDimSender<AlT>(altimages,"ALT",8000);
//			RunnableDimSender sendalt2 = new RunnableDimSender<AlT>(altimages,"ALT",9000);
			sendalb.start();
			sendalb1.start();
			sendald.start();
			//delay for one second
			Thread.currentThread().sleep(1000);

			//Display info about the main thread
			System.out.println(Thread.currentThread());

			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
