package gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import assetshandler.AssetsLoader;
import assetshandler.AssetsSaver;
import model.FoodType;
import model.Species;
import model.ToyType;

/**
 * A controller and viewer for the application. Loads and shows the various game panels.
 * @author Andrew Davidson (ada130)
 */
public class GuiRunner {
	private Font poppins, sourceSansPro, sourceSansProSemibold, sourceSansProBold;
	private Species[] species;
	private ToyType[] toyTypes;
	private FoodType[] foodTypes;
	
	private JFrame frame;
	private MainMenu mainMenu;
	private GameSetup gameSetup;
	private AssetCreator assetCreator;
	private Game game;
	private HelpPanel helpPanel;

	/**
	 * Launch the application.
	 * @param args
	 * Arguments to run the application with. Not used
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiRunner window = new GuiRunner();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Load fonts, species, toy types, and food types. Initialises the gui and loads the main menu.
	 */
	public GuiRunner() {
		try {
			species = AssetsLoader.loadCustomSpeciesFile(GuiRunner.class.getResourceAsStream("/default_species.txt"));
			toyTypes = AssetsLoader.loadCustomToyTypesFile(GuiRunner.class.getResourceAsStream("/default_toytypes.txt"));
			foodTypes = AssetsLoader.loadCustomFoodTypesFile(GuiRunner.class.getResourceAsStream("/default_foodtypes.txt"));
		}
		catch (FileNotFoundException exc) {
			JOptionPane.showMessageDialog(frame, 
					"Default asset resources could not be found." + exc.getMessage(),
					"Loading Default Assets Error", JOptionPane.ERROR_MESSAGE);
		}
		catch (IOException exc) {
			JOptionPane.showMessageDialog(frame, 
					"An error occurred while loading default assets: \n" + exc.getMessage(),
					"Loading Default Assets Error", JOptionPane.ERROR_MESSAGE);
		}
		
		poppins = loadFont(GuiRunner.class.getResourceAsStream("/fonts/Poppins/Poppins-Regular.ttf"));
		sourceSansPro = loadFont(GuiRunner.class.getResourceAsStream("/fonts/Source_Sans_Pro/SourceSansPro-Regular.ttf"));
		sourceSansProSemibold = loadFont(GuiRunner.class.getResourceAsStream("/fonts/Source_Sans_Pro/SourceSansPro-Semibold.ttf"));
		sourceSansProBold = loadFont(GuiRunner.class.getResourceAsStream("/fonts/Source_Sans_Pro/SourceSansPro-Bold.ttf"));
		
		initialise();
		loadMainMenu();
		helpPanel = new HelpPanel(sourceSansProBold.deriveFont(15f), sourceSansPro.deriveFont(15f));
		frame.add(helpPanel);
		mainMenu.setVisible(true);
	}
	
