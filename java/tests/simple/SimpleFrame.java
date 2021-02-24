package tests.simple;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class SimpleFrame {
	private static final long serialVersionUID = 1L;
	   public static void main(String[] args) throws InvocationTargetException, InterruptedException {
	    	//EventQueue.invokeLater(
	          SwingUtilities.invokeLater(
	    			new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					JFrame frame = new JFrame("JFrame Example");  
					JPanel panel = new JPanel();  
					panel.setLayout(new FlowLayout());  
					JLabel label = new JLabel("JFrame By Example");  
					JButton button = new JButton();  
					button.setText("Button");  
					panel.add(label);  
					panel.add(button);  
					frame.add(panel);  
					frame.setSize(200, 300);  
					frame.setLocationRelativeTo(null);  
					//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setVisible(true);  
					
				}
			});
	   }
}
