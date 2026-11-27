package tp1;

import java.util.Random;

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
public void RemettreService () {
	if(etat!=EtatEngin.EN_PANNE) {
		throw new IllegalStateException("l'engin n'est pas en panne ");
	}
	etat= EtatEngin.DISPONIBLE;
}
public abstract Echantillon explorer(Zone zone, Random random);
}

