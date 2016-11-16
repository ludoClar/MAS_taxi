package test;

import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

public class FreeTaxi extends Taxi {

	public FreeTaxi(Grid<Taxi> grid,ContinuousSpace<Taxi> space) {
		super(grid,space);
		// TODO Auto-generated constructor stub
	}

	// move the car
	public void compute() {
		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint;
		if (lastCalc != 0) //si on a pas besoin  de recalculer
		{
			otherPoint = dest;
			lastCalc--;
		}
		else //si on a besoin d'en tirer un nouveau
		{
			otherPoint = new NdPoint(50 * Math.random(), 50 * Math.random());
			dest = otherPoint;
			lastCalc = 20;
		}
		double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
		space.moveByVector(this, 0.1, angle, 0); //on se déplace dans l'espace
		grid.moveTo(this, (int)space.getLocation(this).getX(), (int)space.getLocation(this).getY()); //on recopie ce déplacement dans la grille	

		//		MooreQuery<FreeTaxi> query = new MooreQuery<FreeTaxi>(space, this);
		//		neighbours = 0;
		//		for (FreeTaxi o : query.query())
		//			if (o instanceof FreeTaxi)
		//				neighbours++;

	}

	@Override
	public void implement() {
		// TODO Auto-generated method stub
	}

}
