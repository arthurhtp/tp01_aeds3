import type { Ambiente } from '../../types/home.types';

export function getNomeAmbiente(amb: Ambiente): string {
  return amb.nome ?? amb.nomeAmbiente ?? 'Ambiente';
}

export function getImgAmbiente(tipo: number): string {
  if (tipo === 1) return '/assets/images/ambientes-images/G.png';
  if (tipo === 2) return '/assets/images/ambientes-images/F.png';
  if (tipo === 3) return '/assets/images/ambientes-images/D.png';
  return '/assets/images/ambientes-images/tipo3.png';
}
