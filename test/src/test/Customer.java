//package test;
//
//import java.util.Random;
//
//import repast.simphony.engine.schedule.ScheduledMethod;
//import repast.simphony.space.continuous.ContinuousSpace;
//import repast.simphony.space.continuous.NdPoint;
//
//public class Customer {
//	protected ContinuousSpace<Customer> space;
//	int lastCalc;
//	NdPoint dest;
//	int neighbours;
//
//	public Customer(ContinuousSpace<Customer> space) {
//		this.space = space;
//		lastCalc = 0;
//		dest = new NdPoint(0, 0);
//		neighbours = 0;
//	}
//
//	public int getNeighbours() {
//		return neighbours;
//	}
//
//	// move the car
//	@ScheduledMethod(start = 1, interval = 30, priority = 2)
//	public void compute() {
//		boolean newCustomer = (new Random().nextInt() % 2 == 0 ? true : false);
//		if (newCustomer) {
//			Customer = new Customer();
//		}
//	}
//
//	// create complementary type of agent
//	@ScheduledMethod(start = 1, interval = 1, priority = 1)
//	public void implement() {
//	}
//
//}
