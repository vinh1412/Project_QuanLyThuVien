package views.components;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 *
 * @author Raven
 */
public interface MenuEvent {

    public void menuSelected(int index, int subIndex, MenuAction action) throws RemoteException, MalformedURLException, NotBoundException;
}
