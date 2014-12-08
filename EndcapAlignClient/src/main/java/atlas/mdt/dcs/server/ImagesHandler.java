/**
 * 
 */
package atlas.mdt.dcs.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.mdt.dcs.dao.EcDcsDAO;
import atlas.mdt.dcs.dao.EcDcsRetrieveException;
import atlas.mdt.dcs.data.BImages;
import atlas.mdt.dcs.data.DImages;
import atlas.mdt.dcs.data.RImages;

/**
 * @author formica
 * 
 */
@Named("imgmgr")
@SessionScoped
public class ImagesHandler implements Serializable {

	@Inject
	private Logger log;

	@Inject
	private FacesContext facesContext;

	@Inject
	private EcDcsDAO dcsDAO;

	private List<BImages> bimgSet;

	private List<RImages> rimgSet;

	private List<DImages> dimgSet;

	private String selImgName = "%";

	private String selCrateName = "%";

	private Long selDriver = -2L;

	private Long selMux = -2L;
	
	private Map<String, Object> imgTypeMap = new HashMap<String,Object>();
	
	private Set<String> imgTypes;
	
	private String selImgType;
	
	private boolean viewbimages=false;
	
	private boolean viewrimages=false;

	private boolean viewdimages=false;

	@Inject
	private Event<ImgSelectionEvent> imgEventSrc;

	
	
	/**
	 * 
	 */
	public ImagesHandler() {
		super();
		// TODO Auto-generated constructor stub
		init();
	}

	protected void init() {
		imgTypeMap.put("BImages",new BImages());
		imgTypeMap.put("RImages",new RImages());
		imgTypeMap.put("DImages",new DImages());
		imgTypes = imgTypeMap.keySet();
		selImgType = "BImages";
	}
	
	public void changeSelectedType() {
		this.log.info("Change selection...selected imgtypename "
				+ this.selImgType);
		if (selImgType.equals("BImages")) {
			viewbimages=true;
			viewrimages=false;
			viewdimages=false;			
		} else if (selImgType.equals("RImages")) {
			viewbimages=false;
			viewdimages=false;			
			viewrimages=true;			
		} else if (selImgType.equals("DImages")) {
			viewbimages=false;
			viewrimages=false;			
			viewdimages=true;			
		}
	}

	
	/**
	 * @return the viewbimages
	 */
	@Produces
	@Named
	public boolean isViewbimages() {
		return viewbimages;
	}

	/**
	 * @return the viewbimages
	 */
	@Produces
	@Named
	public boolean isViewdimages() {
		return viewdimages;
	}

	/**
	 * @return the viewrimages
	 */
	@Produces
	@Named
	public boolean isViewrimages() {
		return viewrimages;
	}

	/**
	 * @return the imgTypes
	 */
	@Produces
	@Named
	public Set<String> getImgTypes() {
		return imgTypes;
	}

	/**
	 * @return the selImgType
	 */
	@Produces
	@Named
	public String getSelImgType() {
		return selImgType;
	}

	/**
	 * @param selImgType the selImgType to set
	 */
	public void setSelImgType(String selImgType) {
		this.log.info("Setting image type to "+selImgType);
		this.selImgType = selImgType;
	}

	@Produces
	@Named
	public List<BImages> getBimgSet() {
		return this.bimgSet;
	}
	@Produces
	@Named
	public List<RImages> getRimgSet() {
		return this.rimgSet;
	}
	@Produces
	@Named
	public List<DImages> getDimgSet() {
		return this.dimgSet;
	}


	/**
	 * @return the selImgName
	 */
	@Produces
	@Named
	public String getSelImgName() {
		return selImgName;
	}

	/**
	 * @param selImgName
	 *            the selImgName to set
	 */
	public void setSelImgName(String selImgName) {
		String oldimgname = this.selImgName;
		this.log.info("Setting image name : "+selImgName);
		if ((oldimgname == null) || !oldimgname.equals(selImgName)) {
			this.selImgName = selImgName;
			ImgSelectionEvent evt = new ImgSelectionEvent();
			evt.setField("imgname", selImgName);
			this.imgEventSrc.fire(evt);
		}
	}

	/**
	 * @return the selCrateName
	 */
	@Produces
	@Named
	public String getSelCrateName() {
		return selCrateName;
	}

	/**
	 * @param selCrateName
	 *            the selCrateName to set
	 */
	public void setSelCrateName(String selCrateName) {
		String oldcratename = this.selCrateName;
		this.log.info("Setting crate name : "+selCrateName);
		if ((oldcratename == null) || !oldcratename.equals(selCrateName)) {
			this.selCrateName = selCrateName;
			ImgSelectionEvent evt = new ImgSelectionEvent();
			evt.setField("cratename", selCrateName);
			this.imgEventSrc.fire(evt);
		}
	}

