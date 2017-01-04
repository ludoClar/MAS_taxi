package test;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class Customer extends Agent {
	protected ContinuousSpace<Customer> space;
	protected Grid<Customer> grid;
	protected boolean baby;
	NdPoint dest;
	int neighbours;
	int IDclient;
	Coordonnees coordonnees;
	Coordonnees destination;
	int satisfaction = 0;
	int shout = 0;
	Source originSource;
	boolean quit = false;


	/*--------------GETTERS AND SETTERS-----------------*/
	public int getSatisfaction() {
		return satisfaction;
	}

	public void setSatisfaction(int satisfaction) {
		this.satisfaction = satisfaction;
	}

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

	public Source getOriginSource() {
		return originSource;
	}

	public void setOriginSource(Source originSource) {
		this.originSource = originSource;
	}

	public Customer(Grid<Agent> grid, ContinuousSpace<Agent> space, boolean baby) {
		super(grid, space);
		//find a random destination
		float xCont = (float) (Math.random() * 50);
		float yCont = (float) (Math.random() * 50);
		Coordonnees coord = new Coordonnees(xCont, yCont);
		setDestination(coord);
		this.baby = baby;
	}

	public int getNeighbours() {
		return neighbours;
	}

	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void implement() {
	}

	@Override
	public void compute() {
		satisfaction--;
		if (satisfaction <= 0) {
			quit = true;
			originSource.unhappyClient();
			Context context = ContextUtils.getContext(this);
			context.remove(this);
		}
		else { //the client shout for a taxi
			shout++;
		}
	}
	
	public void happyClient(){originSource.happyClient();};

	public boolean hasBaby() {
		return this.baby;
	}

}
