/* package in source folder for Sushi-Game containing classes of view component
* Classes: BeltView.java, HighToLowBalanceComparator.java, PlateView.java, PlateChefView.java, ScoreboardWidget.java,
* SushiGameView.java
* Interfaces: ChefViewListener.java, RotationRequestListener.java
*/

package src.sushiGame.controller;

/* CHEF VIEW INTERFACE */
import sushiGame.sushi.Sushi;
public interface ChefViewListener_Interface {
	void handleRedPlateRequest(Sushi plate_sushi, int plate_position);
	void handleGreenPlateRequest(Sushi plate_sushi, int plate_position);
	void handleBluePlateRequest(Sushi plate_sushi, int plate_position);
	void handleGoldPlateRequest(Sushi plate_sushi, int plate_position, double price);
}

/* ROTATION REQUEST LISTENER INTERFACE */
public interface RotationRequestListener_Interface {
	void handleRotationRequest();
}

/* BELT VIEW CLASS */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import sushiGame.sushi.Plate;
import sushiGame.model.Belt;
import sushiGame.model.BeltEvent;
import sushiGame.model.BeltObserver;

// provides user interface for displaying the contents of the sushi belt
public class BeltView extends JPanel implements BeltObserver {

	private Belt belt;
	private JPanel[] plateView;

	public BeltView(Belt b) {
		this.belt = b;
		belt.registerBeltObserver(this);
		setLayout(new GridLayout(belt.getSize(), 1));

		// creates vertical plate list (each is a panel)
		plateView = new JPanel[belt.getSize()];

		// adds panels to plateView
		for (int i = 0; i < belt.getSize(); i++) {
			JPanel panel = new JPanel();
			panel.setMinimumSize(new Dimension(1150, 40));
			panel.setPreferredSize(new Dimension(1150, 40));
			panel.setOpaque(true);
			panel.setBackground(Color.PINK);
			add(panel);
			plateView[i] = panel;
		}
		refresh();
	}

	// calls refresh() everytime BeltEvent occurs
	@Override
	public void handleBeltEvent(BeltEvent e) {
		refresh();
	}

	public void refresh() {

		// goes through every position on belt and modifies panels at each position
		for (int i = 0; i < belt.getSize(); i++) {
			Plate p = belt.getPlateAtPosition(i);
			JPanel plate = plateView[i];
			int position = i;

			// if no plate, empties panel and makes it pink
			if (Objects.equals(p, null)) {
				plate.setBackground(Color.PINK);
				plate.removeAll();
			} else {
				String plateName = p.getContents().getName().toString().toUpperCase();
				// makes panel same color as plate it represents
				switch (p.getColor()) {
				case RED:
					plate.setBackground(Color.RED);
					break;
				case GREEN:
					plate.setBackground(Color.GREEN);
					break;
				case BLUE:
					plate.setBackground(Color.BLUE);
					break;
				case GOLD:
					plate.setBackground(Color.YELLOW);
					break;
				}

				if (plate.getComponentCount() > 0) {
					plate.removeAll(); // removes button
				}
				
				JButton button = new JButton(plateName + " PLATE");
				button.setForeground(plate.getBackground().darker());
				button.setVisible(true);

				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JOptionPane.showMessageDialog(null,
								new PlateView(p, position, belt.getAgeOfPlateAtPosition(position)).getInfo(),
								"PLATE INFORMATION", JOptionPane.PLAIN_MESSAGE);
					}
				});
				plate.add(button, BorderLayout.CENTER);
			}
		}
	}
}

/* HIGH TO LOW BALANCE COMPARATOR CLASS*/
import java.util.Comparator;
import sushiGame.model.Chef;
public class HighToLowBalanceComparator implements Comparator<Chef> {

	@Override
	public int compare(Chef a, Chef b) {
		// We do b - a because we want largest to smallest
		return (int) (Math.round(b.getBalance()*100.0) - 
				Math.round(a.getBalance()*100));
	}			
}

