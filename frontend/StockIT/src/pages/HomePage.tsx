import { useState, useEffect } from 'react';
import { Header } from '../components/Header/index';
import { AmbientesCarousel } from '../components/AmbientesCarousel/index';
import { ModalCadastroAmbiente } from '../components/ModalCadastroAmbiente/index';
import type { Ambiente } from '../types/home.types';
import { ambientesService } from '../services/home.service';

// Helper
function tipoNumParaChar(tipo: number): string {
  return tipo === 1 ? 'G' : tipo === 2 ? 'F' : 'D';
}

export function HomePage() {
  const [ambientes, setAmbientes] = useState<Ambiente[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  // Modal cadastro ambiente
  const [modalCadastroOpen, setModalCadastroOpen] = useState(false);
  const [ambienteEditando, setAmbienteEditando] = useState<{ id: number; nome: string; tipo: number } | null>(null);

  async function carregarAmbientes() {
    try {
      const data = await ambientesService.getAll();
      setAmbientes(data);
      setError(false);
    } catch {
      setError(true);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { carregarAmbientes(); }, []);

  // ── Cadastro / Edição ──────────────────────────────────────
  async function handleSubmitAmbiente(nome: string, tipo: string) {
    if (ambienteEditando) {
      await ambientesService.update(ambienteEditando.id, nome, tipoNumParaChar(Number(tipo)));
    } else {
      await ambientesService.create(nome, tipo);
    }
    setAmbienteEditando(null);
    await carregarAmbientes();
  }

  async function handleEditar(id: number) {
    try {
      const amb = await ambientesService.getById(id);
      const nome = amb.nome ?? amb.nomeAmbiente ?? '';
      setAmbienteEditando({ id, nome, tipo: amb.tipo });
      setModalCadastroOpen(true);
    } catch {
      alert('Erro ao carregar ambiente.');
    }
  }

  async function handleExcluir(id: number) {
    const ok = window.confirm('Tem certeza? Esta ação não pode ser desfeita!');
    if (!ok) return;
    await ambientesService.delete(id);
    await carregarAmbientes();
  }

  // ── Logout ─────────────────────────────────────────────────
  function handleLogout() {
    const ok = window.confirm('Deseja sair?');
    if (ok) {
      sessionStorage.removeItem('user');
      window.location.href = '../login/login.html';
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col font-sans">
      <Header onLogout={handleLogout} />

      <main className="flex-1 max-w-7xl mx-auto w-full px-8 py-12">

        {/* ── Meus Ambientes ── */}
        <section className="mb-16">
          <h1 className="text-4xl font-bold text-gray-800 tracking-widest mb-10">MEUS AMBIENTES</h1>

          {loading ? (
            <div className="flex justify-center py-12">
              <div className="w-10 h-10 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin" />
            </div>
          ) : error ? (
            <div className="text-center py-12 text-red-500">
              <p className="text-lg font-semibold">Erro ao carregar ambientes</p>
              <p className="text-sm mt-1">Verifique sua conexão e tente novamente.</p>
            </div>
          ) : ambientes.length === 0 ? (
            <div className="text-center py-12 text-gray-400">
              <p className="text-lg">Nenhum ambiente cadastrado ainda.</p>
              <p className="text-sm mt-1">Clique em "Adicionar Ambientes" para começar.</p>
            </div>
          ) : (
            <AmbientesCarousel
              ambientes={ambientes}
              onEditar={handleEditar}
              onExcluir={handleExcluir}
            />
          )}

          {/* Botões de ação */}
          <div className="flex justify-center gap-4 mt-10">
            <button
              onClick={() => { setAmbienteEditando(null); setModalCadastroOpen(true); }}
              className="flex items-center gap-3 bg-emerald-600 text-white px-8 py-3.5 rounded-xl font-semibold text-base hover:bg-emerald-700 hover:-translate-y-0.5 hover:shadow-lg transition-all shadow-emerald-200 shadow-md"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
              </svg>
              Adicionar Ambientes
            </button>
          </div>
        </section>
      </main>

      {/* ── Footer ── */}
      <footer className="text-center py-4 bg-white border-t border-gray-200 text-sm text-gray-400">
        StockIT © 2025 - Gerenciamento de Estoque de Alimentos
      </footer>

      {/* ── Modais ── */}
      <ModalCadastroAmbiente
        open={modalCadastroOpen}
        ambienteParaEditar={ambienteEditando}
        onClose={() => { setModalCadastroOpen(false); setAmbienteEditando(null); }}
        onSubmit={handleSubmitAmbiente}
      />
    </div>
  );
}
