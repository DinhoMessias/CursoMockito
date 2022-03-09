package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

class FinalizarLeilaoServiceTest {

    @Mock
    private LeilaoDao leilaoDao;

    @Mock
    private EnviadorDeEmails enviadorDeEmails;

    private FinalizarLeilaoService service;

    private List<Leilao> leiloes() {
        List<Leilao> leiloes = Lists.newArrayList();

        Leilao leilao = new Leilao("Teste", new BigDecimal("500"), new Usuario("Dinho"));

        Lance l1 = new Lance(new Usuario("Wenderson"), new BigDecimal("750"));
        Lance l2 = new Lance(new Usuario("Wesley"), new BigDecimal("850"));

        leilao.propoe(l1);
        leilao.propoe(l2);

        leiloes.add(leilao);

        return leiloes;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new FinalizarLeilaoService(leilaoDao, enviadorDeEmails);
    }

    @Test
    void deveFinalizarLeiloesExpirados() {
        List<Leilao> leiloes = leiloes();
        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);

        service.finalizarLeiloesExpirados();

        Leilao leilao = leiloes.get(0);

        assertTrue(leilao.isFechado());
        assertEquals(new BigDecimal("850"), leilao.getLanceVencedor().getValor());
        Mockito.verify(leilaoDao).salvar(leilao);
    }

    @Test
    void deveEnviarEmailParaLanceVencedorAoFinalizarLeiloesExpirados() {
        List<Leilao> leiloes = leiloes();
        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);

        Leilao leilao = leiloes.get(0);
        
        service.finalizarLeiloesExpirados();
        Mockito.verify(enviadorDeEmails).enviarEmailVencedorLeilao(leilao.getLanceVencedor());
    }
}