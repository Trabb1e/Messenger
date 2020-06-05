package server;

import domain.Message;
import domain.xml.MessageBuilder;
import domain.xml.MessageParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatMessengerServer {

    public static final int PORT = 7070;
    final static Logger LOGGER = LogManager.getLogger(ChatMessengerServer.class);
    private static final int SERVER_TIMEOUT = 500 ;
    private static final String XML_FILE_NAME = "messages.xml" ;
    private static boolean stop = false;
    private static AtomicInteger id = new AtomicInteger(0);
    private static List<String> userList = Collections.synchronizedList(new LinkedList<String>(){
        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            Iterator<String> i = iterator();
            while (i.hasNext()) {
                result.append(i.next()).append(";");
            }
            return result.toString();
        }
    });
    private static Map<Long, Message> messagesList = Collections.synchronizedSortedMap(new TreeMap<Long, Message>());

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        //Load messages from XML file
        loadMessageXMLFile();

        //Run thread for quit command
        quitCommandThread();

        ServerSocket serverSocket = new ServerSocket(PORT);
        LOGGER.info("Server started on port: " + PORT);

        while (!stop){
            serverSocket.setSoTimeout(SERVER_TIMEOUT);
            Socket socket;
            try {
                socket = serverSocket.accept();
                try {
                    new ServerThread(socket, id, messagesList, userList);
                } catch (IOException e){
                    LOGGER.error("IO error");
                    socket.close();
                }
            } catch (SocketTimeoutException e){
            }
        }
        //Save all messages in XML file
        saveMessagesXMLFile();
        LOGGER.info("Server stopped ");
        serverSocket.close();
    }

    private static void saveMessagesXMLFile() throws ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        String xmlContent = MessageBuilder.buildDocument(document, messagesList.values());

        OutputStream os = new FileOutputStream(new File(XML_FILE_NAME));
        OutputStreamWriter out = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        out.write(xmlContent + "\n");
        out.flush();
        out.close();
    }

    private static void loadMessageXMLFile() throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        List<Message> messages = new ArrayList<>();
        MessageParser saxp = new MessageParser(id, messages);
        InputStream is = new ByteArrayInputStream(Files.readAllBytes(Paths.get(XML_FILE_NAME)));
        parser.parse(is, saxp);
        for (Message message : messages){
            messagesList.put(message.getId(), message);
        }
        id.incrementAndGet();
        is.close();
    }

    private static void quitCommandThread() {
        new Thread() {
            @Override
            public void run() {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                while (true){
                    String buf;
                    try {
                        buf = br.readLine();
                        if("quit".equals(buf)){
                            stop = true;
                            break;
                        } else {
                            LOGGER.warn("Type 'quit' for server terminantion");
                        }
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
