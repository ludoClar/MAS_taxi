package test;

import java.awt.Color;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;

public class AgentStyle2D extends DefaultStyleOGL2D {
	@Override
	public Color getColor(Object o) {
		if (o instanceof Taxi) {
			if (((Taxi) o).isFree())
				return Color.GREEN;
			else
				return Color.RED;
		}
		else {
			return Color.BLUE;
		}
	}
}