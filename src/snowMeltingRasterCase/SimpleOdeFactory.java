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
package snowMeltingRasterCase;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

// TODO: Auto-generated Javadoc
/**
 * A simple factory pattern that create concrete ODE
 * for the resolution of the water budget equations: FirstLayer, 
 * SecondLayer and ExternalValues. 
 * @author Marialaura Bancheri
 */
public class SimpleOdeFactory {

	

	public static FirstOrderDifferentialEquations createOde(String type,double precipitation,double freezing,double melting) 
			{
		FirstOrderDifferentialEquations ode=null;

		if (type.equals("SolidWater")){
			ode=new SolidWater(precipitation,freezing,melting);
		}else if (type.equals("LiquidWater")){
			ode=new LiquidWater(precipitation,freezing,melting);
		}
			
		return ode;
		
	}

}
