package service;

import entities.Dia;
import repository.DiaRepository;

import java.time.LocalTime;

public class DiaService {
    private final DiaRepository diaRepository = new DiaRepository();

    public Dia criarOuCarregarDia(String dataStr) {
        for (Dia d : diaRepository.findAll()) {
            if (d.getDataFormatada().equals(dataStr)) {
                return d;
            }
        }
        Dia novoDia = new Dia();
        novoDia.setData(dataStr);
        return diaRepository.insertDia(novoDia);
    }

    public void atualizarInicioTrabalho(Dia dia, LocalTime inicio) {
        dia.setInicioTrabalho(inicio);
        diaRepository.updateDia(dia);
    }

    public void atualizarFimTrabalho(Dia dia, LocalTime fim) {
        dia.setFimTrabalho(fim);
        diaRepository.updateDia(dia);
    }

    public void atualizarInicioAlmoco(Dia dia, LocalTime inicioAlmoco) {
        dia.setInicioAlmoco(inicioAlmoco);
        diaRepository.updateDia(dia);
    }

    public void atualizarFimAlmoco(Dia dia, LocalTime fimAlmoco) {
        dia.setFimAlmoco(fimAlmoco);
        diaRepository.updateDia(dia);
    }
}
