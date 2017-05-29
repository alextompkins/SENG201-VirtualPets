package application;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.SwingConstants;

/**
 * A panel that takes up the entire screen, showing a main menu. From the main menu, the user can start a new game, 
 * load a game, create new assets, read about how to play the game, and quit.
 * @author Andrew Davidson (ada130)
 */
public class MainMenu extends JPanel {
	private static final long serialVersionUID = 5563328853841424713L;
	private JButton buttonNewGame, buttonLoadGame, buttonCreateNewAsset, buttonHelp, buttonQuit;

	/**
	 * Create the main menu panel: title label and menu buttons.
	 * @param titleFont
	 * The font to use for the title
	 * @param buttonFont
	 * The font to use for the buttons
	 */
	public MainMenu(Font titleFont, Font buttonFont) {
		setLayout(null);
		setSize(800, 600);
		setVisible(false);

		//Main menu title, buttons, and background
		JLabel title = new JLabel("Virtual Pets");
		title.setFont(titleFont);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setBounds(150, 40, 500, 80);
		add(title);
		
		buttonNewGame = new JButton("New Game");
		buttonNewGame.setFont(buttonFont);
		buttonNewGame.setEnabled(true);
		buttonNewGame.setBounds(300, 150, 200, 60);
		add(buttonNewGame);
		
		buttonLoadGame = new JButton("Load Game");
		buttonLoadGame.setFont(buttonFont);
		buttonLoadGame.setEnabled(true);
		buttonLoadGame.setBounds(300, 221, 200, 60);
		add(buttonLoadGame);
		
		buttonCreateNewAsset = new JButton("Create New Asset");
		buttonCreateNewAsset.setFont(buttonFont);
		buttonCreateNewAsset.setEnabled(true);
		buttonCreateNewAsset.setBounds(300, 292, 200, 60);
		add(buttonCreateNewAsset);
		
		buttonHelp = new JButton("Help");
		buttonHelp.setFont(buttonFont);
		buttonHelp.setEnabled(true);
		buttonHelp.setBounds(300, 363, 200, 60);
		add(buttonHelp);
		
		buttonQuit = new JButton("Quit");
		buttonQuit.setFont(buttonFont);
		buttonQuit.setEnabled(true);
		buttonQuit.setBounds(300, 434, 200, 60);
		add(buttonQuit);
		
		JLabel backgroundImage = new JLabel("");
		backgroundImage.setIcon(new ImageIcon(MainMenu.class.getResource("/images/menuBackground.png")));
		backgroundImage.setBounds(0, 0, 800, 600);
		add(backgroundImage);
	}
	
	// Getters (GuiRunner must be able to access these buttons to add relevant ActionListeners to them)
	public JButton getNewGameButton() {
		return buttonNewGame;
	}
	
	public JButton getLoadGameButton() {
		return buttonLoadGame;
	}

	public JButton getCreateNewAssetButton() {
		return buttonCreateNewAsset;
	}
	
	public JButton getHelpButton() {
		return buttonHelp;
	}
	
	public JButton getQuitButton() {
		return buttonQuit;
	}
	// End Getters	
	
	/**
	 * Sets whether the menu buttons are visible. Used when displaying the help screen so that the buttons do not draw in
	 * front of the help panel at all.
	 * @param enabled
	 * Whether the buttons will be set to be visible
	 */
	public void setButtonsVisible(boolean enabled) {
		buttonNewGame.setVisible(enabled);
		buttonLoadGame.setVisible(enabled);
		buttonCreateNewAsset.setVisible(enabled);
		buttonHelp.setVisible(enabled);
		buttonQuit.setVisible(enabled);
	}
}
