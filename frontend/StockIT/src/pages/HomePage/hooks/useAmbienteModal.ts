import { useState } from 'react';
import type { AmbienteEditando } from '../types';

export function useAmbienteModal() {
  const [modalCadastroOpen, setModalCadastroOpen] = useState(false);
  const [ambienteEditando, setAmbienteEditando] = useState<AmbienteEditando | null>(null);

  function abrirModal() {
    setAmbienteEditando(null);
    setModalCadastroOpen(true);
  }

  function fecharModal() {
    setModalCadastroOpen(false);
    setAmbienteEditando(null);
  }

  function editarAmbiente(data: AmbienteEditando) {
    setAmbienteEditando(data);
    setModalCadastroOpen(true);
  }

  return {
    modalCadastroOpen,
    setModalCadastroOpen,
    ambienteEditando,
    setAmbienteEditando,
    abrirModal,
    fecharModal,
    editarAmbiente,
  };
}
