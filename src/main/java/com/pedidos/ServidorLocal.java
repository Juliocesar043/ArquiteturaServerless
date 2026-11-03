package com.pedidos;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/** Inicia um servidor HTTP local para receber pedidos */
public class ServidorLocal {
    
    private static final int PORTA = 8080;
    private static final PedidoService service = new PedidoService();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static void main(String[] args) throws IOException {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║    Sistema de Pedidos - Arquitetura Serverless     ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Inicia o servidor HTTP na porta configurada
        HttpServer servidor = HttpServer.create(new InetSocketAddress(PORTA), 0);
        
        // Registra os caminhos disponíveis
        servidor.createContext("/pedidos", new ManipuladorPedidos());
        servidor.createContext("/", new ManipuladorRaiz());
        
        servidor.setExecutor(null);
        servidor.start();
        
        System.out.println(" Servidor iniciado: http://localhost:" + PORTA);
        System.out.println();
        System.out.println(" Endpoints:");
        System.out.println("  • POST http://localhost:" + PORTA + "/pedidos   (criar pedido)");
        System.out.println("  • GET  http://localhost:" + PORTA + "/pedidos   (listar pedidos)");
        System.out.println();
        System.out.println("Executando LOCALMENTE");
        System.out.println("(mesma arquitetura que rodaria na AWS Lambda)");
        System.out.println();
        System.out.println("Pressione Ctrl+C para parar");
        System.out.println("════════════════════════════════════════════════════");
        System.out.println();
    }
    
    /** Processa as requisições para /pedidos */
    static class ManipuladorPedidos implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String metodo = exchange.getRequestMethod();
            
            System.out.println("─────────────────────────────────────────");
            System.out.println("Nova requisição: " + metodo + " /pedidos");
            
            try {
                if ("POST".equals(metodo)) {
                    handleCriar(exchange);
                } else if ("GET".equals(metodo)) {
                    handleListar(exchange);
                } else {
                    enviarResposta(exchange, 405, "{\"erro\":\"Método não permitido\"}");
                }
            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
                enviarResposta(exchange, 500, "{\"erro\":\"Erro interno\"}");
            }
            
            System.out.println("─────────────────────────────────────────\n");
        }
        
        /** Lê os dados recebidos e cria um pedido. */
        private void handleCriar(HttpExchange exchange) throws IOException {
            try {
                // Lê o JSON enviado na requisição
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                
                JsonNode json = objectMapper.readTree(body);
                String cliente = json.get("cliente").asText();
                String produto = json.get("produto").asText();
                int quantidade = json.get("quantidade").asInt();
                
                // Valida os dados e cria o pedido
                Pedido pedido = service.criarPedido(cliente, produto, quantidade);
                
                // Converte o pedido para JSON e envia a resposta
                String resposta = objectMapper.writeValueAsString(pedido);
                enviarResposta(exchange, 201, resposta);
                
                System.out.println("Resposta enviada: 201 Created");
                
            } catch (IllegalArgumentException e) {
                String erro = "{\"erro\":\"" + e.getMessage() + "\"}";
                enviarResposta(exchange, 400, erro);
                System.out.println("Erro de validação: " + e.getMessage());
            }
        }
        
        /** Retorna a lista de pedidos */
        private void handleListar(HttpExchange exchange) throws IOException {
            // Busca os pedidos no serviço.
            List<Pedido> pedidos = service.listarPedidos();
            
            Map<String, Object> resposta = Map.of(
                "pedidos", pedidos,
                "total", pedidos.size()
            );
            
            String json = objectMapper.writeValueAsString(resposta);
            enviarResposta(exchange, 200, json);
            
            System.out.println("Resposta enviada: 200 OK");
        }
    }
    
    /** Exibe a página inicial com instruções de uso */
    static class ManipuladorRaiz implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <title>Sistema de Pedidos Serverless</title>
                    <style>
                        body { 
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
                            max-width: 900px; 
                            margin: 50px auto; 
                            padding: 20px;
                            background: #f5f5f5;
                        }
                        .container {
                            background: white;
                            padding: 30px;
                            border-radius: 10px;
                            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                        }
                        h1 { color: #333; border-bottom: 3px solid #007bff; padding-bottom: 10px; }
                        h2 { color: #555; margin-top: 30px; }
                        .endpoint { 
                            background: #f8f9fa; 
                            padding: 20px; 
                            margin: 15px 0; 
                            border-radius: 8px;
                            border-left: 4px solid #007bff;
                        }
                        .method { 
                            font-weight: bold; 
                            color: #007bff; 
                            font-size: 18px;
                        }
                        code { 
                            background: #e9ecef; 
                            padding: 3px 8px; 
                            border-radius: 4px;
                            font-family: 'Courier New', monospace;
                        }
                        pre {
                            background: #2d2d2d;
                            color: #f8f8f2;
                            padding: 15px;
                            border-radius: 5px;
                            overflow-x: auto;
                        }
                        .success { color: #28a745; font-weight: bold; }
                        .info { 
                            background: #d1ecf1; 
                            border: 1px solid #bee5eb; 
                            padding: 15px; 
                            border-radius: 5px;
                            margin: 20px 0;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>Sistema de Pedidos - Arquitetura Serverless</h1>
                        
                        <div class="info">
                            <p class="success">Servidor rodando em: <code>http://localhost:8080</code></p>
                            <p><strong>Demonstração local</strong> da arquitetura serverless<br>
                               (mesma estrutura que rodaria na AWS Lambda)</p>
                        </div>
                        
                        <h2>Endpoints Disponíveis</h2>
                        
                        <div class="endpoint">
                            <div class="method">POST /pedidos</div>
                            <p>Cria um novo pedido</p>
                            <pre>curl -X POST http://localhost:8080/pedidos \\
  -H "Content-Type: application/json" \\
  -d '{
    "cliente": "João Silva",
    "produto": "Notebook Dell",
    "quantidade": 2
  }'</pre>
                        </div>
                        
                        <div class="endpoint">
                            <div class="method">GET /pedidos</div>
                            <p>Lista todos os pedidos</p>
                            <pre>curl http://localhost:8080/pedidos</pre>
                        </div>
                        
                        <h2>Arquitetura</h2>
                        <pre>
Cliente HTTP
    ↓
ServidorLocal (simula API Gateway)
    ↓
PedidoService (lógica de negócio)
    ↓
PedidoRepository (acesso a dados)
    ↓
Memória (localmente) ou DynamoDB (na nuvem)</pre>
                        
                        <h2>Como seria na nuvem?</h2>
                        <ul>
                            <li><strong>API Gateway</strong> → recebe requisições HTTP</li>
                            <li><strong>Lambda Function</strong> → executa PedidoService</li>
                            <li><strong>DynamoDB</strong> → salva os dados</li>
                        </ul>
                        <p><strong>O código do Service e Repository seria O MESMO!</strong></p>
                    </div>
                </body>
                </html>
                """;
            
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, html.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(html.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
    }
    
    /** Envia uma resposta HTTP com status e conteúdo */
    private static void enviarResposta(HttpExchange exchange, int statusCode, String body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
