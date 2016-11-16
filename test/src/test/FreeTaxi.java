package test;

import repast.simphony.query.space.grid.MooreQuery;
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
		int gridSize = 50*50;
		int nbTaxi = 20;
		int sizeToFill = (int) Math.sqrt(gridSize/nbTaxi);
		sizeToFill  = sizeToFill/2;
//		System.out.println(String.valueOf(sizeToFill));
		/*if (lastCalc != 0) //si on a pas besoin  de recalculer
		{
			otherPoint = dest;
			lastCalc--;
		}
		else //si on a besoin d'en tirer un nouveau
		{
			otherPoint = new NdPoint(50 * Math.random(), 50 * Math.random());
			dest = otherPoint;
			lastCalc = 20;
		}*/

		//on regarde si on à d'autres taxis a proximité
		MooreQuery<Taxi> query = new MooreQuery<Taxi>(grid, this,sizeToFill,sizeToFill);
		neighbours = 0;
		for (Taxi o : query.query())
			if (o instanceof FreeTaxi)
				neighbours++;
		
		if (neighbours == 0) //si pas de voisin, on ne bouge pas
		{
//			System.out.println("seul");
			otherPoint = myPoint; //si on a pas de voisin, on reste au même endroit
		}
		else //sinon on se déplace
		{
//			System.out.println("pas seul");
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
			//otherPoint = new NdPoint(50 * Math.random(), 50 * Math.random()); //on part dans une direction random
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
			//déplacemement
			space.moveByVector(this, 0.1, angle, 0); //on se déplace dans l'espace
			grid.moveTo(this, (int)space.getLocation(this).getX(), (int)space.getLocation(this).getY()); //on recopie ce déplacement dans la grille	
		}
				
		
		
	}

	@Override
	public void implement() {
		// TODO Auto-generated method stub
	}

}
