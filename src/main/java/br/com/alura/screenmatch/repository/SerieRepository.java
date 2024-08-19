package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// TO-DO: Derived-Query para criar metodos com base em padroes de SQL (JPA Query Methods)
public interface SerieRepository extends JpaRepository<Serie, Long> {
    
    // Encontra uma serie pelo titulo ignorando cases sensitive
    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);
    
    // Consulta por atores em que a avaliação é maior ou igual à algum valor
    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);
    
    // Retorna as 5 melhores series de forma decrescente
    List<Serie> findTop5ByOrderByAvaliacaoDesc();
    
    // Busca séries pela categoria
    List<Serie> findByCategoria(Categoria categoria);
    
    List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(Integer numeroDeTemporadas, Double avaliacao);
}
