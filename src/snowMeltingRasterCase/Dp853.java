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
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;

// TODO: Auto-generated Javadoc
/**
 * The Class Dp853 is the concrete implementation of the SolverOde
 * interface. It implements the Domand Prince 853 solver. The inputs are
 * the integration time dt, the ODE to be solved and the boundary conditions
 * @author Marialaura Bancheri
 */
public class Dp853 implements SolverODE{

	FirstOrderIntegrator integrator;

	double dt;

	FirstOrderDifferentialEquations ode;

	double y [];

	/**
	 * Instantiates a new dp853 solver.
	 *
	 * @param dt: the integration time
	 * @param ode: is the ordinary differential equation to be solved 
	 * @param y: is a vector with the boundary conditions
	 */
	public Dp853(double dt, FirstOrderDifferentialEquations ode,double[] y){
		this.ode=ode;
		this.y=y;
		this.dt=dt;
		this.integrator=new DormandPrince853Integrator(1.0e-8,dt, 1.0e-10, 1.0e-10);
	}

	/* (non-Javadoc)
	 * @see waterBudget.SolverODE#integrateValues()
	 */
	public double integrateValues() {
		integrator.integrate(ode, 0.0, y, dt, y);
		return y[0];

	}


}
