import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class UserInterface extends JFrame implements ActionListener {

    static JPanel p1;
    static JButton button;
    static JLabel testLabel, inputLabel, advancedLabel;
    JTextField input;
    private JLabel rangeSliderLabel1 = new JLabel();
    private JLabel rangeSliderValue1 = new JLabel();
    private JLabel rangeSliderLabel2 = new JLabel();
    private JLabel rangeSliderValue2 = new JLabel();
    private RangeSlider rangeSlider = new RangeSlider();

    public UserInterface() {
        p1 = new JPanel();
        p1.setLayout(new GridBagLayout());
        GridBagConstraints gb = new GridBagConstraints();
        gb.insets = new Insets(5, 5, 5, 5);

        button = new JButton("Summarize");
        button.addActionListener(this);

        testLabel = new JLabel();
        inputLabel = new JLabel("Enter a topic to summarize");

        input = new JTextField();
        input.setPreferredSize(new Dimension(200, 24));
        advancedLabel = new JLabel(
                "<html><font color=\"blue\"><u>Advanced Options</u></font></html>");
        advancedLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        setLayout(new GridBagLayout());

        rangeSliderLabel1.setText("Start Year");
        rangeSliderLabel2.setText("End Year");
        rangeSliderValue1.setHorizontalAlignment(JLabel.LEFT);
        rangeSliderValue2.setHorizontalAlignment(JLabel.LEFT);

        rangeSlider.setPreferredSize(new Dimension(240, rangeSlider
                .getPreferredSize().height));
        rangeSlider.setMinimum(2001);
        rangeSlider.setMaximum(2010);

        // Add listener to update display.
        rangeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                RangeSlider slider = (RangeSlider) e.getSource();
                rangeSliderValue1.setText(String.valueOf(slider.getValue()));
                rangeSliderValue2.setText(String.valueOf(slider.getUpperValue()));
            }
        });

        advancedLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                advancedLabel
                        .setText("<html><font color=\"purple\"><u>Advanced Options</u></font></html>");
                rangeSlider.setVisible(true);
                rangeSliderLabel1.setVisible(true);
                rangeSliderValue1.setVisible(true);
                rangeSliderLabel2.setVisible(true);
                rangeSliderValue2.setVisible(true);
            }
        });

        add(rangeSliderLabel1, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        add(rangeSliderValue1, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        add(rangeSliderLabel2, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        add(rangeSliderValue2, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        add(rangeSlider, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));

        gb.gridx = 0;
        gb.gridy = 0;
        p1.add(inputLabel, gb);
        gb.gridy = 1;
        p1.add(input, gb);
        gb.gridx = 1;
        p1.add(button, gb);
        p1.add(testLabel);
        gb.gridy = 2;
        p1.add(advancedLabel, gb);

        rangeSlider.setValue(2001);
        rangeSlider.setUpperValue(2010);

        rangeSlider.setVisible(false);
        rangeSliderLabel1.setVisible(false);
        rangeSliderValue1.setVisible(false);
        rangeSliderLabel2.setVisible(false);
        rangeSliderValue2.setVisible(false);
        rangeSliderValue1.setText(String.valueOf(rangeSlider.getValue()));
        rangeSliderValue2.setText(String.valueOf(rangeSlider.getUpperValue()));
        this.add(p1);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // On Clicking Button
        JOptionPane.showConfirmDialog(testLabel, input.getText());
    }

}