package domain.xml;

import domain.Message;
import domain.Message.Builder;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;



public class MessageParser extends DefaultHandler {
    final static Logger LOGGER = LogManager.getLogger(MessageParser.class);
    private List<Message> messages;
    private Message message;
    private AtomicInteger id;
    private String thisElement = "";

    public MessageParser(AtomicInteger id, List<Message> messages){
        this.id = id;
        this.messages = messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public void startDocument() throws SAXException {
        LOGGER.debug("Start document");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        thisElement = qName;
        LOGGER.debug("Start element");
        LOGGER.trace("<" + qName);
        if("message".equals(qName)){
            Builder builder = Message.newMessage();
            for(int i = 0; i< attributes.getLength(); i++){
                String atrrName = attributes.getLocalName(i);
                String attrValue = attributes.getValue(i);
                LOGGER.trace(atrrName + " = " + attrValue);
                switch (atrrName){
                    case "from":
                        builder.from(attrValue);
                        break;
                    case "to":
                        builder.to(attrValue);
                        break;
                    case "id":
                        builder.id(Long.valueOf(attrValue));
                        break;
                    case "moment":
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
                        try {
                            calendar.setTime(format.parse(attrValue));
                        }
                        catch (ParseException e){
                            LOGGER.error(e.getMessage());
                            e.printStackTrace();
                        }
                        builder.moment(calendar);
                }
            }
            message = builder.build();
        }
        LOGGER.trace(">");
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if("message".equals(qName)){
            Long newId = (long) id.getAndIncrement();
            if(message.getId() == null){
                message.setId(newId);
            }
            else{
                newId = message.getId();
                id.set(newId.intValue());
            }
            LOGGER.debug("id = " + newId);
            messages.add(message);
        }
        thisElement ="";
        LOGGER.debug("End element");
        LOGGER.trace("</" + qName + ">");
    }



    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if ("message".equals(thisElement)){
            String messBody = new String(ch, start, length).trim();
            LOGGER.trace(messBody);
            message.setText(messBody);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        LOGGER.debug("End document");

    }


}


