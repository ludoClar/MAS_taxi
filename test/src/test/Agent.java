package test;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

public abstract class Agent {
	protected ContinuousSpace<Agent> space;
	protected Grid<Agent> grid; //la grille servira à simplifier le nombre de calculs servant a savoir si un taxi est proche ou pas
	//protected boolean free;
	protected int lastCalc;
	protected NdPoint dest;
	protected int neighboursTaxi;
	protected int neighboursClients;
	protected final int SEUIL_SATISFACTION_MAX = RunEnvironment.getInstance().getParameters()
			.getInteger("seuilSatisfaction");

	public Agent(Grid<Agent> grid, ContinuousSpace<Agent> space) {
		this.space = space;
		this.grid = grid;
		//free = true;
		lastCalc = 0;
		dest = new NdPoint(0, 0);
		neighboursTaxi = 0;
		neighboursClients = 0;
	}

	@ScheduledMethod(start = 1, interval = 1, priority = 2)
	public abstract void compute();

	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public abstract void implement();

}
