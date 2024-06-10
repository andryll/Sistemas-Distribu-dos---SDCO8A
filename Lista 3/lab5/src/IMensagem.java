/**
 * Lab05: Sistema P2P
 * 
 * André Luís de Oliveira (2270170) e Lucas Eduardo Pires Parra (2208490)
 * Ultima atualizacao: 10/06/2024
 */

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IMensagem extends Remote {
    
    public Mensagem enviar(Mensagem mensagem) throws RemoteException;
    
}
