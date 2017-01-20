package test;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

/* ==========================================================================
 * 																			*
 * Nom de la classe : Taxi													*
 * 																			*
 * Classe qui permet de repr�senter les taxis.								*
 * G�re leur d�placements quand ils sont pleins et vides, les messages		*
 * qu'ils s'envoient et leur gestion de ces messages, ainsi que la gestion	*
 * des messages envoy�s par les clients voulant se faire prendre.		 	*
 * 																			*
 ===========================================================================*/

public class Taxi extends Agent {

	// Liste pour stocker les clients possibles
	protected ArrayList<Customer> clientsPossibles;

	// Liste qui stocke les clients suivis
	protected ArrayList<Boolean> clientSuivi;

	// Liste pour choisir les clients en fonction de leur distance
	protected ArrayList<Double> minDistReceived;

	protected boolean watched;
	protected String preparedMessage;

	// Coordonnees de destinations lorsqu'un client est mont�
	protected Coordonnees coordDestination;

	// Coordonnees de destinations lorsqu'on attent un client
	protected Coordonnees coordAttente;

	// La duree entre 2 changements de directions quand le taxi attend
	protected int dureeChangementDirection;

	// Booleen pour savoir si le taxi esst occupe ou non 
	protected boolean free;

	// Booleen pour savoir si le taxi dispose d'un siege bebe
	protected boolean babySeat;

	/*--------------GETTERS ET SETTERS-----------------*/
	public String getPreparedMessage() {
		return preparedMessage;
	}

	public void setPreparedMessage(String preparedMessage) {
		this.preparedMessage = preparedMessage;
	}

	public boolean isFree() {
		return this.free;
	}

	public boolean hasBabySeat() {
		return this.babySeat;
	}

	/*--------------CONSTRUCTEUR-----------------*/
	public Taxi(Grid<Agent> grid, ContinuousSpace<Agent> space, boolean babySeat) {
		super(grid, space);
		clientsPossibles = new ArrayList<Customer>(); //cette liste repr�sente tous les clients dont le taxi � entendu parler.
		clientSuivi = new ArrayList<Boolean>(); //cette liste permet de savoir si le client est toujours suivi, c'est � dire si aucun autre taxi n'a annonc� �tre plus proche de lui
		minDistReceived = new ArrayList<Double>(); //cette liste permet de suive la distance minimale re�ue entre les autres taxis et les diff�rents clients, pour �viter que deux taxis aillent voir le m�me client
		//on met le nombre de base tr�s haut pour que la d�tection ne soit pas fauss�e
		watched = true;
		preparedMessage = "";
		coordDestination = null;
		free = true; // Au commencement, le taxi est libre
		this.babySeat = babySeat;
		this.coordAttente = null;
		dureeChangementDirection = 150; //la dur�e entre deux changements de direction pour se balader
	}

	/*--------------FONCTIONS-----------------*/

	/* ==============================================================================
	 * Nom de la fonction : compute() 												*
	 * 																				* 
	 * Entr�e : aucune 																*
	 * Sortie : aucune																*
	 * 																				*
	 * Cette fonction est lanc�e � chaque tick pour chaque taxi. Elle permet de		*
	 * savoir ce qu'il va faire : s'il se dirige vers un client ou s'il se			*
	 * balade en attendant un client qu'il pourra prendre en charge					*
	 * =============================================================================*/
	public void compute() {
		if (!isFree()) {
			moveTo(coordDestination); //si le taxi est occup�, il se contente d'aller vers sa destination.
		}
		else { //s'il est vide, il calcule sa distance avec tous les clients et d�cide de se diriger vers le plus proche.
			ArrayList<Double> distCalc = new ArrayList<Double>();
			for (int i = 0; i < clientsPossibles.size(); i++) {
				Coordonnees coordTaxi = new Coordonnees(space.getLocation(this).getX(), space.getLocation(this).getY());
				if (!(!babySeat && clientsPossibles.get(i).hasBaby())) { // Si le taxi a un si�ge b�b� ou que le client n'a pas de b�b� on l'ajoute � la liste des clients possibles
					//le but est d'�viter que les taxis sans si�ge b�b� suivent les clients qui en ont besoin d'un.
					double distance = clientsPossibles.get(i).getCoordonnees().getDistance(coordTaxi);
					distCalc.add(distance); //on cr��e une liste de la distance du taxi � chaque client
				}
			}

			choseClient(distCalc);
		}
	}

