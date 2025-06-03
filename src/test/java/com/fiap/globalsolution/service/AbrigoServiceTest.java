package com.fiap.globalsolution.service;

import com.fiap.globalsolution.dto.AbrigoResponseDTO;
import com.fiap.globalsolution.model.Abrigo;
import com.fiap.globalsolution.model.Role;
import com.fiap.globalsolution.rabbitmq.AbrigoProducer;
import com.fiap.globalsolution.repository.AbrigoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.fiap.globalsolution.dto.AbrigoRequestDTO;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

class AbrigoServiceTest {

    @Mock
    private AbrigoRepository abrigoRepository;

    @Mock
    private AbrigoProducer abrigoProducer;

    @InjectMocks
    private AbrigoService abrigoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarTodos_deveRetornarListaDeAbrigosDTO() {
        Abrigo abrigo1 = new Abrigo();
        abrigo1.setId("1");
        abrigo1.setNome("Abrigo A");
        abrigo1.setEndereco("Rua 1");
        abrigo1.setCidade("Cidade X");
        abrigo1.setEstado("SP");
        abrigo1.setLatitude(10.0);
        abrigo1.setLongitude(20.0);
        abrigo1.setAtivo(true);

        when(abrigoRepository.findAll()).thenReturn(List.of(abrigo1));

        List<AbrigoResponseDTO> resultado = abrigoService.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Abrigo A");
    }

    @Test
    void buscarPorId_deveRetornarAbrigoDTO_quandoIdForValido() {
        Abrigo abrigo = new Abrigo();
        abrigo.setId("123");
        abrigo.setNome("Abrigo Teste");
        abrigo.setEndereco("Rua Teste");
        abrigo.setCidade("Cidade Y");
        abrigo.setEstado("RJ");
        abrigo.setLatitude(-23.0);
        abrigo.setLongitude(-43.0);
        abrigo.setAtivo(true);

        when(abrigoRepository.findById("123")).thenReturn(java.util.Optional.of(abrigo));

        AbrigoResponseDTO resultado = abrigoService.buscarPorId("123");

        assertThat(resultado.nome()).isEqualTo("Abrigo Teste");
        assertThat(resultado.estado()).isEqualTo("RJ");
    }



