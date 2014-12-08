package atlas.mdt.dcs.data;

// Generated Aug 31, 2012 4:54:30 PM by Hibernate Tools 3.4.0.CR1

import io.utils.MyPrinterHandler;

import java.math.BigDecimal;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * AlR generated by hbm2java
 */
@Entity
@Table(name = "AL_R", schema = "ATLAS_MDT_DCS")
@NamedQueries({

@NamedQuery(name = AlR.QUERY_GET_HISTORY, query = " FROM AlR as mt"
		+ " WHERE (mt.id.imageId = :id OR :id is null) AND mt.id.stime > :since AND mt.id.stime < :until"),
@NamedQuery(name = AlR.QUERY_GET_CYCLE, query = " FROM AlR as mt"
				+ " WHERE (mt.cycleNumber = :cycle) order by mt.id.stime asc")				
})
public class AlR implements java.io.Serializable, EcImage {

	public static final String QUERY_GET_HISTORY = "alr.gethistory";
	public static final String QUERY_GET_CYCLE = "alr.getcycle";

	private AlRId id;
	private BigDecimal cycleNumber;
	private BigDecimal x;
	private BigDecimal y;
	private BigDecimal mag;
	private BigDecimal RDm;
	private BigDecimal RRo;
	private BigDecimal RDx;
	private BigDecimal RSq;
	private BigDecimal ROr;
	private BigDecimal RRf;
	private BigDecimal REx;
	private BigDecimal RAv;
	private BigDecimal RSt;
	private BigDecimal RMx;
	private BigDecimal RMn;
	private BigDecimal REc;
	private BigDecimal REl;
	private BigDecimal RCo;
	private BigDecimal RLn;
	private BigDecimal RDn;
	private BigDecimal RSh;

	public AlR() {
	}

	public AlR(AlRId id) {
		this.id = id;
	}

