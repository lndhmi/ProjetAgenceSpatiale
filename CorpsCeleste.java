package tp1;

import java.util.List;
import java.util.ArrayList;

public class CorpsCeleste {
	private String nom;
	private boolean atmosphere;
	private int carte ;
	private List<Zone> zones = new ArrayList<>();

	public CorpsCeleste(String nom) {
        this.nom = nom;
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

	public boolean isAtmosphere() {
		return atmosphere;
	}

	public void setAtmosphere(boolean atmosphere) {
		this.atmosphere = atmosphere;
	}

	public int getCarte() {
		return carte;
	}

	public void setCarte(int carte) {
		this.carte = carte;
	}
}
