package test;

import java.awt.Color;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;

/* ==========================================================================
 * 																			*
 * Nom de la classe : AgentStyle2D											*
 * 																			*
 * Classe qui permet de changer la couleur des taxis en fonction			*
 * de leur présence ou non de siège bébé ou encore s'ils sont occupés.		*
 * 																			*
 ===========================================================================*/

public class AgentStyle2D extends DefaultStyleOGL2D {
	@Override
	public Color getColor(Object o) {
		if (o instanceof Taxi) {
			if (((Taxi) o).isFree())
				if (((Taxi) o).hasBabySeat())
					return Color.BLUE; //bleu si le taxi à un siège bébé et libre
				else
					return Color.GREEN; //vert s'il est libre sans siège bébé
			else
				return Color.RED; //rouge s'il est occupé, sans regarder la présence d'un siège bébé
		}
		else {
			return Color.BLUE; //bleu par défaut
		}
	}
}