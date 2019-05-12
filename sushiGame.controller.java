/* package in source folder for Sushi-Game containing classes of controller component
* Classes: NigiriChefController.java, PlayerChefController.java, RollMakerChefController.java, SashimiChefController.java,
* SushiGameController.java
* Interfaces: ChefController_Interface.java
*/

package src.sushiGame.controller;

/*  CHEF CONTROLLER INTERFACE */
import sushiGame.model.BeltObserver;
public interface ChefController_Interface extends BeltObserver {
}

/*  NIGIRI CHEF CONTROLLER CLASS */
import sushiGame.sushi.BluePlate;
import sushiGame.sushi.GreenPlate;
import sushiGame.sushi.Nigiri;
import sushiGame.sushi.Plate;
import sushiGame.sushi.PlatePriceException;
import sushiGame.sushi.RedPlate;
import sushiGame.model.AlreadyPlacedThisRotationException;
import sushiGame.model.BeltEvent;
import sushiGame.model.BeltFullException;
import sushiGame.model.Chef;
import sushiGame.model.InsufficientBalanceException;

public class NigiriChefController implements ChefController {
	private Chef chef;
	private double makeFrequency;
	private int belt_size;

	public NigiriChefController(Chef c, int belt_size) {
		chef = c;
		makeFrequency = Math.random() * 0.5 + 0.25;
		this.belt_size = belt_size;
	}

	@Override
	public void handleBeltEvent(BeltEvent e) {
		if (e.getType() == BeltEvent.EventType.ROTATE) {
			if (Math.random() < makeFrequency) {
				Nigiri.NigiriType type = pickType();
				Plate plate = null;
				try {
					switch(pickColor()) {
					case RED:
						plate = new RedPlate(chef, new Nigiri(type)); 
						break;
					case GREEN:
						plate = new GreenPlate(chef, new Nigiri(type)); 
						break;
					case BLUE:
						plate = new BluePlate(chef, new Nigiri(type)); 
						break;
					case GOLD:
						// This will never happen but need the case for 
						// the switch statement.
						return;
					}
				}
				catch (PlatePriceException exc) {
					// Nigiri too expensive for plate we chose.
					// Bail and do not try to place plate.
					return;
				}
				try {
					chef.makeAndPlacePlate(plate, (int) (Math.random()*belt_size));
				} catch (InsufficientBalanceException | BeltFullException | AlreadyPlacedThisRotationException exc) {
					// Too little money, belt too full, or already went this rotation.
					// Bail and do nothing.
					return;
				}
			}
		}
	}

	private Nigiri.NigiriType pickType() {
		Nigiri.NigiriType[] types = new Nigiri.NigiriType[] {
				Nigiri.NigiriType.CRAB,
				Nigiri.NigiriType.EEL, 
				Nigiri.NigiriType.SALMON, 
				Nigiri.NigiriType.TUNA, 
				Nigiri.NigiriType.SHRIMP
		};
		return types[(int) (Math.random()*types.length)];
	}

	private Plate.Color pickColor() {
		Plate.Color[] colors = new Plate.Color[] {
				Plate.Color.RED, Plate.Color.GREEN, Plate.Color.BLUE
		};
		return colors[(int) (Math.random()*colors.length)];
	}
}

/*  PLAYER CHEF CONTROLLER CLASS */
import sushiGame.sushi.BluePlate;
import sushiGame.sushi.GoldPlate;
import sushiGame.sushi.GreenPlate;
import sushiGame.sushi.Plate;
import sushiGame.sushi.PlatePriceException;
import sushiGame.sushi.RedPlate;
import sushiGame.sushi.Sushi;
import sushiGame.model.AlreadyPlacedThisRotationException;
import sushiGame.model.BeltFullException;
import sushiGame.model.Chef;
import sushiGame.model.InsufficientBalanceException;
import sushiGame.view.ChefViewListener;
import sushiGame.view.SushiGameView;

public class PlayerChefController implements ChefViewListener {
	private Chef chef;
	private SushiGameView game_view;
	
	public PlayerChefController(Chef playerChef, SushiGameView gv) {
		chef = playerChef;
		this.game_view = gv;
	}

