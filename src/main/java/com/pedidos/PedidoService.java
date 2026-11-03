package com.pedidos;

import java.util.List;

/** Valida pedidos e aplica as regras de negócio */
public class PedidoService {
    
    private final PedidoRepository repository;
    
    public PedidoService() {
        this.repository = new PedidoRepository();
        System.out.println(" Service inicializado");
    }
    
    /** Valida os dados e salva um novo pedido */
    public Pedido criarPedido(String cliente, String produto, int quantidade) {
        System.out.println("\n Processando novo pedido:");
        System.out.println("  Cliente: " + cliente);
        System.out.println("  Produto: " + produto);
        System.out.println("  Quantidade: " + quantidade);
        
        // Confere se os dados atendem às regras do sistema.
        validarDados(cliente, produto, quantidade);
        
        // Cria o objeto do pedido.
        Pedido pedido = new Pedido(cliente, produto, quantidade);
        System.out.println("Pedido criado: " + pedido.getId());
        
        // Salva o pedido no repositório.
        return repository.salvar(pedido);
    }
    
    /** Retorna os pedidos salvos */
    public List<Pedido> listarPedidos() {
        System.out.println("\n📋 Listando todos os pedidos");
        List<Pedido> pedidos = repository.listarTodos();
        System.out.println("   Total encontrado: " + pedidos.size());
        return pedidos;
    }
    
    /** Verifica se os dados do pedido são válidos */
    private void validarDados(String cliente, String produto, int quantidade) {
        if (cliente == null || cliente.trim().isEmpty()) {
            throw new IllegalArgumentException("Cliente é obrigatório");
        }
        
        if (produto == null || produto.trim().isEmpty()) {
            throw new IllegalArgumentException("Produto é obrigatório");
        }
        
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        
        if (quantidade > 100) {
            throw new IllegalArgumentException("Quantidade máxima é 100 unidades");
        }
        
        System.out.println("Validações OK");
    }
}
