export interface ModalHeaderProps {
  isEditing: boolean;
  onClose: () => void;
}

export interface FormProps {
  nome: string;
  tipo: string;
  loading: boolean;
  isEditing: boolean;
  onNomeChange: (value: string) => void;
  onTipoChange: (value: string) => void;
  onSubmit: (e: React.FormEvent) => void;
  onCancel: () => void;
}

export interface ModalCadastroAmbienteProps {
  open: boolean;
  ambienteParaEditar?: { id: number; nome: string; tipo: number } | null;
  onClose: () => void;
  onSubmit: (nome: string, tipo: string) => Promise<void>;
}
