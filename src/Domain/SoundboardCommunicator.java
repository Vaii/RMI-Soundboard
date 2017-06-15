package Domain;

import Client.ClientController;
import Controller.ControllerController;
import Shared.RefreshEvent;
import Shared.SoundEvent;
import Shared.VolumeEvent;
import fontyspublisher.IRemotePropertyListener;
import fontyspublisher.IRemotePublisherForDomain;
import fontyspublisher.IRemotePublisherForListener;
import sun.security.util.Password;

import java.beans.PropertyChangeEvent;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Vai on 6/12/17.
 */
public class SoundboardCommunicator extends UnicastRemoteObject implements IRemotePropertyListener {

    private IRemotePublisherForListener publisherForListener;
    private IRemotePublisherForDomain publisherForDomain;
    private static int portNumber = 1099;
    private static String bindingName = "publisher";
    private boolean connected = false;

    public boolean isConnected() {
        return connected;
    }

    private ControllerController controller;
    private ClientController client;

    private final int nrThreads = 10;
    private ExecutorService threadPool = null;

    public SoundboardCommunicator(ClientController client) throws RemoteException{
        this.client = client;
        threadPool = Executors.newFixedThreadPool(nrThreads);
    }

    public SoundboardCommunicator(ControllerController controller) throws RemoteException{
        this.controller = controller;
        threadPool = Executors.newFixedThreadPool(nrThreads);
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException {
        String property = evt.getPropertyName();

        if(property.equals("Sound")){
            SoundEvent soundEvent = (SoundEvent)evt.getNewValue();

            client.requestPlaySound(property, soundEvent);
        }
        if(property.equals("Refresh")){

            RefreshEvent refreshEvent = (RefreshEvent)evt.getNewValue();
            controller.refreshSoundList(property, refreshEvent);
        }
        if(property.equals("Volume")){
            VolumeEvent volumeEvent = (VolumeEvent)evt.getNewValue();
            client.requestVolumeChange(property, volumeEvent);
        }
    }


    public void connectToPublisher(String ipAdress, int port){
        try{
            Registry registry = LocateRegistry.getRegistry(ipAdress, port);
            publisherForDomain = (IRemotePublisherForDomain) registry.lookup(bindingName);
            publisherForListener = (IRemotePublisherForListener) registry.lookup(bindingName);
            connected = true;
            System.out.println("Connection with remote publisher established");
        }
        catch(RemoteException | NotBoundException re){
            connected = false;
            System.err.println("Cannot establish connection to remote publisher");
            System.err.println("Run SoundboardServer to start remote publisher");
        }
    }

    public void register(String property){
        if(connected){
            try{
                publisherForDomain.registerProperty(property);
            }
            catch(RemoteException ex){
                Logger.getLogger(SoundboardCommunicator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void subscribe(String property) {
        if (connected) {
            final IRemotePropertyListener listener = this;
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        publisherForListener.subscribeRemoteListener(listener, property);
                    } catch (RemoteException ex) {
                        Logger.getLogger(SoundboardCommunicator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
    }


    public void unsubscribe(String property){
        if(connected){
            final IRemotePropertyListener listener = this;
            threadPool.execute(() ->{
                try{
                    publisherForListener.unsubscribeRemoteListener(listener,property);
                }
                catch(RemoteException ex){
                    Logger.getLogger(SoundboardCommunicator.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }

    public void broadcast(String property, Object object){
        if(connected){
            threadPool.execute(() -> {
                try{
                    publisherForDomain.inform(property, null, object);
                }
                catch(RemoteException ex){
                    Logger.getLogger(SoundboardCommunicator.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }

    public void stop(){
        if(connected){
            try{
                publisherForListener.unsubscribeRemoteListener(this, null);
            }
            catch(RemoteException ex){
                Logger.getLogger(SoundboardCommunicator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try{
            UnicastRemoteObject.unexportObject(this, true);
        }
        catch(NoSuchObjectException ex){
            Logger.getLogger(SoundboardCommunicator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
