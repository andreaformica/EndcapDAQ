/**
 * 
 */
package ec.dim.tools;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import atlas.mdt.dcs.data.AlB;
import atlas.mdt.dcs.data.AlBId;

/**
 * @author formica
 * 
 */
public class ImageListCreator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		args = new String[3];
		args[0] = "test.out";
		args[1] = "10";
		args[2] = "20";
		if (args.length < 3)
			System.out.println("ImageListCreator: <outputfile> <numberofimages> <numberofcycles");
		String filename = args[0];
		Integer nimages = new Integer(args[1]);
		Integer ncycles = new Integer(args[2]);
		if (ncycles>100000) {
			System.err.println("Cannot simulate too many cycles");
			return;
		}

		AlBId id = new AlBId(new BigDecimal(0), 1L);
		AlB al = new AlB(id, new BigDecimal(0), new BigDecimal(1), new BigDecimal(1),
				new BigDecimal(1), new BigDecimal(1), new BigDecimal(1),
				new BigDecimal(1), new BigDecimal(1), new BigDecimal(1),
				new BigDecimal(1), new BigDecimal(1), new BigDecimal(1),
				new BigDecimal(1), new BigDecimal(1), new BigDecimal(1),
				new BigDecimal(1), new BigDecimal(1), new BigDecimal(1));
		
		List<AlB> albimageList = new ArrayList<AlB>();
		for (int i=0; i<nimages; i++) {
			AlB ial = new AlB(al);
			AlBId ialid = ial.getId();			
			ialid.setImageId(i);
			ial.setX(new BigDecimal(i));
			for (int ic = 0; ic<ncycles; ic++) {
				AlB ical = new AlB(ial);
				AlBId icalid = ical.getId();
				icalid.setStime(new BigDecimal(i*1000000+ic));
				ical.setCycleNumber(new BigDecimal(ic));
				albimageList.add(ical);
			}
		}
		try {
			FileWriter fw = new FileWriter(filename);
			for (AlB alb : albimageList) {
				fw.write(alb.toString()+"\n");
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
