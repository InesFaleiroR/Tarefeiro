package com.tarefeiro.service;

import com.tarefeiro.dto.UtilizadorDTO;
import com.tarefeiro.model.Utilizador;

import java.util.List;

public interface UtilizadorService {
    Utilizador registar(UtilizadorDTO dto);
    Utilizador atualizar(Long id, UtilizadorDTO dto);
    void desativar(Long id);
    Utilizador buscarPorId(Long id);
    Utilizador buscarPorEmail(String email);
    List<Utilizador> listarTodos();
    boolean emailExiste(String email);
    void atualizarUltimoAcesso(String email);
}
