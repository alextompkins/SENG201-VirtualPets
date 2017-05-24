package application;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PetInteract extends JPanel {
	private static final long serialVersionUID = 571759901412319640L;

	private JLabel speciesTitle;
	private JLabel speciesLabel;
	private JLabel favouriteToyLabel;
	private JLabel favouriteToyIcon;
	private JLabel favouriteFoodLabel;
	private JLabel favouriteFoodIcon;
	private JLabel healthyLabel;
	private JButton buttonCure;
	private JLabel behavingLabel;
	private JButton buttonDiscipline;
	private PetStatDisplayer hungerSlider;
	private PetStatDisplayer energySlider;
	private PetStatDisplayer happinessSlider;
	private PetStatDisplayer weightSlider;

	/**
	 * Create the panel.
	 */
	public PetInteract(Font boldFont, Font semiBoldFont) {
		setOpaque(false);
		setLayout(null);
		
		speciesTitle = new JLabel("Species");
		speciesTitle.setHorizontalAlignment(SwingConstants.CENTER);
		speciesTitle.setFont(boldFont);
		speciesTitle.setBounds(12, 12, 232, 16);
		add(speciesTitle);
		
		speciesLabel = new JLabel("");
		speciesLabel.setHorizontalAlignment(SwingConstants.CENTER);
		speciesLabel.setFont(semiBoldFont);
		speciesLabel.setBounds(12, 28, 232, 16);
		add(speciesLabel);
		
		healthyLabel = new JLabel("");
		healthyLabel.setHorizontalAlignment(SwingConstants.CENTER);
		healthyLabel.setFont(boldFont);
		healthyLabel.setBounds(12, 48, 232, 16);
		add(healthyLabel);
		
		behavingLabel = new JLabel("");
		behavingLabel.setHorizontalAlignment(SwingConstants.CENTER);
		behavingLabel.setFont(boldFont);
		behavingLabel.setBounds(12, 92, 232, 16);
		add(behavingLabel);
		
		buttonCure = new JButton("Cure");
		buttonCure.setEnabled(false);
		buttonCure.setFont(boldFont);
		buttonCure.setToolTipText("Cures sickness. Costs $50 and consumes one action point.");
		buttonCure.setBounds(82, 66, 100, 20);
		add(buttonCure);
		
		buttonDiscipline = new JButton("Discipline");
		buttonDiscipline.setEnabled(false);
		buttonDiscipline.setFont(boldFont);
		buttonDiscipline.setToolTipText("Will make your pet unhappy (-30 happiness) but stops them from misbehaving. Consumes one action point.");
		buttonDiscipline.setBounds(82, 110, 100, 20);
		add(buttonDiscipline);
		
		JLabel favouriteToyTitle = new JLabel("Favourite Toy");
		favouriteToyTitle.setHorizontalAlignment(SwingConstants.CENTER);
		favouriteToyTitle.setVerticalAlignment(SwingConstants.TOP);
		favouriteToyTitle.setFont(boldFont);
		favouriteToyTitle.setBounds(12, 142, 110, 28);
		add(favouriteToyTitle);
		
		favouriteToyLabel = new JLabel("");
		favouriteToyLabel.setHorizontalAlignment(SwingConstants.CENTER);
		favouriteToyLabel.setVerticalAlignment(SwingConstants.TOP);
		favouriteToyLabel.setFont(semiBoldFont);
		favouriteToyLabel.setBounds(12, 158, 110, 16);
		add(favouriteToyLabel);
		
		favouriteToyIcon = new JLabel("");
		favouriteToyIcon.setIcon(new ImageIcon(Game.class.getResource("/images/species/Cat.png")));
		favouriteToyIcon.setBounds(20, 180, 94, 94);
		add(favouriteToyIcon);
		
		JLabel favouriteFoodTitle = new JLabel("Favourite Food");
		favouriteFoodTitle.setHorizontalAlignment(SwingConstants.CENTER);
		favouriteFoodTitle.setVerticalAlignment(SwingConstants.TOP);
		favouriteFoodTitle.setFont(boldFont);
		favouriteFoodTitle.setBounds(132, 142, 110, 28);
		add(favouriteFoodTitle);
		
		favouriteFoodLabel = new JLabel("");
		favouriteFoodLabel.setHorizontalAlignment(SwingConstants.CENTER);
		favouriteFoodLabel.setVerticalAlignment(SwingConstants.TOP);
		favouriteFoodLabel.setFont(semiBoldFont);
		favouriteFoodLabel.setBounds(134, 158, 110, 16);
		add(favouriteFoodLabel);
		
		favouriteFoodIcon = new JLabel("");
		favouriteFoodIcon.setIcon(new ImageIcon(Game.class.getResource("/images/species/Bunny.png")));
		favouriteFoodIcon.setBounds(142, 180, 94, 94);
		add(favouriteFoodIcon);
		
		JButton buttonPlay = new JButton("Play");
		buttonPlay.setToolTipText("Play with the selected pet. You will need to select a toy. This will use 1 action point.");
		buttonPlay.setBounds(14, 298, 110, 35);
		add(buttonPlay);
		
		JButton buttonFeed = new JButton("Feed");
		buttonFeed.setToolTipText("Feed the selected pet. You will need to select a food. This will use 1 action point.");
		buttonFeed.setBounds(134, 298, 110, 35);
		add(buttonFeed);
		
		JButton buttonRest = new JButton("Rest");
		buttonRest.setToolTipText("Have the selected pet rest. This will use 1 action point.");
		buttonRest.setBounds(256, 298, 110, 35);
		add(buttonRest);
		
		JButton buttonToilet = new JButton("Toilet");
		buttonToilet.setToolTipText("Send the selected pet to the toilet. This will use 1 action point.");
		buttonToilet.setBounds(378, 298, 110, 35);
		add(buttonToilet);
		
		hungerSlider = new PetStatDisplayer(boldFont, semiBoldFont, "Hunger", new ImageIcon(Game.class.getResource("/images/sliders/hunger.png")), 
				"How hungry this pet is. Once hunger reaches the orange region, the pet will begin to starve.", new Color(227, 66, 52), 0, 100, 0, 8);
		hungerSlider.setBounds(257, 12, 232, 50);
		add(hungerSlider);
		
		energySlider = new PetStatDisplayer(boldFont, semiBoldFont, "Energy", new ImageIcon(Game.class.getResource("/images/sliders/energy.png")), 
				"How much energy this pet has. Once energy reaches the red region, the pet will have a chance to die at the end of each turn.", new Color(30, 224, 220), 0, 100, 6, 0);
		energySlider.setBounds(257, 72, 232, 50);
		add(energySlider);
		
		happinessSlider = new PetStatDisplayer(boldFont, semiBoldFont, "Happiness", new ImageIcon(Game.class.getResource("/images/sliders/happiness.png")), 
				"How happy this pet is.", new Color(255, 180, 0), 0, 100, 3, 0);
		happinessSlider.setBounds(257, 132, 232, 50);
		add(happinessSlider);
		
		weightSlider = new PetStatDisplayer(boldFont, semiBoldFont, "Weight", new ImageIcon(Game.class.getResource("/images/sliders/weight.png")), 
				"How much this pet weighs.", new Color(127, 127, 127), 0, 100, 6, 6);
		weightSlider.setBounds(257, 192, 232, 50);
		add(weightSlider);
		
		JLabel petInfoBackground = new JLabel(new ImageIcon(Game.class.getResource("/images/backs/petInteract.png")));
		petInfoBackground.setBounds(0, 0, 500, 345);
		add(petInfoBackground);
	}

	public void setPet(Pet activePet) {
		speciesLabel.setText(activePet.getSpecies().getName());
		if (activePet.isHealthy())
			healthyLabel.setText("This pet is healthy");
		else
			healthyLabel.setText("This pet is sick");
		buttonCure.setEnabled(!activePet.isHealthy());
		
		if (activePet.isBehaving())
			behavingLabel.setText("This pet is behaving");
		else
			behavingLabel.setText("This pet is misbehaving");
		buttonDiscipline.setEnabled(!activePet.isBehaving());
		
		hungerSlider.setStat(activePet.getHunger());
		energySlider.setStat(activePet.getEnergy());
		happinessSlider.setStat(activePet.getHappiness());
		int optWeight = activePet.getSpecies().getOptimumWeight();
		weightSlider.setMinMax(optWeight*1/3, optWeight*5/3);
		weightSlider.setStat(activePet.getWeight());
		
		favouriteToyLabel.setText(activePet.getFavouriteToy().getName());
		favouriteFoodLabel.setText(activePet.getFavouriteFood().getName());
	}
}
