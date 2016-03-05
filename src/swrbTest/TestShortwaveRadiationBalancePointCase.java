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
package swrbTest;

import java.net.URISyntaxException;
import java.util.HashMap;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.jgrasstools.gears.io.rasterreader.OmsRasterReader;
import org.jgrasstools.gears.io.shapefile.OmsShapefileFeatureReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorReader;
import org.jgrasstools.gears.io.timedependent.OmsTimeSeriesIteratorWriter;

import swrbPointCase.ShortwaveRadiationBalance;


import org.jgrasstools.hortonmachine.utils.HMTestCase;

/**
 * Test the {@link SWRB} module.
 * 
 * @author Marialaura Bancheri 
 */
public class TestShortwaveRadiationBalancePointCase extends HMTestCase {



	public void testInsolation() throws Exception {

		String startDate = "2002-01-01 00:00";
		String endDate = "2002-01-01 10:00";
		int timeStepMinutes = 60;
		String fId = "ID";


		String inPathToAirT ="Resources/Input/temperature_orarie_2002_2008_NEWNEW.csv";
		String inPathToHumidity ="Resources/Input/humidity_orarie_2002_2008_NEW.csv";

		OmsTimeSeriesIteratorReader airTReader = getTimeseriesReader(inPathToAirT, fId, startDate, endDate, timeStepMinutes);
		OmsTimeSeriesIteratorReader humidityReader = getTimeseriesReader(inPathToHumidity, fId, startDate, endDate, timeStepMinutes);

		OmsShapefileFeatureReader stationsReader = new OmsShapefileFeatureReader();
		stationsReader.file = "Resources/Input/stazioniGIUSTE.shp";
		stationsReader.readFeatureCollection();
		SimpleFeatureCollection stationsFC = stationsReader.geodata;

		OmsRasterReader reader = new OmsRasterReader();
		reader.file = "Resources/Input/pit_LW.asc";
		reader.fileNovalue = -9999.0;
		reader.geodataNovalue = Double.NaN;
		reader.process();
		GridCoverage2D pit = reader.outRaster;

		OmsRasterReader readers = new OmsRasterReader();
		readers.file = "Resources/Input/sky.asc";
		readers.fileNovalue = -9999.0;
		readers.geodataNovalue = Double.NaN;
		readers.process();
		GridCoverage2D skyviewfactor = readers.outRaster;

		String pathToDirect= "Resources/Output/DIRETTA_Mary.csv";
		String pathToDiffuse= "Resources/Output/DIFFUSA_Mary.csv";
		String pathToTopATM= "Resources/Output/TOPATM_Mary.csv";

		OmsTimeSeriesIteratorWriter writerDirect = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writerDiffuse = new OmsTimeSeriesIteratorWriter();
		OmsTimeSeriesIteratorWriter writerTopAtm = new OmsTimeSeriesIteratorWriter();


		writerDirect.file = pathToDirect;
		writerDirect.tStart = startDate;
		writerDirect.tTimestep = timeStepMinutes;
		writerDirect.fileNovalue="-9999";

		writerDiffuse.file = pathToDiffuse;
		writerDiffuse.tStart = startDate;
		writerDiffuse.tTimestep = timeStepMinutes;
		writerDiffuse.fileNovalue="-9999";

		writerTopAtm.file = pathToTopATM;
		writerTopAtm.tStart = startDate;
		writerTopAtm.tTimestep = timeStepMinutes;
		writerTopAtm.fileNovalue="-9999";



		ShortwaveRadiationBalance insolation = new ShortwaveRadiationBalance();
		insolation.inStations = stationsFC;
		insolation.inDem = pit;
		insolation.inSkyview = skyviewfactor;
		insolation.tStartDate = startDate;
		insolation.fStationsid = "int_1";
		insolation.timeStep="Hourly";

		while( airTReader.doProcess  ) {

			airTReader.nextRecord();	
			HashMap<Integer, double[]> id2ValueMap = airTReader.outData;
			insolation.inTemperatureValues= id2ValueMap;

			humidityReader.nextRecord();
			id2ValueMap = humidityReader.outData;
			insolation.inHumidityValues = id2ValueMap;


			insolation.process();

			HashMap<Integer, double[]> outHMdirect = insolation.outHMdirect;
			HashMap<Integer, double[]> outHMdiffuse = insolation.outHMdiffuse;
			HashMap<Integer, double[]> outHMtop = insolation.outHMtopatm;


			writerDirect.inData = outHMdirect ;
			writerDirect.writeNextLine();

			if (pathToDirect != null) {
				writerDirect .close();
			}

			writerDiffuse.inData = outHMdiffuse ;
			writerDiffuse.writeNextLine();

			if (pathToDiffuse != null) {
				writerDiffuse .close();
			}	

			writerTopAtm.inData =outHMtop;
			writerTopAtm.writeNextLine();

			if (pathToTopATM != null) {
				writerTopAtm.close();
			}


		}

		airTReader.close();
		humidityReader.close();


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
