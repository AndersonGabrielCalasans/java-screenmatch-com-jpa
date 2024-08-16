package br.com.alura.screenmatch.model;

import br.com.alura.screenmatch.service.ConsultaChatGPT;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Entity
@Table(name = "series")
public class Serie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    @NotNull
    private String titulo;
    
    @Column(name = "total_temporadas")
    private Integer totalTemporadas;
    
    private Double avaliacao;
    
    private String poster;
    
    @Enumerated(EnumType.STRING)
    private Categoria genero;
    
    private String atores;
    
    private String sinopse;
    
    @Transient // Não reflete no banco de dados
    private List<Episodio> episodios = new ArrayList<>();
    
    public Serie(DadosSerie dadosSerie) {
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();
        this.avaliacao = OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).orElse(0.0); //Valor padrão 0 caso erro
        this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim()); //Retorna só o primeiro item da string
        this.atores = dadosSerie.atores();
        this.poster = dadosSerie.poster();
        this.sinopse = ConsultaChatGPT.obterTraducao(dadosSerie.sinopse().trim());  // Obtem a tradução da sinopse
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }
    
    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }
    
    public Double getAvaliacao() {
        return avaliacao;
    }
    
    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }
    
    public String getPoster() {
        return poster;
    }
    
    public void setPoster(String poster) {
        this.poster = poster;
    }
    
    public Categoria getGenero() {
        return genero;
    }
    
    public void setGenero(Categoria genero) {
        this.genero = genero;
    }
    
    public String getAtores() {
        return atores;
    }
    
    public void setAtores(String atores) {
        this.atores = atores;
    }
    
    public String getSinopse() {
        return sinopse;
    }
    
    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }
    
    public List<Episodio> getEpisodios() {
        return episodios;
    }
    
    public void setEpisodios(List<Episodio> episodios) {
        this.episodios = episodios;
    }
    
    @Override
    public String toString() {
        return  "titulo='" + titulo + '\'' +
                ", totalTemporadas=" + totalTemporadas +
                ", avaliacao=" + avaliacao +
                ", poster='" + poster + '\'' +
                ", genero=" + genero +
                ", atores='" + atores + '\'' +
                ", sinopse='" + sinopse + '\'';
    }
}
