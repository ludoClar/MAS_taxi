package test;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.query.space.grid.MooreQuery;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class Taxi extends Agent {

	ArrayList<Customer> clientsPossibles;
	ArrayList<Boolean> clientSuivi;
	int watched;
	Customer loadedCustomer;
	String preparedMessage;
	ArrayList<Double> minDistReceived;
	Coordonnees destination;
	boolean free;

	public Taxi(Grid<Agent> grid, ContinuousSpace<Agent> space) {
		super(grid, space);
		clientsPossibles = new ArrayList<Customer>();
		clientSuivi = new ArrayList<Boolean>();
		minDistReceived = new ArrayList<Double>(); //on met le nombre de base très haut pour que la détection ne soit pas faussée
		watched = 0;
		loadedCustomer = null;
		preparedMessage = "";
		destination = null;
		free = true;
	}

	public String getPreparedMessage() {
		return preparedMessage;
	}

	public void setPreparedMessage(String preparedMessage) {
		this.preparedMessage = preparedMessage;
	}

	// move the car
	public void compute() {
		int gridSize = 50 * 50;
		int nbTaxi = 4;
		int sizeToFill = (int) Math.sqrt(gridSize / nbTaxi);
		sizeToFill = sizeToFill / 2;
		//boolean isBusy = false;

		MooreQuery<Agent> query = new MooreQuery<Agent>(grid, this, sizeToFill + 5, sizeToFill + 5);
		neighboursTaxi = 0;
		for (Agent o : query.query())
			if (o instanceof Taxi)
				neighboursTaxi++;

		query = new MooreQuery<Agent>(grid, this, 24, 24);
		//neighboursClients = 0;
		for (Agent o : query.query())
			if (o instanceof Customer) {
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

		for (int j = 0; j < clientsPossibles.size(); j++)
		//System.out.println("Coordonées du client " + (j+1) + " : " + clientsPossibles.get(j).getCoordonnees().toString());
		{
			if (clientSuivi.get(j)) {
				Coordonnees coordTaxi = new Coordonnees(space.getLocation(this).getX(), space.getLocation(this).getY());
				double distance = clientsPossibles.get(j).getCoordonnees().getDistance(coordTaxi);
				//System.out.println("Distance taxi-client " + (j+1) + " : "+ distance);
				int id_client = clientsPossibles.get(j).getIDclient();
				String message = /* (j+1) */id_client + "_" + distance;
				//System.out.println(message);
				//si la distance calculée est inférieure à la distance que l'on a reçu : 
				if (minDistReceived.get(j) >= distance) {
					System.out.println(minDistReceived.get(j));
					System.out.println(distance);
					//watched = message; //on annonce à tout le monde que l'on est plus proche
					//System.out.println("je suis plus proche");
					setPreparedMessage(message);
					watched++; //on envoie le message

					//commence à se déplacer, ne fait plus rien d'autre
					moveTo(clientsPossibles.get(j));
					return;
				}
				else {
					clientSuivi.set(j, false); //sinon on ne suit plus le client car quelqu'un d'autre est plus proche
				}
			}

		}

		if (loadedCustomer != null) {
			moveTo(loadedCustomer);
		}
		//gestion des clients
		/* System.out.println("Liste du taxi : "); for (int p = 0 ; p <
		 * minDistReceived.size();p++) {
		 * System.out.println(minDistReceived.get(p)); } */

	}

	@Watch(watcheeClassName = "test.Taxi", watcheeFieldNames = "watched", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void changeDistanceReceived(Taxi taxi) {
		//System.out.println("message reçu!");
		//System.out.println(taxi.getPreparedMessage());
		String[] info = taxi.getPreparedMessage().split("_");
		int id_client = Integer.parseInt(info[0]);
		double dist = Double.parseDouble(info[1]);
		for (int i = 0; i < clientsPossibles.size(); i++) {
			if (clientsPossibles.get(i).getIDclient() == id_client) {
				if (dist < minDistReceived.get(i)) //si la distance reçue est plus petite que la distance stockée
				{
					minDistReceived.set(i, dist);
				}
			}
		}

	}

	/* public void moveTo(Customer cust) { Coordonnees coordTaxi = new
	 * Coordonnees(space.getLocation(this).getX(),
	 * space.getLocation(this).getY()); double distance =
	 * cust.getCoordonnees().getDistance(coordTaxi); //on calcule la distance
	 * par rapport au client System.out.println(
	 * "Distance taxi-client pour le déplacement vers le client : "+ distance);
	 * 
	 * //otherPoint = new NdPoint(50 * Math.random(), 50 * Math.random()); //on
	 * part dans une direction random NdPoint myPoint = space.getLocation(this);
	 * NdPoint otherPoint = space.getLocation(cust);
	 * 
	 * double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint,
	 * otherPoint); //déplacemement space.moveByVector(this, 0.1, angle, 0);
	 * //on se déplace dans l'espace grid.moveTo(this,
	 * (int)space.getLocation(this).getX(),
	 * (int)space.getLocation(this).getY()); //on recopie ce déplacement dans la
	 * grille
	 * 
	 * } */

	public void moveTo(Customer cust) {
		Coordonnees coordTaxi = new Coordonnees(space.getLocation(this).getX(), space.getLocation(this).getY());
		double distance = cust.getCoordonnees().getDistance(coordTaxi); //on calcule la distance par rapport au client
		//System.out.println("Distance taxi-client pour le déplacement vers le client : "+ distance);

		if (free) {
			if (distance > 0.5 && loadedCustomer == null) {
				//move to the customer
				NdPoint myPoint = space.getLocation(this);
				NdPoint otherPoint = space.getLocation(cust);
				//System.out.println(otherPoint.toString());

				double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
				//déplacemement
				space.moveByVector(this, 0.1, angle, 0); //on se déplace dans l'espace
				grid.moveTo(this, (int) space.getLocation(this).getX(), (int) space.getLocation(this).getY()); //on recopie ce déplacement dans la grille	
			}
			else {
				free = false;
				destination = cust.getdestination();
				Context context = ContextUtils.getContext(this);
				context.remove(cust);

				//TODO: nettoyer la liste taxis les plus proches des clients
				//flush lists? 
				clientsPossibles = new ArrayList<Customer>();
				clientSuivi = new ArrayList<Boolean>();
				minDistReceived = new ArrayList<Double>();
			}
		}
		else {
			//move to the destination
			//TODO: faire disparaitre client du dessin

			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(destination.getX(), destination.getY());
			//System.out.println(otherPoint.toString());

			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
			//déplacemement
			space.moveByVector(this, 0.1, angle, 0); //on se déplace dans l'espace
			grid.moveTo(this, (int) space.getLocation(this).getX(), (int) space.getLocation(this).getY()); //on recopie ce déplacement dans la grille

			//check if the ride is over
			distance = space.getDistance(myPoint, otherPoint);
			if (distance < 0.5)
				free = true;
			System.out.println("else | distance=" + distance + " | free=" + free);
		}

	}

	@Override
	public void implement() {
		// TODO Auto-generated method stub
	}

	public boolean isFree() {
		return this.free;
	}
}
