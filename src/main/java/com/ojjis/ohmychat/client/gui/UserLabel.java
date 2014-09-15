package com.ojjis.ohmychat.client.gui;

import com.ojjis.ohmychat.common.User;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: ojjis
 * Date: 11/24/13
 * Time: 11:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserLabel extends JLabel{
    private static final Color SELECTED_BACKGROUND = Color.DARK_GRAY;
    private static final Color SELECTED_COLOR = Color.BLACK;
    private static final Color NORMAL_BACKGROUND = Color.BLACK;
    private static final Color NORMAL_COLOR = Color.GREEN;
    private static final Color USER_COLOR = Color.ORANGE;

    private boolean selected;
    private User user;

    public UserLabel (){
        super ();
        setOpaque ( true );
        setBorder ( BorderFactory.createEmptyBorder ( 0, 5, 0, 5 ) );
    }

    public void setSelected(boolean selected){
        this.selected = selected;
        setForeground ( selected ? SELECTED_COLOR: NORMAL_COLOR);
        setBackground ( selected ? SELECTED_BACKGROUND : NORMAL_BACKGROUND );
    }

    public void setData(User user){
        this.user = user;
        setText (user.getName() );

    }

    @Override
    protected void paintComponent ( Graphics g ){
        Graphics2D g2d = ( Graphics2D ) g;
        g2d.setRenderingHint ( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        if ( selected ){
            //Display differently if the user is selected?
        }

        if ( user.isClient() ){
            setForeground(USER_COLOR);
        }

        super.paintComponent ( g );
    }

    @Override
    public Dimension getPreferredSize ()
    {
        final Dimension ps = super.getPreferredSize ();
        ps.height = 20;
        return ps;
    }
}