/* PLATE VIEW CLASS */
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Objects;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sushiGame.sushi.IngredientPortion;
import sushiGame.sushi.Plate;

public class PlateView extends JPanel {
	private JPanel plateInfo;
	public PlateView(Plate plate, int position, int age) {
		if (Objects.equals(plate, null)) {
			throw new NullPointerException();
		}
		this.plateInfo = new JPanel();
		plateInfo.setLayout(new GridLayout(0,2));
		plateInfo.setSize(new Dimension(100,100));
		plateInfo.setVisible(true);
	
		IngredientPortion[] ing = plate.getContents().getIngredients().clone();
		
		plateInfo.add(new JLabel("PLATE COLOR: "));
		plateInfo.add(new JLabel(plate.getColor().toString().toLowerCase()));
		
		plateInfo.add(new JLabel("TYPE OF SUSHI: "));
		plateInfo.add( new JLabel(plate.getContents().getName().toLowerCase()));
		
		plateInfo.add(new JLabel ("CHEF: "));
		plateInfo.add(new JLabel(plate.getChef().getName().toLowerCase()));
		
		plateInfo.add(new JLabel("AGE OF PLATE:"));
		plateInfo.add(new JLabel("" + age));

		plateInfo.add(new JLabel("INGREDIENTS: "));	

		for (int i = 0; i < ing.length; i++) {
			double amount = ((int) (ing[i].getAmount() * 100.0 + 0.5)) / 100.0;
			String ingredient = ing[i].getName();
			plateInfo.add(new JLabel(ingredient + " 	(" + amount + " oz." + ")"));
			if (!Objects.equals(ing.length -1, i)) {
				plateInfo.add(new JLabel(""));
			}
		}
	}

	public JPanel getInfo() {
		return plateInfo;
	}
}

/* PLATE CHEF VIEW CLASS */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import sushiGame.sushi.AvocadoPortion;
import sushiGame.sushi.CrabPortion;
import sushiGame.sushi.EelPortion;
import sushiGame.sushi.IngredientPortion;
import sushiGame.sushi.Nigiri;
import sushiGame.sushi.RicePortion;
import sushiGame.sushi.Roll;
import sushiGame.sushi.SalmonPortion;
import sushiGame.sushi.Sashimi;
import sushiGame.sushi.SeaweedPortion;
import sushiGame.sushi.ShrimpPortion;
import sushiGame.sushi.Sushi;
import sushiGame.sushi.TunaPortion;

public class PlayerChefView extends JPanel implements ActionListener {
	private List<ChefViewListener> listeners;
	private int count;

