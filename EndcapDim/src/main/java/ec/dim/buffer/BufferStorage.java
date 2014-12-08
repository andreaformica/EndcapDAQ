/**
 * 
 */
package ec.dim.buffer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author formica
 *
 */
public class BufferStorage<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2085019263442777375L;

	List<T> dbdataList = new ArrayList<T>();
	int bufferSize = 100;
	
	public BufferStorage() {
		super();
	}

	public BufferStorage(int bufSize) {
		super();
		bufferSize = bufSize;
	}
	
	public synchronized boolean addEntry(T obj) {
		dbdataList.add(obj);
		int listsize = dbdataList.size();
		if (listsize>=bufferSize) {
			return true;
		}
		return false;
	}
	
	public synchronized boolean addEntryList(List<?> objlist) {
		dbdataList.addAll((Collection<? extends T>) objlist);
		int listsize = dbdataList.size();
		if (listsize>=bufferSize) {
			return true;
		}
		return false;
	}

	public synchronized void setBufferSize(Integer bsize) {
		if (bsize != null && bsize>=0) {
			System.out.println("Setting buffer size to "+ bsize);
			bufferSize = bsize;
		}
	}
	
	public synchronized void clearData() {
		dbdataList.clear();
	}
	
	public synchronized List<T> getData() {
		return dbdataList;
	}
	
}
