/**
 * 
 */
package ec.dim;

import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import dim.DimClient;

/**
 * @author formica
 * 
 */
public class RunnableDimSender<T> extends Thread implements Runnable {

	public static Log log = LogFactory.getLog(RunnableDimSender.class
			.getName());

	final List<T> imglist;

	final String tablename;
	final String threadName;
	
//	ThreadLocal<Integer> cycleNumber = new ThreadLocal<Integer>();

//	EcDcsDAODB db = null;
//	Thread runner;

//	ThreadLocal<StringBuffer> strbuf = new ThreadLocal<StringBuffer>();
	

	/**
	 * 
	 */
	public RunnableDimSender(List<T> objlist, String name, Integer cycle) {
		super();
//		db = new EcDcsDAODB();
		imglist = objlist;
		tablename = name;
//		cycleNumber.set(cycle) ;
		threadName = "Thread_" + tablename+"_"+cycle;
		setName(threadName);
//		init();
	}

	private void init() {
		//runner = new Thread(this, threadName); // (1) Create a new thread.
//		log.info("Starting thread "+this.getName());
//		log.info("Using cycle number "+cycleNumber.get());
		//runner.start(); // (2) Start the thread.
	}

//	public synchronized EcDcsDAODB getDB() {
//		return db;
//	}
/*
	protected synchronized void createBufferFromList() {
		int i = 0;
		for (T t : imglist) {
			if (i>=1) break;
			T shifted = shiftdates(null, t);
			T ccycle = setCycle(new BigDecimal(cycleNumber.get()), shifted);
			// Takes only the first 10 lines in the list...
//			if (i++ < 10) {
				// System.out.println("...shifting time for " + t.toString());
				String line = ccycle.toString();
				strbuf.get().append(line + "\n");
				i++;
//			}
		}
		strbuf.get().append("#end - "+this.getName());
//		System.out.println("Bufferized " + i + " lines from thread "
//				+ threadName);
		log.info("Bufferized "+i+" lines to "+strbuf.get().toString()+" from thread "+this.getName());
		//return buf;
	}
*/
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
//		Integer incrementcycle = cycleNumber.get();
		//strbuf.remove();
		String[] name = getName().split("_");
		Integer _cycle = new Integer(name[name.length-1]);
		log.info("Starting thread "+getName());
		ThreadContext.setCycle(_cycle);
		ThreadContext.setRandom(new Random());
//		strbuf.set(new StringBuffer());
		for (int i = 0; i < 20; i++) {
//			incrementcycle = cycleNumber.get() + i;
			ThreadContext.setBuffer(new StringBuffer());
			log.info("Sending image list of size "+imglist.size()+" for cycle "+(ThreadContext.getCycle() + i)+" from thread "+threadName);
			new ImageBufferManager<T>(imglist).createBufferFromList();
			if(DimClient.sendCommand("EC/DAQ/"+tablename+"/"+_cycle, ThreadContext.getBuffer().toString()) != 1) {
                log.error("ERROR: Couldn't connect to ExpertCommand.parameters. Command was " + "EC/DAQ/"+tablename+"/"+_cycle);
             //   System.exit(0);
			}			
			//DimClient.sendCommand("EC/DAQ/"+cycleNumber+"/"+tablename, buf.toString());
			log.info("Sending to EC/DAQ/"+tablename+"/"+_cycle+ " cycle "+(ThreadContext.getCycle())+" content "+ThreadContext.getBuffer().toString()+" thread "+getName());
			ThreadContext.cleanBuffer();
			ThreadContext.incrementCycle();
			try {
				currentThread().sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
