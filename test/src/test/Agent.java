package test;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

/* ==========================================================================
 * 																			*
 * Nom de la classe : Agent													*
 * 																			*
 * Super Classe dont les classes Source, Taxi et Customer sont dérivées		*
 * permet la création de la grille et de l'espace continu.					*
 * 																			*
 ===========================================================================*/

public abstract class Agent {
	protected ContinuousSpace<Agent> space;
	protected Grid<Agent> grid; //la grille servira à simplifier le nombre de calculs servant a savoir si un taxi est proche ou pas
	protected Coordonnees coordPosition;

	/*--------------CONSTRUCTEUR-----------------*/
	public Agent(Grid<Agent> grid, ContinuousSpace<Agent> space) {
		this.space = space;
		this.grid = grid;
	}

	/*--------------FONCTIONS-----------------*/
	@ScheduledMethod(start = 1, interval = 1, priority = 2) //pour tous ceux qui sont dérivés de Agent, on lancera compute() à chaque tick
	public abstract void compute();
}
