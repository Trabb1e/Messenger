package client;

import domain.Message;
import domain.xml.MessageBuilder;
import org.w3c.dom.Document;
import server.ChatMessengerServer;
import server.ServerThread;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;

public class ShowChatViewCommand implements Command {
    private ChatMessengerAppl appl;
    private LoginPanelView view;
    private InetAddress addr;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Model model;
    public ShowChatViewCommand(ChatMessengerAppl parent, LoginPanelView view) {
        appl = parent;
        this.view = view;
        this.model = ChatMessengerAppl.getModel();

    }

    @Override
    public void execute() {
        Utility.messagesUpdate(appl);
        Utility.updateAllUsers(ChatMessengerAppl.getModel().getMessages());
        ChatMessengerAppl.getModel().setLoggedUser(view.getUserNameField().getText());
        if(!model.getUsers()
                .contains(model.getLoggedUser())) {
            try {
                addr = InetAddress.getByName(appl.getModel().getServerIPAddress());
                socket = new Socket(addr, ChatMessengerServer.PORT);
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
            }
            try {
                String result;
                do {
                    out.println(ServerThread.METHOD_PUT);
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.newDocument();
                    List<Message> messages = new ArrayList<>();
                    messages.add(
                            Message.newMessage().text("Reg me")
                                    .from(ChatMessengerAppl.getModel().getLoggedUser())
                                    .to(ChatMessengerAppl.getModel().REGISTRAR)
                                    .moment(Calendar.getInstance()).build()
                    );
                    String xmlContent = MessageBuilder.buildDocument(document,messages);
                    out.println(xmlContent);
                    out.println(ServerThread.END_LINE_MESSAGE);
                    result = in.readLine();
                } while ("OK".equals(result));
            } catch (IOException | ParserConfigurationException e) {
                e.printStackTrace();
            }finally {
                try {
                    in.close();
                    out.close();
                    socket.close();

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        Utility.messagesUpdate(appl);
        Utility.updateAllUsers(ChatMessengerAppl.getModel().getMessages());
        try {
            addr = InetAddress.getByName(appl.getModel().getServerIPAddress());
            socket = new Socket(addr, ChatMessengerServer.PORT);
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            out.println(ServerThread.METHOD_GET_USER_INFO);
            out.println(ServerThread.METHOD_PUT);
            out.println(view.getUserNameField().getText());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                socket.close();

            }catch (IOException e){
                e.printStackTrace();
            }
        }
        view.clearFields();
        view.setVisible(false);
        appl.setTimer(new Timer());
        appl.getTimer().scheduleAtFixedRate(new UpdateMessagesAndOnlineUsersTask(appl), ChatMessengerAppl.DELAY, ChatMessengerAppl.PERIOD);
        appl.showChatPanelView();
    }
}
