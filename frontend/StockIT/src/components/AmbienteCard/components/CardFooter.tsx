import type { CardFooterProps } from '../types';
import { MenuDropdown } from './MenuDropdown';
import { getNomeAmbiente } from '../utils';
import { useMenuDropdown } from '../hooks';

export function CardFooter({ ambiente, onEditar, onExcluir }: CardFooterProps) {
  const { menuOpen, setMenuOpen, menuRef } = useMenuDropdown();

  return (
    <div className="flex items-center justify-between px-4 py-3 border-t border-gray-100">
      <h3 className="font-semibold text-gray-800 text-base truncate">
        {getNomeAmbiente(ambiente)}
      </h3>

      <div className="menu-area relative" ref={menuRef}>
        <button
          onClick={e => {
            e.stopPropagation();
            setMenuOpen(v => !v);
          }}
          className="p-1.5 rounded-full text-gray-400 hover:text-gray-700 hover:bg-gray-100 transition-colors"
        >
          <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
            <path d="M12 5a1.5 1.5 0 110-3 1.5 1.5 0 010 3zm0 7a1.5 1.5 0 110-3 1.5 1.5 0 010 3zm0 7a1.5 1.5 0 110-3 1.5 1.5 0 010 3z" />
          </svg>
        </button>

        {menuOpen && (
          <MenuDropdown
            ambiente={ambiente}
            onEditar={onEditar}
            onExcluir={onExcluir}
            onClose={() => setMenuOpen(false)}
          />
        )}
      </div>
    </div>
  );
}
