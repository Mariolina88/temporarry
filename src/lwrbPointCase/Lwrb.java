/*
 * GNU GPL v3 License
 *
 * Copyright 2015 Marialaura Bancheri
 *
 * This program is free software: you can redistribute it and/or modify
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
package lwrbPointCase;


import org.geotools.feature.SchemaException;
import org.jgrasstools.gears.libs.modules.JGTConstants;
import java.util.HashMap;
import static org.jgrasstools.gears.libs.modules.JGTConstants.isNovalue;
import oms3.annotations.Author;
import oms3.annotations.Bibliography;
import oms3.annotations.Description;
import oms3.annotations.Documentation;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Keywords;
import oms3.annotations.Label;
import oms3.annotations.License;
import oms3.annotations.Name;
import oms3.annotations.Out;
import oms3.annotations.Status;
import oms3.annotations.Unit;

import org.jgrasstools.gears.libs.modules.JGTModel;



@Description("The component computes the longwave solar radiation, both upwelling and downwelling.")
@Documentation("")
@Author(name = "Marialaura Bancheri and Giuseppe Formetta", contact = "maryban@hotmail.it")
@Keywords("Hydrology, Radiation, Downwelling , upwelling")
@Label(JGTConstants.HYDROGEOMORPHOLOGY)
@Name("lwrb")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")

public class Lwrb extends JGTModel {


	@Description("Air temperature input value")
	@Unit("°C")
	double airTemperature;

	@Description("Air temperature input Hashmap")
	@In
	public HashMap<Integer, double[]> inAirTempratureValues;

	@Description("Soil temperature input value") 
	@Unit("°C")
	double soilTemperature;

	@Description("Soil temprature input Hashmap")
	@In
	public HashMap<Integer, double[]> inSoilTempratureValues;

	@Description("Humidity input value") 
	@Unit("%")
	double humidity;

	@Description("Reference humidity")
	private static final double pRH = 0.7;

	@Description("Humidity input Hashmap")
	@In
	public HashMap<Integer, double[]> inHumidityValues;

	@Description("Clearness index input value") 
	@Unit("[0,1]")
	double clearnessIndex;

	@Description("Clearness index input Hashmap")
	@In
	public HashMap<Integer, double[]> inClearnessIndexValues;

	@Description("X parameter of the literature formulation")
	@In
	public double X; 

	@Description("Y parameter of the literature formulation")
	@In
	public double Y ;

	@Description("Z parameter of the literature formulation")
	@In
	public double Z;

	@Description("The id of the station investigated")
	@In
	public int stationsId;

	@Description("Soil emissivity")
	@Unit("-")
	@In
	public double epsilonS;	

	@Description("String containing the number of the model: "
			+ " 1: Angstrom [1918];"
			+ " 2: Brunt's [1932];"
			+ " 3: Swinbank [1963];"
			+ " 4: Idso and Jackson [1969];"
			+ " 5: Brutsaert [1975];"
			+ " 6: Idso [1981];"
			+ " 7: Monteith and Unsworth [1990];"
			+ " 8: Konzelman [1994];"
			+ " 9: Prata [1996];"
			+ " 10: Dilley and O'Brien [1998];"
			+ " 11: To be implemented")
	@In
	public String model;

	@Description("Coefficient to take into account the cloud cover,"
			+ "set equal to 0 for clear sky conditions ")
	@In
	public double A_Cloud;

	@Description("Exponent  to take into account the cloud cover,"
			+ "set equal to 1 for clear sky conditions")
	@In
	public double B_Cloud;

	@Description("Stefan-Boltzaman costant")
	private static final double ConstBoltz = 5.670373 * Math.pow(10, -8);

	@Description("The output downwelling Hashmap")
	@Out
	public HashMap<Integer, double[]> outHMlongwaveDownwelling;

	@Description("The output upwelling Hashmap")
	@Out
	public HashMap<Integer, double[]> outHMlongwaveUpwelling;

	@Description("The output longwave Hashmap")
	@Out
	public HashMap<Integer, double[]> outHMlongwave;

	Model modelCS;



	/**
	 * Process.
	 *
	 * @throws Exception the exception
	 */
	@Execute
	public void process() throws Exception { 
			
		/**Input data reading*/
	     airTemperature = inAirTempratureValues.get(stationsId)[0];
	     if (isNovalue(airTemperature)) airTemperature=0;
	     
	     
		 soilTemperature = inSoilTempratureValues.get(stationsId)[0];
		 if (isNovalue(soilTemperature)) soilTemperature= 0;
		 
		 humidity = inHumidityValues.get(stationsId)[0];
		 if (isNovalue(humidity)) humidity= pRH;
		 
		 clearnessIndex = inClearnessIndexValues.get(stationsId)[0];
		 if (isNovalue(clearnessIndex )) clearnessIndex = 1;

		
		
		/**Computation of the downwelling, upwelling and longwave:
		 * if there is no value in the input data, there will be no value also in
		 * the output*/
		double downwellingALLSKY=(airTemperature==0)? Double.NaN:computeDownwelling(model,airTemperature,humidity/100, clearnessIndex);
		double upwelling=(soilTemperature==0)? Double.NaN:computeUpwelling(soilTemperature);
		double longwave=downwellingALLSKY+upwelling;


		/**Store results in Hashmaps*/
		storeResult(downwellingALLSKY,upwelling,longwave);


	}



	/**
	 * Compute downwelling longwave radiation.
	 *
	 * @param model: the string containing the number of the model
	 * @param airTemperature:  the air temperature input
	 * @param humidity: the humidity input
	 * @param clearnessIndex: the clearness index input
	 * @return the double value of the all sky downwelling
	 */
	private double computeDownwelling(String model,double airTemperature, 
			double humidity, double clearnessIndex){

		/**e is the screen-level water-vapor pressure*/
		double e = humidity *6.11 * Math.pow(10, (7.5 * airTemperature) / (237.3 + airTemperature)) / 10;

		/**compute the clear sky emissivity*/
		modelCS=SimpleModelFactory.createModel(model,X,Y,Z,airTemperature+ 273.15,e);
		double epsilonCS=modelCS.epsilonCSValues();

		/**compute the downwelling in clear sky conditions*/
		double downwellingCS=epsilonCS* ConstBoltz* Math.pow(airTemperature+ 273.15, 4);

		/**compute the cloudness index*/
		double cloudnessIndex = 1 + A_Cloud* Math.pow(clearnessIndex, B_Cloud);

		/**compute the downwelling in all-sky conditions*/
		return downwellingCS * cloudnessIndex;

	}

	/**
	 * Compute upwelling longwave radiation .
	 *
	 * @param soilTemperature: the soil temperature input
	 * @return the double value of the upwelling
	 */
	private double computeUpwelling( double soilTemperature){

		/**compute the upwelling*/
		return epsilonS * ConstBoltz * Math.pow(soilTemperature+ 273.15, 4);
	}

	/**
	 * Store result in given hashpmaps.
	 *
	 * @param downwellingALLSKY: the downwelling radiation in all sky conditions
	 * @param upwelling: the upwelling radiation
	 * @param longwave: the longwave radiation
	 * @throws SchemaException 
	 */
	private void storeResult(double downwellingALLSKY, double upwelling,double longwave) 
			throws SchemaException {
		outHMlongwaveDownwelling = new HashMap<Integer, double[]>();
		outHMlongwaveUpwelling = new HashMap<Integer, double[]>();
		outHMlongwave = new HashMap<Integer, double[]>();

		outHMlongwaveDownwelling.put(stationsId, new double[]{downwellingALLSKY});
		outHMlongwaveUpwelling.put(stationsId, new double[]{upwelling});
		outHMlongwave.put(stationsId, new double[]{longwave});
	}


}