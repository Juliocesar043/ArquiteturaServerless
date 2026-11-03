package com.pedidos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

/** Representa um pedido e seus dados */
public class Pedido {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("cliente")
    private String cliente;
    
    @JsonProperty("produto")
    private String produto;
    
    @JsonProperty("quantidade")
    private int quantidade;
    
    @JsonProperty("valor")
    private double valor;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("dataCriacao")
    private String dataCriacao;
    
    // O Jackson usa este construtor ao ler JSON
    public Pedido() {
    }
    
    // Cria um pedido e define seus valores iniciais
    public Pedido(String cliente, String produto, int quantidade) {
        this.id = UUID.randomUUID().toString();
        this.cliente = cliente;
        this.produto = produto;
        this.quantidade = quantidade;
        this.valor = calcularValor(produto, quantidade);
        this.status = "PENDENTE";
        this.dataCriacao = Instant.now().toString();
    }

    private double calcularValor(String produto, int quantidade) {
        return 100.0 * quantidade;
    }
    
    // Getters e Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getCliente() {
        return cliente;
    }
    
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }
    
    public String getProduto() {
        return produto;
    }
    
    public void setProduto(String produto) {
        this.produto = produto;
    }
    
    public int getQuantidade() {
        return quantidade;
    }
    
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
    
    public double getValor() {
        return valor;
    }
    
    public void setValor(double valor) {
        this.valor = valor;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    @Override
    public String toString() {
        return String.format("Pedido{id='%s', cliente='%s', produto='%s', quantidade=%d, valor=%.2f, status='%s'}", 
                           id, cliente, produto, quantidade, valor, status);
    }
}
