/**
 * 
 */
package ec.dim;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author formica
 *
 */
public class ThreadContext {

	public static Log log = LogFactory.getLog(ThreadContext.class
			.getName());

	static final ThreadLocal<Integer> cycleNumber = new ThreadLocal<Integer>();
	static final ThreadLocal<BigDecimal> cycleNumberBigDecimal = new ThreadLocal<BigDecimal>();
	static final ThreadLocal<StringBuffer> strbuf = new ThreadLocal<StringBuffer>();
	static final ThreadLocal<Random> random = new ThreadLocal<Random>();
	
	public static final Long ONE_HOUR = (60 * 60) * 1000L;
	public static final SimpleDateFormat df = new SimpleDateFormat(
			"yyyy/MM/dd-HH:mm:ss:z");

	
	/**
	 * 
	 */
	public ThreadContext() {
		super();
	}

	public static Integer getCycle() {
		return cycleNumber.get();
	}
	
	public static void setCycle(Integer cycle) {
		cycleNumber.set(cycle);
		cycleNumberBigDecimal.set(new BigDecimal(cycle));
	}

	public static BigDecimal getBdCycle() {
		return cycleNumberBigDecimal.get();
	}

	public static void incrementCycle() {
		Integer _cyc = cycleNumber.get();
		_cyc++;
		cycleNumber.remove();
		cycleNumber.set(_cyc);
		cycleNumberBigDecimal.remove();
		cycleNumberBigDecimal.set(new BigDecimal(_cyc));
	}

	public static StringBuffer getBuffer() {
		return strbuf.get();
	}
	
	public static void setBuffer(StringBuffer buf) {
		strbuf.set(buf);
	}
	
	public static void appendLineBuffer(String line) {
		strbuf.get().append(line);
		log.info(" -- Thread "+Thread.currentThread().getName()+" appending "+line);
	}

	public static void cleanBuffer() {
		strbuf.remove();
	}
	
	public static Random getRandom() {
		return random.get();
	}
	
	public static void setRandom(Random rand) {
		random.set(rand);
	}
}
