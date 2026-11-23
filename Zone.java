package tp1;

public class Zone Comparable <Zone> {
	private String nom;
	private Position position ;
	private double interetScientifique ;
	private TypeTerrain type;
	private int TempsZone;
	private List<Echantillon> echantillons;
	private Set<TypeVehicule> vehiculesAyantExplore;


	public Zone(String nom, Position position, TypeTerrain type,double interetScientifique) {
        if (interetScientifique < 0 || interetScientifique > 10) {
            throw new IllegalArgumentException("L'intérêt scientifique doit être entre 0 et 10" );
        }
	    this.nom = nom;
	    this.position=position;
		this.type=type ;
		this.interetScientifique=interetScientifique;
		this.echantillons=Collections.synchronizedList(new ArrayList<>()) ;
		this.vehiculesAyantExplore = new HashSet<>(); 
		
	}
	 public String getNom() {
        return nom;
    }

    public Position getPosition() {
        return position;
    }

    public TypeTerrain getTypeTerrain() {
        return type;
    }

    public double getInteretScientifique() {
        return interetScientifique;
    }

	public void ajouterEchantillon(Echantillon e) {
        echantillons.add(e);
    }

    public List<Echantillon> getEchantillons() {
        return echantillons;
    }
	public synchronized boolean peutEtreExploree(TypeVehicule type) {
        return !vehiculesAyantExplore.contains(type);
    }

    public synchronized boolean commencerExploration(TypeVehicule type) {
        if (vehiculesAyantExplore.contains(type)) {
            return false;
        }
		@Override 
		public int compareTo(Zone T){
			int resultat =Integer.compare(this.position.getY(),T.position.getY());
		    if (resulat!=0){
				return resultat ;
			}
		    return Integer.compare(this.position.getX(),T.position.getX());
		
	}
}
		
			


	
	

