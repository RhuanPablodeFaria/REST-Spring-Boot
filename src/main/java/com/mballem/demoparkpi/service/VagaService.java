package com.mballem.demoparkpi.service;

import com.mballem.demoparkpi.entyti.Vaga;
import com.mballem.demoparkpi.exception.CodigoUniqueViolationException;
import com.mballem.demoparkpi.exception.EntityNotFoundException;
import com.mballem.demoparkpi.exception.VagaDisponivelException;
import com.mballem.demoparkpi.repository.VagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class VagaService {

    private final VagaRepository vagaRepository;

    @Transactional
    public Vaga salvar(Vaga vaga) {
        try {
            return vagaRepository.save(vaga);
        } catch (DataIntegrityViolationException ex) {
            throw new CodigoUniqueViolationException("Vaga", vaga.getCodigo());
        }
    }

    @Transactional(readOnly = true)
    public Vaga buscarPorCodigo(String codigo) {
        return vagaRepository.findByCodigo(codigo).orElseThrow(
                () -> new EntityNotFoundException("Vaga", codigo)
        );
    }

    @Transactional(readOnly = true)
    public Vaga buscarPorVagaLivre() {
        return vagaRepository.findFirstByStatus(Vaga.StatusVaga.LIVRE).orElseThrow(
                () -> new VagaDisponivelException()
        );
    }
}