	private void placePlate(Plate plate, int position) {
		try {
			chef.makeAndPlacePlate(plate, position);
		} catch (InsufficientBalanceException e) {
			game_view.setControllerMessage("Insufficient balance");
		} catch (BeltFullException e) {
			game_view.setControllerMessage("Belt is full");
		} catch (AlreadyPlacedThisRotationException e) {
			game_view.setControllerMessage("Already placed a plate this rotation");
		} catch (Exception e) {
			game_view.setControllerMessage(e.getMessage());
		}
	}

	@Override
	public void handleRedPlateRequest(Sushi plate_sushi, int plate_position) {
		try {
			Plate p = new RedPlate(chef, plate_sushi);
			placePlate(p, plate_position);
			
		} catch (PlatePriceException e) {
			game_view.setControllerMessage("Sushi too costly for red plate.");
		}
	}

	@Override
	public void handleGreenPlateRequest(Sushi plate_sushi, int plate_position) {
		try {
			Plate p = new GreenPlate(chef, plate_sushi);
			placePlate(p, plate_position);
		} catch (PlatePriceException e) {
			game_view.setControllerMessage("Sushi too costly for green plate.");
		}
	}

	@Override
	public void handleBluePlateRequest(Sushi plate_sushi, int plate_position) {
		try {
			Plate p = new BluePlate(chef, plate_sushi);
			placePlate(p, plate_position);
		} catch (PlatePriceException e) {
			game_view.setControllerMessage("Sushi too costly for blue plate.");
		}
	}

	@Override
	public void handleGoldPlateRequest(Sushi plate_sushi, int plate_position, double price) {
		try {
			Plate p = new GoldPlate(chef, plate_sushi, price);
			placePlate(p, plate_position);
		} catch (PlatePriceException e) {
			game_view.setControllerMessage("Sushi too costly for gold plate.");
		}		
	}
}

/*  ROLL MAKER CHEF CONTROLLER CLASS */
import sushiGame.sushi.AvocadoPortion;
import sushiGame.sushi.CrabPortion;
import sushiGame.sushi.EelPortion;
import sushiGame.sushi.GoldPlate;
import sushiGame.sushi.IngredientPortion;
import sushiGame.sushi.Plate;
import sushiGame.sushi.PlatePriceException;
import sushiGame.sushi.RicePortion;
import sushiGame.sushi.Roll;
import sushiGame.sushi.SalmonPortion;
import sushiGame.sushi.SeaweedPortion;
import sushiGame.sushi.ShrimpPortion;
import sushiGame.sushi.TunaPortion;
import sushiGame.model.AlreadyPlacedThisRotationException;
import sushiGame.model.BeltEvent;
import sushiGame.model.BeltFullException;
import sushiGame.model.Chef;
import sushiGame.model.InsufficientBalanceException;

public class RollMakerChefController implements ChefController {

	private Chef chef;
	private double makeFrequency;
	private int belt_size;

	public RollMakerChefController(Chef c, int belt_size) {
		chef = c;
		makeFrequency = Math.random() * 0.5 + 0.25;
		this.belt_size = belt_size;
	}

	@Override
	public void handleBeltEvent(BeltEvent e) {

		if (e.getType() == BeltEvent.EventType.ROTATE) {
			if (Math.random() < makeFrequency) {
				Roll random_roll = makeRandomRoll();
				Plate plate = null;
				try {
					plate = new GoldPlate(chef, random_roll, Math.random()*3 + 5.0);
				}
				catch (PlatePriceException exc) {
					// Roll too expensive for price we chose.
					// Bail and do not try to place plate.
					return;
				}
				try {
					chef.makeAndPlacePlate(plate, (int) (Math.random()*belt_size));
				} catch (InsufficientBalanceException | BeltFullException | AlreadyPlacedThisRotationException exc) {
					// Too little money, belt too full, or already went this rotation.
					// Bail and do nothing.
					return;
				}
			}
		}
	}

	private Roll makeRandomRoll() {
		
		return new Roll("Random Roll",
				new IngredientPortion[] {
						new AvocadoPortion(Math.random()),
						new CrabPortion(Math.random()),
						new EelPortion(Math.random()),
						new RicePortion(Math.random()),
						new SalmonPortion(Math.random()),
						new SeaweedPortion(Math.random()),
						new ShrimpPortion(Math.random()),
						new TunaPortion(Math.random()),
		});
	}
}

