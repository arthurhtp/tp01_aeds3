import { useState } from 'react';
import type { Categoria } from '../ambiente.types';
import { categoriaService } from '../ambiente.service';

interface ModalGerenciarCategoriasProps {
  open: boolean;
  categorias: Categoria[];
  onClose: () => void;
  onAtualizar: () => Promise<void>;
}

export default function ModalGerenciarCategorias({
  open, categorias, onClose, onAtualizar,
}: ModalGerenciarCategoriasProps) {
  const [nomeNova, setNomeNova] = useState('');
  const [editando, setEditando] = useState<Categoria | null>(null);
  const [nomeEditando, setNomeEditando] = useState('');
  const [loading, setLoading] = useState(false);

  if (!open) return null;

  function handleFechar() {
    setEditando(null);
    setNomeNova('');
    onClose();
  }

  async function handleCriar() {
    if (!nomeNova.trim()) return;
    setLoading(true);
    try {
      await categoriaService.criar(nomeNova.trim());
      setNomeNova('');
      await onAtualizar();
    } finally {
      setLoading(false);
    }
  }

  async function handleSalvarEdicao() {
    if (!editando || !nomeEditando.trim()) return;
    setLoading(true);
    try {
      await categoriaService.atualizar(editando.id, nomeEditando.trim());
      setEditando(null);
      setNomeEditando('');
      await onAtualizar();
    } finally {
      setLoading(false);
    }
  }

  async function handleDeletar(id: number) {
    if (!window.confirm('Excluir esta categoria?')) return;
    await categoriaService.deletar(id);
    await onAtualizar();
  }

  return (
    <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-60 p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-sm">
        <div className="flex items-center justify-between p-5 border-b border-gray-100">
          <h3 className="text-base font-semibold text-gray-800">Gerenciar Categorias</h3>
          <button onClick={handleFechar} className="text-gray-400 hover:text-gray-600 transition-colors">
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <div className="p-5 space-y-4">
          {/* Lista */}
          <ul className="space-y-2 max-h-52 overflow-y-auto">
            {categorias.length === 0 && (
              <li className="text-sm text-gray-400 text-center py-4">Nenhuma categoria cadastrada.</li>
            )}
            {categorias.map(cat => (
              <li key={cat.id} className="flex items-center gap-2">
                {editando?.id === cat.id ? (
                  <>
                    <input
                      autoFocus
                      value={nomeEditando}
                      onChange={e => setNomeEditando(e.target.value)}
                      className="flex-1 border border-emerald-400 rounded-lg px-2 py-1.5 text-sm outline-none focus:ring-2 focus:ring-emerald-500"
                    />
                    <button onClick={handleSalvarEdicao} disabled={loading}
                      className="text-emerald-600 hover:text-emerald-700 text-xs font-semibold px-2 py-1 rounded-lg hover:bg-emerald-50 transition-colors">
                      Salvar
                    </button>
                    <button onClick={() => { setEditando(null); setNomeEditando(''); }}
                      className="text-gray-400 hover:text-gray-600 text-xs px-2 py-1 rounded-lg hover:bg-gray-50 transition-colors">
                      ✕
                    </button>
                  </>
                ) : (
                  <>
                    <span className="flex-1 text-sm text-gray-700">{cat.nome}</span>
                    <button onClick={() => { setEditando(cat); setNomeEditando(cat.nome); }}
                      className="text-gray-400 hover:text-emerald-600 transition-colors p-1 rounded" title="Editar">
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                          d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                      </svg>
                    </button>
                    <button onClick={() => handleDeletar(cat.id)}
                      className="text-gray-400 hover:text-red-500 transition-colors p-1 rounded" title="Excluir">
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                          d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    </button>
                  </>
                )}
              </li>
            ))}
          </ul>

          {/* Criar nova */}
          <div className="flex gap-2 pt-2 border-t border-gray-100">
            <input
              type="text"
              placeholder="Nova categoria..."
              value={nomeNova}
              onChange={e => setNomeNova(e.target.value)}
              onKeyDown={e => e.key === 'Enter' && handleCriar()}
              className="flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition"
            />
            <button onClick={handleCriar} disabled={loading || !nomeNova.trim()}
              className="bg-emerald-600 text-white px-3 py-2 rounded-lg text-sm font-semibold hover:bg-emerald-700 transition-colors disabled:opacity-50">
              Criar
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}