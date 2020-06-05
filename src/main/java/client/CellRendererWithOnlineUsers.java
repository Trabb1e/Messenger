package client;

import javax.swing.*;
import java.awt.*;

public class CellRendererWithOnlineUsers extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
        if(ChatMessengerAppl.getModel().getOnlineUsers().contains(value)){
            c.setBackground(Color.CYAN);
        }
        if(isSelected){
            c.setBackground(Color.BLUE);
        }
        return c;
    }
}
