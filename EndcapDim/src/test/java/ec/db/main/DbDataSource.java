/**
 * 
 */
package ec.db.main;

import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.pool.OracleDataSource;

/**
 * @author formica
 *
 */
public class DbDataSource {

	static String url = "jdbc:oracle:thin:@(DESCRIPTION =  "
           +"(ADDRESS =      (PROTOCOL = TCP)(HOST = int8r1-s.cern.ch)(PORT = 10121))"
           +"(ADDRESS =      (PROTOCOL = TCP)(HOST = int8r2-s.cern.ch)(PORT = 10121))"
           +"(ENABLE=BROKEN)(LOAD_BALANCE=on) "
           +"(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME = int8r_lb.cern.ch) "
           +"(FAILOVER_MODE = (TYPE = SELECT)(METHOD = BASIC)(RETRIES = 200)(DELAY = 15))))";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		try {
			OracleDataSource ds = new OracleDataSource();
			ds.setURL(url);
			ds.setUser("ATLAS_MDT_DCSMON");
			ds.setPassword("xxxx");
			System.out.println("Getting datasource "+ds);
			Connection con = ds.getConnection();
			System.out.println("Getting connection "+con);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
