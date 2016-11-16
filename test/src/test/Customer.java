package test;

import java.util.Random;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

public class Customer extends Taxi{
	protected ContinuousSpace<Customer> space;
	protected Grid<Customer> grid;
	int lastCalc;
	NdPoint dest;
	int neighbours;

	/*public Customer(Grid<Customer> grid,ContinuousSpace<Customer> space) {
		this.space = space;
		this.grid = grid;
		lastCalc = 0;
		dest = new NdPoint(0, 0);
		neighbours = 0;
	}*/
	
	public Customer(Grid<Taxi> grid,ContinuousSpace<Taxi> space) {
		super(grid,space);
		// TODO Auto-generated constructor stub
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
