package main.java;

import java.io.Serializable;

public class Contatos implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String nome;
	private String numero;
	
	public Contatos(int id, String nome, String numero) {
		super();
		this.id = id;
		this.nome = nome;
		this.numero = numero;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}	
	
	@Override
	public String toString() {
		return "Contatos: id: " + id + ", nome: " + nome + ", telefone: " + numero
				+ "\n";
	}

}
