/**
 * 
 */
package ec.dim.tools;

/**
 * @author formica
 *
 */
public enum ServerState {

	INIT ("INIT", "READY"),
	READY ("READY","RUNNING"),
	RUNNING ("RUNNING","FINISH"),
	FINISH ("FINISH","INIT"), 
	ERROR ("ERROR", "RECOVER");
	
	private String srvState;
	private String nxtState;
	
	ServerState(String ch, String nxt) {
		this.srvState = ch;
		this.nxtState = nxt;
		
	}
	
	public String serverState() {
		return srvState;
	}
	
	public String next() {
		return nxtState;
	}
}
