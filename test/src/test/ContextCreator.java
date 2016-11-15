package test;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.RandomCartesianAdder;

public class ContextCreator implements ContextBuilder<Taxi> {

	@Override
	public Context build(Context<Taxi> context) {
		int height = 50;//= RunEnvironment.getInstance().getParameters().getInteger("spaceHeight");
		int width = 50; //RunEnvironment.getInstance().getParameters().getInteger("spaceWidth");
		//space factory
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Taxi> space = spaceFactory.createContinuousSpace("space", context,
				new RandomCartesianAdder<Taxi>(), new repast.simphony.space.continuous.WrapAroundBorders(), 50, 50);
		for (int i = 0; i < 10; i++) //1000 Taxis placées de manière random
		{
			float x = (float) (Math.random() * width);
			float y = (float) (Math.random() * height);
			Taxi a = new FreeTaxi(space);
			context.add(a);
			space.moveTo(a, x, y);
		}
		return context;
	}
}
