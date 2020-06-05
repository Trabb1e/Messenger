package client;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import static client.ChatPanelView.*;
import static client.LoginPanelView.LOGIN_ACTION_COMMAND;

public class Controller implements ActionListener {
    private ChatMessengerAppl parent;
    private Command command;

    private Controller(){}

    private static class ControllerHolder{
        private static final Controller INSTANCE = new Controller();
    }

    public static Controller getInstance() {
        return ControllerHolder.INSTANCE;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            action(e);
        } catch (ParseException ex) {
            LOGGER.error(ex.getMessage());
        }
        command.execute();
    }

    private void action(ActionEvent e) throws ParseException {
        String commandName = e.getActionCommand();
        switch (commandName){
            case LOGIN_ACTION_COMMAND: {
                LoginPanelView view = Utility.findParent(
                        (Component) e.getSource(), LoginPanelView.class);
                if (//!EmailValidator.getInstance().isValid(view.getUserNameField().getText())
                        (view.getUserNameField().getText() == "") ||
                        !InetAddressValidator.getInstance().isValid(view.getServerIpAddressField().getText())) {
                    command = new LoginErrorCommand(view);
                } else {
                    parent.getModel().setLoggedUser(view.getUserNameField().getText());
                    parent.getModel().setServerIpAddress(view.getServerIpAddressField().getText());
                    //parent.getModel().getUsersOnline().add(view.getUserNameField().getText()); // new
                    command = new ShowChatViewCommand(parent, view);

                }
            }
                break;
            case SEND_ACTION_COMMAND: {
                ChatPanelView view = Utility.findParent((Component) e.getSource(), ChatPanelView.class);
                parent.getModel().setLastMessageText(view.getTextMessageField().getText());
                command = new SendMessageCommand(parent, view);
            }
            break;
            case LOGOUT_ACTION_COMMAND: {
                Utility.offlineMaker();
                ChatPanelView view = Utility.findParent((Component) e.getSource(), ChatPanelView.class);
                parent.getModel().initialize();
                command = new ShowLoginViewCommand(parent, view);
            }
            break;
            default:
                throw new ParseException("Unknown command:" + commandName, 0);
        }
    }




    public void setParent(ChatMessengerAppl parent) {
        this.parent = parent;
    }
}
