package org.gilmario.bot;

/**
 *
 * @author gilmario
 */
public class Mensagem {

    private String detalhe;
    private String resumo;
    private TipoMessagem tipo;

    public Mensagem(String detalhe, String resumo, TipoMessagem tipo) {
        this.detalhe = detalhe;
        this.resumo = resumo;
        this.tipo = tipo;
    }

    public Mensagem(String detalhe, String resumo) {
        this.detalhe = detalhe;
        this.resumo = resumo;
        this.tipo = TipoMessagem.SUCCESS;
    }

    public Mensagem(String detalhe) {
        this.detalhe = detalhe;
        this.tipo = TipoMessagem.SUCCESS;
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

    public TipoMessagem getTipo() {
        return tipo;
    }

    public void setTipo(TipoMessagem tipo) {
        this.tipo = tipo;
    }

}
