package test;

import java.util.ArrayList;

import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.query.space.grid.MooreQuery;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

public class Taxi extends Agent {
	
	ArrayList<Customer> clientsPossibles;
	ArrayList<Boolean> clientSuivi;
	int watched;
	String preparedMessage;
	ArrayList<Double> minDistReceived;
	

	public Taxi(Grid<Agent> grid,ContinuousSpace<Agent> space) {
		super(grid,space);
		// TODO Auto-generated constructor stub
		clientsPossibles = new ArrayList<Customer>();
		clientSuivi = new ArrayList<Boolean>();
		watched = 0;
		preparedMessage = "";
		minDistReceived = new ArrayList<Double>(); //on met le nombre de base très haut pour que la détection ne soit pas faussée
	}

	public String getPreparedMessage() {
		return preparedMessage;
	}

	public void setPreparedMessage(String preparedMessage) {
		this.preparedMessage = preparedMessage;
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
					minDistReceived.add(10000.0); //on commence avec une très grande valeur pour ne pas perturber les autres
				}
					
			}
		
		for (int j = 0 ; j < clientsPossibles.size();j++)
			//System.out.println("Coordonées du client " + (j+1) + " : " + clientsPossibles.get(j).getCoordonnees().toString());
		{
			if (clientSuivi.get(j))
			{
				Coordonnees coordTaxi = new Coordonnees(space.getLocation(this).getX(), space.getLocation(this).getY());
				double distance = clientsPossibles.get(j).getCoordonnees().getDistance(coordTaxi);
				//System.out.println("Distance taxi-client " + (j+1) + " : "+ distance);
				int id_client = clientsPossibles.get(j).getIDclient();
				String message = /*(j+1)*/id_client + "_" + distance;
				//System.out.println(message);
				//si la distance calculée est inférieure à la distance que l'on a reçu : 
				if (minDistReceived.get(j)>distance)
				{
					//watched = message; //on annonce à tout le monde que l'on est plus proche
					//System.out.println("je suis plus proche");
					setPreparedMessage(message);
					watched++; //on envoie le message
					
					
					//commence à se déplacer, ne fait plus rien d'autre
					moveTo(clientsPossibles.get(j));
					return;
				}
				else
				{
					clientSuivi.set(j, false);	//sinon on ne suit plus le client car quelqu'un d'autre est plus proche
				}
			}
				
		}
		
		
		//gestion de la répartition sur l'espace
		/*if (neighboursTaxi == 0) //si pas de voisin, on ne bouge pas
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
		}*/
				
		//gestion des clients
		System.out.println("Liste du taxi : ");
		for (int p = 0 ; p < minDistReceived.size();p++)
		{
			System.out.println(minDistReceived.get(p));
		}
		
		
	}

	@Watch(watcheeClassName = "test.Taxi",
			watcheeFieldNames = "watched",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void changeDistanceReceived(Taxi taxi)
	{
		//System.out.println("message reçu!");
		//System.out.println(taxi.getPreparedMessage());
		String[] info = taxi.getPreparedMessage().split("_");
		int id_client = Integer.parseInt(info[0]);
		double dist = Double.parseDouble(info[1]);
		for (int i = 0 ; i < clientsPossibles.size();i++)
		{
			if (clientsPossibles.get(i).getIDclient() == id_client)
			{
				if(dist<minDistReceived.get(i)) //si la distance reçue est plus petite que la distance stockée
				{
					minDistReceived.set(i, dist);
				}
			}
		}

	}
	
	public void moveTo(Customer cust)
	{
		//otherPoint = new NdPoint(50 * Math.random(), 50 * Math.random()); //on part dans une direction random
		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint = space.getLocation(cust);
		
		double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
		//déplacemement
		space.moveByVector(this, 0.1, angle, 0); //on se déplace dans l'espace
		grid.moveTo(this, (int)space.getLocation(this).getX(), (int)space.getLocation(this).getY()); //on recopie ce déplacement dans la grille	
	
	}
	
	@Override
	public void implement() {
		// TODO Auto-generated method stub
	}

}
