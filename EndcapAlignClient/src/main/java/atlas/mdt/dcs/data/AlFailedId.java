package atlas.mdt.dcs.data;

// Generated Aug 31, 2012 4:54:30 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * AlFailedId generated by hbm2java
 */
@Embeddable
public class AlFailedId implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5026049901444654401L;
	private BigDecimal stime;
	private long imageId;

	public AlFailedId() {
	}

	public AlFailedId(BigDecimal stime, long imageId) {
		this.stime = stime;
		this.imageId = imageId;
	}

	@Column(name = "STIME", nullable = false, precision = 38, scale = 0)
	public BigDecimal getStime() {
		return this.stime;
	}

	public void setStime(BigDecimal stime) {
		this.stime = stime;
	}

	@Column(name = "IMAGE_ID", nullable = false, precision = 10, scale = 0)
	public long getImageId() {
		return this.imageId;
	}

	public void setImageId(long imageId) {
		this.imageId = imageId;
	}

}
