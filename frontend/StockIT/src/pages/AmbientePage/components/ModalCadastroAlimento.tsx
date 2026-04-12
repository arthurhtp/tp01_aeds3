import { useState, useEffect } from 'react';
import type { Categoria } from '../ambiente.types';
import { categoriaService } from '../ambiente.service';

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

  useEffect(() => {
    categoriaService.getAll().then(setCategorias).catch(console.error);
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
            <select required value={idCategoria} onChange={e => setIdCategoria(Number(e.target.value))}
              className="w-full border border-gray-300 rounded-lg px-3 py-2.5 text-sm outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition">
              <option value="" disabled>Escolha uma categoria</option>
              {categorias.map(c => <option key={c.id} value={c.id}>{c.nomeCategoria}</option>)}
            </select>
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
  );
}
