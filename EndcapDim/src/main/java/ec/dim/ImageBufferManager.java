/**
 * 
 */
package ec.dim;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import atlas.mdt.dcs.data.AlB;
import atlas.mdt.dcs.data.AlD;
import atlas.mdt.dcs.data.AlR;
import atlas.mdt.dcs.data.AlT;

/**
 * @author formica
 *
 */
public class ImageBufferManager<T> {

	public static Log log = LogFactory.getLog(ImageBufferManager.class
			.getName());

	final List<T> imglist;

	
	public ImageBufferManager(List<T> imgs) {
		this.imglist = imgs;
	}
	
	public synchronized void createBufferFromList() {
		int i = 0;
		Integer cyc = ThreadContext.getCycle();
		for (T t : imglist) {
			if (i>=1) break;
			T shifted = shiftdates(null, t);
			log.info("Thread "+Thread.currentThread().getName()+" has cycle "+cyc+ " and object "+shifted+" in class "+this.getClass()+" "+this);
			T ccycle = setCycle(shifted);
			log.info("Thread "+Thread.currentThread().getName()+" has cycle "+cyc+ " and modifed object "+ccycle+this.getClass()+" "+this);
			// Takes only the first 10 lines in the list...
//			if (i++ < 10) {
				// System.out.println("...shifting time for " + t.toString());
//				String line = ccycle.toString();
//				log.info("Thread "+Thread.currentThread().getName()+" has line "+ line);
				//buf.append(line + "\n");
				i++;
//			}
		}
		ThreadContext.appendLineBuffer("#end - "+Thread.currentThread().getName());
//		System.out.println("Bufferized " + i + " lines from thread "
//				+ threadName);
		log.info("Bufferized "+i+" lines to "+ThreadContext.getBuffer().toString()+" from thread "+Thread.currentThread().getName());
		//return buf;
	}

	protected synchronized <T> T shiftdates(Date refdate, T anobj) {
		Calendar cal = Calendar.getInstance();
		if (anobj instanceof AlB) {
			AlB alb=(AlB)anobj;
			cal.setTimeInMillis(alb.getId().getStime().longValue()*1000L);
			cal.set(Calendar.YEAR, 2000);
			Date since = new Date(cal.getTime().getTime()  - ThreadContext.ONE_HOUR);
			BigDecimal meastime = new BigDecimal(since.getTime() / 1000L);
//			Long diff = alb.getId().getStime().longValue()
//					- refdate.getTime() / 1000L;
			alb.getId().setStime(
					new BigDecimal(meastime.longValue()));
			
		} else if (anobj instanceof AlD) {
			AlD ald=(AlD)anobj;
			cal.setTimeInMillis(ald.getId().getStime().longValue()*1000L);
			cal.set(Calendar.YEAR, 2000);
			Date since = new Date(cal.getTime().getTime() - ThreadContext.ONE_HOUR);
			BigDecimal meastime = new BigDecimal(since.getTime() / 1000L);
//			Long diff = ald.getId().getStime().longValue()
//					- refdate.getTime() / 1000L;
			ald.getId().setStime(
					new BigDecimal(meastime.longValue()));
			
		}  else if (anobj instanceof AlR) {
			AlR alr=(AlR)anobj;
			cal.setTimeInMillis(alr.getId().getStime().longValue()*1000L);
			cal.set(Calendar.YEAR, 2000);
			Date since = new Date(cal.getTime().getTime()  - ThreadContext.ONE_HOUR);
			BigDecimal meastime = new BigDecimal(since.getTime() / 1000L);
//			Long diff = alr.getId().getStime().longValue()
//					- refdate.getTime() / 1000L;
			alr.getId().setStime(
					new BigDecimal(meastime.longValue()));
		}  else if (anobj instanceof AlT) {
			AlT alt=(AlT)anobj;
			cal.setTimeInMillis(alt.getId().getStime().longValue()*1000L);
			cal.set(Calendar.YEAR, 2000);
			Date since = new Date(cal.getTime().getTime()  - ThreadContext.ONE_HOUR);
			BigDecimal meastime = new BigDecimal(since.getTime() / 1000L);
//			Long diff = alr.getId().getStime().longValue()
//					- refdate.getTime() / 1000L;
			alt.getId().setStime(
					new BigDecimal(meastime.longValue()));
		}
		return anobj;
	}

	protected synchronized <T> T setCycle(T anobj) {
		if (anobj instanceof AlB) {
			AlB alb=(AlB)anobj;
			AlB alblocal = new AlB();
			alblocal.setBAv(alb.getBAv());
			alblocal.setBBg(alb.getBBg());
			alblocal.setBCo(alb.getBCo());
			alblocal.setBEc(alb.getBEc());
			alblocal.setBEx(alb.getBEx());
			alblocal.setBIn(alb.getBIn());
			alblocal.setId(alb.getId());
			alblocal.setY(new BigDecimal(ThreadContext.getRandom().nextDouble()));
			alblocal.setCycleNumber(ThreadContext.getBdCycle());
			alblocal.setX(((ThreadContext.getCycle()/1000)>1) ? new BigDecimal(2) : new BigDecimal(1));
			log.info("Thread "+Thread.currentThread().getName()+" is setting on ALB object cycle "+ThreadContext.getCycle()
					+" "+this.getClass()+" "+this+" using object "+alblocal.hashCode());
			ThreadContext.appendLineBuffer(alblocal.toString()+"\n");
		} else if (anobj instanceof AlD) {
			AlD ald=(AlD)anobj;
			ald.setCycleNumber(ThreadContext.getBdCycle());
			log.info("Thread "+Thread.currentThread().getName()+" is setting on ALD object cycle "+ThreadContext.getCycle()+" "+this.getClass()+" "+this);
			ThreadContext.appendLineBuffer(ald.toString()+"\n");
			
		}  else if (anobj instanceof AlR) {
			AlR alr=(AlR)anobj;
			alr.setCycleNumber(ThreadContext.getBdCycle());
			log.info("Thread "+Thread.currentThread().getName()+" is setting on ALR object cycle "+ThreadContext.getCycle());
			ThreadContext.appendLineBuffer(alr.toString()+"\n");
	
		}  else if (anobj instanceof AlT) {
			AlT alt=(AlT)anobj;
			alt.setCycleNumber(ThreadContext.getBdCycle());
			log.info("Thread "+Thread.currentThread().getName()+" is setting on ALT object cycle "+ThreadContext.getCycle());
			ThreadContext.appendLineBuffer(alt.toString()+"\n");

		}
		log.info("Thread "+Thread.currentThread().getName()+" returns object with cycle "+ThreadContext.getCycle());
		return anobj;
	}

}
