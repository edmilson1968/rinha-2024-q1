package br.com.edm.app.rinha.model;

public class Clientes {

    private Long id;
    private Integer limite;
    private Integer saldo;

    public Clientes(Integer limite, Integer saldo) {
        this.limite = limite;
        this.saldo = saldo;
    }

    public Integer getLimite() {
        return limite;
    }

    public Integer getSaldo() {
        return saldo;
    }

}
