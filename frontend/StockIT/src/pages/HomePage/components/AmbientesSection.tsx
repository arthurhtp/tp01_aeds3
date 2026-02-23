import { AmbientesCarousel } from '../../../components/AmbientesCarousel';
import { AmbientesLoadingState } from './AmbientesLoadingState';
import { AmbientesErrorState } from './AmbientesErrorState';
import { AmbientesEmptyState } from './AmbientesEmptyState';
import type { AmbientesSectionProps } from '../types';
import { PAGINA_TITULO } from '../constants';

export function AmbientesSection({
  ambientes,
  loading,
  error,
  onEditar,
  onExcluir,
  onAdicionarClick,
}: AmbientesSectionProps) {
  return (
    <section className="mb-16">
      <h1 className="text-4xl font-bold text-gray-800 tracking-widest mb-10">{PAGINA_TITULO}</h1>

      {loading ? (
        <AmbientesLoadingState />
      ) : error ? (
        <AmbientesErrorState />
      ) : ambientes.length === 0 ? (
        <AmbientesEmptyState />
      ) : (
        <AmbientesCarousel
          ambientes={ambientes}
          onEditar={onEditar}
          onExcluir={onExcluir}
        />
      )}

      {/* Botões de ação */}
      <div className="flex justify-center gap-4 mt-10">
        <button
          onClick={onAdicionarClick}
          className="flex items-center gap-3 bg-emerald-600 text-white px-8 py-3.5 rounded-xl font-semibold text-base hover:bg-emerald-700 hover:-translate-y-0.5 hover:shadow-lg transition-all shadow-emerald-200 shadow-md"
        >
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Adicionar Ambientes
        </button>
      </div>
    </section>
  );
}
