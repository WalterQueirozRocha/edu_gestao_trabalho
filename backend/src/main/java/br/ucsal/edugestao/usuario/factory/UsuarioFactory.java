package br.ucsal.edugestao.usuario.factory;

import br.ucsal.edugestao.usuario.entity.Administrador;
import br.ucsal.edugestao.usuario.entity.Aluno;
import br.ucsal.edugestao.usuario.entity.Coordenador;
import br.ucsal.edugestao.usuario.entity.Perfil;
import br.ucsal.edugestao.usuario.entity.Professor;
import br.ucsal.edugestao.usuario.entity.ProfissionalApoio;
import br.ucsal.edugestao.usuario.entity.Responsavel;
import br.ucsal.edugestao.usuario.entity.Usuario;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Padrao Factory: concentra a criacao da subclasse correta de {@link Usuario}
 * a partir do perfil, isolando o resto do sistema da hierarquia de heranca (JPA SINGLE_TABLE).
 */
public final class UsuarioFactory {

    private static final Map<Perfil, Supplier<Usuario>> CONSTRUTORES = new EnumMap<>(Perfil.class);

    static {
        CONSTRUTORES.put(Perfil.ADMINISTRADOR, Administrador::new);
        CONSTRUTORES.put(Perfil.COORDENADOR, Coordenador::new);
        CONSTRUTORES.put(Perfil.PROFESSOR, Professor::new);
        CONSTRUTORES.put(Perfil.PROFISSIONAL_APOIO, ProfissionalApoio::new);
        CONSTRUTORES.put(Perfil.ALUNO, Aluno::new);
        CONSTRUTORES.put(Perfil.RESPONSAVEL, Responsavel::new);
    }

    private UsuarioFactory() {
    }

    public static Usuario criar(Perfil perfil) {
        if (perfil == null) {
            throw new IllegalArgumentException("Perfil do usuario e obrigatorio.");
        }
        Supplier<Usuario> construtor = CONSTRUTORES.get(perfil);
        if (construtor == null) {
            throw new IllegalArgumentException("Perfil sem implementacao de usuario: " + perfil);
        }
        return construtor.get();
    }
}
