package ec.db.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public final class DBCyc {

static String start,end;
static String scycle,smcycle;
static int cycle, mcyc,maxcyc;

    /**
            12 August 2008    INTR
               Cycle Statistics
          ------------------------------------
            19 Sept. 2011  modifed for recent ATLR
               then modified for  INT8R  to test
                uses August partition
         runDBCyc -cycle 18170 -maxcyc 5
          -----------------------------------------
     */

    private DBCyc() throws Exception {


        String dformat="YYYY-MM-DD HH24:MI:SS";

	Class.forName("oracle.jdbc.driver.OracleDriver");

////// 	String url = "jdbc:oracle:thin:@intr1-v.cern.ch:10121:intr1";
// 	String url = "jdbc:oracle:thin:@atonr1-v.cern.ch:10121:atonr1";

//ATONR	
////	Connection con = DriverManager.getConnection(url,
////						     "",
////						     "");

//	Connection con = DriverManager.getConnection(url,
//						     "",
//						     "");
// INTR
//////	Connection con = DriverManager.getConnection(url,
//						     "",
//						     "");

//-------------------------------------------

// Offline   Sept 2011

        String url = "jdbc:oracle:thin:@atlr1-v.cern.ch:10121:atlr1";
        Connection con = DriverManager.getConnection(url,
                                                     "ATLAS_MDT_DCS_R",
                                                     "xxx");
//-------------------------------------------------------------------
// INT8R    Sept 2011

//        String url = "jdbc:oracle:thin:@int8r1-v.cern.ch:10121:int8r1";
//        Connection con = DriverManager.getConnection(url,
//                                                     "ATLAS_MDT_DCS_R",
//                                                     "change_me_now5099");
//-------------------------------------------------------------------
        con.setAutoCommit(false);
	Statement stmt = con.createStatement();


	/*
	 * The simplest way to submit a query...
	 */
	//String query = "SELECT * FROM atlas_muon_comm.tmp_dummy";
//	String query = "SELECT file_name,file_id,nrows FROM alignment_file order by -file_id";
//"SELECT stime,UNIXTS_TO_DATE(STIME),x FROM AL_B  order by stime";

///"SELECT stime,UNIXTS_TO_DATE(STIME),AL_B.image_id,B_IMAGES.IMAGE_NAME,x,y,mag FROM AL_B,B_IMAGES where AL_B.image_id=B_IMAGES.image_id AND stime> DATE_TO_UNIXTS(TO_DATE('2007-08-29 07:30:40', 'YYYY-MM-DD HH24:MI:SS')) AND stime< DATE_TO_UNIXTS(TO_DATE('2007-08-29 09:20:40', 'YYYY-MM-DD HH24:MI:SS'))  order by stime";

//	String queryB = 
//"SELECT COUNT(*) AS nrowsb FROM AL_B WHERE stime> DATE_TO_UNIXTS(TO_DATE(?, '" + dformat +"')) AND stime< DATE_TO_UNIXTS(TO_DATE(?,'" + dformat +"'  )) ";

//	String queryR = 
//"SELECT COUNT(*) AS nrowsr FROM AL_R WHERE stime> DATE_TO_UNIXTS(TO_DATE(?,'" + dformat +"' )) AND stime< DATE_TO_UNIXTS(TO_DATE(?,'" + dformat +"')) ";

//	String queryT = 
//"SELECT COUNT(*) AS nrowst FROM AL_T WHERE stime> DATE_TO_UNIXTS(TO_DATE(?,'" + dformat +"' )) AND stime< DATE_TO_UNIXTS(TO_DATE(?,'" + dformat +"' )) ";


//  modified 19  Sept. 2011
	String queryB = 
"SELECT COUNT(*) AS nrowsb,UNIXTS_TO_DATE(max(stime)) AS st FROM AL_B partition(AL_B_FEB_2012) WHERE cycle_number= ?";
	String queryR = 
"SELECT COUNT(*) AS nrowsr FROM AL_R  partition(AL_R_FEB_2012)  WHERE cycle_number= ?";
	String queryT = 
	"SELECT COUNT(*) AS nrowst FROM AL_T  partition(AL_T_FEB_2012)  WHERE cycle_number= ?";

// errors
	String queryBe = 
"SELECT COUNT(*) AS nrowsbe FROM AL_B  partition(AL_B_FEB_2012) WHERE cycle_number= ? AND B_EC<0 AND B_EC>-10000 "; 
	String queryRe = 
"SELECT COUNT(*) AS nrowsre FROM AL_R  partition(AL_R_FEB_2012)  WHERE cycle_number= ? AND R_EC<0 AND R_EC>-10000 ";
	String queryTe = 
"SELECT COUNT(*) AS nrowste FROM AL_T  partition(AL_T_FEB_2012)  WHERE cycle_number= ? AND T_EC<0  ";


PreparedStatement stmtpb = con.prepareStatement(queryB);
PreparedStatement stmtpr = con.prepareStatement(queryR);
PreparedStatement stmtpt = con.prepareStatement(queryT);

PreparedStatement stmtpbe = con.prepareStatement(queryBe);
PreparedStatement stmtpre = con.prepareStatement(queryRe);
PreparedStatement stmtpte = con.prepareStatement(queryTe);

//stmtp.setString(1,"2007-08-29 07:30:40");
//stmtp.setString(2,"2007-08-29 09:30:40");

int cyci;
int i=0;
while(i<maxcyc) {

cyci=cycle-i;
stmtpb.setInt(1,cyci);
stmtpr.setInt(1,cyci);
stmtpt.setInt(1,cyci);

stmtpbe.setInt(1,cyci);
stmtpre.setInt(1,cyci);
stmtpte.setInt(1,cyci);

i=i+1;

//stmtpb.setString(1,start);
//stmtpb.setString(2,end);

//stmtpr.setString(1,start);
//stmtpr.setString(2,end);

//stmtpt.setString(1,start);
//stmtpt.setString(2,end);


        int irow=0;
//        String name=" ", x=" ",y=" ",mag=" ";
//        int image_id=0;

//        System.out.println(" "+start+" "+end);


        ResultSet rsb = stmtpb.executeQuery();
	 rsb.next(); 
	    int nrowsb = rsb.getInt("nrowsb");
	    String nstime = rsb.getString("st");

        ResultSet rsr = stmtpr.executeQuery();
	 rsr.next(); 
	    int nrowsr = rsr.getInt("nrowsr");

        ResultSet rst = stmtpt.executeQuery();
	 rst.next(); 
	    int nrowst = rst.getInt("nrowst");


        ResultSet rsbe = stmtpbe.executeQuery();
	 rsbe.next(); 
	    int nrowsbe = rsbe.getInt("nrowsbe");

        ResultSet rsre = stmtpre.executeQuery();
	 rsre.next(); 
	    int nrowsre = rsre.getInt("nrowsre");

        ResultSet rste = stmtpte.executeQuery();
	 rste.next(); 
	    int nrowste = rste.getInt("nrowste");

   System.out.println(" "+cyci+"   "+nstime+"   "+nrowsb+" "+nrowsbe+"\t  "+nrowsr+" "+nrowsre+"\t"+nrowst+" "+nrowste );

} // cycle loop i

//            if(irow>10)break;

//	}  // rs

// ====================================	

	con.close();
    }

    /**
     *
     */
    public static void main(String[] args) {

        int argIndex = 0;
        String arg;
        while (argIndex < args.length) {
          arg = args[argIndex];
          maxcyc=10;
//          if   (args[argIndex].equals("-start"))   { start =  args[++argIndex]; }
//          else if (args[argIndex].equals("-end"))  { end =    args[++argIndex]; }
           if (args[argIndex].equals("-cycle"))  { scycle =    args[++argIndex];
              int cyc = new Integer(scycle).intValue(); 
              cycle=cyc;
           }
           if (args[argIndex].equals("-maxcyc"))  { smcycle =    args[++argIndex];
              int mcyc = new Integer(smcycle).intValue(); 
              maxcyc=mcyc;
 }
         argIndex++;
         } 
      

	try {
	    DBCyc test = new DBCyc();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

}
