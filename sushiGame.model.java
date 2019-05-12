/* package in source folder for Sushi-Game containing model component of game
* Classes: BeltEvent.java, BeltFullException.java, BeltImpl.java, BeltPlateException.java, ChefImpl.java
* HistoricalPlateImpl.java, InsufficientBalanceException.java, PlateConsumedEvent.java, PlateEvent.java, PlatePlacedEvent.java,
* PlateSpoiledEvent.java, RandomCustomer.java, RotateEvent.java, SushiGameModel.java, TimedPlateImpl.java,
* Interfaces: Belt_Interface.java, BeltObserver_Interface.java, Chef_Interface.java, ChefsBelt_Interface.java,
* Customer_Interface.java, Historical_Interface.java, TimedPlate.java
*/

package src.sushiGame.game;

/* BELT INTERFACE */
import sushiGame.sushi.Plate;

public interface Belt_Interface {
	int getRotationCount();
	int getSize();
	void rotate();
	Plate getPlateAtPosition(int position);
	int getAgeOfPlateAtPosition(int position);
	int findPlate(Plate plate);
	void registerBeltObserver(BeltObserver o);
	void unregisterBeltObserver(BeltObserver o);
	Customer getCustomerAtPosition(int position);
}

/* BELT OBSERVER INTERFACE */
public interface BeltObserver_Interface {
	public void handleBeltEvent(BeltEvent e);
}

/* CHEF INTERFACE */
import sushiGame.sushi.Plate;

public interface Chef_Interface {
	String getName();
	void setName(String name);
	
	void makeAndPlacePlate(Plate plate, int position) 
			throws InsufficientBalanceException, BeltFullException, AlreadyPlacedThisRotationException;
		
	HistoricalPlate[] getPlateHistory(int max_history_length);
	HistoricalPlate[] getPlateHistory();
	
	double getBalance();
	
	boolean alreadyPlacedThisRotation();
}

/* CHEFS BELT INTERFACE */
import sushiGame.sushi.Plate;
interface ChefsBelt extends Belt {
	int setPlateNearestToPosition(Plate plate, int position) throws BeltFullException;
}

/* CUSTOMER INTERFACE */
import sushiGame.sushi.Plate;
public interface Customer {
	boolean consumesPlate(Plate p);
}

/* HISTORICAL INTERFACE */
import sushiGame.sushi.Plate;
public interface HistoricalPlate extends Plate {
	boolean wasSpoiled();
	Customer getConsumer();
}

/* TIMED PLATE INTERFACE */
import sushiGame.sushi.Plate;
public interface TimedPlate_Interface extends Plate {
	int getInceptDate();
	Plate getOriginal();
}

/* BELT EVENT CLASS */
abstract public class BeltEvent {
	public enum EventType {PLATE_PLACED, PLATE_CONSUMED, PLATE_SPOILED, ROTATE}

	private BeltEvent.EventType type;

	public BeltEvent(EventType type) {
		this.type = type;
	}
	
	public BeltEvent.EventType getType() {
		return type;
	}
}

/* BELT FULL EXCEPTION CLASS */
public class BeltFullException extends Exception {
	private Belt belt;

	public BeltFullException(Belt belt) {
		this.belt = belt;
	}
	
	public Belt getBelt() {
		return belt;
	}
}

/* BELT IMPLEMENTATION CLASS */
import java.util.ArrayList;
import java.util.List;
import sushiGame.sushi.Plate;
import sushiGame.sushi.Sushi;

class BeltImpl implements ChefsBelt {

	private TimedPlate[] belt;
	private int rotation_count;
	private Customer[] customers;
	private List<BeltObserver> belt_observers;
	
	public BeltImpl(int size) {
		if (size < 1) {
			throw new IllegalArgumentException("Belt size must be greater than zero.");
		}

		belt = new TimedPlate[size];
		customers = new Customer[size];
		rotation_count = 0;
		belt_observers = new ArrayList<BeltObserver>();
	}

	@Override
	public int getRotationCount() {
		return rotation_count;
	}

	@Override
	public int getSize() {
		return belt.length;
	}
	
	@Override
	public Customer getCustomerAtPosition(int position) {
		return customers[normalizePosition(position)];
	}

