package main.java;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class TCPClient {
	private static final int BUFFER_SIZE = 1024;
	private static Selector selector = null;
	
	public static boolean TCPServerResponse(MsgTCP msg) {
		try{
			InetAddress hostIP= InetAddress.getLocalHost();
			ServerSocketChannel mySocket = ServerSocketChannel.open();

			selector = Selector.open();
			ServerSocket serverSocket = mySocket.socket();
			InetSocketAddress address = new InetSocketAddress(hostIP, 0);
			serverSocket.bind(address);
			System.out.println("Client Token Server Started: ");
			
			Socket conexao = null;
			try {
				conexao = new Socket ("localhost", 8080);
				ObjectOutputStream output = new
				ObjectOutputStream(conexao.getOutputStream());
				msg.setPort(serverSocket.getLocalPort());
				
				output.writeObject(msg);
				output.flush();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					conexao.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
						
			mySocket.configureBlocking(false);
			//int ops = mySocket.validOps();
			// mySocket.register(selector, ops, null);
			mySocket.register(selector,SelectionKey.OP_ACCEPT);
			while (true) 
			{
				selector.select();
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> i = selectedKeys.iterator();
				while (i.hasNext()) 
				{
					SelectionKey key = i.next();
					if (key.isAcceptable()) 
					{
						processAcceptEvent(mySocket, key);
						 
					} 
					else if (key.isReadable()) 
					{
						processReadEvent(key);
						  
					 return false;
					}
					i.remove();
				}
			}
		} 
		catch (IOException e){
			
			e.printStackTrace();
		
	}catch (Exception e) {
		//e.printStackTrace();
		System.out.println("TCP Server Terminating");		
	}
		
	System.out.println("TCP Server Terminating");			
	return false;

	}
	
	public static void main(String args[]){
		System.out.println("TCP Client Started");
		Scanner scanner = new Scanner(System.in);
		MsgTCP msg = new MsgTCP();
		
		while(true) {
			System.out.print("1 - Listar Contatos (GET) \n");
			System.out.print("2 - Adicionar Contatos (POST) \n");
			System.out.print("3 - Editar Contatos (PUT) \n");
			System.out.print("4 - Deletar Contatos (DELETE) \n");
			System.out.print("Escolha uma opção: ");
			String opt1 = scanner.nextLine();
			
			if ("1".equalsIgnoreCase(opt1)) {
				msg.setType("GET");

				TCPServerResponse(msg);
			}else if ("2".equalsIgnoreCase(opt1)) {
				System.out.print("Nome: \n");
				String nome = scanner.nextLine();
				System.out.print("Telefone: \n");
				String tel = scanner.nextLine();
				Contatos c = new Contatos(-1, nome, tel);
				msg.setType("POST");
				msg.setBody(c);
				TCPServerResponse(msg);
			}else if ("3".equalsIgnoreCase(opt1)) {
				System.out.print("Digite o ID do usuário: \n");
				String id = scanner.nextLine();
				System.out.print("Nome: \n");
				String nome = scanner.nextLine();
				System.out.print("Telefone: \n");
				String tel = scanner.nextLine();
				Contatos c = new Contatos(Integer.parseInt(id), nome, tel);
				msg.setType("PUT");
				msg.setBody(c);
				TCPServerResponse(msg);
			}else if ("4".equalsIgnoreCase(opt1)) {
				System.out.print("Digite o ID do usuário: \n");
				String id = scanner.nextLine();
				Contatos c = new Contatos(Integer.parseInt(id), "", "");
				msg.setType("DELETE");
				msg.setBody(c);
				TCPServerResponse(msg);
			}else {
				logger("Digite um Número Válido !\n");
			}
		  }
		}
	
	private static void processAcceptEvent(ServerSocketChannel mySocket,
			SelectionKey key) throws IOException 
	{
		// Accept the connection and make it non-blocking
		SocketChannel myClient = mySocket.accept();
		myClient.configureBlocking(false);
		// Register interest in reading this channel
		myClient.register(selector, SelectionKey.OP_READ);
	}
			
	private static void processReadEvent(SelectionKey key) throws IOException 
	{
		// create a ServerSocketChannel to read the request
		SocketChannel myClient = (SocketChannel) key.channel();
		// Set up out 1k buffer to read data into
		ByteBuffer myBuffer = ByteBuffer.allocate(BUFFER_SIZE);
		myClient.read(myBuffer);
		String data = new String(myBuffer.array()).trim();
		if (data.length() > 0) 
		{
			logger(data);
			myClient.close();
			
		}else {
			myClient.close();
		}
	}
	
	public static void logger(String msg) 
	{
		System.out.println(msg);
	}
}
