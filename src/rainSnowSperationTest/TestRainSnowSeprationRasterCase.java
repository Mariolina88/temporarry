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
package rainSnowSperationTest;



import org.geotools.coverage.grid.GridCoverage2D;

import org.jgrasstools.gears.io.rasterreader.OmsRasterReader;
import org.jgrasstools.gears.io.rasterwriter.OmsRasterWriter;

import org.jgrasstools.hortonmachine.utils.HMTestCase;


import rainSnowSperataion.RainSnowSeparationRasterCase;

/**
 * Test the separetor module.
 * 
 * @author Marialaura Bancheri
 */
public class TestRainSnowSeprationRasterCase extends HMTestCase {

	GridCoverage2D outRainfallDataGrid = null;
	GridCoverage2D outSnowfallDataGrid = null;

	public TestRainSnowSeprationRasterCase() throws Exception {


		OmsRasterReader demReader = new OmsRasterReader();
		demReader.file = "/Users/marialaura/Desktop/dottorato/CSU/NeveGiuseppe/data/Maps/pit.asc";
		demReader.fileNovalue = -9999.0;
		demReader.geodataNovalue = Double.NaN;
		demReader.process();
		GridCoverage2D dem = demReader.outRaster;
		
		


		RainSnowSeparationRasterCase separetor = new RainSnowSeparationRasterCase();
		separetor.inPrecipitationGrid=dem;
		separetor.inTemperatureGrid=dem;
		separetor.inDem = dem;


	
		separetor.alfa_r=1.12963980507173877;
		separetor.alfa_s= 1.07229882570334652;
		separetor.meltingTemperature=-0.64798915634369553;

		separetor.pm = pm;

		separetor.process();



		outRainfallDataGrid =separetor.outRainfallGrid;
		outSnowfallDataGrid = separetor.outSnowfallGrid;

		OmsRasterWriter writerRainfallRaster = new OmsRasterWriter();
		writerRainfallRaster.inRaster = outRainfallDataGrid;
		writerRainfallRaster.file = "/Users/marialaura/Desktop/mapRainfall.asc";
		writerRainfallRaster.process();

		OmsRasterWriter writerSnowfallRaster = new OmsRasterWriter();
		writerSnowfallRaster.inRaster = outSnowfallDataGrid;
		writerSnowfallRaster.file = "/Users/marialaura/Desktop/mapSnowfall.asc";
		writerSnowfallRaster.process();

	}


}
