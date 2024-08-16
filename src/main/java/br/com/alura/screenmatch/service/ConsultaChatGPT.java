package br.com.alura.screenmatch.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

/**
 * Classe para utilizar o ChatGPT para traduzir o texto da sinopse
 */
public class ConsultaChatGPT {
    
    // TO-DO: precisa gerar o token da API e colocar nas variaveis de ambiente para funcionar
    public static String obterTraducao(String texto) {
        OpenAiService servico = new OpenAiService(System.getenv("OPENAI_APIKEY"));
        
        CompletionRequest requisicao = CompletionRequest.builder()
                .model("gpt-3.5-turbo-instruct")
                .prompt("traduza para o português o texto: " + texto)
                .maxTokens(1000)
                .temperature(0.7)
                .build();
        
        var resposta = servico.createCompletion(requisicao);
        return resposta.getChoices().get(0).getText();
    }
}
