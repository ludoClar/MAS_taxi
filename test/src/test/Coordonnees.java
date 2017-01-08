package test;

/* ==========================================================================
 * 																			*
 * Nom de la classe : Coordonnees											*
 * 																			*
 * Classe qui permet de représenter dans un objet un point dans l'espace	*
 * représentée par un x et un y.											*
 * Permet de calculer la distance etnre ce point et un autre, et de 		*
 * transformer le point en String.										 	*
 * 																			*
 ===========================================================================*/

public class Coordonnees {
        private double x;
        private double y;


        /*--------------GETTERS ET SETTERS-----------------*/
        public double getX() {
                return x;
        }

        public void setX(double x) {
                this.x = x;
        }

        public double getY() {
                return y;
        }

        public void setY(double y) {
                this.y = y;
        }

        public String toString() {
                return "(" + x + "," + y + ")";
        }
        
        public double getDistance(Coordonnees otherCoordonees) {
            return Math.sqrt(Math.pow(otherCoordonees.getX() - this.x, 2) + Math.pow(otherCoordonees.getY() - this.y, 2));
        }

        /*--------------CONSTRUCTEUR-----------------*/
        public Coordonnees(double x, double y) {
            super();
            this.x = x;
            this.y = y;
        }

}

