package tp1;

import java.util.Random;

public class Rover extends Engin implements Deplacer , Preleve{
	
	public Rover (int id ,String nom , Position pos) {
		super (id,nom,pos);
	}
	
	@Override
	public void seDeplacer( Position destination ) {
		if (destination == null) {
			throw new IllegalArgumentException ("position indisponible ");
		}
		setposition(destination);
	}

	@Override
	public Echantillon prelever(Zone zone, Random random) {
		TypeEchantillon[] types = TypeEchantillon.values();
		TypeEchantillon type = types[random.nextInt(types.length)];
		double masse = 0.1 + random.nextDouble() * 5;
		return new Echantillon(masse, type);
	}

	@Override
	public Echantillon explorer(Zone zone, Random random) {
	    if (!zone.commencerExploration(getType())) {
	        return null; // déjà explorée par un rover
	    }
	    seDeplacer(zone.getPosition());
	    return prelever(zone, random);
	}

	@Override
	public TypeVehicule getType() {
		return TypeVehicule.ROVER;
	}
	




}
