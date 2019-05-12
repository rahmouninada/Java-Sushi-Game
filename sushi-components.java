/* package in source folder for Sushi-Game containing components of game
* Classes: Avocado.java, AvocadoPortion.java, BluePlate.java, Crab.java, CrabPortion.java, Eel.java, EelPortion.java, 
* GoldPlate.java, GreenPlate.java, IngredientImpl.java, IngredientPortionImpl.java, Nigiri.java, PlateImpl.java,
* PlatePriceException.java, RedPlate.java, Rice.java, RicePortion.java, Roll.java, Salmon.java, SalmonPortion.java, 
* Sashimi.java, Seaweed.java, SeaweedPortion.java, Shrimp.java, ShrimpPortion.java, Tuna.java, TunaPortion.java, 
* Interfaces: Ingredient_Interface.java, Ingredient_Portion_Interface.java, Plate_Interface.java, Sushi_Interface.java
*/

package src.sushiGame.sushi;

/* INGREDIENT INTERFACE */
public interface Ingredient {
	String getName();
	double getCaloriesPerDollar();
	int getCaloriesPerOunce();
	double getPricePerOunce();
	boolean equals(Ingredient other);
	boolean getIsVegetarian();
	boolean getIsRice();
	boolean getIsShellfish();
}

/* INGREDIENT PORTION INTERFACE */
public interface IngredientPortion {
	Ingredient getIngredient();
	String getName();
	double getAmount();
	double getCalories();
	double getCost();
	boolean getIsVegetarian();
	boolean getIsRice();
	boolean getIsShellfish();
	IngredientPortion combine(IngredientPortion other);
}

/* PLATE INTERFACE */
import sushigame.model.Chef;
public interface Plate {
	 public enum Color {RED, GREEN, BLUE, GOLD}

     Sushi getContents();
     double getPrice();
     Plate.Color getColor();
     double getProfit();
     Chef getChef();     
}

/* SUSHI INTERFACE */
public interface Sushi {

	String getName();
	IngredientPortion[] getIngredients();
	int getCalories();
	double getCost();
	boolean getHasRice();
	boolean getHasShellfish();
	boolean getIsVegetarian();
}

/* AVOCADO CLASS */
public class Avocado extends IngredientImpl {
	public Avocado() {
		super("avocado", 0.22, 45, true, false, false);
	}
}

/* AVOCADO PORTION CLASS */
public class AvocadoPortion extends IngredientPortionImpl {

	private static final Ingredient AVOCADO = new Avocado();
	
	public AvocadoPortion(double amount) {
		super(amount, AVOCADO);
	}
	
	@Override
	public IngredientPortion combine(IngredientPortion other) {
		if (other == null) {
			return this;
		}
		if (!other.getIngredient().equals(AVOCADO)) {
			throw new RuntimeException("Can not combine portions of different ingredients");
		}
		return new AvocadoPortion(other.getAmount()+this.getAmount());
	}
}

/* BLUE PLATE CLASS */
import sushigame.model.Chef;

public class BluePlate extends PlateImpl {

	public BluePlate(Chef chef, Sushi s) throws PlatePriceException {
		super(chef, s, 4.0, Plate.Color.BLUE);
	}
}

/* CRAB CLASS */
public class Crab extends IngredientImpl {
	public Crab() {
		super("crab", 0.75, 36, false, false, true);
	}
}

/* CRAB PORTION CLASS */
public class CrabPortion extends IngredientPortionImpl {

	private static final Ingredient CRAB = new Crab();
	
	public CrabPortion(double amount) {
		super(amount, CRAB);
	}
	
	@Override
	public IngredientPortion combine(IngredientPortion other) {
		if (other == null) {
			return this;
		}
		if (!other.getIngredient().equals(CRAB)) {
			throw new RuntimeException("Can not combine portions of different ingredients");
		}
		return new CrabPortion(other.getAmount()+this.getAmount());
	}
}

/* EEL CLASS */
public class Eel extends IngredientImpl {
	public Eel() {
		super("eel", 2.18, 84, false, false, false);
	}
}

/* EEL PORTION CLASS */
public class EelPortion extends IngredientPortionImpl {

	private static final Ingredient EEL = new Eel();
	
	public EelPortion(double amount) {
		super(amount, EEL);
	}
	
	@Override
	public IngredientPortion combine(IngredientPortion other) {
		if (other == null) {
			return this;
		}
		if (!other.getIngredient().equals(EEL)) {
			throw new RuntimeException("Can not combine portions of different ingredients");
		}
		return new EelPortion(other.getAmount()+this.getAmount());
	}
}

