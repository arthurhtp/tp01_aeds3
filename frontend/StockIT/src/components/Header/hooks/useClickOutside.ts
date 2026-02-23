import { useRef, useEffect } from 'react';

interface UseClickOutsideProps {
  onClickOutside: () => void;
}

export function useClickOutside({ onClickOutside }: UseClickOutsideProps) {
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (ref.current && !ref.current.contains(e.target as Node)) {
        onClickOutside();
      }
    }

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [onClickOutside]);

  return ref as React.RefObject<HTMLDivElement>;
}
