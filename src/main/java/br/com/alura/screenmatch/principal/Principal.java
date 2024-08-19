package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    
    // Lista de Series
    private List<DadosSerie> dadosSeries = new ArrayList<>();
    private List<Serie> series = new ArrayList<>();
    
    private SerieRepository repositorio;
    
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=75cc07dd";
    
    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }
    
    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    \n1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série por título
                    5 - Buscar séries por ator
                    6 - Buscar top 5 séries
                    7 - Buscar séries por categoria
                                        
                    0 - Sair
                    """;
            
            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();
        }
        switch (opcao) {
            case 1:
                buscarSerieWeb();
                break;
            case 2:
                buscarEpisodioPorSerieV2();
                break;
            case 3:
                listarSeriesBuscadasV2();
                break;
            case 4:
                buscarSeriePorTitulo();
                break;
            case 5:
                buscarSeriesPorAtor();
                break;
            case 6:
                buscarTop5Series();
                break;
            case 7:
                buscarSeriesPorCategoria();
                break;
            case 8:
                filtrarSeriesPorNumeroTemporadasEAvaliacao();
                break;
            case 0:
                System.out.println("Saindo...");
                break;
            default:
                System.out.println("Opção inválida");
        }
    }
    


//    private void buscarSerieWeb() {
//        DadosSerie dados = getDadosSerie();
//        dadosSeries.add(dados);
//        System.out.println(dados);
//    }
    
    private void buscarSerieWeb() {
        Serie serie = getSerie();
        series.add(serie);
        System.out.println(series);
    }
    
    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }
    
    private Serie getSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        Serie serie = new Serie(dados);
        return serie;
    }
    
    /**
     * @deprecated Use {@link #buscarEpisodioPorSerieV2()} em vez dela.
     */
    private void buscarEpisodioPorSerie() {
        DadosSerie dadosSerie = getDadosSerie();
        List<DadosTemporada> temporadas = new ArrayList<>();
        
        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            var json = consumo.obterDados(ENDERECO + dadosSerie.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);
    }
    
    private void buscarEpisodioPorSerieV2() {
        
        listarSeriesBuscadasV2();
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();
        
        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        
        if (serie.isPresent()) {
            
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();
            
            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);
            
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(t -> t.episodios().stream()
                            .map(e -> new Episodio(t.numero(), e)))
                    .collect(Collectors.toList());
            
            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
            
        } else {
            System.out.println("Serie não encontrada!");
        }
    }
    
    /**
     * @deprecated Use {@link #listarSeriesBuscadasV2()} em vez dela.
     */
    @Deprecated
    private void listarSeriesBuscadas() {
        // Parte desnecessária pois objeto já foi convertido na criação
        List<Serie> series = new ArrayList<>();
        
        // Transformar DadosSerie em Serie
        dadosSeries.stream()
                .map(d -> new Serie(d))
                .collect(Collectors.toList());
        
        // Ordenar as series por genero e imprimir
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }
    
    private void listarSeriesBuscadasV2() {
        // Busca todas as séries no banco
        series = repositorio.findAll();
        
        // Ordenar as series por genero e imprimir
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }
    
    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();
        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        
        if (serie.isPresent()) {
            System.out.println("Dados da série: " + serie.get());
        } else {
            System.out.println("Série não encontrada!");
        }
    }
    
    private void buscarSeriesPorAtor() {
        
        System.out.println("Qual o nome para busca: ");
        var nomeAtor = leitura.nextLine();
        
        System.out.println("Avaliações a partir de que valor? ");
        var avaliacao = leitura.nextDouble();
        
        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        
        System.out.println("Séries em que " + nomeAtor + " trabalhou: ");
        seriesEncontradas.forEach(s ->
                System.out.println(s.getTitulo() + "avaliação: " + s.getAvaliacao()));
    }
    
    private void buscarTop5Series() {
        
        List<Serie> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        
        serieTop.forEach(s ->
                System.out.println(s.getTitulo() + "avaliação: " + s.getAvaliacao()));
    }
    
    private void buscarSeriesPorCategoria() {
        
        System.out.println("Digite a categoria que deseja buscar: ");
        var nomeGenero = leitura.nextLine();

        // Transforma String em Enum Categoria
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> seriesPorCategoria = repositorio.findByCategoria(categoria);
        
        System.out.println("Séries de " + categoria + ": ");
        seriesPorCategoria.forEach(System.out::println);
    }
    
    private void filtrarSeriesPorNumeroTemporadasEAvaliacao() {
        System.out.println("Qual o numero máximo de temporadas da série para busca? ");
        var maxTemporadas = leitura.nextInt();
        
        System.out.println("Avaliações a partir de que valor? ");
        var avaliacao = leitura.nextDouble();
        
        List<Serie> seriesFiltradas = repositorio.findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(maxTemporadas, avaliacao);
        
        System.out.println("Series filtradas: ");
        seriesFiltradas.forEach(s ->
                System.out.println(s.getTitulo() + "temporadas: " + s.getTotalTemporadas() + " avaliação: " + s.getAvaliacao()));
    }
}
