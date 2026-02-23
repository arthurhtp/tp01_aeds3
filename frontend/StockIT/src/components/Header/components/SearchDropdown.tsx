import type { SearchResult } from '../../../types/home.types';

interface SearchDropdownProps {
  show: boolean;
  loading: boolean;
  results: SearchResult[];
  onResultClick: (result: SearchResult) => void;
}

export function SearchDropdown({ show, loading, results, onResultClick }: SearchDropdownProps) {
  if (!show) return null;

  return (
    <div className="absolute top-full left-0 right-0 mt-2 bg-white border border-gray-200 rounded-xl shadow-lg z-50 overflow-hidden">
      {loading ? (
        <div className="px-4 py-3 text-gray-500 text-sm">Buscando...</div>
      ) : results.length === 0 ? (
        <div className="px-4 py-3 text-gray-500 text-sm">Nenhum resultado encontrado.</div>
      ) : (
        results.map((r, i) => (
          <button
            key={i}
            onClick={() => onResultClick(r)}
            className="w-full text-left px-4 py-3 hover:bg-gray-50 border-b border-gray-100 last:border-0 text-sm text-gray-700 transition-colors"
          >
            <span className="text-xs text-emerald-600 font-medium mr-2">
              {r.type === 'ambiente' ? '📦' : '🍎'}
            </span>
            {r.type === 'ambiente' ? r.nome : `${r.nome} (${r.ambienteNome})`}
          </button>
        ))
      )}
    </div>
  );
}
