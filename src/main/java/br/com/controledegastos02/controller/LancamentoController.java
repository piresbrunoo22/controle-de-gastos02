// src/main/java/br/com/controledegastos/controller/LancamentoController.java
package br.com.controledegastos02.controller;

import br.com.controledegastos02.model.Lancamento;
import br.com.controledegastos02.model.TipoLancamento;
import br.com.controledegastos02.repository.LancamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Controller
public class LancamentoController {

    @Autowired
    private LancamentoRepository lancamentoRepository;

    private void carregarDados(Model model) {
        List<Lancamento> lancamentos = lancamentoRepository.findAll();
        lancamentos.sort(Comparator.comparing(Lancamento::getData).reversed());
        
        // Cálculo do Saldo (Receitas - Despesas)
        BigDecimal saldo = lancamentos.stream()
                .map(l -> l.getTipo() == TipoLancamento.RECEITA ? l.getValor() : l.getValor().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        model.addAttribute("lancamentos", lancamentos);
        model.addAttribute("saldo", saldo);
    }

    // Método para adicionar um novo lançamento (via htmx)
    @PostMapping("/lancamentos")
    public String addLancamento(@ModelAttribute Lancamento novoLancamento, Model model) {
        lancamentoRepository.save(novoLancamento);
            
        // Após salvar, recarregamos a lista e a retornamos como um fragmento
        List<Lancamento> lancamentos = lancamentoRepository.findAll();
        lancamentos.sort(Comparator.comparing(Lancamento::getData).reversed());
        model.addAttribute("lancamentos", lancamentos);
            
        // Retorna apenas o fragmento da tabela, não a página inteira
        return "index :: lista-lancamentos"; 
    }

    // Método para excluir um lançamento (via htmx)
    @DeleteMapping("/lancamentos/{id}")
    public String deleteLancamento(@PathVariable Long id, Model model) {
        lancamentoRepository.deleteById(id);
            
        // Após deletar, recarregamos a lista e a retornamos como um fragmento
        List<Lancamento> lancamentos = lancamentoRepository.findAll();
        lancamentos.sort(Comparator.comparing(Lancamento::getData).reversed());
        model.addAttribute("lancamentos", lancamentos);
            
        // Retorna apenas o fragmento da tabela
        return "index :: lista-lancamentos";
    }
}
