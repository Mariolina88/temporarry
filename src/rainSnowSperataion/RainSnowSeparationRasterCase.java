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
 package rainSnowSperataion;


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
import oms3.annotations.Unit;

import org.geotools.coverage.grid.GridCoverage2D;
import org.jgrasstools.gears.libs.modules.JGTConstants;
import org.jgrasstools.gears.libs.modules.JGTModel;

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
public class RainSnowSeparationRasterCase extends JGTModel {

	@Description("The map of the interpolated temperature.")
	@In
	public GridCoverage2D inTemperatureGrid;

	@Description("The double value of the  temperature, once read from the HashMap")
	double temperature;

	@Description("The map of the the interpolated precipitation.")
	@In
	public GridCoverage2D inPrecipitationGrid;

	@Description("The double value of the precipitation, once read from the HashMap")
	double precipitation;

	@Description("Alfa_r is the adjustment parameter for the precipitation measurements errors")
	@In
	public double alfa_r;

	@Description("Alfa_s is the adjustment parameter for the snow measurements errors")
	@In
	public double alfa_s;

	@Description("m1 is the smoothing parameter, for the detecting ot the rainfall in "
			+ "the total precipitation")
	@In
	public double m1 = 1.0;


	@Description("The melting temperature")
	@In
	@Unit("C")
	public double meltingTemperature;


	@Description("the linked HashMap with the coordinate of the stations")
	LinkedHashMap<Integer, Coordinate> stationCoordinates;

	@Description("The digital elevation model.")
	@In
	public GridCoverage2D inDem;

	@Description("The output rainfall map")
	@Out
	public WritableRaster outRainfallWritableRaster = null;

	@Description(" The output snowfall map")
	@Out
	public WritableRaster outSnowfallWritableRaster = null;



	@Execute
	public void process() throws Exception { 

		// transform the GrifCoverage2D maps into writable rasters
		WritableRaster temperatureMap=mapsReader(inTemperatureGrid);
		WritableRaster precipitationMap=mapsReader(inPrecipitationGrid);

		// get the dimension of the maps
		int height=temperatureMap.getHeight();
		int width=temperatureMap.getWidth();

		// iterate over the entire domain and compute for each pixel the SWE
		for (int i=0;i<width;i++){
			for (int j=0;j<height;j++){

				// get the exact value of the variable in the pixel i, j 
				precipitation=precipitationMap.getSampleDouble(i, j, 0);
				temperature=temperatureMap.getSampleDouble(i, j, 0);

				// compute the rainfall and the snowfall according to Kavetski et al. (2006)
				double rainfall=alfa_r*((precipitation/ Math.PI)* Math.atan((temperature - meltingTemperature) / m1)+precipitation/2);
				double snowfall=alfa_s*(precipitation-rainfall);

				// create the output maps with the right dimensions
				outRainfallWritableRaster = CoverageUtilities.createDoubleWritableRaster(width, height,null, null, null);
				outSnowfallWritableRaster = CoverageUtilities.createDoubleWritableRaster(width, height,null, null, null);

				// computes the SWE and the melting discharge and store them in the maps
				storeResultMaps(rainfall, snowfall, i,j);


			}
		}


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




	/**
	 * Store the result in the output maps.
	 *
	 * @param rainfall is the output rainfall
	 * @param snowfall is the output snowfall
	 * @param i the i-position of the the pixel in the map
	 * @param j the j-position of the pixel in the map
	 * @throws MismatchedDimensionException the mismatched dimension exception
	 * @throws Exception
	 */
	private void storeResultMaps(double rainfall , double snowfall,int i, int j)
			throws MismatchedDimensionException, Exception {

		WritableRandomIter outIterRain = RandomIterFactory.createWritable(outRainfallWritableRaster, null);
		WritableRandomIter outIterSnow = RandomIterFactory.createWritable(outSnowfallWritableRaster, null);

		outIterRain.setSample(i, j, 0, rainfall);
		outIterSnow.setSample(i, j, 0, snowfall);


	}


}
