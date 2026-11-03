package com.pedidos;

import java.util.*;

/** Armazena e consulta os pedidos */
public class PedidoRepository {
    
    // Guarda os pedidos em memória.
    private final Map<String, Pedido> storage = new HashMap<>();
    
    public PedidoRepository() {
        System.out.println(" Repository inicializado: armazenamento em MEMÓRIA");
        System.out.println("  (Em produção serverless: seria DynamoDB, Firestore, etc.)");
    }
    
    /** Salva um pedido no armazenamento */
    public Pedido salvar(Pedido pedido) {
        storage.put(pedido.getId(), pedido);
        System.out.println("    Pedido salvo: " + pedido.getId());
        return pedido;
    }
    
    /** Retorna todos os pedidos salvos */
    public List<Pedido> listarTodos() {
        return new ArrayList<>(storage.values());
    }
    
    /** Busca um pedido pelo ID */
    public Optional<Pedido> buscarPorId(String id) {
        return Optional.ofNullable(storage.get(id));
    }
}
