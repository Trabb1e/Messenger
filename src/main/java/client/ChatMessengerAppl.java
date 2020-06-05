package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;

public class ChatMessengerAppl extends JFrame {
    public static final short DELAY = 100;
    public static final short PERIOD = 1000;
    final static Logger LOGGER = LogManager.getLogger(ChatMessengerAppl.class);

    private static final Model MODEL;
    private static final Controller CONTROLLER;
    private static final ViewFactory VIEWS;
    public static final int FRAME_WIDTH = 900;
    public static final int FRAME_HEIGHT = 600;

    static {
        MODEL = Model.getInstance();
        CONTROLLER = Controller.getInstance();
        VIEWS = ViewFactory.getInstance();
        LOGGER.trace("MVC instantiated" + MODEL + ";" + CONTROLLER + ";" + VIEWS);
    }

    private Timer timer;

    public ChatMessengerAppl(){
        super();
        initialize();
    }

    public static void main(String[] args) {
        JFrame frame = new ChatMessengerAppl();
        frame.setVisible(true);
        frame.repaint();

    }

    private void initialize() {
        AbstractView.setParent(this);
        MODEL.setParent(this);
        MODEL.initialize();
        CONTROLLER.setParent(this);
        VIEWS.viewRegister("login", LoginPanelView.getInstance());
        VIEWS.viewRegister("chat", ChatPanelView.getInstance());
        timer = new Timer("Server request for update messages");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.setLocationRelativeTo(null);
        this.setTitle("Chat Messenger");
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(getLoginPanelView(), BorderLayout.CENTER);
        this.setContentPane(contentPanel);
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                if(!getModel().getCurrentUSer().equals("")){
                    Utility.offlineMaker();
                }
                e.getWindow().dispose();
            }
        });
    }

    private LoginPanelView getLoginPanelView() {
        LoginPanelView loginPanelView = VIEWS.getView("login");
        loginPanelView.initModel();
        return  loginPanelView;
    }

    static ChatPanelView getChatPanelView() {
        ChatPanelView chatPanelView = VIEWS.getView("chat");
        chatPanelView.initModel();
        return chatPanelView;
    }

    public static Model getModel() {
        return MODEL;
    }

    public static Controller getController() {
        return CONTROLLER;
    }

    public static ViewFactory getViews() {
        return VIEWS;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    private void showPanel(JPanel panel){
        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setVisible(true);
        panel.repaint();

    }

    public void showChatPanelView() {
        showPanel(getChatPanelView());
        getChatPanelView().getTextMessageField().requestFocusInWindow();
        getChatPanelView().getRootPane().setDefaultButton(getChatPanelView().getSendMessageButton());
        InputMap im = getChatPanelView().getSendMessageButton().getInputMap();
        im.put(KeyStroke.getKeyStroke("ENTER"), "pressed");
        im.put(KeyStroke.getKeyStroke("released ENTER"), "released");
    }

    public void showLoginPanelView() {
        showPanel(getLoginPanelView());
        getLoginPanelView().getUserNameField().requestFocusInWindow();
        getLoginPanelView().getRootPane().setDefaultButton(getLoginPanelView().getLoginButton());
        InputMap im = getLoginPanelView().getLoginButton().getInputMap();
        im.put(KeyStroke.getKeyStroke("ENTER"), "pressed");
        im.put(KeyStroke.getKeyStroke("released ENTER"), "released");
    }
}
