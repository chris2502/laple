package fr.laple.controller.config;

import fr.laple.extensions.plugins.Plugins;
import fr.laple.extensions.plugins.IPlugin;
import fr.laple.extensions.plugins.PluginLoadingException;
import fr.laple.extensions.plugins.PluginLoadingFatalException;
import fr.laple.extensions.plugins.PluginTypeException;
import fr.laple.extensions.plugins.features.FeaturePluginConfigFileParser;
import fr.laple.extensions.plugins.features.IFeaturePlugin;
import fr.laple.extensions.plugins.languages.LanguagePluginConfigFileParser;
import fr.laple.model.datamodel.LapleDataModel;
import fr.laple.model.listable.IListable;
import fr.laple.model.listable.RootData;
import fr.laple.view.ListView;
import fr.laple.view.config.PluginConfigView;
import fr.laple.ztools.tabTools.TabTools;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the controller for the Plugin Configuration interface
 *
 * @see fr.laple.view.config.PluginConfigView
 * @author anthonyrey
 */
public class PluginConfigController implements ActionListener, ItemListener, ListSelectionListener, IListable {

    private PluginConfigView view;
    private RootData rootData;
    private JTabbedPane tabbedPane;
    private LapleDataModel model;

    private List<IPlugin> languagePlugins;
    private List<IFeaturePlugin> featurePlugins;

    public PluginConfigController()
    {
        this.view = new PluginConfigView();
        view.getAdd().addActionListener(this);
        view.getBack().addActionListener(this);
        view.getRemove().addActionListener(this);
        view.getPluginTypes().addItemListener(this);
        view.getPlugins().addListSelectionListener(this);

        view.getPluginTypes().addItem("Language plugin");
        view.getPluginTypes().addItem("Feature plugin");
    }

    /**
     * This is the ActionEvent handler for this class
     * There are 3 possible kind of actions to be called :
     *
     * - Back, return to the root listView
     * - Remove, will remove the selected plugin if the conditions are matched (not internal)
     * - Add, will add a plugin if the type is right (ie. not adding a feature to the language) and if not already added
     *
     * This method is throwing a lot of exceptions and handle them by itself
     *
     * @see fr.laple.extensions.plugins.Plugins
     * @param e An event from the GUI
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource().equals(view.getBack()))
        {
            TabTools.swapTab(tabbedPane, new ListView(model, rootData.getRootModel(), false,
                    rootData));
        }
        else if(e.getSource().equals(view.getRemove()))
        {
            IPlugin selectedPlugin = (IPlugin) view.getPlugins().getSelectedValue();
            if (selectedPlugin == null) {

                Plugins.pluginWarning("You must select a value to remove !");
            }
            else
            {

                if(!selectedPlugin.isInternal())
                {
                    try{

                        if(!languagePlugins.remove(selectedPlugin))
                        {
                            featurePlugins.remove(selectedPlugin);

                            FeaturePluginConfigFileParser fcfp = new FeaturePluginConfigFileParser();
                            fcfp.removePlugin(selectedPlugin);
                            updateFeaturePluginView();
                        }
                        else
                        {
                            languagePlugins.remove(selectedPlugin);

                            LanguagePluginConfigFileParser fcfp = new LanguagePluginConfigFileParser();
                            fcfp.removePlugin(selectedPlugin);
                            updateLanguagePluginView();
                        }
                    } catch (PluginLoadingException e1) {
                        Plugins.pluginWarning(e1.getMessage());
                    } catch (PluginLoadingFatalException e1) {
                        Plugins.pluginError(e1.getMessage());
                    }


                    view.getDescription().setText("");
                    Plugins.pluginMessage("Your changes will be taken into account upon next restart" +
                            "\nPlease restart LAPLE");
                }
                else
                {
                    Plugins.pluginWarning("You cannot remove this plugin, you may delete it from the config file " +
                            "at your own risk.");
                }
            }
        }
        else if(e.getSource().equals(view.getAdd()))
        {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.toString().endsWith(".jar");
                }

                @Override
                public String getDescription() {
                    return "Jar file";
                }
            });
            chooser.showOpenDialog(view);

            if(chooser.getSelectedFile() != null)
            {
                try
                {

                    IPlugin added = null;

                    if(view.getPluginTypes().getSelectedItem().equals("Language plugin"))
                    {
                        LanguagePluginConfigFileParser pcfp = new LanguagePluginConfigFileParser();
                        added = pcfp.addPlugin(chooser.getSelectedFile());
                        model.getAllDummyLanguagePlugins().add(added);
                        updateLanguagePluginView();
                    }
                    else
                    {

                        FeaturePluginConfigFileParser pcfp = new FeaturePluginConfigFileParser();
                        added =  pcfp.addPlugin(chooser.getSelectedFile());
                        model.getFeatures().add((IFeaturePlugin) added);
                        updateFeaturePluginView();
                    }

                    JOptionPane.showMessageDialog(this.view, "Plugin : \"" + added.getName() + "\" added with success." +
                            "\nPlease restart to see the changes");

                }
                catch (PluginLoadingException | PluginTypeException e1) {
                    Plugins.pluginWarning(e1.getMessage());
                } catch (PluginLoadingFatalException e1) {
                    Plugins.pluginError(e1.getMessage());
                }

            }
            else
            {
                Plugins.pluginWarning("Please select a file to load !");
            }
        }
    }

    /**
     * This method is handling what to do when an item state changes
     * (namely only the JComboBox from the view is affected)
     *
     * @param e
     */
    @Override
    public void itemStateChanged(ItemEvent e) {

        if(e.getItem().equals("Language plugin"))
            updateLanguagePluginView();
        else
           updateFeaturePluginView();

        view.getDescription().setText("");

        if(view.getPlugins().getComponentCount() > 0)
        {
            this.view.getPlugins().setSelectedIndex(0);
        }
    }

