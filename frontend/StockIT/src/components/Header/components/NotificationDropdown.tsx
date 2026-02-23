interface NotificationDropdownProps {
  show: boolean;
  notificacoes: Array<{ texto: string; ambienteId: number; alimentoId: number }>;
  diasLimite: number;
}

export function NotificationDropdown({ show, notificacoes, diasLimite }: NotificationDropdownProps) {
  if (!show) return null;

  return (
    <div className="absolute top-full right-0 mt-3 bg-white border border-gray-200 rounded-2xl shadow-xl z-50 w-80 max-h-96 overflow-y-auto p-4">
      <p className="font-semibold text-gray-800 mb-3">Notificações</p>
      {notificacoes.length === 0 ? (
        <p className="text-sm text-gray-500">Nenhum alimento vencendo nos próximos {diasLimite} dias.</p>
      ) : (
        notificacoes.map((n, i) => (
          <div key={i} className="py-3 border-b border-gray-100 last:border-0 text-sm text-gray-700">
            {n.texto}
          </div>
        ))
      )}
    </div>
  );
}
