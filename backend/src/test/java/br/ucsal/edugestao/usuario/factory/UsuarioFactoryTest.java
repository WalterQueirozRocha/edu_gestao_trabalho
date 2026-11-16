package br.ucsal.edugestao.usuario.factory;

import br.ucsal.edugestao.usuario.entity.Perfil;
import br.ucsal.edugestao.usuario.entity.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Padrao Factory: cada perfil gera a subclasse correspondente de Usuario
class UsuarioFactoryTest {

    @ParameterizedTest(name = "{0}")
    @EnumSource(Perfil.class)
    @DisplayName("Todo perfil tem uma subclasse de usuario")
    void criaSubclassePorPerfil(Perfil perfil) {
        Usuario usuario = UsuarioFactory.criar(perfil);

        assertThat(usuario.getPerfilTipo()).isEqualTo(perfil);
    }

    @Test
    @DisplayName("Perfil nulo e rejeitado")
    void perfilNulo() {
        assertThatThrownBy(() -> UsuarioFactory.criar(null)).isInstanceOf(IllegalArgumentException.class);
    }
}
