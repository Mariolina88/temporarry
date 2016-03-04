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


public class Model6Idso implements Model{

	/** The x parameter of the model  */
	double X;

	/** The y parameter of the model */
	double Y;

	/** The input e */
	double e;

	/** The input air temperature */
	double airTemperature;


	public Model6Idso(double X, double Y, double airTemperature, double e){

		this.X=X;
		this.Y=Y;
		this.e=e;
		this.airTemperature=airTemperature;

	}

	/**
	 * Idso [1981].
	 *
	 * @return the double value of the clear sky emissivity
	 */
	public double epsilonCSValues() {

		return (X + Y * Math.pow(10, -4) * e * Math.exp(1500 / airTemperature));
	}

}
