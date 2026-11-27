package tp1;

public class Echantillon {
	private final TypeEchantillon type;
	private final double masse;

	public Echantillon(double masse, TypeEchantillon type) {
		if (masse <= 0) {
			throw new IllegalArgumentException("La masse doit être positive");
		}
		if (type == null) {
			throw new IllegalArgumentException("Le type est obligatoire");
		}
		this.masse = masse;
		this.type = type;
	}

	public double getMasse() {
		return masse;
	}

	public TypeEchantillon getType() {
		return type;
	}

	@Override
	public String toString() {
		return type + " (" + String.format("%.2f", masse) + " kg)";
	}
}
