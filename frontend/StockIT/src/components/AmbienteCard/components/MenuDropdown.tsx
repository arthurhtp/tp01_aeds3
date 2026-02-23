import type { MenuDropdownProps } from '../types';

export function MenuDropdown({ ambiente, onEditar, onExcluir, onClose }: MenuDropdownProps) {
  return (
    <div className="absolute top-full right-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg z-50 min-w-[120px] overflow-hidden">
      <button
        onClick={e => {
          e.stopPropagation();
          onClose();
          onEditar(ambiente.id);
        }}
        className="w-full text-left px-4 py-2.5 text-sm text-gray-700 hover:bg-gray-50 transition-colors border-b border-gray-100"
      >
        Editar
      </button>
      <button
        onClick={e => {
          e.stopPropagation();
          onClose();
          onExcluir(ambiente.id);
        }}
        className="w-full text-left px-4 py-2.5 text-sm text-red-600 hover:bg-red-50 transition-colors"
      >
        Excluir
      </button>
    </div>
  );
}
