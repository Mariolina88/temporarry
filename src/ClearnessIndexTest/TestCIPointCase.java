/*
 * This file is part of JGrasstools (http://www.jgrasstools.org)
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * JGrasstools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ClearnessIndexTest;

import java.net.URISyntaxException;
import java.util.HashMap;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.jgrasstools.gears.io.rasterreader.OmsRasterReader;
import org.jgrasstools.gears.io.shapefile.OmsShapefileFeatureReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorWriter;
import org.jgrasstools.hortonmachine.utils.HMTestCase;



import clearnessIndex.ClearnessIndexPointCase;
import rainSnowSperataion.RainSnowSeparationPointCase;


/**
 * Test the {@link Insolation} module.
 * 
 * @author Daniele Andreis
 */
public class TestCIPointCase extends HMTestCase {

	GridCoverage2D outSweDataGrid = null;
	GridCoverage2D outMeltingDataGrid = null;
	
	public void testSnow() throws Exception {


		String startDate = "2007-10-17 00:00" ;
		String endDate = "2007-10-18 00:00";
		int timeStepMinutes = 60;
		String fId = "ID";

		String inPathToSWRBmeasured ="Resources/Input/temperature_orarie_2002_2008_NEWNEW.csv";
		String inPathToTopATM ="Resources/Input/humidity_orarie_2002_2008_NEW.csv";
		String pathToCI= "/Users/marialaura/Desktop/CI.csv";


		OmsTimeSeriesIteratorReader SWRBreader = getTimeseriesReader(inPathToSWRBmeasured, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader topATMreader = getTimeseriesReader(inPathToTopATM, fId, startDate, endDate, timeStepMinutes);


		
		OmsShapefileFeatureReader stationsReader = new OmsShapefileFeatureReader();
		stationsReader.file = "Resources/Input/stazioniGIUSTE.shp";
		stationsReader.readFeatureCollection();
		SimpleFeatureCollection stationsFC = stationsReader.geodata;



		OmsTimeSeriesIteratorWriter writerCI = new OmsTimeSeriesIteratorWriter();


		
		writerCI.file = pathToCI;
		writerCI.tStart = startDate;
		writerCI.tTimestep = timeStepMinutes;
		writerCI.fileNovalue="-9999";

		 

		ClearnessIndexPointCase CI = new ClearnessIndexPointCase();
		CI.inStations = stationsFC;
		CI.fStationsid =  "int_1";

		while( topATMreader.doProcess  ) { 


			
			topATMreader.nextRecord();	
			HashMap<Integer, double[]> id2ValueMap = topATMreader.outData;
			CI.inSWRBTopATMValues= id2ValueMap;

			SWRBreader.nextRecord();
			id2ValueMap = SWRBreader.outData;
			CI.inSWRBMeasuredValues = id2ValueMap;
			

			CI.pm = pm;

			CI.process();
			
			
			 HashMap<Integer, double[]> outHM = CI.outCIHM;
	            
				writerCI.inData = outHM;
				writerCI.writeNextLine();
				
				
				
				if (pathToCI != null) {
					writerCI.close();
				}

	        
			
		}
		
		topATMreader.close();
		SWRBreader.close();


	}

	private OmsTimeSeriesIteratorReader getTimeseriesReader( String inPath, String id, String startDate, String endDate,
			int timeStepMinutes ) throws URISyntaxException {
		OmsTimeSeriesIteratorReader reader = new OmsTimeSeriesIteratorReader();
		reader.file = inPath;
		reader.idfield = "ID";
		reader.tStart = startDate;
		reader.tTimestep = timeStepMinutes;
		reader.tEnd = endDate;
		reader.fileNovalue = "-9999";
		reader.initProcess();
		return reader;
	}

}
