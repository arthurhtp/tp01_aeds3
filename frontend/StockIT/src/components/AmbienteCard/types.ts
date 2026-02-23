import type { Ambiente } from '../../types/home.types';

export type { Ambiente };

export interface CardImageProps {
  ambiente: Ambiente;
}

export interface CardFooterProps {
  ambiente: Ambiente;
  onEditar: (id: number) => void;
  onExcluir: (id: number) => void;
}

export interface MenuDropdownProps {
  ambiente: Ambiente;
  onEditar: (id: number) => void;
  onExcluir: (id: number) => void;
  onClose: () => void;
}

export interface AmbienteCardProps {
  ambiente: Ambiente;
  onEditar: (id: number) => void;
  onExcluir: (id: number) => void;
}
