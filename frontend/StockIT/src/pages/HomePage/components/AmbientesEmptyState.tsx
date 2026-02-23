import { NENHUM_AMBIENTE, NENHUM_AMBIENTE_HINT } from '../constants';

export function AmbientesEmptyState() {
  return (
    <div className="text-center py-12 text-gray-400">
      <p className="text-lg">{NENHUM_AMBIENTE}</p>
      <p className="text-sm mt-1">{NENHUM_AMBIENTE_HINT}</p>
    </div>
  );
}
