package atlas.mdt.dcs.data;

import io.utils.MyPrinterHandler;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AlSupId {

	public static Log log = LogFactory.getLog(AlSupId.class
			.getName());

	protected BigDecimal stime;
	protected long imageId;

//	MyPrinterHandler<?> handler = null;

	public AlSupId() {
		super();
	}

	/**
	 * @return the stime
	 */
	public BigDecimal getStime() {
		return stime;
	}

	/**
	 * @return the imageId
	 */
	public long getImageId() {
		return imageId;
	}

	/**
	 * toString().
	 */
	@Override
	public String toString() {
		String _mobj = "none";
		try {
			MyPrinterHandler<?> handler = new MyPrinterHandler<AlSupId>(this, " ");   	

			handler.add(getClass().getDeclaredMethod("getImageId", (Class[]) null));
			handler.add(getClass().getDeclaredMethod("getStime", (Class[]) null));
			_mobj = handler.print();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return _mobj;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj.getClass().isInstance(this))) {
			return false;
		}
		AlSupId idobj = (AlSupId) obj;

		if (imageId != (idobj.getImageId())) {
			return false;
		}
		if (stime.compareTo(idobj.getStime()) != 0) {
			return false;
		}
		
		return true;
	}

	@Override
	public int hashCode() {
		return stime.hashCode() + (int)imageId;
	}

}