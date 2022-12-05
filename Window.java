import java.awt.*;

import javax.swing.*;

public class Window extends Canvas{

	public Window(int width, int height, String title, Game game) {
		JFrame frame = new JFrame(title);
		JLabel background;

		frame.setPreferredSize(new Dimension(width, height));
		frame.setMaximumSize(new Dimension(width, height));
		frame.setMinimumSize(new Dimension(width, height));

		ImageIcon img = new ImageIcon("concert.png");

		background = new JLabel("",img , JLabel.CENTER);
		background.setBounds(0,0,1200,1000);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.add(game);
		frame.setVisible(true);
		game.start();
	}
	
}
