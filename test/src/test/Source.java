package test;

import java.util.Random;

import repast.simphony.context.Context;
import repast.simphony.query.space.grid.MooreQuery;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

/* ==========================================================================
 * 																			*
 * Nom de la classe : Source												*
 * 																			*
 * Classe qui permet de représenter les sources de clients.					*
 * Régulèrement, fait apparaitre un client, annonce sa satisfaction			*
 * jusqu'ici, et tire un nombre de ticks aléatoires jusqu'au prochain		*
 * client créé.															 	*
 * 																			*
 ===========================================================================*/

public class Source extends Agent {

	// Stocke les coordonnees de la source
	protected Coordonnees coordonnees;

	// Pourcentage de clients crees par la source qui auront un bebe
	protected int pourcentageBaby;

	// Temps (en nombre de ticks) minimal pour satisfaire les clients
	protected int satisfactionMin = 0;

	// Marge de satisfaction -> Satisfaction max = satisfactionMin + satisfactionStep
	protected int satisfactionStep = 0;
	protected int nextClient;
	protected int frequenceApparitionClient = 0;

	// Id du client
	protected int idClient = 0;
	protected int step = 0;

	// Id de la source 
	protected int idSource = 0;

	// Nombre de clients satisfaits
	protected int happyClients = 0;

	// Nombre de clients insatisfaits
	protected int angryClients = 0;

	/*--------------GETTERS ET SETTERS-----------------*/
	public void setStep(int step) {
		this.step = step;
	}

	public Coordonnees getCoordonnees() {
		return coordonnees;
	}

	public void setStart(int start) {
		this.idSource = start;
	}

	public void setCoordonnees(Coordonnees coordonnees) {
		this.coordonnees = coordonnees;
	}

	public void happyClient() {
		happyClients++;
	};

	public void unhappyClient() {
		angryClients++;
	};

	public int getId() {
		return this.idSource;
	}

	// Retourne le ratio de la source
	public float getRatio() {
		if ((happyClients + angryClients) != 0)
			return (happyClients / (angryClients + happyClients));
		else
			return 0f;
	}

	/*--------------CONSTRUCTEUR-----------------*/
	public Source(Grid<Agent> grid, ContinuousSpace<Agent> space, int pourcentageBaby, int satisfactionMin,
			int satisfactionStep, int freqApparClients) {
		super(grid, space);
		this.pourcentageBaby = pourcentageBaby;
		this.satisfactionMin = satisfactionMin;
		this.satisfactionStep = satisfactionStep;
		this.frequenceApparitionClient = freqApparClients;
	}

	/*--------------FONCTIONS-----------------*/

	/* ==============================================================================
	 * Nom de la fonction : compute()												*
	 * 																				*
	 * Entrée : aucune																*
	 * Sortie : aucune																*
	 *																				*
	 * Cette fonction est lancée à chaque tick pour chaque source. Elle permet de 	*
	 * savoir ce qu'elle va faire : créer un client, annoncer sa  satisfaction ou	* 
	 * rien du tout.																*
	 * =============================================================================*/
	public void compute() {

		/*--------------ANNONCE DE LA SATISFACTION-----------------*/
		if (nextClient == 10) //on ne fait apparaitre le message que si le client est sur le point d'apparaitre.
		{
			float ratio = -1;
			int total = happyClients + angryClients;
			if (total != 0)
				ratio = (float) happyClients / total;
			//System.out.println("Ratio from the source " + idSource + ": " + ratio);
		}
		if (nextClient > 0) //compute() est lancée à chaque tick, mais on ne veut pas que un client apparaisse à chaque tick.
			nextClient--;
		else {

			/*--------------CREATION DU CLIENT-----------------*/
			nextClient = (new Random().nextInt() % 101) + frequenceApparitionClient;

			// On tire aleatoirement si le client a un bebe ou non
			boolean baby = (Math.abs(new Random().nextInt()) % 100) < pourcentageBaby ? true : false;

			Customer client = new Customer(grid, space, baby);
			int randSatisfaction = (int) ((Math.random() * satisfactionStep) + satisfactionMin);

			// On choisi aléatoirement une source de destination (sauf la notre)
			int sourceDest = idSource;
			while (sourceDest == idSource)
				sourceDest = Math.abs(new Random().nextInt() % step);

			// On initialise destination avec la source elle-meme
			Coordonnees destination = this.getCoordonnees();

			// On cherche les coordonnnees de la source choisie
			MooreQuery<Agent> query = new MooreQuery<Agent>(grid, this, 100, 100);
			for (Agent agent : query.query()) {
				if (agent instanceof Source && agent != this) //on observe chaque source
				{
					if (sourceDest == ((Source) agent).getId())
						destination = ((Source) agent).getCoordonnees();
				}
			}

			client.setIDclient(idSource + idClient * step);
			client.setDestination(destination);
			client.setCoordonnees(this.coordonnees);
			client.setSatisfaction(randSatisfaction);
			client.setOriginSource(this);

			// On ajoute le client au contexte
			@SuppressWarnings("unchecked")
			Context<Agent> context = ContextUtils.getContext(this);
			context.add(client);

			// On crée le client à la position donnée
			space.moveTo(client, coordonnees.getX(), coordonnees.getY());

			// On incremente l'id client (permet de ne pas avoir 2 clients avec le meme id)
			idClient++;
		}
	}
}
