package test;

import java.util.ArrayList;

import repast.simphony.query.space.grid.MooreQuery;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

public class Taxi extends Agent {
	
	ArrayList<Customer> clientsPossibles;
	ArrayList<Boolean> clientSuivi;
	

	public Taxi(Grid<Agent> grid,ContinuousSpace<Agent> space) {
		super(grid,space);
		// TODO Auto-generated constructor stub
		clientsPossibles = new ArrayList<Customer>();
		clientSuivi = new ArrayList<Boolean>();
	}

	// move the car
	public void compute() {
		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint;
		int gridSize = 50*50;
		int nbTaxi = 4;
		int sizeToFill = (int) Math.sqrt(gridSize/nbTaxi);
		sizeToFill  = sizeToFill/2;
		boolean isBusy = false;
		
		
		
		MooreQuery<Agent> query = new MooreQuery<Agent>(grid, this,sizeToFill+5,sizeToFill+5);
		neighboursTaxi = 0;
		for (Agent o : query.query())
			if (o instanceof Taxi)
				neighboursTaxi++;
		
		query = new MooreQuery<Agent>(grid, this,24,24);
		//neighboursClients = 0;
		for (Agent o : query.query())
			if (o instanceof Customer)
			{
				//System.out.println("ID de client : " + String.valueOf(((Customer) o).getIDclient()));
				//neighboursClients++;
				//System.out.println(((Customer) o).getCoordonnees().toString());
				if (!clientsPossibles.contains(o)) //si on ne le stocke pas deja
				{
					clientsPossibles.add((Customer) o);
					clientSuivi.add(true);
				}
					
			}
		
		for (int j = 0 ; j < clientsPossibles.size();j++)
			//System.out.println("Coordonées du client " + (j+1) + " : " + clientsPossibles.get(j).getCoordonnees().toString());
		{
			if (clientSuivi.get(j))
			{
				Coordonnees coordTaxi = new Coordonnees(space.getLocation(this).getX(), space.getLocation(this).getY());
				double distance = clientsPossibles.get(j).getCoordonnees().getDistance(coordTaxi);
				System.out.println("Distance taxi-client " + (j+1) + " : "+ distance);
			}
				
		}
			
				
		
		//System.out.println("nombre de clients dans la zone : " + String.valueOf(neighboursClients));
		
		
		//gestion de la répartition sur l'espace
		if (neighboursTaxi == 0) //si pas de voisin, on ne bouge pas
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
				
		//gestion des clients
		
		
		
		
	}

	@Override
	public void implement() {
		// TODO Auto-generated method stub
	}

}