    /**
     * Update the JList containing the plugins with the current content of the DataModel
     * @see fr.laple.model.datamodel.LapleDataModel
     */
    private void updateLanguagePluginView()
    {
        if(languagePlugins != null && !languagePlugins.isEmpty() )
        {
            DefaultListModel<IPlugin> model = new DefaultListModel<>();
            languagePlugins.forEach(model::addElement);
            view.getPlugins().setModel(model);
        }
        else
        {
            view.getPlugins().setModel(new DefaultListModel<>());
        }
    }

    private void updateFeaturePluginView()
    {
        if(featurePlugins != null && !featurePlugins.isEmpty() )
        {
            DefaultListModel<IFeaturePlugin> model = new DefaultListModel<>();
            featurePlugins.forEach(model::addElement);
            view.getPlugins().setModel(model);
        }
        else
        {
            view.getPlugins().setModel(new DefaultListModel<>());
        }
    }

    /**
     * Handle what to do when the user select a plugin in the view's Jlist
     *
     * @param e A listSelectionEvent
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {

        IPlugin selectedPlugin = ((JList<IPlugin>) e.getSource()).getSelectedValue();
        if (selectedPlugin != null) {

            String desc = "Name : "+selectedPlugin.getName() + "\nPath : "+selectedPlugin.getPath() +
                    "\nDeveloper : " + selectedPlugin.getDeveloper() +
                    "\nOther credits : "+selectedPlugin.otherCredits() +
                    "\nVersion : " + selectedPlugin.getVersion() + "\n" + "Is internal : " + selectedPlugin.isInternal()
                    +"\n\n"+ selectedPlugin.getDescription();
            view.getDescription().setText(desc);
        }

    }

    /**
     * This is an implementation of a method from the IListable interface
     * When you press OK in the ListView, this method is called
     *
     * Here we fill the class variables with the values from the model
     * add the view to the tab
     *
     * @see fr.laple.model.listable.IListable
     * @see fr.laple.model.listable.RootData
     * @see fr.laple.view.ListView
     * @see fr.laple.view.LapleGUI
     *
     * @param tabbedPane The main tabbed pane from LapleGUI (view.getParent() usually)
     * @param model      The lapleDataModel containing all the application data
     * @param rootData   A rootDataObject containing the information to go back to the listView
     */
    @Override
    public void expectedBehavior(JTabbedPane tabbedPane, LapleDataModel model, RootData rootData) {

        this.rootData = rootData;
        this.tabbedPane = tabbedPane;
        this.model = model;

        featurePlugins = model.getFeatures();
        languagePlugins = new ArrayList<>();
        languagePlugins = model.getAllDummyLanguagePlugins();

        TabTools.swapTab(tabbedPane, this.getView());

        //changing state manually ;)
        this.view.getPluginTypes().setSelectedIndex(1);
        this.view.getPluginTypes().setSelectedIndex(0);
    }

    /**
     * Accessor method for the view
     * @return A PluginConfigView Object
     */
    public PluginConfigView getView() {
        return view;
    }

    @Override
    public String toString()
    {
        return "Plugin settings";
    }

}