	@Override
	public int setPlateNearestToPosition(Plate plate, int position) throws BeltFullException {
		for (int i=0; i<getSize(); i++) {
			try {
				setPlateAtPosition(plate, position);
				return normalizePosition(position);
			} catch (BeltPlateException e) {
				position += 1;
			}
		}
		throw new BeltFullException(this);
	}


	@Override
	public void rotate() {
		TimedPlate last_plate = belt[getSize()-1];
		for (int i=getSize()-1; i>0; i--) {
			belt[i] = belt[i-1];
		}
		belt[0] = last_plate;
		rotation_count++;

		notifyBeltObservers(new RotateEvent());
		
		for (int i=0; i<getSize(); i++) {
			if (plateAtPositionIsSpoiled(i)) {
				Plate spoiled_plate = removePlateAtPosition(i);
				notifyBeltObservers(new PlateSpoiledEvent(spoiled_plate, i));
			}
		}
		
		for (int i=0; i<getSize(); i++) {
			if (customers[i] != null) {
				Plate plate = getPlateAtPosition(i);
				if (plate != null) {
					if (customers[i].consumesPlate(plate)) {
						removePlateAtPosition(i);
						notifyBeltObservers(new PlateConsumedEvent(plate, i));			
					}
				}
			}
		}
	}

	@Override
	public int getAgeOfPlateAtPosition(int position) {
		position = normalizePosition(position);
		if (belt[position] == null) {
			return -1;
		} else {
			return getRotationCount() - belt[position].getInceptDate();
		}
	}

