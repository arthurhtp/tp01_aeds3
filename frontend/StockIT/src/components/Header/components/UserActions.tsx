interface UserActionsProps {
  onLogout: () => void;
}

export function UserActions({ onLogout }: UserActionsProps) {
  return (
    <button
      onClick={onLogout}
      className="border border-white/60 text-white text-sm px-3 py-1.5 rounded-md hover:bg-white/10 transition-colors"
    >
      Sair
    </button>
  );
}