    @Test
    void buscarPorId_deveLancarExcecao_quandoIdNaoForEncontrado() {
        when(abrigoRepository.findById("inexistente")).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> abrigoService.buscarPorId("inexistente"));
    }

    @Test
    void criar_deveSalvarAbrigoEEnviarMensagemERetornarDTO() {
        AbrigoRequestDTO dto = new AbrigoRequestDTO(
                "Abrigo Central",
                "Av. Principal",
                "São Paulo",
                "SP",
                -23.55,
                -46.63
        );

        Abrigo abrigoSalvo = new Abrigo();
        abrigoSalvo.setId("1");
        abrigoSalvo.setNome(dto.nome());
        abrigoSalvo.setEndereco(dto.endereco());
        abrigoSalvo.setCidade(dto.cidade());
        abrigoSalvo.setEstado(dto.estado());
        abrigoSalvo.setLatitude(dto.latitude());
        abrigoSalvo.setLongitude(dto.longitude());
        abrigoSalvo.setAtivo(true);

        when(abrigoRepository.save(any(Abrigo.class))).thenReturn(abrigoSalvo);

        AbrigoResponseDTO resultado = abrigoService.criar(dto);

        assertThat(resultado.nome()).isEqualTo("Abrigo Central");
        assertThat(resultado.estado()).isEqualTo("SP");
        verify(abrigoProducer, times(1)).enviarMensagem(contains("Av. Principal"));
    }

    @Test
    void atualizar_comAdmin_deveAtualizarAbrigo() {
        String abrigoId = "123";
        AbrigoRequestDTO dto = new AbrigoRequestDTO(
                "Novo Nome",
                "Novo Endereço",
                "Rio de Janeiro",
                "RJ",
                -22.90,
                -43.17
        );

        Abrigo abrigoExistente = new Abrigo();
        abrigoExistente.setId(abrigoId);
        abrigoExistente.setNome("Antigo Nome");
        abrigoExistente.setEndereco("Endereço Antigo");
        abrigoExistente.setCidade("Cidade Antiga");
        abrigoExistente.setEstado("Estado Antigo");
        abrigoExistente.setLatitude(-23.00);
        abrigoExistente.setLongitude(-44.00);
        abrigoExistente.setAtivo(true);

        when(abrigoRepository.findById(abrigoId)).thenReturn(Optional.of(abrigoExistente));
        when(abrigoRepository.save(any(Abrigo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AbrigoResponseDTO atualizado = abrigoService.atualizar(abrigoId, dto, Role.ADMIN);

        assertThat(atualizado.nome()).isEqualTo("Novo Nome");
        assertThat(atualizado.estado()).isEqualTo("RJ");
    }

    @Test
    void atualizar_comUsuarioNaoAdmin_deveLancarExcecao() {
        AbrigoRequestDTO dto = new AbrigoRequestDTO(
                "Nome",
                "Endereço",
                "Cidade",
                "Estado",
                0.0,
                0.0
        );

        assertThatThrownBy(() -> abrigoService.atualizar("idQualquer", dto, Role.USER))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Apenas ADMIN pode atualizar abrigos");
    }

    @Test
    void atualizar_comAdmin_eAbrigoNaoExiste_deveLancarExcecao() {
        String idInvalido = "999";
        AbrigoRequestDTO dto = new AbrigoRequestDTO(
                "Nome",
                "Endereço",
                "Cidade",
                "Estado",
                0.0,
                0.0
        );

        when(abrigoRepository.findById(idInvalido)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> abrigoService.atualizar(idInvalido, dto, Role.ADMIN))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Abrigo não encontrado");
    }

    @Test
    void deletar_comAdmin_eAbrigoExiste_deveDeletar() {
        String abrigoId = "123";

        when(abrigoRepository.existsById(abrigoId)).thenReturn(true);
        doNothing().when(abrigoRepository).deleteById(abrigoId);

        assertThatCode(() -> abrigoService.deletar(abrigoId, Role.ADMIN))
                .doesNotThrowAnyException();

        verify(abrigoRepository).deleteById(abrigoId);
    }

    @Test
    void deletar_comUsuarioNaoAdmin_deveLancarExcecao() {
        assertThatThrownBy(() -> abrigoService.deletar("123", Role.USER))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Apenas ADMIN pode excluir abrigos permanentemente");
    }

    @Test
    void deletar_comAdmin_eAbrigoNaoExiste_deveLancarExcecao() {
        String idInvalido = "999";

        when(abrigoRepository.existsById(idInvalido)).thenReturn(false);

        assertThatThrownBy(() -> abrigoService.deletar(idInvalido, Role.ADMIN))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Abrigo não encontrado");
    }

    @Test
    void filtrar_comEstadoECidade_deveChamarFindByEstadoAndCidade() {
        Abrigo abrigo = new Abrigo();
        abrigo.setId("1");
        abrigo.setNome("Teste");
        abrigo.setEndereco("Rua A");
        abrigo.setCidade("São Paulo");
        abrigo.setEstado("SP");
        abrigo.setLatitude(0.0);
        abrigo.setLongitude(0.0);
        abrigo.setAtivo(true);

        when(abrigoRepository.findByEstadoAndCidade("SP", "São Paulo"))
                .thenReturn(List.of(abrigo));

        List<AbrigoResponseDTO> resultado = abrigoService.filtrar("SP", "São Paulo");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Teste");
    }

    @Test
    void filtrar_comSomenteEstado_deveChamarFindByEstado() {
        when(abrigoRepository.findByEstado("SP"))
                .thenReturn(List.of());

        List<AbrigoResponseDTO> resultado = abrigoService.filtrar("SP", null);

        assertThat(resultado).isEmpty();
    }

    @Test
    void filtrar_comSomenteCidade_deveChamarFindByCidade() {
        when(abrigoRepository.findByCidade("Campinas"))
                .thenReturn(List.of());

        List<AbrigoResponseDTO> resultado = abrigoService.filtrar(null, "Campinas");

        assertThat(resultado).isEmpty();
    }

    @Test
    void filtrar_semParametros_deveChamarListarTodos() {
        List<AbrigoResponseDTO> todos = List.of();
        when(abrigoRepository.findAll()).thenReturn(List.of());

        List<AbrigoResponseDTO> resultado = abrigoService.filtrar(null, null);

        assertThat(resultado).isEmpty();
    }


}
