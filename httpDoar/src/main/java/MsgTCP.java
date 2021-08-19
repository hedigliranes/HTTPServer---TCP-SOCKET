package main.java;

import java.io.Serializable;

public class MsgTCP implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Contatos body;
	private String type;
	private Integer port;
	
	public MsgTCP() {
		super();
	}

	public Contatos getBody() {
		return body;
	}

	public void setBody(Contatos body) {
		this.body = body;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
	
	
}
