import type { AmbienteCardProps } from './types';
import { CardImage, CardFooter } from './components';

export function AmbienteCard({ ambiente, onEditar, onExcluir }: AmbienteCardProps) {
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

      <CardImage ambiente={ambiente} />
      <CardFooter ambiente={ambiente} onEditar={onEditar} onExcluir={onExcluir} />
    </div>
  );
}
