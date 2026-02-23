import { useState, useRef, useEffect } from 'react';
import type { Ambiente } from '../types/home.types';

function getNomeAmbiente(amb: Ambiente): string {
  return amb.nome ?? amb.nomeAmbiente ?? 'Ambiente';
}

function getImgAmbiente(tipo: number): string {
  if (tipo === 1) return '/assets/images/ambientes-images/G.png';
  if (tipo === 2) return '/assets/images/ambientes-images/F.png';
  if (tipo === 3) return '/assets/images/ambientes-images/D.png';
  return '/assets/images/ambientes-images/tipo3.png';
}

interface AmbienteCardProps {
  ambiente: Ambiente;
  onEditar: (id: number) => void;
  onExcluir: (id: number) => void;
}

export default function AmbienteCard({ ambiente, onEditar, onExcluir }: AmbienteCardProps) {
  const [menuOpen, setMenuOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    function handleClick(e: MouseEvent) {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setMenuOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClick);
    return () => document.removeEventListener('mousedown', handleClick);
  }, []);

  function handleCardClick(e: React.MouseEvent) {
    if ((e.target as HTMLElement).closest('.menu-area')) return;
    window.location.href = `../ambientes/ambiente.html?id=${ambiente.id}`;
  }

  return (
    <div
      onClick={handleCardClick}
      className="group relative bg-white rounded-2xl border border-gray-100 shadow-sm hover:shadow-lg hover:-translate-y-2 transition-all duration-300 cursor-pointer overflow-visible flex-shrink-0 w-60 flex flex-col"
    >
      {/* Hover overlay */}
      <div className="absolute inset-0 rounded-2xl bg-gradient-to-br from-emerald-500/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none" />

      {/* Image */}
      <div className="h-40 w-full overflow-hidden rounded-t-2xl bg-gradient-to-br from-gray-50 to-gray-100 border-b-2 border-dashed border-gray-200 group-hover:border-emerald-400 transition-colors flex items-center justify-center">
        <img
          src={getImgAmbiente(ambiente.tipo)}
          alt={getNomeAmbiente(ambiente)}
          className="w-full h-full object-cover"
          onError={e => {
            (e.target as HTMLImageElement).style.display = 'none';
          }}
        />
      </div>

      {/* Footer */}
      <div className="flex items-center justify-between px-4 py-3 border-t border-gray-100">
        <h3 className="font-semibold text-gray-800 text-base truncate">{getNomeAmbiente(ambiente)}</h3>

        <div className="menu-area relative" ref={menuRef}>
          <button
            onClick={e => { e.stopPropagation(); setMenuOpen(v => !v); }}
            className="p-1.5 rounded-full text-gray-400 hover:text-gray-700 hover:bg-gray-100 transition-colors"
          >
            <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 5a1.5 1.5 0 110-3 1.5 1.5 0 010 3zm0 7a1.5 1.5 0 110-3 1.5 1.5 0 010 3zm0 7a1.5 1.5 0 110-3 1.5 1.5 0 010 3z" />
            </svg>
          </button>

          {menuOpen && (
            <div className="absolute top-full right-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg z-50 min-w-[120px] overflow-hidden">
              <button
                onClick={e => { e.stopPropagation(); setMenuOpen(false); onEditar(ambiente.id); }}
                className="w-full text-left px-4 py-2.5 text-sm text-gray-700 hover:bg-gray-50 transition-colors border-b border-gray-100"
              >
                Editar
              </button>
              <button
                onClick={e => { e.stopPropagation(); setMenuOpen(false); onExcluir(ambiente.id); }}
                className="w-full text-left px-4 py-2.5 text-sm text-red-600 hover:bg-red-50 transition-colors"
              >
                Excluir
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
