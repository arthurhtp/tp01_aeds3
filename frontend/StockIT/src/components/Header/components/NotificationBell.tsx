import { NotificationDropdown } from './NotificationDropdown';
import type { Notificacao } from '../../../types/home.types';

interface NotificationBellProps {
  show: boolean;
  onToggle: () => void;
  notificacoes: Notificacao[];
  diasLimite: number;
  notifRef: React.RefObject<HTMLDivElement>;
}

export function NotificationBell({
  show,
  onToggle,
  notificacoes,
  diasLimite,
  notifRef,
}: NotificationBellProps) {
  return (
    <div className="relative" ref={notifRef}>
      <button
        onClick={onToggle}
        className="relative text-white/80 hover:text-white transition-colors"
      >
        <svg className="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
        </svg>
        {notificacoes.length > 0 && (
          <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full min-w-[18px] h-[18px] flex items-center justify-center px-1">
            {notificacoes.length}
          </span>
        )}
      </button>

      <NotificationDropdown show={show} notificacoes={notificacoes} diasLimite={diasLimite} />
    </div>
  );
}
