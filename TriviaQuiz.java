import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.sql.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TriviaQuiz {				//Definizione della classe server
	private ServerSocket server;
	private Socket client;
	private String DatiRic;
	private String DatiModif;
	protected static int c;
	private BufferedWriter outVersoClient;

	public TriviaQuiz() {				//Costruttore della classe Server01 
		this.client=null;
		this.DatiRic="";
		this.DatiModif="";
		this.c=0;
		this.outVersoClient=null;
	}

	public Socket attendi(String args){		//Metodo per l'ascolto e controllo dei client accettati

		int port = Integer.parseInt(args);	//Parametro contenente la porta inserita da riga di comando

		System.out.print("\033[H\033[2J");	//Pulizia schermo

		System.out.println("********************BENVENUTO IN TRIVIA QUIZ********************");
		System.out.println("\nServer avviato su porta "+port);

		//creo il Server
		try {
			server = new ServerSocket(port);

			while(true)
			{
				this.client=server.accept();		//Server si mette in ascolto continuamente
				System.out.println("Connessione con "+this.client.getInetAddress()+" porta: "+this.client.getLocalPort());
				try
				{
					Thread.sleep(3000);		//Delay di connessione per i client
				}catch(InterruptedException e){
					e.printStackTrace();
					System.exit(-1);
				}
				if(c>=3)				//Se è stato raggiunto il limite massimo di connessioni, rifiuta. Altrimenti accetta.
				{
					System.out.println("Connessione rifiutata a "+this.client.getInetAddress()+" porta: "+this.client.getLocalPort());
					outVersoClient = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
					outVersoClient.write("quit");
					outVersoClient.flush();
					client.close();
				}else{
					System.out.println("Connesso con successo con "+this.client.getInetAddress()+" porta: "+this.client.getLocalPort());
					Thread t = new Comunica(client);
					t.start();
					c++;				//Incremento del numero di connessioni in caso di successo
				}
			}
		} catch (IOException e) {
			System.out.println("Errore durante connessione");
			e.printStackTrace();
		}


		return client;
	}

	public void chiudi() {
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {			//Programma principale

		TriviaQuiz server = new TriviaQuiz();		//Definizione della classe server

		try
		{
			server.attendi(args[0]);			//Metodo di esecuzione del server
			server.chiudi();
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			System.out.println("\nNon e' stata inserita alcuna porta.\n");
		};
	}
}


class Comunica extends Thread{					//classe Thread di comunicazione ed elaborazione delle stringhe del client

	private Socket client;
	private String nick;
	private String msg,i;
	private String[] dmd;
	private String[] res;
	private int flag,count,c,s,punt;
	private Object[] list;
	private BufferedReader inc;
	private BufferedWriter outc;
	private Random rand;
	private Set<Integer> set;
	private RandomAccessFile f1,f2;

	public Comunica(Socket client){				//Cotruttore della classe
		try
		{
			inc = new BufferedReader(new InputStreamReader(client.getInputStream()));
			outc = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		}
	       	catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		this.client=client;
		nick="";
		s=1;
		dmd = new String[8];
		res = new String[8];
	}

	public boolean check(String risposta_data, String risposta)		//Metodo per il controllo di risposta corretta
	{
		try
		{
			risposta_data = risposta_data.toUpperCase();	
			if (risposta_data.equals(risposta)) {
				outc.write("Risposta corretta!\n");
				outc.flush();
				return true;
			} else {
				outc.write("Risposta sbagliata!\n");
				outc.flush();
				return false;
			}
		}catch(Exception e){
			return false;	
		}
	}

	@Override
	public void run(){					//Metodo di esecuzione per lettura, elaborazione, risposta

		//Thread per il controllo interruzione da sistema, con conseguente messaggio di disconnesione verso i client
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					System.out.println("\n*********************GAME OVER********************");
					if(!client.isClosed())
					{
						outc.write("quit");
						outc.flush();
					}

				}catch(IOException e){
					e.printStackTrace();
				};
			}
		});

		try
		{
			outc.write("Successo\n");
			outc.flush();

			
			nick = inc.readLine();
			
			if(nick != null)
			{
			System.out.println(this.client.getInetAddress()+": "+this.client.getLocalPort()+" AKA "+nick+"\n");

			outc.write("Benvenuto "+nick+"!\n");
			outc.flush();

			Random rand = new Random();

			int flag = 0;
			int count = 0;
			int c = 0;
			int s = 0;	
			do
			{
			flag = 0;	
			c = 0;
			s = Integer.parseInt(inc.readLine());

			switch(s)
			{
				case 1:
					f1 = new RandomAccessFile("Categorie/Attualita.txt","r");
					punt = 0;					
					for(i = f1.readLine(); i != null; i = f1.readLine())
					{
						dmd[punt]=i;
						res[punt]=f1.readLine();
						punt++;			
					}
					
					set = new LinkedHashSet<Integer>();
					while (set.size() < 4) {
						set.add(rand.nextInt(4));
					}
					list = set.toArray();
					do
					{
						outc.write(dmd[(Integer)list[c]]+"\n");
						outc.flush();
						if(check(inc.readLine(), res[(Integer)list[c]]))
						{
							count++;
						}else{
							flag++;
						}
						c++;
					}while(flag < 3 && c < 4);

					f1.close();
				break;
				case 2:
					f1 = new RandomAccessFile("Categorie/Cittadinanza_e_costituzione.txt","r");
					punt = 0;					
					for(i = f1.readLine(); i != null; i = f1.readLine())
					{
						dmd[punt]=i;
						res[punt]=f1.readLine();
						punt++;			
					}
					
					set = new LinkedHashSet<Integer>();
					while (set.size() < 4) {
						set.add(rand.nextInt(4));
					}
					list = set.toArray();
					do
					{
						outc.write(dmd[(Integer)list[c]]+"\n");
						outc.flush();
						if(check(inc.readLine(), res[(Integer)list[c]]))
						{
							count++;
						}else{
							flag++;
						}
						c++;
					}while(flag < 3 && c < 4);

					f1.close();

				break;
				case 3:
					f1 = new RandomAccessFile("Categorie/Informatica.txt","r");
					punt = 0;					
					for(i = f1.readLine(); i != null; i = f1.readLine())
					{
						dmd[punt]=i;
						res[punt]=f1.readLine();
						punt++;			
					}
					
					set = new LinkedHashSet<Integer>();
					while (set.size() < 5) {
						set.add(rand.nextInt(5));
					}
					list = set.toArray();
					do
					{
						outc.write(dmd[(Integer)list[c]]+"\n");
						outc.flush();
						if(check(inc.readLine(), res[(Integer)list[c]]))
						{
							count++;
						}else{
							flag++;
						}
						c++;
					}while(flag < 3 && c < 4);

					f1.close();

				break;
				case 4:
					f1 = new RandomAccessFile("Categorie/Storia.txt","r");
					punt = 0;					
					for(i = f1.readLine(); i != null; i = f1.readLine())
					{
						dmd[punt]=i;
						res[punt]=f1.readLine();
						punt++;			
					}
					
					set = new LinkedHashSet<Integer>();
					while (set.size() < 4) {
						set.add(rand.nextInt(4));
					}
					list = set.toArray();
					do
					{
						outc.write(dmd[(Integer)list[c]]+"\n");
						outc.flush();
						if(check(inc.readLine(), res[(Integer)list[c]]))
						{
							count++;
						}else{
							flag++;
						}
						c++;
					}while(flag < 3 && c < 4);

					f1.close();
				break;
				case 5:	
						
					f1 = new RandomAccessFile("Leaderboard.txt","r");

					while ((i = f1.readLine()) != null) 
					{
				    		if(i.equals(nick))
						{
							punt = 1;
							i = f1.readLine();

							if(Integer.parseInt(i) > c)
							{
								c = Integer.parseInt(i);
								msg = f1.readLine();
							}

						}	
					}
			       		if(punt == 1)
					{	
						outc.write("Il record di "+nick+" e' "+c+" su "+msg+".\n");
						outc.flush();
					}else{
						outc.write(nick+" non hai registrato alcun punteggio.\n");
						outc.flush();
					}	
					flag = -1;	
				break;
				default:
					flag = -1;
				break;
			}
			if(!client.isClosed() && flag != -1)
			{
				System.out.println(nick+" hai totalizzato "+count+" punti.");
				outc.write("Il tuo punteggio e' "+count+"! Salvare il punteggio? [s/n]\n");
				outc.flush();
			
				if((inc.readLine()).equals("s"))
				{
					try {
						if(s == 1) msg = "Attualita";
						if(s == 2) msg = "Cittadinanza_e_costituzione";
						if(s == 3) msg = "Informatica";
						if(s == 4) msg = "Storia";
				    		Files.write(Paths.get("Leaderboard.txt"), (nick+"\n"+count+"\n"+msg+"\n").getBytes(), StandardOpenOption.APPEND);
						outc.write("Salvataggio...\n");
						outc.flush();
						Thread.sleep(3000);
						outc.write("Il tuo punteggio e' stato salvato. (Invio)\n");
						outc.flush();
					}catch (Exception e) {
						System.out.println("\nErrore nel salvataggio del punteggio.");
						System.exit(-1);
					}
				}		
			}
			}while(s==4);
		}
		} catch (IOException e) 
		{
			System.out.println("Qualcosa e' andato storto.\n");
		}
		System.out.println("Chiusura connessione con "+this.client.getInetAddress()+" porta: "+this.client.getLocalPort());

		try {
			client.close();				//Chiusura comunicazione con il client e decremento del numero di connessioni
			TriviaQuiz.c--;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