/* GOLD PLATE CLASS */
import sushigame.model.Chef;
public class GoldPlate extends PlateImpl {

	public GoldPlate(Chef chef, Sushi s, double price) throws PlatePriceException {
		super(chef, s, check_price(price), Plate.Color.GOLD);
	}
	
	private static double check_price(double price) {
		if (price < 5.0) {
			throw new IllegalArgumentException();
		}
		return price;
	}
}

/* GREEN PLATE CLASS */
import sushigame.model.Chef;
public class GreenPlate extends PlateImpl {

	public GreenPlate(Chef chef, Sushi s) throws PlatePriceException {
		super(chef, s, 2.0, Plate.Color.GREEN);
	}
}

/* INGREDIENT IMPLEMENTATION CLASS */
abstract public class IngredientImpl implements Ingredient {

	private String name;
	private double price;
	private int calories;
	private boolean is_vegetarian;
	private boolean is_rice;
	private boolean is_shellfish;
	
	protected IngredientImpl(String name, double price, int calories, 
			boolean is_vegetarian, boolean is_rice, boolean is_shellfish) {
		this.name = name;
		this.price = price;
		this.calories = calories;
		this.is_vegetarian = is_vegetarian;
		this.is_rice = is_rice;
		this.is_shellfish = is_shellfish;		
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public double getCaloriesPerDollar() {
		return calories/price;
	}

	@Override
	public int getCaloriesPerOunce() {
		return calories;
	}

	@Override
	public double getPricePerOunce() {
		return price;
	}

	@Override
	public boolean equals(Ingredient other) {
		if (other == null) {
			return false;
		}
		return ((other == this) ||
				(other.getName().equals(getName()) &&
				 (other.getCaloriesPerOunce() == getCaloriesPerOunce()) &&
				 (Math.abs(other.getPricePerOunce()-getPricePerOunce()) < 0.01) &&
				 (other.getIsVegetarian() == getIsVegetarian()) &&
				 (other.getIsRice() == getIsRice()) &&
				 (other.getIsShellfish() == getIsShellfish())));
	}

	@Override
	public boolean getIsVegetarian() {
		return is_vegetarian;
	}

	@Override
	public boolean getIsRice() {
		return is_rice;
	}

	@Override
	public boolean getIsShellfish() {
		return is_shellfish;
	}
}


/* INGREDIENT PORTION IMPLEMENTATION CLASS */
abstract public class IngredientPortionImpl implements IngredientPortion {

	private double amount;
	private Ingredient ingredient;

	protected IngredientPortionImpl(double amount, Ingredient ingredient) {
		if (amount <= 0.0) {
			throw new RuntimeException("Amount of ingredient portion must be greater than 0.0");
		}
		
		this.amount = amount;
		this.ingredient = ingredient;
	}
	
	@Override
	public Ingredient getIngredient() {
		return ingredient;
	}

	@Override
	public String getName() {
		return ingredient.getName();
	}

	@Override
	public double getAmount() {
		return amount;
	}

	@Override
	public double getCalories() {
		return amount * ingredient.getCaloriesPerOunce();
	}

	@Override
	public double getCost() {
		return amount * ingredient.getPricePerOunce();
	}

	@Override
	public boolean getIsVegetarian() {
		return ingredient.getIsVegetarian();
	}

	@Override
	public boolean getIsRice() {
		return ingredient.getIsRice();
	}

	@Override
	public boolean getIsShellfish() {
		return ingredient.getIsShellfish();
	}

	@Override
	abstract public IngredientPortion combine(IngredientPortion other);
}

/* NIGIRI CLASS */
public class Nigiri implements Sushi {

	public enum NigiriType {TUNA, SALMON, EEL, CRAB, SHRIMP}

	private static double NIGIRI_PORTION_AMOUNT = 0.75;
	private static double RICE_PORTION_AMOUNT = 0.5;
	
	private IngredientPortion seafood;
	private IngredientPortion rice;
	
	public Nigiri(NigiriType type) {
		rice = new RicePortion(RICE_PORTION_AMOUNT);

		switch(type) {
		case TUNA:
			seafood = new TunaPortion(NIGIRI_PORTION_AMOUNT);
			break;
		case SALMON:
			seafood = new SalmonPortion(NIGIRI_PORTION_AMOUNT);
			break;
		case EEL:
			seafood = new EelPortion(NIGIRI_PORTION_AMOUNT);
			break;
		case CRAB:
			seafood = new CrabPortion(NIGIRI_PORTION_AMOUNT);
			break;
		case SHRIMP:
			seafood = new ShrimpPortion(NIGIRI_PORTION_AMOUNT);
			break;			
		}
	}
	
