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
	Coordonnees attente;
	int recalc;
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
		this.attente = null;
		recalc = 150;
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
			if (min <= minDistReceived.get(i)) //if the calculating taxi is closer than the other one
			{ //move and announce his distance
				String message = clientsPossibles.get(i).getIDclient() + "_" + min;
				setPreparedMessage(message);
				watched++;
				moveTo(clientsPossibles.get(i));

				return;
			}
			else { //stop following this client, start choseClient() again
				clientSuivi.set(i, false);
				choseClient(distCalc);
				return;
			}
		}
		else //si le taxi ne trouve pas de client
		{
			if (attente!=null)
			{
				Coordonnees coordTaxi = new Coordonnees(space.getLocation(this).getX(), space.getLocation(this).getY());
				double distance = attente.getDistance(coordTaxi);
				if (recalc > 0 && distance > .5)
				{
					moveTo(attente);
					recalc --;
				}
				else
				{
					attente = null;
				}				
			}
			else
			{
				recalc = 150;
				float x = (float) (Math.random() * 48)+1;
				float y = (float) (Math.random() * 48)+1;	
				this.attente = new Coordonnees(x, y);
				moveTo(attente);
			}
		}

	}

	@Watch(watcheeClassName = "test.Taxi", watcheeFieldNames = "watched", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void changeDistanceReceived(Taxi taxi) {
		String[] info = taxi.getPreparedMessage().split("_");
		int id_client = Integer.parseInt(info[0]);
		double dist = Double.parseDouble(info[1]);
		for (int i = 0; i < clientsPossibles.size(); i++) {
			if (clientsPossibles.get(i).getIDclient() == id_client) {
				if (dist < minDistReceived.get(i))
				{
					minDistReceived.set(i, dist);
				}
			}
		}
	}

	@Watch(watcheeClassName = "test.Customer", watcheeFieldNames = "shout", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void getCustomer(Customer customer) {
		if (!clientsPossibles.contains(customer)) 
		{
			clientsPossibles.add(customer); 
			clientSuivi.add(true);
			minDistReceived.add(10000.0); //very high number to be sure any calculated/received distance will be lower
		}
	}

	@Watch(watcheeClassName = "test.Customer", watcheeFieldNames = "quit", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void removeCustomer(Customer customer) {
		if (clientsPossibles.contains(customer))
		{
			for (int i = 0; i < clientsPossibles.size(); i++) {
				if (clientsPossibles.get(i).equals(customer)) {
					clientsPossibles.remove(i); 
					clientSuivi.remove(i);
					minDistReceived.remove(i);
				}
			}
		}
	}

	public void moveTo(Customer cust) {
		
		Coordonnees coordTaxi = new Coordonnees(space.getLocation(this).getX(), space.getLocation(this).getY());
		double distance = cust.getCoordonnees().getDistance(coordTaxi); //on calcule la distance par rapport au client

		if (distance > 0.5) {
			//move to the customer
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = space.getLocation(cust);

			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
			//déplacemement
			space.moveByVector(this, 0.1, angle, 0); //on se déplace dans l'espace
			grid.moveTo(this, (int) space.getLocation(this).getX(), (int) space.getLocation(this).getY()); //on recopie ce déplacement dans la grille	
		}
		else {
			free = false;
			attente = null;
			destination = cust.getdestination();
			Context context = ContextUtils.getContext(this);
			cust.happyClient(); //we tell the client he's happy
			context.remove(cust);

			//flush lists 
			eraseMemory();
		}
	}

	public void moveTo(Coordonnees coordonnes) {
		Coordonnees coordTaxi = new Coordonnees(space.getLocation(this).getX(), space.getLocation(this).getY());
		double distance = coordonnes.getDistance(coordTaxi); //on calcule la distance par rapport au client

		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint = new NdPoint(coordonnes.getX(), coordonnes.getY());

		double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
		//déplacemement
		space.moveByVector(this, 0.1, angle, 0); //on se déplace dans l'espace
		grid.moveTo(this, (int) space.getLocation(this).getX(), (int) space.getLocation(this).getY()); //on recopie ce déplacement dans la grille

		//check if the ride is over
		distance = space.getDistance(myPoint, otherPoint);
		if (distance < 0.5)
			free = true;
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