	/**
	 * @return the selDriver
	 */
	@Produces
	@Named
	public Long getSelDriver() {
		return selDriver;
	}

	/**
	 * @param selDriver
	 *            the selDriver to set
	 */
	public void setSelDriver(Long selDriver) {
		Long oldsel = this.selDriver;
		if ((oldsel == null) || oldsel != selDriver) {
			this.selDriver = selDriver;
			ImgSelectionEvent evt = new ImgSelectionEvent();
			evt.setField("driver", selDriver);
			this.imgEventSrc.fire(evt);
		}
	}

	/**
	 * @return the selMux
	 */
	@Produces
	@Named
	public Long getSelMux() {
		return selMux;
	}

	/**
	 * @param selMux
	 *            the selMux to set
	 */
	public void setSelMux(Long selMux) {
		Long oldsel = this.selMux;
		if ((oldsel == null) || oldsel != selMux) {
			this.selMux = selMux;
			ImgSelectionEvent evt = new ImgSelectionEvent();
			evt.setField("mux", selMux);
			this.imgEventSrc.fire(evt);
		}
	}
	
	public void searchImagesByParams() {
		this.log.info("Search B images with name " + this.selImgName);
		if (selImgType.equals("BImages")) {
			List<BImages> oldlist = this.bimgSet;
			this.bimgSet = this.searchImagesForTypeByParams(new BImages());
			if (bimgSet != oldlist) {
				log.info("Firing <event selection of BImages> changed");
				ImgSelectionEvent evt = new ImgSelectionEvent();
				evt.setField("imglist", bimgSet.size());
				this.imgEventSrc.fire(evt);
			}
			
		} else if (selImgType.equals("RImages")) {
			List<RImages> oldlist = this.rimgSet;
			this.rimgSet = this.searchImagesForTypeByParams(new RImages());
			if (rimgSet != oldlist) {
				log.info("Firing <event selection of RImages> changed");
				ImgSelectionEvent evt = new ImgSelectionEvent();
				evt.setField("imglist", rimgSet.size());
				this.imgEventSrc.fire(evt);
			}
		} else if (selImgType.equals("DImages")) {
			List<DImages> oldlist = this.dimgSet;
			this.dimgSet = this.searchImagesForTypeByParams(new DImages());
			if (dimgSet != oldlist) {
				log.info("Firing <event selection of DImages> changed");
				ImgSelectionEvent evt = new ImgSelectionEvent();
				evt.setField("imglist", dimgSet.size());
				this.imgEventSrc.fire(evt);
			}
		}
	}

	protected <T> List<T> searchImagesForTypeByParams(T obj) {
		this.log.info("Search images with name " + this.selImgName+" for type "+obj.getClass().getName());
		try {
			List<T> _imglist = null;
			_imglist = dcsDAO.getImagesByParams(selImgName, selCrateName, selDriver, selMux, obj);
			return _imglist;
		} catch (EcDcsRetrieveException e) {
			e.printStackTrace();
		}
		return null;
	}


	public void onSelImagesChanged(
			@Observes(notifyObserver = Reception.IF_EXISTS) final ImgSelectionEvent imgevt) {
		this.log.info("Received new Image change event ");
		Set<String> keys = imgevt.getKeys();
		String _img = this.selImgName;
		String _crate = this.selCrateName;
		Long _driver = this.selDriver;
		Long _mux = this.selMux;
		boolean haschanged = false;
		for (String akey : keys) {
			Object val = imgevt.getField(akey);
			if (val == null) continue;
			this.log.info("key " + akey + " = " + val);
			if (akey.equals("imglist")) {
				this.log.info("refresh image list: "+(Integer)imgevt.getField(akey)+" entries!");
			} else if (akey.equals("cratename")) {
				_crate = (String) imgevt.getField(akey);
				haschanged = true;
				this.log.info("crate has changed: "+_crate);
			} else if (akey.equals("imgname")) {
				_img = (String) imgevt.getField(akey);
				haschanged = true;
				this.log.info("img has changed: "+_img);
			} else if (akey.equals("driver")) {
				_driver = (Long) imgevt.getField(akey);
				haschanged = true;
				this.log.info("driver has changed: "+_driver);
			} else if (akey.equals("mux")) {
				_mux = (Long) imgevt.getField(akey);
				haschanged = true;
				this.log.info("mux has changed: "+_mux);
			}
		}
		if (haschanged) {
			searchImagesByParams();
		}
	}

}
