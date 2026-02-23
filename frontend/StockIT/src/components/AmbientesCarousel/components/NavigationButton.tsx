import type { NavigationButtonProps } from '../types';

export function NavigationButton({ disabled, onClick, direction }: NavigationButtonProps) {
  const isPrev = direction === 'prev';

  return (
    <button
      onClick={onClick}
      disabled={disabled}
      className="absolute top-1/2 -translate-y-1/2 z-10 w-11 h-11 rounded-full bg-white shadow-md flex items-center justify-center text-emerald-600 hover:scale-110 hover:shadow-lg transition-all disabled:opacity-40 disabled:cursor-not-allowed disabled:scale-100"
      style={{ [isPrev ? 'left' : 'right']: '0' }}
    >
      <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth={2}
          d={isPrev ? 'M15 19l-7-7 7-7' : 'M9 5l7 7-7 7'}
        />
      </svg>
    </button>
  );
}
