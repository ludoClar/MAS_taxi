package test;

public class Coordonnees {
        double x;
        double y;

        public Coordonnees(double x, double y) {
                super();
                this.x = x;
                this.y = y;
        }

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



}

