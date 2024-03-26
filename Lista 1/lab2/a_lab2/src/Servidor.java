/* Sistemas Distribuídos - SDCO8A
 * Prof: Lúcio Agostinho Rocha
 * 26/03/2024
 * 
 * André Luís de Oliveira - 2270170
 * Lucas Eduardo Pires Parra - 2208490
 * */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Scanner;


public class Servidor {

	private static Socket socket;
	private static ServerSocket server;

	private static DataInputStream entrada;
	private static DataOutputStream saida;
	public final static Path path = Paths.get("src\\fortune-br.txt");

	private int porta = 1025;

	public void iniciar() {
		
		System.out.println("Servidor iniciado na porta: " + porta);
		try {

			// Criar porta de recepcao
			server = new ServerSocket(porta);
			socket = server.accept();  //Processo fica bloqueado, ah espera de conexoes

			// Criar os fluxos de entrada e saida
			entrada = new DataInputStream(socket.getInputStream());
			saida = new DataOutputStream(socket.getOutputStream());

			// Recebimento do json
			String json = entrada.readUTF();

			// Processamento do json
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
			fr.parser(hm);
			
			if(option.equals("read")) {
				String fortune = fr.read(hm);
				saida.writeUTF("{\n" + "\"result: \"" + fortune + "\"\n}");
				
			} else if(option.equals("write")) {
				fr.write(hm, args);
				saida.writeUTF("{\n" + "\"result: \"" + args + "\"\n}");
				
			}else {
				saida.writeUTF("{\n" + "\"result: \"false\"\n}");
			}//else
			

			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}//iniciar

	
	
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
	
	
	public static void main(String[] args) {

		new Servidor().iniciar();

	}

}
