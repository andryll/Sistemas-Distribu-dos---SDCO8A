/* Sistemas Distribuídos - SDCO8A
 * Prof: Lúcio Agostinho Rocha
 * 03/05/2024
 * 
 * André Luís de Oliveira - 2270170
 * Lucas Eduardo Pires Parra - 2208490
 * */
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ClienteRMI {
    
    public static void main(String[] args) {
                
		
        try {
                   
        	List<Peer> peers = new ArrayList<>();
        	
        	for (Peer peer : Peer.values()) {
        		peers.add(peer);
        	}//for
        	
	        System.out.println("Peers:");
	        for (Peer peer : peers) {
	        	System.out.println(peer);
	        }//for
        	
            Registry registro = LocateRegistry.getRegistry("127.0.0.1", 1099);

            
        	//Escolhe um peer aleatorio da lista de peers para conectar
            SecureRandom sr = new SecureRandom();
    		
            IMensagem stub = null;
            Peer peer = null;
            
    		boolean conectou=false;
    		while(!conectou){
    			peer = peers.get(sr.nextInt(peers.size()));
    			try{    				
    				stub = (IMensagem) registro.lookup(peer.getNome());
    				conectou=true;
    			} catch(java.rmi.ConnectException e){
    				System.out.println(peer.getNome() + " indisponivel. ConnectException. Tentanto o proximo...");
    			} catch(java.rmi.NotBoundException e){
    				System.out.println(peer.getNome() + " indisponivel. NotBoundException. Tentanto o proximo...");
    			}
    		}
            System.out.println("Conectado no peer: " + peer.getNome());            
    		
    		
            String opcao="";
            Scanner leitura = new Scanner(System.in);
            do {
            	System.out.println("1) Read");
            	System.out.println("2) Write");
            	System.out.println("x) Exit");
            	System.out.print(">> ");
            	opcao = leitura.next();
            	switch(opcao){
            	case "1": {
            		Mensagem mensagem = new Mensagem("", opcao);
            		Mensagem resposta = stub.enviar(mensagem); //dentro da mensagem tem o campo 'read'
            		System.out.println(resposta.getMensagem());
            		break;
            	}
            	case "2": {
            		//Monta a mensagem    
            		leitura.nextLine();
            		System.out.print("Add fortune: ");
            		String fortune = leitura.next();
            		
            		Mensagem mensagem = new Mensagem(fortune, opcao);
            		Mensagem resposta = stub.enviar(mensagem); //dentro da mensagem tem o campo 'write'
            		System.out.println(resposta.getMensagem());
            		break;
            	}
            	}
            } while(!opcao.equals("x"));
                        
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
}
