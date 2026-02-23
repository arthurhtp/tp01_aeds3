import { Header } from '../../components/Header/index';
import { ModalCadastroAmbiente } from '../../components/ModalCadastroAmbiente';
import { AmbientesSection, PageFooter } from './components';
import { useAmbientes, useAmbienteModal } from './hooks';
import { ambientesService } from '../../services/home.service';
import { tipoNumParaChar } from './utils';
import { CONFIRM_SAIR, CONFIRM_EXCLUIR, ERRO_CARREGAR_AMBIENTE } from './constants';

export function HomePage() {
  const { ambientes, loading, error, carregarAmbientes } = useAmbientes();
  const {
    modalCadastroOpen,
    ambienteEditando,
    abrirModal,
    fecharModal,
    editarAmbiente,
  } = useAmbienteModal();

  // ── Cadastro / Edição ──────────────────────────────────────
  async function handleSubmitAmbiente(nome: string, tipo: string) {
    if (ambienteEditando) {
      await ambientesService.update(ambienteEditando.id, nome, tipoNumParaChar(Number(tipo)));
    } else {
      await ambientesService.create(nome, tipo);
    }
    fecharModal();
    await carregarAmbientes();
  }

  async function handleEditar(id: number) {
    try {
      const amb = await ambientesService.getById(id);
      const nome = amb.nome ?? amb.nomeAmbiente ?? '';
      editarAmbiente({ id, nome, tipo: amb.tipo });
    } catch {
      alert(ERRO_CARREGAR_AMBIENTE);
    }
  }

  async function handleExcluir(id: number) {
    const ok = window.confirm(CONFIRM_EXCLUIR);
    if (!ok) return;
    await ambientesService.delete(id);
    await carregarAmbientes();
  }

  // ── Logout ─────────────────────────────────────────────────
  function handleLogout() {
    const ok = window.confirm(CONFIRM_SAIR);
    if (ok) {
      sessionStorage.removeItem('user');
      window.location.href = '../login/login.html';
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col font-sans">
      <Header onLogout={handleLogout} />

      <main className="flex-1 max-w-7xl mx-auto w-full px-8 py-12">
        <AmbientesSection
          ambientes={ambientes}
          loading={loading}
          error={error}
          onEditar={handleEditar}
          onExcluir={handleExcluir}
          onAdicionarClick={abrirModal}
        />
      </main>

      <PageFooter />

      {/* ── Modais ── */}
      <ModalCadastroAmbiente
        open={modalCadastroOpen}
        ambienteParaEditar={ambienteEditando}
        onClose={fecharModal}
        onSubmit={handleSubmitAmbiente}
      />
    </div>
  );
}
