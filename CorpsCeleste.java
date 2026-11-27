package tp1;

import java.util.ArrayList;
import java.util.List;

public class CorpsCeleste {
	private String nom;
	private boolean atmosphere;
	private int carte ;
	private List<Zone> zones = new ArrayList<>();

	public CorpsCeleste(String nom, boolean atmosphere) {
        this.nom = nom;
        this.atmosphere = atmosphere;
    }

	public boolean aAtmosphere() {
		return atmosphere;
	}

	public synchronized void ajouterZone(Zone zone) {
        zones.add(zone);
    }

    public synchronized List<Zone> getZones() {
        return new ArrayList<>(zones);
    }
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
}