	/* ==============================================================================
	 * Nom de la fonction : choseClient()											*
	 * 																				*
	 * Entr�e : une liste de double	repr�sentant les distances entre le taxi et 	*
	 * tous les clients int�ressants  												*
	 * Sortie : aucune																*
	 *																				*
	 * Cette fonction est lanc�e par compute() et permet de prendre les d�cisions : *
	 * vers quel client ou quel point al�atoire se diriger. Il s'agit d'une fonction*
	 * s�par�e de compute() car elle est r�cursive : si le client le plus proche 	*
	 * n'est  pas satisfaisant, on passe au suivant. 								*
	 * =============================================================================*/
	public void choseClient(ArrayList<Double> distCalc) {
		double min = 10000;
		int i = -1;
		for (int j = 0; j < distCalc.size(); j++) {
			if (distCalc.get(j) < min && clientSuivi.get(j)) { //on extrait l'indice du client le plus proche qui est toujours suivi
				min = distCalc.get(j);
				i = j;
			}
		}

		if (i != -1) //si on � bien trouv� un client satisfaisant
		{
			if (min <= minDistReceived.get(i)) //si aucun autre taxi n'a annonc� etre plus proche
			{
				String message = clientsPossibles.get(i).getIDclient() + "_" + min;
				setPreparedMessage(message);
				watched = !watched; //on annonce aux autres taxis : le client vers lequel on se dirige ainsi que la distance nous s�parant du client
				moveTo(clientsPossibles.get(i)); //on se dirige un peu vers le client.
				return;
			}
			else { //si un taxi � annonc� �tre plus proche,
				clientSuivi.set(i, false); //on ne suit plus le client
				choseClient(distCalc); //et on recommence.
				return;
			}
		}
		else //si le taxi ne trouve pas de client
		{
			//il se dirige vers un point al�atoire pendant 150 ticks ou jusqu'a ce qu'il soit � moins de .2 de ce point, � ce moment il d�cide d'un autre point.
			if (coordAttente != null) {
				Coordonnees coordTaxi = new Coordonnees(space.getLocation(this).getX(), space.getLocation(this).getY());
				double distance = coordAttente.getDistance(coordTaxi);
				if (dureeChangementDirection > 0 && distance > .2) {
					moveTo(coordAttente);
					dureeChangementDirection--;
				}
				else {
					coordAttente = null;
				}
			}
			else { // On met a jour le point aleatoire
				dureeChangementDirection = 75;
				float x = (float) (Math.random() * 48) + 1;
				float y = (float) (Math.random() * 48) + 1;
				this.coordAttente = new Coordonnees(x, y);
				eraseMemory(); // On vide la memoire
				moveTo(coordAttente); // Le taxi se deplace vers les nouvelles coordonnees d'attentes 
			}
		}

	}

