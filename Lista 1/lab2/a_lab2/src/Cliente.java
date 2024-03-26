/* Sistemas Distribuídos - SDCO8A
 * Prof: Lúcio Agostinho Rocha
 * 26/03/2024
 * 
 * André Luís de Oliveira - 2270170
 * Lucas Eduardo Pires Parra - 2208490
 * */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Cliente {
    
    private static Socket socket;
    private static DataInputStream entrada;
    private static DataOutputStream saida;
    
    private int porta=1025;
    
    public void iniciar(){
    	System.out.println("Cliente iniciado na porta: "+porta);
    	
    	try {
            
            socket = new Socket("127.0.0.1", porta);
            
            entrada = new DataInputStream(socket.getInputStream());
            saida = new DataOutputStream(socket.getOutputStream());
            
            //Recebe do usuario algum valor
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Para escrever uma nova fortuna, digite \"write\". Para ler uma fortuna digite \"read\"");
            
            String option = br.readLine();
            String args = "";
            
            if(option.equals("write")) {
            	System.out.print("Digite sua fortuna a seguir: ");
            	args = br.readLine();
            }else if(option.equals("read")) {
            	args = "";
            }else {
            	System.out.println("Opção Inválida! Finalizando programa.");
            	System.exit(0);
            }
            
            String json = "{\n" + "\"method\": \"" + option + "\",\n" + "\"args\": [\"" + args + "\"]\n}";
            	
            
            //O valor eh enviado ao servidor
            saida.writeUTF(json);
            
            //Recebe-se o resultado do servidor
            String resultado = entrada.readUTF();
            
            //Mostra o resultado na tela
            System.out.println(resultado);
            
            socket.close();
            br.close();
            
        } catch(Exception e) {
        	e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new Cliente().iniciar();
    }
    
}
