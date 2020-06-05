package client;

public class ShowLoginViewCommand implements Command {
    private ChatMessengerAppl appl;
    private ChatPanelView panel;
    public ShowLoginViewCommand(ChatMessengerAppl parent, ChatPanelView view)
    {
        appl = parent;
        panel = view;
    }

    @Override
    public void execute() {
        panel.clearFields();
        panel.setVisible(false);
        appl.getTimer().cancel();
        appl.showLoginPanelView();
    }
}
