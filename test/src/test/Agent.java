package test;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

/* ==========================================================================
 * 																			*
 * Nom de la classe : Agent													*
 * 																			*
 * Super Classe dont les classes Source, Taxi et Customer sont d�riv�es		*
 * permet la cr�ation de la grille et de l'espace continu.					*
 * 																			*
 ===========================================================================*/

public abstract class Agent {
	protected ContinuousSpace<Agent> space;
	protected Grid<Agent> grid; //la grille servira � simplifier le nombre de calculs servant a savoir si un taxi est proche ou pas
	protected int lastCalc;
	protected NdPoint dest;
	protected int neighboursTaxi;
	protected int neighboursClients;

	/*--------------CONSTRUCTEUR-----------------*/
	public Agent(Grid<Agent> grid, ContinuousSpace<Agent> space) {
		this.space = space;
		this.grid = grid;
		//free = true;
		lastCalc = 0;
		dest = new NdPoint(0, 0);
		neighboursTaxi = 0;
		neighboursClients = 0;
	}

	/*--------------FONCTIONS-----------------*/
	@ScheduledMethod(start = 1, interval = 1, priority = 2) //pour tous ceux qui sont d�riv�s de Agent, on lancera compute() � chaque tick
	public abstract void compute();
}
