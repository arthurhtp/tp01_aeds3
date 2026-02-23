import type { ModalHeaderProps } from '../types';

export function ModalHeader({ isEditing, onClose }: ModalHeaderProps) {
  return (
    <div className="flex items-center justify-between p-6 border-b border-gray-100">
      <h2 className="text-lg font-semibold text-gray-800">
        {isEditing ? 'Editar Ambiente' : 'Cadastrar Ambiente'}
      </h2>
      <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition-colors">
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>
  );
}