	public AlR(AlRId id, BigDecimal cycleNumber, BigDecimal x, BigDecimal y,
			BigDecimal mag, BigDecimal RDm, BigDecimal RRo, BigDecimal RDx,
			BigDecimal RSq, BigDecimal ROr, BigDecimal RRf, BigDecimal REx,
			BigDecimal RAv, BigDecimal RSt, BigDecimal RMx, BigDecimal RMn,
			BigDecimal REc, BigDecimal REl, BigDecimal RCo, BigDecimal RLn,
			BigDecimal RDn, BigDecimal RSh) {
		this.id = id;
		this.cycleNumber = cycleNumber;
		this.x = x;
		this.y = y;
		this.mag = mag;
		this.RDm = RDm;
		this.RRo = RRo;
		this.RDx = RDx;
		this.RSq = RSq;
		this.ROr = ROr;
		this.RRf = RRf;
		this.REx = REx;
		this.RAv = RAv;
		this.RSt = RSt;
		this.RMx = RMx;
		this.RMn = RMn;
		this.REc = REc;
		this.REl = REl;
		this.RCo = RCo;
		this.RLn = RLn;
		this.RDn = RDn;
		this.RSh = RSh;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "stime", column = @Column(name = "STIME", nullable = false, precision = 20, scale = 0)),
			@AttributeOverride(name = "imageId", column = @Column(name = "IMAGE_ID", nullable = false, precision = 10, scale = 0)) })
	public AlRId getId() {
		return this.id;
	}

	public void setId(AlRId id) {
		this.id = id;
	}

	@Column(name = "CYCLE_NUMBER", precision = 22, scale = 0)
	public BigDecimal getCycleNumber() {
		return this.cycleNumber;
	}

	public void setCycleNumber(BigDecimal cycleNumber) {
		this.cycleNumber = cycleNumber;
	}

	@Column(name = "X", precision = 22, scale = 0)
	public BigDecimal getX() {
		return this.x;
	}

	public void setX(BigDecimal x) {
		this.x = x;
	}

	@Column(name = "Y", precision = 22, scale = 0)
	public BigDecimal getY() {
		return this.y;
	}

	public void setY(BigDecimal y) {
		this.y = y;
	}

	@Column(name = "MAG", precision = 22, scale = 0)
	public BigDecimal getMag() {
		return this.mag;
	}

	public void setMag(BigDecimal mag) {
		this.mag = mag;
	}

	@Column(name = "R_DM", precision = 22, scale = 0)
	public BigDecimal getRDm() {
		return this.RDm;
	}

	public void setRDm(BigDecimal RDm) {
		this.RDm = RDm;
	}

	@Column(name = "R_RO", precision = 22, scale = 0)
	public BigDecimal getRRo() {
		return this.RRo;
	}

	public void setRRo(BigDecimal RRo) {
		this.RRo = RRo;
	}

	@Column(name = "R_DX", precision = 22, scale = 0)
	public BigDecimal getRDx() {
		return this.RDx;
	}

	public void setRDx(BigDecimal RDx) {
		this.RDx = RDx;
	}

	@Column(name = "R_SQ", precision = 22, scale = 0)
	public BigDecimal getRSq() {
		return this.RSq;
	}

	public void setRSq(BigDecimal RSq) {
		this.RSq = RSq;
	}

	@Column(name = "R_OR", precision = 22, scale = 0)
	public BigDecimal getROr() {
		return this.ROr;
	}

	public void setROr(BigDecimal ROr) {
		this.ROr = ROr;
	}

	@Column(name = "R_RF", precision = 22, scale = 0)
	public BigDecimal getRRf() {
		return this.RRf;
	}

	public void setRRf(BigDecimal RRf) {
		this.RRf = RRf;
	}

	@Column(name = "R_EX", precision = 22, scale = 0)
	public BigDecimal getREx() {
		return this.REx;
	}

	public void setREx(BigDecimal REx) {
		this.REx = REx;
	}

	@Column(name = "R_AV", precision = 22, scale = 0)
	public BigDecimal getRAv() {
		return this.RAv;
	}

	public void setRAv(BigDecimal RAv) {
		this.RAv = RAv;
	}

	@Column(name = "R_ST", precision = 22, scale = 0)
	public BigDecimal getRSt() {
		return this.RSt;
	}

	public void setRSt(BigDecimal RSt) {
		this.RSt = RSt;
	}

	@Column(name = "R_MX", precision = 22, scale = 0)
	public BigDecimal getRMx() {
		return this.RMx;
	}

	public void setRMx(BigDecimal RMx) {
		this.RMx = RMx;
	}

	@Column(name = "R_MN", precision = 22, scale = 0)
	public BigDecimal getRMn() {
		return this.RMn;
	}

	public void setRMn(BigDecimal RMn) {
		this.RMn = RMn;
	}

	@Column(name = "R_EC", precision = 22, scale = 0)
	public BigDecimal getREc() {
		return this.REc;
	}

	public void setREc(BigDecimal REc) {
		this.REc = REc;
	}

	@Column(name = "R_EL", precision = 22, scale = 0)
	public BigDecimal getREl() {
		return this.REl;
	}

	public void setREl(BigDecimal REl) {
		this.REl = REl;
	}

	@Column(name = "R_CO", precision = 22, scale = 0)
	public BigDecimal getRCo() {
		return this.RCo;
	}

	public void setRCo(BigDecimal RCo) {
		this.RCo = RCo;
	}

	@Column(name = "R_LN", precision = 22, scale = 0)
	public BigDecimal getRLn() {
		return this.RLn;
	}

	public void setRLn(BigDecimal RLn) {
		this.RLn = RLn;
	}

	@Column(name = "R_DN", precision = 22, scale = 0)
	public BigDecimal getRDn() {
		return this.RDn;
	}

	public void setRDn(BigDecimal RDn) {
		this.RDn = RDn;
	}

	@Column(name = "R_SH", precision = 22, scale = 0)
	public BigDecimal getRSh() {
		return this.RSh;
	}

	public void setRSh(BigDecimal RSh) {
		this.RSh = RSh;
	}

	/**
	 * toString().
	 */
	@Override
	public String toString() {
		MyPrinterHandler<AlR> handler = new MyPrinterHandler<AlR>(this, " ");
		String _mobj = "none";
		try {
			handler.add(this.getClass().getDeclaredMethod("getId", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getCycleNumber", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getX", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getY", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getMag", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getRDm", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getRRo", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getRDx", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getRSq", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getROr", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getRRf", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getREx", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getRAv", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getRSt", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getRMx", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getRMn", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getREc", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getREl", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getRCo", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getRLn", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getRDn", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getRSh", (Class[]) null));

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
		if (!(obj instanceof AlR)) {
			return false;
		}
		AlR idobj = (AlR) obj;

		if (!id.equals(idobj.getId())) {
			return false;
		}
		if (cycleNumber.compareTo(idobj.getCycleNumber()) != 0) {
			return false;
		}
		if (x.compareTo(idobj.getX()) != 0) {
			return false;
		}
		if (y.compareTo(idobj.getY()) != 0) {
			return false;
		}
		if (mag.compareTo(idobj.getMag()) != 0) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode() + cycleNumber.hashCode()+x.hashCode()+y.hashCode()+mag.hashCode();
	}

}
