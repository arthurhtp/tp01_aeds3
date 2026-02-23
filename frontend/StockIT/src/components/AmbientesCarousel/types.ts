import type { Ambiente } from '../../types/home.types';

export interface NavigationButtonProps {
  disabled: boolean;
  onClick: () => void;
  direction: 'prev' | 'next';
}

export interface CardListProps {
  ambientes: Ambiente[];
  index: number;
  onEditar: (id: number) => void;
  onExcluir: (id: number) => void;
}

export interface AmbientesCarouselProps {
  ambientes: Ambiente[];
  onEditar: (id: number) => void;
  onExcluir: (id: number) => void;
}
