import { useState, useEffect, useMemo } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import type { AlimentoTabela, Ambiente } from './ambiente.types';
import {
  ambienteService,
  alimentoService,
  itensAmbienteService,
} from './ambiente.service';
import ModalCadastroAlimento from './components/ModalCadastroAlimento';
import ModalEditarAlimento from './components/ModalEditarAlimento';

// ── Helpers ──────────────────────────────────────────────────
function formatarData(dataIso: string): string {
  if (!dataIso) return '–';
  const [ano, mes, dia] = dataIso.split('-');
  if (!dia) return '–';
  return `${dia}/${mes}/${ano}`;
}

function conferirValidade(dataIso: string): boolean {
  if (!dataIso) return true;
  const validade = new Date(dataIso + 'T00:00:00');
  const hoje = new Date();
  hoje.setHours(0, 0, 0, 0);
  return validade >= hoje;
}

function normalizarTexto(texto: string): string {
  return texto.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase().trim();
}

function getAmbienteId(idParam: string | undefined): number {
  return parseInt(idParam ?? '0', 10);
}

// ── Componente ───────────────────────────────────────────────
export function AmbientePage() {
  const navigate = useNavigate();
  const { id } = useParams();
  const ambienteId = getAmbienteId(id);

  const [nomeAmbiente, setNomeAmbiente] = useState('Carregando...');
  const [alimentos, setAlimentos] = useState<AlimentoTabela[]>([]);
  const [todosAmbientes, setTodosAmbientes] = useState<Ambiente[]>([]);
  const [loading, setLoading] = useState(true);

  const [busca, setBusca] = useState('');
  const [ordemAsc, setOrdemAsc] = useState(true);

  const [modalCadastro, setModalCadastro] = useState(false);
  const [modalEditar, setModalEditar] = useState(false);
  const [modalExcluir, setModalExcluir] = useState(false);
  const [modalExcluirVencidos, setModalExcluirVencidos] = useState(false);
  const [indexSelecionado, setIndexSelecionado] = useState<number | null>(null);

  // ── Carregamento ─────────────────────────────────────────
  async function carregar() {
    setLoading(true);
    try {
      const [ambiente, todosAmbs, todosAlimentos] = await Promise.all([
        ambienteService.getById(ambienteId),
        ambienteService.getAll(),
        alimentoService.getAll(),
      ]);

      setNomeAmbiente(ambiente.nome);
      document.title = `StockIT - ${ambiente.nome}`;

      const linhas: AlimentoTabela[] = (ambiente.itens ?? []).map((item, index) => {
        const ali = todosAlimentos.find(a => a.id === item.alimentoId);
        return {
          index,
          nome: ali?.nome ?? `Alimento #${item.alimentoId}`,
          cadastro: item.cadastro,
          vencimento: item.vencimento,
          quantidade: item.quantidade,
          vencido: !conferirValidade(item.vencimento),
        };
      });

      setAlimentos(linhas);
      setTodosAmbientes(todosAmbs.filter(a => a.id !== ambienteId));
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { carregar(); }, []);

  // ── Filtro + Ordenação ───────────────────────────────────
  const alimentosFiltrados = useMemo(() => {
    const q = normalizarTexto(busca);
    const lista = q.length >= 2
      ? alimentos.filter(a => normalizarTexto(a.nome).includes(q))
      : [...alimentos];

    return lista.sort((a, b) =>
      normalizarTexto(a.nome).localeCompare(normalizarTexto(b.nome)) * (ordemAsc ? 1 : -1)
    );
  }, [alimentos, busca, ordemAsc]);

  // ── Ações ────────────────────────────────────────────────
  async function handleCadastrar(nome: string, idCategoria: number, quantidade: number, vencimento: string) {
    const id = await alimentoService.verificarOuCriar(nome, idCategoria);
    const ambiente = await ambienteService.getById(ambienteId);
    const itens = Array.isArray(ambiente.itens) ? ambiente.itens : [];
    itens.push({ alimentoId: id, quantidade, cadastro: new Date().toISOString().split('T')[0], vencimento });
    await ambienteService.patchItens(ambienteId, itens);
    await carregar();
  }

  async function handleEditar(quantidade: number, vencimento: string) {
    if (indexSelecionado === null) return;
    const itens = await itensAmbienteService.getAll();
    const ambiente = await ambienteService.getById(ambienteId);

    let itemId: number | null = null;
    let itemAtual: any = null;
    let contador = 0;

    for (const item of itens) {
      if (item.idAmbiente === ambienteId) {
        if (contador === indexSelecionado) { itemId = item.idItemAmbiente; itemAtual = item; break; }
        contador++;
      }
    }

    if (!itemAtual || itemId === null) { alert('Item não encontrado!'); return; }

    const novaValidade = vencimento || itemAtual.dataVencimento;
    await itensAmbienteService.update(itemId, {
      ...itemAtual,
      quantidade,
      dataVencimento: novaValidade,
    });
    await carregar();
  }

  async function handleExcluir() {
    if (indexSelecionado === null) return;
    const ambiente = await ambienteService.getById(ambienteId);
    ambiente.itens.splice(indexSelecionado, 1);
    await ambienteService.patchItens(ambienteId, ambiente.itens);
    setModalExcluir(false);
    await carregar();
  }

  async function handleExcluirVencidos() {
    const ambiente = await ambienteService.getById(ambienteId);
    const novosItens = ambiente.itens.filter(item => conferirValidade(item.vencimento));
    if (novosItens.length === ambiente.itens.length) {
      alert('Nenhum alimento vencido encontrado!'); return;
    }
    await ambienteService.patchItens(ambienteId, novosItens);
    setModalExcluirVencidos(false);
    await carregar();
  }

  // ── Render ───────────────────────────────────────────────
  return (
    <div className="min-h-screen bg-gray-50 flex flex-col font-sans">

      {/* Header */}
      <header className="bg-gradient-to-r from-emerald-600 to-emerald-400 text-white px-6 py-4 shadow-md">
        <div className="max-w-5xl mx-auto">
          <button
            type="button"
            onClick={() => navigate('/')}
            className="text-2xl font-bold text-white no-underline"
          >
            StockIT
          </button>
        </div>
      </header>

      {/* Main */}
      <main className="flex-1 max-w-5xl mx-auto w-full px-4 py-8">
        <div className="bg-white rounded-2xl shadow-sm p-6">

          {/* Cabeçalho da página */}
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-8">
            <div className="flex items-center gap-3">
              <button
                type="button"
                onClick={() => navigate(-1)}
                className="text-emerald-600 hover:scale-110 transition-transform text-3xl leading-none"
              >
                ←
              </button>
              <h2 className="text-3xl font-bold text-gray-800">{nomeAmbiente}</h2>
            </div>

            <div className="flex flex-col gap-2">
              <div className="flex gap-2">
                <button onClick={() => setModalCadastro(true)}
                  className="flex items-center gap-2 bg-emerald-600 text-white px-4 py-2 rounded-xl text-sm font-semibold hover:bg-emerald-700 transition-colors">
                  ➕ Adicionar Alimento
                </button>
              </div>
            </div>
          </div>

          {/* Cabeçalho da tabela */}
          <div className="flex items-center justify-between mb-4 flex-wrap gap-3">
            <h3 className="text-lg font-semibold text-gray-800">Alimentos</h3>
            <input
              type="text"
              placeholder="Buscar alimento..."
              value={busca}
              onChange={e => setBusca(e.target.value)}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition max-w-xs w-full"
            />
          </div>

          {/* Tabela */}
          <div className="overflow-x-auto rounded-xl border border-gray-200">
            <table className="w-full text-sm">
              <thead className="bg-gray-50">
                <tr>
                  <th
                    onClick={() => setOrdemAsc(v => !v)}
                    className="px-4 py-3 text-left font-semibold text-gray-600 cursor-pointer hover:bg-gray-100 transition-colors select-none"
                  >
                    Nome {ordemAsc ? '↑' : '↓'}
                  </th>
                  <th className="px-4 py-3 text-center font-semibold text-gray-600">Data de Adição</th>
                  <th className="px-4 py-3 text-center font-semibold text-gray-600">Data de Validade</th>
                  <th className="px-4 py-3 text-center font-semibold text-gray-600">Quantidade</th>
                  <th className="px-4 py-3 text-center font-semibold text-gray-600">Ações</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr>
                    <td colSpan={5} className="text-center py-10">
                      <div className="inline-block w-8 h-8 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin" />
                    </td>
                  </tr>
                ) : alimentosFiltrados.length === 0 ? (
                  <tr>
                    <td colSpan={5} className="text-center py-8 text-gray-400">
                      {busca.length >= 2 ? 'Nenhum alimento encontrado 😔' : 'Nenhum alimento cadastrado.'}
                    </td>
                  </tr>
                ) : (
                  alimentosFiltrados.map(ali => (
                    <tr key={ali.index} className="border-t border-gray-100 hover:bg-gray-50 transition-colors">
                      <td className="px-4 py-3">
                        <span className={ali.vencido ? 'text-red-500 line-through font-medium' : 'text-gray-800'}>
                          {ali.nome}
                        </span>
                        {ali.vencido && (
                          <span className="ml-2 text-red-500 text-xs font-medium">⚠️ vencido</span>
                        )}
                      </td>
                      <td className="px-4 py-3 text-center text-gray-600">{formatarData(ali.cadastro)}</td>
                      <td className="px-4 py-3 text-center text-gray-600">{formatarData(ali.vencimento)}</td>
                      <td className="px-4 py-3 text-center text-gray-600">{ali.quantidade}</td>
                      <td className="px-4 py-3 text-center">
                        <div className="flex items-center justify-center gap-2">
                          <button
                            onClick={() => { setIndexSelecionado(ali.index); setModalEditar(true); }}
                            className="border border-gray-300 text-gray-600 px-3 py-1.5 rounded-lg text-xs font-medium hover:bg-gray-50 transition-colors"
                          >
                            Editar
                          </button>
                          <button
                            onClick={() => { setIndexSelecionado(ali.index); setModalExcluir(true); }}
                            className="bg-red-500 text-white px-3 py-1.5 rounded-lg text-xs font-medium hover:bg-red-600 transition-colors"
                          >
                            Excluir
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </main>

      {/* Footer */}
      <footer className="text-center py-4 bg-white border-t border-gray-200 text-sm text-gray-400">
        StockIT © 2025 - Gerenciamento de Estoque de Alimentos
      </footer>

      {/* ── Modais ── */}
      <ModalCadastroAlimento
        open={modalCadastro}
        onClose={() => setModalCadastro(false)}
        onSubmit={handleCadastrar}
      />

      <ModalEditarAlimento
        open={modalEditar}
        onClose={() => setModalEditar(false)}
        onSubmit={handleEditar}
      />

      {/* Modal Excluir */}
      {modalExcluir && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-sm p-6 space-y-4">
            <h2 className="text-lg font-semibold text-gray-800">Excluir Alimento</h2>
            <p className="text-gray-600 text-sm">Tem certeza que deseja excluir este alimento?</p>
            <div className="flex gap-3 pt-2">
              <button onClick={handleExcluir}
                className="flex-1 bg-red-500 text-white py-2.5 rounded-xl font-semibold hover:bg-red-600 transition-colors">
                Excluir
              </button>
              <button onClick={() => setModalExcluir(false)}
                className="flex-1 border border-gray-300 text-gray-600 py-2.5 rounded-xl font-semibold hover:bg-gray-50 transition-colors">
                Cancelar
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal Excluir Vencidos */}
      {modalExcluirVencidos && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-sm p-6 space-y-4">
            <h2 className="text-lg font-semibold text-gray-800">Excluir Alimentos Vencidos</h2>
            <p className="text-gray-600 text-sm">Tem certeza que deseja excluir todos os alimentos vencidos?</p>
            <div className="flex gap-3 pt-2">
              <button onClick={handleExcluirVencidos}
                className="flex-1 bg-red-500 text-white py-2.5 rounded-xl font-semibold hover:bg-red-600 transition-colors">
                Excluir
              </button>
              <button onClick={() => setModalExcluirVencidos(false)}
                className="flex-1 border border-gray-300 text-gray-600 py-2.5 rounded-xl font-semibold hover:bg-gray-50 transition-colors">
                Cancelar
              </button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}
