package lwrbTest;

import java.net.URISyntaxException;
import java.util.HashMap;

import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.jgrasstools.gears.libs.monitor.PrintStreamProgressMonitor;
import org.jgrasstools.hortonmachine.utils.HMTestCase;

import lwrbPointCase.*;



public class TestLwrb extends HMTestCase{

	public void testLinear() throws Exception {

		String startDate = "2004-06-14 00:00";
		String endDate = "2004-06-16 00:00";
		int timeStepMinutes = 60;
		String fId = "ID";


		PrintStreamProgressMonitor pm = new PrintStreamProgressMonitor(System.out, System.out);


		String inPathToAirT = "src/test/resources/input/Taria.csv";
		String inPathToSoilT = "src/test/resources/input/Tsuolo.csv";
		String inPathToHumidity = "src/test/resources/input/H.csv";
		String inPathToCI = "src/test/resources/input/ClearnessIndex.csv";

		String pathToDownwelling= "src/test/resources/output/downwelling_model1.csv";
		String pathToUpwelling= "src/test/resources/output/upwelling_model1.csv";
		String pathToLongwave= "src/test/resources/output/longwave_model1.csv";


		OmsTimeSeriesIteratorReader airTReader = getTimeseriesReader(inPathToAirT, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader soilTReader = getTimeseriesReader(inPathToSoilT, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader humidityReader = getTimeseriesReader(inPathToHumidity, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader CIReader = getTimeseriesReader(inPathToCI, fId, startDate, endDate, timeStepMinutes);


		OmsTimeSeriesIteratorWriter writer_down = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writer_up = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writer_long = new OmsTimeSeriesIteratorWriter();


		writer_down.file = pathToDownwelling;
		writer_down.tStart = startDate;
		writer_down.tTimestep = timeStepMinutes;
		writer_down.fileNovalue="-9999";

		writer_up.file = pathToUpwelling;
		writer_up.tStart = startDate;
		writer_up.tTimestep = timeStepMinutes;
		writer_up.fileNovalue="-9999";

		writer_long.file = pathToLongwave;
		writer_long.tStart = startDate;
		writer_long.tTimestep = timeStepMinutes;
		writer_long.fileNovalue="-9999";



		Lwrb lwrb= new Lwrb();

		while( airTReader.doProcess  ) { 

			lwrb.stationsId=86;
			lwrb.X=0.52;
			lwrb.Y=0.21;
			lwrb.model="2";
			lwrb.epsilonS=0.98;
			lwrb.A_Cloud=0;
			lwrb.B_Cloud=1;

            
			airTReader.nextRecord();	
			HashMap<Integer, double[]> id2ValueMap = airTReader.outData;
			lwrb.inAirTempratureValues= id2ValueMap;

			soilTReader.nextRecord();
			id2ValueMap = soilTReader.outData;
			lwrb.inSoilTempratureValues = id2ValueMap;

			humidityReader.nextRecord();
			id2ValueMap = humidityReader.outData;
			lwrb.inHumidityValues= id2ValueMap;

			CIReader.nextRecord();
			id2ValueMap = CIReader.outData;
			lwrb.inClearnessIndexValues = id2ValueMap;

			lwrb.pm = pm;
			lwrb.process();

			HashMap<Integer, double[]> outHMdown = lwrb.outHMlongwaveDownwelling;
			HashMap<Integer, double[]> outHMup = lwrb.outHMlongwaveUpwelling;
			HashMap<Integer, double[]> outHMlong = lwrb.outHMlongwave;



			writer_down.inData = outHMdown;
			writer_down.writeNextLine();

			if (pathToDownwelling != null) {
				writer_down.close();
			}

			writer_up.inData = outHMup;
			writer_up.writeNextLine();

			if (pathToUpwelling != null) {
				writer_up.close();
			}

			writer_long.inData = outHMlong;
			writer_long.writeNextLine();

			if (pathToLongwave != null) {
				writer_long.close();
			}



		}
		airTReader.close();
		soilTReader.close();    
		humidityReader.close();     
		CIReader.close();

	}

	private OmsTimeSeriesIteratorReader getTimeseriesReader( String inPath, String id, String startDate, String endDate,
			int timeStepMinutes ) throws URISyntaxException {
		OmsTimeSeriesIteratorReader reader = new OmsTimeSeriesIteratorReader();
		reader.file = inPath;
		reader.idfield = "ID";
		reader.tStart = startDate;
		reader.tTimestep = 60;
		reader.tEnd = endDate;
		reader.fileNovalue = "-9999";
		reader.initProcess();
		return reader;
	}

}