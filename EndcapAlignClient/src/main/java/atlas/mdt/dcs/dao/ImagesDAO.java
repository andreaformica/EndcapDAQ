/**
 * 
 */
package atlas.mdt.dcs.dao;

import java.util.List;


/**
 * @author formica
 *
 */
public interface ImagesDAO {

	public abstract <T> List<T> retrieveImagesForTypes(T obj);

	public abstract <T> T retrieveImageForName(T obj);
	
}