	@Override
	public int findPlate(Plate plate) {
		if (plate == null) {
			return -1;
		}
		
		for (int i=0; i<getSize(); i++) {
			if (getPlateAtPosition(i) == plate) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void registerBeltObserver(BeltObserver o) {
		belt_observers.add(o);
	}
	
	@Override
	public void unregisterBeltObserver(BeltObserver o) {
		belt_observers.remove(o);
	}
	
	private void notifyBeltObservers(BeltEvent event) {
		for (BeltObserver o : belt_observers) {
			o.handleBeltEvent(event);
		}
	}

	@Override
	public Plate getPlateAtPosition(int position) {
		position = normalizePosition(position);
		if (belt[position] != null) {
			return belt[position].getOriginal();
		} else {
			return null;
		}
	}

	private Plate removePlateAtPosition(int position) {
		Plate plate = getPlateAtPosition(position);
		clearPlateAtPosition(position);
		return plate;
	}

	void setCustomerAtPosition(Customer c, int position) {
		customers[normalizePosition(position)] = c;
	}
	
	private void setPlateAtPosition(Plate plate, int position) throws BeltPlateException {
		position = normalizePosition(position);

		if (plate == null) {
			throw new IllegalArgumentException("Plate is null");
		}

		if (belt[position] != null) {
			throw new BeltPlateException(position, plate, this);
		}
		belt[position] = new TimedPlateImpl(plate, getRotationCount());
		notifyBeltObservers(new PlatePlacedEvent(plate, position));
	}

	private void clearPlateAtPosition(int position) {
		belt[normalizePosition(position)] = null;
	}
	
	private boolean plateAtPositionIsSpoiled(int pos) {
		pos = normalizePosition(pos);
		TimedPlate plate = belt[pos];
		
		if (plate == null) {
			return false;
		}
		
		Sushi sushi = plate.getContents();
		if (sushi == null) {
			return false;
		}
		
		int age = getAgeOfPlateAtPosition(pos);
		
		if (sushi.getIsVegetarian()) {
			return (age >= 3 * getSize());
		}
		
		if (!sushi.getHasShellfish()) {
			return (age >= 2 * getSize());
		}
		
		return (age >= getSize());		
	}
	
	private int normalizePosition(int position) {
		int normalized_position = position%getSize();

		if (position < 0) {
			normalized_position += getSize();
		}

		return normalized_position;
	}
}

/* BELT PLATE EXCEPTION CLASS */
import sushiGame.sushi.Plate;

public class BeltPlateException extends Exception {
	private int position;
	private Plate plate;
	private Belt belt;
	
	public BeltPlateException(int position, Plate plate_to_be_set, Belt belt) {
		this.position = position;
		this.plate = plate;
		this.belt = belt;
	}
	
	public int getPosition() {
		return position;
	}

	public Plate getPlateToSet() {
		return plate;
	}
	
	public Belt getBelt() {
		return belt;
	}
}

/* CHEF IMPLEMENTATION CLASS */
import java.util.ArrayList;
import java.util.List;
import sushiGame.sushi.Plate;

public class ChefImpl implements Chef, BeltObserver {

	private double balance;
	private List<HistoricalPlate> plate_history;
	private String name;
	private ChefsBelt belt;
	private boolean already_placed_this_rotation;
	
	public ChefImpl(String name, double starting_balance, ChefsBelt belt) {
		this.name = name;
		this.balance = starting_balance;
		this.belt = belt;
		belt.registerBeltObserver(this);
		already_placed_this_rotation = false;
		plate_history = new ArrayList<HistoricalPlate>();
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String n) {
		this.name = n;
	}

	@Override
	public HistoricalPlate[] getPlateHistory(int history_length) {
		if (history_length < 1 || (plate_history.size() == 0)) {
			return new HistoricalPlate[0];
		}

		if (history_length > plate_history.size()) {
			history_length = plate_history.size();
		}
		return plate_history.subList(plate_history.size()-history_length, plate_history.size()-1).toArray(new HistoricalPlate[history_length]);
	}

	@Override
	public HistoricalPlate[] getPlateHistory() {
		return getPlateHistory(plate_history.size());
	}

	@Override
	public double getBalance() {
		return balance;
	}

	@Override
	public void makeAndPlacePlate(Plate plate, int position) 
			throws InsufficientBalanceException, BeltFullException, AlreadyPlacedThisRotationException {

		if (already_placed_this_rotation) {
			throw new AlreadyPlacedThisRotationException();
		}
		
		if (plate.getContents().getCost() > balance) {
			throw new InsufficientBalanceException();
		}
		belt.setPlateNearestToPosition(plate, position);
		balance = balance - plate.getContents().getCost();
		already_placed_this_rotation = true;
	}

	@Override
	public void handleBeltEvent(BeltEvent e) {
		if (e.getType() == BeltEvent.EventType.PLATE_CONSUMED) {
			Plate plate = ((PlateEvent) e).getPlate();
			if (plate.getChef() == this) {
				balance += plate.getPrice();
				Customer consumer = belt.getCustomerAtPosition(((PlateEvent) e).getPosition());
				plate_history.add(new HistoricalPlateImpl(plate, consumer));
			}
		} else if (e.getType() == BeltEvent.EventType.PLATE_SPOILED) {
			Plate plate = ((PlateEvent) e).getPlate();
			plate_history.add(new HistoricalPlateImpl(plate, null));
		} else if (e.getType() == BeltEvent.EventType.ROTATE) {
			already_placed_this_rotation = false;
		}
	}
	
	@Override
	public boolean alreadyPlacedThisRotation() {
		return already_placed_this_rotation;
	}
}

/* HISTORICAL PLATE IMPLEMENTATION CLASS */
import sushiGame.sushi.Plate;
import sushiGame.sushi.Sushi;

public class HistoricalPlateImpl implements HistoricalPlate {
	private Customer consumer;
	private Plate plate;
	public HistoricalPlateImpl(Plate p, Customer c) {
		plate = p;
		consumer = c;
	}
	
	@Override
	public Sushi getContents() {
		return plate.getContents();
	}

	@Override
	public double getPrice() {
		return plate.getPrice();
	}

	@Override
	public Color getColor() {
		return plate.getColor();
	}

	@Override
	public double getProfit() {
		return plate.getProfit();
	}

	@Override
	public Chef getChef() {
		return plate.getChef();
	}

	@Override
	public boolean wasSpoiled() {
		return (consumer == null);
	}

	@Override
	public Customer getConsumer() {
		return consumer;
	}
}

/* INSUFFICIENT BALANCE EXCEPTION CLASS */
public class InsufficientBalanceException extends Exception {
	public InsufficientBalanceException() {
		super("Insufficient balance");
	}
}

/* PLATE CONSUMED EVENT CLASS */
import sushiGame.sushi.Plate;
public class PlateConsumedEvent extends PlateEvent {
	public PlateConsumedEvent (Plate p, int position) {
		super(BeltEvent.EventType.PLATE_CONSUMED, p, position);
	}
}

/* PLATE EVENT CLASS */
import sushiGame.sushi.Plate;

abstract public class PlateEvent extends BeltEvent {
	private Plate plate;
	private int position;
	public PlateEvent(BeltEvent.EventType type, Plate plate, int position) {
		super(type);
		this.plate = plate;
		this.position = position;
	}
	
	public  Plate getPlate() {
		return plate;
	}
	
	public int getPosition() {
		return position;
	}
}

/* PLATE PLACED EVENT CLASS */
import sushiGame.sushi.Plate;
public class PlatePlacedEvent extends PlateEvent {
	public PlatePlacedEvent (Plate p, int position) {
		super(BeltEvent.EventType.PLATE_PLACED, p, position);
	}
}

/* PLATE SPOILED EVENT CLASS */
import sushiGame.sushi.Plate;
public class PlateSpoiledEvent extends PlateEvent {
	public PlateSpoiledEvent (Plate p, int position) {
		super(BeltEvent.EventType.PLATE_SPOILED, p, position);
	}
}

/* RANDOM CUSTOMER CLASS */
import sushiGame.sushi.Plate;
public class RandomCustomer implements Customer {
	private double pickiness;
	
	public RandomCustomer(double pickiness) {
		this.pickiness = pickiness;
	}

	@Override
	public boolean consumesPlate(Plate p) {
		return (Math.random() < pickiness);
	}
}

/* ROTATE EVENT CLASS */
public class RotateEvent extends BeltEvent {
	public RotateEvent() {
		super(BeltEvent.EventType.ROTATE);
	}
}

/* SUSHI GAME MODEL CLASS */
public class SushiGameModel {

	private BeltImpl belt;
	private Customer[] customers;
	private Chef[] opponent_chefs;
	private Chef player_chef;

	private final double STARTING_BALANCE = 100.0;

	public SushiGameModel(int belt_size, int num_customers, int num_chef_opponents) {
		if (belt_size < 1) {
			throw new IllegalArgumentException("Belt must have size > 0");
		}

		if (belt_size < num_customers) {
			throw new IllegalArgumentException("Belt size must be greater then number of customers");
		}

		belt = new BeltImpl(belt_size);
		customers = new Customer[num_customers];
		opponent_chefs = new Chef[num_chef_opponents];

		int belt_idx = 0;
		for (int i=0; i<num_customers; i++) {
			customers[i] = new RandomCustomer(Math.random());
			belt.setCustomerAtPosition(customers[i], belt_idx);
			belt_idx += belt_size / num_customers;
		}

		for (int i=0; i<num_chef_opponents; i++) {
			opponent_chefs[i] = new ChefImpl("Opponent Chef " + i, STARTING_BALANCE, belt);
		}
		player_chef = new ChefImpl("Player", STARTING_BALANCE, belt);
	}

	public Chef getPlayerChef() {
		return player_chef;
	}

	public Chef[] getOpponentChefs() {
		return opponent_chefs.clone();
	}

	public Belt getBelt() {
		return belt;
	}
}

/* TIMED PLATE IMPLEMENTATION CLASS */
import sushiGame.sushi.Plate;
import sushiGame.sushi.Sushi;

public class TimedPlateImpl implements TimedPlate {
	private Plate original;
	private int incept_date;
	
	public TimedPlateImpl(Plate plate, int rotation_count) {
		original = plate;
		incept_date = rotation_count;
	}

	@Override
	public Sushi getContents() {
		return original.getContents();
	}

	@Override
	public double getPrice() {
		return original.getPrice();
	}

	@Override
	public Color getColor() {
		return original.getColor();
	}

	@Override
	public double getProfit() {
		return original.getProfit();
	}

	@Override
	public int getInceptDate() {
		return incept_date;
	}

	@Override
	public Plate getOriginal() {
		return original;
	}

	@Override
	public Chef getChef() {
		return original.getChef();
	}
}
