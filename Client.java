import java.io.*;
import java.net.*;

class Client{					//Defn
	private Socket server;
	private BufferedReader inS;
	private BufferedWriter outS;
	private String datin;
	private String datio;
	private String args;
	private int c;
	
	Client(String args){					//Costruttore
		this.server=null;
		this.inS=null;
		this.outS=null;
		this.datio="";
		this.datin="";
		this.args=args;	
	}
	
	public void connetti(){
		int port = Integer.parseInt(args);	
	
		//Thread per il controllo interruzione da sistema, con conseguente messaggio di disconnesione verso i client
                Runtime.getRuntime().addShutdownHook(new Thread()
                {
                        @Override
                        public void run()
                        {
                                        System.out.println("\nShutdown...");
                         }
                });
	
		try
		{
			server = new Socket("localhost",port);	//Connessione al server tramite indirizzo, e la porta passata come parametro da linea di comando
			
			//Apertura dei flussi di comunicazione
			inS = new BufferedReader(new InputStreamReader(server.getInputStream()));
			outS = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));

		} catch (IOException e) {
			System.out.println("Errore durante connessione");
			System.exit(-1);
		}
	
	//Buffer per la lettura da tastiera	
	InputStreamReader lett = new InputStreamReader(System.in);
        BufferedReader t = new BufferedReader(lett);

	System.out.println("Connessione..."); 
	try
        {	
		datin=inS.readLine();				//Attesa e controllo dell'esito del tentativo di connessione
		if(datin.equals("quit"))
		{
			System.out.println("Connessione rifiutata.");
			System.exit(-1);
		}else{
			System.out.println("Connessione accettata.");
			System.out.println("Inserisci il tuo nickname:");
			datio=t.readLine();
			outS.write(datio+"\n");
			outS.flush();
			datin=inS.readLine();
			if(!datin.equals("quit"))		//Controllo messaggio di uscita
			{
				System.out.println("Server: "+datin+"");
			}else{	
				System.out.println("Disconnesso.");
				System.exit(0);
			}
		}
	}	
	catch (IOException e) {
                e.printStackTrace();
        }
	
	do
	{
		try {
			System.out.println("Quale categoria vorresti affrontare?\n1) Attualita;\n2) Cittadinanza e costituzione;\n3) Informatica;\n4) Storia\n\n5) Visualizza il tuo record.\n0) -EXIT-");
			datin = t.readLine();
			outS.write(""+datin+"\n");
			outS.flush();
			if(datin.equals("4"))
			{
				System.out.print("\033[H\033[2J");			//Pulizia schermo
				System.out.println(inS.readLine()+"\n");
			}
		}catch(IOException e){};

	}while(datin.equals("4"));

	do
        {
                try 
		{       
			datin=inS.readLine();
			if(!datin.equals("quit"))		//Controllo messaggio di uscita
			{
				System.out.println("Server: "+datin+"");
			}else{	
				System.out.println("Disconnesso.");
				System.exit(0);
			}
	
			datio=t.readLine();
			outS.write(datio+"\n");
			outS.flush();
			datin=inS.readLine();
			if(!datin.equals("quit"))		//Controllo messaggio di uscita
			{
				System.out.println("Server: "+datin+"");
			}else{	
				System.out.println("Disconnesso.");
				System.exit(0);
			}
			c++;
		
		}
		catch (Exception e) 
		{
                      System.out.println("Disconnesso.");
		      System.exit(0); 
		}
        }while(!datin.equals(0));			//Finchè la stringa inserita non è "quit", richiede una stringa da inserire ed inviare al server
	}
	
	public static void main(String args[])					//Programma principale
	{
		try
		{	
			Client prova = new Client(args[0]);			//Definizione di un oggetto client
			System.out.print("\033[H\033[2J");			//Pulizia schermo
			prova.connetti();					//Esecuzione del client
		}catch (ArrayIndexOutOfBoundsException e)
		{
			System.out.println("\nNon e' stata inserita alcuna porta.\n");
		}
	}

}
