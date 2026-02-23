import { ERRO_CARREGAR, ERRO_HINT } from '../constants';

export function AmbientesErrorState() {
  return (
    <div className="text-center py-12 text-red-500">
      <p className="text-lg font-semibold">{ERRO_CARREGAR}</p>
      <p className="text-sm mt-1">{ERRO_HINT}</p>
    </div>
  );
}
