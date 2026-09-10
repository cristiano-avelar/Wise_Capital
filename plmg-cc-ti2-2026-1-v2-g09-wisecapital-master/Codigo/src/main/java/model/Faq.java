package model;

public class Faq {
    private int id;
    private String pergunta;
    private String resposta;
    private int acessos; // Novo atributo

    public Faq() {}

    public Faq(int id, String pergunta, String resposta, int acessos) {
        this.id = id;
        this.pergunta = pergunta;
        this.resposta = resposta;
        this.acessos = acessos;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPergunta() { return pergunta; }
    public void setPergunta(String pergunta) { this.pergunta = pergunta; }

    public String getResposta() { return resposta; }
    public void setResposta(String resposta) { this.resposta = resposta; }

    public int getAcessos() { return acessos; }
    public void setAcessos(int acessos) { this.acessos = acessos; }
}