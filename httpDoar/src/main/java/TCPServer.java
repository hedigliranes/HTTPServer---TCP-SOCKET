package main.java;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TCPServer {
	
	static int id = 0;
	static List<Contatos> lc;
	
	public static void main(String args[]){
		lc = new ArrayList<>();

		ServerSocket server = null;
		try {
			server = new ServerSocket (8080);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		while(true)
			{
				try{
					Socket nextClient = server.accept();
					ObjectInputStream input = new ObjectInputStream(nextClient.getInputStream());
					MsgTCP msg = (MsgTCP)input.readObject();
					System.out.println("Tipo:"+msg.getType()+
							"Mensagem:"+msg.getBody());
					
					if(msg.getType().equals("GET")) {
						String listString = lc.stream().map(Object::toString)
				                .collect(Collectors.joining(""));
						envio("Code: 200 \n"
								+ "Body Response: \n" + listString ,msg.getPort());

					}else if(msg.getType().equals("POST")) {
						Contatos aux = msg.getBody();
						aux.setId(id);
						id++;
						lc.add(aux);
						envio("Code: 200 \n"
								+ "Body Response: \n" + aux.toString() , msg.getPort());
					}else if(msg.getType().equals("PUT")) {
						Contatos aux = msg.getBody();
						Boolean find = false;
						for(Contatos c : lc){
							if(c.getId() == aux.getId()) {
								c.setNome(aux.getNome());
								c.setNumero(aux.getNumero());
								find = true;
								envio("Code: 200 \n"
										+ "Body Response: \n" + c.toString() , msg.getPort());
							}
						}
						
						if(!find) {
							envio("Code: 200 \n"
									+ "Body Response: Nenhum usuário encontrado com o ID informado \n", msg.getPort());
						}
						
					}else if(msg.getType().equals("DELETE")) {
						Contatos aux = msg.getBody();
						Boolean find = false;
						for(int i = 0; i < lc.size();i++) {
							if(lc.get(i).getId() == aux.getId()) {
								lc.remove(i);
								find = true;
								envio("Code: 200 \n"
										+ "Body Response: Usuário Deletado ! \n", msg.getPort());
								break;
							}
							
							if(!find) {
								envio("Code: 200 \n"
										+ "Body Response: Nenhum usuário encontrado com o ID informado \n", msg.getPort());
							}
						}
					}else {
						
					}
					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				try {
					server.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	
	public static void envio(String msg, Integer port) {
		
		try {
			InetAddress hostIP= InetAddress.getLocalHost();
			InetSocketAddress myAddress = new InetSocketAddress(hostIP, port);
			SocketChannel myClient2 = SocketChannel.open(myAddress);
	
			logger(String.format("Trying to connect to %s:%d...",
					myAddress.getHostName(), myAddress.getPort()));
	
				ByteBuffer myBuffer2 = ByteBuffer.allocate(1024);
				myBuffer2.put(msg.getBytes());
				myBuffer2.flip();
				int bytesWritten= myClient2.write(myBuffer2);
				logger(String.format("Sending Message...: %s\nbytesWritten...: %d",
						msg, bytesWritten));
						
			logger("Closing Client connection...");
			myClient2.close();
		} 
		catch (IOException e) 
		{
			logger(e.getMessage());
			//e.printStackTrace();
		}
		
	}
	
	public static void logger(String msg) 
	{
		System.out.println(msg);
	}
	
}
