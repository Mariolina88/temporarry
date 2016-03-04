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
package snowMeltingPointCase;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

// TODO: Auto-generated Javadoc
/**
 * The Class FirstLayer implements the FirstOrderDifferentialEquations interface
 * and solves the water budget equation considering the upper layer in a 
 * model which considers different layers. Inputs are: the
 * precipitation (J), the evapotranspiration (ET), the coefficients of the 
 * non-linear reservoir model (a and b), the maximum value of the soil moisture (s_max),
 * the product of the porosity (n) and the depth of the root zone (Z), the 
 * soil moisture value at the previous time step (S)
 * @author Marialaura Bancheri
 */
public class LiquidWater implements FirstOrderDifferentialEquations{

	public static double precipitation;
	
	public static double freezing;

	public static double melting;




	public LiquidWater(double precipitation,double freezing,double melting) {
		this.precipitation=precipitation;
		this.freezing=freezing;
		this.melting=melting;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.commons.math3.ode.FirstOrderDifferentialEquations#getDimension()
	 */
	public int getDimension() {
		return 2;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.commons.math3.ode.FirstOrderDifferentialEquations#computeDerivatives(double, double[], double[])
	 */
	public void computeDerivatives(double t, double[] y, double[] yDot)
			throws MaxCountExceededException, DimensionMismatchException {
		yDot[0] =precipitation-freezing+melting;
		

	
	}


}
