package test;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

public class ContextCreator implements ContextBuilder<Taxi> {

	@Override
	public Context build(Context<Taxi> context) {
		int height = 50;//= RunEnvironment.getInstance().getParameters().getInteger("spaceHeight");
		int width = 50; //RunEnvironment.getInstance().getParameters().getInteger("spaceWidth");
		int nbTaxis = 20;
		int nbClients = 1;
		Schedule schedule = new Schedule();
		
		//grid factory
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Taxi> grid = gridFactory.createGrid("grid", context,
		new GridBuilderParameters<Taxi>(new WrapAroundBorders(),
		new SimpleGridAdder<Taxi>(), false, 50, 50));
		
		//space factory
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Taxi> space = spaceFactory.createContinuousSpace("space", context,
				new RandomCartesianAdder<Taxi>(), new repast.simphony.space.continuous.WrapAroundBorders(), 50, 50);
		
		//placement initial des taxis
		for (int i = 0; i < nbTaxis; i++) // Taxis plac�es de mani�re random
		{	
			//on tire un endroit al�atoire et on place le taxi dans l'espace continu
			float xCont = (float) (Math.random() * width);
			float yCont = (float) (Math.random() * height);
			Taxi a = new FreeTaxi(grid,space);
			context.add(a);
			space.moveTo(a, xCont, yCont);
			
			//on transmet le taxi dans l'espace discret (la grille)
			int xGrid = (int) xCont;
			int yGrid = (int) yCont;
			grid.moveTo(a, xGrid, yGrid);
		}
		
		/*for (int i = 0; i< nbClients;i++) //fait apparaitre clients au d�but
		{
			float xCont = (float) (Math.random() * width);
			float yCont = (float) (Math.random() * height);
			Customer a = new Customer(grid,space);
			context.add(a);
			space.moveTo(a, xCont, yCont);
			
			//on transmet le taxi dans l'espace discret (la grille)
			int xGrid = (int) xCont;
			int yGrid = (int) yCont;
			grid.moveTo(a, xGrid, yGrid);
		}*/
		
	
		//apparition des clients

		//double ticknumber = (Math.random()+1) * 5000 * i ;
		ScheduleParameters scheduleParam  =  ScheduleParameters.createRepeating(0, 3000);
		schedule.createAction(scheduleParam, this, spawnCustomer()/* TODO : on doit ici mettre la fonction permettant de d�finir l'apparition des clients*/);

		
		return context;
	}


}
