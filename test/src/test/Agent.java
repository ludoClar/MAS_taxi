package test;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

public abstract class Taxi {
	protected ContinuousSpace<Taxi> space;
	protected Grid<Taxi> grid; //la grille servira à simplifier le nombre de calculs servant a savoir si un taxi est proche ou pas
	protected boolean free;
	protected int lastCalc;
	protected NdPoint dest;
	protected int neighbours;

	public Taxi(Grid<Taxi> grid,ContinuousSpace<Taxi> space) {
		this.space = space;
		this.grid = grid;
		free = true;
		lastCalc = 0;
		dest = new NdPoint(0, 0);
		neighbours = 0;
	}

	@ScheduledMethod(start = 1, interval = 1, priority = 2)
	public abstract void compute();

	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public abstract void implement();

}
