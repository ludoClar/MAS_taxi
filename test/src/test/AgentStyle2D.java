package test;

import java.awt.Color;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;

/* ==========================================================================
 * 																			*
 * Nom de la classe : AgentStyle2D											*
 * 																			*
 * Classe qui permet de changer la couleur des taxis en fonction			*
 * de leur pr�sence ou non de si�ge b�b� ou encore s'ils sont occup�s.		*
 * 																			*
 ===========================================================================*/

public class AgentStyle2D extends DefaultStyleOGL2D {
	@Override
	public Color getColor(Object o) {
		if (o instanceof Taxi) {
			if (((Taxi) o).isFree())
				if (((Taxi) o).hasBabySeat())
					return Color.BLUE; //bleu si le taxi � un si�ge b�b� et libre
				else
					return Color.GREEN; //vert s'il est libre sans si�ge b�b�
			else
				return Color.RED; //rouge s'il est occup�, sans regarder la pr�sence d'un si�ge b�b�
		}
		else {
			return Color.BLUE; //bleu par d�faut
		}
	}
}