	public PlayerChefView(int belt_size) {
		listeners = new ArrayList<ChefViewListener>();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JButton redButton = new JButton("Make a Red Plate");
		JButton blueButton = new JButton("Make a Blue Plate");
		JButton greenButton = new JButton("Make a Green Plate");
		JButton goldButton = new JButton("Make a Gold Plate");

		redButton.setActionCommand("red");
		blueButton.setActionCommand("blue");
		greenButton.setActionCommand("green");
		goldButton.setActionCommand("gold");

		redButton.addActionListener(this);
		blueButton.addActionListener(this);
		greenButton.addActionListener(this);
		goldButton.addActionListener(this);

		add(redButton);
		add(blueButton);
		add(greenButton);
		add(goldButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Sushi type = null;
		int position = 0;
		double price = 0;
		
		Object[] possibleTypes = { "Sashimi", "Nigiri", "Custom Roll" };
		String selectedType = JOptionPane.showInputDialog(null, "Select a Type of Sushi", "",
				JOptionPane.PLAIN_MESSAGE, null, possibleTypes, possibleTypes[0]).toString();

		switch (selectedType) {
		case "Sashimi":
			Object[] possibleSashimi = { "Tuna", "Salmon", "Eel", "Crab", "Shrimp" };
			String selectedSashimi = JOptionPane.showInputDialog(null, "Select a type of Sashimi.", "",
					JOptionPane.PLAIN_MESSAGE, null, possibleSashimi, possibleSashimi[0]).toString();

			{ switch (selectedSashimi) {
			case "Tuna":
				type = new Sashimi(Sashimi.SashimiType.TUNA);
				break;
			case "Salmon":
				type = new Sashimi(Sashimi.SashimiType.SALMON);
				break;
			case "Eel":
				type = new Sashimi(Sashimi.SashimiType.EEL);
				break;
			case "Crab":
				type = new Sashimi(Sashimi.SashimiType.CRAB);
				break;
			case "Shrimp":
				type = new Sashimi(Sashimi.SashimiType.SHRIMP);
				break; } 
			
			Object[] possiblePositions = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
			int selectedPosition = (int) JOptionPane.showInputDialog(null, "Select a position to place your plate.", "",
					JOptionPane.PLAIN_MESSAGE, null, possiblePositions, possiblePositions[0]);
			position = selectedPosition - 1;
			break;
			}
		case "Nigiri":
			Object[] possibleNigiri = { "Tuna", "Salmon", "Eel", "Crab", "Shrimp" };
			String selectedNigiri = JOptionPane.showInputDialog(null, "Select a type of Nigiri.", "Input",
					JOptionPane.PLAIN_MESSAGE, null, possibleNigiri, possibleNigiri[0]).toString();
			
			{ switch (selectedNigiri) {
			case "Tuna":
				type = new Nigiri(Nigiri.NigiriType.TUNA);
				break;
			case "Salmon":
				type = new Nigiri(Nigiri.NigiriType.SALMON);
				break;
			case "Eel":
				type = new Nigiri(Nigiri.NigiriType.EEL);
				break;
			case "Crab":
				type = new Nigiri(Nigiri.NigiriType.CRAB);
				break;
			case "Shrimp":
				type = new Nigiri(Nigiri.NigiriType.SHRIMP);
				break; } 
			Object[] possiblePositions = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
			int selectedPosition = (int) JOptionPane.showInputDialog(null, "Select a position to place your plate.", "",
					JOptionPane.PLAIN_MESSAGE, null, possiblePositions, possiblePositions[0]);
			position = selectedPosition - 1;
			break;
			}
			
		default: 
			JPanel frame = new JPanel();
			frame.setLayout(new GridLayout(0, 2));
			frame.setSize(new Dimension(100, 200));
			frame.setVisible(true);
			
			JPanel one = new JPanel();
			one.isOpaque();
			one.setVisible(true);
			JLabel pickIngredients = new JLabel(" Select your ingredients. ");
			one.add(pickIngredients); 
			frame.add(one);
			
			JPanel two = new JPanel();
			two.isOpaque();
			two.setVisible(true);
			JLabel pickAmounts = new JLabel(" Select amounts of each ingredient. ");
			two.add(pickAmounts);
			frame.add(two);
		    
			JCheckBox avocado = new JCheckBox("Avocado");
			frame.add(avocado); 

			JPanel p1 = new JPanel();
			SpinnerModel m1 = new SpinnerNumberModel(0, 0, 1.50, 0.01);
		    JSpinner avocadoSpinner = new JSpinner(m1);
		    p1.add(avocadoSpinner, BorderLayout.WEST);
			p1.add(new JLabel("oz."), BorderLayout.EAST);
		    frame.add(p1, BorderLayout.SOUTH);
		    
			JCheckBox crab = new JCheckBox("Crab");
			frame.add(crab);
			
			JPanel p2 = new JPanel();
			SpinnerModel m2 = new SpinnerNumberModel(0, 0, 1.50, 0.01);
		    JSpinner crabSpinner = new JSpinner(m2);
		    p2.add(crabSpinner, BorderLayout.WEST);
			p2.add(new JLabel("oz."), BorderLayout.EAST);
			frame.add(p2, BorderLayout.SOUTH);
		    
			JCheckBox eel = new JCheckBox("Eel");
			frame.add(eel);
			
			JPanel p3 = new JPanel();
			SpinnerModel m3 = new SpinnerNumberModel(0, 0, 1.50, 0.01);
		    JSpinner eelSpinner = new JSpinner(m3);
		    p3.add(eelSpinner, BorderLayout.WEST);
			p3.add(new JLabel("oz."), BorderLayout.EAST);
			frame.add(p3, BorderLayout.SOUTH);

			JCheckBox rice = new JCheckBox("Rice");
			frame.add(rice);
			
			JPanel p4 = new JPanel();
			SpinnerModel m4 = new SpinnerNumberModel(0, 0, 1.50, 0.01);
		    JSpinner riceSpinner = new JSpinner(m4);
		    p4.add(riceSpinner, BorderLayout.WEST);
			p4.add(new JLabel("oz."), BorderLayout.EAST);
			frame.add(p4, BorderLayout.SOUTH);
			
			JCheckBox salmon = new JCheckBox("Salmon");
			frame.add(salmon);
			
			JPanel p5 = new JPanel();
			SpinnerModel m5 = new SpinnerNumberModel(0, 0, 1.50, 0.01);
		    JSpinner salmonSpinner = new JSpinner(m5);
		    p5.add(salmonSpinner, BorderLayout.WEST);
			p5.add(new JLabel("oz."), BorderLayout.EAST);
			frame.add(p5, BorderLayout.SOUTH);
			
			JCheckBox seaweed = new JCheckBox("Seaweed");
			frame.add(seaweed);
			
			JPanel p6 = new JPanel();
			SpinnerModel m6 = new SpinnerNumberModel(0, 0, 1.50, 0.01);
		    JSpinner seaweedSpinner = new JSpinner(m6);
		    p6.add(seaweedSpinner, BorderLayout.WEST);
			p6.add(new JLabel("oz."), BorderLayout.EAST);
			frame.add(p6, BorderLayout.SOUTH);
			
			JCheckBox shrimp = new JCheckBox("Shrimp");
			frame.add(shrimp);
			
			JPanel p7 = new JPanel();
			SpinnerModel m7 = new SpinnerNumberModel(0, 0, 1.50, 0.01);
		    JSpinner shrimpSpinner = new JSpinner(m7);
		    p7.add(shrimpSpinner, BorderLayout.WEST);
			p7.add(new JLabel("oz."), BorderLayout.EAST);
			frame.add(p7, BorderLayout.SOUTH);

			JCheckBox tuna = new JCheckBox("Tuna");
			frame.add(tuna);
			
			JPanel p8 = new JPanel();
			SpinnerModel m8 = new SpinnerNumberModel(0, 0, 1.50, 0.01);
		    JSpinner tunaSpinner = new JSpinner(m8);
		    p8.add(tunaSpinner, BorderLayout.WEST);
			p8.add(new JLabel("oz."), BorderLayout.EAST);
			frame.add(p8, BorderLayout.SOUTH);

			int p = JOptionPane.showOptionDialog(null, frame, "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			
			List<IngredientPortion> list = new ArrayList<IngredientPortion>();
			if (Objects.equals(p, JOptionPane.OK_OPTION)) {
				if (avocado.isSelected()) {
					list.add(new AvocadoPortion((double)avocadoSpinner.getValue()));
				}
				if (crab.isSelected()) {
					list.add(new CrabPortion((double)crabSpinner.getValue()));
				}
				if (eel.isSelected()) {
					list.add(new EelPortion((double)eelSpinner.getValue()));
				}
				if (rice.isSelected()) {
					list.add(new RicePortion((double)riceSpinner.getValue()));
				}
				if (salmon.isSelected()) {
					list.add(new SalmonPortion((double)salmonSpinner.getValue()));
				}
				if (seaweed.isSelected()) {
					list.add(new SeaweedPortion((double)seaweedSpinner.getValue()));
				}
				if (shrimp.isSelected()) {
					list.add(new ShrimpPortion((double)shrimpSpinner.getValue()));
				}
				if (tuna.isSelected()) {
					list.add(new TunaPortion((double)tunaSpinner.getValue()));
				}
			}
			IngredientPortion[] ingArray = new IngredientPortion[list.size()];
			for (int i = 0; i < list.size(); i++) {
				ingArray[i] = list.get(i);
			}
			count++;
			type = new Roll("Custom Roll " + count, ingArray);
			
			Object[] possiblePositions = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
			int selectedPosition = (int) JOptionPane.showInputDialog(null, "Select a position to place your plate.", "",
					JOptionPane.PLAIN_MESSAGE, null, possiblePositions, possiblePositions[0]);
			position = selectedPosition - 1;
			
		}
		
		switch (e.getActionCommand()) {
		case "red":
			makeRedPlateRequest(type, position);
			makeRedPlateRequest(type, position);
			break;
		case "blue":
			makeBluePlateRequest(type, position);
			makeBluePlateRequest(type, position);
			break;
		case "green":
			makeGreenPlateRequest(type, position);
			makeGreenPlateRequest(type, position);
			break;
		case "gold":
			JPanel x = new JPanel();
			x.setLayout(new GridLayout(0, 2));
			x.setSize(new Dimension(100, 200));
			x.setVisible(true);
			SpinnerModel model = new SpinnerNumberModel(7.50, 5.00, 10.00, 0.01);
		    JSpinner priceSpinner = new JSpinner(model);
		    JLabel y = new JLabel("$");
		    x.add(y);
		    x.add(priceSpinner);
			int p = JOptionPane.showOptionDialog(null, x, "Select a price for your plate.", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
			
			if (Objects.equals(p, JOptionPane.CANCEL_OPTION)) {
				price = 0;
			} else if (Objects.equals(p, JOptionPane.OK_OPTION)) {
				price = (Double)priceSpinner.getValue();
			}
			makeGoldPlateRequest(type, position, price);
			makeGoldPlateRequest(type, position, price);
		
			break;
		}
	}

	private Sushi nigiriSelection() {
		Object[] possibleValues = { "Tuna", "Salmon", "Eel", "Crab", "Shrimp" };
		String selectedValue = JOptionPane.showInputDialog(null, "Select a type of Nigiri.", "Input",
				JOptionPane.PLAIN_MESSAGE, null, possibleValues, possibleValues[0]).toString();
		switch (selectedValue) {
		case "Tuna":
			return new Nigiri(Nigiri.NigiriType.TUNA);
		case "Salmon":
			return new Nigiri(Nigiri.NigiriType.SALMON);
		case "Eel":
			return new Nigiri(Nigiri.NigiriType.EEL);
		case "Crab":
			return new Nigiri(Nigiri.NigiriType.CRAB);
		case "Shrimp":
			return new Nigiri(Nigiri.NigiriType.SHRIMP);
		}
		return new Nigiri(Nigiri.NigiriType.TUNA);
	}

	public void registerChefListener(ChefViewListener cl) {
		listeners.add(cl);
	}

	private void makeRedPlateRequest(Sushi plate_sushi, int plate_position) {
		for (ChefViewListener l : listeners) {
			l.handleRedPlateRequest(plate_sushi, plate_position);
		}	
	}

	private void makeGreenPlateRequest(Sushi plate_sushi, int plate_position) {
		for (ChefViewListener l : listeners) {
			l.handleGreenPlateRequest(plate_sushi, plate_position);
		}
	}

	private void makeBluePlateRequest(Sushi plate_sushi, int plate_position) {
		for (ChefViewListener l : listeners) {
			l.handleBluePlateRequest(plate_sushi, plate_position);
		}
	}

	private void makeGoldPlateRequest(Sushi plate_sushi, int plate_position, double price) {
		for (ChefViewListener l : listeners) {
			l.handleGoldPlateRequest(plate_sushi, plate_position, price);
		}
	}
}

/* SCOREBOARD WIDGET CLASS */
import java.awt.BorderLayout;
import java.util.Arrays;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import sushiGame.model.BeltEvent;
import sushiGame.model.BeltObserver;
import sushiGame.model.Chef;
import sushiGame.model.SushiGameModel;

public class ScoreboardWidget extends JPanel implements BeltObserver {

	private SushiGameModel game_model;
	private JLabel display;
	
	public ScoreboardWidget(SushiGameModel gm) {
		game_model = gm;
		game_model.getBelt().registerBeltObserver(this);
		display = new JLabel();
		display.setVerticalAlignment(SwingConstants.TOP);
		setLayout(new BorderLayout());
		add(display, BorderLayout.CENTER);
		display.setText(makeScoreboardHTML());
	}

	private String makeScoreboardHTML() {
		String sb_html = "<html>";
		sb_html += "<h1>Scoreboard</h1>";

		// Create an array of all chefs and sort by balance.
		Chef[] opponent_chefs= game_model.getOpponentChefs();
		Chef[] chefs = new Chef[opponent_chefs.length+1];
		chefs[0] = game_model.getPlayerChef();
		for (int i=1; i<chefs.length; i++) {
			chefs[i] = opponent_chefs[i-1];
		}
		Arrays.sort(chefs, new HighToLowBalanceComparator());
		
		for (Chef c : chefs) {
			sb_html += c.getName() + " ($" + Math.round(c.getBalance()*100.0)/100.0 + ") <br>";
		}
		return sb_html;
	}

	public void refresh() {
		display.setText(makeScoreboardHTML());		
	}
	
	@Override
	public void handleBeltEvent(BeltEvent e) {
		if (e.getType() == BeltEvent.EventType.ROTATE) {
			refresh();
		}		
	}
}

/* SUSHI GAME VIEW CLASS */
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sushiGame.model.BeltEvent;
import sushiGame.model.BeltObserver;
import sushiGame.model.SushiGameModel;

public class SushiGameView extends JPanel implements ActionListener, BeltObserver {
	private PlayerChefView player_chef_ui;
	private List<RotationRequestListener> rotation_request_listeners;
	private JLabel controller_messages;
	ScoreboardWidget scoreboard;
	
	public SushiGameView(SushiGameModel game_model) {
		setLayout(new BorderLayout());
		
		scoreboard = new ScoreboardWidget(game_model);
		add(scoreboard, BorderLayout.WEST);
				
		player_chef_ui = new PlayerChefView(game_model.getBelt().getSize());
		add(player_chef_ui, BorderLayout.EAST);
		
		BeltView belt_view = new BeltView(game_model.getBelt());
		add(belt_view, BorderLayout.CENTER);
		
		JPanel bottom_panel = new JPanel();
		bottom_panel.setLayout(new BorderLayout());
		
		JButton rotate_button = new JButton("Rotate");
		rotate_button.setActionCommand("rotate");
		rotate_button.addActionListener(this);
		
		bottom_panel.add(rotate_button, BorderLayout.WEST);
		
		controller_messages = new JLabel("Controller messages.");
		bottom_panel.add(controller_messages, BorderLayout.CENTER);
		
		add(bottom_panel, BorderLayout.SOUTH);
		
		rotation_request_listeners = new ArrayList<RotationRequestListener>();
		
		game_model.getBelt().registerBeltObserver(this);
	}
	
	public void registerPlayerChefListener(ChefViewListener cl) {
		player_chef_ui.registerChefListener(cl);
	}
	
	public void registerRotationRequestListener(RotationRequestListener rrl) {
		rotation_request_listeners.add(rrl);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("rotate")) {
			for (RotationRequestListener rrl : rotation_request_listeners) {
				rrl.handleRotationRequest();
			}
		}
	}
	
	public void setControllerMessage(String message) {
		controller_messages.setText(message);
	}

	@Override
	public void handleBeltEvent(BeltEvent e) {
		if (e.getType() == BeltEvent.EventType.ROTATE) {
			controller_messages.setText("");
		}
	}
	
	public void refreshScoreboard() {
		scoreboard.refresh();
	}
}
