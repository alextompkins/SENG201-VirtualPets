package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import model.FoodType;
import model.Pet;
import model.Player;
import model.Toy;
import model.ToyType;

/**
 * The main game panel. Displays pet and player info, and allows the player to interact with pets and visit the store.
 * @author Andrew Davidson (ada130)
 * @author Alex Tompkins (ato47)
 */
public class Game extends JPanel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private ToyType[] toyTypes;
	private FoodType[] foodTypes;
	private Player[] players;
	private RoundOverview roundOverview;
	private InternalDialog currentDialog;
	private PetInteract petInteract;
	private int numberOfDays, incomePerTurn, currentDay;
	private PetTab[] petTabs = new PetTab[3];
	private final int[][] tabLayouts = {{175, 0, 0}, {100, 250, 0}, {25, 175, 325}};
	
	private Player activePlayer;
	private Pet activePet;
	
	private JLabel dayLabel, playerLabel;
	private JButton buttonShop, buttonEndTurn, buttonMenu;
	private JPanel menu;
	private JButton buttonSaveGame, buttonHelp, buttonExitToMainMenu, buttonExitToDesktop, buttonCloseMenu;

	private JLabel inventoryMoney;
	private boolean selectingToy = false;
	private boolean selectingFood = false;
	private JScrollPane foodInventoryScrollPane, toyInventoryScrollPane;
	private FoodInventory foodInventory;
	private ToyInventory toyInventory;
	
	private JPanel shopBase;
	private ShopPanel shopPanel;
	
	private Font semiBoldFont, boldFont, regularFont;

	/**
	 * Create the panel - the panel that the main game is played in. Players can interact with pets and visit the store.
	 * @param toyTypes
	 * All toy types in the game
	 * @param foodTypes
	 * All food types in the game
	 * @param titleFont
	 * The font for the day heading
	 * @param subtitleFont
	 * The font for subtitles
	 * @param boldFont
	 * The font for subheadings
	 * @param semiBoldFont
	 * The font for regular text
	 * @param regularFont
	 * The font for fields and spinners
	 * @param roundOverview
	 * A panel to display a round overview at the end of each round
	 * @param exitToMainMenu
	 * An action listener that will exit to main menu on action
	 * @param exitToDesktop
	 * An action listener that will exit to desktop on action
	 */
	public Game(ToyType[] toyTypes, FoodType[] foodTypes, Font titleFont, Font subtitleFont, Font boldFont, Font semiBoldFont, Font regularFont, 
			RoundOverview roundOverview, ActionListener exitToMainMenu, ActionListener exitToDesktop) {
		setLayout(null);
		setSize(800, 600);
		setVisible(false);

		//Initialise an empty internal dialog box
		currentDialog = new InternalDialog(boldFont);
		currentDialog.setBounds(275, 165, 250, 100);
		add(currentDialog);
		
		//Menu panel and buttons
		menu = new JPanel();
		menu.setBackground(Color.GRAY);
		menu.setBounds(250, 153, 300, 372);
		menu.setVisible(false);
		menu.setLayout(null);
		add(menu);
		
		// Save Game Button
		buttonSaveGame = new JButton("Save Game");
		buttonSaveGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// When clicked, brings up a save file dialog box to select your save file location.
				JFileChooser saveFileDialog = new JFileChooser();
				saveFileDialog.setCurrentDirectory(new File("."));
				saveFileDialog.setDialogTitle("Save Game");
				if (saveFileDialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					// Tries writing a save file of the current game.
					try {
						SaveGameHandler.writeGameToFile(getGame(), saveFileDialog.getSelectedFile());
					}
					// If writing a save file fails, bring up an error popup to show why.
					catch (NullPointerException exc) {
						JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(getGame()), 
								"Saving game file failed due to an invalid save file location being provided.", 
								"Save File Loading Error", JOptionPane.ERROR_MESSAGE);
					}
					catch (IOException exc) {
						JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(getGame()), 
								"Saving game file failed due to unexpected IO error when writing to the file.", 
								"Save File Loading Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		buttonSaveGame.setBounds(50, 36, 200, 50);
		menu.add(buttonSaveGame);

		buttonHelp = new JButton("Help");
		buttonHelp.setBounds(50, 98, 200, 50);
		menu.add(buttonHelp);
		
		buttonExitToMainMenu = new JButton("Exit to Main Menu");
		buttonExitToMainMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setMenuButtonsEnabled(false);
				currentDialog.setOptions("Are you sure? All unsaved", "progress will be lost.", true, true);

				currentDialog.getButtonOk().addActionListener(exitToMainMenu);
				
				currentDialog.getButtonCancel().addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setMenuButtonsEnabled(true);
					}
				});
				
				currentDialog.setVisible(true);
			}
		});
		buttonExitToMainMenu.setBounds(50, 160, 200, 50);
		menu.add(buttonExitToMainMenu);
		
		buttonExitToDesktop = new JButton("Exit to Desktop");
		buttonExitToDesktop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setMenuButtonsEnabled(false);
				currentDialog.setOptions("Are you sure? All unsaved", "progress will be lost.", true, true);

				currentDialog.getButtonOk().addActionListener(exitToDesktop);
				
				currentDialog.getButtonCancel().addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setMenuButtonsEnabled(true);
					}
				});
				
				currentDialog.setVisible(true);
			}
		});
		buttonExitToDesktop.setBounds(50, 222, 200, 50);
		menu.add(buttonExitToDesktop);
		
		buttonCloseMenu = new JButton("Close Menu");
		buttonCloseMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menu.setVisible(false);
				setButtonsEnabled(true);
			}
		});
		buttonCloseMenu.setBounds(50, 284, 200, 50);
		menu.add(buttonCloseMenu);

		//Shop background panel to be drawn to later, inventory scroll panes
		shopBase = new JPanel();
		shopBase.setLayout(null);
		shopBase.setVisible(false);
		shopBase.setBounds(5, 145, 500, 450);
		add(shopBase);
		
		foodInventoryScrollPane = new JScrollPane();
		foodInventoryScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		foodInventoryScrollPane.setBounds(510, 264, 286, 156);
		foodInventoryScrollPane.setOpaque(false);
		foodInventoryScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(foodInventoryScrollPane);
		
		toyInventoryScrollPane = new JScrollPane();
		toyInventoryScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		toyInventoryScrollPane.setBounds(510, 438, 286, 156);
		toyInventoryScrollPane.setOpaque(false);
		toyInventoryScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(toyInventoryScrollPane);
		
		for (int i=0; i<3; i++) {
			petTabs[i] = new PetTab(semiBoldFont);
			petTabs[i].setBounds(0, 100, 148, 155);
			petTabs[i].setVisible(false);
			
			final int finalI = i;
			petTabs[i].getClickDetector().addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					for (int i=0; i<3; i++) {
						if (i == finalI)
							petTabs[i].setBorder(new MatteBorder(4, 4, 0, 4, Color.WHITE));
						else
							petTabs[i].setBorder(null);
						setPet(finalI);
					}
				}
			});
			add(petTabs[i]);
		}
		petTabs[0].setBorder(new MatteBorder(4, 4, 0, 4, Color.WHITE));
		
		//Titles
		dayLabel = new JLabel("");
		dayLabel.setForeground(Color.WHITE);
		dayLabel.setFont(titleFont);
		dayLabel.setHorizontalAlignment(SwingConstants.CENTER);
		dayLabel.setBounds(250, 10, 300, 50);
		add(dayLabel);
		
		playerLabel = new JLabel("");
		playerLabel.setForeground(Color.WHITE);
		playerLabel.setFont(subtitleFont);
		playerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		playerLabel.setBounds(200, 60, 400, 30);
		add(playerLabel);
		
		//Pet interaction area and its button responders
		petInteract = new PetInteract(boldFont, semiBoldFont);
		petInteract.setBounds(0, 255, 500, 345);
		add(petInteract);

		petInteract.getButtonPlay().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setButtonsEnabled(false);
				currentDialog.setOptions("Click a toy from your inventory", "to use to play with your pet", false, true);
				selectingToy = true;
				toyInventory.setToyIconsEnabled(true);
				
				currentDialog.getButtonCancel().addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setButtonsEnabled(true);
						selectingToy = false;
						toyInventory.setToyIconsEnabled(false);
					}
				});
				
				currentDialog.setVisible(true);
			}
		});
		
		// Feed Pet Button
		petInteract.getButtonFeed().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Disables all other buttons when you have feed pet active
				setButtonsEnabled(false);
				currentDialog.setOptions("Click a food from your inventory", "to feed to your pet", false, true);
				// But enables the food icons buttons
				selectingFood = true;
				foodInventory.setFoodIconsEnabled(true);
				
				currentDialog.getButtonCancel().addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setButtonsEnabled(true);
						selectingFood = false;
						foodInventory.setFoodIconsEnabled(false);
					}
				});
				
				currentDialog.setVisible(true);
			}
		});
		
		// Rest Button
		petInteract.getButtonRest().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				activePet.sleep();
				refreshPetInfo();
			}
		});
		
		// Toilet Button
		petInteract.getButtonToilet().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				activePet.goToToilet();
				refreshPetInfo();
			}
		});
		
		// Cure Button
		petInteract.getButtonCure().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Only available if player has enough money to buy cure
				if (activePlayer.getMoney() >= 50) {
					activePet.cure();
					activePlayer.changeMoney(-50);
					inventoryMoney.setText("Money: $"+activePlayer.getMoney());
					refreshPetInfo();
				}
				else {
					setButtonsEnabled(false);
					currentDialog.setOptions("You do not have enough money", "to cure your pet", true, false);
					
					currentDialog.getButtonOk().addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							setButtonsEnabled(true);
						}
					});
					
					currentDialog.setVisible(true);
				}
			}
		});

		// Discipline Pet Button
		petInteract.getButtonDiscipline().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				activePet.discipline();
				refreshPetInfo();
			}
		});
		
		// Revive Button
		petInteract.getButtonRevive().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (activePlayer.getMoney() >= 100) {
					activePet.revive();
					activePlayer.changeMoney(-100);
					inventoryMoney.setText("Money: $"+activePlayer.getMoney());
					refreshPetInfo();
				}
				else {
					setButtonsEnabled(false);
					currentDialog.setOptions("You do not have enough money", "to revive your pet", true, false);
					
					currentDialog.getButtonOk().addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							setButtonsEnabled(true);
						}
					});
					
					currentDialog.setVisible(true);
				}
			}
		});
		
		// Shop Button
		buttonShop = new JButton(new ImageIcon(Game.class.getResource("/images/shop.png")));
		buttonShop.addActionListener(new ActionListener() {
			// When clicked, display shop screen
			public void actionPerformed(ActionEvent e) {
				displayShop();
			}
		});
		buttonShop.setBounds(531, 123, 65, 65);
		buttonShop.setToolTipText("Shop for food and toys for your pets.");
		add(buttonShop);
		
		// End Turn Button
		buttonEndTurn = new JButton(new ImageIcon(Game.class.getResource("/images/endTurn.png")));
		buttonEndTurn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Goes through checks to see if any pets have remaining action points.
				boolean requiresPrompt = false;
				for (Pet pet: activePlayer.getPets()) {
					if (pet.getActionPoints() > 0) {
						requiresPrompt = true;
						break;
					}
				}
				// If so, will prompt the player to make sure they want to end their turn.
				if (requiresPrompt) {
					setButtonsEnabled(false);
					currentDialog.setOptions("Some of your pets still have AP.", "Are you sure you want to end turn?", true, true);
					
					currentDialog.getButtonOk().addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							endTurn();
							setButtonsEnabled(true);
						}
					});
					
					currentDialog.getButtonCancel().addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							setButtonsEnabled(true);
						}
					});
					
					currentDialog.setVisible(true);
				}
				else
					endTurn();
			}
		});
		buttonEndTurn.setBounds(608, 110, 90, 90);
		buttonEndTurn.setToolTipText("End your turn.");
		add(buttonEndTurn);
		
		buttonMenu = new JButton(new ImageIcon(Game.class.getResource("/images/menu.png")));
		buttonMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setButtonsEnabled(false);
				menu.setVisible(true);
			}
		});
		buttonMenu.setBounds(710, 123, 65, 65);
		buttonMenu.setToolTipText("Open the menu.");
		add(buttonMenu);
		
		//Inventory backgrounds and titles
		JLabel inventoryLabel = new JLabel("Inventory");
		inventoryLabel.setForeground(Color.BLACK);
		inventoryLabel.setBounds(506, 212, 294, 20);
		inventoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
		inventoryLabel.setFont(subtitleFont);
		add(inventoryLabel);
		
		inventoryMoney = new JLabel("");
		inventoryMoney.setHorizontalAlignment(SwingConstants.CENTER);
		inventoryMoney.setFont(boldFont);
		inventoryMoney.setBounds(506, 233, 294, 20);
		add(inventoryMoney);
		
		JLabel inventoryBackground = new JLabel(new ImageIcon(Game.class.getResource("/images/backs/inventory.png")));
		inventoryBackground.setBounds(506, 255, 294, 345);
		add(inventoryBackground);
		
		JLabel inventoryLabelBackground = new JLabel("");
		inventoryLabelBackground.setIcon(new ImageIcon(Game.class.getResource("/images/backs/inventoryTitle.png")));
		inventoryLabelBackground.setHorizontalAlignment(SwingConstants.CENTER);
		inventoryLabelBackground.setFont(null);
		inventoryLabelBackground.setBounds(589, 210, 130, 45);
		add(inventoryLabelBackground);
		
		//Overall background
		JLabel background = new JLabel(new ImageIcon(Game.class.getResource("/images/gameBackground.png")));
		background.setBounds(0, 0, 800, 600);
		add(background);

		//Private variable saving and initialisation of the round overview screen
		roundOverview.initialise();
		roundOverview.getButtonContinue().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(true);
				roundOverview.setVisible(false);
			}
		});
		
		this.toyTypes = toyTypes;
		this.foodTypes = foodTypes;
		this.semiBoldFont = semiBoldFont;
		this.boldFont = boldFont;
		this.regularFont = regularFont;
		this.roundOverview = roundOverview;
	}
	
	//Getters
	public Game getGame() {
		return this;
	}
	
	public JButton getHelpButton() {
		return buttonHelp;
	}
	
	public JButton getExitToMainMenu() {
		return buttonExitToMainMenu;
	}
	
	public JButton getExitToDesktop() {
		return buttonExitToDesktop;
	}
	
	public InternalDialog getCurrentDialog() {
		return currentDialog;
	}
	//End getters
	
	/**
	 * Initialise the game environment for a new game.
	 * @param players
	 * The players to participate in this new game
	 * @param numberOfDays
	 * The number of days the game will go for
	 * @param incomePerTurn
	 * The player's income per day in the game
	 */
	public void initialise(Player[] players, int numberOfDays, int incomePerTurn) {
		this.players = players;
		this.numberOfDays = numberOfDays;
		this.incomePerTurn = incomePerTurn;
		currentDay = 1;
		dayLabel.setText("Day "+currentDay+" of "+numberOfDays);
		setTurn(0);
	}
	
	/**
	 * Initialise the game from a saved game file.
	 * @param foodTypes
	 * The food types that were in the saved game
	 * @param toyTypes
	 * The toy types that were in the saved game
	 * @param players
	 * The players that were in the saved game
	 * @param currentDay
	 * The day the saved game was at
	 * @param currentPlayerIndex
	 * The player the saved game was at
	 * @param numberOfDays
	 * The total number of days the saved game is to go for
	 * @param incomePerTurn
	 * The income per turn for each player
	 * @param previousScores
	 * The scores of the players for the previous round, used in round overview
	 */
	public void resume(Game savedGame) {
		this.foodTypes = savedGame.foodTypes;
		this.toyTypes = savedGame.toyTypes;
		this.players = savedGame.players;
		this.numberOfDays = savedGame.numberOfDays;
		this.incomePerTurn = savedGame.incomePerTurn;
		this.currentDay = savedGame.currentDay;
		this.activePlayer = savedGame.activePlayer;
		this.roundOverview.setPreviousRoundScores(savedGame.roundOverview.getPreviousScores());

		dayLabel.setText("Day "+currentDay+" of "+numberOfDays);
		for (int i = 0; i<players.length; i++)
			if (players[i].equals(activePlayer))
				setTurn(i);
	}
	
	/**
	 * Sets the turn to a certain player, changing the GUI as required.
	 * @param playerIndex
	 * The index in the players array to change to
	 */
	private void setTurn(int playerIndex) {
		activePlayer = players[playerIndex];
		playerLabel.setText(activePlayer.getName()+"'s turn. Score: "+activePlayer.getScore());
		inventoryMoney.setText("Money: $"+activePlayer.getMoney());
		
		refreshFoodInventory();
		refreshToyInventory();
		
		for (int i=0; i<3; i++) {
			if (i < activePlayer.getPets().length) {
				petTabs[i].setBounds(tabLayouts[activePlayer.getPets().length-1][i], 100, 148, 155);
				petTabs[i].setBorder(null);
				petTabs[i].setPet(activePlayer.getPets()[i]);
				petTabs[i].setVisible(true);
			}
			else
				petTabs[i].setVisible(false);
		}
		setPet(0);
		petTabs[0].setBorder(new MatteBorder(4, 4, 0, 4, Color.WHITE));
	}

	/**
	 * Set the selected pet, changing the GUI as necessary.
	 * @param petIndex
	 * The index of the newly selected pet in the pets array of the selected player
	 */
	private void setPet(int petIndex) {
		activePet = activePlayer.getPets()[petIndex];
		petInteract.setPet(activePet);
		petInteract.setButtonsEnabled(activePet.getActionPoints() > 0);
	}
	
	/**
	 * Ends the turn for the currently selected player, changes score, generates random events, and selects the next player.
	 */
	private void endTurn() {
		activePlayer.changeMoney(incomePerTurn);
		for (Pet pet: activePlayer.getPets())
			activePlayer.changeScore(pet.finishTurn());
		
		int currentPlayerIndex = 0;
		for (int i=0; i<players.length; i++)
			if (players[i] == activePlayer)
				currentPlayerIndex = i;
		currentPlayerIndex = (currentPlayerIndex+1)%players.length;
		if (currentPlayerIndex == 0) {
			if (currentDay == numberOfDays) {
				roundOverview.displayEndOfGame(currentDay, players);
				setVisible(false);
				roundOverview.setVisible(true);
			}
			else {
				roundOverview.displayEndOfRound(currentDay, players);
				setVisible(false);
				roundOverview.setVisible(true);
				currentDay += 1;
				dayLabel.setText("Day "+currentDay+" of "+numberOfDays);
			}
		}
		
		setTurn(currentPlayerIndex);
	}
	
	/**
	 * Refresh the pet tabs and pet interaction panel.
	 */
	private void refreshPetInfo() {
		for (int i=0; i<activePlayer.getPets().length; i++)
			petTabs[i].setPet(activePlayer.getPets()[i]);
		petInteract.setPet(activePet);
		if (activePet.getActionPoints() == 0)
			petInteract.setButtonsEnabled(false);
	}
	
	/**
	 * Refreshes the food inventory panel, generally used after any event that affects the contents 
	 * of the player's food inventory, such as buying food from the shop or feeding a pet.
	 */
	private void refreshFoodInventory() {
		foodInventory = new FoodInventory(activePlayer.getFood(), semiBoldFont);
		foodInventory.setPreferredSize(new Dimension(269, ((activePlayer.getFood().size()+2)/3)*90));
		foodInventoryScrollPane.setViewportView(foodInventory);
		
		HashMap<FoodType, FoodInventoryIcon> foodIcons = foodInventory.getFoodIcons();
		for (FoodType food : foodIcons.keySet()) {
			foodIcons.get(food).getClickDetector().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// Add a listener to each icon so that when the icon is clicked, if the player is selecting food 
					// to feed their pet, the food the icon represents will be chosen to be fed to the pet.
					if (selectingFood) {
						activePlayer.feed(activePet, food);
						selectingFood = false;
						setButtonsEnabled(true);
						currentDialog.setVisible(false);
						
						refreshFoodInventory();
						refreshPetInfo();
					}
				}
			});
		}
	}
	
	/**
	 * Refreshes the toy inventory panel, generally used after any event that affects the contents 
	 * of the player's toy inventory, such as buying a toy from the shop or playing with a pet.
	 */
	private void refreshToyInventory() {
		toyInventory = new ToyInventory(activePlayer.getToys(), semiBoldFont);
		toyInventory.setPreferredSize(new Dimension(269, ((activePlayer.getToys().size()+2)/3)*90));
		toyInventoryScrollPane.setViewportView(toyInventory);
		
		ToyInventoryIcon[] toyIcons = toyInventory.getToyIcons();
		for (ToyInventoryIcon icon : toyIcons) {
			icon.getClickDetector().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// Add a listener to each icon so that when the icon is clicked, if the player is selecting a 
					// toy to play with with their pet, the toy the icon represents will be chosen to be played with.
					if (selectingToy) {
						activePlayer.playWith(activePet, icon.getSpecificToy());
						selectingToy = false;
						setButtonsEnabled(true);
						currentDialog.setVisible(false);
						
						refreshToyInventory();
						refreshPetInfo();
					}
				}
			});
		}
	}
	
	/**
	 * Displays the shop panel over the left hand side of the screen. When called, will remove any
	 * previous shop screens created on the shopBase, to ensure no conflicts. Will refresh all its
	 * information when called, so generally also called to refresh the view after a player buys something.
	 */
	private void displayShop() {
		// Clear base component of any previous panels added to it.
		for (Component comp : shopBase.getComponents()) {
			shopBase.remove(comp);
		}
		// Make a new shopPanel to add to the shopBase.
		shopPanel = new ShopPanel(foodTypes, toyTypes, activePlayer.getMoney(), semiBoldFont, boldFont, regularFont);
		shopBase.add(shopPanel);
		shopBase.setVisible(true);
		setButtonsEnabled(false);
		shopPanel.enablePossibleBuyButtons(activePlayer.getMoney());
		shopPanel.getLeaveButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Add listener so that the shop quits when leave button clicked.
				shopBase.setVisible(false);
				setButtonsEnabled(true);
			}
		});
		
		// Adding listeners to each food for sale in the shop
		for (ShopFoodDisplayer foodDisplay : shopPanel.getFoodsForSale()) {
			foodDisplay.getBuyButton().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// For each food item for sale in the shop, add a listener so that it 
					// gets bought when its buy button is clicked.
					if (activePlayer.getMoney() >= foodDisplay.getFoodType().getPrice()) {
						activePlayer.changeMoney(-foodDisplay.getFoodType().getPrice());
						activePlayer.addFood(foodDisplay.getFoodType());
						inventoryMoney.setText("Money: $"+activePlayer.getMoney());
						
						Point buyFoodScrollPosition = shopPanel.getBuyFoodScrollPane().getViewport().getViewPosition();
						Point buyToysScrollPosition = shopPanel.getBuyToysScrollPane().getViewport().getViewPosition();
						shopBase.setVisible(false);
						displayShop();

						shopPanel.getBuyFoodScrollPane().getViewport().setViewPosition(buyFoodScrollPosition);
						shopPanel.getBuyToysScrollPane().getViewport().setViewPosition(buyToysScrollPosition);
						refreshFoodInventory();
					}
				}
			});
		}
		
		// Adding listeners to each toy for sale in the shop
		for (ShopToyDisplayer toyDisplay : shopPanel.getToysForSale()) {
			toyDisplay.getBuyButton().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// For each toy for sale in the shop, add a listener so that it
					// gets bought when its buy button is clicked.
					if (activePlayer.getMoney() >= toyDisplay.getToyType().getPrice()) {
						activePlayer.changeMoney(-toyDisplay.getToyType().getPrice());
						activePlayer.addToy(new Toy(toyDisplay.getToyType()));
						inventoryMoney.setText("Money: $"+activePlayer.getMoney());
						
						Point buyFoodScrollPosition = shopPanel.getBuyFoodScrollPane().getViewport().getViewPosition();
						Point buyToysScrollPosition = shopPanel.getBuyToysScrollPane().getViewport().getViewPosition();
						shopBase.setVisible(false);
						displayShop();
						
						shopPanel.getBuyFoodScrollPane().getViewport().setViewPosition(buyFoodScrollPosition);
						shopPanel.getBuyToysScrollPane().getViewport().setViewPosition(buyToysScrollPosition);
						refreshToyInventory();
					}
				}
			});
		}
	}
	
	/**
	 * Set whether the game buttons will be enabled or not.
	 * @param enabled
	 * Whether the game buttons will be enabled
	 */
	private void setButtonsEnabled(boolean enabled) {
		buttonShop.setEnabled(enabled);
		buttonEndTurn.setEnabled(enabled);
		buttonMenu.setEnabled(enabled);
		if (activePet.getActionPoints() > 0)
			petInteract.setButtonsEnabled(enabled);
		for (PetTab petTab: petTabs)
			petTab.setButtonEnabled(enabled);
	}
	
	/**
	 * Set whether the game menu buttons are enabled or not.
	 * @param enabled
	 * Whether the buttons will be enabled
	 */
	private void setMenuButtonsEnabled(boolean enabled) {
		buttonSaveGame.setEnabled(enabled);
		buttonHelp.setEnabled(enabled);
		buttonExitToMainMenu.setEnabled(enabled);
		buttonExitToDesktop.setEnabled(enabled);
		buttonCloseMenu.setEnabled(enabled);
	}
}
