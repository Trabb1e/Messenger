package client;

import domain.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ChatPanelView extends AbstractView {
    final static Logger LOGGER = LogManager.getLogger(ChatPanelView.class);
    public static final String SEND_ACTION_COMMAND = "send";
    public static final String LOGOUT_ACTION_COMMAND = "logout";
    private JScrollPane messagesListPanel;
    private JTextPane messagesTextPane;
    private JPanel textMessagePanel;
    private JButton sendMessageButton;
    private JTextField textMessageField;
    private JButton logoutButton;
    private JLabel promptLabel;
    private JList<String> userList;
    private JLabel selectedUserLabel;
    private DefaultListModel<String> usersListModel;

    private ChatPanelView(){
        super();
        initialize();
    }

    public DefaultListModel<String> getUsersListModel() {
        if(usersListModel==null){
            usersListModel = new DefaultListModel<>();
        }
        return usersListModel;
    }

    public void updateUsersListModel(String user){
        getUsersListModel().addElement(user);
        getUserList().setModel(getUsersListModel());
        getUserList().repaint();
    }

    public static ChatPanelView getInstance() {
        return ChatPanelViewHolder.INSTANCE;
    }

    public void modelChangedNotification(String newMessages) {
        HTMLDocument document = (HTMLDocument) getMessagesTextPane().getStyledDocument();
        HTMLEditorKit kit = (HTMLEditorKit)getMessagesTextPane().getEditorKit();
        try {
            kit.insertHTML(document,document.getLength(),newMessages,0,0,null);
        } catch (BadLocationException | IOException e) {
            e.printStackTrace();
        }
        getMessagesTextPane().setCaretPosition(document.getLength());
    }

    private static class ChatPanelViewHolder {
        private static final ChatPanelView INSTANCE = new ChatPanelView();
    }

    @Override
    public void initialize() {
        this.setName("chatPanelView");
        this.setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        header.add(getPromptLabel(), BorderLayout.EAST);
        header.add(getLogoutButton(), BorderLayout.WEST);
        getSelectedUser().setText("  Choose a user you want to speak :D");
        header.setBackground(Color.PINK);
        header.add(getSelectedUser(), BorderLayout.CENTER);
        getMessagesTextPane().setBackground(new Color(255,213,107));
        getUserList().setBackground(new Color(206,254,164));
        getTextMessageField().setEnabled(false);
        getSendMessageButton().setEnabled(false);
        this.add(header, BorderLayout.NORTH);
        this.add(getMessagesListPanel(), BorderLayout.CENTER);
        this.add(getTextMessagePanel(), BorderLayout.SOUTH);
        this.add(getUserList(), BorderLayout.EAST);
        InputMap im = getSendMessageButton().getInputMap();
        im.put(KeyStroke.getKeyStroke("ENTER"), "pressed");
        im.put(KeyStroke.getKeyStroke("released ENTER"), "released");
    }

    @Override
    public void clearFields() {
        getMessagesTextPane().setText("");
        getTextMessageField().setText("");
        getSelectedUser().setText("  Choose a user you want to speak :D");
        getTextMessageField().setEnabled(false);
    }

    public void initModel(){
        parent.getModel().setLastMessageText("");
        getPromptLabel().setText(parent.getModel().getLoggedUser() + " ");
        getTextMessageField().requestFocusInWindow();
        parent.getRootPane().setDefaultButton(getSendMessageButton());
    }

    public JScrollPane getMessagesListPanel() {
        if(messagesListPanel == null){
             messagesListPanel = new JScrollPane(getMessagesTextPane());
             messagesListPanel.setSize(getMaximumSize());
             messagesListPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        }
        return messagesListPanel;
    }

    private JLabel getPromptLabel(){
        if (promptLabel == null){
            promptLabel = new JLabel();
            promptLabel.setText(parent.getModel().getLoggedUser() + " ");
        }
        return promptLabel;
    }

    public JLabel getSelectedUser() {
        if (selectedUserLabel == null){
            selectedUserLabel = new JLabel("");
        }
        return selectedUserLabel;
    }

    public JTextPane getMessagesTextPane() {
        if (messagesTextPane == null){
            messagesTextPane = new JTextPane();
            messagesTextPane.setContentType("text/html");
            messagesTextPane.setEditable(false);
            messagesTextPane.setName("messagesTextArea");
            ((DefaultCaret)messagesTextPane.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        }
        return messagesTextPane;
    }

    public JPanel getTextMessagePanel() {
        if (textMessagePanel == null){
            textMessagePanel = new JPanel();
            textMessagePanel.setLayout(new BoxLayout(textMessagePanel, BoxLayout.X_AXIS));
            addLabeledField(textMessagePanel, "Enter message", getTextMessageField());
            textMessagePanel.add(getSendMessageButton());
        }
        return textMessagePanel;
    }

    public JButton getSendMessageButton() {
        if (sendMessageButton == null){
            sendMessageButton = new JButton();
            sendMessageButton.setText("Send");
            sendMessageButton.setName("sendMessageButton");
            sendMessageButton.setActionCommand(SEND_ACTION_COMMAND);
            sendMessageButton.addActionListener(parent.getController());
        }
        return sendMessageButton;
    }

    public JTextField getTextMessageField() {
        if (textMessageField == null){
            textMessageField = new JTextField(12);
            textMessageField.setName("textMessageField");
        }
        return textMessageField;
    }

    public JButton getLogoutButton() {
        if (logoutButton == null){
            logoutButton = new JButton();
            logoutButton.setText("Logout");
            logoutButton.setName("logoutButton");
            logoutButton.setActionCommand(LOGOUT_ACTION_COMMAND);
            logoutButton.addActionListener(parent.getController());
        }
        return logoutButton;
    }


    public JList<String> getUserList() {
        if (userList == null) {
            userList = new JList(getUsersListModel());
            userList.setLayout(new BoxLayout(userList, BoxLayout.Y_AXIS));
            userList.setSize(getMaximumSize());
            userList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    ChatMessengerAppl.getModel().setCurrentUSer(userList.getSelectedValue());
                    Utility.messagesUpdate(parent);
                    getSelectedUser().setText(String.format("Selected user %s",
                             ChatMessengerAppl.getModel().getCurrentUSer()));
                    getMessagesTextPane().setText("");
                    parent.getModel().getWithCurrentUserMessages(parent.getModel().getMessages());
                    getTextMessageField().setEnabled(true);
                    getSendMessageButton().setEnabled(true);
                }
            });
        }
        return userList;
    }

    public void listRepaintWithOnlineUsers(){
        getUserList().setCellRenderer(new CellRendererWithOnlineUsers());
        getUserList().repaint();
    }

}
