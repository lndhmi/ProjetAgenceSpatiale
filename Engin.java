package tp1;

import java.util.Random;
import java.util.List;
import java.util.Map;
import java.util.EnumMap;


public abstract class Engin {
	private int id;
	private String  nom;
	private  Position pos;
	private  EtatEngin etat ;
	private Mission miss;

public Engin(int id,String  nom,Position pos) {
	this.id=id;
	this.nom=nom;
	this.pos=pos;
	this.etat=EtatEngin.DISPONIBLE;
	this.miss= null;
}

public String getnom() {
	return nom ;
}
public int getid () {
	return id;
}
public Position getposition() {
	return pos;
}
public EtatEngin getetat() {
	return etat ;
}

public Mission getmission () {
	return miss;
}

public void setposition (Position pos) {
	this.pos=pos ;
}

public void affecterMisiion (Mission mission) {
	if(mission==null) {
		throw new IllegalArgumentException("mission Invalide");
	}
	if (etat!=EtatEngin.DISPONIBLE) {
		throw new IllegalStateException("l'engin n'est pas dipo");
	}
	miss=mission;
	etat=EtatEngin.EN_MISSION;
}

public void libererMission () {
	miss=null;
	if(etat==EtatEngin.EN_MISSION) {
		etat= EtatEngin.DISPONIBLE;
	}
}

public void TomberPanne() {
	etat=EtatEngin.EN_PANNE;;
	miss=null;
}
public abstract TypeVehicule getType();
//engin passe a letat en panne 
public void verifierPanne(Random random) {
    double probabilite = random.nextDouble();

    if (probabilite < 0.10) {
        TomberPanne();
    } else {
        etat = EtatEngin.DISPONIBLE;
    }
}

public void RemettreService () {
	if(etat!=EtatEngin.EN_PANNE) {
		throw new IllegalStateException("l'engin n'est pas en panne ");
	}
	etat= EtatEngin.DISPONIBLE;
}
public abstract Echantillon explorer(Zone zone, Random random);
}


//regrouper les engins selon leur etats 
public static Map<EtatEngin, List<Engin>> enginsParEtat(List<Engin> engins) {
    Map<EtatEngin, List<Engin>> resultat = new EnumMap<>(EtatEngin.class);

    for (Engin e : engins) {
        EtatEngin etat = e.getetat();

        if (!resultat.containsKey(etat)) {
            resultat.put(etat, new ArrayList<>());
        }

        resultat.get(etat).add(e);
    }

    return resultat;
}
