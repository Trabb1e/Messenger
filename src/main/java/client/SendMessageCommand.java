package client;

import domain.Message;
import domain.xml.MessageBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import server.ChatMessengerServer;
import server.ServerThread;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SendMessageCommand implements Command {
    final static Logger LOGGER = LogManager.getLogger(SendMessageCommand.class);
    private ChatMessengerAppl appl;
    private ChatPanelView panel;
    private InetAddress addr;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public SendMessageCommand(ChatMessengerAppl parent, ChatPanelView view) {
        appl = parent;
        panel = view;
    }

    @Override
    public void execute() {
        if (!panel.getTextMessageField().getText().equals("")) {
            try {
                addr = InetAddress.getByName(appl.getModel().getServerIpAddress());
                socket = new Socket(addr, ChatMessengerServer.PORT);
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                LOGGER.error("Socket error" + e.getMessage());
            }
            String result;
            try {
                do {
                    out.println(ServerThread.METHOD_PUT);
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.newDocument();
                    List<Message> messages = new ArrayList<>();
                    messages.add(
                            Message.newMessage().text(panel.getTextMessageField().getText())
                                    .from(appl.getModel().getLoggedUser())
                                    .to(appl.getModel().getCurrentUSer())
                                    .moment(Calendar.getInstance())
                                    .build()
                    );
                    String xmlContent = MessageBuilder.buildDocument(document, messages);
                    out.println(xmlContent);
                    out.println(ServerThread.END_LINE_MESSAGE);
                    result = in.readLine();

                }
                while (!"OK".equals(result));
            } catch (IOException | ParserConfigurationException e) {
                LOGGER.error("Send message error" + e.getMessage());
            } finally {
                try {
                    in.close();
                    out.close();
                    socket.close();
                } catch (IOException e) {
                    LOGGER.error("Socket error" + e.getMessage());
                }
                panel.getTextMessageField().setText("");
                panel.getTextMessageField().requestFocus();
            }
        }
    }
}
