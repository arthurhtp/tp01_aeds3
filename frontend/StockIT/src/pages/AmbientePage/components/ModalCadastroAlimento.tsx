import { useState, useEffect } from 'react';
import type { Categoria } from '../ambiente.types';
import { categoriaService } from '../ambiente.service';
import ModalGerenciarCategorias from './ModalGerenciarCategorias';

interface ModalCadastroAlimentoProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (nome: string, idCategoria: number, quantidade: number, vencimento: string) => Promise<void>;
}

export default function ModalCadastroAlimento({ open, onClose, onSubmit }: ModalCadastroAlimentoProps) {
  const [nome, setNome] = useState('');
  const [vencimento, setVencimento] = useState('');
  const [quantidade, setQuantidade] = useState(1);
  const [idCategoria, setIdCategoria] = useState<number | ''>('');
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalCat, setModalCat] = useState(false);

  async function recarregarCategorias() {
    const data = await categoriaService.getAll();
    setCategorias(data);
  }

  useEffect(() => {
    recarregarCategorias().catch(console.error);
  }, []);

  useEffect(() => {
    if (!open) { setNome(''); setVencimento(''); setQuantidade(1); setIdCategoria(''); }
  }, [open]);

  if (!open) return null;

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!nome || !vencimento || !quantidade || idCategoria === '') {
      alert('Preencha todos os campos obrigatórios.'); return;
    }
    setLoading(true);
    try {
      await onSubmit(nome.trim(), Number(idCategoria), quantidade, vencimento);
      onClose();
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
      <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
        <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md">
          <div className="flex items-center justify-between p-6 border-b border-gray-100">
            <h2 className="text-lg font-semibold text-gray-800">Cadastrar Alimento</h2>
            <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition-colors">
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <form onSubmit={handleSubmit} className="p-6 space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Nome</label>
              <input type="text" required value={nome} onChange={e => setNome(e.target.value)}
                placeholder="Ex: Banana"
                className="w-full border border-gray-300 rounded-lg px-3 py-2.5 text-sm outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition" />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Validade</label>
              <input type="date" required value={vencimento} onChange={e => setVencimento(e.target.value)}
                className="w-full border border-gray-300 rounded-lg px-3 py-2.5 text-sm outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition" />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Quantidade</label>
              <input type="number" min={1} required value={quantidade} onChange={e => setQuantidade(Number(e.target.value))}
                className="w-full border border-gray-300 rounded-lg px-3 py-2.5 text-sm outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition" />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Categoria</label>
              <div className="flex gap-2 items-center">
                <select required value={idCategoria} onChange={e => setIdCategoria(Number(e.target.value))}
                  className="flex-1 border border-gray-300 rounded-lg px-3 py-2.5 text-sm outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition">
                  <option value="" disabled>Escolha uma categoria</option>
                  {categorias.map(c => <option key={c.id} value={c.id}>{c.nome}</option>)}
                </select>
                <button type="button" onClick={() => setModalCat(true)} title="Gerenciar categorias"
                  className="p-2.5 rounded-lg border border-gray-300 text-gray-500 hover:text-emerald-600 hover:border-emerald-400 transition-colors">
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                      d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  </svg>
                </button>
              </div>
            </div>

            <div className="flex gap-3 pt-2">
              <button type="submit" disabled={loading}
                className="flex-1 bg-emerald-600 text-white py-2.5 rounded-xl font-semibold hover:bg-emerald-700 transition-colors disabled:opacity-60">
                {loading ? 'Adicionando...' : 'Adicionar'}
              </button>
              <button type="button" onClick={onClose}
                className="flex-1 border border-gray-300 text-gray-600 py-2.5 rounded-xl font-semibold hover:bg-gray-50 transition-colors">
                Cancelar
              </button>
            </div>
          </form>
        </div>
      </div>

      <ModalGerenciarCategorias
        open={modalCat}
        categorias={categorias}
        onClose={() => setModalCat(false)}
        onAtualizar={recarregarCategorias}
      />
    </>
  );
}