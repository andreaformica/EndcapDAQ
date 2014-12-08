package atlas.mdt.dcs.data;
// Generated Aug 31, 2012 4:54:30 PM by Hibernate Tools 3.4.0.CR1


import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * TBad generated by hbm2java
 */
@Entity
@Table(name="T_BAD"
    ,schema="ATLAS_MDT_DCS"
)
public class TBad  implements java.io.Serializable {


     private BigDecimal imageId;
     private String imageName;

    public TBad() {
    }

	
    public TBad(BigDecimal imageId) {
        this.imageId = imageId;
    }
    public TBad(BigDecimal imageId, String imageName) {
       this.imageId = imageId;
       this.imageName = imageName;
    }
   
     @Id 

    
    @Column(name="IMAGE_ID", nullable=false, precision=22, scale=0)
    public BigDecimal getImageId() {
        return this.imageId;
    }
    
    public void setImageId(BigDecimal imageId) {
        this.imageId = imageId;
    }

    
    @Column(name="IMAGE_NAME", length=32)
    public String getImageName() {
        return this.imageName;
    }
    
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }




}


