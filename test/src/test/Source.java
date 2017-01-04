package test;

import java.util.Random;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class Source extends Agent {
	protected Coordonnees coordonnees;
	protected int pourcentageBaby;
	protected int nextClient;
	int i = 0;
	int step = 0;
	int start = 0;

	public void setStep(int step) {
		this.step = step;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public Source(Grid<Agent> grid, ContinuousSpace<Agent> space, int pourcentageBaby) {
		super(grid, space);
		this.pourcentageBaby = pourcentageBaby;
	}

	public void compute() {

	}

	@ScheduledMethod(start = 1, interval = 1, priority = 2)
	public void implement() {
		if (nextClient > 0)
			nextClient--;
		else {
			nextClient = new Random().nextInt() % 10 + 350;
			boolean baby = (new Random().nextInt()) % 100 + 1 <= pourcentageBaby ? true : false;
			Customer a = new Customer(grid, space, baby);
			int satisfaction = (int) ((Math.random() * 250) + 50);
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
	}

	public void setCoordonnees(Coordonnees coordonnees) {
		this.coordonnees = coordonnees;
	}
}
