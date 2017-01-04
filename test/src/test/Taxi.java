package test;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class Taxi extends Agent {

	ArrayList<Customer> clientsPossibles;
	ArrayList<Boolean> clientSuivi;
	protected boolean babySeat;
	int watched;
	String preparedMessage;
	ArrayList<Double> minDistReceived;
	Coordonnees destination;
	boolean free;

	public Taxi(Grid<Agent> grid, ContinuousSpace<Agent> space, boolean babySeat) {
		super(grid, space);
		clientsPossibles = new ArrayList<Customer>();
		clientSuivi = new ArrayList<Boolean>();
		minDistReceived = new ArrayList<Double>(); //on met le nombre de base très haut pour que la détection ne soit pas faussée
		watched = 0;
		preparedMessage = "";
		destination = null;
		free = true;
		this.babySeat = babySeat;
	}

	public String getPreparedMessage() {
		return preparedMessage;
	}

	public void setPreparedMessage(String preparedMessage) {
		this.preparedMessage = preparedMessage;
	}

	public void compute() {
		if (!isFree()) {
			moveTo(destination);
		}
		else {
			ArrayList<Double> distCalc = new ArrayList<Double>();
			//System.out.println("clPossibles.size() ==>" + clientsPossibles.size());
			for (int i = 0; i < clientsPossibles.size(); i++) 
			{
				Coordonnees coordTaxi = new Coordonnees(space.getLocation(this).getX(), space.getLocation(this).getY());
				double distance = clientsPossibles.get(i).getCoordonnees().getDistance(coordTaxi);
				distCalc.add(distance); //on créée une liste de la distance du client à chaque client
			}
			choseClient(distCalc);
		}
	}

	public void choseClient(ArrayList<Double> distCalc) {
		/* on prend le i de la distance la plus petite suivie on compare la
		 * distance de ce i avec la distance recue par les autres si calc<recu
		 * on y va on annonce aux autre la distance que l'on doit faire return
		 * si calc>recu on suit plus ce client là on relance choseClient()
		 * return */

		//1) find the i of the nearest client
		double min = 10000;
		int i = -1;
		for (int j = 0; j < distCalc.size(); j++) {
			if (distCalc.get(j) < min && clientSuivi.get(j)) {
				min = distCalc.get(j);
				i = j;
			}
		}

		if (i != -1) //if no client exists, error
		{
			//2) compare the distance to the client @ i with the distance received by other taxis
			if (min <= minDistReceived.get(i)) //if the calculating taxi is closer than the other one
			{ //move and announce his distance
				String message = /* (j+1) */clientsPossibles.get(i).getIDclient() + "_" + min; //we send the message id_distance
				//System.out.println(message);
				setPreparedMessage(message);
				watched++; //on envoie le message
				moveTo(clientsPossibles.get(i));

				return;
			}
			else { //stop following this client, start choseClient() again
				clientSuivi.set(i, false);
				choseClient(distCalc);
				return;
			}
		}

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

	@Watch(watcheeClassName = "test.Customer", watcheeFieldNames = "shout", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void getCustomer(Customer customer) {
		if (!clientsPossibles.contains(customer)) //si on ne le stocke pas deja
		{
			clientsPossibles.add(customer); //on le met en mémoire du taxi
			clientSuivi.add(true);
			minDistReceived.add(10000.0); //on commence avec une très grande valeur pour ne pas perturber les autres
		}
	}

	@Watch(watcheeClassName = "test.Customer", watcheeFieldNames = "quit", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void removeCustomer(Customer customer) {
		if (clientsPossibles.contains(customer)) //si on ne le stocke pas deja
		{
			for (int i = 0; i < clientsPossibles.size(); i++) {
				if (clientsPossibles.get(i).equals(customer)) {
					clientsPossibles.remove(i); //on le met en mémoire du taxi
					clientSuivi.remove(i);
					minDistReceived.remove(i); //on commence avec une très grande valeur pour ne pas perturber les autres
				}
			}
		}
	}

	public void moveTo(Customer cust) {
		Coordonnees coordTaxi = new Coordonnees(space.getLocation(this).getX(), space.getLocation(this).getY());
		double distance = cust.getCoordonnees().getDistance(coordTaxi); //on calcule la distance par rapport au client
		//System.out.println("Distance taxi-client pour le déplacement vers le client : "+ distance);
		//System.out.println("distance : " + distance);
		//if (free) {
		if (distance > 0.5) {
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
			cust.happyClient(); //we tell the client he's happy
			context.remove(cust);

			//flush lists? 
			eraseMemory();
		}
		//}
	}

	public void moveTo(Coordonnees coordonnes) {
		Coordonnees coordTaxi = new Coordonnees(space.getLocation(this).getX(), space.getLocation(this).getY());
		double distance = coordonnes.getDistance(coordTaxi); //on calcule la distance par rapport au client

		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint = new NdPoint(coordonnes.getX(), coordonnes.getY());
		//System.out.println(otherPoint.toString());

		double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
		//déplacemement
		space.moveByVector(this, 0.1, angle, 0); //on se déplace dans l'espace
		grid.moveTo(this, (int) space.getLocation(this).getX(), (int) space.getLocation(this).getY()); //on recopie ce déplacement dans la grille

		//check if the ride is over
		distance = space.getDistance(myPoint, otherPoint);
		if (distance < 0.5)
			free = true;
		//System.out.println("else | distance=" + distance + " | free=" + free);
	}

	@Override
	public void implement() {

	}

	public boolean isFree() {
		return this.free;
	}

	public boolean hasBabySeat() {
		return this.babySeat;
	}
	
	public void eraseMemory()
	{
		clientsPossibles = new ArrayList<Customer>();
		clientSuivi = new ArrayList<Boolean>();
		minDistReceived = new ArrayList<Double>();
	}
}
