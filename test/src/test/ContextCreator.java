package test;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
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
		int nbTaxis = 100;
		
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
		for (int i = 0; i < nbTaxis; i++) //1000 Taxis placées de manière random
		{	
			//on tire un endroit aléatoire et on place le taxi dans l'espace continu
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
		
		
		return context;
	}
}
