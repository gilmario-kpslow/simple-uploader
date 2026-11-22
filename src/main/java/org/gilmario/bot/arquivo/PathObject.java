package org.gilmario.bot.arquivo;

/**
 *
 * @author gilmario
 */
public class PathObject {

    private String nome;
    private Boolean diretorio;
    private Long tamanho;
    private String ultimaModificacao;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getDiretorio() {
        return diretorio;
    }

    public void setDiretorio(Boolean diretorio) {
        this.diretorio = diretorio;
    }

    public Long getTamanho() {
        return tamanho;
    }

    public void setTamanho(Long tamanho) {
        this.tamanho = tamanho;
    }

    public String getUltimaModificacao() {
        return ultimaModificacao;
    }

    public String getUltimaModificacaoString() {
        return ultimaModificacao;
    }

    public void setUltimaModificacao(String ultimaModificacao) {
        this.ultimaModificacao = ultimaModificacao;
    }

}
