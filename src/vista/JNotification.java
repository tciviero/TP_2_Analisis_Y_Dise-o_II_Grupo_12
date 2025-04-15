package vista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
public class JNotification extends JDialog implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton button;
	private JLabel label;
	
	public JNotification(String mensaje) {
		setLayout(null);
	    setBounds(440,10,410,210);
		label = new JLabel(mensaje);
		label.setBounds(115,60,200,30);
	    add(label);
		
		button = new JButton("Ok");
		button.addActionListener(this);
		button.setBounds(105,110,200,40);
	    add(button);
		
	
		label.setVisible(true);
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		dispose();
	}

}
