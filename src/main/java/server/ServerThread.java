package server;

import domain.Message;
import domain.xml.MessageBuilder;
import domain.xml.MessageParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.Map.Entry;

public class ServerThread extends Thread {
    final static Logger LOGGER = LogManager.getLogger(ServerThread.class);
    public static final String METHOD_GET = "GET";
    public static final String METHOD_PUT = "PUT";
    public static final String END_LINE_MESSAGE = "END";
    public static final String METHOD_GET_USER_INFO = "USER_INFO";
    public static final String METHOD_REMOVE = "REMOVE";
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final List<String> userList;


    public AtomicInteger getMessageid() {
        return messageid;
    }

    private final AtomicInteger messageid;

    public Map<Long, Message> getMessageList() {
        return messageList;
    }

    private final Map<Long, Message> messageList;

    public ServerThread(Socket socket, AtomicInteger id, Map<Long, Message> messagesList, List<String> userList) throws IOException {
        this.socket = socket;
        this.messageid = id;
        this.messageList = messagesList;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        this.userList = userList;
        start();
    }

    @Override
    public void run() {
        try {
            LOGGER.debug("New server thread starting");
            String requestLine = in.readLine();
            LOGGER.debug("request" + requestLine);
            switch (requestLine){
                case METHOD_GET_USER_INFO:{
                    requestLine = in.readLine();
                    switch (requestLine){
                        case METHOD_GET:
                            LOGGER.debug("get online users" + requestLine);
                            out.println(userList.toString());
                            out.flush();
                            break;
                        case METHOD_PUT: {
                            LOGGER.debug("get online users" + requestLine);
                            String newOnlineUser = in.readLine();
                            if (!userList.contains(newOnlineUser))
                                userList.add(newOnlineUser);
                            break;
                        }
                        case METHOD_REMOVE: {
                            String newOfflineUser = in.readLine();
                            LOGGER.debug("set offline" + newOfflineUser);
                            if (userList.contains(newOfflineUser))
                                userList.remove(newOfflineUser);
                            break;
                        }
                    }
                }
                case METHOD_GET:
                    LOGGER.debug("get: ");
                    final Long lastId = Long.valueOf(in.readLine());
                    LOGGER.debug("last id" + lastId);
                    List<Message> lastNotSeenMessages =
                            messageList.entrySet().stream().filter(message -> message.getKey().compareTo(lastId) > 0).map(Entry::getValue).collect(Collectors.toList());
                    LOGGER.debug("messages:" + lastNotSeenMessages);

                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.newDocument();


                    String xmlContent = MessageBuilder.buildDocument(document, lastNotSeenMessages);
                    LOGGER.trace("Echoing:" + xmlContent);
                    out.println(xmlContent);
                    out.println(END_LINE_MESSAGE);
                    out.flush();
                    break;
                case METHOD_PUT:
                    LOGGER.debug("put:" );
                    requestLine = in.readLine();
                    StringBuilder mesStr = new StringBuilder();
                    while (! END_LINE_MESSAGE.equals(requestLine)){
                        mesStr.append(requestLine);
                        requestLine = in.readLine();
                    }
                    LOGGER.debug(mesStr);
                    SAXParserFactory parsefactory = SAXParserFactory.newInstance();
                    SAXParser parser = parsefactory.newSAXParser();
                    List<Message> messages = new ArrayList<>();
                    MessageParser saxp = new MessageParser(messageid, messages);
                    InputStream is = new ByteArrayInputStream(mesStr.toString().getBytes());
                    parser.parse(is, saxp);
                    for (Message message: messages){
                        messageList.put(message.getId(), message);
                    }
                    LOGGER.trace("Echoing: " + messages);
                    out.println("OK");
                    out.flush();
                    out.close();
                    break;
                default:
                    LOGGER.info("Unknown request" + requestLine);
                    out.println("BAD REQUEST");
                    out.flush();
                    break;

            }

        }
        catch (Exception e){
            LOGGER.error(e.getMessage());
            out.println("Error");
            out.flush();
        } finally {
            try {
                LOGGER.debug("SOCKET is closed");
                LOGGER.debug("Close object stream");
                in.close();
                out.close();
                socket.close();
            } catch (IOException e){
                LOGGER.error("Socket not close");
            }
        }
    }
}
