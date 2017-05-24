package application;

import java.awt.Font;
import java.util.HashMap;
import javax.swing.JPanel;

public class FoodInventory extends JPanel {
	private static final long serialVersionUID = -581342435839614651L;
	private static final int[] itemHorizontalPositions = {5, 95, 185};
	private static final int itemVerticalDistance = 90;
	
	private HashMap<FoodType, FoodInventoryItem> foodItems;

	/**
	 * Create the panel.
	 */
	public FoodInventory(HashMap<FoodType, Integer> playerFoods, Font semiBoldFont) {
		setLayout(null);
		
		int i = 1;
		for (FoodType food : playerFoods.keySet()) {
			// DEBUG ONLY
			System.out.println(food);
			System.out.println(semiBoldFont.toString());
			// END DEBUG
			foodItems.put(food, new FoodInventoryItem(food, playerFoods.get(food), semiBoldFont));
			foodItems.get(food).setFood(food);
			foodItems.get(food).setQuantity(playerFoods.get(food));
			foodItems.get(food).setBounds(itemHorizontalPositions[i%3], 
													i/3*itemVerticalDistance,
													85,85);
			add(foodItems.get(food));
		}
		
	}
}