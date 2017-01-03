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

	@ScheduledMethod(start = 50, interval = 200, priority = 2)
	public void implement() {
		boolean baby = (new Random().nextInt()) % 100 + 1 <= pourcentageBaby ? true : false;
		System.out.println("In clients cr��s : ");
		Customer a = new Customer(grid, space, baby);
		int satisfaction = (int) ((Math.random() * 50) + 50);
		a.setIDclient(start + i * step);
		a.setCoordonnees(this.coordonnees);
		a.setSatisfaction(satisfaction);
		System.out.println(a.getCoordonnees());
		//System.out.println("In clients cr��s : ");
		Context context = ContextUtils.getContext(this);
		context.add(a);
		space.moveTo(a, coordonnees.getX(), coordonnees.getY());
		i++;
	}

	public void setCoordonnees(Coordonnees coordonnees) {
		this.coordonnees = coordonnees;
	}
}
