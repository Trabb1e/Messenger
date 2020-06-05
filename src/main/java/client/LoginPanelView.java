package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

public class LoginPanelView extends AbstractView {
    final static Logger LOGGER = LogManager.getLogger(LoginPanelView.class);
    public static final String LOGIN_ACTION_COMMAND = "login";

    private JPanel loginPanel;
    private JPanel mainPanel;
    private JButton loginButton;
    private JTextField userNameField;
    private JTextField serverIpAddressField;
    private JLabel errorLabel;

    private LoginPanelView(){
        super();
        initialize();
    }

    public static LoginPanelView getInstance(){
        return LoginPanelViewHolder.INSTANCE;
    }

    private static class LoginPanelViewHolder{
        private static final LoginPanelView INSTANCE = new LoginPanelView();
    }

    @Override
    public void initialize() {
        this.setName("loginPanelView");
        this.setLayout(new BorderLayout());
        this.add(getLoginPanel(), BorderLayout.CENTER);
        clearFields();
        initModel();
        InputMap im = getLoginButton().getInputMap();
        im.put(KeyStroke.getKeyStroke("ENTER"), "pressed");
        im.put(KeyStroke.getKeyStroke("released ENTER"), "released");
    }

    @Override
    public void clearFields() {
        getErrorLabel().setVisible(false);
        getUserNameField().setText("");
        getServerIpAddressField().setText(parent.getModel().getServerIpAddress());
    }

    public void initModel(){
        parent.getModel().setCurrentUSer("");
        parent.getModel().setLoggedUser("");
        getUserNameField().requestFocusInWindow();
        parent.getRootPane().setDefaultButton(getLoginButton());

    }

    public JPanel getLoginPanel() {
        if (loginPanel == null){
            loginPanel = new JPanel();
            loginPanel.setLayout(new BorderLayout());
            loginPanel.add(getMainPanel(), BorderLayout.NORTH);
            addLabeledField(getMainPanel(), "name of user:", getUserNameField());
            addLabeledField(getMainPanel(), "server ip-address:", getServerIpAddressField());
            loginPanel.add(getLoginButton(), BorderLayout.SOUTH);

        }
        return loginPanel;
    }

    public JPanel getMainPanel() {
        if(mainPanel == null){
            mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        }
        return mainPanel;
    }

    public JButton getLoginButton() {
        if (loginButton == null){
            loginButton = new JButton();
            loginButton.setText("Login...");
            loginButton.setName("loginButton");
            loginButton.setActionCommand(LOGIN_ACTION_COMMAND);
            loginButton.addActionListener(parent.getController());
            
        }
        return loginButton;
    }

    public JTextField getUserNameField() {
        if (userNameField == null){
            userNameField = new JTextField(12);
            userNameField.setName("userNameField");

        }
        return userNameField;
    }

    public JTextField getServerIpAddressField() {
        if (serverIpAddressField== null){
            serverIpAddressField = new JTextField(12);
            serverIpAddressField.setName("serverIpAddressField");

        }
        return serverIpAddressField;
    }

    public JLabel getErrorLabel() {
        if (errorLabel == null){
            errorLabel = new JLabel("Wrong ip address or user name");
            errorLabel.setForeground(Color.red);

        }
        return errorLabel;
    }

    private void setErrorLabelText(String errorText) {
        getErrorLabel().setText(errorText);
    }
}
