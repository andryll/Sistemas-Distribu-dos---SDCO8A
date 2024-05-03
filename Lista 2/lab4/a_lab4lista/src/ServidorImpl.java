/* Sistemas Distribuídos - SDCO8A
 * Prof: Lúcio Agostinho Rocha
 * 03/05/2024
 * 
 * André Luís de Oliveira - 2270170
 * Lucas Eduardo Pires Parra - 2208490
 * */
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServidorImpl implements IMensagem{
    
	List<Peer> alocados = new ArrayList<>();
	Principal principal;
	public final static Path path = Paths.get("a_lab4lista\\src\\fortunes_openbsd.txt");

	
    public ServidorImpl() throws IOException {
    	for (Peer peer : Peer.values()) {
    		alocados.add(peer);
    	}//for
    	this.principal = new Principal();
    }
    
    //Cliente: invoca o metodo remoto 'enviar'
    //Servidor: invoca o metodo local 'enviar'
    @Override
    public Mensagem enviar(Mensagem mensagem) throws RemoteException {
        Mensagem resposta;
        try {
        	System.out.println("Mensagem recebida: " + mensagem.getMensagem());
			resposta = new Mensagem(parserJSON(mensagem.getMensagem()));
		} catch (Exception e) {
			e.printStackTrace();
			resposta = new Mensagem("{\n" + "\"result\": false\n" + "}");
		}
        return resposta;
    }    
    
    public String parserJSON(String json) throws IOException {
		String result = json;
		
		String option = "";
		String args = "";
		
		json = json.substring(1, json.length()-1);
		String[] fields = json.split(",");
		
		for(String field : fields) {
			String[] values  = field.split(":");
			String key = values[0].trim();
			String value = values[1].trim();
			
			//Removendo as aspas
			key = key.substring(1, key.length() - 1);
			value = value.substring(1, value.length() - 1);
			
			if(key.equals("method")) {
				option = value;
			}else if (key.equals("args")) {
				args = value;
			}//else
		}//for
		
		FileReader fr = new FileReader();
		HashMap hm = new HashMap<Integer, String>();
		
		try {
			fr.parser(hm);
			if(option.equals("read")) {
				String fortune = fr.read(hm);
				result = "{\n" + "\"result: \"" + fortune + "\"\n}";
				
			} else if(option.equals("write")) {
				fr.write(hm, args);
				result = ("{\n" + "\"result: \"" + args + "\"\n}");
				
			}else {
				result = "{\n" + "\"result: \"false\"\n}";
			}//else

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		System.out.println(result);

		return result;
	}
    
    public void iniciar(){

    try {
  		
    		Registry servidorRegistro;
    		try {
    			servidorRegistro = LocateRegistry.createRegistry(1099);
    		} catch (java.rmi.server.ExportException e){ //Registro jah iniciado 
    			System.out.print("Registro jah iniciado. Usar o ativo.\n");
    		}
    		servidorRegistro = LocateRegistry.getRegistry(); //Registro eh unico para todos os peers
    		String [] listaAlocados = servidorRegistro.list();
    		for(int i=0; i<listaAlocados.length;i++)
    			System.out.println(listaAlocados[i]+" ativo.");
    		
    		SecureRandom sr = new SecureRandom();
    		Peer peer = alocados.get(sr.nextInt(alocados.size()));
    		
    		int tentativas=0;
    		boolean repetido = true;
    		boolean cheio = false;
    		while(repetido && !cheio){
    			repetido=false;    			
    			peer = alocados.get(sr.nextInt(alocados.size()));
    			for(int i=0; i<listaAlocados.length && !repetido; i++){
    				
    				if(listaAlocados[i].equals(peer.getNome())){
    					System.out.println(peer.getNome() + " ativo. Tentando proximo...");
    					repetido=true;
    					tentativas=i+1;
    				}    			  
    				
    			}
    			//System.out.println(tentativas+" "+listaAlocados.length);
    			    			
    			//Verifica se o registro estah cheio (todos alocados)
    			if(listaAlocados.length>0 && //Para o caso inicial em que nao ha servidor alocado,
    					                     //caso contrario, o teste abaixo sempre serah true
    				tentativas==alocados.size()){ 
    				cheio=true;
    			}
    		}
    		
    		if(cheio){
    			System.out.println("Sistema cheio. Tente mais tarde.");
    			System.exit(1);
    		}
    		
            IMensagem skeleton  = (IMensagem) UnicastRemoteObject.exportObject(this, 0); //0: sistema operacional indica a porta (porta anonima)
            servidorRegistro.rebind(peer.getNome(), skeleton);
            System.out.print(peer.getNome() +" Servidor RMI: Aguardando conexoes...");
                        
        } catch(Exception e) {
            e.printStackTrace();
        }        

    }
    
    public static void main(String[] args) throws IOException {
        ServidorImpl servidor = new ServidorImpl();
        servidor.iniciar();
    }    
    
    public class FileReader {

		public int countFortunes() throws FileNotFoundException {

			int lineCount = 0;

			InputStream is = new BufferedInputStream(new FileInputStream(
					path.toString()));
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
					is))) {

				String line = "";
				while (!(line == null)) {

					if (line.equals("%"))
						lineCount++;

					line = br.readLine();

				}// fim while

				System.out.println(lineCount);
			} catch (IOException e) {
				System.out.println("SHOW: Excecao na leitura do arquivo.");
			}
			return lineCount;
		}

		public void parser(HashMap<Integer, String> hm)
				throws FileNotFoundException {

			InputStream is = new BufferedInputStream(new FileInputStream(
					path.toString()));
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
					is))) {

				int lineCount = 0;

				String line = "";
				while (!(line == null)) {

					if (line.equals("%"))
						lineCount++;

					line = br.readLine();
					StringBuffer fortune = new StringBuffer();
					while (!(line == null) && !line.equals("%")) {
						fortune.append(line + "\n");
						line = br.readLine();
						// System.out.print(lineCount + ".");
					}

					hm.put(lineCount, fortune.toString());
					System.out.println(fortune.toString());

					System.out.println(lineCount);
				}// fim while

			} catch (IOException e) {
				System.out.println("SHOW: Excecao na leitura do arquivo.");
			}
		}

		public String read(HashMap<Integer, String> hm)
				throws FileNotFoundException {

			Object[] keys = hm.keySet().toArray();
			int randomIndex = new SecureRandom().nextInt(keys.length);
			Object randomKey = keys[randomIndex];
			
			String fortune = hm.get(randomKey);
			
//			System.out.println("\n\nChave: " + randomKey);
//			System.out.println("Fortune:\n" + fortune);
			
			return fortune;
		}

		public void write(HashMap<Integer, String> hm, String fortune)
				throws FileNotFoundException {
			
			//Scanner scan = new Scanner(System.in);
			
			//System.out.print("Digite a fortuna que desejas adcionar na base de dados: ");
			//String newFortune = scan.nextLine();
			
			int newKey = hm.keySet().toArray().length;
			
			hm.put(newKey, fortune);
			
			//System.out.println("Fortuna adcionada!\nChave: " + newKey + "\nFortuna: " + newFortune);
			
		}
	}
}
