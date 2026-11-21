package tp1;

public class Echantillon {
	private TypeTerrain type;
	private float masse;

public Echantillon (float masse, TypeTerrain type ) {
	if (masse <= 0) {
            throw new IllegalArgumentException("La masse doit être positive");
        }
	this.masse=masse ;
	this.setType(type);
}


public float getMasse() {
	return masse;
}

public void setMasse(float masse) {
	this.masse = masse;
}

public TypeTerrain getType() {
	return type;
}

public void setType(TypeTerrain type) {
	this.type = type;
}
}
