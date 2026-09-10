package model;

public class Quiz {
    private int id;
    private String titulo;

    // Construtor vazio
    public Quiz() {
        this.id = -1;
        this.titulo = "";
    }

    // Novo construtor adaptado apenas com ID e Título
    public Quiz(int id, String titulo) {
        this.id = id;
        this.titulo = titulo;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    @Override
    public String toString() {
        return "Quiz [id=" + id + ", titulo=" + titulo + "]";
    }
}