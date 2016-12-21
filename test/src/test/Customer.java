package test;

import java.util.Random;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

public class Customer extends Agent{
	protected ContinuousSpace<Customer> space;
	protected Grid<Customer> grid;
	NdPoint dest;
	int neighbours;
	int IDclient;
	Coordonnees coordonnees;
	Coordonnees destination;

	/*public Customer(Grid<Customer> grid,ContinuousSpace<Customer> space) {
		this.space = space;
		this.grid = grid;
		lastCalc = 0;
		dest = new NdPoint(0, 0);
		neighbours = 0;
	}*/
	
	public int getIDclient() {
		return IDclient;
	}

	public Coordonnees getCoordonnees() {
		return coordonnees;
	}

	public void setDestination(Coordonnees destination) {
		this.destination = destination;
	}
	
	public Coordonnees getdestination() {
		return destination;
	}

	public void setCoordonnees(Coordonnees coordonnees) {
		this.coordonnees = coordonnees;
	}

	public void setIDclient(int iDclient) {
		IDclient = iDclient;
	}

	public Customer(Grid<Agent> grid,ContinuousSpace<Agent> space) {
		super(grid,space);
		//calculate the destination
		float xCont = (float) (Math.random() * 50);
		float yCont = (float) (Math.random() * 50);
		Coordonnees coord = new Coordonnees(xCont, yCont);
		setDestination(coord);
	}

	public int getNeighbours() {
		return neighbours;
	}

	/*// move the car
	@ScheduledMethod(start = 1, interval = 30, priority = 2)
	public void compute() {
		boolean newCustomer = (new Random().nextInt() % 2 == 0 ? true : false);
		if (newCustomer) {
			Customer = new Customer();
		}
	}*/

	// create complementary type of agent
	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void implement() {
	}

	@Override
	public void compute() {
		// TODO Auto-generated method stub
		
	}

}
