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
		int nbClients = RunEnvironment.getInstance().getParameters().getInteger("nbClients");
		int pourcentageBabySeat = RunEnvironment.getInstance().getParameters().getInteger("pBabySeat");
		int pourcentageBaby = RunEnvironment.getInstance().getParameters().getInteger("pBaby");
		Schedule schedule = new Schedule();

		//grid factory
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Agent> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Agent>(new WrapAroundBorders(), new SimpleGridAdder<Agent>(), false, 50, 50));

		//space factory
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Agent> space = spaceFactory.createContinuousSpace("space", context,
				new RandomCartesianAdder<Agent>(), new repast.simphony.space.continuous.StrictBorders(), 50, 50);

		//placement initial des taxis
		for (int i = 0; i < nbTaxis; i++) // Taxis placées de manière random
		{
			boolean babySeat = (new Random().nextInt()) % 100 + 1 <= pourcentageBabySeat ? true : false;
			//on tire un endroit aléatoire et on place le taxi dans l'espace continu
			float xCont = (float) (Math.random() * width);
			float yCont = (float) (Math.random() * height);
			Agent a = new Taxi(grid, space, babySeat);
			context.add(a);
			space.moveTo(a, xCont, yCont);

			//on transmet le taxi dans l'espace discret (la grille)
			int xGrid = (int) xCont;
			int yGrid = (int) yCont;
			grid.moveTo(a, xGrid, yGrid);
		}

		for (int i = 0; i < nbClients; i++) //fait apparaitre clients au début
		{
			float xCont = (float) (Math.random() * width);
			float yCont = (float) (Math.random() * height);
			Coordonnees coord = new Coordonnees(xCont, yCont);
			Source a = new Source(grid, space, pourcentageBaby);
			//Customer a = new Customer(grid, space);
			//a.setIDclient(i);
			a.setCoordonnees(coord);
			a.setStep(nbClients);
			a.setStart(i);
			context.add(a);
			space.moveTo(a, xCont, yCont);

			//on transmet le taxi dans l'espace discret (la grille)
			int xGrid = (int) xCont;
			int yGrid = (int) yCont;
			grid.moveTo(a, xGrid, yGrid);
		}

		//apparition des clients

		//		//double ticknumber = (Math.random()+1) * 5000 * i ;
		//		ScheduleParameters scheduleParam  =  ScheduleParameters.createRepeating(0, 3000);
		//		schedule.createAction(scheduleParam, this, spawnCustomer()/* TODO : on doit ici mettre la fonction permettant de définir l'apparition des clients*/);

		return context;
	}

}
