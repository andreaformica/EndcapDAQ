package atlas.mdt.dcs.data;

// Generated Aug 31, 2012 4:54:30 PM by Hibernate Tools 3.4.0.CR1

import io.utils.MyPrinterHandler;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * DImages generated by hbm2java
 */
@Entity
@Table(name = "D_IMAGES", schema = "ATLAS_MDT_DCS")
@NamedQueries({
		@NamedQuery(name = DImages.QUERY_GET_IMAGES_BYNAME, query = " FROM DImages as mt"
				+ " WHERE (mt.imageName like :imgname) "),
		@NamedQuery(name = DImages.QUERY_GET_IMAGES_BYID, query = " FROM DImages as mt"
				+ " WHERE (mt.imageId = :imgid) ") })
public class DImages implements java.io.Serializable, EcConfImage {

	public static final String QUERY_GET_IMAGES_BYNAME = "dimages.getimagesbyname";
	public static final String QUERY_GET_IMAGES_BYID = "dimages.getimagesbyid";


	private Long imageId;
	private String imageName;

	public DImages() {
	}

	public DImages(Long imageId, String imageName) {
		this.imageId = imageId;
		this.imageName = imageName;
	}

	@Id
	@Column(name = "IMAGE_ID", nullable = false, precision = 10, scale = 0)
	public Long getImageId() {
		return this.imageId;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;
	}

	@Column(name = "IMAGE_NAME", unique = true, nullable = false, length = 64)
	public String getImageName() {
		return this.imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	/**
	 * toString().
	 */
	@Override
	public String toString() {
		MyPrinterHandler<DImages> handler = new MyPrinterHandler<DImages>(this, " ");
		try {
			handler.add(this.getClass().getDeclaredMethod("getImageId", (Class[]) null));
			handler.add(this.getClass().getDeclaredMethod("getImageName", (Class[]) null));
			
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return handler.print();
	}

}
