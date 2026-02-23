import { useState, useEffect } from 'react';
import type { ModalCadastroAmbienteProps } from './types';
import { ModalHeader, Form } from './components';
import { tipoNumParaChar } from './constants';

export function ModalCadastroAmbiente({
  open,
  ambienteParaEditar,
  onClose,
  onSubmit,
}: ModalCadastroAmbienteProps) {
  const [nome, setNome] = useState('');
  const [tipo, setTipo] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (ambienteParaEditar) {
      setNome(ambienteParaEditar.nome);
      setTipo(tipoNumParaChar(ambienteParaEditar.tipo));
    } else {
      setNome('');
      setTipo('');
    }
  }, [ambienteParaEditar, open]);

  if (!open) return null;

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    try {
      await onSubmit(nome.trim(), tipo);
      onClose();
    } finally {
      setLoading(false);
    }
  }

  const isEditing = !!ambienteParaEditar;

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md">
        <ModalHeader isEditing={isEditing} onClose={onClose} />
        <Form
          nome={nome}
          tipo={tipo}
          loading={loading}
          isEditing={isEditing}
          onNomeChange={setNome}
          onTipoChange={setTipo}
          onSubmit={handleSubmit}
          onCancel={onClose}
        />
      </div>
    </div>
  );
}
