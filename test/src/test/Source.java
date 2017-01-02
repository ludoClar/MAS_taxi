package test;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class Source extends Agent {
	protected Coordonnees coordonnees;
	int i = 0;

	public Source(Grid<Agent> grid, ContinuousSpace<Agent> space) {
		super(grid, space);

	}

	public void compute() {

	}

	@ScheduledMethod(start = 1, interval = 100, priority = 2)
	public void implement() {
		System.out.println("In clients créés : ");
		Customer a = new Customer(grid, space);
		a.setIDclient(i++);
		a.setCoordonnees(this.coordonnees);
		Context context = ContextUtils.getContext(this);
		context.add(a);
		space.moveTo(a, coordonnees.getX(), coordonnees.getY());
	}

	public void setCoordonnees(Coordonnees coordonnees) {
		this.coordonnees = coordonnees;
	}
}
