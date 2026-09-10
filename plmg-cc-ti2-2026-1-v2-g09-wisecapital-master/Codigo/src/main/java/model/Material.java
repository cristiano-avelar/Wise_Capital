package model;

public class Material {
    private int id;
    private String titulo;
    private String descricao;
    private String tipo;
    private String categoria;
    private String urlConteudo;

    public Material() {}

    public Material(int id, String titulo, String descricao, String tipo, String categoria, String urlConteudo) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.tipo = tipo;
        this.categoria = categoria;
        this.urlConteudo = urlConteudo;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getUrlConteudo() { return urlConteudo; }
    public void setUrlConteudo(String urlConteudo) { this.urlConteudo = urlConteudo; }
}