	@Override
	public String getName() {
		return seafood.getName() + " nigiri";
	}

	@Override
	public IngredientPortion[] getIngredients() {
		return new IngredientPortion[] {seafood, rice};
	}

	@Override
	public int getCalories() {
		return (int) (seafood.getCalories() + rice.getCalories() + 0.5);
	}

	@Override
	public double getCost() {
		return ((int) ((seafood.getCost() + rice.getCost()) * 100.0 + 0.5)) / 100.0;
	}

	@Override
	public boolean getHasRice() {
		return true;
	}

	@Override
	public boolean getHasShellfish() {
		return seafood.getIsShellfish();
	}

	@Override
	public boolean getIsVegetarian() {
		return false;
	}
}

/* PLATE IMPLEMENTATION CLASS */
import sushigame.model.Chef;

abstract public class PlateImpl implements Plate {

	private Sushi contents;
	private double price;
	private Plate.Color color;
	private Chef chef;
		
	public PlateImpl(Chef chef, Sushi s, double price, Plate.Color color) throws PlatePriceException {
		if (s == null) {
			throw new IllegalArgumentException();
		}

		if (chef == null) {
			throw new IllegalArgumentException();
		}
		
		if (s.getCost() > price) {
			throw new PlatePriceException(this, s);
		}
		
		this.price = price;
		this.color = color;
		this.chef = chef;
		contents = s;
	}

	@Override
	public Sushi getContents() {
		return contents;
	}

	@Override
	public double getPrice() {
		return price;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public double getProfit() {
		return getPrice() - contents.getCost();
	}
	
	@Override
	public Chef getChef() {
		return chef;
	}
}

/* PLATE PRICE EXCEPTION CLASS */
public class PlatePriceException extends Exception {
	private Plate plate;
	private Sushi sushi;
	
	public PlatePriceException(Plate p, Sushi s) {
		super("Plate price is too low for sushi placed on it.");

		this.plate = p;
		this.sushi = s;
	}
}

/* RED PLATE CLASS */
import sushigame.model.Chef;
public class RedPlate extends PlateImpl {
	public RedPlate(Chef chef, Sushi s) throws PlatePriceException {
		super(chef, s, 1.0, Plate.Color.RED);
	}
}

/* RICE CLASS */
public class Rice extends IngredientImpl {
	public Rice() {
		super("rice", 0.12, 37, true, true, false);
	}
}

/* RICE PORTION CLASS */
public class RicePortion extends IngredientPortionImpl {
	private static final Ingredient RICE = new Rice();
	
	public RicePortion(double amount) {
		super(amount, RICE);
	}
	
	@Override
	public IngredientPortion combine(IngredientPortion other) {
		if (other == null) {
			return this;
		}
		if (!other.getIngredient().equals(RICE)) {
			throw new RuntimeException("Can not combine portions of different ingredients");
		}
		return new RicePortion(other.getAmount()+this.getAmount());
	}
}

/* ROLL CLASS */
public class Roll implements Sushi {
	private String name;
	private IngredientPortion[] roll_ingredients;

