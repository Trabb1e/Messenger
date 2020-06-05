package client;

import java.util.TimerTask;

public class UpdateMessagesAndOnlineUsersTask extends TimerTask {
    ChatMessengerAppl appl;
    public UpdateMessagesAndOnlineUsersTask(ChatMessengerAppl appl) {
        this.appl = appl;
    }

    @Override
    public void run() {
        Utility.usersOnlineUpdate();
        Utility.messagesUpdate(appl);
        appl.getChatPanelView().listRepaintWithOnlineUsers();
    }
}
