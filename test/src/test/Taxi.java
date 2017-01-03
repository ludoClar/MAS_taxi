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
	String preparedMessage;
	ArrayList<Double> minDistReceived;
	Coordonnees destination;
	boolean free;
	int memory;
	int memorySize;

	public Taxi(Grid<Agent> grid, ContinuousSpace<Agent> space) {
		super(grid, space);
		clientsPossibles = new ArrayList<Customer>();
		clientSuivi = new ArrayList<Boolean>();
		minDistReceived = new ArrayList<Double>(); //on met le nombre de base tr�s haut pour que la d�tection ne soit pas fauss�e
		watched = 0;
		preparedMessage = "";
		destination = null;
		free = true;
		memorySize = 100;
		memory = memorySize; //nombre de ticks entre deux reset de m�moire
	}

	public String getPreparedMessage() {
		return preparedMessage;
	}

	public void setPreparedMessage(String preparedMessage) {
		this.preparedMessage = preparedMessage;
	}

	// move the car
	public void compute() {
		
		if (!isFree())
		{
			moveTo(destination);
		}
		else
		{
			if (memory>0)
				memory--;
			else //quand le timer de la m�moire atteint 0, on l'efface
			{
				memory = memorySize;
				clientsPossibles = new ArrayList<Customer>();
				clientSuivi = new ArrayList<Boolean>();
				minDistReceived = new ArrayList<Double>(); //on met le nombre de base tr�s haut pour que la d�tection ne soit pas fauss�e
			}
			
			int gridSize = 50 * 50; //TODO: remplacer �a par la taille de la grille
			int nbTaxi = 4; //TODO: remplacer �a par le param�tre qu'on a rentr�
			int sizeToFill = (int) Math.sqrt(gridSize / nbTaxi);
			sizeToFill = sizeToFill / 2;

			
//			MooreQuery<Agent> query = new MooreQuery<Agent>(grid, this, sizeToFill + 5, sizeToFill + 5);
//			neighboursTaxi = 0;
//			for (Agent o : query.query())
//				if (o instanceof Taxi)
//					neighboursTaxi++;

			/*query = new MooreQuery<Agent>(grid, this, 24, 24); //TODO: pareil, changer �a par (taillegrid/2)-1
			for (Agent o : query.query())
				if (o instanceof Customer) { //on observe chaque client
					if (!clientsPossibles.contains(o)) //si on ne le stocke pas deja
					{
						clientsPossibles.add((Customer) o); //on le met en m�moire du taxi
						clientSuivi.add(true);
						minDistReceived.add(10000.0); //on commence avec une tr�s grande valeur pour ne pas perturber les autres
					}

				}*/

			for (int j = 0; j < clientsPossibles.size(); j++)
			{
				//System.out.println("Client potentiel en vue!");
				if (clientSuivi.get(j)) {
					//System.out.println("Ce client est suivi!");
					Coordonnees coordTaxi = new Coordonnees(space.getLocation(this).getX(), space.getLocation(this).getY());
					double distance = clientsPossibles.get(j).getCoordonnees().getDistance(coordTaxi);
					//System.out.println("Distance taxi-client " + (j+1) + " : "+ distance);
					int id_client = clientsPossibles.get(j).getIDclient();
					String message = /* (j+1) */id_client + "_" + distance;
					//System.out.println(message);
					//si la distance calcul�e est inf�rieure � la distance que l'on a re�u : 
					if (minDistReceived.get(j) >= distance) {
						//watched = message; //on annonce � tout le monde que l'on est plus proche
						//System.out.println("je suis plus proche");
						setPreparedMessage(message);
						watched++; //on envoie le message

						
						
						//commence � se d�placer, ne fait plus rien d'autre
						moveTo(clientsPossibles.get(j));
						return;
					}
					else {
						clientSuivi.set(j, false); //sinon on ne suit plus le client car quelqu'un d'autre est plus proche
					}
				}
			}
		}
	}

	@Watch(watcheeClassName = "test.Taxi", watcheeFieldNames = "watched", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void changeDistanceReceived(Taxi taxi) {
		//System.out.println("message re�u!");
		//System.out.println(taxi.getPreparedMessage());
		String[] info = taxi.getPreparedMessage().split("_");
		int id_client = Integer.parseInt(info[0]);
		double dist = Double.parseDouble(info[1]);
		for (int i = 0; i < clientsPossibles.size(); i++) {
			if (clientsPossibles.get(i).getIDclient() == id_client) {
				if (dist < minDistReceived.get(i)) //si la distance re�ue est plus petite que la distance stock�e
				{
					minDistReceived.set(i, dist);
				}
			}
		}
	}
	
	@Watch(watcheeClassName = "test.Customer", watcheeFieldNames = "shout", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void getCustomer(Customer customer) {
		if (!clientsPossibles.contains(customer)) //si on ne le stocke pas deja
		{
			clientsPossibles.add(customer); //on le met en m�moire du taxi
			clientSuivi.add(true);
			minDistReceived.add(10000.0); //on commence avec une tr�s grande valeur pour ne pas perturber les autres
		}
	}

	/* public void moveTo(Customer cust) { Coordonnees coordTaxi = new
	 * Coordonnees(space.getLocation(this).getX(),
	 * space.getLocation(this).getY()); double distance =
	 * cust.getCoordonnees().getDistance(coordTaxi); //on calcule la distance
	 * par rapport au client System.out.println(
	 * "Distance taxi-client pour le d�placement vers le client : "+ distance);
	 * 
	 * //otherPoint = new NdPoint(50 * Math.random(), 50 * Math.random()); //on
	 * part dans une direction random NdPoint myPoint = space.getLocation(this);
	 * NdPoint otherPoint = space.getLocation(cust);
	 * 
	 * double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint,
	 * otherPoint); //d�placemement space.moveByVector(this, 0.1, angle, 0);
	 * //on se d�place dans l'espace grid.moveTo(this,
	 * (int)space.getLocation(this).getX(),
	 * (int)space.getLocation(this).getY()); //on recopie ce d�placement dans la
	 * grille
	 * 
	 * } */

	public void moveTo(Customer cust) {
		Coordonnees coordTaxi = new Coordonnees(space.getLocation(this).getX(), space.getLocation(this).getY());
		double distance = cust.getCoordonnees().getDistance(coordTaxi); //on calcule la distance par rapport au client
		//System.out.println("Distance taxi-client pour le d�placement vers le client : "+ distance);

		if (free) {
			if (distance > 0.5) {
				//move to the customer
				NdPoint myPoint = space.getLocation(this);
				NdPoint otherPoint = space.getLocation(cust);
				//System.out.println(otherPoint.toString());

				double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
				//d�placemement
				space.moveByVector(this, 0.1, angle, 0); //on se d�place dans l'espace
				grid.moveTo(this, (int) space.getLocation(this).getX(), (int) space.getLocation(this).getY()); //on recopie ce d�placement dans la grille	
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
		/*else {
			//move to the destination
			//TODO: faire disparaitre client du dessin

			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(destination.getX(), destination.getY());
			//System.out.println(otherPoint.toString());

			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
			//d�placemement
			space.moveByVector(this, 0.1, angle, 0); //on se d�place dans l'espace
			grid.moveTo(this, (int) space.getLocation(this).getX(), (int) space.getLocation(this).getY()); //on recopie ce d�placement dans la grille

			//check if the ride is over
			distance = space.getDistance(myPoint, otherPoint);
			if (distance < 0.5)
				free = true;
			System.out.println("else | distance=" + distance + " | free=" + free);
		}*/

	}
	
	public void moveTo(Coordonnees coordonnes) {
		Coordonnees coordTaxi = new Coordonnees(space.getLocation(this).getX(), space.getLocation(this).getY());
		double distance = coordonnes.getDistance(coordTaxi); //on calcule la distance par rapport au client

		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint = new NdPoint(coordonnes.getX(), coordonnes.getY());
		//System.out.println(otherPoint.toString());

		double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
		//d�placemement
		space.moveByVector(this, 0.1, angle, 0); //on se d�place dans l'espace
		grid.moveTo(this, (int) space.getLocation(this).getX(), (int) space.getLocation(this).getY()); //on recopie ce d�placement dans la grille

		//check if the ride is over
		distance = space.getDistance(myPoint, otherPoint);
		if (distance < 0.5)
			free = true;
		System.out.println("else | distance=" + distance + " | free=" + free);
	}

	@Override
	public void implement() {
		
	}

	public boolean isFree() {
		return this.free;
	}
	
	//taux d'erreur ~5% avec la m�moire nettoy�, attention � ne pas avoir une m�moire trop fr�quemment nettoy�e
	//un taxi allait droit sur un client, sans raison il a saut� de cible, et le premier taxi s'est fait abandonner tout le long de la simulation
}