	public Roll(String name, IngredientPortion[] roll_ingredients) {
		if (name == null) {
			throw new RuntimeException("Roll name is null");
		}
		this.name = name;
		if (roll_ingredients == null) {
			throw new RuntimeException("Roll ingredients is null");
		}
		for (int i=0; i<roll_ingredients.length; i++) {
			if (roll_ingredients[i] == null) {
				throw new RuntimeException("At least one roll ingredient is null");
			}
		}
		this.roll_ingredients = roll_ingredients.clone();
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public IngredientPortion[] getIngredients() {
		return roll_ingredients.clone();
	}

	@Override
	public int getCalories() {
		double calorie_sum = 0.0;
		for (int i=0; i<roll_ingredients.length; i++) {
			calorie_sum += roll_ingredients[i].getCalories();
		}
		
		return (int) (calorie_sum + 0.5);
	}

	@Override
	public double getCost() {
		double cost_sum = 0.0;
		for (int i=0; i<roll_ingredients.length; i++) {
			cost_sum += roll_ingredients[i].getCost();
		}
		
		return ((int) (cost_sum * 100.0 + 0.5))/100.0;
	}

	@Override
	public boolean getHasRice() {
		for (int i=0; i<roll_ingredients.length; i++) {
			if (roll_ingredients[i].getIsRice()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean getHasShellfish() {
		for (int i=0; i<roll_ingredients.length; i++) {
			if (roll_ingredients[i].getIsShellfish()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean getIsVegetarian() {
		for (int i=0; i<roll_ingredients.length; i++) {
			if (!roll_ingredients[i].getIsVegetarian()) {
				return false;
			}
		}
		return true;
	}
}

/* SALMON CLASS */
public class Salmon extends IngredientImpl {
	public Salmon() {
		super("salmon", 0.72, 56, false, false, false);
	}
}

/* SALMON PORTION CLASS */
public class SalmonPortion extends IngredientPortionImpl {

	private static final Ingredient SALMON = new Salmon();
	
	public SalmonPortion(double amount) {
		super(amount, SALMON);
	}
	
	@Override
	public IngredientPortion combine(IngredientPortion other) {
		if (other == null) {
			return this;
		}
		if (!other.getIngredient().equals(SALMON)) {
			throw new RuntimeException("Can not combine portions of different ingredients");
		}
		return new SalmonPortion(other.getAmount()+this.getAmount());
	}
}

/* SASHIMI CLASS */
public class Sashimi implements Sushi {

	public enum SashimiType {TUNA, SALMON, EEL, CRAB, SHRIMP}

	private static double SASHIMI_PORTION_AMOUNT = 0.75;
	
	private IngredientPortion seafood;
	
	public Sashimi(SashimiType type) {
		switch(type) {
		case TUNA:
			seafood = new TunaPortion(SASHIMI_PORTION_AMOUNT);
			break;
		case SALMON:
			seafood = new SalmonPortion(SASHIMI_PORTION_AMOUNT);
			break;
		case EEL:
			seafood = new EelPortion(SASHIMI_PORTION_AMOUNT);
			break;
		case CRAB:
			seafood = new CrabPortion(SASHIMI_PORTION_AMOUNT);
			break;
		case SHRIMP:
			seafood = new ShrimpPortion(SASHIMI_PORTION_AMOUNT);
			break;			
		}
	}
	
	@Override
	public String getName() {
		return seafood.getName() + " sashimi";
	}

	@Override
	public IngredientPortion[] getIngredients() {
		return new IngredientPortion[] {seafood};
	}

	@Override
	public int getCalories() {
		return (int) (seafood.getCalories() + 0.5);
	}

	@Override
	public double getCost() {
		return ((int) (seafood.getCost() * 100.0 + 0.5)) / 100.0;
	}

	@Override
	public boolean getHasRice() {
		return false;
	}

	@Override
	public boolean getHasShellfish() {
		return seafood.getIsShellfish();
	}

	@Override
	public boolean getIsVegetarian() {
		return false;
	}
}

/* SEAWEED CLASS */
public class Seaweed extends IngredientImpl {
	public Seaweed() {
		super("seaweed", 2.95, 113, true, false, false);
	}
}

/* SEAWEED PORTION CLASS */
public class SeaweedPortion extends IngredientPortionImpl {

	private static final Ingredient SEAWEED = new Seaweed();
	
	public SeaweedPortion(double amount) {
		super(amount, SEAWEED);
	}
	
	@Override
	public IngredientPortion combine(IngredientPortion other) {
		if (other == null) {
			return this;
		}
		if (!other.getIngredient().equals(SEAWEED)) {
			throw new RuntimeException("Can not combine portions of different ingredients");
		}
		return new SeaweedPortion(other.getAmount()+this.getAmount());
	}
}

/* SHRIMP CLASS */
public class Shrimp extends IngredientImpl {
	public Shrimp() {
		super("shrimp", 0.55, 39, false, false, true);
	}
}

/* SHRIMP PORTION CLASS */
public class ShrimpPortion extends IngredientPortionImpl {

	private static final Ingredient SHRIMP = new Shrimp();
	
	public ShrimpPortion(double amount) {
		super(amount, SHRIMP);
	}
	
	@Override
	public IngredientPortion combine(IngredientPortion other) {
		if (other == null) {
			return this;
		}
		if (!other.getIngredient().equals(SHRIMP)) {
			throw new RuntimeException("Can not combine portions of different ingredients");
		}
		return new ShrimpPortion(other.getAmount()+this.getAmount());
	}
}

/* TUNA CLASS */
public class Tuna extends IngredientImpl {
	public Tuna() {
		super("tuna", 1.77, 48, false, false, false);
	}
}

/* TUNA PORTION CLASS */
public class TunaPortion extends IngredientPortionImpl {
	private static final Ingredient TUNA = new Tuna();
	
	public TunaPortion(double amount) {
		super(amount, TUNA);
	}
	
	@Override
	public IngredientPortion combine(IngredientPortion other) {
		if (other == null) {
			return this;
		}
		if (!other.getIngredient().equals(TUNA)) {
			throw new RuntimeException("Can not combine portions of different ingredients");
		}
		return new TunaPortion(other.getAmount()+this.getAmount());
	}
}
