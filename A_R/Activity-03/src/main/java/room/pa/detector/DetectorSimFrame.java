package room.pa.detector;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class DetectorSimFrame extends JFrame implements ActionListener {		
	
	private JButton enter;
	private JButton exit;
	private DetectorDeviceSimulator simulator;
	
	public DetectorSimFrame(DetectorDeviceSimulator simulator, String name){
		setTitle("Pres. Detect. Sensor: " + name);
		setSize(300,70);
		
		this.simulator = simulator;

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		setContentPane(mainPanel);
		
		enter = new JButton("Enter");
		exit = new JButton("Exit");

		enter.addActionListener(this);
		exit.addActionListener(this);

		mainPanel.add(enter);
		mainPanel.add(exit);
		simulator.updateValue(false);

		enter.setEnabled(true);
		exit.setEnabled(false);
	}
	
	
	public void display() {
		SwingUtilities.invokeLater(() -> {
			this.setVisible(true);
		});
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == enter){
			enter.setEnabled(false);
			exit.setEnabled(true);
			this.simulator.updateValue(true);
		} else if (e.getSource() == exit) {
			enter.setEnabled(true);
			exit.setEnabled(false);
			this.simulator.updateValue(false);
		}
	}

}