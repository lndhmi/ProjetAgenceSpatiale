package tp1;

import java.util.Random;

public class Drone extends Engin implements Deplacer, Cartographe {

	public Drone(int id, String nom, Position pos) {
		super(id, nom, pos);
	}

	@Override
	public TypeVehicule getType() {
		return TypeVehicule.DRONE;
	}

	@Override
	public void seDeplacer(Position destination) {
		if (destination == null) {
			throw new IllegalArgumentException("position indisponible");
		}
		setposition(destination);
	}

	@Override
	public void Cartographier(Zone zone) {
		//fait par mission
	}

	@Override
	public Echantillon explorer(Zone zone, Random random) {
	    if (!zone.commencerExploration(getType())) {
	        return null; // déjà explorée par un drone
	    }
	    seDeplacer(zone.getPosition());
	    Cartographier(zone);
	    return null;
	}

}
