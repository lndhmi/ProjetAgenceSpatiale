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
		// TODO Auto-generated method stub
		return null;
	}





}
