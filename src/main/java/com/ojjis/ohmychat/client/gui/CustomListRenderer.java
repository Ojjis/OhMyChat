package com.ojjis.ohmychat.client.gui;

import com.ojjis.ohmychat.common.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created with IntelliJ IDEA.
 * User: ojjis
 * Date: 11/24/13
 * Time: 11:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomListRenderer extends DefaultListCellRenderer {

    /**
     * Actual renderer.
     */
    private UserLabel renderer;

    /**
     * Custom renderer constructor.
     * We will use it to create actual renderer component instance.
     * We will also add a custom mouse listener to process close button.
     *
     * @param list our JList instance
     */
    public CustomListRenderer ( final JList list ){
        super ();
        renderer = new UserLabel();

        list.addMouseListener ( new MouseAdapter(){
            //Handle stuff on the user like klicks etc..
            @Override
             public void mouseReleased ( MouseEvent e ){
                if ( SwingUtilities.isRightMouseButton(e) ){
                    int index = list.locationToIndex ( e.getPoint () );
                    if ( index != -1 && list.isSelectedIndex ( index ) ){
                        System.out.println("Right Klick On  <" + index +"> which is selected");
                    }    else{

                        System.out.println("Right Klick On  <" + index +"> which is not selected");
                    }
                }

            }
            /*@Override
            public void mouseReleased ( MouseEvent e ){
                if ( SwingUtilities.isLeftMouseButton ( e ) ){
                    int index = list.locationToIndex ( e.getPoint () );
                    if ( index != -1 && list.isSelectedIndex ( index ) ){
                        Rectangle rect = list.getCellBounds ( index, index );
                        Point pointWithinCell = new Point ( e.getX () - rect.x, e.getY () - rect.y );
                        Rectangle crossRect = new Rectangle ( rect.width - 9 - 5 - crossIcon.getIconWidth () / 2,
                                rect.height / 2 - crossIcon.getIconHeight () / 2, crossIcon.getIconWidth (), crossIcon.getIconHeight () );
                        if ( crossRect.contains ( pointWithinCell ) ){
                            DefaultListModel model = ( DefaultListModel ) list.getModel ();
                            model.remove ( index );
                        }
                    }
                }
            }*/
        } );
    }

    /**
     * Returns custom renderer for each cell of the list.
     *
     * @param list         list to process
     * @param value        cell value (CustomData object in our case)
     * @param index        cell index
     * @param isSelected   whether cell is selected or not
     * @param cellHasFocus whether cell has focus or not
     * @return custom renderer for each cell of the list
     */
    @Override
    public Component getListCellRendererComponent ( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ){
        renderer.setSelected ( cellHasFocus );
        renderer.setData ( (User) value );
        return renderer;
    }
}