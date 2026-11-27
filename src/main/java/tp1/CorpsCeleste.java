package tp1;

import java.util.ArrayList;
import java.util.List;

public class CorpsCeleste {
	private String nom;
	private boolean atmosphere;
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
	public synchronized double tauxExploration() {
	    if (zones.isEmpty()) {
	        return 0;
	    }

	    int nbExplorees = 0;

	    for (Zone z : zones) {
	        if (z.estExploree()) {
	            nbExplorees++;
	        }
	    }

	    return (double) nbExplorees / zones.size() * 100; // retourner le taux dexploration du corps celeste 
	}
	
}
