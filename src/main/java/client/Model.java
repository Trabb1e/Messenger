package client;

import domain.Message;

import java.util.*;
import java.util.stream.Collectors;

public class Model {
    private ChatMessengerAppl parent;
    private String currentUSer;
    private String loggedUser;
    private String lastMessageText;
    private List<Message> messages;
    private Long lastMessageId;
    private String serverIpAddress = "127.0.0.1";
    private List<String> users;
    public static String REGISTRAR = "Registrar";
    private List<String> onlineUsers;


    private Model(){
        users = new ArrayList<>();
        onlineUsers = new ArrayList<>();
    }

    public static Model getInstance() {
        return ModelHolder.INSTANCE;
    }

    public String messagesToString() {
        return messages.toString();
    }

    public List<String> getOnlineUsers() {
        return onlineUsers;
    }

    public void setOnlineUsers(List<String> onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    public void adddMessages(List<Message> messages) {
        this.getMessages().addAll(messages);
        getWithCurrentUserMessages(messages);
        Utility.updateAllUsers(messages.stream().filter(message -> (message.getUserNameTo()
                .equals(ChatMessengerAppl.getModel().REGISTRAR))).collect(Collectors.toList()));
    }

    public void getWithCurrentUserMessages(List<Message> messages){
        if(getCurrentUSer()!=null){
            List<Message> newMessages = new ArrayList<Message>(){
                @Override
                public String toString() {
                    StringBuilder result = new StringBuilder();
                    Iterator<Message> i = iterator();
                    while (i.hasNext()) {
                        result.append(i.next());
                    }
                    return result.toString();
                }
            };
            if(!getLoggedUser().equals(getCurrentUser()))
                newMessages.addAll(messages.stream().filter(mes->((mes.getUserNameFrom().equals(getCurrentUser()) &&
                        mes.getUserNameTo().equals(getLoggedUser())) ||
                        (mes.getUserNameTo().equals(getCurrentUser()) &&
                                mes.getUserNameFrom().equals(getLoggedUser())) && !mes.getUserNameTo().equals(REGISTRAR))).collect(Collectors.toList()));
            else newMessages.addAll(messages.stream().filter(mes->mes.getUserNameFrom().equals(getCurrentUser())&&
                    mes.getUserNameTo().equals(getCurrentUser())).collect(Collectors.toList()));
            if(newMessages!=null) {
                java.util.Collections.sort(newMessages);
                String mess = newMessages.toString();
                parent.getChatPanelView().modelChangedNotification(mess);
            }
        }
    }

    private String getCurrentUser() {
        return currentUSer;
    }

    public String getServerIPAddress() {
        return serverIpAddress;
    }

    private static class  ModelHolder {
        private static final Model INSTANCE = new Model();
    }

    public void initialize(){
        setMessages(new ArrayList<Message>(){
            @Override
            public String toString() {
                StringBuilder result = new StringBuilder();
                Iterator<Message> i = iterator();
                while (i.hasNext()) {
                    result.append(i.next().toString()).append("<br>");
                }
                return result.toString();
            }
        });
        lastMessageId = 0L;
        currentUSer = "";
        loggedUser = "";
        lastMessageText = "";
    }

    // getters and setters
    public ChatMessengerAppl getParent() {
        return parent;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setParent(ChatMessengerAppl parent) {
        this.parent = parent;
    }

    public String getCurrentUSer() {
        return currentUSer;
    }

    public void setCurrentUSer(String currentUSer) {
        this.currentUSer = currentUSer;
    }

    public String getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(String loggedUser) {
        this.loggedUser = loggedUser;
    }

    public String getLastMessageText() {
        return lastMessageText;
    }

    public void setLastMessageText(String lastMessageText) {
        this.lastMessageText = lastMessageText;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(Long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public String getServerIpAddress() {
        return serverIpAddress;
    }

    public void setServerIpAddress(String serverIpAddress) {
        this.serverIpAddress = serverIpAddress;
    }

}
