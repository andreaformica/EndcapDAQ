package atlas.mdt.dcs.data;
// Generated Aug 31, 2012 4:54:30 PM by Hibernate Tools 3.4.0.CR1


import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * AlBBnds generated by hbm2java
 */
@Entity
@Table(name="AL_B_BNDS"
    ,schema="ATLAS_MDT_DCS"
)
public class AlBBnds  implements java.io.Serializable {


     private long imageId;
     private BigDecimal stime;
     private BigDecimal XNom;
     private BigDecimal XBnd;
     private BigDecimal YNom;
     private BigDecimal YBnd;
     private BigDecimal pxNom;
     private BigDecimal pxBnd;
     private BigDecimal exNom;
     private BigDecimal exBnd;
     private BigDecimal mxNom;
     private BigDecimal mxBnd;
     private BigDecimal mnNom;
     private BigDecimal mnBnd;

    public AlBBnds() {
    }

	
    public AlBBnds(long imageId, BigDecimal stime) {
        this.imageId = imageId;
        this.stime = stime;
    }
    public AlBBnds(long imageId, BigDecimal stime, BigDecimal XNom, BigDecimal XBnd, BigDecimal YNom, BigDecimal YBnd, BigDecimal pxNom, BigDecimal pxBnd, BigDecimal exNom, BigDecimal exBnd, BigDecimal mxNom, BigDecimal mxBnd, BigDecimal mnNom, BigDecimal mnBnd) {
       this.imageId = imageId;
       this.stime = stime;
       this.XNom = XNom;
       this.XBnd = XBnd;
       this.YNom = YNom;
       this.YBnd = YBnd;
       this.pxNom = pxNom;
       this.pxBnd = pxBnd;
       this.exNom = exNom;
       this.exBnd = exBnd;
       this.mxNom = mxNom;
       this.mxBnd = mxBnd;
       this.mnNom = mnNom;
       this.mnBnd = mnBnd;
    }
   
     @Id 

    
    @Column(name="IMAGE_ID", nullable=false, precision=10, scale=0)
    public long getImageId() {
        return this.imageId;
    }
    
    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    
    @Column(name="STIME", nullable=false, precision=38, scale=0)
    public BigDecimal getStime() {
        return this.stime;
    }
    
    public void setStime(BigDecimal stime) {
        this.stime = stime;
    }

    
    @Column(name="X_NOM", precision=22, scale=0)
    public BigDecimal getXNom() {
        return this.XNom;
    }
    
    public void setXNom(BigDecimal XNom) {
        this.XNom = XNom;
    }

    
    @Column(name="X_BND", precision=22, scale=0)
    public BigDecimal getXBnd() {
        return this.XBnd;
    }
    
    public void setXBnd(BigDecimal XBnd) {
        this.XBnd = XBnd;
    }

    
    @Column(name="Y_NOM", precision=22, scale=0)
    public BigDecimal getYNom() {
        return this.YNom;
    }
    
    public void setYNom(BigDecimal YNom) {
        this.YNom = YNom;
    }

    
    @Column(name="Y_BND", precision=22, scale=0)
    public BigDecimal getYBnd() {
        return this.YBnd;
    }
    
    public void setYBnd(BigDecimal YBnd) {
        this.YBnd = YBnd;
    }

    
    @Column(name="PX_NOM", precision=22, scale=0)
    public BigDecimal getPxNom() {
        return this.pxNom;
    }
    
    public void setPxNom(BigDecimal pxNom) {
        this.pxNom = pxNom;
    }

    
    @Column(name="PX_BND", precision=22, scale=0)
    public BigDecimal getPxBnd() {
        return this.pxBnd;
    }
    
    public void setPxBnd(BigDecimal pxBnd) {
        this.pxBnd = pxBnd;
    }

    
    @Column(name="EX_NOM", precision=22, scale=0)
    public BigDecimal getExNom() {
        return this.exNom;
    }
    
    public void setExNom(BigDecimal exNom) {
        this.exNom = exNom;
    }

    
    @Column(name="EX_BND", precision=22, scale=0)
    public BigDecimal getExBnd() {
        return this.exBnd;
    }
    
    public void setExBnd(BigDecimal exBnd) {
        this.exBnd = exBnd;
    }

    
    @Column(name="MX_NOM", precision=22, scale=0)
    public BigDecimal getMxNom() {
        return this.mxNom;
    }
    
    public void setMxNom(BigDecimal mxNom) {
        this.mxNom = mxNom;
    }

    
    @Column(name="MX_BND", precision=22, scale=0)
    public BigDecimal getMxBnd() {
        return this.mxBnd;
    }
    
    public void setMxBnd(BigDecimal mxBnd) {
        this.mxBnd = mxBnd;
    }

    
    @Column(name="MN_NOM", precision=22, scale=0)
    public BigDecimal getMnNom() {
        return this.mnNom;
    }
    
    public void setMnNom(BigDecimal mnNom) {
        this.mnNom = mnNom;
    }

    
    @Column(name="MN_BND", precision=22, scale=0)
    public BigDecimal getMnBnd() {
        return this.mnBnd;
    }
    
    public void setMnBnd(BigDecimal mnBnd) {
        this.mnBnd = mnBnd;
    }




}

