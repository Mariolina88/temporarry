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
package clearnessIndexTest;



import org.geotools.coverage.grid.GridCoverage2D;

import org.jgrasstools.gears.io.rasterreader.OmsRasterReader;
import org.jgrasstools.gears.io.rasterwriter.OmsRasterWriter;

import org.jgrasstools.hortonmachine.utils.HMTestCase;


import clearnessIndex.ClearnessIndexRasterCase;

/**
 * Test the CI module.
 * 
 * @author Marialaura Bancheri
 */
public class TestCIRasterCase extends HMTestCase {

	GridCoverage2D outCIDataGrid2 ;

	public TestCIRasterCase() throws Exception {


		OmsRasterReader demReader = new OmsRasterReader();
		demReader.file = "/Users/marialaura/Desktop/dottorato/CSU/NeveGiuseppe/data/Maps/pit.asc";
		demReader.fileNovalue = -9999.0;
		demReader.geodataNovalue = Double.NaN;
		demReader.process();
		GridCoverage2D dem = demReader.outRaster;
		
		OmsRasterReader demReader2 = new OmsRasterReader();
		demReader2.file = "/Users/marialaura/Desktop/dottorato/CSU/NeveGiuseppe/data/Maps/pit2.asc";
		demReader2.fileNovalue = -9999.0;
		demReader2.geodataNovalue = Double.NaN;
		demReader2.process();
		
		GridCoverage2D dem2 = demReader2.outRaster;
		
		


		ClearnessIndexRasterCase CI = new ClearnessIndexRasterCase();
		CI.inSWRBMeasuredGrid=dem;
		CI.inSWRBTopATMGrid=dem2;
		CI.inDem = dem;



		CI.pm = pm;

		CI.process();


		GridCoverage2D CICoverage = CI.outCIDataGrid;
		
		OmsRasterWriter writerRainfallRaster = new OmsRasterWriter();
		writerRainfallRaster.inRaster = CICoverage;
		writerRainfallRaster.file = "/Users/marialaura/Desktop/CI.asc";
		writerRainfallRaster.process();



	}


}