	/* ==============================================================================
	 * Nom de la fonction : changeDistanceReceived()								*
	 * 																				*
	 * Entr�e : le taxi qui vient d'annoncer un message  							*
	 * Sortie : aucune																*
	 *																				*
	 * Cette fonction est lanc�e quand un autre taxi annonce un message (quand il 	*
	 * incr�mente sa variable watched), le taxi qui r�cup�re le message va alors 	*
	 * r�cup�rer les informations qu'il contient et mettre ses * donn�es � jour sur *
	 * la distance qui s�pare le taxi annoncant et le client * dont il parle 		*
	 * =============================================================================*/
	@Watch(watcheeClassName = "test.Taxi", watcheeFieldNames = "watched", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void changeDistanceReceived(Taxi taxi) {
		String[] info = taxi.getPreparedMessage().split("_");
		int id_client = Integer.parseInt(info[0]); //on r�cup�re l'ID du client dont on parle
		double dist = Double.parseDouble(info[1]); //on r�cup�re la distance entre le taxi annoncant et le client 
		for (int i = 0; i < clientsPossibles.size(); i++) { //on parcourt la liste des clients que le taxi recevant connait
			if (clientsPossibles.get(i).getIDclient() == id_client) {
				if (dist < minDistReceived.get(i)) { //s'il y a une correspondance et cette nouvelle distance est plus petite que celle que le taxi recevant avait, il remplace.
					minDistReceived.set(i, dist);
				}
			}
		}
	}


	/* ==============================================================================
	 * Nom de la fonction : getCustomer()											*
	 * 																				*
	 * Entr�e : le client qui annonce sa pr�sence  									*
	 * Sortie : aucune																*
	 *																				*
	 * Cette fonction est lanc�e quand un client annonce sa pr�sence (quand il 		*
	 * incr�mente sa variable shout), le taxi qui r�cup�re le message va alors 		*
	 * r�cup�rer l'instance du client, et s'il ne le contient pas d�j�, l'ajoute 	*
	 * � sa liste de clients potentiels												*
	 * =============================================================================*/
	@Watch(watcheeClassName = "test.Customer", watcheeFieldNames = "shout", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void getCustomer(Customer customer) {
		if (!clientsPossibles.contains(customer)) {
			clientsPossibles.add(customer);
			clientSuivi.add(true);
			minDistReceived.add(10000.0); //very high number to be sure any calculated/received distance will be lower
		}
	}

	/* ==============================================================================
	 * Nom de la fonction : removeCustomer()										*
	 * 																				*
	 * Entr�e : le client qui annonce son d�part  									*
	 * Sortie : aucune																*
	 *																				*
	 * Cette fonction est lanc�e quand un client annonce son d�part (quand il 		*
	 * change sa variable quit), le taxi qui r�cup�re le message va alors r�cup�rer	*
	 * l'instance du client, et s'il le contient toujours, va le supprimer de ses 	*
	 * listes.																		*
	 * =============================================================================*/
	@Watch(watcheeClassName = "test.Customer", watcheeFieldNames = "quit", whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void removeCustomer(Customer customer) {
		if (clientsPossibles.contains(customer)) {
			for (int i = 0; i < clientsPossibles.size(); i++) {
				if (clientsPossibles.get(i).equals(customer)) {
					clientsPossibles.remove(i);
					clientSuivi.remove(i);
					minDistReceived.remove(i);
				}
			}
		}
	}

	/* ==============================================================================
	 * Nom de la fonction : moveTo()												*
	 * 																				*
	 * Entr�e : le client vers lequel on se dirige									*
	 * Sortie : aucune																*
	 *																				*
	 * Cette fonction permet de se diriger vers un client, et une fois qu'on est 	*
	 * arriv� � au cleint, elle permet d'annoncer que le client a bien �t� pris 	*
	 * en charge.																	*
	 * =============================================================================*/
	public void moveTo(Customer cust) {

		Coordonnees coordTaxi = new Coordonnees(space.getLocation(this).getX(), space.getLocation(this).getY());
		double distance = cust.getCoordonnees().getDistance(coordTaxi); //on calcule la distance par rapport au client

		if (distance > 0.2) {
			free = true;
			//si on est encore loin, on se dirige vers la destination
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = space.getLocation(cust);
			double angle = 0D;
			if (cust != null && otherPoint != null) {
				angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
				//d�placemement
				space.moveByVector(this, 0.1, angle, 0); //on se d�place dans l'espace
				grid.moveTo(this, (int) space.getLocation(this).getX(), (int) space.getLocation(this).getY()); //on recopie ce d�placement dans la grille	
			}
		}
		else { //si on est arriv� au client
			free = false; //le taxi est maintenant occup�
			coordAttente = null; //il n'a plus besoin d'aller vers un point al�atoire
			coordDestination = cust.getdestination();
			Context<?> context = ContextUtils.getContext(this);
			cust.happyClient(); //on annonce au client qu'il est bien pris en charge, pour qu'il le transmette a sa source d'origine
			context.remove(cust); //on supprime le client

			//on efface la m�moire du taxi par s�curit�
			eraseMemory();
		}
	}

	/* ==============================================================================
	 * Nom de la fonction : moveTo()												*
	 * 																				*
	 * Entr�e : les coordonn�es vers lesquelles on se dirige						*
	 * Sortie : aucune																*
	 *																				*
	 * Cette fonction permet de se diriger vers un point, symbolis� par un objet 	*
	 * Coordonnes. Une fois qu'on est arriv�s a destination, le taxi se met en mode	*
	 *  libre et peut donc commencer � chercher un client.							*
	 * =============================================================================*/
	public void moveTo(Coordonnees coordonnes) {
		Coordonnees coordTaxi = new Coordonnees(space.getLocation(this).getX(), space.getLocation(this).getY());
		double distance = coordonnes.getDistance(coordTaxi); //on calcule la distance de la destination par rapport au taxi

		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint = new NdPoint(coordonnes.getX(), coordonnes.getY());

		double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
		//d�placemement
		space.moveByVector(this, 0.1, angle, 0); //on se d�place dans l'espace
		grid.moveTo(this, (int) space.getLocation(this).getX(), (int) space.getLocation(this).getY()); //on recopie ce d�placement dans la grille

		//on regarde si on est arriv�s � destination
		distance = space.getDistance(myPoint, otherPoint);
		if (distance< 0.5) {
			free = true;
		}
	}
 
	/* ==============================================================================
	 * Nom de la fonction : eraseMemory()											*
	 * 																				*
	 * Entr�e : aucune																*
	 * Sortie : aucune																*
	 *																				*
	 * Cette fonction efface toutes les listes servant de m�moire au taxi.			*
	 * =============================================================================*/
	public void eraseMemory() {
		clientsPossibles = new ArrayList<Customer>();
		clientSuivi = new ArrayList<Boolean>();
		minDistReceived = new ArrayList<Double>();
	}
}
