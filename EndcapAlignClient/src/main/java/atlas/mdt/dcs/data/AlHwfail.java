package atlas.mdt.dcs.data;

// Generated Aug 31, 2012 4:54:30 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * AlHwfail generated by hbm2java
 */
@Entity
@Table(name = "AL_HWFAIL", schema = "ATLAS_MDT_DCS")
public class AlHwfail implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4416081601989582650L;
	private AlHwfailId id;

	public AlHwfail() {
	}

	public AlHwfail(AlHwfailId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "cycleNumber", column = @Column(name = "CYCLE_NUMBER", nullable = false, precision = 22, scale = 0)),
			@AttributeOverride(name = "endTime", column = @Column(name = "END_TIME", nullable = false, precision = 20, scale = 0)),
			@AttributeOverride(name = "crate", column = @Column(name = "CRATE", nullable = false, length = 8)),
			@AttributeOverride(name = "driver", column = @Column(name = "DRIVER", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "mux", column = @Column(name = "MUX", nullable = false, precision = 8, scale = 0)) })
	public AlHwfailId getId() {
		return this.id;
	}

	public void setId(AlHwfailId id) {
		this.id = id;
	}

}
