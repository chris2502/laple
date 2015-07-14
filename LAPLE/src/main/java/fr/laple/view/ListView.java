package fr.laple.view;

import fr.laple.controller.lessons.ListViewController;
import fr.laple.model.language.ILanguagePlugin;
import fr.laple.model.listable.IListable;
import fr.laple.model.listable.RootData;

import javax.swing.*;
import java.awt.*;

/**
 * Created by anthonyrey on 06/06/2015.
 */
public class ListView extends JPanel {

    private JList<IListable> list;
    private JButton validationButton;
    private JButton backButton;

    //display model is mandatory because you need to know what is to be shown
    public ListView(ILanguagePlugin model, java.util.List<IListable> displayModel, boolean hasBackButton, String title,
                   RootData rootData)
    {

        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel message = new JLabel(title);

        JPanel scrollPanel = new JPanel();
        scrollPanel.setLayout(new BorderLayout());
        JScrollPane scroll = new JScrollPane(scrollPanel);
        scroll.setHorizontalScrollBar(null);

        list = new JList<>();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPanel.add(list, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        validationButton = new JButton("Ok");
        backButton = new JButton("Return to root menu");
        buttons.add(validationButton);
        buttons.add(backButton);

        this.add(message, BorderLayout.PAGE_START);
        this.add(scroll, BorderLayout.CENTER);
        this.add(buttons, BorderLayout.PAGE_END);

        ListViewController controller = new ListViewController(model, displayModel, this, rootData);
        validationButton.addActionListener(controller);
        backButton.addActionListener(controller);

        if(!hasBackButton)
            backButton.setVisible(false);

    }

    public JList<IListable> getList()
    {
        return list;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JButton getValidationButton() {
        return validationButton;
    }
}