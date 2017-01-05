package test;

import java.util.Random;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

public class ContextCreator implements ContextBuilder<Agent> {

	Context<Agent> context;

	@Override
	public Context build(Context<Agent> context) {
		int height = 50;//= RunEnvironment.getInstance().getParameters().getInteger("spaceHeight");
		int width = 50; //RunEnvironment.getInstance().getParameters().getInteger("spaceWidth");
		int nbTaxis = RunEnvironment.getInstance().getParameters().getInteger("nbTaxis");
		int nbSources = RunEnvironment.getInstance().getParameters().getInteger("nbClients");
		int pourcentageBabySeat = RunEnvironment.getInstance().getParameters().getInteger("pBabySeat");
		int pourcentageBaby = RunEnvironment.getInstance().getParameters().getInteger("pBaby");
		int satisfactionMin = RunEnvironment.getInstance().getParameters().getInteger("satisfactionMin");
		int satisfactionStep = RunEnvironment.getInstance().getParameters().getInteger("satisfactionStep");
		Schedule schedule = new Schedule();

		/*--------------GRID FACTORY CREATION-----------------*/
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Agent> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Agent>(new WrapAroundBorders(), new SimpleGridAdder<Agent>(), false, 50, 50));

		/*-------------- CONTINUOUS SPACE FACTORY CREATION-----------------*/
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Agent> space = spaceFactory.createContinuousSpace("space", context,
				new RandomCartesianAdder<Agent>(), new repast.simphony.space.continuous.StrictBorders(), 50, 50);

		/*--------------TAXIS CREATION-----------------*/
		for (int i = 0; i < nbTaxis; i++)
		{
			boolean babySeat = (new Random().nextInt()) % 100 + 1 <= pourcentageBabySeat ? true : false;
			//taxi starts at a random place
			float xCont = (float) (Math.random() * width);
			float yCont = (float) (Math.random() * height);
			Agent a = new Taxi(grid, space, babySeat);
			context.add(a);
			space.moveTo(a, xCont, yCont);

			//put the taxi on the grid
			int xGrid = (int) xCont;
			int yGrid = (int) yCont;
			grid.moveTo(a, xGrid, yGrid);
		}

		/*--------------SOURCES CREATION-----------------*/
		for (int i = 0; i < nbSources; i++) //fait apparaitre clients au d�but
		{
			float xCont = (float) (Math.random() * width);
			float yCont = (float) (Math.random() * height);
			Coordonnees coord = new Coordonnees(xCont, yCont);
			Source a = new Source(grid, space, pourcentageBaby, satisfactionMin, satisfactionStep);
			a.setCoordonnees(coord);
			a.setStep(nbSources);
			a.setStart(i);
			context.add(a);
			space.moveTo(a, xCont, yCont);

			int xGrid = (int) xCont;
			int yGrid = (int) yCont;
			grid.moveTo(a, xGrid, yGrid);
		}

		return context;
	}

}
