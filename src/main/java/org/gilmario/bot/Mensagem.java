/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.gilmario.bot;

/**
 *
 * @author gilmario
 */
public class Mensagem {

    private String detalhe;
    private String resumo;
    private String tipo;

    public Mensagem(String detalhe, String resumo, String tipo) {
        this.detalhe = detalhe;
        this.resumo = resumo;
        this.tipo = tipo;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(String detalhe) {
        this.detalhe = detalhe;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

}
