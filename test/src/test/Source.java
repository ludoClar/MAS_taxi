package test;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class Source extends Agent {
	protected Coordonnees coordonnees;
	int i = 0;
	int step = 0;
	int start = 0;
	
	public void setStep(int step) {
		this.step = step;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public Source(Grid<Agent> grid, ContinuousSpace<Agent> space) {
		super(grid, space);

	}

	public void compute() {

	}

	@ScheduledMethod(start = 1, interval = 200, priority = 2)
	public void implement() {
		//System.out.println("In clients créés : ");
		Customer a = new Customer(grid, space);
		int satisfaction = (int) ((Math.random() * 50) + 50);
		a.setIDclient(start + i * step);
		a.setCoordonnees(this.coordonnees);
		a.setSatisfaction(satisfaction);
		//System.out.println(a.getCoordonnees());
		//System.out.println("In clients créés : ");
		Context context = ContextUtils.getContext(this);
		context.add(a);
		space.moveTo(a, coordonnees.getX(), coordonnees.getY());
		i++;
	}

	public void setCoordonnees(Coordonnees coordonnees) {
		this.coordonnees = coordonnees;
	}
}