	/**
	 * Initialises the frame at the correct size with nothing in it. Sets window label and icon.
	 */
	private void initialise() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.getContentPane().setPreferredSize(new Dimension(800, 600));
		frame.pack();
		frame.setTitle("Virtual Pets");
		frame.setIconImage(new ImageIcon(GuiRunner.class.getResource("/images/FrameIcon.png")).getImage());
	};
	
	/**
	 * Load a font.
	 * @param file
	 * An InputStream to the desired font
	 * @return
	 * The loaded font
	 */
	public Font loadFont(InputStream file) {
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, file);
			return font;
		}
		catch (FontFormatException e) {
			System.out.println("Font must be a truetype font.");
			return null;
		}
		catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to load font.");
			return null;
		}
		finally {
			try {
				file.close();
			}
			catch (IOException e) {
			}
		}
	}
	
	/**
	 * Load the main menu screen - has buttons that allow access to the different parts of the program.
	 */
	private void loadMainMenu() {
		mainMenu = new MainMenu(poppins.deriveFont(84f), sourceSansProSemibold.deriveFont(16f));

		// Switch to gameSetup if new game is clicked
		mainMenu.getNewGameButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				loadGameSetup();
				gameSetup.setVisible(true);
				mainMenu.setVisible(false);
			}
		});
		
		// Load a game if the 'Load Game' button is clicked.
		mainMenu.getLoadGameButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				// Open up a open file dialog for choosing a save file to load
				JFileChooser openFileDialog = new JFileChooser();
				openFileDialog.setCurrentDirectory(new File("."));
				openFileDialog.setDialogTitle("Load Game");
				if (openFileDialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					try {
						// If file selected, try to read game from that file and resume.
						File saveFile = openFileDialog.getSelectedFile();
						loadGame();
						Game savedGame = SaveGameHandler.readGameFromFile(saveFile);
						game.setVisible(true);
						mainMenu.setVisible(false);
						game.resume(savedGame);
					}
					// If failed, show popup error with the reason why.
					catch (NullPointerException exc) {
						JOptionPane.showMessageDialog(frame, 
								"Loading game failed due to an invalid/missing save file being provided.", 
								"Save File Loading Error", JOptionPane.ERROR_MESSAGE);
					}
					catch (IOException exc) {
						JOptionPane.showMessageDialog(frame, 
								"Loading game failed due to the save file being invalid or a problem occurring while reading it.", 
								"Save File Loading Error", JOptionPane.ERROR_MESSAGE);
					}
					catch (ClassNotFoundException exc) {
						JOptionPane.showMessageDialog(frame, 
								"Loading game failed as the version of the save file does not match this version of the game.", 
								"Save File Loading Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		//Switch to AssetCreator if create new asset is clicked
		mainMenu.getCreateNewAssetButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				loadAssetCreator();
				assetCreator.setVisible(true);
				mainMenu.setVisible(false);
			}
		});
		
		//Save current asset configuration to a file
		mainMenu.getSaveAssetsButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JFileChooser saveFileDialog = new JFileChooser();
				saveFileDialog.setCurrentDirectory(new File("."));
				saveFileDialog.setDialogTitle("Choose a folder to store assets in");
				saveFileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				saveFileDialog.setAcceptAllFileFilterUsed(false);
				
				if (saveFileDialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					try {
						File customAssetFile = saveFileDialog.getSelectedFile();
						AssetsSaver.writeAssetsToFile(customAssetFile, species, foodTypes, toyTypes);
					}
					catch (IOException exc) {
						JOptionPane.showMessageDialog(frame, 
								"Saving assets failed due to: \n" + exc.getMessage(), 
								"Saving Assets Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		// Load asset configuration from a folder
		mainMenu.getLoadAssetsButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				// If load asset config button clicked, open up open file dialog to choose the folder where assets are stored.
				JFileChooser openFileDialog = new JFileChooser();
				openFileDialog.setCurrentDirectory(new File("."));
				openFileDialog.setDialogTitle("Choose a folder to load assets from");
				openFileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				openFileDialog.setAcceptAllFileFilterUsed(false);
				
				if (openFileDialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File customAssetFolder = openFileDialog.getSelectedFile();
					try {
						// Try to load assets from this folder, then separate into different types.
						Object[][] customAssets = AssetsLoader.loadCustomAssetsFile(customAssetFolder);
						Species[] customSpecies = Arrays.copyOf(customAssets[0], customAssets[0].length, Species[].class);
						FoodType[] customFoodTypes = Arrays.copyOf(customAssets[1], customAssets[1].length, FoodType[].class);
						ToyType[] customToyTypes = Arrays.copyOf(customAssets[2], customAssets[2].length, ToyType[].class);
						
						// Check if any Species loaded from the file conflict with those already in the game, and if so, disregard them.
						// Otherwise, add them to the list of species.
						ArrayList<Species> newSpeciesList = new ArrayList<Species>(Arrays.asList(species));
						for (Species custom : customSpecies) {
							boolean conflict = false;
							for (Species old : species)
								if (custom.getName().equals(old.getName()))
									conflict = true;
							if (!conflict)
								newSpeciesList.add(custom);
						}
						// Check if any FoodTypes loaded from the file conflict with those already in the game, and if so, disregard them.
						// Otherwise, add them to the list of FoodTypes.
						ArrayList<FoodType> newFoodTypesList = new ArrayList<FoodType>(Arrays.asList(foodTypes));
						for (FoodType custom : customFoodTypes) {
							boolean conflict = false;
							for (FoodType old : foodTypes)
								if (custom.getName().equals(old.getName()))
									conflict = true;
							if (!conflict)
								newFoodTypesList.add(custom);
						}
						// Check if any ToyTypes loaded from the file conflict with those already in the game, and if so, disregard them.
						// Otherwise, add them to the list of ToyTypes.
						ArrayList<ToyType> newToyTypesList = new ArrayList<ToyType>(Arrays.asList(toyTypes));
						for (ToyType custom : customToyTypes) {
							boolean conflict = false;
							for (ToyType old : toyTypes)
								if (custom.getName().equals(old.getName()))
									conflict = true;
							if (!conflict)
								newToyTypesList.add(custom);
						}
						
						// Finally, set private variables to the newly combined lists to update them.
						species = newSpeciesList.toArray(new Species[newSpeciesList.size()]);
						foodTypes = newFoodTypesList.toArray(new FoodType[newFoodTypesList.size()]);
						toyTypes = newToyTypesList.toArray(new ToyType[newToyTypesList.size()]);
					}
					// If loading saved asset files failed, show popup error with the reason why.
					catch (FileNotFoundException exc) {
						JOptionPane.showMessageDialog(frame, 
								"The specified custom asset files could not be found or are invalid.", 
								"Loading Assets Error", JOptionPane.ERROR_MESSAGE);
					}
					catch (IOException exc) {
						JOptionPane.showMessageDialog(frame, 
								"Loading of custom assets failed due to: \n" + exc.getMessage(),
								"Loading Assets Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		//Open help panel if help pressed
		mainMenu.getHelpButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				helpPanel.setActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						helpPanel.setVisible(false);
						mainMenu.setVisible(true);
					}
				});
				helpPanel.setVisible(true);
				mainMenu.setVisible(false);
			}
		});
		
		//Quit if quit is pressed
		mainMenu.getQuitButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				frame.dispose();
			}
		});
		
		frame.getContentPane().add(mainMenu);
	}
	
	/**
	 * Load the game setup screen and store it. The game setup screen allows the user to specify players and their pets, and the attributes of all of these. Game variables such as number of days are also set.
	 */
	private void loadGameSetup() {
		gameSetup = new GameSetup(species, toyTypes, foodTypes, poppins.deriveFont(48f), sourceSansProBold.deriveFont(14f), sourceSansProSemibold.deriveFont(14f), sourceSansPro.deriveFont(14f));

		//If all fields are filled, use the inputs to create and start a new game
		gameSetup.getDoneButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (gameSetup.fieldsFilled()) {
					loadGame();
					if (gameSetup.setGamePlayers(game)) {
						gameSetup.setVisible(false);
						game.setVisible(true);
					}
				}
			}
		});
		
		//Go back to the main menu if back is clicked
		gameSetup.getBackButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				gameSetup.setVisible(false);
				mainMenu.setVisible(true);
			}
		});
		
		frame.getContentPane().add(gameSetup);
	}
	
	/**
	 * Load the asset creation screen and store it. The asset creation screen allows players to add custom new species, toy types and food types.
	 */
	private void loadAssetCreator() {
		assetCreator = new AssetCreator(poppins.deriveFont(72f), sourceSansProSemibold.deriveFont(14f));
		
		//Go back to the main menu when back is pressed
		assetCreator.getBackButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				assetCreator.setVisible(false);
				
				//Load newly created species, toy types, and food types
				
				ArrayList<Species> newSpecies = assetCreator.getNewSpecies();
				if (newSpecies.size() > 0) {
					Species[] updatedSpecies = new Species[species.length+newSpecies.size()];
					for (int i=0; i<species.length; i++)
						updatedSpecies[i] = species[i];
					for (int i=0; i<newSpecies.size(); i++)
						updatedSpecies[species.length+i] = newSpecies.get(i);
					species = updatedSpecies;
				}
				
				ArrayList<ToyType> newToyTypes = assetCreator.getNewToyTypes();
				if (newToyTypes.size() > 0) {
					ToyType[] updatedToyTypes = new ToyType[toyTypes.length+newToyTypes.size()];
					for (int i=0; i<toyTypes.length; i++)
						updatedToyTypes[i] = toyTypes[i];
					for (int i=0; i<newToyTypes.size(); i++)
						updatedToyTypes[toyTypes.length+i] = newToyTypes.get(i);
					toyTypes = updatedToyTypes;
				}
				
				ArrayList<FoodType> newFoodTypes = assetCreator.getNewFoodTypes();
				if (newFoodTypes.size() > 0) {
					FoodType[] updatedFoodTypes = new FoodType[foodTypes.length+newFoodTypes.size()];
					for (int i=0; i<foodTypes.length; i++)
						updatedFoodTypes[i] = foodTypes[i];
					for (int i=0; i<newFoodTypes.size(); i++)
						updatedFoodTypes[foodTypes.length+i] = newFoodTypes.get(i);
					foodTypes = updatedFoodTypes;
				}
				
				mainMenu.setVisible(true);
			}
		});
		
		frame.getContentPane().add(assetCreator);
	}
	
	/**
	 * Load the game screen and store it. The game screen is where game execution occurs - players take turns to interact with their pets and shop, and a round overview is shown at the end of each turn.
	 */
	private void loadGame() {
		RoundOverview roundOverview = new RoundOverview(poppins.deriveFont(36f), poppins.deriveFont(22f), sourceSansProBold.deriveFont(16f));
		
		//When the game ends, show the main menu
		roundOverview.getButtonEndGame().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				roundOverview.setVisible(false);
				mainMenu.setVisible(true);
			}
		});
		
		frame.getContentPane().add(roundOverview);
		
		//When exit to main menu is pressed, switch to the main menu
		ActionListener exitToMainMenu = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainMenu.setVisible(true);
				game.setVisible(false);
			}
		};
		
		//When exit to desktop is pressed, quit
		ActionListener exitToDesktop = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				frame.dispose();
			}
		};
		
		game = new Game(toyTypes, foodTypes, poppins.deriveFont(48f), poppins.deriveFont(18f), sourceSansProBold.deriveFont(14f), 
				sourceSansProSemibold.deriveFont(14f), sourceSansPro.deriveFont(14f), roundOverview, exitToMainMenu, exitToDesktop);
		
		//Open help panel if help pressed
		game.getHelpButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				helpPanel.setActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						helpPanel.setVisible(false);
						game.setVisible(true);
					}
				});
				helpPanel.setVisible(true);
				game.setVisible(false);
			}
		});
		
		frame.getContentPane().add(game);
	}
}
