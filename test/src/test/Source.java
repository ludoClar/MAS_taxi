package test;

import java.util.Random;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.MooreQuery;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class Source extends Agent {
	protected Coordonnees coordonnees;
	protected int pourcentageBaby;
	protected int nextClient;
	protected int i = 0;
	protected int step = 0;
	protected int start = 0;
	protected int happyClients = 0;
	protected int angryClients = 0;
	protected int satisfactionMin = 0;
	protected int satisfactionStep = 0;

	public void setStep(int step) {
		this.step = step;
	}

	public Coordonnees getCoordonnees() {
		return coordonnees;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public Source(Grid<Agent> grid, ContinuousSpace<Agent> space, int pourcentageBaby, int satisfactionMin,
			int satisfactionStep) {
		super(grid, space);
		this.pourcentageBaby = pourcentageBaby;
		this.satisfactionMin = satisfactionMin;
		this.satisfactionStep = satisfactionStep;
	}

	public void compute() {
		if (nextClient == 10) //we only display the message when a client is about to spawn
		{
			float ratio = -1;
			int total = happyClients + angryClients;
			if (total != 0)
				ratio = (float) happyClients / total;
			System.out.println("Ratio from the source " + start + ": " + ratio);
		}
	}

	@ScheduledMethod(start = 1, interval = 1, priority = 2)
	public void implement() {
		if (nextClient > 0) //even if this function is started at each tick, we don't want a client to appear every tick
			nextClient--;
		else {
			nextClient = Math.abs(new Random().nextInt()) % 10 + 350;
			boolean baby = (Math.abs(new Random().nextInt())) % 100 + 1 <= pourcentageBaby ? true : false;
			Customer a = new Customer(grid, space, baby);
			int randSatisfaction = (int) ((Math.random() * satisfactionStep) + satisfactionMin);
			int sourceDest = (int) ((Math.random() * step));
			System.out.println("nb de sources checkée : " + sourceDest);
			Coordonnees destination = new Coordonnees(1, 1);
			System.out.println("Début de Moore");
			MooreQuery<Agent> query = new MooreQuery<Agent>(grid, this, 100, 100);
			for (Agent o : query.query())
				if (o instanceof Source && sourceDest != -1 && o != this) { //on observe chaque source
					System.out.println("Source detectée");
					if (sourceDest == 0) {
						System.out.println("Destination choisie");
						destination = ((Source) o).getCoordonnees();
						sourceDest = -1;
					}
					else
						sourceDest--;
				}
			a.setIDclient(start + i * step);
			a.setDestination(destination);
			a.setCoordonnees(this.coordonnees);
			a.setSatisfaction(randSatisfaction);
			a.setOriginSource(this);
			Context context = ContextUtils.getContext(this);
			context.add(a);
			space.moveTo(a, coordonnees.getX(), coordonnees.getY());
			i++;
		}
	}

	public void setCoordonnees(Coordonnees coordonnees) {
		this.coordonnees = coordonnees;
	}

	public void happyClient() {
		happyClients++;
	};

	public void unhappyClient() {
		angryClients++;
	};
}
