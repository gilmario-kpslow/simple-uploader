package org.gilmario.bot.arquivo;

import java.time.LocalDateTime;

/**
 *
 * @author gilmario
 */
public class PathObject {

    private String nome;
    private Boolean diretorio;
    private Long tamanho;
    private LocalDateTime ultimaModificacao;

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

    public LocalDateTime getUltimaModificacao() {
        return ultimaModificacao;
    }

    public void setUltimaModificacao(LocalDateTime ultimaModificacao) {
        this.ultimaModificacao = ultimaModificacao;
    }

}
