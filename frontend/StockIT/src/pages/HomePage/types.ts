import type { Ambiente } from '../../types/home.types';

export interface AmbienteEditando {
  id: number;
  nome: string;
  tipo: number;
}

export interface AmbientesSectionProps {
  ambientes: Ambiente[];
  loading: boolean;
  error: boolean;
  onEditar: (id: number) => void;
  onExcluir: (id: number) => void;
  onAdicionarClick: () => void;
}

export interface AmbientesLoadingStateProps {}

export interface AmbientesErrorStateProps {}

export interface AmbientesEmptyStateProps {}

export interface ActionButtonsProps {
  onAdicionarClick: () => void;
}

export interface PageFooterProps {}
