import { useState, useEffect } from 'react';

interface ModalCadastroAmbienteProps {
  open: boolean;
  ambienteParaEditar?: { id: number; nome: string; tipo: number } | null;
  onClose: () => void;
  onSubmit: (nome: string, tipo: string) => Promise<void>;
}

const TIPOS = [
  { value: '1', label: 'Geladeira' },
  { value: '2', label: 'Freezer' },
  { value: '3', label: 'Despensa' },
];

function tipoNumParaChar(tipo: number): string {
  return tipo === 1 ? '1' : tipo === 2 ? '2' : '3';
}

export default function ModalCadastroAmbiente({
  open,
  ambienteParaEditar,
  onClose,
  onSubmit,
}: ModalCadastroAmbienteProps) {
  const [nome, setNome] = useState('');
  const [tipo, setTipo] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (ambienteParaEditar) {
      setNome(ambienteParaEditar.nome);
      setTipo(tipoNumParaChar(ambienteParaEditar.tipo));
    } else {
      setNome('');
      setTipo('');
    }
  }, [ambienteParaEditar, open]);

  if (!open) return null;

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    try {
      await onSubmit(nome.trim(), tipo);
      onClose();
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md">
        <div className="flex items-center justify-between p-6 border-b border-gray-100">
          <h2 className="text-lg font-semibold text-gray-800">
            {ambienteParaEditar ? 'Editar Ambiente' : 'Cadastrar Ambiente'}
          </h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition-colors">
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Nome</label>
            <input
              type="text"
              required
              value={nome}
              onChange={e => setNome(e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2.5 text-sm outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition"
              placeholder="Nome do ambiente"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Tipo</label>
            <select
              required
              value={tipo}
              onChange={e => setTipo(e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2.5 text-sm outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition"
            >
              <option value="" disabled>Selecione</option>
              {TIPOS.map(t => (
                <option key={t.value} value={t.value}>{t.label}</option>
              ))}
            </select>
          </div>

          <div className="flex gap-3 pt-2">
            <button
              type="submit"
              disabled={loading}
              className="flex-1 bg-emerald-600 text-white py-2.5 rounded-xl font-semibold hover:bg-emerald-700 transition-colors disabled:opacity-60"
            >
              {loading ? 'Salvando...' : (ambienteParaEditar ? 'Salvar' : 'Cadastrar')}
            </button>
            <button
              type="button"
              onClick={onClose}
              className="flex-1 border border-gray-300 text-gray-600 py-2.5 rounded-xl font-semibold hover:bg-gray-50 transition-colors"
            >
              Cancelar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
