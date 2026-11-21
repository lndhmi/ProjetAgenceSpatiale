package tp1;

import java.util.Random;

public class Drone extends Engin implements Deplacer,Cartographe {

	public Drone(int id, String nom, Position pos) {
		super(id, nom, pos);
	}

	@Override
	public void Cartographier(Zone zone) {
		
		
	}

	@Override
	public void seDeplacer(Position destination) {
		
		
	}

	@Override
	public Echantillon prelever(Zone zone, Random random) {
		
		return null;
	}

}
