package com.exemplo.algamoney.api.service;

import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.exemplo.algamoney.api.dto.LancamentoEstatisticaPessoa;
import com.exemplo.algamoney.api.mail.Mailer;
import com.exemplo.algamoney.api.model.Lancamento;
import com.exemplo.algamoney.api.model.Pessoa;
import com.exemplo.algamoney.api.model.Usuario;
import com.exemplo.algamoney.api.repository.LancamentoRepository;
import com.exemplo.algamoney.api.repository.PessoaRepository;
import com.exemplo.algamoney.api.repository.UsuarioRepository;
import com.exemplo.algamoney.api.service.exception.PessoaInexistenteOuInativoExcpetion;
import com.exemplo.algamoney.api.storage.S3;

import org.springframework.util.StringUtils;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class LancamentoService {

	private static final Logger logger = LoggerFactory.getLogger(LancamentoService.class);

	private static final String DESTINATARIOS = "ROLE_PESQUISAR_LANCAMENTO";

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private Mailer mailer;

	@Autowired
	private S3 s3;

	// @Scheduled(cron = "0 0 6 * * *")
	@Scheduled(fixedDelay = 20000)
	public void avisarSobreLancamentosVencidos() {
		logger.info("", "");
		if (logger.isDebugEnabled()) {
			logger.debug("Preparando envio de " + "e-mails de aviso de lançamentos vencidos.");
		}

		List<Lancamento> vencidos = lancamentoRepository
				.findByDataVencimetoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());

		if (vencidos.isEmpty()) {
			logger.info("Sem lançamentos vencidos para aviso.");

			return;
		}

		logger.info("Exitem {} lançamentos vencidos.", vencidos.size());

		List<Usuario> destinatarios = usuarioRepository.findByPermissoesDescricao(DESTINATARIOS);

		if (destinatarios.isEmpty()) {
			logger.warn("Existem lançamentos vencidos, mas o " + "sistema não encontrou destinatários.");

			return;
		}

		mailer.avisarSobreLancamentosVencidos(vencidos, destinatarios);

		logger.info("Envio de e-mail de aviso concluído.");
	}

	public byte[] relatorioPorPessoa(LocalDate inicio, LocalDate fim) throws Exception {
		List<LancamentoEstatisticaPessoa> dados = lancamentoRepository.porPessoa(inicio, fim);

		System.out.println("Dados " + dados.size());

		Map<String, Object> parametros = new HashMap<>();

		// o parametro do report espera um util.date, e aqui estou passando um sql.date,
		// como sql.date extende de util.date nao tem problema
		parametros.put("DT_INICIO", Date.valueOf(inicio));
		parametros.put("DT_FIM", Date.valueOf(fim));
		parametros.put("", new Locale("pt", "BR"));

		InputStream inputStream = this.getClass().getResourceAsStream("/relatorios/lancamento-por-pessoa.jasper");

		JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros,
				new JRBeanCollectionDataSource(dados));

		return JasperExportManager.exportReportToPdf(jasperPrint);
	}

	public Lancamento salvar(Lancamento lancamento) {
		Pessoa pessoa = pessoaRepository.findOne(lancamento.getPessoa().getCodigo());

		if (pessoa == null || pessoa.isInativo()) {
			throw new PessoaInexistenteOuInativoExcpetion();
		}

		if (StringUtils.hasText(lancamento.getAnexo())) {
			s3.salvar(lancamento.getAnexo());
		}

		return lancamentoRepository.save(lancamento);

	}

	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		Lancamento lancamentoSalvo = buscarLancamentoExistente(codigo);
		if (!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
			validarPessoa(lancamento);
		}

		BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");

		return lancamentoRepository.save(lancamentoSalvo);
	}

	private void validarPessoa(Lancamento lancamento) {
		Pessoa pessoa = null;
		if (lancamento.getPessoa().getCodigo() != null) {
			pessoa = pessoaRepository.findOne(lancamento.getPessoa().getCodigo());
		}

		if (pessoa == null || pessoa.isInativo()) {
			throw new PessoaInexistenteOuInativoExcpetion();
		}
	}

	private Lancamento buscarLancamentoExistente(Long codigo) {
		Lancamento lancamentoSalvo = lancamentoRepository.findOne(codigo);
		if (lancamentoSalvo == null) {
			throw new IllegalArgumentException();
		}
		return lancamentoSalvo;
	}

}
