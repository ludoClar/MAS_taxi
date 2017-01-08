package test;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

/* ==========================================================================
 * 																			*
 * Nom de la classe : Customer												*
 * 																			*
 * Classe qui permet de repr�senter les clients.							*
 * G�re la d�gradation de leur satisfaction, quand ils prennent la d�cision	*
 * d'arreter d'attendre. G�re aussi la cr�ation et l'envoi des diff�rents	*
 * messages � la destination des taxis (requ�te de prise en charge et 		*
 * annonce du d�part du client).										 	*
 * 																			*
 ===========================================================================*/

public class Customer extends Agent {
	protected ContinuousSpace<Customer> space;
	protected Grid<Customer> grid;
	protected boolean baby;
	protected NdPoint dest;
	protected int IDclient;
	protected Coordonnees coordonnees;
	protected Coordonnees destination;
	protected int satisfaction = 0;
	protected int shout = 0;
	protected Source originSource;
	protected boolean quit = false;

	/*--------------GETTERS ET SETTERS-----------------*/
	public int getSatisfaction() {
		return satisfaction;
	}

	public void setSatisfaction(int satisfaction) {
		this.satisfaction = satisfaction;
	}

	public int getIDclient() {
		return IDclient;
	}

	public Coordonnees getCoordonnees() {
		return coordonnees;
	}

	public void setDestination(Coordonnees destination) {
		this.destination = destination;
	}

	public Coordonnees getdestination() {
		return destination;
	}

	public void setCoordonnees(Coordonnees coordonnees) {
		this.coordonnees = coordonnees;
	}

	public void setIDclient(int iDclient) {
		IDclient = iDclient;
	}

	public Source getOriginSource() {
		return originSource;
	}

	public void setOriginSource(Source originSource) {
		this.originSource = originSource;
	}

	/*--------------CONSTRUCTEUR-----------------*/
	public Customer(Grid<Agent> grid, ContinuousSpace<Agent> space, boolean baby) {
		super(grid, space);
		//find a random destination
		float xCont = (float) (Math.random() * 50);
		float yCont = (float) (Math.random() * 50);
		Coordonnees coord = new Coordonnees(xCont, yCont);
		setDestination(coord);
		this.baby = baby;
	}

	/*--------------FONCTIONS-----------------*/
	
	
	/* ==========================================================================
	 * 																			*
	 * Nom de la fonction : compute()											*
	 * 																			*
	 * Entr�e : aucune															*
	 * Sortie : aucune															*
	 * 																			*
	 * Cette fonction est lanc�e � chaque tick pour chaque client. Elle permet  *
	 * de d�cider de ce qu'il va faire : s'il continue d'attendre parce qu'il a	*
	 * encore de la patience, dans ce cas l� il va demander d'etre pris en 		*
	 * charge. S'il n'a plus de patience, il annonce son d�part, annonce � la 	*
	 * source qu'il n'est pas satisfait, puis dispara�t.						*
	 * 																			*
	 ===========================================================================*/
	@Override
	public void compute() {
		satisfaction--;
		if (satisfaction <= 0) {
			quit = true;
			originSource.unhappyClient();
			Context context = ContextUtils.getContext(this);
			context.remove(this);
		}
		else { //the client shout for a taxi
			shout++;
		}
	}

	public void happyClient() {
		originSource.happyClient();
	};

	public boolean hasBaby() {
		return this.baby;
	}

}