/* SASHIMI CHEF CONTROLLER CLASS */
import sushiGame.sushi.BluePlate;
import sushiGame.sushi.GreenPlate;
import sushiGame.sushi.Plate;
import sushiGame.sushi.PlatePriceException;
import sushiGame.sushi.RedPlate;
import sushiGame.sushi.Sashimi;
import sushiGame.model.AlreadyPlacedThisRotationException;
import sushiGame.model.BeltEvent;
import sushiGame.model.BeltFullException;
import sushiGame.model.Chef;
import sushiGame.model.InsufficientBalanceException;

public class SashimiChefController implements ChefController {
	private Chef chef;
	private double makeFrequency;
	private int belt_size;

	public SashimiChefController(Chef c, int belt_size) {
		chef = c;
		makeFrequency = Math.random() * 0.5 + 0.25;
		this.belt_size = belt_size;
	}

	@Override
	public void handleBeltEvent(BeltEvent e) {
		if (e.getType() == BeltEvent.EventType.ROTATE) {
			if (Math.random() < makeFrequency) {
				Sashimi.SashimiType type = pickType();
				Plate plate = null;
				try {
					switch(pickColor()) {
					case RED:
						plate = new RedPlate(chef, new Sashimi(type)); 
						break;
					case GREEN:
						plate = new GreenPlate(chef, new Sashimi(type)); 
						break;
					case BLUE:
						plate = new BluePlate(chef, new Sashimi(type)); 
						break;
					case GOLD:
						// This will never happen but need the case for 
						// the switch statement.
						return;
					}
				}
				catch (PlatePriceException exc) {
					// Sashimi too expensive for plate we chose.
					// Bail and do not try to place plate.
					return;
				}
				try {
					chef.makeAndPlacePlate(plate, (int) (Math.random()*belt_size));
				} catch (InsufficientBalanceException | BeltFullException | AlreadyPlacedThisRotationException exc) {
					// Too little money, belt too full, or already went this rotation.
					// Bail and do nothing.
					return;
				}
			}
		}
	}

	private Sashimi.SashimiType pickType() {
		Sashimi.SashimiType[] types = new Sashimi.SashimiType[] {
				Sashimi.SashimiType.CRAB,
				Sashimi.SashimiType.EEL, 
				Sashimi.SashimiType.SALMON, 
				Sashimi.SashimiType.TUNA, 
				Sashimi.SashimiType.SHRIMP
		};
		return types[(int) (Math.random()*types.length)];
	}

	private Plate.Color pickColor() {
		Plate.Color[] colors = new Plate.Color[] {
				Plate.Color.RED, Plate.Color.GREEN, Plate.Color.BLUE
		};
		return colors[(int) (Math.random()*colors.length)];
	}
}

/* SUSHI GAME CONTROLLER CLASS */
import sushiGame.model.Belt;
import sushiGame.model.Chef;
import sushiGame.model.SushiGameModel;
import sushiGame.view.RotationRequestListener;
import sushiGame.view.SushiGameView;

public class SushiGameController implements RotationRequestListener {

	private Belt belt;
	
	public SushiGameController(SushiGameModel game_model, SushiGameView game_view) {
		belt = game_model.getBelt();
		game_view.registerRotationRequestListener(this);
		
		Chef[] opponent_chefs = game_model.getOpponentChefs();
		
		for (Chef c: opponent_chefs) {
			double random_draw = Math.random();
			ChefController chef_controller = null;
			String name = createRandomName();
			
			if (random_draw < 0.333) {
				c.setName("Sashimi " + name);
				chef_controller = new SashimiChefController(c, belt.getSize());
			} else if (random_draw < 0.66666) {
				c.setName("Nigiri " + name);
				chef_controller = new NigiriChefController(c, belt.getSize());				
			} else {
				c.setName("Rollmaker " + name);
				chef_controller = new RollMakerChefController(c, belt.getSize());
			}
			belt.registerBeltObserver(chef_controller);
		}
		
		// Refresh the game view's scoreboard now that chef names may have been changed.
		game_view.refreshScoreboard();
		
		PlayerChefController player_chef_controller = new PlayerChefController(game_model.getPlayerChef(), game_view);
		game_view.registerPlayerChefListener(player_chef_controller);
	}

	@Override
	public void handleRotationRequest() {
		belt.rotate();
	}

	private String createRandomName() {
		String[] names = new String[] {
				"Bob", "Carol", "Alex", "Sejal", "Hussein", 
				"Ming", "Carlos", "Tom", "Lester", "Maya",
				"Malcolm", "Grace", "Darius", "Thor", "Keisha"};
		return names[(int) (Math.random()*names.length)];
	}
}
