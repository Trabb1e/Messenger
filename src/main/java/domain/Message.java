package domain;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Message implements Serializable, Comparable<Message> {
    private Long id;
    private String text;
    private String userNameFrom, userNameTo; // poosibly change type string with custom class user
    private Calendar moment;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserNameFrom() {
        return userNameFrom;
    }

    public void setUserNameFrom(String userNameFrom) {
        this.userNameFrom = userNameFrom;
    }

    public String getUserNameTo() {
        return userNameTo;
    }

    public void setUserNameTo(String userNameTo) {
        this.userNameTo = userNameTo;
    }

    public Calendar getMoment() {
        return moment;
    }

    public void setMoment(Calendar moment) {
        this.moment = moment;
    }

    private Message(){}

    private Message(Builder builder){
        setId(builder.id);
        setText(builder.text);
        setUserNameFrom(builder.userNameFrom);
        setUserNameTo(builder.userNameTo);
        setMoment(builder.moment);
    }

    public static Builder newMessage(){
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;

        Message message = (Message) o;

        if (!getId().equals(message.getId())) return false;
        if (!getText().equals(message.getText())) return false;
        if (!getUserNameFrom().equals(message.getUserNameFrom())) return false;
        if (!getUserNameTo().equals(message.getUserNameTo())) return false;
        return getMoment().equals(message.getMoment());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getText().hashCode();
        result = 31 * result + getUserNameFrom().hashCode();
        result = 31 * result + getUserNameTo().hashCode();
        result = 31 * result + getMoment().hashCode();
        return result;
    }

    public int compareTo(Message o) {
        if(getMoment().equals(o.getMoment()))
            return getId().compareTo(o.getId());
        else
            return getMoment().compareTo(o.getMoment());
    }

    @Override
    public String toString() {
        return new StringBuilder("<p><b>").append(userNameFrom)
                .append((userNameTo.length() != 0) ? (" -> " + userNameTo) : "")
                .append(":</b><br /> <message>").append(text)
                .append("</message><br /> <div style = 'text-align:right; font-size:small;' >")
                .append((new SimpleDateFormat("HH:mm:ss dd-MMM-yyyy")).format(moment.getTime().getTime()))
                .append("</div> <br /></p>").toString();
    }

    public static final class Builder {
        private Long id;
        private String text;
        private String userNameFrom, userNameTo; // poosibly change type string with custom class user
        private Calendar moment;

        private Builder(){}

        public Message build(){
            return new Message(this);
        }

        public Builder id(Long id){
            this.id = id;
            return this;
        }

        public Builder text(String text){
            this.text = text;
            return this;
        }

        public Builder from(String userNameFrom){
            this.userNameFrom = userNameFrom;
            return this;
        }

        public Builder to(String userNameTo){
            this.userNameTo = userNameTo;
            return this;
        }

        public Builder moment(Calendar moment){
            this.moment = moment;
            return this;
        }


    }
}
