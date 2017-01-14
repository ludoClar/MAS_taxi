package test;

import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

/* ==========================================================================
 * 																			*
 * Nom de la classe : Customer												*
 * 																			*
 * Classe qui permet de représenter les clients.							*
 * Gère la dégradation de leur satisfaction, quand ils prennent la décision	*
 * d'arreter d'attendre. Gère aussi la création et l'envoi des différents	*
 * messages à la destination des taxis (requête de prise en charge et 		*
 * annonce du départ du client).										 	*
 * 																			*
 ===========================================================================*/

public class Customer extends Agent {
	protected ContinuousSpace<Customer> space;
	protected Grid<Customer> grid;

	// boolean pour savoir si le taxi a un bebe ou pas
	protected boolean baby;
	protected NdPoint dest;
	protected int IDclient;
	protected Coordonnees coordPosition;
	protected Coordonnees coordDestination;

	// Entier pour conntaitre sa satisfaction
	protected int satisfaction = 0;

	// Lorsque le cleitn appelle les taxis
	protected int shout = 0;

	// Le client est rattaché a sa source d'origine
	protected Source originSource;

	// Booleen pour avertir aux taxis que le client part (car insatisfait)
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
		return coordPosition;
	}

	public void setDestination(Coordonnees destination) {
		this.coordDestination = destination;
	}

	public Coordonnees getdestination() {
		return coordDestination;
	}

	public void setCoordonnees(Coordonnees coordonnees) {
		this.coordPosition = coordonnees;
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

	/* =========================================================================
	 * = * Nom de la fonction : compute() * * Entrée : aucune * Sortie : aucune
	 * * * Cette fonction est lancée à chaque tick pour chaque client. Elle
	 * permet * de décider de ce qu'il va faire : s'il continue d'attendre parce
	 * qu'il a * encore de la patience, dans ce cas là il va demander d'etre
	 * pris en * charge. S'il n'a plus de patience, il annonce son départ,
	 * annonce à la * source qu'il n'est pas satisfait, puis disparaît. * *
	 * =========================================================================
	 * == */
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
