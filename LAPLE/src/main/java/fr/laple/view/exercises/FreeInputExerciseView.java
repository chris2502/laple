package fr.laple.view.exercises;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * View for freeInputExercises
 *
 * @author anthonyrey
 */
public class FreeInputExerciseView extends AbstractExerciseView {

    private JLabel question;
    private JLabel symbol;
    private JTextField answer;
    private JButton validationButton;

    public FreeInputExerciseView()
    {
        super();
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;

        question = new JLabel("What is the appropriate answer to :");
        symbol = new JLabel();
        symbol.setHorizontalAlignment(JLabel.CENTER);
        answer = new JTextField();
        validationButton = new JButton("Ok");

        this.add(question, gbc);
        gbc.gridy = 0;
        gbc.gridx = 1;
        this.add(getRemainingCount(),gbc);
        gbc.gridy = 1;
        gbc.gridx = 0;
        this.add(symbol, gbc);
        gbc.gridx = 1;
        this.add(getSuccesCount(), gbc );
        gbc.gridx = 0;
        gbc.gridy = 2;
        this.add(answer, gbc);
        gbc.gridy = 3;
        this.add(validationButton, gbc);
        gbc.gridx = 1;
        this.add(getNextButton(), gbc);
        gbc.gridx = 2;
        this.add(getBackButton(), gbc);


    }

    public void addActionListener(ActionListener al)
    {
        validationButton.addActionListener(al);
    }

    public JLabel getSymbol() {
        return symbol;
    }

    public void setSymbol(JLabel symbol) {
        this.symbol = symbol;
    }

    public JTextField getAnswer() {
        return answer;
    }

    @Override
    public void resetTheView() {
        answer.setEnabled(true);
        this.getNextButton().setEnabled(false);
        this.getValidationButton().setEnabled(true);
        this.getAnswer().setText("");
    }

    public JButton getValidationButton() {
        return validationButton;
    }
}
