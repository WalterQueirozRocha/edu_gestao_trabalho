import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './auth/AuthContext';
import { RotaProtegida } from './auth/RotaProtegida';
import { Layout } from './components/Layout';
import { LoginPage } from './pages/auth/LoginPage';
import { PainelPage } from './pages/dashboard/PainelPage';
import { UsuariosPage } from './pages/usuarios/UsuariosPage';
import { CadastrosAcademicosPage } from './pages/academico/CadastrosAcademicosPage';
import { MatriculasPage } from './pages/matriculas/MatriculasPage';
import { DiarioClassePage } from './pages/diario/DiarioClassePage';
import { AvaliacoesNotasPage } from './pages/avaliacoes/AvaliacoesNotasPage';
import { ResultadosEtapaPage } from './pages/coordenacao/ResultadosEtapaPage';
import { BoletimPage } from './pages/boletim/BoletimPage';
import { ComunicadosPage } from './pages/comunicados/ComunicadosPage';
import { RegistrarOcorrenciaDenunciaPage } from './pages/ocorrencias/RegistrarOcorrenciaDenunciaPage';
import { TratarOcorrenciasDenunciasPage } from './pages/ocorrencias/TratarOcorrenciasDenunciasPage';
import { PlanoPedagogicoPage } from './pages/pedagogico/PlanoPedagogicoPage';
import { PainelAlertasIaPage } from './pages/ia/PainelAlertasIaPage';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route
            element={
              <RotaProtegida>
                <Layout />
              </RotaProtegida>
            }
          >
            <Route path="/" element={<PainelPage />} />
            <Route
              path="/usuarios"
              element={
                <RotaProtegida perfisPermitidos={['ADMINISTRADOR']}>
                  <UsuariosPage />
                </RotaProtegida>
              }
            />
            <Route
              path="/cadastros-academicos"
              element={
                <RotaProtegida perfisPermitidos={['ADMINISTRADOR']}>
                  <CadastrosAcademicosPage />
                </RotaProtegida>
              }
            />
            <Route
              path="/matriculas"
              element={
                <RotaProtegida perfisPermitidos={['ADMINISTRADOR']}>
                  <MatriculasPage />
                </RotaProtegida>
              }
            />
            <Route
              path="/diario"
              element={
                <RotaProtegida perfisPermitidos={['PROFESSOR', 'COORDENADOR']}>
                  <DiarioClassePage />
                </RotaProtegida>
              }
            />
            <Route
              path="/avaliacoes"
              element={
                <RotaProtegida perfisPermitidos={['PROFESSOR', 'COORDENADOR']}>
                  <AvaliacoesNotasPage />
                </RotaProtegida>
              }
            />
            <Route
              path="/resultados-etapa"
              element={
                <RotaProtegida perfisPermitidos={['COORDENADOR', 'ADMINISTRADOR']}>
                  <ResultadosEtapaPage />
                </RotaProtegida>
              }
            />
            <Route
              path="/boletim"
              element={
                <RotaProtegida perfisPermitidos={['ALUNO', 'RESPONSAVEL', 'COORDENADOR', 'ADMINISTRADOR']}>
                  <BoletimPage />
                </RotaProtegida>
              }
            />
            <Route path="/comunicados" element={<ComunicadosPage />} />
            <Route
              path="/ocorrencias-denuncias"
              element={
                <RotaProtegida perfisPermitidos={['PROFESSOR', 'ALUNO', 'RESPONSAVEL', 'COORDENADOR']}>
                  <RegistrarOcorrenciaDenunciaPage />
                </RotaProtegida>
              }
            />
            <Route
              path="/tratamento-ocorrencias"
              element={
                <RotaProtegida perfisPermitidos={['COORDENADOR', 'ADMINISTRADOR']}>
                  <TratarOcorrenciasDenunciasPage />
                </RotaProtegida>
              }
            />
            <Route
              path="/planos-pedagogicos"
              element={
                <RotaProtegida perfisPermitidos={['COORDENADOR', 'PROFISSIONAL_APOIO', 'ADMINISTRADOR']}>
                  <PlanoPedagogicoPage />
                </RotaProtegida>
              }
            />
            <Route
              path="/alertas-ia"
              element={
                <RotaProtegida perfisPermitidos={['COORDENADOR', 'ADMINISTRADOR']}>
                  <PainelAlertasIaPage />
                </RotaProtegida>
              }
            />
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
