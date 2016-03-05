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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
 package clearnessIndex;


import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.LinkedHashMap;

import javax.media.jai.iterator.RandomIterFactory;
import javax.media.jai.iterator.WritableRandomIter;

import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Keywords;
import oms3.annotations.Label;
import oms3.annotations.License;
import oms3.annotations.Name;
import oms3.annotations.Out;
import oms3.annotations.Status;

import org.geotools.coverage.grid.GridCoverage2D;
import org.jgrasstools.gears.libs.modules.JGTConstants;
import org.jgrasstools.gears.libs.modules.JGTModel;
import org.jgrasstools.gears.utils.RegionMap;
import org.jgrasstools.gears.utils.coverage.CoverageUtilities;
import org.opengis.geometry.MismatchedDimensionException;

import com.vividsolutions.jts.geom.Coordinate;


@Description("")
@Author(name = "Marialaura Bancheri and Giuseppe Formetta", contact = "maryban@hotmail.it")
@Keywords("Hydrology, Rain-snow separation")
@Label(JGTConstants.HYDROGEOMORPHOLOGY)
@Name("Rain-snow separation raster case")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")
public class ClearnessIndexRasterCase extends JGTModel {

	@Description("The map of the interpolated measured shortwave .")
	@In
	public GridCoverage2D inSWRBMeasuredGrid;

	@Description("The double value of the SWRB meadured, once read from the HashMap")
	double SWRBMeasured;

	@Description("The map of the the interpolated SWRB at the top of the atmosphere.")
	@In
	public GridCoverage2D inSWRBTopATMGrid;

	@Description("The double value of the SWRB at the top of the atmosphere, once read from the HashMap")
	double SWRBTopATM;

	@Description("the linked HashMap with the coordinate of the stations")
	LinkedHashMap<Integer, Coordinate> stationCoordinates;

	@Description("The digital elevation model.")
	@In
	public GridCoverage2D inDem;
	
	@Description("The clearness index map")
	@Out
	public GridCoverage2D outCIDataGrid;
	




	@Execute
	public void process() throws Exception { 

		// transform the GrifCoverage2D maps into writable rasters
		WritableRaster SWRBMeasuredMap=mapsReader(inSWRBMeasuredGrid);
		WritableRaster SWRBTopATMMap=mapsReader(inSWRBTopATMGrid);

		// get the dimension of the maps
		RegionMap regionMap = CoverageUtilities.getRegionParamsFromGridCoverage(inDem);
        int cols = regionMap.getCols();
        int rows = regionMap.getRows();

		// create the output maps with the right dimensions
		WritableRaster outCIWritableRaster= CoverageUtilities.createDoubleWritableRaster(cols, rows, null, null, null);
		WritableRandomIter CIIter = RandomIterFactory.createWritable(outCIWritableRaster, null);
		
		// iterate over the entire domain and compute for each pixel the SWE
		for( int r = 1; r < rows - 1; r++ ) {
            for( int c = 1; c < cols - 1; c++ ) {

				// get the exact value of the variable in the pixel i, j 
				SWRBMeasured=SWRBMeasuredMap.getSampleDouble(c, r, 0);
				SWRBTopATM=SWRBTopATMMap.getSampleDouble(c, r, 0);

				// compute the clearness index
				double CI=SWRBMeasured/SWRBTopATM;

				CIIter.setSample(c, r, 0, CI);


			}
		}

		CoverageUtilities.setNovalueBorder(outCIWritableRaster);
		outCIDataGrid = CoverageUtilities.buildCoverage("CI", outCIWritableRaster, 
				regionMap, inDem.getCoordinateReferenceSystem());

	}
	/**
	 * Maps reader transform the GrifCoverage2D in to the writable raster and
	 * replace the -9999.0 value with no value.
	 *
	 * @param inValues: the input map values
	 * @return the writable raster of the given map
	 */
	private WritableRaster mapsReader ( GridCoverage2D inValues){	
		RenderedImage inValuesRenderedImage = inValues.getRenderedImage();
		WritableRaster inValuesWR = CoverageUtilities.replaceNovalue(inValuesRenderedImage, -9999.0);
		inValuesRenderedImage = null;
		return inValuesWR;
	}



}
