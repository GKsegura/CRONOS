package entities;

import org.jetbrains.annotations.NotNull;

public class Task implements Comparable<Task> {
    private Long id; // novo
    private String descricao;
    private Categoria categoria;
    private String cooperativa;
    private Long duracaoMin;

    public Task(String descricao, Categoria categoria, String cooperativa){
        this.descricao = descricao;
        this.categoria = categoria;
        this.cooperativa = cooperativa;
    }

    public Task(String descricao, Categoria categoria, String cooperativa, Long duracaoMin){
        this.descricao = descricao;
        this.categoria = categoria;
        this.cooperativa = cooperativa;
        this.duracaoMin = duracaoMin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao(){
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getCooperativa(){
        return cooperativa;
    }

    public void setCooperativa(String cooperativa){
        this.cooperativa = cooperativa;
    }

    public Long getDuracaoMin() {
        return duracaoMin;
    }

    public void setDuracaoMin(Long duracaoMin) {
        this.duracaoMin = duracaoMin;
    }

    public String getDuracaoHora(){
        return formatarDuracao(duracaoMin);
    }

    @Override
    public String toString() {
        String cat = (categoria != null ? categoria.toString() : "Sem categoria");
        String coop = (cooperativa != null && !cooperativa.isEmpty() ? cooperativa : "N/A");

        String base = descricao + " [" + cat + "] [" + coop + "]";

        if (duracaoMin != null) {
            base += " (" + formatarDuracao(duracaoMin) + ")";
        }

        return base;
    }

    private String formatarDuracao(Long minutos) {
        long h = minutos / 60;
        long m = minutos % 60;
        return String.format("%02dh%02dm", h, m);
    }

    @Override
    public int compareTo(@NotNull Task outra) {
        return this.descricao.compareToIgnoreCase(outra.descricao);
    }
}
